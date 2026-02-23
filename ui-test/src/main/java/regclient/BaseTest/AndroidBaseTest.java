package regclient.BaseTest;

import java.time.Duration;

import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import io.appium.java_client.remote.SupportsRotation;
import regclient.driver.DriverManager;
import regclient.page.BasePage;
import regclient.utils.TestDataReader;

public class AndroidBaseTest extends BaseTest {
	 protected BasePage basePage;
	 
	@BeforeMethod(alwaysRun = true)
	public void setup() {
		try {
			DriverManager.startAppiumServer();
			this.driver = DriverManager.getDriver();
		} catch (Exception e) {
			throw new RuntimeException();
		}
		basePage = new BasePage(driver);
	    basePage.applyOrientation();
	}
	
	@AfterMethod(alwaysRun = true)
	public void teardown(ITestResult result) {
		driver.quit();
	}
}
