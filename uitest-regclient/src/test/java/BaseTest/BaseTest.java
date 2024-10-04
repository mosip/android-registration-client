package BaseTest;


import io.appium.java_client.AppiumDriver;
import regclient.driver.DriverManager;
import regclient.utils.TestDataReader;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

public class BaseTest {
    protected AppiumDriver driver;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
    	System.getProperties().setProperty("testng.outpur.dir", "test-output");
		System.getProperties().setProperty("emailable.report2.name", "AndroidRegClient-"+TestDataReader.readData("env")
				+"-run-" + System.currentTimeMillis() + "-report.html");
            DriverManager.startAppiumServer();
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
            DriverManager.stopAppiumServer();
    }

}