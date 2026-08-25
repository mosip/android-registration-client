package regclient.pages.french;

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

public class SettingsPageFrench extends SettingsPage {

	private final AppiumDriver driver;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"Paramètres des travaux planifiés\")")
	private WebElement scheduledJobsSettingsTab;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"Paramètres de configuration globale\")")
	private WebElement globalConfigSettingsTab;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"Réglages de l'appareil\")")
	private WebElement deviceSettingsTab;

	@AndroidFindBy(accessibility = "Key\nServer Value\nLocal Value")
	private WebElement globalConfigSettingsHeader;

	@AndroidFindBy(accessibility = "SUBMIT")
	private WebElement submitButton;

	@AndroidFindBy(accessibility = "No changes to save")
	private WebElement noChangesToSave;

	@AndroidFindBy(accessibility = "Réglages de l'appareil")
	private WebElement deviceSettingsPage;

	@AndroidFindBy(accessibility = "ANALYSE")
	private WebElement scanNowButton;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"mock.sbi.face\")")
	private WebElement faceDeviceCard;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"mock.sbi.iris\")")
	private WebElement irisDeviceCard;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"mock.sbi.finger\")")
	private WebElement fingerDeviceCard;

	@AndroidFindBy(accessibility = "No devices found")
	private WebElement noDevicesFound;

	@AndroidFindBy(accessibility = "Submit Changes")
	private WebElement submitChangesPopup;

	@AndroidFindBy(accessibility = "CONFIRM")
	private WebElement changesConfirmButton;

	@AndroidFindBy(accessibility = "CANCEL")
	private WebElement changesCancelButton;

	@AndroidFindBy(accessibility = "Paramètres des travaux planifiés")
	private WebElement scheduledJobSettingsPageHeader;

	@AndroidFindBy(xpath = "//*[@content-desc[contains(.,'Master Data Sync')]]")
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

	public SettingsPageFrench(AppiumDriver driver) {
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
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
			wait.until(ExpectedConditions.visibilityOf(deviceSettingsPage));
			return isElementDisplayed(deviceSettingsPage);
		} catch (TimeoutException e) {
			return false;
		}
	}

	public boolean isFaceDeviceCardDisplayed() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
			wait.until(ExpectedConditions.visibilityOf(faceDeviceCard));
			return isElementDisplayed(faceDeviceCard);
		} catch (TimeoutException e) {
			return false;
		}
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
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement card = wait.until(ExpectedConditions.presenceOfElementLocated(
				MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\"" + deviceName + "\")")));
		String desc = card.getAttribute("content-desc");
		System.out.println("Card text: " + desc);
		assertTrue(desc.contains(deviceName), "Device name not found");
		assertTrue(desc.toLowerCase().contains("ready"), "Device status not Ready");
		Pattern p = Pattern.compile("\\b[a-f0-9]{10,}\\b");
		Matcher m = p.matcher(desc);
		assertTrue(m.find(), "Device ID not found or empty");
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

	public boolean isMasterDataSyncCardDisplayed() {
		return isElementDisplayed(masterDataSyncCard);
	}

	public void clickOnMasterDataSyncButton() {
		clickOnElement(masterDataSyncButton);
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
		WebElement card = driver
				.findElement(By.xpath("//android.view.View[contains(@content-desc,'" + jobName + "')]"));
		String cd = card.getAttribute("content-desc");
		return cd.contains(jobName) && cd.contains("Next Run") && cd.contains("Last Sync")
				&& cd.contains("Cron Expression");
	}

	public boolean isJobDisplayed(String jobName) {

		try {
			By locator = MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true))"
					+ ".scrollIntoView(new UiSelector().descriptionContains(\"" + jobName + "\"))");

			WebElement element = driver.findElement(locator);
			return element.isDisplayed();

		} catch (Exception e) {
			return false;
		}
	}

	public void clickOnSyncButton(String jobName) {
		By job = MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true))"
				+ ".scrollIntoView(new UiSelector().descriptionContains(\"" + jobName + "\"))");
		WebElement jobElement = driver.findElement(job);
		WebElement syncBtn = jobElement.findElement(By.xpath(".//android.widget.Button[1]"));
		syncBtn.click();
	}
}
