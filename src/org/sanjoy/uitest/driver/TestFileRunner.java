package org.sanjoy.uitest.driver;
import java.io.File;
import java.util.List;

import org.sanjoy.uitest.config.Configuration;
import org.sanjoy.uitest.config.RunMode;
import org.sanjoy.uitest.error.ErrorMessages;
import org.sanjoy.uitest.imaging.ImageVerifier;
import org.sanjoy.uitest.imaging.ImageVerifierResult;
import org.sanjoy.uitest.result.TestFileResult;
import org.sanjoy.uitest.result.TestSuitResult;
import org.sanjoy.uitest.steps.ExecutionStep;
import org.sanjoy.uitest.steps.SnapShotStep;

public class TestFileRunner implements Runnable {

	private Configuration	_config;
	private TestFileResult _store;
	private String 			_fileName;
	private TestSuitResult  _suitResult;

	public TestFileRunner(Configuration config, TestSuitResult suitResult, String fileName) {
		_config = config;
		_fileName = fileName;
		_suitResult = suitResult;
		_store = new TestFileResult();
	}

	public void run() {
		TestStepDriver driver = null;;

		try {
			List<ExecutionStep> steps = new TestStepLoader().load(_fileName);
			driver = new TestStepDriver(_config);

			for (ExecutionStep step : steps) {
				if (_config.isVerbose()) {
					System.err.println("Executing Step : " + step.getMethod());
				}

				driver.execute(step.getMethod(),step.getParms().toArray());
				if (step instanceof SnapShotStep && _config.getRunMode() == RunMode.COMPARE) {
					doImageVerification(step);
				}
			}
		} catch (RuntimeException rex) {
			System.err.println(ErrorMessages.ERROR_EXEC_TEST_SUITE + _fileName);
			System.err.println(rex.getMessage());
			if (_config.isVerbose()) rex.printStackTrace();
		} finally {
			_store.setEndTime();
			_suitResult.addResult(_fileName,_store);
			if (driver != null)
				driver.tearDown();
		}
	}

	private void doImageVerification(ExecutionStep step) {
		SnapShotStep snapShotStep = (SnapShotStep)step;

		String storeImageFile = _config.getStoreImageDir() + File.separatorChar + snapShotStep.getFileName();
		String tempImageFile = _config.getCompareImageDir() + File.separatorChar + snapShotStep.getFileName();

		try {

			ImageVerifier imageVerifier = new ImageVerifier();
			imageVerifier.setRegions(snapShotStep.getRegions());
			imageVerifier.setInclude(snapShotStep.isInclude());

			ImageVerifierResult result = imageVerifier.verify(storeImageFile,tempImageFile);
			result.setDescription((String)step.getParms().get(0));
			_store.addResult(result);

			if (_config.isVerbose()) {
				System.err.println("Compared Images : " + storeImageFile + " : " + tempImageFile);
			}

		} catch (Throwable ex) {
			throw new RuntimeException(ErrorMessages.ERROR_COMPARING_IMAGES + storeImageFile + " , " + tempImageFile + " : " + ex.getMessage());
		}
	}
}