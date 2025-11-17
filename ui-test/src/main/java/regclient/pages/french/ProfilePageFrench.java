package regclient.pages.french;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.LoginPage;
import regclient.page.ProfilePage;
import regclient.pages.english.LoginPageEnglish;

public class ProfilePageFrench extends ProfilePage {

	@AndroidFindBy(accessibility = "Profil")
	private WebElement profileTitle;

	@AndroidFindBy(accessibility = "DÉCONNEXION")
	private WebElement logoutButton;

	@AndroidFindBy(accessibility = "Il y a encore des actions à faire !")
	private WebElement logoutPopUpMessage;

	@AndroidFindBy(accessibility = "Réinitialiser le mot de passe")
	private WebElement resetPasswordButton;

	public ProfilePageFrench(AppiumDriver driver) {
		super(driver);
	}

	public LoginPage clickOnLogoutButton() {
		if (isElementDisplayed(logoutButton))
			clickOnElement(logoutButton);
		return new LoginPageFrench(driver);
	}

	public LoginPage clickOnLogoutButtonOnPopUp() {
		clickOnElement(logoutButton);
		return new LoginPageFrench(driver);
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
		return new LoginPageFrench(driver);
	}
}
