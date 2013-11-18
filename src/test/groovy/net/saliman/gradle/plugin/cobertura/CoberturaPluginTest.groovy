package net.saliman.gradle.plugin.cobertura

import static org.junit.Assert.*

import org.gradle.StartParameter
import org.gradle.api.DefaultTask
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class CoberturaPluginTest {


	def project = ProjectBuilder.builder().build()
	def plugin = new CoberturaPlugin()

	@Test
	void canApplyPlugin() {
		//set the default projectdir
		project.gradle.startParameter.projectDir = project.rootProject.projectDir
		assertTrue("Project is missing plugin", project.plugins.hasPlugin(CoberturaPlugin))
		def task = project.tasks.findByName("instrument")
		assertNotNull("Project is missing instrument task", task)
		assertTrue("Instrument task is the wrong type", task instanceof InstrumentTask)
		task = project.tasks.findByName("generateCoberturaReport")
		assertNotNull("Project is missing generateCoberturaReport task", task)
		assertTrue("GenerateCoberturaReport task is the wrong type", task instanceof GenerateReportTask)
		task = project.tasks.findByName("coberturaReport")
		assertNotNull("Project is missing coberturaReport task", task)
		assertTrue("CoberturaReport task is the wrong type", task instanceof DefaultTask)
		task = project.tasks.findByName("cobertura")
		assertNotNull("Project is missing cobertura task", task)
		assertTrue("cobertura task is the wrong type", task instanceof DefaultTask)
		assertNotNull("We're missing the configuration", project.configurations.asMap['cobertura'])
	}
}
