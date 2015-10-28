package org.sanjoy.uitest.result.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.sanjoy.uitest.config.Configuration;
import org.sanjoy.uitest.error.ErrorMessages;
import org.sanjoy.uitest.imaging.ImageVerifierResult;
import org.sanjoy.uitest.result.TestFileResult;
import org.sanjoy.uitest.result.TestSuitResult;

public class TestResultWriter {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
	private final static String RESULT_FILE_NAME = "result.html";
	private final static String WEB_CONTEXT_KEY = "#WEB_CONTEXT#";
	private final static String JSON_KEY = "#INSERT_JSON#";
	private final static String IMAGES_DIR = "images";

	private Configuration _config = null;

	public TestResultWriter(Configuration config) {
		_config = config;
	}

	public void write(TestSuitResult results) {
		String resultFileName = _config.getReportDir() + File.separatorChar + RESULT_FILE_NAME;
		String resultFileNameTemp = resultFileName + ".temp";
		try {
			FileUtils.copyFile(new File(_config.getReportTemplate()), new File(resultFileNameTemp));
		} catch (IOException e) {
			throw new RuntimeException(ErrorMessages.ERROR_WRITING_RESULT + resultFileNameTemp + " : " + e.getMessage());
		}

		Iterator<String> resultsIter = results.getResults().keySet().iterator();

		String imagesPrefix = IMAGES_DIR + "/";

		while (resultsIter.hasNext()) {
			TestFileResult store = results.getResults().get(resultsIter.next());
			for (ImageVerifierResult result: store.getResults()) {
				String baseImg= moveImageAndReturnRelPath(_config.getReportImagesDir(), "base_",result.getBaseImage(),_config.getStoreImageDir());
				String compareImg = moveImageAndReturnRelPath(_config.getReportImagesDir(),"compare_",result.getCompareToImage(),_config.getCompareImageDir());
				String diffImg = moveImageAndReturnRelPath(_config.getReportImagesDir(),"diff_",result.getDiffImage(),_config.getCompareImageDir());
				result.setBaseImage(imagesPrefix + baseImg);
				result.setCompareToImage(imagesPrefix + compareImg);
				result.setDiffImage(imagesPrefix + diffImg);
			}
		}
		injectVariablesAndSave(resultFileNameTemp,resultFileName, results.toJSON());
	}

	private void injectVariablesAndSave(String from, String to, String json) {
		String templ = null;

		try {
			templ = FileUtils.readFileToString(new File(from));
		} catch (IOException e) {
			throw new RuntimeException(ErrorMessages.INVALID_REPORT_TEMPL_FILE + from);
		}

		templ = templ.replaceAll(WEB_CONTEXT_KEY, _config.getWebContext());
		templ = templ.replaceAll(JSON_KEY, json);

		try {
			FileUtils.writeStringToFile(new File(to), templ);
		} catch (IOException e) {
			throw new RuntimeException(ErrorMessages.ERROR_WRITING_RESULT + to + " : " + e.getMessage());
		}

		FileUtils.deleteQuietly(new File(from));
	}

	private String moveImageAndReturnRelPath(String reportImagesDir, String prefix, String srcPath, String basePath) {
		String destPath = prefix + srcPath.substring(basePath.length()+1);
		String moveDestPath = reportImagesDir + File.separatorChar + destPath;
		try {
			FileUtils.copyFile(new File(srcPath), new File(moveDestPath));
		} catch (IOException e) {
			// Consume
			System.err.println(ErrorMessages.WARN_COPYING_IMAGE + srcPath + " --> : " + moveDestPath);
		}
		return destPath;
	}
}