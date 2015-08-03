package org.sanjoy.uitest.result;

import java.util.ArrayList;
import java.util.List;

import org.sanjoy.uitest.imaging.ImageVerifierResult;

public class TestFileResult {

	private List<ImageVerifierResult> _store = new ArrayList<ImageVerifierResult>();
	private long _startTime;
	private long _endTime;
	private String _fileName;
	private boolean _isPass = true;

	public TestFileResult() {
		_startTime = System.currentTimeMillis();
	}

	public void setPass(boolean isPass) {
		_isPass = isPass;
	}

	public boolean isPass() {
		return _isPass;
	}

	public long getStartTime() {
		return _startTime;
	}

	public void setEndTime() {
		_endTime = System.currentTimeMillis();
	}

	public long getEndTime() {
		return _endTime;
	}

	public List<ImageVerifierResult> getResults() {
		return _store;
	}

	public void addResult(ImageVerifierResult result) {
		_isPass = _isPass & result.isPass();
		_store.add(result);
	}

	public String getFileName() {
		return _fileName;
	}

	public void setFileName(String fileName) {
		_fileName = fileName;
	}
}