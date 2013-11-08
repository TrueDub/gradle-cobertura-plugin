package net.saliman.gradle.plugin.cobertura

import static org.junit.Assert.*

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class CoberturaExtensionTest {

	@Test
	public void testConstructorJava() {
		def projectMock = TestUtilities.defineMockProject()
		//apply the java plugin
		projectMock.apply plugin: 'java'
		CoberturaExtension coberturaExtension = new CoberturaExtension(projectMock)
		testStandardAttributes(projectMock.buildDir.path,coberturaExtension)
		assertEquals(1,coberturaExtension.coverageSourceDirs.size())
		assertEquals(projectMock.rootDir.path + "\\src\\main\\java",coberturaExtension.coverageSourceDirs.asList().get(0).path)
	}

	@Test
	public void testConstructorGroovy() {
		def projectMock = TestUtilities.defineMockProject()
		//apply the groovy plugin
		projectMock.apply plugin: 'groovy'
		CoberturaExtension coberturaExtension = new CoberturaExtension(projectMock)
		testStandardAttributes(projectMock.buildDir.path,coberturaExtension)
		assertEquals(2,coberturaExtension.coverageSourceDirs.size())
		assertEquals(projectMock.rootDir.path + "\\src\\main\\java",coberturaExtension.coverageSourceDirs.asList().get(0).path)
		assertEquals(projectMock.rootDir.path + "\\src\\main\\groovy",coberturaExtension.coverageSourceDirs.asList().get(1).path)
	}

	@Test
	public void testConstructorScala() {
		def projectMock = TestUtilities.defineMockProject()
		//apply the scala plugin
		projectMock.apply plugin: 'scala'
		CoberturaExtension coberturaExtension = new CoberturaExtension(projectMock)
		assertEquals(2,coberturaExtension.coverageSourceDirs.size())
		assertEquals(projectMock.rootDir.path + "\\src\\main\\java",coberturaExtension.coverageSourceDirs.asList().get(0).path)
		assertEquals(projectMock.rootDir.path + "\\src\\main\\scala",coberturaExtension.coverageSourceDirs.asList().get(1).path)
	}

	@Test
	public void testConstructorGroovyAndScala() {
		def projectMock = TestUtilities.defineMockProject()
		//apply the groovy and scala plugins
		projectMock.apply plugin: 'groovy'
		projectMock.apply plugin: 'scala'
		CoberturaExtension coberturaExtension = new CoberturaExtension(projectMock)
		assertEquals(3,coberturaExtension.coverageSourceDirs.size())
		assertEquals(projectMock.rootDir.path + "\\src\\main\\java",coberturaExtension.coverageSourceDirs.asList().get(0).path)
		assertEquals(projectMock.rootDir.path + "\\src\\main\\groovy",coberturaExtension.coverageSourceDirs.asList().get(1).path)
		assertEquals(projectMock.rootDir.path + "\\src\\main\\scala",coberturaExtension.coverageSourceDirs.asList().get(2).path)
	}

	private void testStandardAttributes(String buildDir,CoberturaExtension coberturaExtension) {
		assertEquals("2.0.3",coberturaExtension.coberturaVersion)
		assertEquals(1,coberturaExtension.coverageDirs.size())
		assertEquals(buildDir + "\\classes\\main",coberturaExtension.coverageDirs.get(0))
		assertEquals(buildDir + "\\cobertura\\coberturaInput.ser",coberturaExtension.coverageInputDatafile.path)
		assertEquals(buildDir + "\\cobertura\\cobertura.ser",coberturaExtension.coverageOutputDatafile.path)
		assertEquals(buildDir + "\\reports\\cobertura",coberturaExtension.coverageReportDir.path)
		assertEquals("html",coberturaExtension.coverageFormats.asList().get(0))
		assertEquals(0,coberturaExtension.coverageIncludes.size())
		assertEquals(0,coberturaExtension.coverageExcludes.size())
		assertEquals(0,coberturaExtension.coverageIgnores.size())
		assertFalse(coberturaExtension.coverageIgnoreTrivial)
		assertEquals(0,coberturaExtension.coverageIgnoreMethodAnnotations.size())
	}
}
