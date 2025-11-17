package regclient.pages.english;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import io.appium.java_client.android.Activity;

import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.touch.offset.PointOption;

import static org.testng.Assert.assertTrue;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.StartsActivity;
import regclient.api.FetchUiSpec;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.api.FetchUiSpec;
import regclient.page.BasePage;
import regclient.page.SettingsPage;

public class SettingsPageEnglish extends SettingsPage {

	private final AppiumDriver driver;

	@AndroidFindBy(accessibility = "Scheduled Jobs Settings\nTab 1 of 3")
	private WebElement scheduledJobsSettingsTab;

	@AndroidFindBy(accessibility = "Global Config Settings\nTab 2 of 3")
	private WebElement globalConfigSettingsTab;

	@AndroidFindBy(accessibility = "Device Settings\nTab 3 of 3")
	private WebElement deviceSettingsTab;

	@AndroidFindBy(accessibility = "Key\nServer Value\nLocal Value")
	private WebElement globalConfigSettingsHeader;

	@AndroidFindBy(accessibility = "SUBMIT")
	private WebElement submitButton;

	@AndroidFindBy(accessibility = "No changes to save")
	private WebElement noChangesToSave;

	@AndroidFindBy(accessibility = "Device Settings")
	private WebElement deviceSettingsPage;

	@AndroidFindBy(accessibility = "Scan Now")
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

	public SettingsPageEnglish(AppiumDriver driver) {
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

}
