package org.sanjoy.uitest.standalone;

import org.sanjoy.uitest.config.Configuration;
import org.sanjoy.uitest.result.writer.TestResultWriter;

public class UIVerification {

	public void run(String[] args) {
		System.err.println("Starting...");
		Configuration config = Configuration.getInstance();

		try {
			config.processCommandLine(args);

			DirectoryComparator dirCompare = new DirectoryComparator(config.getStoreImageDir(),config.getCompareImageDir());
			dirCompare.run();
			new TestResultWriter().write(dirCompare.getTestSuitResult());

		} catch (RuntimeException rex) {
			System.err.println(rex.getMessage());
			if (Configuration.getInstance().isVerbose()) rex.printStackTrace();
		} finally {
			System.err.println("Complete.");
		}
	}

	public static void main(String[] args){
		new UIVerification().run(args);
	}
}