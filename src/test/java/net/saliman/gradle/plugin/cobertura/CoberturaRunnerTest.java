package net.saliman.gradle.plugin.cobertura;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.cobertura.instrument.Main;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Main.class, net.sourceforge.cobertura.reporting.Main.class })
public class CoberturaRunnerTest {

	private static String BASEDIR_ENTRY = "--basedir";
	private static String DATAFILE_ENTRY = "--datafile";
	private static String DESTINATION_ENTRY = "--destination";
	private static String IGNORE_ENTRY = "--ignore";
	private static String INCLUDECLASSES_ENTRY = "--includeClasses";
	private static String EXCLUDECLASSES_ENTRY = "--excludeClasses";
	private static String IGNORETRIVIAL_ENTRY = "--ignoreTrivial";
	private static String IGNOREMETHODANNOTATIONS_ENTRY = "--ignoreMethodAnnotation";
	private static String AUXCLASSPATH_ENTRY = "--auxClasspath";

	@Test
	public void testAllParameters() {
		List<String> expectedList = setupCompleteResponse();
		// basedir
		String basedir = expectedList.get(1);
		// datafile
		String datafile = expectedList.get(3);
		// destination
		String destination = expectedList.get(5);
		// ignore
		List<String> ignore = new ArrayList<String>();
		ignore.add(expectedList.get(7));
		// includeclasses
		List<String> includeClasses = new ArrayList<String>();
		includeClasses.add(expectedList.get(9));
		// excludeclasses
		List<String> excludeClasses = new ArrayList<String>();
		excludeClasses.add(expectedList.get(11));
		// ignoretrivial
		boolean ignoreTrivial = true;
		// ignoremethodannotations
		List<String> ignoreMethodAnnotations = new ArrayList<String>();
		ignoreMethodAnnotations.add(expectedList.get(14));
		// auxilaryclasspath
		String auxiliaryClasspath = expectedList.get(16);
		// instrument list
		List<String> instrument = new ArrayList<String>();
		instrument.add(expectedList.get(17));
		String[] argument = executeTest(basedir, datafile, destination, ignore,
				includeClasses, excludeClasses, ignoreTrivial,
				ignoreMethodAnnotations, auxiliaryClasspath, instrument);
		// check the captured array of strings
		compareResult(expectedList.toArray(new String[expectedList.size()]),
				argument);
	}

	@Test
	public void testAllNull() {
		List<String> expectedList = setupCompleteResponse();
		// basedir
		String basedir = null;
		// datafile
		String datafile = null;
		// destination
		String destination = null;
		// ignore
		List<String> ignore = null;
		// includeclasses
		List<String> includeClasses = null;
		// excludeclasses
		List<String> excludeClasses = null;
		// ignoretrivial
		boolean ignoreTrivial = false;
		// ignoremethodannotations
		List<String> ignoreMethodAnnotations = null;
		// auxilaryclasspath
		String auxiliaryClasspath = expectedList.get(16);
		// instrument list
		List<String> instrument = new ArrayList<String>();
		instrument.add(expectedList.get(17));
		// remove the entries from the expected list
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		String[] argument = executeTest(basedir, datafile, destination, ignore,
				includeClasses, excludeClasses, ignoreTrivial,
				ignoreMethodAnnotations, auxiliaryClasspath, instrument);
		// check the captured array of strings
		compareResult(expectedList.toArray(new String[expectedList.size()]),
				argument);
	}

	@Test
	public void testEmptyOrNull() {
		List<String> expectedList = setupCompleteResponse();
		// basedir
		String basedir = "";
		// datafile
		String datafile = "";
		// destination
		String destination = "";
		// ignore
		List<String> ignore = null;
		// includeclasses
		List<String> includeClasses = null;
		// excludeclasses
		List<String> excludeClasses = null;
		// ignoretrivial
		boolean ignoreTrivial = false;
		// ignoremethodannotations
		List<String> ignoreMethodAnnotations = null;
		// auxilaryclasspath
		String auxiliaryClasspath = expectedList.get(16);
		// instrument list
		List<String> instrument = new ArrayList<String>();
		instrument.add(expectedList.get(17));
		// remove the entries from the expected list
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		expectedList.remove(0);
		String[] argument = executeTest(basedir, datafile, destination, ignore,
				includeClasses, excludeClasses, ignoreTrivial,
				ignoreMethodAnnotations, auxiliaryClasspath, instrument);
		// check the captured array of strings
		compareResult(expectedList.toArray(new String[expectedList.size()]),
				argument);
	}

