package regclient.BaseTest;

import io.appium.java_client.AppiumDriver;
import regclient.driver.DriverManager;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

public class BaseTest {
	protected AppiumDriver driver;

	@BeforeSuite(alwaysRun = true)
	public void beforeSuite() {
		DriverManager.startAppiumServer();
		try {
			driver = DriverManager.getDriver();
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize AppiumDriver", e);
		}
	}

	@AfterSuite(alwaysRun = true)
	public void afterSuite() {
		if (driver != null) {
			driver.quit();
		}
		DriverManager.stopAppiumServer();
	}

}