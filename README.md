<h1><strong>UI Verification Tool</strong></h1>

<hr />
<p>During project life-cycle we used Selenium/Robotframework to automate functional test cases. While these tools did a fantastic job on testing the functional correctness of the site that was been developed, we had challenges around UI alignment or UI not been correct (such as header, footer) etc. This tool is a simple utility to test UI correctness of any site works on basic image recognition using snapshot of pages (via Selenium).</p>

<p>The utility can work in a three modes</p>

<ul>
	<li>Test Driver</li>
	<li>Standalone></li>
	<li>API included in a framework</li>
</ul>

<p><strong><u>Test Driver</u></strong></p>

<p>In this mode the program runs as a utility where it can drive a website using selenium. The commands are specified in a &ldquo;teststep&rdquo; file or a directory containing multiple &ldquo;teststep&rdquo; files which may be passed as a command line or in a configuration file. The run can be run in two modes.</p>

<p>STORE&nbsp;&nbsp;&nbsp;&nbsp; : This is to create a baseline based on the &lsquo;teststeps&rsquo; file of pages that would need to be compared.</p>

<p>COMPARE: This is to run the same &lsquo;teststeps&rsquo; file to compare against the baseline images by the STORE run.</p>

<p>Set the JAR file in the CLASSPATH</p>

<p><code>java org.sanjoy.uitest STORE config.file</code> </p>

<p><em>Configuration</em></p>

<p>One mandatory command-line parameter is requires, which is the mode of the run. Which is either &ldquo;STORE&rdquo; or &ldquo;COMPARE&rdquo; (case in-sensitive), the operations of which are described above. The rest of the configuration parameters are specified in either as command line JVM argument using &ndash;D<em>key</em>=<em>value</em> or by specifying in the configuration file as a property file (<em>key=value</em>) format. The same configuration key can be specified as a JVM argument or configuration property key. For example</p>

<code>java Duiverify.verbose=true or in the configuration file specify ui.verbose=true</code>
<p>If a key is passed as a JVM argument and also in the configuration file, the value defined in the configuration file overrides the value passed as a JVM argument.</p>

<table border="1" cellpadding="0" cellspacing="0" height="1180" width="807">
	<tbody>
		<tr>
			<td>
			<p style="text-align: center;"><strong>Configuration Key</strong></p>
			</td>
			<td>
			<p style="text-align: center;"><strong>Valid Values</strong></p>
			</td>
			<td>
			<p style="text-align: center;"><strong>Mandatory/Optional</strong></p>
			</td>
			<td>
			<p style="text-align: center;"><strong>Remarks</strong></p>
			</td>
		</tr>
		<tr>
			<td>
			<p>uiverify.testfile</p>
			</td>
			<td>
			<p>Path to a teststep file</p>
			</td>
			<td>
			<p>Mandatory (if uiverify.testdir not specified)</p>
			</td>
			<td>
			<p>Reads the commands to be executed on the selenium driver. Essentially a test driver.</p>
			</td>
		</tr>
		<tr>
			<td>
			<p>uiverify.testdir</p>
			</td>
			<td>
			<p>Path to a directory containing teststep files</p>
			</td>
			<td>
			<p>Mandatory (if uiverify.testfile not specified)</p>
			</td>
			<td>
			<p>Reads the directory for all files, which are treated as teststep files and are executed.</p>

			<p><em>Only one of uiverify.testfile or uiverify.testdir can be specified.</em></p>
			</td>
		</tr>
		<tr>
			<td>
			<p>uiverify.storedir</p>
			</td>
			<td>
			<p>Path to a directory.</p>
			</td>
			<td>
			<p>Mandatory</p>
			</td>
			<td>
			<p>This is the directory where the baseline images will be stored. While running in <em>STORE</em> mode, the utility writes to this directory. While running in <em>COMPARE</em> mode, the utility refers to this directory for the baseline images</p>
			</td>
		</tr>
		<tr>
			<td>
			<p>uiverify.comparedir</p>
			</td>
			<td>
			<p>Path to a directory</p>
			</td>
			<td>
			<p>Mandatory in COMPARE mode</p>
			</td>
			<td>
			<p>This is the directory where the images during the <em>COMPARE</em> run will be temporarily stores for comparison. Post run this directory is cleared.</p>
			</td>
		</tr>
		<tr>
			<td>
			<p>uiverify.resultsdir</p>
			</td>
			<td>
			<p>Path to a directory</p>
			</td>
			<td>
			<p>Mandatory in COMPARE mode</p>
			</td>
			<td>
			<p>Results are written to this directory. A sub-directory with current date-time is created, if already exists appends numbers to create a unique name.</p>
			</td>
		</tr>
		<tr>
			<td>
			<p>uiverify.verbose</p>
			</td>
			<td>
			<p>Boolean</p>
			</td>
			<td>
			<p>Optional</p>

			<p>Default : false</p>
			</td>
			<td>
			<p>Controls the verbosity of the run</p>
			</td>
		</tr>
		<tr>
			<td>
			<p>uiverify.parallelize</p>
			</td>
			<td>
			<p>Number</p>
			</td>
			<td>
			<p>Optional</p>

			<p>Default : 1</p>
			</td>
			<td>
			<p>Controls the number of threads to execute in parallel. If uiverify.testfile is specified, increasing the threads has no affect. Only when it has multiple files to run via uiverify.testdir while it spawn multiple threads.</p>
			</td>
		</tr>
		<tr>
			<td>
			<p>uiverify.retainimgindiff</p>
			</td>
			<td>
			<p>Boolean</p>
			</td>
			<td>
			<p>Optional</p>

			<p>Default: False</p>
			</td>
			<td>
			<p>Controls if the compared image bits are retained in the difference image. By default pixels which match are put as black pixel in the difference image</p>
			</td>
		</tr>
		<tr>
			<td>
			<p>uiverify.diffmarkcolor</p>
			</td>
			<td>
			<p>Integer</p>
			</td>
			<td>
			<p>Optional</p>

			<p>Default: Gray Scale</p>
			</td>
			<td>
			<p>Controls what color would be used to mark the differences in pixels in difference image. By default gray scale pixel is used. One could verify produce a RGB value as integer</p>
			</td>
		</tr>
		<tr>
			<td>
			<p>uiverify.drawdiffrect</p>
			</td>
			<td>
			<p>Boolean</p>
			</td>
			<td>
			<p>Optional</p>

			<p>Default: true</p>
			</td>
			<td>
			<p>Controls if a rectangle is drawn around the differences.</p>
			</td>
		</tr>
		<tr>
			<td>
			<p>uiverify.reporttempl</p>
			</td>
			<td>
			<p>Path to the template HTML</p>
			</td>
			<td>
			<p>Mandatory in COMPARE mode</p>
			</td>
			<td>
			<p>Provides the path to the template HTML to be used. The one used is present in the template directory. The path should include the file name</p>
			</td>
		</tr>
		<tr>
			<td>
			<p>uiverify.webcontext</p>
			</td>
			<td>
			<p>Web Context to use in the result file</p>
			</td>
			<td>
			<p>Optional</p>

			<p>Default: <strong>.</strong></p>
			</td>
			<td>
			<p>Provides the web context that would be prefixed to the resources JS/Images in the result file</p>
			</td>
		</tr>
	</tbody>
