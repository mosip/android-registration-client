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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DriverManager {
	private static ThreadLocal<AppiumDriver> appiumDriver = new ThreadLocal<>();
	private static AppiumDriverLocalService service = null;
	private static final int MAX_SESSION_ATTEMPTS = 2;

	private static AppiumDriver getAndroidDriver() {
		DesiredCapabilities desiredCapabilities = CapabilitiesReader.getDesiredCapabilities("androidDevice",
				"/DesiredCapabilities.json");
		if (service == null) {
			throw new IllegalStateException("Appium service not started. Call startAppiumServer() first.");
		}
		String udid = String.valueOf(desiredCapabilities.getCapability("appium:udid"));

		RuntimeException lastFailure = null;
		for (int attempt = 1; attempt <= MAX_SESSION_ATTEMPTS; attempt++) {
			try {
				appiumDriver.set(new AndroidDriver(service.getUrl(), desiredCapabilities));
				return appiumDriver.get();
			} catch (RuntimeException e) {
				lastFailure = e;
				if (attempt < MAX_SESSION_ATTEMPTS) {
					System.out.println("Appium session creation failed (attempt " + attempt + "/"
							+ MAX_SESSION_ATTEMPTS + "), cleaning up device state and retrying: " + e.getMessage());
					prepareDeviceForSession(udid);
				}
			}
		}
		throw lastFailure;
	}

	/**
	 * Recovers from a stale UiAutomator2 instrumentation still holding the device's UiAutomation
	 * connection, and wakes/unlocks the screen - the two most common causes of
	 * "UiAutomation not connected" on session creation.
	 */
	private static void prepareDeviceForSession(String udid) {
		runAdbQuietly(udid, "shell", "am", "force-stop", "io.appium.uiautomator2.server.test");
		runAdbQuietly(udid, "shell", "am", "force-stop", "io.appium.settings");
		runAdbQuietly(udid, "shell", "input", "keyevent", "224"); // KEYCODE_WAKEUP
		runAdbQuietly(udid, "shell", "wm", "dismiss-keyguard");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private static void runAdbQuietly(String udid, String... args) {
		try {
			List<String> command = new ArrayList<>(Arrays.asList("adb", "-s", udid));
			command.addAll(Arrays.asList(args));
			new ProcessBuilder(command).start().waitFor();
		} catch (Exception e) {
			System.out.println("Device cleanup step failed (continuing anyway): adb " + String.join(" ", args)
					+ " - " + e.getMessage());
		}
	}

	public static AppiumDriver getDriver() throws MalformedURLException, InterruptedException {
		return getAndroidDriver();
	}

	public static void startAppiumServer() {
		PropertiesReader propertiesReader = new PropertiesReader();
		String ipAddress = System.getProperty("ipAddress") != null ? System.getProperty("ipAddress")
				: propertiesReader.getIpAddress();
		AppiumServiceBuilder builder = new AppiumServiceBuilder()
				.withAppiumJS(new File(propertiesReader.getAppiumServerExecutable()))
				.usingDriverExecutable(new File(propertiesReader.getNodePath())).withIPAddress(ipAddress)
				.usingAnyFreePort().withArgument(GeneralServerFlag.LOCAL_TIMEZONE)
				.withArgument(() -> "--allow-insecure", "chromedriver_autodownload");
		service = AppiumDriverLocalService.buildService(builder);
		service.start();
	}

	public static void stopAppiumServer() {
		if (service != null)
			service.stop();
	}
}
