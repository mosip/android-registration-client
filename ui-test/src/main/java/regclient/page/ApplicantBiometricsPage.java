package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class ApplicantBiometricsPage extends BasePage {

	public ApplicantBiometricsPage(AppiumDriver driver) {
		super(driver);
	}

	public abstract void enterCommentsInTextBox(String comments);

	public abstract void clickOnScanButton();

	public abstract void clickOnExceptionTypePermanentButton();

	public abstract void clickOnExceptionTypeTemporaryButton();

	public abstract void markOneEyeException();

	public abstract void markOneFingureException();

	public abstract void markFourFingureExceptionThenRemoveOne();

	public abstract void clickOnClosePopUp();

	public abstract void clickOnMarkExceptionButton();

	public abstract void clickOnIrisScanButton();

	public abstract BiometricDetailsPage clickOnNextButton();

	public abstract void clickOnZoomButton();

	public abstract void clickOnIrisScanTitle();

	public abstract void clickOnRightHandScanTitle();

	public abstract void clickOnleftHandScanTitle();

	public abstract void clickOnThumbsScanTitle();

	public abstract void closeScanCapturePopUp();

	public abstract BiometricDetailsPage clickOnBackButton();

	public abstract boolean isApplicantBiometricsPageDisplayed();

	public abstract boolean isExceptionTypeTitleDisplayed();

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

	public abstract boolean isExceptionScan();

	public abstract boolean isCommentHeaderDisplayed();

	public abstract boolean isExceptionCountDisplayed();

	public abstract boolean isAuthenticationBiometricsPageDisplayed();
}
