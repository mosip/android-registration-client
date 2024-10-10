package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class UpdateUINPage extends BasePage{

	public UpdateUINPage(AppiumDriver driver) {
		super(driver);
	}

	public abstract boolean isUpdateMyUINTitleDisplayed();

	public abstract void enterUIN(String UIN);

	public abstract ConsentPage clickOnContinueButton();

	public abstract boolean isInvalidUINErrorMessageDisplayed();

	public abstract void selectUpdateValue(String page);
	
	public abstract void selectUpdateIntroducerDetails();


}
