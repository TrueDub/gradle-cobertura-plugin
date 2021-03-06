package net.saliman.gradle.plugin.cobertura

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.UnknownConfigurationException
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.testing.Test

import org.gradle.api.invocation.Gradle

/**
 * Provides Cobertura coverage for Test tasks.
 *
 * This plugin will create 4 tasks.
 *
 * The first is the "cobertura" task that users may call to generate coverage
 * reports.  This task will run the all tasks named "test" from the directory
 * where gradle was invoked as well as "test" tasks in any sub projects.  In
 * addition, it will run all tasks of type {@code Test} in the project applying
 * the plugin.  It is designed to do the same thing as "gradle test", but with
 * all testing tasks running in the applying project and coverage reports
 * generated at the end of the build.  This task doesn't actually do any work,
 * it is used by the plugin to determine user intent. This task will do the
 * right thing for most users.
 * <p>
 * The second task is called "coberturaReport". Users may call this task to
 * tell Gradle that coverage reports should be generated after tests are run,
 * but without causing any tests to actually run. This allows users to have
 * more fine-grained control over the tasks that get run by requiring them to
 * specify the desired tasks on the command line.
 * <p>
 * The third task is the "instrument" task, that will instrument the source
 * code. Users won't call it directly, but the plugin will make all test
 * tasks depend on it so that instrumentation only happens once, and only if
 * the task graph has the "cobertura" or "coberturaReport" task in the execution
 * graph.
 * <p>
 * The fourth task is the "copyCoberturaDatafile" task, which copies the input
 * datafile, generated by the instrument task, to the output datafile, which is
 * is modified by tests and used to generate the coverage report.  This task
 * must run whenever we want a coverage report to make sure the data in it is
 * for the current run only.
 * <p>
 * The fifth task is the "generateCoberturaReport" task, which does the actual
 * work of generating the coverage reports if the "cobertura" or
 * "coberturaReport" tasks are in the execution graph.
 * <p>
 * This plugin also defines a "cobertura" extension with properties that are
 * used to configure the operation of the plugin and its tasks.
 *
 * The plugin runs cobertura coverage reports for sourceSets.main.  A project
 * might have have multiple artifacts that use different parts of the code, and
 * there may be different test tasks that test different parts of the source,
 * but there is almost always only one main source set.
 *
 * Most of the magic of this plugin happens not at apply time, but when tasks
 * are added and when the task graph is ready.  If the graph contains the
 * "cobertura" or "coberturaReport" task, it will make sure the instrument and
 * generateCoberturaReport" tasks are configured to do actual work, and that
 * task dependencies are what they need to be for the users to get what they
 * want.
 */
class CoberturaPlugin implements Plugin<Project> {
	// Constants for the tasks created by this plugin that don't have their own
	// classes.
	static final String COBERTURA_TASK_NAME = 'cobertura'
	static final String COBERTURA_REPORT_TASK_NAME = 'coberturaReport'

	def void apply(Project project) {
		project.logger.info("Applying cobertura plugin to $project.name")
		// It doesn't make sense to have the cobertura plugin without the java
		// plugin because it works with java classes, so apply it here.  If the
		// project is a Groovy or Scala project, we're still good because Groovy
		// and Scala compiles to java classes under the hood, and the Groovy and
		// Scala plugins will extend the Java plugin anyway.
		project.plugins.apply(JavaPlugin)

		CoberturaExtension extension = project.extensions.create('cobertura', CoberturaExtension, project)
		if (!project.configurations.asMap['cobertura']) {
			project.configurations.create('cobertura') {
				extendsFrom project.configurations['testCompile']
			}
			project.afterEvaluate {
				project.dependencies {
					cobertura "net.sourceforge.cobertura:cobertura:${project.extensions.cobertura.coberturaVersion}"
				}
			}
		}
		project.afterEvaluate {
			project.dependencies.add('testRuntime', "net.sourceforge.cobertura:cobertura:${project.extensions.cobertura.coberturaVersion}")
		}

		createTasks(project, extension)

		fixTaskDependencies(project, extension)

		registerTaskFixupListener(project)

	}

