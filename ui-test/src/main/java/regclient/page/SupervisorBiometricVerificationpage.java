package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class SupervisorBiometricVerificationpage extends BasePage {

	public SupervisorBiometricVerificationpage(AppiumDriver driver) {
		super(driver);
	}

	public abstract boolean isSupervisorBiometricVerificationPageLoaded();

	public abstract void clickOnIrisScan();

	public abstract void clickOnRightHandScanIcon();

	public abstract void clickOnLeftHandScanIcon();

	public abstract void clickOnThumbsScanIcon();

	public abstract void clickOnFaceScanIcon();

	public abstract void clickOnExceptionScanIcon();

	public abstract void clickOnScanButton();

	public abstract void clickOnNextButton();

	public abstract void closeScanCapturePopUp();

	public abstract boolean isRightHandScanTitleDisplayed();

	public abstract boolean isLeftHandScanTitleDisplayed();

	public abstract boolean isThumbsScanTitleDisplayed();

	public abstract boolean isFaceScanTitleDisplayed();

	public abstract boolean isExceptionScanTitleDisplayed();

	public abstract boolean isIrisScan();

	public abstract boolean isRightHandScan();

	public abstract boolean isLeftHandScan();

	public abstract boolean isThumbsScan();

	public abstract boolean isFaceScan();

	public abstract boolean isIrisScanQualityDisplayed();

	public abstract boolean isRightHandScanQualityDisplayed();

	public abstract boolean isLeftHandScanQualityDisplayed();

	public abstract boolean isThumbsScanQualityDisplayed();

	public abstract boolean isFaceScanQualityDisplayed();

	public abstract void clickOnMarkExceptionButton();

	public abstract void markOneEyeException();

	public abstract void markOneFingerException();

	public abstract boolean isExceptionTypeTitleDisplayed();

	public abstract void clickOnExceptionTypeTemporaryButton();

	public abstract boolean isCommentHeaderDisplayed();

	public abstract void enterCommentsInTextBox(String comments);

	public abstract void clickOnIrisScanTitle();

	public abstract void clickOnThumbsScanTitle();

	public abstract boolean isMarkingExceptionsOnFaceIsNotAllowedTextDisplayed();

	public abstract boolean isZoomButtonDisplayed();

	public abstract void clickOnRightHandScanTitle();

	public abstract void clickOnLeftHandScanTitle();

	public abstract void clickOnFaceScanTitle();

	public abstract boolean checkThresholdValueIris();

	public abstract boolean checkThresholdValueRightHand();

	public abstract boolean checkThresholdValueLeftHand();

	public abstract boolean checkThresholdValueThumbs();

	public abstract boolean checkThresholdValueFace();

	public abstract boolean isVerifyAndSaveButtonEnabled();

	public abstract void clickOnVerifyAndSaveButton();

	public abstract boolean isDismissPageLoaded();

	public abstract boolean isOperatorOnboardedPopupLoaded();

	public abstract void clickOnHomeButton();

	public abstract void clickOnBackButton();
}
