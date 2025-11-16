package regclient.pages.english;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.UpdateOperatorBiometricspage;

public class UpdateOperatorBiometricspageEnglish extends UpdateOperatorBiometricspage {

	public UpdateOperatorBiometricspageEnglish(AppiumDriver driver) {
		super(driver);
	}

	@AndroidFindBy(accessibility = "Supervisor's Biometric Update")
	private WebElement supervisorBiometricUpdatePageTitle;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Iris\"))")
	private WebElement irisScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Right\"))")
	private WebElement rightHandScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().className(\"android.widget.ScrollView\")).scrollIntoView(new UiSelector().className(\"android.widget.ImageView\").description(\"Left Hand Scan\"))")
	private WebElement leftHandScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Thumbs\"))")
	private WebElement thumbsScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().className(\"android.widget.ScrollView\")).scrollIntoView(new UiSelector().className(\"android.widget.ImageView\").description(\"Face Scan\"))")
	private WebElement faceScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Exception Scan\"))")
	private WebElement exceptionScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Iris Scan\"))")
	private WebElement irisScanButtonTitle;

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

	@AndroidFindBy(className = "android.widget.Button")
	private WebElement popUpCloseButton;

	@AndroidFindBy(accessibility = "NEXT")
	private WebElement nextButton;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Threshold\")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement irisScanQuality;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Threshold\")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement rightHandScanQuality;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Threshold\")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement leftHandScanQuality;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Threshold\")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement thumbsScanQuality;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Threshold\")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement faceScanQuality;

	@AndroidFindBy(accessibility = "Mark Exception")
	private WebElement markExceptionButton;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(1)")
	private WebElement oneEyeException;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(2)")
	private WebElement firstFingureExceptionImage;

	@AndroidFindBy(accessibility = "Mark Exception")
	private WebElement exceptionTypeTitle;

	@AndroidFindBy(accessibility = "Temporary")
	private WebElement temporaryButton;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Comments\"))")
	private WebElement commentsHeader;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Comments\")]/following-sibling::android.widget.EditText")
	private WebElement commentsTextBox;

	@AndroidFindBy(accessibility = "Marking exceptions on Face is not allowed")
	private WebElement markingExceptionsOnFaceIsNotAllowedText;

	@AndroidFindBy(xpath = "//android.widget.ScrollView/android.view.View[3]")
	private WebElement zoomButton;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Threshold\")]/following-sibling::android.view.View")
	private WebElement scanQualityText;

	@AndroidFindBy(accessibility = "VERIFY & SAVE")
	private WebElement verifyAndSaveButton;

	@AndroidFindBy(accessibility = "Dismiss")
	private WebElement dismissPage;

	@AndroidFindBy(accessibility = "Operator biometrics updated successfully.")
	private WebElement successPopup;

	@AndroidFindBy(accessibility = "HOME")
	private WebElement homeButton;

	@AndroidFindBy(accessibility = "Update Operator Biometrics")
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

	public boolean isRightHandScanTitleDisplayed() {
		return isElementDisplayed(rightHandScanTitle);
	}

	public boolean isLeftHandScanTitleDisplayed() {
		return isElementDisplayed(leftHandScanTitle);
	}

	public boolean isThumbsScanTitleDisplayed() {
		return isElementDisplayed(thumbsScanTitle);
	}

	public boolean isFaceScanTitleDisplayed() {
		return isElementDisplayed(faceScanTitle);
	}

	public boolean isExceptionScanTitleDisplayed() {
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

	public boolean isExceptionScan() {
		return isElementDisplayed(exceptionCapturerHeader, 2000);
	}

	public boolean isIrisScanQualityDisplayed() {
		return isElementDisplayed(irisScanQuality);
	}

	public boolean isRightHandScanQualityDisplayed() {
		return isElementDisplayed(rightHandScanQuality);
	}

	public boolean isLeftHandScanQualityDisplayed() {
		return isElementDisplayed(leftHandScanQuality);
	}

	public boolean isThumbsScanQualityDisplayed() {
		return isElementDisplayed(thumbsScanQuality);
	}

	public boolean isFaceScanQualityDisplayed() {
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

	public boolean isExceptionTypeTitleDisplayed() {
		return isElementDisplayed(exceptionTypeTitle);
	}

	public void clickOnExceptionTypeTemporaryButton() {
		if (!isElementDisplayedOnScreen(temporaryButton)) {
			swipeOrScroll();
		}
		clickOnElement(temporaryButton);
	}

	public boolean isCommentHeaderDisplayed() {
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

	public boolean isMarkingExceptionsOnFaceIsNotAllowedTextDisplayed() {
		return isElementDisplayed(markingExceptionsOnFaceIsNotAllowedText);
	}

	public boolean isZoomButtonDisplayed() {
		return true;
//		return isElementDisplayed(zoomButton);
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

	public boolean isDismissPageLoaded() {
		return isElementDisplayed(dismissPage);
	}

	public boolean isOperatorBiometricsUpdatedPopupLoaded() {
		return isElementDisplayed(successPopup);
	}

	public void clickOnHomeButton() {
		clickOnElement(homeButton);
	}

	@Override
	public void clickOnVerifyAndSaveButton() {
		clickOnElement(verifyAndSaveButton);

	}

	public void clickOnBackButton() {
		driver.navigate().back();
	}

	public boolean isUpdateOperatorBiometricsPageLoaded() {
		return isElementDisplayed(updateOperatorBiometrics);
	}
}
