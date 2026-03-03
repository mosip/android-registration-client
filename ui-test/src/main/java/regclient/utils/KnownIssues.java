package regclient.utils;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.log4testng.Logger;

import regclient.api.AdminTestUtil;

public class KnownIssues implements IInvokedMethodListener {

	private static final Logger logger = Logger.getLogger(KnownIssues.class);

	public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {

		if (!method.isTestMethod())
			return;

		String methodName = testResult.getMethod().getMethodName();
		String className = testResult.getTestClass().getRealClass().getSimpleName();

		for (String knownIssue : AdminTestUtil.getKnownIssues()) {

			if (knownIssue.equalsIgnoreCase(className)) {
				logger.warn("Skipping Known Issue CLASS: " + className + "." + methodName);
				throw new SkipException("KNOWN_ISSUE: " + className);
			}

			if (knownIssue.equalsIgnoreCase(methodName)) {
				logger.warn("Skipping Known Issue METHOD: " + className + "." + methodName);
				throw new SkipException("KNOWN_ISSUE: " + methodName);
			}
		}
	}

	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
	}
}