</table>

<p>&nbsp;</p>

<p>The following table enumerates the commands that can be specified in the &ldquo;teststep&rdquo; file to drive a browser. Each token is separated by a &ldquo;,&rdquo; character, the first token is always the keyword command. As an example to open &ldquo;Chrome&rdquo; browser the command in the &ldquo;teststep&rdquo; file would be</p>

<p><code>openBrowser, chrome</code></p>

<table border="1" cellpadding="0" cellspacing="0" height="2042" width="807">
	<tbody>
		<tr>
			<td>
			<p style="text-align: center;"><strong>Keyword</strong></p>
			</td>
			<td>
			<p style="text-align: center;"><strong>Arguments</strong></p>
			</td>
			<td>
			<p style="text-align: center;"><strong>Remarks</strong></p>
			</td>
		</tr>
		<tr>
			<td>
			<p>openBrowser</p>
			</td>
			<td>
			<ol>
				<li>Browser name</li>
				<li>URL</li>
			</ol>
			</td>
			<td>
			<p>The first argument is the name of the browser to launch, valid values are</p>

			<ul>
				<li>Chrome</li>
				<li>FireFox</li>
				<li>IE</li>
				<li>Remote</li>
			</ul>

			<p>If the browser name is specified as remote, then the second argument is mandatory and should specify the URL to connect.</p>
			</td>
		</tr>
		<tr>
			<td>
			<p>Maximize</p>
			</td>
			<td>
			<p style="margin-left:.25in;">&nbsp;</p>
			</td>
			<td>
			<p>Maximizes the browser window</p>
			</td>
		</tr>
		<tr>
			<td>
			<p>enterURL</p>
			</td>
			<td>
			<ol>
				<li>URL</li>
			</ol>
			</td>
			<td>
			<p>Will make the browser navigate to the URL given. Please include http/https in the URL</p>
			</td>
		</tr>
		<tr>
			<td>
			<p>scrollToBottom</p>
			</td>
			<td>
			<p style="margin-left:.25in;">&nbsp;</p>
			</td>
			<td>
			<p>Scrolls the browser window to the bottom</p>
			</td>
		</tr>
		<tr>
			<td>
			<p>scrollToTop</p>
			</td>
			<td>
			<p style="margin-left:.25in;">&nbsp;</p>
			</td>
			<td>
			<p>Scrolls the browser window to the top</p>
			</td>
		</tr>
		<tr>
			<td>
			<p>enterText</p>
			</td>
			<td>
			<ol>
				<li>Locator Type</li>
				<li>Locator Value</li>
				<li>Text to enter</li>
			</ol>
			</td>
			<td>
			<p>The locator type is the method by which the locator value should be located. It is essentially the &ldquo;By&rdquo; class of Selenium. Valid values are:</p>

			<ul>
				<li>id</li>
				<li>name</li>
				<li>xpath</li>
				<li>css</li>
				<li>linkText</li>
				<li>partialLinkText</li>
				<li>class</li>
			</ul>

			<p>Locator value is the value to locate by locator type in the DOM.</p>

			<p>Once successfully located, the third argument &ldquo;text&rdquo; is entered/selected on that element.</p>

			<p>For example to find a TextBox with &ldquo;id&rdquo;, &ldquo;search_box&rdquo; and entering &ldquo;UI Validator&rdquo;, the line in the teststep file would look like</p>

			<p>enterText, id, search_box, UI Validator</p>
			</td>
		</tr>
		<tr>
			<td>
			<p>ClickOnButton</p>

			<p>ClickOnLink</p>
			</td>
			<td>
			<ol>
				<li>Locator Type</li>
				<li>Locator Value</li>
			</ol>

			<p style="margin-left:.25in;">&nbsp;</p>
			</td>
			<td>
			<p>Same as above</p>
			</td>
		</tr>
		<tr>
			<td>
			<p>selectDropDown</p>
			</td>
			<td>
			<ol>
				<li>Locator Type</li>
				<li>Locator Value</li>
				<li>Text to select</li>
			</ol>
			</td>
			<td>
			<p>First two arguments, refer to remarks of &ldquo;enterText&rdquo; keyword</p>

			<p>&nbsp;</p>

			<p>The third argument is used to select the text from the drop down.</p>
			</td>
		</tr>
		<tr>
			<td>
			<p>sleep</p>
			</td>
			<td>
			<ol>
				<li>Time in milliseconds</li>
			</ol>
			</td>
			<td>
			<p>Sleeps for the specified time period</p>
			</td>
		</tr>
		<tr>
			<td>
			<p>takeSnapShot</p>
			</td>
			<td>
			<ol>
				<li>Description</li>
				<li>File name</li>
				<li>Include/Exclude (optional)</li>
				<li>Region specification (optional)</li>
			</ol>
			</td>
			<td>
			<p>Description is free flow text which will be used in the resulting report to indicate the step, does not influence the test.</p>

			<p>&nbsp;</p>

			<p>File name will be the name used to store the image in the store directory while running in the <em>STORE mode</em>. In the <em>COMPARE or STANDALONE </em>mode this would be used to locate the file in the store directory for baseline image.</p>

			<p>&nbsp;</p>

			<p>Third argument takes only two values &ldquo;include&rdquo; or &ldquo;exclude&rdquo;. This specifies while in <em>COMPARE or STANDALONE</em> mode, if the comparison to include or exclude the regions specified in the 4<sup>th</sup> argument. This is useful when there are regions on the page that you want to exclude for comparison. Example would be a ticker.</p>

			<p>&nbsp;</p>

			<p>Fourth argument is mandatory when third is specified. It is specified in the format of region delimited by &ldquo;:&rdquo;. Multiple regions may be specified separated by &ldquo;:&rdquo;. Each region is specified as x coordinate &ldquo;x&rdquo; y coordinate &ldquo;x&rdquo; width &ldquo;x&rdquo; height. Example:</p>

			<p>120x200x100x300 : Region specification with x-coordinate as 120, y-coordinate 200, width 100 and height 300.</p>

			<p>120x200x100x300 : 5x5x20x20 : 10x10:30x30&nbsp; - Specifies three regions that are separated by &ldquo;:&rdquo;</p>

			<p>&nbsp;</p>
			</td>
		</tr>
		<tr>
			<td>
			<p>closeBrowser</p>
			</td>
			<td>
			<p style="margin-left:.25in;">&nbsp;</p>
			</td>
			<td>
			<p>Closed the browser and releases the driver object.</p>
			</td>
		</tr>
	</tbody>
</table>

<p>&nbsp;</p>

<p><strong><u>Standalone</u></strong></p>

<p>The standalone is triggered when the first argument is passed as <em>&ldquo;STANDALONE&rdquo;</em>. In this mode the utility runs always in <em>COMPARE </em>mode and doesn&rsquo;t expect any teststep file as it doesn&rsquo;t run any test. This mode is handy when with an existing test framework, snapshots are baseline and comparison images are taken and only differences and report generation is required. For existing assets, where a team doesn&rsquo;t want to write new &ldquo;teststeps&rdquo; to be written again would be useful.</p>

<code>java org.sanjoy.uitest STORE config.file</code>

<p>Please read the &ldquo;Configuration&rdquo; section in &ldquo;TestDriver&rdquo; mode.</p>

<p><strong><u>API</u></strong></p>

<p>Read the source code and include as library</p>

<p>&nbsp;</p>


<p>Have Fun!<p>
<p>Sanjoy Ghosh</p>
