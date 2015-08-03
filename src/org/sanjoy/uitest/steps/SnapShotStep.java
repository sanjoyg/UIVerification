package org.sanjoy.uitest.steps;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class SnapShotStep extends ExecutionStep {

	private String _description;
	private String _fileName;
	private boolean _include = true;
	private ArrayList<Rectangle> _regions;

	public SnapShotStep() {

	}

	public void setInclude(boolean include) {
		_include = include;
	}

	public boolean isInclude() {
		return _include;
	}

	public String getDescription() {
		return _description;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public String getFileName() {
		return _fileName;
	}

	public void setFileName(String fileName) {
		_fileName = fileName;
	}

	public List<Rectangle> getRegions() {
		return _regions;
	}

	public void addRegion(Rectangle rect) {
		if (_regions == null)
			_regions = new ArrayList<Rectangle>();
		_regions.add(rect);
	}

	public void dump() {
		System.err.println("desc: " + _description);
	}
}