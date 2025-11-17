package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class ProfilePage extends BasePage {

	public ProfilePage(AppiumDriver driver) {
		super(driver);
	}

	public abstract LoginPage clickOnLogoutButton();

	public abstract boolean isProfileTitleDisplayed();

	public abstract boolean isLogoutPopUpMessageDisplayed();

	public abstract boolean isResetPasswordButtonDisplayed();

	public abstract LoginPage clickOnResetPasswordButton();
}
