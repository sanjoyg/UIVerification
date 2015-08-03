package org.sanjoy.uitest.config;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.sanjoy.uitest.error.ErrorMessages;
import org.sanjoy.uitest.imaging.ImageVerifier;
import org.sanjoy.uitest.imaging.ImageVerifierConfig;

public class Configuration {

	private static Configuration _instance = null;

	public static String TEST_STEP_FILE_KEY 			= "uiverify.testfile";
	public static String TEST_STEP_DIR_KEY 				= "uiverify.testdir";
	public static String STORE_IMAGE_DIR_KEY 			= "uiverify.storedir";
	public static String COMPARE_IMAGE_DIR_KEY			= "uiverify.comparedir";
	public static String RESULTS_DIR_KEY 				= "uiverify.resultsdir";
	public static String VERBOSE_KEY 					= "uiverify.verbose";
	public static String PARALLEL_THREADS 				= "uiverify.parallelize";
	public static String RETAIN_IMG_IN_DIFF				= "uiverify.retainimgindiff";
	public static String DIFF_MARK_COLOR_KEY 			= "uiverify.diffmarkcolor";
	public static String DIFF_DRAW_RECTS				= "uiverify.drawdiffrect";

	private static String YES_STR				= "YES";
	private static String TRUE_STR				= "TRUE";
	private static String STORE_STR				= "STORE";
	private static String COMPARE_STR			= "COMPARE";
	private static String STANDALONE_STR		= "STANDALONE";
	private static String DEFAULT_COMPARE_DIR	= "_temp";

	private String _storeImageDir = null;
	private String _compareImageDir = null;
	private String _testStepFile = null;
	private String _testStepDir = null;
	private String _reportDir = null;
	private String _reportImagesDir = null;
	private RunMode _runMode;
	private boolean _verbose = false;
	private int		_threads = 1;

	private static SimpleDateFormat dirDateFormat = new SimpleDateFormat("yyyyMMdd-HHmm");

	public static Configuration getInstance() {
		if (_instance == null)
			_instance = new Configuration();
		return _instance;
	}

	private Configuration() {;}

	public void processCommandLine(String [] args) {
		if (args == null || args.length == 0)
			throw new RuntimeException(ErrorMessages.TOOL_USAGE);

		setupRunMode(args);

		checkAndLoadConfig(args);

		if ( _runMode != RunMode.STANDALONE)
			setupTestInputs();

		setupCompareImageDir();

		setupRunParameters();

		if (_runMode != RunMode.STORE)
			setupReportDir();
		setupImageVerificationProps();
	}

	private void setupRunMode(String[] args) {
		if (!STORE_STR.equalsIgnoreCase(args[0]) &&
			!COMPARE_STR.equalsIgnoreCase(args[0]) &&
			!STANDALONE_STR.equalsIgnoreCase(args[0])) {
			throw new RuntimeException(ErrorMessages.INVALID_RUN_MODE);
		}

		if (STORE_STR.equalsIgnoreCase(args[0])) {
			setTestRunMode(RunMode.STORE);
		} else if (COMPARE_STR.equalsIgnoreCase(args[0])) {
			setTestRunMode(RunMode.COMPARE);
		} else {
			setTestRunMode(RunMode.STANDALONE);
		}
	}

	private void checkAndLoadConfig(String[] args) {
		if (args == null || args.length <=1)
			return;
		String value = args[1];

		FileInputStream fis = null;
		Properties configProps = new Properties();
		try {
			fis = new FileInputStream(value);
			configProps.load(fis);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load config file : " + value);
		} finally {
			try { if (fis != null) fis.close(); } catch(Exception e) {;}
		}

		System.getProperties().putAll(configProps);
	}

