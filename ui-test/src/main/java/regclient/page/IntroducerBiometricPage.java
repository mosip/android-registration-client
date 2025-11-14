package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class IntroducerBiometricPage extends BasePage {

	public IntroducerBiometricPage(AppiumDriver driver) {
		super(driver);
	}

	public abstract void clickOnScanButton();

	public abstract void markOneEyeException();

	public abstract void markOneFingureException();

	public abstract void clickOnClosePopUp();

	public abstract void clickOnMarkExceptionButton();

	public abstract void clickOnIrisScanButton();

	public abstract BiometricDetailsPage clickOnNextButton();

	public abstract void clickOnZoomButton();

	public abstract void clickOnRightHandScanTitle();

	public abstract void clickOnleftHandScanTitle();

	public abstract void clickOnThumbsScanTitle();

	public abstract void closeScanCapturePopUp();

	public abstract BiometricDetailsPage clickOnBackButton();

	public abstract boolean isIntroducerBiometricsPageDisplyed();

	public abstract boolean isExceptionTypeTitleDisplyed();

	public abstract boolean isRightHandScanTitleDisplyed();

	public abstract boolean isLeftHandScanTitleDisplyed();

	public abstract boolean isThumbsScanTitleDisplyed();

	public abstract boolean isFaceScanTitleDisplyed();

	public abstract boolean isExceptionScanTitleDisplyed();

	public abstract boolean isIrisScan();

	public abstract boolean isRightHandScan();

	public abstract boolean isLeftHandScan();

	public abstract boolean isThumbsScan();

	public abstract boolean isFaceScan();

	public abstract boolean isExceptionScan();
	
	public abstract boolean isNextButtonDisplyed();

}
