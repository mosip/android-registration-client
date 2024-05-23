package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class ConsentPage extends BasePage{

	public ConsentPage(AppiumDriver driver) {
		super(driver);
	}

	public abstract boolean isConsentPageDisplayed();

	
	public abstract boolean isCheckBoxReadable();

	public abstract  void selectTermAndConditionCheckbox();
	
	public abstract  void UnSelectTermAndConditionCheckbox();

	public abstract  boolean isInformedButtonEnabled();

	public abstract  DemographicDetailsPage clickOnInformedButton();
	
	public abstract  RegistrationTasksPage clickOnCancelButton();
}
