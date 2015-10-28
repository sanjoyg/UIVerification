package org.sanjoy.uitest.result;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sanjoy.uitest.imaging.ImageVerifierResult;

public class TestFileResult {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
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

	public String toJSON() {
		String json = new String();

		json += "{\"file\" : \"" + this.getFileName() + "\",";
		json += "\"pass\" :\"" + (this.isPass()?"Pass":"Fail") + "\",";
		json += "\"fileResults\" : [";

		for (ImageVerifierResult imageResult : getResults()) {
			if (!json.endsWith("["))
				json += ",";
			json += imageResult.toJSON();
		}

		json += "]}";

		return json;
	}
}