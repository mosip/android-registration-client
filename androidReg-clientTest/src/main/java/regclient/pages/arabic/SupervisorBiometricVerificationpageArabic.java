package regclient.pages.arabic;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.SupervisorBiometricVerificationpage;

public class SupervisorBiometricVerificationpageArabic extends SupervisorBiometricVerificationpage{

	@AndroidFindBy(accessibility = "Supervisor's Biometric Verification")
	private WebElement supervisorBiometricVerificationPageTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"القزحية مسح\"))")
	private WebElement irisScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"اليد اليمنى مسح\"))")
	private WebElement rightHandScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"اليد اليسرى مسح\"))")
	private WebElement leftHandScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"الأبهام مسح\"))")
	private WebElement thumbsScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"الوجه مسح\"))")
	private WebElement faceScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"استثناء مسح\"))")
	private WebElement exceptionScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"القزحية مسح\"))")
	private WebElement irisScanButtonTitle;
	
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
	
	@AndroidFindBy(className = "android.widget.Button")
	private WebElement popUpCloseButton;
	
	@AndroidFindBy(accessibility = "مقبل")
	private WebElement nextButton;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"الحد \")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement irisScanQuality;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"الحد \")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement rightHandScanQuality;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"الحد \")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement leftHandScanQuality;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"الحد \")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement thumbsScanQuality;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"الحد \")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement faceScanQuality;
	
	@AndroidFindBy(accessibility = "وضع علامة استثناء")
	private WebElement markExceptionButton;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(1)")
	private WebElement oneEyeException;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(2)")
	private WebElement firstFingureExceptionImage;
	
	@AndroidFindBy(accessibility = "وضع علامة استثناء")
	private WebElement exceptionTypeTitle;
	
	@AndroidFindBy(accessibility = "دائم")
	private WebElement permanentButton;
	
	@AndroidFindBy(accessibility = "مؤقت")
	private WebElement temporaryButton;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"التعليقات\"))")
	private WebElement commentsHeader;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"التعليقات\")]/following-sibling::android.widget.EditText")
	private WebElement commentsTextBox;
	
	@AndroidFindBy(accessibility = "وضع علامة على الاستثناءات على الوجه غير مسموح")
	private WebElement markingExceptionsOnFaceIsNotAllowedText;
	
	@AndroidFindBy(xpath = "//android.widget.ScrollView/android.view.View[3]")
	private WebElement zoomButton;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"الحد \")]/following-sibling::android.view.View")
	private WebElement scanQualityText;
	
	@AndroidFindBy(accessibility = "VERIFY & SAVE")
	private WebElement verifyAndSaveButton;
	
	public SupervisorBiometricVerificationpageArabic(AppiumDriver driver) {
		super(driver);
	}

	public void clickOnIrisScan() {
		clickOnElement(irisScanIcon);
	}
	
	public boolean isSupervisorBiometricVerificationPageLoaded() {
		return isElementDisplayed(supervisorBiometricVerificationPageTitle);
	}
	
	public void clickOnRightHandScanIcon() {
		clickOnElement(rightHandScanIcon);
	}
	
	public void clickOnLeftHandScanIcon() {
		clickOnElement(leftHandScanIcon);
	}
	
	public void clickOnThumbsScanIcon() {
		clickOnElement(thumbsScanIcon);
	}
	
	public void clickOnFaceScanIcon() {
		clickOnElement(faceScanIcon);
	}
	
	public void clickOnExceptionScanIcon() {
		clickOnElement(exceptionScanIcon);
	}
	
	public void clickOnScanButton() {
		clickOnElement(scanButton);
	}	
	
	public void clickOnNextButton() {
		clickOnElement(nextButton);
	}
	
	public void closeScanCapturePopUp() {
		driver.navigate().back();
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
	
	public  boolean isIrisScanQualityDisplyed() {
		return isElementDisplayed(irisScanQuality);
	}
	
	public  boolean isRightHandScanQualityDisplyed() {
		return isElementDisplayed(rightHandScanQuality);
	}
	
	public  boolean isLeftHandScanQualityDisplyed() {
		return isElementDisplayed(leftHandScanQuality);
	}
	
	public  boolean isThumbsScanQualityDisplyed() {
		return isElementDisplayed(thumbsScanQuality);
	}
	
	public  boolean isFaceScanQualityDisplyed() {
		return isElementDisplayed(faceScanQuality);
	}
	
	public void clickOnMarkExceptionButton() {
		clickOnElement(markExceptionButton);
	}
	
	public void markOneEyeException() {
		clickOnElement(oneEyeException);
	}
	
	public void markOneFingureException() {
		clickOnElement(firstFingureExceptionImage);
	}
	
	public  boolean isExceptionTypeTitleDisplyed() {
		return isElementDisplayed(exceptionTypeTitle);
	}
	
	public void clickOnExceptionTypeTemporaryButton() {
		if(!isElementDisplayedOnScreen(temporaryButton)) {
			swipeOrScroll();
		}
		clickOnElement(temporaryButton);	
	}
	
	public  boolean isCommentHeaderDisplyed() {
		return isElementDisplayed(commentsHeader);
	}
	
	public  void enterCommentsInTextBox(String comments) {
		if(!isElementDisplayedOnScreen(commentsTextBox)) {
			swipeOrScroll();
		}
		clickAndsendKeysToTextBox(commentsTextBox,comments);
	}
	
	public void clickOnIrisScanTitle() {
		clickOnElement(irisScanButtonTitle);
	}
	
	public void clickOnThumbsScanTitle() {
		clickOnElement(thumbsScanTitle);
	}
	
	public  boolean isMarkingExceptionsOnFaceIsNotAllowedTextDisplyed() {
		return isElementDisplayed(markingExceptionsOnFaceIsNotAllowedText);
	}
	
	public  boolean isZoomButtonDisplyed() {
		return isElementDisplayed(zoomButton);
	}
	
	public void clickOnRightHandScanTitle() {
		clickOnElement(rightHandScanTitle);
	}
	
	public void clickOnleftHandScanTitle() {
		clickOnElement(leftHandScanTitle);
	}
	
	public void clickOnFaceScanTitle() {
		clickOnElement(faceScanTitle);
	}
	public boolean checkThresholdValueIris() {
		int requiredValue=60;
		String scanValue =scanQualityText.getAttribute("contentDescription");
		int value=Integer.valueOf(scanValue.replace("%", ""));
		if(value>=requiredValue) 
			return true;
		else
			return false;
	}
	
	public boolean checkThresholdValueRightHand() {
		int requiredValue=40;
		String scanValue =scanQualityText.getAttribute("contentDescription");
		int value=Integer.valueOf(scanValue.replace("%", ""));
		if(value>=requiredValue) 
			return true;
		else
			return false;
	}
	
	public boolean checkThresholdValueLeftHand() {
		int requiredValue=60;
		String scanValue =scanQualityText.getAttribute("contentDescription");
		int value=Integer.valueOf(scanValue.replace("%", ""));
		if(value>=requiredValue) 
			return true;
		else
			return false;
	}
	
	public boolean checkThresholdValueThumbs() {
		int requiredValue=40;
		String scanValue =scanQualityText.getAttribute("contentDescription");
		int value=Integer.valueOf(scanValue.replace("%", ""));
		if(value>=requiredValue) 
			return true;
		else
			return false;
	}
	
	public boolean checkThresholdValueFace() {
		int requiredValue=90;
		String scanValue =scanQualityText.getAttribute("contentDescription");
		int value=Integer.valueOf(scanValue.replace("%", ""));
		if(value>=requiredValue) 
			return true;
		else
			return false;
	}
	
	public boolean clickOnVerifyAndSaveButton() {
		return isElementDisplayed(verifyAndSaveButton);
	}
}
