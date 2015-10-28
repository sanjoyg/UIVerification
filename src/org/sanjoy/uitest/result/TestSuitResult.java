package org.sanjoy.uitest.result;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class TestSuitResult {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
	private long _startTime;
	private long _endTime;

	private HashMap<String,TestFileResult> _store = new HashMap<String,TestFileResult>();
	private boolean _isPass = true;

	public TestSuitResult() {
		_startTime = System.currentTimeMillis();
	}

	public void setStartTime() {
		_startTime = System.currentTimeMillis();
	}

	public void addResult(String fileName, TestFileResult result) {
		_isPass = _isPass & result.isPass();
		_store.put(fileName, result);
	}

	public HashMap<String, TestFileResult> getResults() {
		return _store;
	}

	public long getStartTime() {
		return _startTime;
	}

	public long getEndTime() {
		return _endTime;
	}

	public void setEndTime() {
		_endTime = System.currentTimeMillis();
	}

	public String toJSON() {
		String json = "{ \"testSuiteResult\" : { ";

		json += "\"pass\" : \"" + (this._isPass ? "Pass":"Fail") + "\",";
		json += "\"startTime\" : \"" + dateFormat.format(new Date(this.getStartTime())) + "\",";
		json += "\"endTime\" : \"" + dateFormat.format(new Date(this.getEndTime())) + "\",";

		json += "\"suiteResults\" : [";

		for (String fileName : this.getResults().keySet()) {
			if (!json.endsWith("["))
				json += ",";
			json += this.getResults().get(fileName).toJSON();
		}

		json += "]}}";
		return json;
	}
}