	/**
	 * Create the tasks that the Cobertura plugin will use.  This method will
	 * create the following tasks:
	 * <ul>
	 * <li>instrument - This is an internal task that does the actual work of
	 * instrumenting the source code.</li>
	 * <li>generateCoberturaReport - This is an internal task that does the
	 * actual work of generating the Cobertura reports.</li>
	 * <li>copyCoberturaDatafile - This is an internal task that makes a copy of
	 * the instrumentation data file (the .ser file) for tests to use so that
	 * the original remains untouched.  This is needed to make sure
	 * instrumentation only happens when source code changes.</li>
	 * <li>coberturaReport - Users will use this task to tell the plugin that
	 * they want coverage reports generated after all the tests run.  The
	 * {@code coberturaReport} task doesn't actually cause any tests to run;
	 * users will need to specify one or more test tasks on the command line
	 * as well.</li>
	 * <li>cobertura</li> - Users will use this task to run tests and generate
	 * a coverage report.  This task is meant to be a convenience task that is
	 * simpler than (but not quite the same as) {@code test coberturaReport}</li>
	 * </ul>
	 *
	 * @param project the project being configured
	 */
	private void createTasks(Project project, CoberturaExtension extension) {
		// Create the coberturaReport task.  This task, when invoked, indicates
		// that users want a coverage report.
		project.tasks.create(name: COBERTURA_REPORT_TASK_NAME, type: DefaultTask)
		Task reportTask = project.tasks.getByName(COBERTURA_REPORT_TASK_NAME)
		reportTask.setDescription("Generate Cobertura reports after tests finish.")

		// Create the cobertura task.  This task, when invoked, indicates that
		// users want a coverage report, and they want all tests to run.
		project.tasks.create(name: COBERTURA_TASK_NAME, type: DefaultTask)
		Task coberturaTask = project.tasks.getByName(COBERTURA_TASK_NAME)
		coberturaTask.setDescription("Run tests and generate Cobertura coverage reports.")
		// If we make cobertura depend on reportTask, it is easier later on to
		// determine of we need reports or not.
		coberturaTask.dependsOn reportTask

		// Create the instrument task that will instrument code. At the moment, it
		// is disabled.
		project.tasks.create(name: InstrumentTask.NAME,
						type: InstrumentTask,
						{
							configuration = project.extensions.cobertura
							classpath = project.configurations.cobertura
						})
		Task instrumentTask = project.tasks.getByName(InstrumentTask.NAME)
		instrumentTask.setDescription("Instrument code for Cobertura coverage reports")
		instrumentTask.enabled = false
		instrumentTask.runner = extension.runner

		// Create the copyCoberturaDatafile task that will copy the .ser file.  It
		// is also disabled to start.
		project.tasks.create(name: CopyDatafileTask.NAME,
						             type: CopyDatafileTask,
						             { configuration = project.extensions.cobertura })
		Task copyDatafileTask = project.tasks.getByName(CopyDatafileTask.NAME)
		copyDatafileTask.setDescription("Helper task that makes a fresh copy of the Cobertura data file for tests")
		copyDatafileTask.enabled = false
		copyDatafileTask.dependsOn instrumentTask

		// Create the generateCoberturaReport task that will generate the reports.
		// Like the others, it starts out disabled.
		project.tasks.create(name: GenerateReportTask.NAME,
						             type: GenerateReportTask,
						             {
							             configuration = project.extensions.cobertura
							             classpath = project.configurations.cobertura
						             })
		Task generateReportTask = project.tasks.getByName(GenerateReportTask.NAME)
		generateReportTask.setDescription("Generate a Cobertura report after tests finish.")
		generateReportTask.enabled = false
		generateReportTask.runner = extension.runner

		// Create the generateCoberturaReport task that will check the coverage
		// levels, and you guessed it, it is disabled.
		project.tasks.create(name: CheckCoverageTask.NAME,
						type: CheckCoverageTask,
						{
							configuration = project.extensions.cobertura
							classpath = project.configurations.cobertura
						})
		Task checkCoverageTask = project.tasks.getByName(CheckCoverageTask.NAME)
		checkCoverageTask.setDescription("Check code coverage.")
		checkCoverageTask.enabled = false
		checkCoverageTask.runner = extension.runner
		checkCoverageTask.dependsOn reportTask
	}

