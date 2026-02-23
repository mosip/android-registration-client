package regclient.pages.arabic;

import static org.testng.Assert.assertTrue;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import regclient.page.SettingsPage;

public class SettingsPageArabic extends SettingsPage {

	private final AppiumDriver driver;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"إعدادات الوظائف المجدولة\")")
	private WebElement scheduledJobsSettingsTab;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"إعدادات التكوين العامة\")")
	private WebElement globalConfigSettingsTab;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"إعدادات الجهاز\")")
	private WebElement deviceSettingsTab;

	@AndroidFindBy(accessibility = "Key\nServer Value\nLocal Value")
	private WebElement globalConfigSettingsHeader;

	@AndroidFindBy(accessibility = "SUBMIT")
	private WebElement submitButton;

	@AndroidFindBy(accessibility = "No changes to save")
	private WebElement noChangesToSave;

	@AndroidFindBy(accessibility = "إعدادات الجهاز")
	private WebElement deviceSettingsPage;

	@AndroidFindBy(accessibility = "مسح")
	private WebElement scanNowButton;

	@AndroidFindBy(accessibility = "ID: e88198714e67562c\nName: io.mosip.mock.sbi.face\nStatus: Ready")
	private WebElement faceDeviceCard;

	@AndroidFindBy(accessibility = "ID: e88198714e67562c\nName: io.mosip.mock.sbi.iris\nStatus: Ready")
	private WebElement irisDeviceCard;

	@AndroidFindBy(accessibility = "ID: e88198714e67562c\nName: io.mosip.mock.sbi.finger\nStatus: Ready")
	private WebElement fingerDeviceCard;

	@AndroidFindBy(accessibility = "No devices found")
	private WebElement noDevicesFound;

	@AndroidFindBy(accessibility = "Submit Changes")
	private WebElement submitChangesPopup;

	@AndroidFindBy(accessibility = "CONFIRM")
	private WebElement changesConfirmButton;

	@AndroidFindBy(accessibility = "CANCEL")
	private WebElement changesCancelButton;

	@AndroidFindBy(accessibility = "إعدادات المهمة المجدولة")
	private WebElement scheduledJobSettingsPageHeader;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"Master Data Sync\")")
	private WebElement masterDataSyncCard;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc,'Master Data Sync')]//android.widget.Button")
	private WebElement masterDataSyncButton;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc,'thumbs_fingerprint_threshold')]//android.widget.EditText")
	private WebElement thumbsThresholdField;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc,'iris_threshold')]//android.widget.EditText")
	private WebElement irisThresholdField;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc,'rightslap_fingerprint_threshold')]//android.widget.EditText")
	private WebElement rightSlapThresholdField;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc,'leftslap_fingerprint_threshold')]//android.widget.EditText")
	private WebElement leftSlapThresholdField;

	public SettingsPageArabic(AppiumDriver driver) {
		super(driver);
		this.driver = driver;
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}

	public boolean isScheduledJobsSettingsTabDisplayed() {
		return isElementDisplayed(scheduledJobsSettingsTab);
	}

	public boolean isGlobalConfigSettingsTabDisplayed() {
		return isElementDisplayed(globalConfigSettingsTab);
	}

	public boolean isDeviceSettingsTabDisplayed() {
		return isElementDisplayed(deviceSettingsTab);
	}

	public void clickOnGlobalConfigSettingsTab() {
		clickOnElement(globalConfigSettingsTab);
	}

	public boolean isGlobalConfigSettingsHeaderDisplayed() {
		return isElementDisplayed(globalConfigSettingsHeader);
	}

	public void clickOnSubmitButton() {
		clickOnElement(submitButton);
	}

	public boolean isNoChangesToSaveDisplayed() {
		return isElementDisplayed(noChangesToSave);
	}

	public void clickOnDeviceSettingsTab() {
		clickOnElement(deviceSettingsTab);
	}

	public boolean isScanNowButtonDisplayed() {
		return isElementDisplayed(scanNowButton);
	}

	public void clickOnScanNowButton() {
		clickOnElement(scanNowButton);
	}

	public boolean isDeviceSettingsPageDisplayed() {
		waitTime(2);
		return isElementDisplayed(deviceSettingsPage);
	}

	public boolean isFaceDeviceCardDisplayed() {
		waitTime(2);
		return isElementDisplayed(faceDeviceCard);
	}

	public boolean isIrisDeviceCardDisplayed() {
		return isElementDisplayed(irisDeviceCard);
	}

	public boolean isFingerDeviceCardDisplayed() {
		return isElementDisplayed(fingerDeviceCard);
	}

	public boolean isNoDevicesFoundDisplayed() {
		return isElementDisplayed(noDevicesFound);
	}

	public void clickOnScheduledJobsSettingsTab() {
		clickOnElement(scheduledJobsSettingsTab);
	}

	public void validateDeviceCard(String deviceName) {
		// Wait a bit for the card to appear (helps if page loads slowly)
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement card = wait.until(ExpectedConditions
				.presenceOfElementLocated(By.xpath("//*[contains(@content-desc,'" + deviceName + "')]")));

		String desc = card.getAttribute("content-desc");
		System.out.println("Card text: " + desc);

		Pattern p = Pattern.compile("ID:\\s*([a-zA-Z0-9]+)");
		Matcher m = p.matcher(desc);
		assertTrue(m.find(), "ID not found or empty");

		assertTrue(desc.contains("Name: " + deviceName), "Device name mismatch");
		assertTrue(desc.contains("Status: Ready"), "Device status not Ready");
	}

	public boolean isSubmitChangesPopupDisplayed() {
		return isElementDisplayed(submitChangesPopup);
	}

	public void clickOnChangesConfirmButton() {
		clickOnElement(changesConfirmButton);
	}

	public boolean isScheduledJobSettingsPageHeaderDisplayed() {
		return isElementDisplayed(scheduledJobSettingsPageHeader);
	}

	public boolean isJobDisplayed(String jobName) {

		// Scroll to jobs list (safe for long lists)
		driver.findElement(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true))"
				+ ".scrollIntoView(new UiSelector().className(\"android.widget.EditText\"))"));

		By job = By.xpath("//android.widget.EditText[contains(@hint,'" + jobName + "')]");

		return isElementDisplayed(job);
	}

	public void clickOnSyncButton(String jobName) {
		By syncButton = By.xpath("//android.widget.EditText[contains(@hint,'" + jobName + "')]");
		click(syncButton);
	}

	public boolean isToastVisible(String toastMessage) {
		for (int i = 0; i < 15; i++) { // ~3 seconds
			if (driver.getPageSource().contains(toastMessage)) {
				return true;
			}
			try {
				Thread.sleep(200);
			} catch (Exception ignored) {
			}
		}
		return false;
	}

	public WebElement getSyncButton(String jobName) {
		return driver.findElement(
				By.xpath("//android.view.View[contains(@content-desc,'" + jobName + "')]//*[@clickable='true']"));
	}

	public boolean validateJobCardFields(String jobName) {
		try {
			// Locate the Next Run / Last Sync field using job name as anchor
			WebElement field = driver.findElement(By.xpath(
					"//android.view.View[@content-desc='" + jobName + "']" + "/following::android.widget.EditText[1]"));

			String text = field.getText();

			return text.contains("Next Run") && text.contains("Last Sync");

		} catch (Exception e) {
			return false;
		}
	}

}
