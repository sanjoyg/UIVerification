package org.sanjoy.uitest.imaging;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;

public class ImageVerifierResult {
	private boolean _pass = false;
    private float 	_diffPercent = -1;
    private int 	_imageArea = 0;
    private String 	_baseImage = null;
	private String 	_compareToImage = null;
	private String 	_diffImage = null;
	private String 	_description = null;

    private ArrayList<Rectangle> _diffRects = null;

	public boolean isPass() {
		return _pass;
	}

	public void setPass(boolean pass) {
		this._pass = pass;
	}

	public ArrayList<Rectangle> getDiffRects() {
		return _diffRects;
	}

	public void setDiffRects(ArrayList<Rectangle> diffRects) {
		this._diffRects = diffRects;
	}

	public float getDiffPercent() {
		if (_imageArea == 0 || _diffRects == null)
    		return 0;
    	if (_diffPercent == -1) {
    		int diffArea = 0;
    		for (Rectangle rectangle : _diffRects) {
    			diffArea += rectangle.width * rectangle.height;
    		}
    		_diffPercent = (((float)diffArea)/_imageArea)*100;
    	}
    	return _diffPercent;
	}

	public void setDiffPercent(float diffPercent) {
		this._diffPercent = diffPercent;
	}

	public int getImageArea() {
		return _imageArea;
	}

	public void setImageArea(int imageArea) {
		this._imageArea = imageArea;
	}

	public String getBaseImage() {
		return _baseImage;
	}

	public void setBaseImage(String baseImage) {
		this._baseImage = baseImage;
	}

	public String getCompareToImage() {
		return _compareToImage;
	}

	public void setCompareToImage(String compareToImage) {
		this._compareToImage = compareToImage;
	}

	public String getDiffImage() {
		return _diffImage;
	}

	public void setDiffImage(String diffImage) {
		this._diffImage = diffImage;
	}

	public String getDescription() {
		return _description;
	}

	public void setDescription(String description) {
		this._description = description;
	}

	public int getFailureCount() {
		return (_diffRects == null ? 0 : _diffRects.size());
	}

	public String toJSON() {
		String json = new String();

		json += "{";
		json += "\"stepResult\" : {";
		json += "\"description\": \"" + this.getDescription() + "\",";
		json += "\"pass\" : \"" + (this.isPass() ? "Pass" : "Fail") + "\",";


		json += "\"baseImage\" : \"" + this.getBaseImage() + "\",";
		json += "\"compareImage\" : \"" + this.getCompareToImage() + "\",";
		json += "\"diffImage\" : \"" + this.getDiffImage() + "\",";

		json += "\"failureCount\": \"" + this.getFailureCount() + "\",";
		json += "\"diffPercent\": \"" + ((int)this.getDiffPercent()) + "%\"";

		if (getDiffRects() != null || getDiffRects().size() == 0) {
			json += ",\"diffRects\" : [";
			for (Rectangle diffRect : getDiffRects()) {
				if (!json.endsWith("["))
					json += ",";
				json += " {";
				json += "\"x\":\"" + diffRect.x + "\",";
				json += "\"y\":\"" + diffRect.y + "\",";
				json += "\"width\":\"" + diffRect.width + "\",";
				json += "\"height\":\"" + diffRect.height + "\"";
				json += "}";
			}
			json += "]";
		}
		json += "}}";
		return json;
	}
}