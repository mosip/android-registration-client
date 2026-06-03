package regclient.page;

import static io.appium.java_client.touch.WaitOptions.waitOptions;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.touch.offset.PointOption;

public class MockSBIPage extends BasePage {
	private WebDriverWait wait;

	@AndroidFindBy(id = "io.mosip.mock.sbi:id/settingBtn")
	private WebElement mockSbiSettingsButton;

	@AndroidFindBy(id = "io.mosip.mock.sbi:id/face_device_status")
	private WebElement faceDeviceStatusDropdown;

	@AndroidFindBy(id = "io.mosip.mock.sbi:id/finger_device_status")
	private WebElement fingerDeviceStatusDropdown;

	@AndroidFindBy(id = "io.mosip.mock.sbi:id/iris_device_status")
	private WebElement irisDeviceStatusDropdown;

	@AndroidFindBy(id = "io.mosip.mock.sbi:id/button12")
	private WebElement mockSbiSaveButton;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text='Device Config']")
	private WebElement deviceConfigTitle;

	public MockSBIPage(AppiumDriver driver) {
		super(driver);
		this.driver = driver;
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	public void switchToMockSBI() {
		String MOCKSBI_PACKAGE = "io.mosip.mock.sbi";
		String MOCKSBI_ACTIVITY = "client.ClientActivity";

		Activity mocksbi = new Activity(MOCKSBI_PACKAGE, MOCKSBI_ACTIVITY);
		((AndroidDriver) driver).startActivity(mocksbi);
	}

	public void clickOnMockSbiSettingsButton() {

		for (int i = 0; i < 3; i++) {
			clickOnElement(mockSbiSettingsButton);

			if (isElementDisplayed(deviceConfigTitle, 3)) {
				return;
			}
		}

		throw new RuntimeException("Device Config page not displayed after clicking Settings");
	}

	public void setAllToNotReadyAndSave() {

		setAllToNotReady("Face", "io.mosip.mock.sbi:id/face_device_status");
		scrollUntilElementVisible(By.id("io.mosip.mock.sbi:id/finger_device_status"));
		setAllToNotReady("Finger", "io.mosip.mock.sbi:id/finger_device_status");
		scrollUntilElementVisible(By.id("io.mosip.mock.sbi:id/iris_device_status"));
		setAllToNotReady("Iris", "io.mosip.mock.sbi:id/iris_device_status");

		clickOnElement(mockSbiSaveButton);
	}

	public void switchBackToArcApp() {
		AndroidDriver driver = (AndroidDriver) this.driver;
		try {
			// detect the package from session capability
			String mainPackage = String.valueOf(driver.getCapabilities().getCapability("appium:appPackage"));
			String mainActivity = String.valueOf(driver.getCapabilities().getCapability("appium:appActivity"));
			// if current package already matches, nothing to do
			if (mainPackage != null && mainPackage.equals(driver.getCurrentPackage())) {
				return;
			}
			// Try to simply bring ARC app to foreground if installed
			if (driver.isAppInstalled(mainPackage)) {
				driver.activateApp(mainPackage);
				return;
			}
			// Fallback: use startActivity if activateApp didn't work
			if (mainActivity != null && !mainActivity.isEmpty()) {
				driver.startActivity(new Activity(mainPackage, mainActivity));
			}
		} catch (Exception e) {
			System.err.println("Failed to switch back to ARC app: " + e.getMessage());
		}
	}

	public void setAllToReadyAndSave() {

		setAllToReady("Face", "io.mosip.mock.sbi:id/face_device_status");
		scrollUntilElementVisible(By.id("io.mosip.mock.sbi:id/finger_device_status"));
		setAllToReady("Finger", "io.mosip.mock.sbi:id/finger_device_status");
		scrollUntilElementVisible(By.id("io.mosip.mock.sbi:id/iris_device_status"));
		setAllToReady("Iris", "io.mosip.mock.sbi:id/iris_device_status");

		clickOnElement(mockSbiSaveButton);
	}

	public void setAllModalityLowScore() {
		// ModalityScore should be (20-5=15)
		setModalityScore("Iris", 20);
		swipeUp();
		clickOnElement(mockSbiSaveButton);
	}

	public void setAllModalityHighScore() {
		setModalityScore("Iris", 90);
		scrollUntilElementVisible(AppiumBy.id("io.mosip.mock.sbi:id/button12"));
		clickOnElement(mockSbiSaveButton);
	}

	private void setAllToNotReady(String section, String dropdownId) {

		driver.findElement(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true))"
				+ ".scrollIntoView(new UiSelector().text(\"" + section + "\"));"));
		WebElement dropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(dropdownId)));
		clickOnElement(dropdown);
		WebElement notReady = wait.until(ExpectedConditions
				.visibilityOfElementLocated(MobileBy.AndroidUIAutomator("new UiSelector().text(\"Not Ready\")")));
		clickOnElement(notReady);

	}

	private void setAllToReady(String section, String dropdownId) {

		driver.findElement(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true))"
				+ ".scrollIntoView(new UiSelector().text(\"" + section + "\"));"));
		WebElement dropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(dropdownId)));
		clickOnElement(dropdown);
		WebElement ready = wait.until(ExpectedConditions
				.elementToBeClickable(MobileBy.AndroidUIAutomator("new UiSelector().text(\"Ready\")")));
		clickOnElement(ready);

	}

	private void setModalityScore(String modality, int score) {
		try {
			scrollToText(modality);

			String modLower = modality.toLowerCase();
			String xpath = String.format(
					"//android.widget.SeekBar[contains(@resource-id, 'slider_%s_score') or contains(@content-desc, '%s Score')]",
					modLower, modality);

			WebElement seekBar = findElementIfExists(By.xpath(xpath)); // non-throwing
			// fallback: a few swipes + re-checks
			for (int i = 0; i < 5 && seekBar == null; i++) {
				swipeUp();
				waitTime(1);
				seekBar = findElementIfExists(By.xpath(xpath));
			}
			// final attempt using retry (may throw) — catch below
			if (seekBar == null) {
				seekBar = findElementWithRetry(By.xpath(xpath));
			}

			if (seekBar == null) {
				throw new RuntimeException("SeekBar not found for modality: " + modality);
			}

			setSeekBarPercent(seekBar, score);
			waitTime(1);
			System.out.println("Set " + modality + " -> " + score);
		} catch (Exception e) {
			System.err.println("Failed to set modality '" + modality + "': " + e.getMessage());
		}
	}

	private void scrollToText(String text) {
		if (text == null || text.isEmpty())
			return;

		try {
			driver.findElement(MobileBy.AndroidUIAutomator(
					"new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().text(\"" + text
							+ "\"))"));
			return;
		} catch (Exception ignored) {
		}

		try {
			driver.findElement(MobileBy.AndroidUIAutomator(
					"new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().textContains(\""
							+ text + "\"))"));
			return;
		} catch (Exception ignored) {
		}

		for (int i = 0; i < 5; i++) {
			try {
				if (driver.findElements(MobileBy.AndroidUIAutomator("new UiSelector().text(\"" + text + "\")"))
						.size() > 0
						|| driver
								.findElements(
										MobileBy.AndroidUIAutomator("new UiSelector().textContains(\"" + text + "\")"))
								.size() > 0) {
					return;
				}
			} catch (Exception ignored) {
			}
			swipeUp();
			waitTime(1);
		}
	}

	public void setSeekBarPercent(WebElement seekBar, int percent) {

	    Rectangle rect = seekBar.getRect();

	    int targetX = rect.x + (rect.width * percent / 100);
	    int targetY = rect.y + (rect.height / 2);

	    clickAtCoordinates(targetX, targetY);

	    waitTime(1);
	}

}
