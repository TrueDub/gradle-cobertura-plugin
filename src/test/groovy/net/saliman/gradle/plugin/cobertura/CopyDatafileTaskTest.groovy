package net.saliman.gradle.plugin.cobertura

import org.junit.Assert
import org.junit.Test

class CopyDatafileTaskTest {

	@Test
	public void testCopy() {
		def projectMock = TestUtilities.defineMockProject()
		//apply the java plugin
		projectMock.apply plugin: 'java'
		//add the relevant files
		String coberturaDirectoryName = projectMock.buildDir.absolutePath + "/cobertura"
		File coberturaDirectory = new File(coberturaDirectoryName)
		assert coberturaDirectory.mkdirs()
		assert coberturaDirectory.exists()
		CoberturaExtension coberturaExtension = new CoberturaExtension(projectMock)
		TestUtilities.copyFile("src/test/resources/coberturaInput.ser",coberturaExtension.coverageInputDatafile.path)
		def task = projectMock.task(CopyDatafileTask.NAME, type: CopyDatafileTask)
		Assert.assertTrue(task instanceof CopyDatafileTask)
		Assert.assertEquals("copyCoberturaDatafile",task.name)
		task.configuration = coberturaExtension
		task.execute()
		//compare the file contents
		Assert.assertTrue(TestUtilities.compareFileContents(coberturaExtension.coverageInputDatafile.path, coberturaExtension.coverageOutputDatafile.path))
	}
}
