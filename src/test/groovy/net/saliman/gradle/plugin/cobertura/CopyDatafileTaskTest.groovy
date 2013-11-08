package net.saliman.gradle.plugin.cobertura

import org.junit.Assert
import org.junit.Test

class CopyDatafileTaskTest {

	@Test
	public void testCopy() {
		def projectMock = TestUtilities.defineMockProject()
		//apply the java plugin
		projectMock.apply plugin: 'java'
		CoberturaExtension coberturaExtension = new CoberturaExtension(projectMock)
		def task = projectMock.task(CopyDatafileTask.NAME, type: CopyDatafileTask)
		Assert.assertTrue(task instanceof CopyDatafileTask)
		Assert.assertEquals("copyCoberturaDatafile",task.name)
		task.execute()
	}
}
