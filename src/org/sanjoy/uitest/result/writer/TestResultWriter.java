package org.sanjoy.uitest.result.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.sanjoy.uitest.config.Configuration;
import org.sanjoy.uitest.error.ErrorMessages;
import org.sanjoy.uitest.imaging.ImageVerifierResult;
import org.sanjoy.uitest.result.TestFileResult;
import org.sanjoy.uitest.result.TestSuitResult;

// Horrible way to do it, ran out of patience to use free-marker
public class TestResultWriter {

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");

	public void write(TestSuitResult results) {

		String report = writePrologue();

		report = report + "\n\t\t\t<div class=\"header\"><h1>UI Comparison Report</h1></div>";
		report = report + "\n\t\t\t<h2>Start : " + dateFormat.format(new Date(results.getStartTime())) +
								"&nbsp;&nbsp;&nbsp;End : " + dateFormat.format(new Date(results.getEndTime())) + "</h2>";


		report = report + "\n\t\t\t<table class=\"grid\" width=\"100%\">";
		report = report + "\n\t\t\t\t<caption>Test Results</caption>";
		report = report + "\n\t\t\t\t<tbody>";


		Iterator<String> resultsIter = results.getResults().keySet().iterator();

		int count = 0;
		while (resultsIter.hasNext()) {
			TestFileResult store = results.getResults().get(resultsIter.next());

			for (ImageVerifierResult result: store.getResults()) {
				count++;
				report = report + "\n\t\t\t\t\t<tr>";
				report = report + "\n\t\t\t\t\t<td>" +  count + "</td>";
				report = report + "\n\t\t\t\t\t<td>" +  result.getDescription() + "</td>";
				boolean isPass = result.isPass();
				report = report + "\n\t\t\t\t\t<td class=\"" + (isPass?"green":"red") + "\">"+ (isPass?"Pass":"Fail");
				if (!isPass) {
					Configuration config = Configuration.getInstance();
					String baseImg= moveImageAndReturnRelPath("base_",result.getBaseImage(),config.getStoreImageDir());
					String compareImg = moveImageAndReturnRelPath("compare_",result.getCompareToImage(),config.getCompareImageDir());
					String diffImg = moveImageAndReturnRelPath("diff_",result.getDiffImage(),config.getCompareImageDir());

					report = report + "&nbsp;&nbsp" + result.getFailureCount() + " Failures : " + result.getDiffPercent();
					report = report + "&nbsp;&nbsp;";
					report = report + "<a href=\"images/" + baseImg + "\" target=\"_blank\">Baseline</a>&nbsp;&nbsp;";
					report = report + "<a href=\"images/" + compareImg + "\" target=\"_blank\">Compare To</a>&nbsp;&nbsp;";
					report = report + "<a href=\"images/" + diffImg + "\" target=\"_blank\">Difference</a>";
				}
				report = report + "</td>";
				report = report + "\n\t\t\t\t\t</tr>";
			}
		}

		report = report + "\n\t\t\t\t</tbody>";
		report = report + "\n\t\t\t</table>";
		report = report + "\n\t\t</div>";
		report = report + "\n\t</body>\n</html>";

		String reportFileName = Configuration.getInstance().getReportDir() + File.separatorChar + "result.html";

		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File(reportFileName)));
			bw.write(report);
			bw.flush();
		} catch (IOException e) {
			throw new RuntimeException(ErrorMessages.ERROR_WRITING_RESULT + reportFileName + " : " + e.getMessage());
		} finally {
			try { if (bw != null) bw.close(); } catch (Exception ex) {}
			try { if (fw != null) fw.close(); } catch (Exception ex) {}
		}
	}

	private String moveImageAndReturnRelPath(String prefix, String srcPath, String basePath) {
		String destPath = prefix + srcPath.substring(basePath.length()+1);
		String moveDestPath = Configuration.getInstance().getReportImagesDir() + File.separatorChar + destPath;
		try {
			FileUtils.copyFile(new File(srcPath), new File(moveDestPath));
		} catch (IOException e) {
			// Consume
			System.err.println(ErrorMessages.WARN_COPYING_IMAGE + srcPath + " --> : " + moveDestPath);
		}
		return destPath;
	}

	private String writePrologue() {
		String report ="<html>\n\t<head>";

		report = report + "	<style type=\"text/css\">";
		report = report + "	*{margin: 0; padding: 0;}";
		report = report + "	body{ font-family: 'Calibri'; color: #111; background: #aaa; }";
		report = report + "	.container{ width: 75%; margin: 0 auto; }";
		report = report + "	h1{ font-size: 30px; color: #069; padding: 10px; background:#f8f8f8; text-align: center; border:#ddd 1px solid; text-transform: uppercase; }";
		report = report + "	h2{ font-size: 18px; margin-top: 10px; color: #333; padding: 10px; text-align: center; background:#f8f8f8; border:#ddd 1px solid; }";
		report = report + "	.summary{ font-size: 18px; color: #444; padding: 10px; background:#f8f8f8; margin: 10px 0px; border:#ddd 1px solid; }";
		report = report + "	.summary h3{ font-size: 14px; font-weight: bold; }";
		report = report + "	.summary p{ font-size: 14px; }";
		report = report + "	.grid{ min-width: 40%; border-collapse: collapse; background: #fff; }";
		report = report + "	.grid caption{ padding: 5px; background: #222; color: #eee; }";
		report = report + "	.grid tbody tr:nth-child(even){ background: #f5f5f5; }";
		report = report + "	.grid th,.grid td{ padding: 5px 10px; border: #ccc 1px solid; }";
		report = report + "	.grid tbody tr td{text-align: center;}";
		report = report + "	.success{background: #f00}";
		report = report + "	.error{background: #f2521b}";
		report = report + "	.grid .red{color: #ff0000;}";
		report = report + "	.grid .green{color: #009900;}";
		report = report + "	</style>";
		report = report + "\n\t</head>";
		report = report + "\n\t<body>\n\t\t<div class=\"container\">";
		return report;
	}
}