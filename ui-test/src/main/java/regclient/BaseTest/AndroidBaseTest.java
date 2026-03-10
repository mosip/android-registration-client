package regclient.BaseTest;

import java.time.Duration;

import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.SupportsRotation;
import regclient.driver.DriverManager;
import regclient.page.BasePage;
import regclient.utils.TestDataReader;
import regclient.utils.TestRunner;

public class AndroidBaseTest extends BaseTest {
	protected BasePage basePage;

	@BeforeMethod(alwaysRun = true)
	public void setup(ITestResult result) {

		String testId = result.getMethod().getDescription();

		if (testId == null || testId.trim().isEmpty()) {
		    testId = result.getMethod().getMethodName();
		}

		testId = testId.trim().toLowerCase();

		if (TestRunner.knownIssues.containsKey(testId)) {
		    String bugId = TestRunner.knownIssues.get(testId);
		    throw new SkipException("Skipping due to Known Issue: " + bugId);
		}

		// 🔥 2. Only if NOT skipped → start driver
		try {
			DriverManager.startAppiumServer();
			this.driver = DriverManager.getDriver();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		basePage = new BasePage(driver);
		basePage.applyOrientation();
	}

	@AfterMethod(alwaysRun = true)
	public void teardown(ITestResult result) {
		driver.quit();
	}
}
