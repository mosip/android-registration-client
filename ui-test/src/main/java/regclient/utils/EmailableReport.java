package regclient.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.collections.Lists;
import org.testng.internal.Utils;
import org.testng.xml.XmlSuite;

import io.mosip.testrig.apirig.utils.ConfigManager;
import regclient.api.ArcConfigManager;

/**
 * Reporter that generates a single-page HTML report of the test results.
 */
public class EmailableReport implements IReporter {

	protected PrintWriter writer;

	protected final List<SuiteResult> suiteResults = Lists.newArrayList();
	protected final boolean reportIgnoredTestCases = ConfigManager.reportIgnoredTestCases();
	protected final boolean reportKnownIssueTestCases = ConfigManager.reportKnownIssueTestCases();

	// Reusable buffer
	private final StringBuilder buffer = new StringBuilder();

	private String fileName = "emailable-report.html";

	private static final String JVM_ARG = "emailable.report2.name";

	private int totalPassedTests = 0;
	private static int totalSkippedTests = 0;
	private int totalIgnoredTests = 0;
	private int totalKnownIssueTests = 0;
	private static int totalFailedTests = 0;
	private long totalDuration = 0;

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		try {
			writer = createWriter(outputDirectory);
		} catch (IOException e) {
			return;
		}
		for (ISuite suite : suites) {
			suiteResults.add(new SuiteResult(suite));
		}
		writeDocumentStart();
		writeHead();
		writeBody();
		writeDocumentEnd();
		writer.close();

		int totalTestCases = totalPassedTests + totalFailedTests + totalSkippedTests + totalKnownIssueTests;
		if (reportIgnoredTestCases) {
			totalTestCases = totalTestCases + totalIgnoredTests;
		}
		if (reportKnownIssueTestCases) {
			totalTestCases = totalTestCases + totalKnownIssueTests;
		}
		String oldString = System.getProperty("emailable.report2.name");
		String temp = "-report_T-" + totalTestCases + "_P-" + totalPassedTests + "_S-" + totalSkippedTests + "_F-"
				+ totalFailedTests;
		if (reportIgnoredTestCases && reportKnownIssueTestCases) {
			temp = "-report_T-" + totalTestCases + "_P-" + totalPassedTests + "_S-" + totalSkippedTests + "_F-"
					+ totalFailedTests + "_I-" + totalIgnoredTests + "_KI-" + totalKnownIssueTests;
		} else if (reportIgnoredTestCases && !(reportKnownIssueTestCases)) {
			temp = "-report_T-" + totalTestCases + "_P-" + totalPassedTests + "_S-" + totalSkippedTests + "_F-"
					+ totalFailedTests + "_I-" + totalIgnoredTests;
		} else if (reportKnownIssueTestCases && !(reportIgnoredTestCases)) {
			temp = "-report_T-" + totalTestCases + "_P-" + totalPassedTests + "_S-" + totalSkippedTests + "_F-"
					+ totalFailedTests + "_KI-" + totalKnownIssueTests;
		}
		String newString = oldString.replace("-report", temp);

		File orignialReportFile = new File(System.getProperty("user.dir") + "/"
				+ System.getProperty("testng.outpur.dir") + "/" + System.getProperty("emailable.report2.name"));

		File newReportFile = new File(
				System.getProperty("user.dir") + "/" + System.getProperty("testng.outpur.dir") + "/" + newString);

