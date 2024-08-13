package regclient.pages.arabic;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.api.FetchUiSpec;
import regclient.page.BiometricDetailsPage;
import regclient.page.IntroducerBiometricPage;
import regclient.pages.english.BiometricDetailsPageEnglish;

public class IntroducerBiometricPageArabic extends IntroducerBiometricPage {
	
	@AndroidFindBy(accessibility = "القزحية مسح")
	private WebElement irisScanButton;;
	
	@AndroidFindBy(accessibility = "وضع علامة استثناء")
	private WebElement markExceptionButton;
	
	@AndroidFindBy(accessibility = "وضع علامة استثناء")
	private WebElement exceptionTypeTitle;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(1)")
	private WebElement oneEyeException;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(2)")
	private WebElement firstFingureException;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"اليد اليمنى مسح\"))")
	private WebElement rightHandScanTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"اليد اليسرى مسح\"))")
	private WebElement leftHandScanTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"الأبهام مسح\"))")
	private WebElement thumbsScanTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"الوجه مسح\"))")
	private WebElement faceScanTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"استثناء مسح\"))")
	private WebElement exceptionScanTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"مسح\"))")
	private WebElement scanButton;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Iris')]")
	private WebElement irisCapturerHeader;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'RightHand')]")
	private WebElement rightHandCapturerHeader;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'LeftHand')]")
	private WebElement leftHandCapturerHeader;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Thumbs')]")
	private WebElement thumbsCapturerHeader;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Face')]")
	private WebElement faceCapturerHeader;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Exception')]")
	private WebElement exceptionCapturerHeader;
	
	@AndroidFindBy(xpath = "//android.widget.ScrollView/android.view.View[3]")
	private WebElement zoomButton;
	
	@AndroidFindBy(className = "android.widget.Button")
	private WebElement popUpCloseButton;
	
	@AndroidFindBy(accessibility = "مقبل")
	private WebElement nextButton;

	public IntroducerBiometricPageArabic(AppiumDriver driver) {
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
		return new BiometricDetailsPageEnglish(driver);
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
		return new BiometricDetailsPageEnglish(driver);
	}
	
	@SuppressWarnings("deprecation")
	public  boolean isIntroducerBiometricsPageDisplyed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId("introducerBiometrics")+"\")")));
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
