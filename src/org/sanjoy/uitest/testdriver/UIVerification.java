package org.sanjoy.uitest.testdriver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.sanjoy.uitest.config.Configuration;
import org.sanjoy.uitest.config.RunMode;
import org.sanjoy.uitest.driver.TestFileRunner;
import org.sanjoy.uitest.result.TestSuitResult;
import org.sanjoy.uitest.result.writer.TestResultWriter;

public class UIVerification {

	private Configuration _config;

	public static void main(String[] args) {
		new UIVerification().start(args);
	}

	private UIVerification() {
	}

	public void start(String[] args) {
		System.err.println("Starting...");

		try {
			_config = new Configuration();
			_config.processCommandLine(args);

			TestSuitResult testSuitResult = new TestSuitResult();

			ArrayList<String> filesToRun = getFilesToRun();
			int numberOfThreads = (filesToRun.size() > _config.getParallelThreads()? _config.getParallelThreads() : filesToRun.size());
			ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

			for (String testFile : filesToRun) {
				TestFileRunner runner = new TestFileRunner(_config,testSuitResult,testFile);
				executorService.execute(runner);
			}

			executorService.shutdown();

			try { executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);}
			catch (InterruptedException e) {;}

			testSuitResult.setEndTime();

			if (_config.getRunMode() != RunMode.STORE) {
				System.err.println("Writing Results...");
				new TestResultWriter(_config).write(testSuitResult);
			}
		} catch (RuntimeException rex) {
			System.err.println(rex.getMessage());
		} finally {
			tearDown();
			System.err.println("Complete.");
		}
	}

	private ArrayList<String> getFilesToRun() {
		ArrayList<String> files = new ArrayList<String>();
		if (_config.getTestStepFile() != null) {
			files = new ArrayList<String>();
			files.add(_config.getTestStepFile());
		} else {
			files = getFilesInDir(_config.getTestStepDir());
		}
		return files;
	}

	private ArrayList<String> getFilesInDir(String dirName) {
		ArrayList<String> files = new ArrayList<String>();
		File dirObj = new File(dirName);
		for (File fileObj : dirObj.listFiles()) {
			files.add(fileObj.getAbsolutePath());
		}
		return files;
	}
	private void tearDown() {
		try {
			if (_config.getCompareImageDir() != null)
				FileUtils.deleteDirectory(new File(_config.getCompareImageDir()));
		} catch (IOException e) {
			System.err.println("Warning: failed to clear temp directory.");
		}
	}
}