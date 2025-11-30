package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class AutoLogoutPage extends BasePage{

	public AutoLogoutPage(AppiumDriver driver) {
		super(driver);
	}

	public abstract boolean isAutoLogoutPopupDisplayed();
	
	public abstract RegistrationTasksPage clickOnStayLoggedInButton();
	
	public abstract LoginPage clickOnStaylogoutButton();
}
