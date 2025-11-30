package regclient.page;

import static io.appium.java_client.touch.WaitOptions.waitOptions;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
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
		clickOnElement(mockSbiSettingsButton);
	}

	public void setAllToNotReadyAndSave() {

		setAllToNotReady("Face", "io.mosip.mock.sbi:id/face_device_status");
		swipeOrScroll();
		setAllToNotReady("Finger", "io.mosip.mock.sbi:id/finger_device_status");
		swipeOrScroll();
		setAllToNotReady("Iris", "io.mosip.mock.sbi:id/iris_device_status");

		clickOnElement(mockSbiSaveButton);
	}

	public void switchBackToArcApp() {
		AndroidDriver driver = (AndroidDriver) this.driver;
		try {
			String mainPackage = String.valueOf(driver.getCapabilities().getCapability("appium:appPackage"));
			String mainActivity = String.valueOf(driver.getCapabilities().getCapability("appium:appActivity"));
			if (mainPackage != null && mainPackage.equals(driver.getCurrentPackage())) {
				return;
			}
			if (driver.isAppInstalled(mainPackage)) {
				driver.activateApp(mainPackage);
				return;
			}
			if (mainActivity != null && !mainActivity.isEmpty()) {
				driver.startActivity(new Activity(mainPackage, mainActivity));
			}
		} catch (Exception e) {
			System.err.println("Failed to switch back to ARC app: " + e.getMessage());
		}
	}

	public void setAllToReadyAndSave() {

		setAllToReady("Face", "io.mosip.mock.sbi:id/face_device_status");
		swipeOrScroll();
		setAllToReady("Finger", "io.mosip.mock.sbi:id/finger_device_status");
		swipeOrScroll();
		setAllToReady("Iris", "io.mosip.mock.sbi:id/iris_device_status");

		clickOnElement(mockSbiSaveButton);
	}

	public void setAllModalityLowScroe() {
		// ModalityScore should be (20-5=15)
		setModalityScore("Face", 20);
		swipeOrScroll();
		clickOnElement(mockSbiSaveButton);
	}

	public void setAllModalityHighScore() {
		setModalityScore("Face", 90);
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

			for (int i = 0; i < 5 && seekBar == null; i++) {
				swipeOrScroll();
				waitTime(1);
				seekBar = findElementIfExists(By.xpath(xpath));
			}
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
			swipeOrScroll();
			waitTime(1);
		}
	}

	public void setSeekBarPercent(WebElement seekBar, int percent) {
		if (seekBar == null)
			throw new IllegalArgumentException("seekBar cannot be null");
		if (percent < 0)
			percent = 0;
		if (percent > 100)
			percent = 100;

		int startX = seekBar.getLocation().getX();
		int width = seekBar.getSize().getWidth();
		int y = seekBar.getLocation().getY() + (seekBar.getSize().getHeight() / 2);

		// 🔸 calibration offsets (approx 4–5% on both sides)
		double leftOffset = 0.04; // skip a few px from start
		double rightOffset = 0.96; // stop a bit before end

		double ratio = percent / 100.0;
		// Apply left/right correction depending on where target is
		if (ratio < leftOffset)
			ratio = leftOffset;
		if (ratio > rightOffset)
			ratio = rightOffset;

		int targetX = startX + (int) (width * ratio);

		try {
			PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
			Sequence drag = new Sequence(finger, 1);
			drag.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX + 5, y));
			drag.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
			drag.addAction(
					finger.createPointerMove(Duration.ofMillis(400), PointerInput.Origin.viewport(), targetX, y));
			drag.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
			driver.perform(Collections.singletonList(drag));
			waitTime(1);
		} catch (Exception ex) {
			clickAtCoordinates(targetX, y);
		}
	}

}
