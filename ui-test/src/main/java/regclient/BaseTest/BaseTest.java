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
	}

	@AfterSuite(alwaysRun = true)
	public void afterSuite() {
		DriverManager.stopAppiumServer();
	}

}