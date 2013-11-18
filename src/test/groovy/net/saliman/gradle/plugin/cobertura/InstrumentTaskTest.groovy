package net.saliman.gradle.plugin.cobertura

import static org.junit.Assert.*
import static org.mockito.Mockito.*

import org.junit.Test
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer


class InstrumentTaskTest {

	@Test
	public void testInstrument() {
		def projectMock = TestUtilities.defineMockProject()
		//apply the java plugin
		projectMock.apply plugin: 'java'
		CoberturaExtension coberturaExtension = new CoberturaExtension(projectMock)
		def task = projectMock.task(InstrumentTask.NAME, type: InstrumentTask)
		assertTrue(task instanceof InstrumentTask)
		assertEquals("instrument",task.name)
		task.configuration = coberturaExtension
		CoberturaRunner mockRunner = Mockito.mock(CoberturaRunner.class)
		Object[] parameters;
		doAnswer(new Answer<Void>() {
					public Object answer(InvocationOnMock invocation) {
						parameters  = invocation.getArguments();
					}
				}).when(mockRunner).instrument(anyString(), anyString(), anyString(), anyList(),anyList(),anyList(),anyBoolean(),anyList(), anyString(), anyList())
		task.runner = mockRunner
		task.destinationDir = projectMock.buildDir
		task.execute()
		assertNull(parameters[0])
		assertEquals(projectMock.buildDir.path + "\\cobertura\\coberturaInput.ser",parameters[1])
		assertEquals(projectMock.buildDir.path,parameters[2])
		List<String> coverageIgnores = parameters[3]
		assertEquals(0,coverageIgnores.size())
		List<String> coverageIncludes = parameters[4]
		assertEquals(0,coverageIncludes.size())
		List<String> coverageExcludes = parameters[5]
		assertEquals(0,coverageExcludes.size())
		assertEquals(coberturaExtension.coverageIgnoreTrivial,parameters[6])
		List<String> coverageIgnoreMethodAnnotations = parameters[7]
		assertEquals(0,coverageIgnoreMethodAnnotations.size())
		assertEquals(projectMock.sourceSets.main.output.classesDir.path +
				":" + projectMock.sourceSets.main.compileClasspath.getAsPath(),parameters[8])
		List<String> instrumentDirs = parameters[9]
		assertEquals(2,instrumentDirs.size())
		assertEquals(projectMock.buildDir.path + "\\classes\\main",instrumentDirs.get(0))
		assertEquals(projectMock.buildDir.path + "/instrumented_classes",instrumentDirs.get(1))
	}
	
	@Test
	public void testInstrumentExtraClassesDir() {
		def projectMock = TestUtilities.defineMockProject()
		String extraClassesDirName = projectMock.buildDir.absolutePath + "/extraclasses"
		File extraClassesDir = new File(extraClassesDirName)
		assert extraClassesDir.mkdirs()
		assert extraClassesDir.exists()
		//apply the java plugin
		projectMock.apply plugin: 'java'
		CoberturaExtension coberturaExtension = new CoberturaExtension(projectMock)
		coberturaExtension.coverageDirs << extraClassesDirName
		def task = projectMock.task(InstrumentTask.NAME, type: InstrumentTask)
		assertTrue(task instanceof InstrumentTask)
		assertEquals("instrument",task.name)
		task.configuration = coberturaExtension
		CoberturaRunner mockRunner = Mockito.mock(CoberturaRunner.class)
		Object[] parameters;
		doAnswer(new Answer<Void>() {
					public Object answer(InvocationOnMock invocation) {
						parameters  = invocation.getArguments();
					}
				}).when(mockRunner).instrument(anyString(), anyString(), anyString(), anyList(),anyList(),anyList(),anyBoolean(),anyList(), anyString(), anyList())
		task.runner = mockRunner
		task.destinationDir = projectMock.buildDir
		task.execute()
		assertNull(parameters[0])
		assertEquals(projectMock.buildDir.path + "\\cobertura\\coberturaInput.ser",parameters[1])
		assertEquals(projectMock.buildDir.path,parameters[2])
		List<String> coverageIgnores = parameters[3]
		assertEquals(0,coverageIgnores.size())
		List<String> coverageIncludes = parameters[4]
		assertEquals(0,coverageIncludes.size())
		List<String> coverageExcludes = parameters[5]
		assertEquals(0,coverageExcludes.size())
		assertEquals(coberturaExtension.coverageIgnoreTrivial,parameters[6])
		List<String> coverageIgnoreMethodAnnotations = parameters[7]
		assertEquals(0,coverageIgnoreMethodAnnotations.size())
		assertEquals(projectMock.sourceSets.main.output.classesDir.path +
				":" + projectMock.sourceSets.main.compileClasspath.getAsPath(),parameters[8])
		List<String> instrumentDirs = parameters[9]
		assertEquals(2,instrumentDirs.size())
		assertEquals(projectMock.buildDir.path + "\\classes\\main",instrumentDirs.get(0))
		assertEquals(projectMock.buildDir.path + "/instrumented_classes",instrumentDirs.get(1))
	}

}
