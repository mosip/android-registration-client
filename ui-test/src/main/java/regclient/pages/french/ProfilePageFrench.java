package regclient.pages.french;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.LoginPage;
import regclient.page.ProfilePage;
import regclient.pages.english.LoginPageEnglish;

public class ProfilePageFrench extends ProfilePage{

	@AndroidFindBy(accessibility = "Profil")
	private WebElement profileTitle;
	
	@AndroidFindBy(accessibility = "DÉCONNEXION")
	private WebElement logoutButton;
	
	@AndroidFindBy(accessibility = "There is still some action required!")
	private WebElement logoutPopUpMessage;
	

	public ProfilePageFrench(AppiumDriver driver) {
		super(driver);
	}

	public  LoginPage clickOnLogoutButton() {
		if(isElementDisplayed(logoutButton))
		clickOnElement(logoutButton);
		return new LoginPageEnglish(driver);
	}	
	
	public  LoginPage clickOnLogoutButtonOnPopUp() {
		clickOnElement(logoutButton);
		return new LoginPageEnglish(driver);
	}	
	
	public boolean isProfileTitleDisplayed() {
		return isElementDisplayed(profileTitle);
	}
	
	public boolean isLogoutPopUpMessageDisplayed() {
		return isElementDisplayed(logoutPopUpMessage);
	}


}
