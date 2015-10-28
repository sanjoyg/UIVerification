package org.sanjoy.uitest.config;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.sanjoy.uitest.error.ErrorMessages;
import org.sanjoy.uitest.imaging.ImageVerifierConfig;

public class Configuration {

	public static String TEST_STEP_FILE_KEY 			= "uiverify.testfile";
	public static String TEST_STEP_DIR_KEY 				= "uiverify.testdir";
	public static String STORE_IMAGE_DIR_KEY 			= "uiverify.storedir";
	public static String COMPARE_IMAGE_DIR_KEY			= "uiverify.comparedir";
	public static String RESULTS_DIR_KEY 				= "uiverify.resultsdir";
	public static String VERBOSE_KEY 					= "uiverify.verbose";
	public static String PARALLEL_THREADS 				= "uiverify.parallelize";
	public static String WEB_CONTEXT					= "uiverify.webcontext";
	public static String REPORT_TEMPLATE				= "uiverify.reporttempl";

	public static String YES_STR				= "YES";
	public static String TRUE_STR				= "TRUE";
	public static String STORE_STR				= "STORE";
	public static String COMPARE_STR			= "COMPARE";
	public static String STANDALONE_STR			= "STANDALONE";
	private static String DEFAULT_COMPARE_DIR	= "_temp";

	private String _storeImageDir = null;
	private String _compareImageDir = null;
	private String _testStepFile = null;
	private String _testStepDir = null;
	private String _reportDir = null;
	private String _reportImagesDir = null;
	private RunMode _runMode = null;
	private boolean _verbose = false;
	private int		_threads = 1;
	private String _webContext = "."; //Default
	private String _reportTemplate = null;

	private static SimpleDateFormat dirDateFormat = new SimpleDateFormat("yyyyMMdd-HHmm");

	public Configuration() {;}

	public void reset() {
		_runMode = null;
		_verbose = false;
		_threads = 1;
		_storeImageDir = null;
		_compareImageDir = null;
		_testStepFile = null;
		_testStepDir = null;
		_reportDir = null;
		_reportImagesDir = null;
		_webContext = ".";
		_reportTemplate = null;
	}

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

		ImageVerifierConfig.setupImageVerificationProps();

		setupReportingParms();
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
		if (args == null || args.length <=1 || args[1] == null || args[1].length() == 0)
			return;
		String value = args[1];

		FileInputStream fis = null;
		Properties configProps = new Properties();
		try {
			fis = new FileInputStream(value);
			configProps.load(fis);
		} catch (IOException e) {
			throw new RuntimeException(ErrorMessages.FAILED_LOADING_CONFIG_FILE + value);
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
			throw new RuntimeException(ErrorMessages.CONFIG_DEFINITION_MISSING + STORE_IMAGE_DIR_KEY);
		}

		setStoreImageDir(value);

		value = System.getProperty(COMPARE_IMAGE_DIR_KEY);
		if ((value == null || value.length()==0) && _runMode == RunMode.STANDALONE) {
			throw new RuntimeException(ErrorMessages.CONFIG_DEFINITION_MISSING  + COMPARE_IMAGE_DIR_KEY);
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
				System.err.println(ErrorMessages.WARN_INCORRECT_THREAD_DEFN + value);
				setParallelThreads(1);
			}
			if (getParallelThreads() <=0) {
				System.err.println(ErrorMessages.WARN_INCORRECT_THREAD_DEFN + value);
				setParallelThreads(1);
			}
		}
	}

	private void setupReportingParms() {
		String webContext = System.getProperty(WEB_CONTEXT);
		if (webContext != null && webContext.length() != 0)
			setWebContext(webContext);

		String reportTemplate = System.getProperty(REPORT_TEMPLATE);

		if (reportTemplate == null || reportTemplate.length() == 0 || !(new File(reportTemplate).isFile()))
			throw new RuntimeException(ErrorMessages.INVALID_REPORT_TEMPL_FILE + reportTemplate);
		setReportTemplate(reportTemplate);
	}

	private void setupReportDir() {
		_reportDir = System.getProperty(RESULTS_DIR_KEY);

		if ((_reportDir  == null || _reportDir .length() == 0) && _runMode == RunMode.COMPARE)
			throw new RuntimeException(ErrorMessages.CONFIG_DEFINITION_MISSING + RESULTS_DIR_KEY);

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
				throw new RuntimeException(ErrorMessages.ERROR_CREATING_DIR + reportDirNameDerived +"-0 : " + reportDirNameDerived + "-999");
			}
		} else {
			_reportDir = reportDirNameDerived;
		}

		if (!dirToCreate.mkdirs()) {
			throw new RuntimeException(ErrorMessages.ERROR_CREATING_DIR + dirToCreate.getName());
		}

		_reportImagesDir = _reportDir + File.separatorChar + "images";
		File imagesDirObj = new File(_reportImagesDir);
		if (!imagesDirObj.isDirectory()) {
			if (!imagesDirObj.mkdirs()) {
				throw new RuntimeException(ErrorMessages.ERROR_CREATING_DIR  + _reportImagesDir);
			}
		}
	}


	public void verifyAndInitializeDirs() {

		File storeImageDir = new File(_storeImageDir);

		if (!storeImageDir.isDirectory()) {
			// Store Image dir required in COMPARE & STANDALONE mode
			if (getRunMode() != RunMode.STORE) {
				throw new RuntimeException(ErrorMessages.IMAGE_DIR_DOESNT_EXIST  + _storeImageDir);
			}
			// In STORE mode create the directory if doesnt exist
			if (!storeImageDir.mkdir()) {
				throw new RuntimeException(ErrorMessages.ERROR_CREATING_DIR + _storeImageDir);
			}
		}

		if (getRunMode() != RunMode.STORE) {
			File compareImageDir = new File(_compareImageDir);
			if (!compareImageDir.isDirectory() && getRunMode() != RunMode.STORE) {
				if (getRunMode() == RunMode.STANDALONE) {
					throw new RuntimeException(ErrorMessages.COMPARE_DIR_DOESN_EXIST + _compareImageDir);
				}

				if (!compareImageDir.mkdir()) {
					throw new RuntimeException(ErrorMessages.ERROR_CREATING_DIR + _compareImageDir);
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

	public String getWebContext() {
		return _webContext;
	}

	public void setWebContext(String webContext) {
		_webContext = webContext;
	}

	public void setReportTemplate(String reportTemplate) {
		_reportTemplate = reportTemplate;
	}

	public String getReportTemplate() {
		return _reportTemplate;
	}
}