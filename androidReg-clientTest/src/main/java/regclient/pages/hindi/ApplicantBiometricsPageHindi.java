package regclient.pages.hindi;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.ApplicantBiometricsPage;
import regclient.page.BiometricDetailsPage;

public class ApplicantBiometricsPageHindi extends ApplicantBiometricsPage{
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'आवेदक बायोमेट्रिक्स')]")
	private WebElement applicantBiometricsPageTitle;
	
	@AndroidFindBy(accessibility = "आईरिस स्कैन")
	private WebElement irisScanButton;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"आईरिस स्कैन\"))")
	private WebElement irisScanButtonTitle;
	
	@AndroidFindBy(accessibility = "स्थायी")
	private WebElement permanentButton;
	
	@AndroidFindBy(accessibility = "अस्थायी")
	private WebElement temporaryButton;
	
	@AndroidFindBy(accessibility = "अस्तित्व चिह्नित करें")
	private WebElement markExceptionButton;
	
	@AndroidFindBy(accessibility = "अस्तित्व चिह्नित करें")
	private WebElement exceptionTypeTitle;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(1)")
	private WebElement oneEyeException;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(2)")
	private WebElement firstFingureExceptionImage;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(1)")
	private WebElement secondFingureExceptionImage;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(3)")
	private WebElement thirdFingureExceptionImage;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(4)")
	private WebElement forthFingureExceptionImage;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"दाएं हाथ\"))")
	private WebElement rightHandScanTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"बाएं हाथ\"))")
	private WebElement leftHandScanTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"अंगूठे स्कैन\"))")
	private WebElement thumbsScanTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"चेहरा स्कैन\"))")
	private WebElement faceScanTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"अपवाद स्कैन\"))")
	private WebElement exceptionScanTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"स्कैन\"))")
	private WebElement scanButton;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Iris कब्जा')]")
	private WebElement irisCapturerHeader;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'RightHand कब्जा')]")
	private WebElement rightHandCapturerHeader;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'LeftHand कब्जा')]")
	private WebElement leftHandCapturerHeader;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Thumbs कब्जा')]")
	private WebElement thumbsCapturerHeader;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Face कब्जा')]")
	private WebElement faceCapturerHeader;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Exception कब्जा')]")
	private WebElement exceptionCapturerHeader;
	
	@AndroidFindBy(xpath = "//android.widget.ScrollView/android.view.View[3]")
	private WebElement zoomButton;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"टिप्पणियाँ\")]/following-sibling::android.widget.EditText")
	private WebElement commentsTextBox;
	
	@AndroidFindBy(className = "android.widget.Button")
	private WebElement popUpCloseButton;
	
	@AndroidFindBy(accessibility = "अगला")
	private WebElement nextButton;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"टिप्पणियाँ\"))")
	private WebElement commentsHeader;

	@AndroidFindBy(xpath = "//android.view.View[@content-desc=\"अस्तित्वाएं\"]/following-sibling::android.view.View[@content-desc=\"1\"]")
	private WebElement exceptionCount;
	
	public ApplicantBiometricsPageHindi(AppiumDriver driver) {
		super(driver);
	}
	
	public  void enterCommentsInTextBox(String comments) {
		if(!isElementDisplayedOnScreen(commentsTextBox)) {
			swipeOrScroll();
		}
		clickAndsendKeysToTextBox(commentsTextBox,comments);
	}

	public void clickOnScanButton() {
		clickOnElement(scanButton);
	}	
	
	public void clickOnExceptionTypePermanentButton() {
		if(!isElementDisplayedOnScreen(permanentButton)) {
			swipeOrScroll();
		}
		clickOnElement(permanentButton);
	}
	
	public void clickOnExceptionTypeTemporaryButton() {
		if(!isElementDisplayedOnScreen(temporaryButton)) {
			swipeOrScroll();
		}
		clickOnElement(temporaryButton);	
	}
	
	public void markOneEyeException() {
		clickOnElement(oneEyeException);
	}
	
	public void markOneFingureException() {
		clickOnElement(firstFingureExceptionImage);
	}
	
	public void markFourFingureExceptionThenRemoveOne() {
		clickOnElement(firstFingureExceptionImage);
		clickOnElement(secondFingureExceptionImage);
		clickOnElement(thirdFingureExceptionImage);
		clickOnElement(forthFingureExceptionImage);
		clickOnElement(firstFingureExceptionImage);
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
		return new BiometricDetailsPageHindi(driver);
	}
	
	public void clickOnZoomButton() {
		clickOnElement(zoomButton);
	}
	
	public void clickOnIrisScanTitle() {
		clickOnElement(irisScanButtonTitle);
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
		return new BiometricDetailsPageHindi(driver);
	}
	
	public  boolean isApplicantBiometricsPageDisplyed() {
		return isElementDisplayed(applicantBiometricsPageTitle);
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
	
	public  boolean isCommentHeaderDisplyed() {
		return isElementDisplayed(commentsHeader);
	}

	public  boolean isExceptionCountDisplyed() {
		if(!isElementDisplayedOnScreen(exceptionCount)) {
			swipeOrScroll();
			isElementDisplayed(exceptionCount);
		}
		return isElementDisplayed(exceptionCount);
	}

	@Override
	public boolean isScanButton() {
		// TODO Auto-generated method stub
		return false;
	}
}
