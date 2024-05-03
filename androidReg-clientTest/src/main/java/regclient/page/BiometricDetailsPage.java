package regclient.page;

import io.appium.java_client.AppiumDriver;



public abstract class BiometricDetailsPage extends BasePage{

	public BiometricDetailsPage(AppiumDriver driver) {
		super(driver);
	}

	public abstract  boolean isBiometricDetailsPageDisplayed();
	
	public abstract ApplicantBiometricsPage clickOnIrisScan();
	
	public abstract IntroducerBiometricPage clickOnIntroducerIrisScan();
	
	public abstract ApplicantBiometricsPage clickOnRightHandScanIcon();
	
	public abstract ApplicantBiometricsPage clickOnLeftHandScanIcon();
	
	public abstract ApplicantBiometricsPage clickOnThumbsScanIcon();
	
	public abstract ApplicantBiometricsPage clickOnFaceScanIcon();
	
	public abstract ApplicantBiometricsPage clickOnExceptionScanIcon();
	
	public abstract  PreviewPage clickOnContinueButton();
}
