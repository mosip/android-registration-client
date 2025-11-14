package regclient.pages.tamil;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.UpdateOperatorBiometricspage;

public class UpdateOperatorBiometricspageTamil extends UpdateOperatorBiometricspage {

	public UpdateOperatorBiometricspageTamil(AppiumDriver driver) {
		super(driver);

	}

	@AndroidFindBy(accessibility = "மேற்பார்வையாளர் பயோமேட்ரிக் புதுப்பிப்பு")
	private WebElement supervisorBiometricUpdatePageTitle;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"ஐரிஸ் ஊடுகதிர்\"))")
	private WebElement irisScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"வலது கை ஊடுகதிர்\"))")
	private WebElement rightHandScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"இடது கை ஊடுகதிர்\"))")
	private WebElement leftHandScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"முழுகுமதி ஊடுகதிர்\"))")
	private WebElement thumbsScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"முகம் ஊடுகதிர்\"))")
	private WebElement faceScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"விதிவிலக்கு ஊடுகதிர்\"))")
	private WebElement exceptionScanIcon;

	@AndroidFindBy(accessibility = "ஐரிஸ் ஊடுகதிர்")
	private WebElement irisScanButton;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"ஐரிஸ் ஊடுகதிர்\"))")
	private WebElement irisScanButtonTitle;

	@AndroidFindBy(accessibility = "நிரந்தர")
	private WebElement permanentButton;

	@AndroidFindBy(accessibility = "குறிப்பிடுக")
	private WebElement temporaryButton;

	@AndroidFindBy(accessibility = "விசாரிக்கப்பட்ட விதிவிலக்கு")
	private WebElement markExceptionButton;

	@AndroidFindBy(accessibility = "விசாரிக்கப்பட்ட விதிவிலக்கு")
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

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"வலது கை ஊடுகதிர்\"))")
	private WebElement rightHandScanTitle;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"இடது கை ஊடுகதிர்\"))")
	private WebElement leftHandScanTitle;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"முழுகுமதி ஊடுகதிர்\"))")
	private WebElement thumbsScanTitle;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"முகம் ஊடுகதிர்\"))")
	private WebElement faceScanTitle;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"விதிவிலக்கு ஊடுகதிர்\"))")
	private WebElement exceptionScanTitle;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"ஊடுகதிர்\"))")
	private WebElement scanButton;

	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Iris பிடிப்பு')]")
	private WebElement irisCapturerHeader;

	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'RightHand பிடிப்பு')]")
	private WebElement rightHandCapturerHeader;

	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'LeftHand பிடிப்பு')]")
	private WebElement leftHandCapturerHeader;

	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Thumbs பிடிப்பு')]")
	private WebElement thumbsCapturerHeader;

	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Face பிடிப்பு')]")
	private WebElement faceCapturerHeader;

	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Exception பிடிப்பு')]")
	private WebElement exceptionCapturerHeader;

	@AndroidFindBy(xpath = "//android.widget.ScrollView/android.view.View[3]")
	private WebElement zoomButton;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"கருத்துகள்\")]/following-sibling::android.widget.EditText")
	private WebElement commentsTextBox;

	@AndroidFindBy(className = "android.widget.Button")
	private WebElement popUpCloseButton;

	@AndroidFindBy(accessibility = "அடுத்தவர்")
	private WebElement nextButton;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"கருத்துகள்\"))")
	private WebElement commentsHeader;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"அளவுக்கு \")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement irisScanQuality;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"அளவுக்கு \")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement rightHandScanQuality;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"அளவுக்கு \")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement leftHandScanQuality;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"அளவுக்கு \")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement thumbsScanQuality;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"அளவுக்கு \")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement faceScanQuality;

	@AndroidFindBy(accessibility = "விதிவிலக்குகளைக் குறிப்பது இயக்கத்தில் உள்ளது முகம் அனுமதிக்கப்படவில்லை")
	private WebElement markingExceptionsOnFaceIsNotAllowedText;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"அளவுக்கு \")]/following-sibling::android.view.View")
	private WebElement scanQualityText;

	@AndroidFindBy(accessibility = "சரிபார்த்து சேமிக்கவும்")
	private WebElement verifyAndSaveButton;

	@AndroidFindBy(accessibility = "நிராகரிக்கும்")
	private WebElement dismissPage;

	@AndroidFindBy(accessibility = "ஆபரேட்டர் பயோமெட்ரிக்ஸ் வெற்றிகரமாக புதுப்பிக்கப்பட்டது.")
	private WebElement successPopup;

	@AndroidFindBy(accessibility = "வீடு")
	private WebElement homeButton;

	@AndroidFindBy(accessibility = "ஆபரேட்டர் பயோமேட்ரிக்ஸ் புதுப்பிக்க")
	private WebElement updateOperatorBiometrics;

	public void clickOnIrisScan() {
		clickOnElement(irisScanIcon);
	}

	public boolean isSupervisorBiometricUpdatePageLoaded() {
		return isElementDisplayed(supervisorBiometricUpdatePageTitle);
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

	public boolean isRightHandScanTitleDisplyed() {
		return isElementDisplayed(rightHandScanTitle);
	}

	public boolean isLeftHandScanTitleDisplyed() {
		return isElementDisplayed(leftHandScanTitle);
	}

	public boolean isThumbsScanTitleDisplyed() {
		return isElementDisplayed(thumbsScanTitle);
	}

	public boolean isFaceScanTitleDisplyed() {
		return isElementDisplayed(faceScanTitle);
	}

	public boolean isExceptionScanTitleDisplyed() {
		return isElementDisplayed(exceptionScanTitle);
	}

	public boolean isIrisScan() {
		return isElementDisplayed(irisCapturerHeader, 2000);
	}

	public boolean isRightHandScan() {
		return isElementDisplayed(rightHandCapturerHeader, 2000);
	}

	public boolean isLeftHandScan() {
		return isElementDisplayed(leftHandCapturerHeader, 2000);
	}

	public boolean isThumbsScan() {
		return isElementDisplayed(thumbsCapturerHeader, 2000);
	}

	public boolean isFaceScan() {
		return isElementDisplayed(faceCapturerHeader, 2000);
	}

	public boolean isIrisScanQualityDisplyed() {
		return isElementDisplayed(irisScanQuality);
	}

	public boolean isRightHandScanQualityDisplyed() {
		return isElementDisplayed(rightHandScanQuality);
	}

	public boolean isLeftHandScanQualityDisplyed() {
		return isElementDisplayed(leftHandScanQuality);
	}

	public boolean isThumbsScanQualityDisplyed() {
		return isElementDisplayed(thumbsScanQuality);
	}

	public boolean isFaceScanQualityDisplyed() {
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

	public boolean isExceptionTypeTitleDisplyed() {
		return isElementDisplayed(exceptionTypeTitle);
	}

	public void clickOnExceptionTypeTemporaryButton() {
		if (!isElementDisplayedOnScreen(temporaryButton)) {
			swipeOrScroll();
		}
		clickOnElement(temporaryButton);
	}

	public boolean isCommentHeaderDisplyed() {
		return isElementDisplayed(commentsHeader);
	}

	public void enterCommentsInTextBox(String comments) {
		if (!isElementDisplayedOnScreen(commentsTextBox)) {
			swipeOrScroll();
		}
		clickAndsendKeysToTextBox(commentsTextBox, comments);
	}

	public void clickOnIrisScanTitle() {
		clickOnElement(irisScanButtonTitle);
	}

	public void clickOnThumbsScanTitle() {
		clickOnElement(thumbsScanTitle);
	}

	public boolean isMarkingExceptionsOnFaceIsNotAllowedTextDisplyed() {
		return isElementDisplayed(markingExceptionsOnFaceIsNotAllowedText);
	}

	public boolean isZoomButtonDisplyed() {
		return isElementDisplayed(zoomButton);
	}

	public void clickOnRightHandScanTitle() {
		clickOnElement(rightHandScanTitle);
	}

	public void clickOnLeftHandScanTitle() {
		clickOnElement(leftHandScanTitle);
	}

	public void clickOnFaceScanTitle() {
		clickOnElement(faceScanTitle);
	}

	public boolean checkThresholdValueIris() {
		int requiredValue = 60;
		String scanValue = scanQualityText.getAttribute("contentDescription");
		int value = Integer.valueOf(scanValue.replace("%", ""));
		if (value >= requiredValue)
			return true;
		else
			return false;
	}

	public boolean checkThresholdValueRightHand() {
		int requiredValue = 40;
		String scanValue = scanQualityText.getAttribute("contentDescription");
		int value = Integer.valueOf(scanValue.replace("%", ""));
		if (value >= requiredValue)
			return true;
		else
			return false;
	}

	public boolean checkThresholdValueLeftHand() {
		int requiredValue = 60;
		String scanValue = scanQualityText.getAttribute("contentDescription");
		int value = Integer.valueOf(scanValue.replace("%", ""));
		if (value >= requiredValue)
			return true;
		else
			return false;
	}

	public boolean checkThresholdValueThumbs() {
		int requiredValue = 40;
		String scanValue = scanQualityText.getAttribute("contentDescription");
		int value = Integer.valueOf(scanValue.replace("%", ""));
		if (value >= requiredValue)
			return true;
		else
			return false;
	}

	public boolean checkThresholdValueFace() {
		int requiredValue = 90;
		String scanValue = scanQualityText.getAttribute("contentDescription");
		int value = Integer.valueOf(scanValue.replace("%", ""));
		if (value >= requiredValue)
			return true;
		else
			return false;
	}

	public boolean isVerifyAndSaveButtonEnabled() {
		return isElementDisplayed(verifyAndSaveButton);
	}

	public void clickOnVerifyAndSaveButton() {
		clickOnElement(verifyAndSaveButton);

	}

	public boolean isDismissPageLoaded() {
		return isElementDisplayed(dismissPage);
	}

	public boolean isOperatorBiometricsUpdatedPopupLoaded() {
		return isElementDisplayed(successPopup);
	}

	public void clickOnHomeButton() {
		clickOnElement(homeButton);
	}

	public void clickOnBackButton() {
		driver.navigate().back();
	}

	public boolean isUpdateOperatorBiometricsPageLoaded() {
		return isElementDisplayed(updateOperatorBiometrics);
	}
}
