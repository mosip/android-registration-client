package regclient.pages.arabic;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.LoginPage;
import regclient.page.ProfilePage;
import regclient.pages.english.LoginPageEnglish;

public class ProfilePageArabic extends ProfilePage {

	@AndroidFindBy(accessibility = "حساب تعريفي")
	private WebElement profileTitle;

	@AndroidFindBy(accessibility = "الخروج")
	private WebElement logoutButton;

	@AndroidFindBy(accessibility = "لا يزال هناك بعض الإجراءات المطلوبة!")
	private WebElement logoutPopUpMessage;

	@AndroidFindBy(accessibility = "إعادة تعيين كلمة المرور")
	private WebElement resetPasswordButton;

	public ProfilePageArabic(AppiumDriver driver) {
		super(driver);
	}

	public LoginPage clickOnLogoutButton() {
		if (isElementDisplayed(logoutButton))
			clickOnElement(logoutButton);
		return new LoginPageArabic(driver);
	}

	public LoginPage clickOnLogoutButtonOnPopUp() {
		clickOnElement(logoutButton);
		return new LoginPageArabic(driver);
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
		return new LoginPageArabic(driver);
	}

}
