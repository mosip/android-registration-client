package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class PreviewPage extends BasePage {

	public PreviewPage(AppiumDriver driver) {
		super(driver);
	}

	public abstract AuthenticationPage clickOnContinueButton();

	public abstract boolean isDemographicInformationInPreviewPageDisplayed();

	public abstract boolean isDocumentsInformationInPreviewPageDisplayed();

	public abstract boolean isBiometricsInformationInPreviewPagePageDisplayed();

	public abstract boolean isNewRegistrationTitleDisplayed();

	public abstract boolean isApplicationIDPreviewPagePageDisplayed();

	public abstract DemographicDetailsPage clickOnDemographicDetailsTitle();

	public abstract boolean isBothIrisImageDisplayed();

	public abstract boolean isSingleIrisImageDisplayed();

	public abstract boolean isFingerExceptionText();

	public abstract String getAID();

	public abstract boolean updateUINTitleDisplayed();

	public abstract void validatePreRegAndApplicationIdMatch(String age);

	public abstract String getEmailId();

	public abstract boolean isLostUinTitleDisplayed();
	
	public abstract boolean isBiometricCorrectionTitleDisplayed();

}
