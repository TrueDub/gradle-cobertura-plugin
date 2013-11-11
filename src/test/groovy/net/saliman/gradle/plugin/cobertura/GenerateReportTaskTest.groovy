package net.saliman.gradle.plugin.cobertura

import static org.mockito.Mockito.*

import static org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

class GenerateReportTaskTest {

	@Test
	public void testGenerateReports() {
		def projectMock = TestUtilities.defineMockProject()
		//apply the java plugin
		projectMock.apply plugin: 'java'
		CoberturaExtension coberturaExtension = new CoberturaExtension(projectMock)
		def task = projectMock.task(GenerateReportTask.NAME, type: GenerateReportTask)
		assertTrue(task instanceof GenerateReportTask)
		assertEquals("generateCoberturaReport",task.name)
		task.configuration = coberturaExtension
		CoberturaRunner mockRunner = Mockito.mock(CoberturaRunner.class)
		Object[] parameters;
		doAnswer(new Answer<Void>() {
					public Object answer(InvocationOnMock invocation) {
						parameters  = invocation.getArguments();
					}
				}).when(mockRunner).generateCoverageReport(anyString(), anyString(), anyString(), anyList())
		task.runner = mockRunner
		task.execute()
		assertEquals(projectMock.buildDir.path + "\\cobertura\\cobertura.ser",parameters[0])
		assertEquals(projectMock.buildDir.path + "\\reports\\cobertura",parameters[1])
		assertEquals("html",parameters[2])
		List<String> sourceDirs = parameters[3]
		assertEquals(1,sourceDirs.size())
		assertEquals(projectMock.rootDir.path + "\\src\\main\\java",sourceDirs.get(0))
	}

}
