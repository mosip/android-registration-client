package BaseTest;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

import regclient.driver.DriverManager;

import java.net.MalformedURLException;

public class AndroidBaseTest extends BaseTest {
	@BeforeMethod(alwaysRun = true)
	public void setup() {
		try {
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
