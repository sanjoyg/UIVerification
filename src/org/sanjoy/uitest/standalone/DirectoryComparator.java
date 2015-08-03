package org.sanjoy.uitest.standalone;

import java.io.File;

import org.sanjoy.uitest.imaging.ImageVerifier;
import org.sanjoy.uitest.imaging.ImageVerifierResult;
import org.sanjoy.uitest.result.TestFileResult;
import org.sanjoy.uitest.result.TestSuitResult;

public class DirectoryComparator {

	private TestSuitResult _store = new TestSuitResult();
	private String _storeImagePath = null;
	private String _compareImagePath = null;

	public DirectoryComparator(String storeImagePath, String compareImagePath) {
		_storeImagePath = storeImagePath;
		_compareImagePath = compareImagePath;
	}

	public void run() {
		TestFileResult fileResult = new TestFileResult();
		_store.addResult("__default__", fileResult);

		File storeImageDir = new File(_storeImagePath);
		for (File storeImageFile : storeImageDir.listFiles()) {
			String storeImageFileName = storeImageFile.getAbsolutePath();
			String compareImageFileName = _compareImagePath;
			compareImageFileName += File.separatorChar + storeImageFileName.substring(_storeImagePath.length());

			ImageVerifierResult result = new ImageVerifier().verify(storeImageFileName, compareImageFileName);
			result.setDescription(storeImageFileName);

			fileResult.addResult(result);
		}
	}

	public TestSuitResult getTestSuitResult() {
		return _store;
	}
}