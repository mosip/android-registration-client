package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class AuthenticationPage extends BasePage{

	public AuthenticationPage(AppiumDriver driver) {
		super(driver);
	}
	
	public abstract void enterUserName(String username);

	public abstract void enterPassword(String password);

	public abstract boolean isAuthenticationPageDisplayed();

	public abstract AcknowledgementPage clickOnAuthenticatenButton();


}
