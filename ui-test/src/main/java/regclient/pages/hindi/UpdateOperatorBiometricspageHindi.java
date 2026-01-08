package regclient.pages.hindi;

import java.time.Duration;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.UpdateOperatorBiometricspage;

public class UpdateOperatorBiometricspageHindi extends UpdateOperatorBiometricspage {

	public UpdateOperatorBiometricspageHindi(AppiumDriver driver) {
		super(driver);
	}

	@AndroidFindBy(accessibility = "पर्यवेक्षक का बायोमेट्रिक अपडेट")
	private WebElement supervisorBiometricUpdatePageTitle;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"आईरिस स्कैन\"))")
	private WebElement irisScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"दाएं हाथ स्कैन\"))")
	private WebElement rightHandScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"बाएं हाथ स्कैन\"))")
	private WebElement leftHandScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"अंगूठे स्कैन\"))")
	private WebElement thumbsScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"चेहरा स्कैन\"))")
	private WebElement faceScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"अपवाद स्कैन\"))")
	private WebElement exceptionScanIcon;

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
	private WebElement firstFingerExceptionImage;

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

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"सीमा \")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement irisScanQuality;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"सीमा \")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement rightHandScanQuality;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"सीमा \")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement leftHandScanQuality;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"सीमा \")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement thumbsScanQuality;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"सीमा \")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement faceScanQuality;

	@AndroidFindBy(accessibility = "अपवादों को चिह्नित करना चेहरा अनुमति नहीं है")
	private WebElement markingExceptionsOnFaceIsNotAllowedText;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"सीमा \")]/following-sibling::android.view.View")
	private WebElement scanQualityText;

	@AndroidFindBy(accessibility = "सत्यापित करें और सहेजें")
	private WebElement verifyAndSaveButton;

	@AndroidFindBy(accessibility = "खारिज करें")
	private WebElement dismissPage;

	@AndroidFindBy(accessibility = "ऑपरेटर बायोमेट्रिक्स सफलतापूर्वक अपडेट किया गया।")
	private WebElement successPopup;

	@AndroidFindBy(accessibility = "घर")
	private WebElement homeButton;

	@AndroidFindBy(accessibility = "ऑपरेटर बायोमेट्रिक अपडेट करें")
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

	public void markOneFingerException() {
		clickOnElement(firstFingerExceptionImage);
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

	public boolean validateThreshold(int expected) {
	    WebElement el = driver.findElement(MobileBy.AndroidUIAutomator(
	            "new UiScrollable(new UiSelector().scrollable(true))" +
	            ".scrollIntoView(new UiSelector().descriptionContains(\"Threshold\"));"));
	    String text = el.getAttribute("content-desc");       // "Threshold 75%"
	    int actual = Integer.parseInt(text.replaceAll("[^0-9]", "")); // extract 75

	    return actual == expected;
	}
	
	public void updateBiometricsAndWaitPopup() {
	    for (int i = 1; i <= 5; i++) {
	        clickOnVerifyAndSaveButton();
	        try {
	            new WebDriverWait(driver, Duration.ofSeconds(60))
	                    .until(ExpectedConditions.visibilityOf(successPopup));
	            return;   // success
	        } catch (Exception ignored) {}
	        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
	    }
	    throw new AssertionError("Biometrics update success popup not displayed after 5 retries.");
	}
}
