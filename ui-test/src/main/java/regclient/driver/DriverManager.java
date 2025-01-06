package regclient.driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import regclient.utils.CapabilitiesReader;
import regclient.utils.PropertiesReader;

import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.net.MalformedURLException;

public class DriverManager {
	private static ThreadLocal<AppiumDriver> appiumDriver = new ThreadLocal<>();
	private static AppiumDriverLocalService service = null;

	private static AppiumDriver getAndroidDriver() {
		DesiredCapabilities desiredCapabilities = CapabilitiesReader.getDesiredCapabilities("androidDevice", "/DesiredCapabilities.json");
		appiumDriver.set(new AndroidDriver(service.getUrl(), desiredCapabilities));
		return appiumDriver.get();
	}

	public static AppiumDriver getDriver() throws MalformedURLException,InterruptedException {
		return getAndroidDriver();
	}

	public static void startAppiumServer() {
		PropertiesReader propertiesReader = new PropertiesReader();
		String ipAddress = System.getProperty("ipAddress") != null ? System.getProperty("ipAddress") : propertiesReader.getIpAddress();
		AppiumServiceBuilder builder = new AppiumServiceBuilder().withAppiumJS(new File(propertiesReader.getAppiumServerExecutable())).usingDriverExecutable(new File(propertiesReader.getNodePath())).withIPAddress(ipAddress).usingAnyFreePort().withArgument(GeneralServerFlag.LOCAL_TIMEZONE).withArgument(() -> "--allow-insecure","chromedriver_autodownload");
		service = AppiumDriverLocalService.buildService(builder);
		service.start();
	}

	public static void stopAppiumServer() {
		if (service != null)
			service.stop();
	}  
}
