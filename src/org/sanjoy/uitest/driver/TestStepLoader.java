package org.sanjoy.uitest.driver;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.sanjoy.uitest.steps.ExecutionStep;
import org.sanjoy.uitest.steps.SnapShotStep;

public class TestStepLoader {
	private static String SNAPSHOT_METHOD_NAME 	= "takeSnapShot";
	private static String INCLUDE_STR			= "include";
	private static String EXCLUDE_STR			= "exclude";
	private static String COMMENT_LINE_STR		= "#";
	private static String PARM_DELIMITER		= ",";
	private static String REGION_DELIMITER		= ":";
	private static String DIMENSION_DELIMITER	= "x";

	public TestStepLoader() {

	}

	public List<ExecutionStep> load(String fileName) {
		List<ExecutionStep> steps = new ArrayList<ExecutionStep>();

		FileReader fr = null;
		BufferedReader br = null;

		int lineNo=0;
		try {
			fr = new FileReader(new File(fileName));
			br = new BufferedReader(fr);

			String line;
			while ((line = br.readLine()) != null) {
				lineNo++;
				if (!line.startsWith(COMMENT_LINE_STR) && line != null && line.trim().length() != 0)
					steps.add(processLine(lineNo, line, steps));
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to load/read file : " + fileName + " : " + e.getMessage());
		} finally {
			try { if (br != null) br.close(); } catch (Exception e) {;}
			try { if (fr != null) fr.close(); } catch (IOException e) {;}
		}
		return steps;
	}

	private ExecutionStep processLine(int lineNo, String line, List<ExecutionStep> steps) {

		StringTokenizer tokenizer = new StringTokenizer(line, PARM_DELIMITER);
		String command = tokenizer.nextToken();
		if (command == null || command.length() == 0) {
			throw new RuntimeException("Line : " + lineNo + ", Syntax error : Command is not specified.");
		}

		ExecutionStep step;
		if (SNAPSHOT_METHOD_NAME.equals(command)) {
			step = new SnapShotStep();
		} else {
			step = new ExecutionStep();
		}

		step.setMethod(command);
		while (tokenizer.hasMoreTokens()) {
			step.addParm(tokenizer.nextToken().trim());
		}

		if (SNAPSHOT_METHOD_NAME.equals(command)) {
			validateSnapshotStep(lineNo, (SnapShotStep)step);
		}

		return step;
	}

	private ExecutionStep validateSnapshotStep(int line, SnapShotStep step) {
		String firstParm = (String) (step.getParms().size() > 0 ? step.getParms().get(0) : null);
		if (firstParm == null || firstParm.length() ==0) {
			throw new RuntimeException("Line : " + line + ",Syntax Error expected description post keyword for takeSnapShot");
		}
		step.setDescription(((String)firstParm).trim());

		String secondParm = (String) (step.getParms().size() > 1 ? step.getParms().get(1) : null);
		if (secondParm == null) {
			System.err.println("Line : " + line + ", Syntax Error expected fileName as second parameter for keyword takeSnapShot");
			return null;
		}
		step.setFileName(((String)secondParm).trim());

		if (step.getParms().size() > 2) {
			String thirdParm = (String)(step.getParms().size() > 2 ? step.getParms().get(2) : "");
			if (thirdParm == null || thirdParm.length() == 0 ||
					!(INCLUDE_STR.equalsIgnoreCase(thirdParm) || EXCLUDE_STR.equalsIgnoreCase(thirdParm))) {
				throw new RuntimeException("Line : " + line + ", Syntax Error Expected include/exclude after instead of : " + thirdParm);
			}
			step.setInclude(INCLUDE_STR.equalsIgnoreCase(thirdParm));

			String fourthParm = (String)(step.getParms().size() > 3 ? step.getParms().get(3) : "");
			if (fourthParm == null || fourthParm.length() == 0 ) {
				throw new RuntimeException("Line : " + line + ", Syntax Error, Expected region definition.");
			}

			StringTokenizer regionTokenizer = new StringTokenizer(fourthParm, REGION_DELIMITER);
			while (regionTokenizer.hasMoreTokens()) {
				String token = regionTokenizer.nextToken();
				step.addRegion(parseRegion(line,token));
			}
		}
		return step;
	}

	private Rectangle parseRegion(int line, String value) {
		StringTokenizer tokenizer = new StringTokenizer(value, DIMENSION_DELIMITER);
		try {
			String numberStr = tokenizer.nextToken().trim();

			if (numberStr == null || numberStr.length() == 0) return null;
			int x = Integer.parseInt(numberStr);

			numberStr = tokenizer.nextToken().trim();
			if (numberStr == null || numberStr.length() == 0) return null;
			int y = Integer.parseInt(numberStr);

			numberStr = tokenizer.nextToken().trim();
			if (numberStr == null || numberStr.length() == 0) return null;
			int width = Integer.parseInt(numberStr);

			numberStr = tokenizer.nextToken().trim();
			if (numberStr == null || numberStr.length() == 0) return null;
			int height = Integer.parseInt(numberStr);

			return new Rectangle(x,y,width,height);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Line : " + line + ", Syntax Error in specifying region format [ xcord x ycord x width x height : xcord x ycord x width x height : ...]");
		}
	}
}