	private void setupTestInputs() {
		Properties properties = System.getProperties();

		setTestStepFile(properties.getProperty(TEST_STEP_FILE_KEY));
		setTestStepDir(properties.getProperty(TEST_STEP_DIR_KEY));

		if ((getTestStepFile() == null || getTestStepFile().length()==0) &&
				(getTestStepDir() == null || getTestStepDir().length()==0)) {
			throw new RuntimeException(ErrorMessages.NO_TEST_STEP_CMD);
		}

		if (getTestStepFile() != null && getTestStepDir() != null) {
			throw new RuntimeException(ErrorMessages.BOTH_FILE_DIR_STEP_SPECIFIED);
		}

		if (_testStepDir != null) {
			File dirObj = new File(_testStepDir);
			if (!dirObj.isDirectory() || !dirObj.canRead())
				throw new RuntimeException(ErrorMessages.INVALID_STEP_DIR + _testStepDir);
		} else {
			if (! new File(_testStepFile).exists())
				throw new RuntimeException(ErrorMessages.INVALID_STEP_FILE + _testStepFile);
		}
	}

	private void setupCompareImageDir() {
		String value = System.getProperty(STORE_IMAGE_DIR_KEY);
		if (value == null || value.length()==0) {
			throw new RuntimeException("Configuration file doesnt specify the property : " + STORE_IMAGE_DIR_KEY);
		}

		setStoreImageDir(value);

		value = System.getProperty(COMPARE_IMAGE_DIR_KEY);
		if ((value == null || value.length()==0) && _runMode == RunMode.STANDALONE) {
			throw new RuntimeException("Configuration file doesnt specify the property : " + COMPARE_IMAGE_DIR_KEY);
		}
		setCompareImageDir(value);

		if (_runMode == RunMode.COMPARE) {
			if ((_compareImageDir == null || _compareImageDir.length() == 0) && _runMode == RunMode.COMPARE) {
				_compareImageDir = _storeImageDir + File.separatorChar + DEFAULT_COMPARE_DIR;
			}
		}

		verifyAndInitializeDirs();
	}

	private void setupRunParameters() {
		String value = System.getProperty(VERBOSE_KEY);
		if (value != null && value.length() != 0) {
			if (YES_STR.equalsIgnoreCase(value) || TRUE_STR.equalsIgnoreCase(value)) {
				_verbose = true;
			}
		}

		value = System.getProperty(PARALLEL_THREADS);
		if (value != null && value.length()!=0) {
			try {
				setParallelThreads(Integer.parseInt(value));
			} catch (NumberFormatException e) {
				System.err.println("Warning: Incorrect threads number specific : <" + value +">, setting to : 1");
				setParallelThreads(1);
			}
			if (getParallelThreads() <=0) {
				System.err.println("Warning: Incorrect threads number specific : <" + value +">, setting to : 1");
				setParallelThreads(1);
			}
		}
	}

	private void setupReportDir() {
		_reportDir = System.getProperty(RESULTS_DIR_KEY);

		if ((_reportDir  == null || _reportDir .length() == 0) && _runMode == RunMode.COMPARE)
			throw new RuntimeException("Command line must specify the property : " + RESULTS_DIR_KEY);

		String reportDirNameDerived = _reportDir + File.separatorChar + (dirDateFormat.format(new Date()));

		File dirToCreate = new File(reportDirNameDerived);

		if (dirToCreate.isDirectory()) {
			for (int i=0;i<1000;i++) {
				_reportDir = reportDirNameDerived + "-" + i;
				dirToCreate = new File(_reportDir);
				if (! dirToCreate.isDirectory())
					break;
				_reportDir = null;
			}
			if (_reportDir == null) {
				throw new RuntimeException("Failed to create report dir, tried : " + reportDirNameDerived +"-0 to " + reportDirNameDerived + "-999");
			}
		} else {
			_reportDir = reportDirNameDerived;
		}

		if (!dirToCreate.mkdirs()) {
			throw new RuntimeException("Failed to create report dir : " + dirToCreate.getName());
		}

		_reportImagesDir = _reportDir + File.separatorChar + "images";
		File imagesDirObj = new File(_reportImagesDir);
		if (!imagesDirObj.isDirectory()) {
			if (!imagesDirObj.mkdirs()) {
				throw new RuntimeException("Failed to create images dir in results : " + _reportImagesDir);
			}
		}
	}