	/**
	 * We need to make several changes to the tasks in the task graph for the
	 * cobertura plugin to work correctly.  The changes need to be made to all
	 * tasks currently in the project, as well as any new tasks that get added
	 * later.
	 * @param project the project applying the plugin. Used for logging.
	 * @param extension the CoberturaExtension which has the various options for
	 *        the plugin, but also the closures that return classes and test
	 *        tasks so we can set up dependencies properly.
	 */
	private void fixTaskDependencies(Project project, CoberturaExtension extension) {
		Task instrumentTask = project.tasks.getByName(InstrumentTask.NAME)
		Task coberturaTask = project.tasks.getByName(COBERTURA_TASK_NAME)
		Task generateReportTask = project.tasks.getByName(GenerateReportTask.NAME)
		Task checkCoverageTask = project.tasks.getByName(CheckCoverageTask.NAME)
		Task copyDatafileTask = project.tasks.getByName(CopyDatafileTask.NAME)

		// Add a whenTaskAdded listener for all projects from the base down.
		extension.coverageClassesTasks.all { task ->
			project.logger.info("Making the instrument task depend on :${task.project.name}:${task.name}")
			instrumentTask.dependsOn task
		}

		// Tests need to depend on coying the data file and be finalized by the
		// check coverage task (which in turn depends on generate reports).  We'll
		// figure out later what is enabled or not.
		extension.coverageTestTasks.all { task ->
			project.logger.info("Making the cobertura task depend on :${task.project.name}:${task.name}")
			task.dependsOn copyDatafileTask
			task.finalizedBy generateReportTask
			task.finalizedBy checkCoverageTask
			coberturaTask.dependsOn task
		}
	}

	/**
	 * Register a listener with Gradle.  When gradle is ready to run tasks, it
	 * will call our listener.  If the coberturaReport task is in our graph, the
	 * listener will fix the classpaths of all the test tasks that we are actually
	 * running.
	 *
	 * @param project the project applying the plugin.
	 */
	private void registerTaskFixupListener(Project project) {
		// If the user wants cobertura reports, fix test classpaths, and set them
		// to generate reports on failure.  If not, disable the 3 tasks that
		// instrument code and run reports.
		// "whenReady()" is a global event, so closure should be registered exactly
		// once for single and multi-project builds.
		Gradle gradle = project.gradle
		gradle.taskGraph.whenReady { TaskExecutionGraph graph ->
			// See if the user wants a coberturaReport.  If so, fix the classpath of
			// any test task we are actually running. (we don't need to look for the
			// cobertura task because it depends on coberturaReport).
			if (graph.hasTask(project.tasks.findByName(COBERTURA_REPORT_TASK_NAME))) {
				project.tasks.withType(Test).all { Test test ->
					try {
						Configuration config = test.project.configurations['cobertura']
						test.systemProperties.put('net.sourceforge.cobertura.datafile', test.project.extensions.cobertura.coverageOutputDatafile)
						test.classpath += config
						test.outputs.upToDateWhen { false }
						fixTestClasspath(test)
					} catch (UnknownConfigurationException e) {
						// Eat this. It just means we have a multi-project build, and
						// there is test in a project that doesn't have cobertura applied.
					}
				}
				// We also need to enable instrumentation, file copying and report
				// generation, but not coverage checking
				project.tasks.withType(InstrumentTask).all {
					it.enabled = true
				}
				project.tasks.withType(CopyDatafileTask).each {
					it.enabled = true
				}
				project.tasks.withType(GenerateReportTask).each {
					it.enabled = true
				}
			}
			// If the user wants to check coverage levels, we need to enable
			// coverage checking.  We've already enabled the others because coverage
			// depends on reports
			if (graph.hasTask(project.tasks.findByName(CheckCoverageTask.NAME))) {
				project.tasks.withType(CheckCoverageTask).each {
					it.enabled = true
				}
			}
		}
	}

	/**
	 * Configure a test task.  remove source dirs and add the instrumented dir
	 * @param test the test task to fix
	 */
	def fixTestClasspath(Task test) {
		def project = test.project
		project.files(project.sourceSets.main.output.classesDir.path).each { File f ->
			if (f.isDirectory()) {
				test.classpath = test.classpath - project.files(f)
			}
		}
		test.classpath = project.files("${project.buildDir}/instrumented_classes") + test.classpath
	}
}
