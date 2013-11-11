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

	public static void copyFile(String source, String destination) {
		def sourceFile = new File (source)
		def destinationFile = new File (destination)
		sourceFile.withInputStream { is -> destinationFile << is }
	}

	public static boolean compareFileContents (String source, String destination) {
		//uses string comparison currently
		def sourceFile = new File (source)
		def destinationFile = new File (destination)
		String sourceString = sourceFile.text
		String destinationString = destinationFile.text
		return sourceString.equals(destinationString)
	}
}
