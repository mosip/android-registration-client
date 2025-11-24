package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class BiometricDetailsPage extends BasePage {

	public BiometricDetailsPage(AppiumDriver driver) {
		super(driver);
	}

	public abstract boolean isBiometricDetailsPageDisplayed();

	public abstract ApplicantBiometricsPage clickOnIrisScan();

	public abstract IntroducerBiometricPage clickOnIntroducerIrisScan();

	public abstract ApplicantBiometricsPage clickOnRightHandScanIcon();

	public abstract ApplicantBiometricsPage clickOnLeftHandScanIcon();

	public abstract ApplicantBiometricsPage clickOnThumbsScanIcon();

	public abstract ApplicantBiometricsPage clickOnFaceScanIcon();

	public abstract ApplicantBiometricsPage clickOnExceptionScanIcon();

	public abstract PreviewPage clickOnContinueButton();

	public abstract boolean isAuthenticationBiometricTitleDisplayed();

	public abstract IntroducerBiometricPage clickOnIntroducerRightHandScan();

	public abstract IntroducerBiometricPage clickOnIntroducerLeftHandScan();

	public abstract IntroducerBiometricPage clickOnIntroducerThumbScan();

	public abstract IntroducerBiometricPage clickOnIntroducerFaceScan();

	public abstract boolean isAdditionalInfoRequestIdTextboxDisplayed();

	public abstract void enterAdditionalInfoUsingEmail(String emailId);
}
