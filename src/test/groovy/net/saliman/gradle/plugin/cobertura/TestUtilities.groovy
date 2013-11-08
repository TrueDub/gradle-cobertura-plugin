package net.saliman.gradle.plugin.cobertura

import org.gradle.testfixtures.ProjectBuilder

class TestUtilities {

	public static org.gradle.api.Project defineMockProject() {
		String baseDirName = 'build/tmp/projects/main'
		File baseDir = new File(baseDirName)
		assert baseDir.deleteDir()
		assert baseDir.mkdirs()
		assert baseDir.exists()
		def projectMock = ProjectBuilder.builder().withProjectDir(baseDir).build()
		return projectMock
	}
}
