package org.sanjoy.uitest.robotframework;

import org.sanjoy.uitest.imaging.ImageVerifier;
import org.sanjoy.uitest.imaging.ImageVerifierResult;

public class RobotUIVerifier {

	public static final String ROBOT_LIBRARY_SCOPE = "TEST CASE";

	public void verify(String baseImage, String compareImage) {
		ImageVerifier verifier = new ImageVerifier();
		ImageVerifierResult result = verifier.verify(baseImage, compareImage);
		if (!result.isPass()) {
			throw new RuntimeException("Image verification failed with " + result.getFailureCount() + " failures.");
		}
	}
}
