package regclient.pages.english;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.LoginPage;
import regclient.page.ProfilePage;

public class ProfilePageEnglish extends ProfilePage {

	@AndroidFindBy(accessibility = "Profile")
	private WebElement profileTitle;

	@AndroidFindBy(accessibility = "LOGOUT")
	private WebElement logoutButton;

	@AndroidFindBy(accessibility = "There is still some action required!")
	private WebElement logoutPopUpMessage;

	@AndroidFindBy(accessibility = "Reset Password")
	private WebElement resetPasswordButton;

	public ProfilePageEnglish(AppiumDriver driver) {
		super(driver);
	}

	public LoginPage clickOnLogoutButton() {
		if (isElementDisplayed(logoutButton))
			clickOnElement(logoutButton);
		return new LoginPageEnglish(driver);
	}

	public LoginPage clickOnLogoutButtonOnPopUp() {
		clickOnElement(logoutButton);
		return new LoginPageEnglish(driver);
	}

	public boolean isProfileTitleDisplayed() {
		return isElementDisplayed(profileTitle);
	}

	public boolean isLogoutPopUpMessageDisplayed() {
		return isElementDisplayed(logoutPopUpMessage);
	}

	public boolean isResetPasswordButtonDisplayed() {
		return isElementDisplayed(resetPasswordButton);
	}

	public LoginPage clickOnResetPasswordButton() {
		clickOnElement(resetPasswordButton);
		return new LoginPageEnglish(driver);
	}
}
