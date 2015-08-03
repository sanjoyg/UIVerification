package org.sanjoy.uitest.unittests.config;

import org.junit.Test;
import org.sanjoy.uitest.config.Configuration;
import org.sanjoy.uitest.config.RunMode;
import org.sanjoy.uitest.error.ErrorMessages;

import junit.framework.TestCase;

public class CommandLineArgTest extends TestCase {

	@Test
	public void testInstanceCreation() {
		Configuration config = Configuration.getInstance();
		assertTrue(config!=null);

	}

	@Test
	public void testSingleton() {
		Configuration config = Configuration.getInstance();
		Configuration nextConfig = Configuration.getInstance();
		assertSame(config,nextConfig);
	}

	@Test
	public void testCmdLineNull() {
		Configuration config = Configuration.getInstance();
		try {
			config.processCommandLine(null);
		} catch(RuntimeException rex) {
			assertTrue(rex.getMessage().startsWith(ErrorMessages.TOOL_USAGE));
		}
	}

	@Test
	public void testCmdLineOneUnInitialized() {
		Configuration config = Configuration.getInstance();
		String[] args = new String[1];
		try {
			config.processCommandLine(args);
		} catch(RuntimeException rex) {
			assertTrue(rex.getMessage().startsWith(ErrorMessages.INVALID_RUN_MODE));
		}
	}

	@Test
	public void testCmdLineOneEmpty() {
		Configuration config = Configuration.getInstance();
		String[] args = new String[1];
		args[0]="";
		try {
			config.processCommandLine(args);
		} catch(RuntimeException rex) {
			assertTrue(rex.getMessage().startsWith(ErrorMessages.INVALID_RUN_MODE));
		}
	}

	@Test
	public void testCmdLineOneInvalid() {
		Configuration config = Configuration.getInstance();
		String[] args = new String[1];
		args[0]="sanjoy";
		try {
			config.processCommandLine(args);
		} catch(RuntimeException rex) {
			assertTrue(rex.getMessage().startsWith(ErrorMessages.INVALID_RUN_MODE));
		}
	}

	@Test
	public void testCmdLineOneStoreValidLowerCase() {
		Configuration config = Configuration.getInstance();
		String[] args = new String[1];
		args[0]="store";

		try {
			config.processCommandLine(args);
		} catch(RuntimeException rex) {
		}
		assertTrue(config.getRunMode() == RunMode.STORE);
	}

	@Test
	public void testCmdLineOneSompareValidUpperCase() {
		Configuration config = Configuration.getInstance();
		String[] args = new String[1];
		args[0]="STORE";

		try {
			config.processCommandLine(args);
		} catch(RuntimeException rex) {
		}
		assertTrue(config.getRunMode() == RunMode.STORE);
	}

	@Test
	public void testCmdLineOneStoreValidMixedCase() {
		Configuration config = Configuration.getInstance();
		String[] args = new String[1];
		args[0]="StorE";

		try {
			config.processCommandLine(args);
		} catch(RuntimeException rex) {
		}
		assertTrue(config.getRunMode() == RunMode.STORE);
	}

	@Test
	public void testCmdLineOneCompareValidLowerCase() {
		Configuration config = Configuration.getInstance();
		String[] args = new String[1];
		args[0]="COMPARE";

		try {
			config.processCommandLine(args);
		} catch(RuntimeException rex) {
		}
		assertTrue(config.getRunMode() == RunMode.COMPARE);
	}

	@Test
	public void testCmdLineOneCompareValidUpperCase() {
		Configuration config = Configuration.getInstance();
		String[] args = new String[1];
		args[0]="compare";

		try {
			config.processCommandLine(args);
		} catch(RuntimeException rex) {
		}
		assertTrue(config.getRunMode() == RunMode.COMPARE);
	}

	@Test
	public void testCmdLineOneCompareValidMixedCase() {
		Configuration config = Configuration.getInstance();
		String[] args = new String[1];
		args[0]="ComparE";

		try {
			config.processCommandLine(args);
		} catch(RuntimeException rex) {
		}
		assertTrue(config.getRunMode() == RunMode.COMPARE);
	}
}