	@Test
	public void testExcludeClassesEmpty() {
		List<String> expectedList = setupCompleteResponse();
		// basedir
		String basedir = expectedList.get(1);
		// datafile
		String datafile = expectedList.get(3);
		// destination
		String destination = expectedList.get(5);
		// ignore
		List<String> ignore = new ArrayList<String>();
		ignore.add(expectedList.get(7));
		// includeclasses
		List<String> includeClasses = new ArrayList<String>();
		includeClasses.add(expectedList.get(9));
		// excludeclasses
		List<String> excludeClasses = new ArrayList<String>();
		// ignoretrivial
		boolean ignoreTrivial = true;
		// ignoremethodannotations
		List<String> ignoreMethodAnnotations = new ArrayList<String>();
		ignoreMethodAnnotations.add(expectedList.get(14));
		// auxilaryclasspath
		String auxiliaryClasspath = expectedList.get(16);
		// instrument list
		List<String> instrument = new ArrayList<String>();
		instrument.add(expectedList.get(17));
		// remove the entries from the expected list
		expectedList.remove(10);
		expectedList.remove(10);
		String[] argument = executeTest(basedir, datafile, destination, ignore,
				includeClasses, excludeClasses, ignoreTrivial,
				ignoreMethodAnnotations, auxiliaryClasspath, instrument);
		// check the captured array of strings
		compareResult(expectedList.toArray(new String[expectedList.size()]),
				argument);
	}

	@Test
	public void testIncludeClassesNullButExcludeIsnt() {
		List<String> expectedList = setupCompleteResponse();
		// basedir
		String basedir = expectedList.get(1);
		// datafile
		String datafile = expectedList.get(3);
		// destination
		String destination = expectedList.get(5);
		// ignore
		List<String> ignore = new ArrayList<String>();
		ignore.add(expectedList.get(7));
		// includeclasses
		List<String> includeClasses = null;
		// excludeclasses
		List<String> excludeClasses = new ArrayList<String>();
		excludeClasses.add(expectedList.get(11));
		// ignoretrivial
		boolean ignoreTrivial = true;
		// ignoremethodannotations
		List<String> ignoreMethodAnnotations = new ArrayList<String>();
		ignoreMethodAnnotations.add(expectedList.get(14));
		// auxilaryclasspath
		String auxiliaryClasspath = expectedList.get(16);
		// instrument list
		List<String> instrument = new ArrayList<String>();
		instrument.add(expectedList.get(17));
		// amend the entries in the expected list
		expectedList.remove(9);
		expectedList.add(9, ".*");
		String[] argument = executeTest(basedir, datafile, destination, ignore,
				includeClasses, excludeClasses, ignoreTrivial,
				ignoreMethodAnnotations, auxiliaryClasspath, instrument);
		// check the captured array of strings
		compareResult(expectedList.toArray(new String[expectedList.size()]),
				argument);
	}

	@Test
	public void testIncludeClassesEmptyButExcludeIsnt() {
		List<String> expectedList = setupCompleteResponse();
		// basedir
		String basedir = expectedList.get(1);
		// datafile
		String datafile = expectedList.get(3);
		// destination
		String destination = expectedList.get(5);
		// ignore
		List<String> ignore = new ArrayList<String>();
		ignore.add(expectedList.get(7));
		// includeclasses
		List<String> includeClasses = new ArrayList<String>();
		// excludeclasses
		List<String> excludeClasses = new ArrayList<String>();
		excludeClasses.add(expectedList.get(11));
		// ignoretrivial
		boolean ignoreTrivial = true;
		// ignoremethodannotations
		List<String> ignoreMethodAnnotations = new ArrayList<String>();
		ignoreMethodAnnotations.add(expectedList.get(14));
		// auxilaryclasspath
		String auxiliaryClasspath = expectedList.get(16);
		// instrument list
		List<String> instrument = new ArrayList<String>();
		instrument.add(expectedList.get(17));
		// amend the entries in the expected list
		expectedList.remove(9);
		expectedList.add(9, ".*");
		String[] argument = executeTest(basedir, datafile, destination, ignore,
				includeClasses, excludeClasses, ignoreTrivial,
				ignoreMethodAnnotations, auxiliaryClasspath, instrument);
		// check the captured array of strings
		compareResult(expectedList.toArray(new String[expectedList.size()]),
				argument);
	}

