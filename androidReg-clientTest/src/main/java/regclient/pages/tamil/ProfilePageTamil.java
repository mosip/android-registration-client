package regclient.pages.tamil;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.LoginPage;
import regclient.page.ProfilePage;
import regclient.pages.english.LoginPageEnglish;

public class ProfilePageTamil extends ProfilePage{

	@AndroidFindBy(accessibility = "சுயவிவரம்")
	private WebElement profileTitle;
	
	@AndroidFindBy(accessibility = "உள்நுழைவு")
	private WebElement logoutButton;
	
	@AndroidFindBy(accessibility = "There is still some action required!")
	private WebElement logoutPopUpMessage;
	

	public ProfilePageTamil(AppiumDriver driver) {
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
