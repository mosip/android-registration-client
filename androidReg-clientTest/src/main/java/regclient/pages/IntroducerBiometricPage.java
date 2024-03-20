package regclient.pages;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;

public class IntroducerBiometricPage extends BasePage {
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Introducer Biometrics')]")
	private WebElement introducerBiometricPageTitle;
	
	@AndroidFindBy(accessibility = "Iris Scan")
	private WebElement irisScanButton;
	
	@AndroidFindBy(accessibility = "Mark Exception")
	private WebElement markExceptionButton;
	
	@AndroidFindBy(accessibility = "Mark Exception")
	private WebElement exceptionTypeTitle;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(1)")
	private WebElement oneEyeException;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(2)")
	private WebElement firstFingureException;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Right Hand\"))")
	private WebElement rightHandScanTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Left Hand\"))")
	private WebElement leftHandScanTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Thumbs Scan\"))")
	private WebElement thumbsScanTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Face Scan\"))")
	private WebElement faceScanTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Exception Scan\"))")
	private WebElement exceptionScanTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"Scan\"))")
	private WebElement scanButton;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Iris Capture')]")
	private WebElement irisCapturerHeader;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'RightHand Capture')]")
	private WebElement rightHandCapturerHeader;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'LeftHand Capture')]")
	private WebElement leftHandCapturerHeader;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Thumbs Capture')]")
	private WebElement thumbsCapturerHeader;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Face Capture')]")
	private WebElement faceCapturerHeader;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Exception Capture')]")
	private WebElement exceptionCapturerHeader;
	
	@AndroidFindBy(xpath = "//android.widget.ScrollView/android.view.View[3]")
	private WebElement zoomButton;
	
	@AndroidFindBy(className = "android.widget.Button")
	private WebElement popUpCloseButton;
	
	@AndroidFindBy(accessibility = "NEXT")
	private WebElement nextButton;

	public IntroducerBiometricPage(AppiumDriver driver) {
		super(driver);
	}

	public void clickOnScanButton() {
		clickOnElement(scanButton);
	}	
	
	public void markOneEyeException() {
		clickOnElement(oneEyeException);
	}
	
	public void markOneFingureException() {
		clickOnElement(firstFingureException);
	}
	
	public void clickOnClosePopUp() {
		clickOnElement(popUpCloseButton);
	}
	
	public void clickOnMarkExceptionButton() {
		clickOnElement(markExceptionButton);
	}
	
	public void clickOnIrisScanButton() {
		clickOnElement(irisScanButton);
	}
	
	public BiometricDetailsPage clickOnNextButton() {
		clickOnElement(nextButton);
		return new BiometricDetailsPage(driver);
	}
	
	public void clickOnZoomButton() {
		clickOnElement(zoomButton);
	}
	
	public void clickOnRightHandScanTitle() {
		clickOnElement(rightHandScanTitle);
	}
	
	public void clickOnleftHandScanTitle() {
		clickOnElement(leftHandScanTitle);
	}
	
	public void clickOnThumbsScanTitle() {
		clickOnElement(thumbsScanTitle);
	}
	
	public void closeScanCapturePopUp() {
		driver.navigate().back();
	}
	
	public BiometricDetailsPage clickOnBackButton() {
		driver.navigate().back();
		return new BiometricDetailsPage(driver);
	}
	
	public  boolean isIntroducerBiometricsPageDisplyed() {
		return isElementDisplayed(introducerBiometricPageTitle);
	}
	
	public  boolean isExceptionTypeTitleDisplyed() {
		return isElementDisplayed(exceptionTypeTitle);
	}
	
	public  boolean isRightHandScanTitleDisplyed() {
		return isElementDisplayed(rightHandScanTitle);
	}
	
	public  boolean isLeftHandScanTitleDisplyed() {
		return isElementDisplayed(leftHandScanTitle);
	}
	
	public  boolean isThumbsScanTitleDisplyed() {
		return isElementDisplayed(thumbsScanTitle);
	}
	
	public  boolean isFaceScanTitleDisplyed() {
		return isElementDisplayed(faceScanTitle);
	}
	
	public  boolean isExceptionScanTitleDisplyed() {
		return isElementDisplayed(exceptionScanTitle);
	}
	
	public  boolean isIrisScan() {
		return isElementDisplayed(irisCapturerHeader,2000);
	}
	
	public  boolean isRightHandScan() {
		return isElementDisplayed(rightHandCapturerHeader,2000);
	}
	
	public  boolean isLeftHandScan() {
		return isElementDisplayed(leftHandCapturerHeader,2000);
	}
	
	public  boolean isThumbsScan() {
		return isElementDisplayed(thumbsCapturerHeader,2000);
	}
	
	public  boolean isFaceScan() {
		return isElementDisplayed(faceCapturerHeader,2000);
	}
	
	public  boolean isExceptionScan() {
		return isElementDisplayed(exceptionCapturerHeader,2000);
	}
}