	@Test
	public void testGenerateCoverageReport() throws Exception {
		CoberturaRunner runner = new CoberturaRunner();
		PowerMockito.mockStatic(net.sourceforge.cobertura.reporting.Main.class);
		ArgumentCaptor<String[]> argument = ArgumentCaptor
				.forClass(String[].class);
		// execute the test
		String datafile = "datafile";
		String destination = "destination";
		String format = "xml";
		List<String> sourceDirectories = new ArrayList<String>();
		sourceDirectories.add("sourceDirectories");
		runner.generateCoverageReport(datafile, destination, format,
				sourceDirectories);
		// verify that the main method ran once and once only
		PowerMockito.verifyStatic(Mockito.times(1));
		// capture the input argument to the main method
		net.sourceforge.cobertura.reporting.Main.main(argument.capture());
		assertEquals("--datafile", argument.getValue()[0]);
		assertEquals(datafile, argument.getValue()[1]);
		assertEquals("--format", argument.getValue()[2]);
		assertEquals("xml", argument.getValue()[3]);
		assertEquals("--destination", argument.getValue()[4]);
		assertEquals(destination, argument.getValue()[5]);
		assertEquals("sourceDirectories", argument.getValue()[6]);

	}

	private String[] executeTest(String basedir, String datafile,
			String destination, List<String> ignore,
			List<String> includeClasses, List<String> excludeClasses,
			boolean ignoreTrivial, List<String> ignoreMethodAnnotations,
			String auxiliaryClasspath, List<String> instrument) {
		// define the mock to capture the input to the cobertura Main class
		CoberturaRunner runner = new CoberturaRunner();
		PowerMockito.mockStatic(Main.class);
		ArgumentCaptor<String[]> argument = ArgumentCaptor
				.forClass(String[].class);
		// execute the test
		runner.instrument(basedir, datafile, destination, ignore,
				includeClasses, excludeClasses, ignoreTrivial,
				ignoreMethodAnnotations, auxiliaryClasspath, instrument);
		// verify that the main method ran once and once only
		PowerMockito.verifyStatic(Mockito.times(1));
		// capture the input argument to the main method
		Main.main(argument.capture());
		return argument.getValue();
	}

	private void compareResult(String[] expected, String[] actual) {
		assertEquals("array sizes are not equal", expected.length,
				actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals("contents in position " + i + "are not equal",
					expected[i], actual[i]);
		}
	}

	private List<String> setupCompleteResponse() {
		List<String> expectedList = new ArrayList<String>();
		// basedir - entry 0 (label), entry 1 (value)
		expectedList.add(BASEDIR_ENTRY);
		expectedList.add("basedir");
		// datafile - 2 and 3
		expectedList.add(DATAFILE_ENTRY);
		expectedList.add("datafile");
		// destination - 4 and 5
		expectedList.add(DESTINATION_ENTRY);
		expectedList.add("destination");
		// ignore - 6 and 7
		expectedList.add(IGNORE_ENTRY);
		expectedList.add("ignore");
		// includeclasses - 8 and 9
		expectedList.add(INCLUDECLASSES_ENTRY);
		expectedList.add("includeClasses");
		// excludeclasses - 10 and 11
		expectedList.add(EXCLUDECLASSES_ENTRY);
		expectedList.add("excludeClasses");
		// ignoretrivial - 12
		expectedList.add(IGNORETRIVIAL_ENTRY);
		// ignoremethodannotations - 13 and 14
		expectedList.add(IGNOREMETHODANNOTATIONS_ENTRY);
		expectedList.add("ignoreMethodAnnotations");
		// auxilaryclasspath 15 and 16
		expectedList.add(AUXCLASSPATH_ENTRY);
		expectedList.add("auxiliaryClasspath");
		// instrument list - 17
		expectedList.add("instrument");
		return expectedList;
	}
}