	public void setupImageVerificationProps() {
		/*
		 * createSeparate difference image : If a third image file is to be created
		 * difference mark color : default grey scale
		 * draw difference rectangles
		 */

		String value = System.getProperties().getProperty(RETAIN_IMG_IN_DIFF);
		ImageVerifierConfig.setRetainOrgImage(false);
		if (value != null && value.length() != 0 ) {
			if (YES_STR.equalsIgnoreCase(value) || TRUE_STR.equalsIgnoreCase(value))
				ImageVerifierConfig.setRetainOrgImage(true);
		}

		value = System.getProperties().getProperty(DIFF_MARK_COLOR_KEY);
		int diffMarkColor = ImageVerifier.GREY_SCALE_MARK_COLOR;
		if (value != null && value.length() != 0 ) {
			try {
				diffMarkColor = Integer.parseUnsignedInt(value);
				ImageVerifierConfig.setDiffMarkColor(diffMarkColor);
			} catch (Exception ex) {;}
		}

		value = System.getProperties().getProperty(DIFF_DRAW_RECTS);
		ImageVerifierConfig.setDrawDiffRects(false);
		if (value != null && value.length() != 0 ) {
			if (YES_STR.equalsIgnoreCase(value) || TRUE_STR.equalsIgnoreCase(value))
				ImageVerifierConfig.setDrawDiffRects(true);
		}
	}

	public void verifyAndInitializeDirs() {

		File storeImageDir = new File(_storeImageDir);

		if (!storeImageDir.isDirectory()) {
			// Store Image dir required in COMPARE & STANDALONE mode
			if (getRunMode() != RunMode.STORE) {
				throw new RuntimeException("Stored Image Directory doesnt exist.: " + _storeImageDir);
			}
			// In STORE mode create the directory if doesnt exist
			if (!storeImageDir.mkdir()) {
				throw new RuntimeException("Failed to make stored Image directory : " + _storeImageDir);
			}
		}

		if (getRunMode() != RunMode.STORE) {
			File compareImageDir = new File(_compareImageDir);
			if (!compareImageDir.isDirectory() && getRunMode() != RunMode.STORE) {
				if (getRunMode() == RunMode.STANDALONE) {
					throw new RuntimeException("Compare Image Directory doesnt exist.: " + _compareImageDir);
				}

				if (!compareImageDir.mkdir()) {
					throw new RuntimeException("Failed to make comparison Image directory : " + _compareImageDir);
				}
			}
		}
	}

	public String getReportDir() {
		return _reportDir;
	}

	public String getReportImagesDir() {
		return _reportImagesDir;
	}

	public boolean isVerbose() {
		return _verbose;
	}

	public void setTestRunMode(RunMode runMode) {
		_runMode = runMode;
	}

	public RunMode getRunMode() {
		return _runMode;
	}

	public void setTestStepFile(String testStepFile) {
		_testStepFile = testStepFile;
	}

	public String getTestStepFile() {
		return _testStepFile;
	}

	public void setTestStepDir(String testStepDir) {
		_testStepDir = testStepDir;
	}

	public String getTestStepDir() {
		return _testStepDir;
	}

	public String getStoreImageDir() {
		return _storeImageDir;
	}

	public String getCompareImageDir() {
		return _compareImageDir;
	}

	public void setStoreImageDir(String storeImageDir) {
		_storeImageDir = storeImageDir;
	}

	public void setCompareImageDir(String tempImageDir) {
		_compareImageDir = tempImageDir;
	}

	public void setParallelThreads(int threads) {
		_threads = threads;
	}

	public int getParallelThreads() {
		return _threads;
	}
}