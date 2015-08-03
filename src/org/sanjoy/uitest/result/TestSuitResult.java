package org.sanjoy.uitest.result;

import java.awt.Rectangle;
import java.io.File;
import java.util.HashMap;

import org.sanjoy.uitest.imaging.ImageVerifierResult;

public class TestSuitResult {

	private long _startTime;
	private long _endTime;

	private HashMap<String,TestFileResult> _store = new HashMap<String,TestFileResult>();
	private boolean isPass = true;

	public TestSuitResult() {
		_startTime = System.currentTimeMillis();
	}

	public void setStartTime() {
		_startTime = System.currentTimeMillis();
	}

	public void addResult(String fileName, TestFileResult result) {
		isPass = isPass & result.isPass();
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

	public String json() {
		String result = "{ \"testsuitresult\" : { ";

		result += "\"pass\" : \"" + this.isPass + "\",";
		result += "\"startTime\" : \"" + this.getStartTime() + "\",";
		result += "\"endTime\" : \"" + this.getEndTime() + "\",";

		result += "\"suitresults\" : [";

		for (String fileName : this.getResults().keySet()) {
			TestFileResult fileResult = this.getResults().get(fileName);

			result += "{\"file\" : \"" + fileName + "\",";
			result += "\"pass\" :\"" + fileResult.isPass() + "\",";
			result += "\"fileresults\" : [";

			for (ImageVerifierResult imageResult : fileResult.getResults()) {
				if (!result.endsWith("["))
					result += ",";
				result += "{";
				result += "\"stepResult\" : {";
				result += "\"description\": \"" + new File(imageResult.getDescription()).toURI() + "\",";
				result += "\"pass\" : \"" + imageResult.isPass() + "\",";
				result += "\"baseImage\" : \"" + new File(imageResult.getBaseImage()).toURI() + "\",";
				result += "\"compareImage\" : \"" + new File(imageResult.getCompareToImage()).toURI() + "\",";
				result += "\"diffImage\" : \"" + new File(imageResult.getDiffImage()).toURI() + "\",";
				result += "\"failureCount\": \"" + imageResult.getFailureCount() + "\",";
				result += "\"diffPercent\": \"" + imageResult.getDiffPercent() + "\",";

				if (imageResult.getDiffRects() != null || imageResult.getDiffRects().size() == 0) {
					result += "\"diffRects\" : [";
					for (Rectangle diffRect : imageResult.getDiffRects()) {
						if (!result.endsWith("["))
							result += ",";
						result += " {";
						result += "\"x\":\"" + diffRect.x + "\",";
						result += "\"y\":\"" + diffRect.y + "\",";
						result += "\"width\":\"" + diffRect.width + "\",";
						result += "\"height\":\"" + diffRect.height + "\"";
						result += "}";
					}
					result += "]";
				}
				result += "}";
			}
			result += "}]";
		}
		result += "}] ";
		result += "}}";
		return result;
	}
}