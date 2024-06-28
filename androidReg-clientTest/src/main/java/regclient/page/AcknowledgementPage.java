package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class AcknowledgementPage extends BasePage{

	public AcknowledgementPage(AppiumDriver driver) {
		super(driver);
	}

	public abstract SelectLanguagePage clickOnNewRegistrationButton();

	public abstract boolean isAcknowledgementPageDisplayed();

	public abstract boolean isApplicationIDDisplayed();

	public abstract boolean isQrCodeImageDisplayed();

	public abstract boolean isDemographicInformationInAcknowledgementPageDisplayed();

	public abstract boolean isDocumentsInformationInAcknowledgementPageDisplayed();

	public abstract boolean isBiometricsInformationInAcknowledgementPageDisplayed();

	public abstract DemographicDetailsPage clickOnDemographicDetailsTitle();
	

	
	
	
	
	
	

}