		if (orignialReportFile.exists()) {
			if (orignialReportFile.renameTo(newReportFile)) {
				orignialReportFile.delete();

			}
		}
	}

	protected PrintWriter createWriter(String outdir) throws IOException {
		new File(outdir).mkdirs();
		String jvmArg = System.getProperty(JVM_ARG);
		if (jvmArg != null && !jvmArg.trim().isEmpty()) {
			fileName = jvmArg;
		}
		return new PrintWriter(new BufferedWriter(new FileWriter(new File(outdir, fileName))));
	}

	protected void writeDocumentStart() {
		writer.println(
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
		writer.print("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
	}

	protected void writeHead() {
		writer.print("<head>");
		writer.print("<title>TestNG Report</title>");
		writeStylesheet();
		writer.print("</head>");
	}

	protected void writeStylesheet() {
		writer.print("<style type=\"text/css\">");

		// base
		writer.print("body {font-family: Arial, Helvetica, sans-serif; font-size:13px; color:#111;}");

		// global table spacing
		writer.print("table {margin-bottom:20px;border-collapse:collapse;empty-cells:show;width: 100%;}");
		writer.print(".env-table { margin-bottom:28px; }");
		writer.print(".summary-block { margin-bottom:22px; }");
		writer.print(".scenario-block { margin-top:14px; margin-bottom:26px; }");

		// fixed layout for summary to keep columns aligned
		writer.print("#summary, #summary table { table-layout: fixed; }");

		// default cell styles
		writer.print("th, td {border:1px solid #bbb; padding:.35em .6em; font-size:13px; vertical-align:middle;}");
		writer.print("th { background:#f2f2f2; font-weight:700; }");
		writer.print("td { color:#111; }");

		// summary top styles (title and labels)
		writer.print(".summary-title { text-align:center; font-weight:700; padding:6px 0; }");
		writer.print(".summary-label { background:#efefef; text-align:center; font-weight:600; }");

		// colored bars used in the top summary overview
		writer.print(".bar { height:30px; text-align:center; vertical-align:middle; font-weight:700; color:#111; }");
		writer.print(".bar-total { background:#ffffff; }");
		writer.print(".bar-passed { background:#2fb500; }");
		writer.print(".bar-ignored { background:#ff9900; }");
		writer.print(".bar-known { background:#eaff7f; }");
		writer.print(".bar-skipped { background:#ffd54f; }");
		writer.print(".bar-failed { background:#e04b4b; }");

		// per-row result colors (apply to both td and th, with !important to override
		// older rules)
		// PASS -> green
		writer.print(
				".passedeven td, .passedodd td, .passedeven th, .passedodd th { background-color: #2fb500 !important; }");
		// SKIP -> yellow
		writer.print(
				".skippedeven td, .skippedodd td, .skippedeven th, .skippedodd th { background-color: #ffd54f !important; }");
		// FAIL -> red
		writer.print(
				".failedeven td, .failedodd td, .failedeven th, .failedodd th, .attn, .attn th { background-color: #e04b4b !important; }");

		// if you want slightly different header colors per odd/even
		writer.print(".passedeven th { background-color: #daf5d8 !important; }");
		writer.print(".failedeven th { background-color: #f8d7da !important; }");
		writer.print(".skippedeven th { background-color: #fff7dd !important; }");

		// number/time alignment
		writer.print(".num { text-align:center; white-space:nowrap; font-weight:600; }");

		// description cell - wrap but avoid pushing width
		writer.print(
				".desc { overflow:hidden; text-overflow:ellipsis; word-wrap:break-word; max-height:8em; padding-right:6px; }");

		// exception / details styling
		writer.print(".result { margin-top:12px; margin-bottom:8px; }");
		writer.print(
				".exception-header { text-align:center; font-weight:700; background:#f2f2f2; padding:6px; border:1px solid #bbb; }");
		writer.print(
				".stacktrace-box { background:#fff; border:1px solid #ccc; padding:12px; font-family:monospace; font-size:12px; white-space:pre-wrap; overflow:auto; }");

		// back-to-summary spacing
		writer.print(".totop { font-size:85%; text-align:center; margin-top:8px; margin-bottom:18px; }");

		writer.print("</style>");
	}

	protected void writeBody() {
		writer.print("<body>");
		writeSuiteSummary();
		writeScenarioSummary();
		writeScenarioDetails();
		writer.print("</body>");
	}

	protected void writeDocumentEnd() {
		writer.print("</html>");
	}

	protected void writeSuiteSummary() {
		NumberFormat integerFormat = NumberFormat.getIntegerInstance();

		totalPassedTests = 0;
		totalSkippedTests = 0;
		totalIgnoredTests = 0;
		totalKnownIssueTests = 0;
		totalFailedTests = 0;
		long totalDuration = 0;

		// compute totals across suites
		for (SuiteResult suiteResult : suiteResults) {
			for (TestResult testResult : suiteResult.getTestResults()) {
				totalPassedTests += testResult.getPassedTestCount();
				totalSkippedTests += testResult.getSkippedTestCount();
				totalFailedTests += testResult.getFailedTestCount();
				totalKnownIssueTests += testResult.getKnownIssueTestCount();
				totalDuration += testResult.getDuration();
			}
		}

		// top block (env info)
		writer.print("<table class='env-table'>");
		writer.print("<tr><th colspan='7'>");
		writer.print(Utils.escapeHtml("Use Cases Test Report ---- Report Date: " + printCurrentDateTime()
				+ " ---- Tested Environment: " + ArcConfigManager.getEnv()));
		writer.print("</th></tr>");
		writer.print("</table>");

		// summary-of-test-results block
		writer.print("<table class='summary-table' style='border:2px solid #2b2b90;'>");

		// define columns widths: tweak percentages to match screenshot proportions
		writer.print("<colgroup>");
		writer.print("<col style='width:14%'>"); // # Total
		writer.print("<col style='width:14%'>"); // # Passed
		writer.print("<col style='width:14%'>"); // # Ignored
		writer.print("<col style='width:14%'>"); // # Known Issues
		writer.print("<col style='width:14%'>"); // # Skipped
		writer.print("<col style='width:20%'>"); // # Failed
		writer.print("<col style='width:10%'>"); // Time
		writer.print("</colgroup>");

		// Title row
		writer.print("<tr>");
		writer.print("<th colspan='7' class='summary-title'>Summary of Test Results</th>");
		writer.print("</tr>");

		// Labels row
		writer.print("<tr>");
		writer.print("<td class='summary-label'># Total</td>");
		writer.print("<td class='summary-label'># Passed</td>");
		writer.print("<td class='summary-label'># Ignored</td>");
		writer.print("<td class='summary-label'># Known Issues</td>");
		writer.print("<td class='summary-label'># Skipped</td>");
		writer.print("<td class='summary-label'># Failed</td>");
		writer.print("<td class='summary-label'>Time (HH:MM:SS)</td>");
		writer.print("</tr>");

		// Values row (colored bars)
		writer.print("<tr>");
		writer.print("<td class='bar bar-total num'>" + integerFormat.format(
				totalPassedTests + totalFailedTests + totalSkippedTests + totalKnownIssueTests)
				+ "</td>");
		writer.print("<td class='bar bar-passed num'>" + integerFormat.format(totalPassedTests) + "</td>");
		// If you have an 'ignored' concept use it; here using 0 placeholder or compute
		// if available
		writer.print("<td class='bar bar-ignored num'>" + integerFormat.format(totalIgnoredTests) + "</td>");
		// If you track known issues, compute; placeholder 0 here
		writer.print("<td class='bar bar-known num'>" + integerFormat.format(totalKnownIssueTests) + "</td>");
		writer.print("<td class='bar bar-skipped num'>" + integerFormat.format(totalSkippedTests) + "</td>");
		writer.print("<td class='bar bar-failed num'>" + integerFormat.format(totalFailedTests) + "</td>");
		if (reportIgnoredTestCases) {
			writer.print("<td class='bar bar-ignored num'>" + integerFormat.format(totalIgnoredTests) + "</td>");
		}

		if (reportKnownIssueTestCases) {
			writer.print("<td class='bar bar-known num'>" + integerFormat.format(totalKnownIssueTests) + "</td>");
		}
		writer.print("<td class='time-cell'>" + formatDurationMillis(totalDuration) + "</td>");
		writer.print("</tr>");

		writer.print("</table>");
	}

	/**
	 * Writes a summary of all the test scenarios.
	 */
	protected void writeScenarioSummary() {
		writer.print("<table id='summary' class='summary-block'>");

		// fixed columns widths: method 25%, desc 65%, time 10% (tweak if you like)
		writer.print("<colgroup>");
		writer.print("<col style='width:25%'>");
		writer.print("<col style='width:65%'>");
		writer.print("<col style='width:10%'>");
		writer.print("</colgroup>");

		writer.print("<thead>");
		writer.print("<tr>");
		writer.print("<th> Test </th>");
		writer.print("<th> Description </th>");
		writer.print("<th>Time (HH:MM:SS)</th>");
		writer.print("</tr>");
		writer.print("</thead>");

		int testIndex = 0;
		int scenarioIndex = 0;
		for (SuiteResult suiteResult : suiteResults) {
			for (TestResult testResult : suiteResult.getTestResults()) {
				writer.print("<tbody id=\"t");
				writer.print(testIndex);
				writer.print("\">");

				String testName = Utils.escapeHtml("Scenarios");

				// The calls below print blocks for Failed / Skipped / Passed etc.

				scenarioIndex += writeScenarioSummary(testName + " &#8212; Failed", testResult.getFailedTestResults(),
						"failed", scenarioIndex);
				scenarioIndex += writeScenarioSummary(testName + " &#8212; Known Issues",
						testResult.getKnownIssueTestResults(), "skipped", scenarioIndex);

				scenarioIndex += writeScenarioSummary(testName + " &#8212; Skipped", testResult.getSkippedTestResults(),
						"skipped", scenarioIndex);
				scenarioIndex += writeScenarioSummary(testName + " &#8212; Passed", testResult.getPassedTestResults(),
						"passed", scenarioIndex);

				writer.print("</tbody>");

				testIndex++;
			}
		}

		writer.print("</table>");
	}

	/**
	 * Writes the scenario summary for the results of a given state for a single
	 * test.
	 */
	private int writeScenarioSummary(String description, List<ClassResult> classResults, String cssClassPrefix,
			int startingScenarioIndex) {
		int scenarioCount = 0;
		if (!classResults.isEmpty()) {
			// Apply result-based class to the block header row so it adopts the correct
			// color.
			// e.g. cssClassPrefix == "failed" -> header row class "failedodd" (uses CSS
			// rule for that class)
			writer.print("<tr class=\"" + cssClassPrefix + "odd\"><th colspan=\"3\">");
			writer.print(description);
			writer.print("</th></tr>");

			int scenarioIndex = startingScenarioIndex;
			int classIndex = 0;
			for (ClassResult classResult : classResults) {
				int methodIndex = 0;

				for (MethodResult methodResult : classResult.getMethodResults()) {
					List<ITestResult> results = methodResult.getResults();
					int resultsCount = results.size();
					assert resultsCount > 0;
					ITestResult firstResult = results.iterator().next();

					String methodName = Utils.escapeHtml(firstResult.getMethod().getMethodName());
					String methodDesc = firstResult.getMethod().getDescription();
					if (methodDesc == null) {
						methodDesc = "";
					} else {
						methodDesc = Utils.escapeHtml(methodDesc);
					}

					// pick odd/even suffix based on methodIndex to alternate row classes
					String suffix = ((methodIndex % 2) == 0) ? "even" : "odd";
					String rowClassPrefix = cssClassPrefix + suffix; // e.g., "failedeven" or "passedeven"

					for (int i = 0; i < resultsCount; i++) {
						ITestResult result = results.get(i);
						long scenarioStart = result.getStartMillis();
						long scenarioDuration = result.getEndMillis() - scenarioStart;

						// each row: method | description | time (HH:MM:SS)
						writer.print("<tr class=\"" + rowClassPrefix + "\">");
						writer.print("<td><a href=\"#m" + scenarioIndex + "\">" + methodName + "</a></td>");
						writer.print("<td class='desc'>" + methodDesc + "</td>");
						writer.print("<td class='num'>" + formatDurationMillis(scenarioDuration) + "</td>");
						writer.print("</tr>");

						scenarioIndex++;
					}
					methodIndex++;
				}
				classIndex++;
			}
			scenarioCount = scenarioIndex - startingScenarioIndex;
		}
		return scenarioCount;
	}

	public static String printCurrentDateTime() {
		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd MMMM HH:mm:ss", Locale.ENGLISH);
		String formattedDateTime = localDateTime.format(formatter);
		return formattedDateTime;
	}

	/**
	 * Writes the details for all test scenarios.
	 */
	protected void writeScenarioDetails() {
		int scenarioIndex = 0;
		for (SuiteResult suiteResult : suiteResults) {
			for (TestResult testResult : suiteResult.getTestResults()) {
				/*
				 * writer.print("<h2>");
				 * writer.print(Utils.escapeHtml(testResult.getTestName()));
				 * writer.print("</h2>");
				 */

				scenarioIndex += writeScenarioDetails(testResult.getFailedTestResults(), scenarioIndex);

				scenarioIndex += writeScenarioDetails(testResult.getSkippedTestResults(), scenarioIndex);
				scenarioIndex += writeScenarioDetails(testResult.getKnownIssueTestResults(), scenarioIndex);

				// scenarioIndex += writeScenarioDetails(testResult.getPassedTestResults(),
				// scenarioIndex);
			}
		}
	}

	/**
	 * Writes the scenario details for the results of a given state for a single
	 * test.
	 */
	private int writeScenarioDetails(List<ClassResult> classResults, int startingScenarioIndex) {
		int scenarioIndex = startingScenarioIndex;
		for (ClassResult classResult : classResults) {
			String className = classResult.getClassName();
			for (MethodResult methodResult : classResult.getMethodResults()) {
				List<ITestResult> results = methodResult.getResults();
				assert !results.isEmpty();
				// ITestResult firstResult = results.iterator().next();
				// String methodName=firstResult.getName();
				String label = Utils
						.escapeHtml(className + "#" + results.iterator().next().getMethod().getMethodName());
				for (ITestResult result : results) {
					writeScenario(scenarioIndex, label, result);
					scenarioIndex++;
				}
			}
		}

		return scenarioIndex - startingScenarioIndex;
	}

	/**
	 * Writes the details for an individual test scenario.
	 */
	private void writeScenario(int scenarioIndex, String label, ITestResult result) {
		writer.print("<h3 id=\"m");
		writer.print(scenarioIndex);
		writer.print("\">");
		writer.print(label);
		writer.print("</h3>");

		writer.print("<table class=\"result\">");

		// Reporter messages (if any)
		List<String> reporterMessages = Reporter.getOutput(result);
		if (!reporterMessages.isEmpty()) {
			writer.print("<tr><td colspan=\"1\">");
			writeReporterMessages(reporterMessages);
			writer.print("</td></tr>");
		}

		// Exception (if any)
		Throwable throwable = result.getThrowable();
		if (throwable != null) {
			// nicer exception header
			writer.print("<tr><td colspan=\"1\" class='exception-header'>Exception</td></tr>");
			writer.print("<tr><td>");
			writer.print("<div class='stacktrace-box'>");
			writer.print(Utils.shortStackTrace(throwable, true));
			writer.print("</div>");
			writer.print("</td></tr>");
		}

		writer.print("</table>");
		writer.print("<p class=\"totop\"><a href=\"#summary\">back to summary</a></p>");
	}

	protected void writeReporterMessages(List<String> reporterMessages) {
		writer.print("<div class=\"messages\">");
		Iterator<String> iterator = reporterMessages.iterator();
		assert iterator.hasNext();
		if (Reporter.getEscapeHtml()) {
			writer.print(Utils.escapeHtml(iterator.next()));
		} else {
			writer.print(iterator.next());
		}
		while (iterator.hasNext()) {
			writer.print("<br/>");
			if (Reporter.getEscapeHtml()) {
				writer.print(Utils.escapeHtml(iterator.next()));
			} else {
				writer.print(iterator.next());
			}
		}
		writer.print("</div>");
	}

	protected void writeStackTrace(Throwable throwable) {
		writer.print("<div class=\"stacktrace\">");
		writer.print(Utils.shortStackTrace(throwable, true));
		writer.print("</div>");
	}

	/**
	 * Writes a TH element with the specified contents and CSS class names.
	 * 
	 * @param html       the HTML contents
	 * @param cssClasses the space-delimited CSS classes or null if there are no
	 *                   classes to apply
	 */
	protected void writeTableHeader(String html, String cssClasses) {
		writeTag("th", html, cssClasses);
	}

	/**
	 * Writes a TD element with the specified contents.
	 * 
	 * @param html the HTML contents
	 */
	protected void writeTableData(String html) {
		writeTableData(html, null);
	}

	/**
	 * Writes a TD element with the specified contents and CSS class names.
	 * 
	 * @param html       the HTML contents
	 * @param cssClasses the space-delimited CSS classes or null if there are no
	 *                   classes to apply
	 */
	protected void writeTableData(String html, String cssClasses) {
		writeTag("td", html, cssClasses);
	}

	/**
	 * Writes an arbitrary HTML element with the specified contents and CSS class
	 * names.
	 * 
	 * @param tag        the tag name
	 * @param html       the HTML contents
	 * @param cssClasses the space-delimited CSS classes or null if there are no
	 *                   classes to apply
	 */
	protected void writeTag(String tag, String html, String cssClasses) {
		writer.print("<");
		writer.print(tag);
		if (cssClasses != null) {
			writer.print(" class=\"");
			writer.print(cssClasses);
			writer.print("\"");
		}
		writer.print(">");
		writer.print(html);
		writer.print("</");
		writer.print(tag);
		writer.print(">");
	}

	/**
	 * Groups {@link TestResult}s by suite.
	 */
	protected static class SuiteResult {
		private final String suiteName;
		private final List<TestResult> testResults = Lists.newArrayList();

		public SuiteResult(ISuite suite) {
			suiteName = suite.getName();
			for (ISuiteResult suiteResult : suite.getResults().values()) {
				testResults.add(new TestResult(suiteResult.getTestContext()));
			}
		}

		public String getSuiteName() {
			return suiteName;
		}

		/**
		 * @return the test results (possibly empty)
		 */
		public List<TestResult> getTestResults() {
			return testResults;
		}
	}

	/**
	 * Groups {@link ClassResult}s by test, type (configuration or test), and
	 * status.
	 */
	protected static class TestResult {
		/**
		 * Orders test results by class name and then by method name (in lexicographic
		 * order).
		 */
		protected static final Comparator<ITestResult> RESULT_COMPARATOR = new Comparator<ITestResult>() {
			@Override
			public int compare(ITestResult o1, ITestResult o2) {
				int result = o1.getTestClass().getName().compareTo(o2.getTestClass().getName());
				if (result == 0) {
					result = o1.getMethod().getMethodName().compareTo(o2.getMethod().getMethodName());
				}
				return result;
			}
		};

		private final String testName;
		private final List<ClassResult> failedTestResults;
		private final List<ClassResult> skippedTestResults;
		private final List<ClassResult> passedTestResults;
		private final List<ClassResult> knownIssueTestResults;
		private final int failedTestCount;
		private final int skippedTestCount;
		private final int passedTestCount;
		private final int knownIssueTestCount;
		private final long duration;
		private final String includedGroups;
		private final String excludedGroups;

		public TestResult(ITestContext context) {
			testName = context.getName();

			Set<ITestResult> failedTests = context.getFailedTests().getAllResults();
			Set<ITestResult> passedTests = context.getPassedTests().getAllResults();
			Set<ITestResult> skippedTests = context.getSkippedTests().getAllResults().stream()
					.filter(result -> result.getMethod().isTest()).collect(Collectors.toSet());

			Set<ITestResult> knownIssueTests = skippedTests.stream().filter(result -> {
				Throwable throwable = result.getThrowable();
				if (throwable != null && throwable.getMessage() != null) {
					return throwable.getMessage().contains("Known Issue");
				}
				return false;
			}).collect(Collectors.toSet());

			Set<ITestResult> actualSkippedTests = skippedTests.stream().filter(result -> {
				Throwable throwable = result.getThrowable();
				if (throwable != null && throwable.getMessage() != null) {
					return !throwable.getMessage().contains("Known Issue");
				}
				return true;
			}).collect(Collectors.toSet());
			failedTestResults = groupResults(failedTests);
			skippedTestResults = groupResults(actualSkippedTests);
			passedTestResults = groupResults(passedTests);
			knownIssueTestResults = groupResults(knownIssueTests);

			failedTestCount = failedTests.size();
			skippedTestCount = actualSkippedTests.size();
			passedTestCount = passedTests.size();
			knownIssueTestCount = knownIssueTests.size();

			duration = context.getEndDate().getTime() - context.getStartDate().getTime();

			includedGroups = formatGroups(context.getIncludedGroups());
			excludedGroups = formatGroups(context.getExcludedGroups());
		}

		/**
		 * Groups test results by method and then by class.
		 */
		protected List<ClassResult> groupResults(Set<ITestResult> results) {
			List<ClassResult> classResults = Lists.newArrayList();
			if (!results.isEmpty()) {
				List<MethodResult> resultsPerClass = Lists.newArrayList();
				List<ITestResult> resultsPerMethod = Lists.newArrayList();

				List<ITestResult> resultsList = Lists.newArrayList(results);
				Collections.sort(resultsList, RESULT_COMPARATOR);
				Iterator<ITestResult> resultsIterator = resultsList.iterator();
				assert resultsIterator.hasNext();

				ITestResult result = resultsIterator.next();
				resultsPerMethod.add(result);

				String previousClassName = result.getTestClass().getName();
				String previousMethodName = result.getMethod().getMethodName();
				while (resultsIterator.hasNext()) {
					result = resultsIterator.next();

					String className = result.getTestClass().getName();
					if (!previousClassName.equals(className)) {
						// Different class implies different method
						assert !resultsPerMethod.isEmpty();
						resultsPerClass.add(new MethodResult(resultsPerMethod));
						resultsPerMethod = Lists.newArrayList();

						assert !resultsPerClass.isEmpty();
						classResults.add(new ClassResult(previousClassName, resultsPerClass));
						resultsPerClass = Lists.newArrayList();

						previousClassName = className;
						previousMethodName = result.getMethod().getMethodName();
					} else {
						String methodName = result.getMethod().getMethodName();
						if (!previousMethodName.equals(methodName)) {
							assert !resultsPerMethod.isEmpty();
							resultsPerClass.add(new MethodResult(resultsPerMethod));
							resultsPerMethod = Lists.newArrayList();

							previousMethodName = methodName;
						}
					}
					resultsPerMethod.add(result);
				}
				assert !resultsPerMethod.isEmpty();
				resultsPerClass.add(new MethodResult(resultsPerMethod));
				assert !resultsPerClass.isEmpty();
				classResults.add(new ClassResult(previousClassName, resultsPerClass));
			}
			return classResults;
		}

		public String getTestName() {
			return testName;
		}

		/**
		 * @return the results for failed tests (possibly empty)
		 */
		public List<ClassResult> getFailedTestResults() {
			return failedTestResults;
		}

		/**
		 * @return the results for skipped tests (possibly empty)
		 */
		public List<ClassResult> getSkippedTestResults() {
			return skippedTestResults;
		}

		/**
		 * @return the results for passed tests (possibly empty)
		 */
		public List<ClassResult> getPassedTestResults() {
			return passedTestResults;
		}

		/**
		 * @return the results for passed tests (possibly empty)
		 */
		public List<ClassResult> getKnownIssueTestResults() {
			return knownIssueTestResults;
		}

		public int getFailedTestCount() {
			return failedTestCount;
		}

		public int getSkippedTestCount() {
			return skippedTestCount;
		}

		public int getPassedTestCount() {
			return passedTestCount;
		}

		public int getKnownIssueTestCount() {
			return knownIssueTestCount;
		}

		public long getDuration() {
			return duration;
		}

		public String getIncludedGroups() {
			return includedGroups;
		}

		public String getExcludedGroups() {
			return excludedGroups;
		}

		/**
		 * Formats an array of groups for display.
		 */
		protected String formatGroups(String[] groups) {
			if (groups.length == 0) {
				return "";
			}

			StringBuilder builder = new StringBuilder();
			builder.append(groups[0]);
			for (int i = 1; i < groups.length; i++) {
				builder.append(", ").append(groups[i]);
			}
			return builder.toString();
		}
	}

	/**
	 * Groups {@link MethodResult}s by class.
	 */
	protected static class ClassResult {
		private final String className;
		private final List<MethodResult> methodResults;

		/**
		 * @param className     the class name
		 * @param methodResults the non-null, non-empty {@link MethodResult} list
		 */
		public ClassResult(String className, List<MethodResult> methodResults) {
			this.className = className;
			this.methodResults = methodResults;
		}

		public String getClassName() {
			return className;
		}

		/**
		 * @return the non-null, non-empty {@link MethodResult} list
		 */
		public List<MethodResult> getMethodResults() {
			return methodResults;
		}
	}

	/**
	 * Groups test results by method.
	 */
	protected static class MethodResult {
		private final List<ITestResult> results;

		/**
		 * @param results the non-null, non-empty result list
		 */
		public MethodResult(List<ITestResult> results) {
			this.results = results;
		}

		/**
		 * @return the non-null, non-empty result list
		 */
		public List<ITestResult> getResults() {
			return results;
		}
	}

	private String formatDurationMillis(long millis) {
		if (millis < 0) {
			millis = 0;
		}
		long totalSeconds = millis / 1000;
		long hours = totalSeconds / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		long seconds = totalSeconds % 60;
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

}
