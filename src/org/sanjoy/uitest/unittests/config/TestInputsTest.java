package org.sanjoy.uitest.unittests.config;

import org.junit.Test;
import org.sanjoy.uitest.config.Configuration;
import org.sanjoy.uitest.error.ErrorMessages;

import junit.framework.TestCase;

public class TestInputsTest extends TestCase {

	private String[] args = new String[1];

	protected void setUp() {
		Configuration config = Configuration.getInstance();
		config.setTestStepFile(null);
		config.setTestStepDir(null);
		System.getProperties().remove(Configuration.TEST_STEP_DIR_KEY);
		System.getProperties().remove(Configuration.TEST_STEP_FILE_KEY);

		args[0]="STORE";
	}

	@Test
	public void testNothingSpecified() {
		try {
			Configuration.getInstance().processCommandLine(args);
			fail("No exception raised.");
		} catch (RuntimeException rex) {
			System.err.println(rex.getMessage());
			assertTrue(rex.getMessage().equals(ErrorMessages.NO_TEST_STEP_CMD));
		}
	}

	@Test
	public void testNullTestFileSpecified() {
		try {
			Configuration.getInstance().processCommandLine(args);
			System.getProperties().put(Configuration.TEST_STEP_FILE_KEY, null);
			fail("No exception raised.");
		} catch (RuntimeException rex) {
			assertTrue(rex.getMessage().equals(ErrorMessages.NO_TEST_STEP_CMD));
		}
	}

	@Test
	public void testEmptyTestFileSpecified() {
		try {
			Configuration.getInstance().processCommandLine(args);
			System.getProperties().put(Configuration.TEST_STEP_FILE_KEY, "");
			fail("No exception raised.");
		} catch (RuntimeException rex) {
			assertTrue(rex.getMessage().equals(ErrorMessages.NO_TEST_STEP_CMD));
		}
	}

	@Test
	public void testInvalidTestFileSpecified() {
		try {
			System.getProperties().put(Configuration.TEST_STEP_FILE_KEY, "zz:\\sanjoy_ghosh");
			Configuration.getInstance().processCommandLine(args);
			fail("No exception raised.");
		} catch (RuntimeException rex) {
			assertTrue(rex.getMessage().startsWith(ErrorMessages.INVALID_STEP_FILE));
		}
	}

	@Test
	public void testBothTestFileAndDirSpecified() {
		try {
			System.getProperties().put(Configuration.TEST_STEP_FILE_KEY, "zz:\\sanjoy_ghosh");
			System.getProperties().put(Configuration.TEST_STEP_DIR_KEY, "zz:\\sanjoy_ghosh");

			Configuration.getInstance().processCommandLine(args);
			fail("No exception raised.");
		} catch (RuntimeException rex) {
			assertTrue(rex.getMessage().startsWith(ErrorMessages.BOTH_FILE_DIR_STEP_SPECIFIED));
		}
	}
}