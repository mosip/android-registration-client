package regclient.pages.hindi;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.LoginPage;
import regclient.page.ProfilePage;
import regclient.pages.english.LoginPageEnglish;

public class ProfilePageHindi extends ProfilePage{

	@AndroidFindBy(accessibility = "प्रोफ़ाइल")
	private WebElement profileTitle;
	
	@AndroidFindBy(accessibility = "लॉगआउट")
	private WebElement logoutButton;
	
	@AndroidFindBy(accessibility = "There is still some action required!")
	private WebElement logoutPopUpMessage;
	
	@AndroidFindBy(accessibility = "पासवर्ड रीसेट")
	private WebElement resetPasswordButton;	

	public ProfilePageHindi(AppiumDriver driver) {
		super(driver);
	}

	public  LoginPage clickOnLogoutButton() {
		if(isElementDisplayed(logoutButton))
		clickOnElement(logoutButton);
		return new LoginPageHindi(driver);
	}	
	
	public  LoginPage clickOnLogoutButtonOnPopUp() {
		clickOnElement(logoutButton);
		return new LoginPageHindi(driver);
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

	public  LoginPage clickOnResetPasswordButton() {
		clickOnElement(resetPasswordButton);
		return new LoginPageHindi(driver);
	}	

}
