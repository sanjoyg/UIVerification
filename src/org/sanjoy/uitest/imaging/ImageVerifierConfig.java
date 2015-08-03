package org.sanjoy.uitest.imaging;

public class ImageVerifierConfig {

	public static final String ROBOT_LIBRARY_SCOPE = "GLOBAL";

	private static boolean  _drawDiffRects = true;
	private static int 		_diffMarkColor = ImageVerifier.GREY_SCALE_MARK_COLOR;
	private static boolean 	_retainOrgImage = false;

	private ImageVerifierConfig() {;}

	public static boolean isDrawDiffRects() {
		return _drawDiffRects;
	}

	public static void setDrawDiffRects(boolean drawDiffRects) {
		_drawDiffRects = drawDiffRects;
	}
	public static boolean isRetainOrgImage() {
		return _retainOrgImage;
	}

	public static void setRetainOrgImage(boolean makeDiffImage) {
		_retainOrgImage = makeDiffImage;
	}

	public static int getDiffMakrColor() {
		return _diffMarkColor;
	}

	public static void setDiffMarkColor(int diffMarkColor) {
		_diffMarkColor = diffMarkColor;
	}

}
