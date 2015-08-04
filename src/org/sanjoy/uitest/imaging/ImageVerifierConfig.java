package org.sanjoy.uitest.imaging;

import org.sanjoy.uitest.config.Configuration;

public class ImageVerifierConfig {

	public static final String ROBOT_LIBRARY_SCOPE = "GLOBAL";

	public static String RETAIN_IMG_IN_DIFF				= "uiverify.retainimgindiff";
	public static String DIFF_MARK_COLOR_KEY 			= "uiverify.diffmarkcolor";
	public static String DIFF_DRAW_RECTS				= "uiverify.drawdiffrect";

	private static boolean  _drawDiffRects = true;
	private static int 		_diffMarkColor = ImageVerifier.GREY_SCALE_MARK_COLOR;
	private static boolean 	_retainOrgImage = false;

	private ImageVerifierConfig() {;}

	public static void setupImageVerificationProps() {
		/*
		 * createSeparate difference image : If a third image file is to be created
		 * difference mark color : default grey scale
		 * draw difference rectangles
		 */

		String value = System.getProperties().getProperty(RETAIN_IMG_IN_DIFF);
		ImageVerifierConfig.setRetainOrgImage(false);
		if (value != null && value.length() != 0 ) {
			if (Configuration.YES_STR.equalsIgnoreCase(value) || Configuration.TRUE_STR.equalsIgnoreCase(value))
				ImageVerifierConfig.setRetainOrgImage(true);
		}

		value = System.getProperties().getProperty(DIFF_MARK_COLOR_KEY);
		int diffMarkColor = ImageVerifier.GREY_SCALE_MARK_COLOR;
		if (value != null && value.length() != 0 ) {
			try {
				diffMarkColor = Integer.parseUnsignedInt(value);
				ImageVerifierConfig.setDiffMarkColor(diffMarkColor);
			} catch (Exception ex) {;}
		}

		value = System.getProperties().getProperty(DIFF_DRAW_RECTS);
		ImageVerifierConfig.setDrawDiffRects(false);
		if (value != null && value.length() != 0 ) {
			if (Configuration.YES_STR.equalsIgnoreCase(value) || Configuration.TRUE_STR.equalsIgnoreCase(value))
				ImageVerifierConfig.setDrawDiffRects(true);
		}
	}

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
