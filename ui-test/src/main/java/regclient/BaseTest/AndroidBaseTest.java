package regclient.BaseTest;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import regclient.driver.DriverManager;


public class AndroidBaseTest extends BaseTest {
	@BeforeMethod(alwaysRun = true)
	public void setup() {
		try {
			DriverManager.startAppiumServer();
			this.driver = DriverManager.getDriver();
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	@AfterMethod(alwaysRun = true)
	public void teardown(ITestResult result) {
		driver.quit();
	}
}
