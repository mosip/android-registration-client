package regclient.pages.tamil;

import java.time.Duration;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.AutoLogoutPage;
import regclient.page.LoginPage;
import regclient.page.RegistrationTasksPage;

public class AutoLogoutPageTamil extends AutoLogoutPage{
	

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"You have been idle\")")
	private WebElement autoLogoutPopup;
	
	@AndroidFindBy(accessibility = "LOG OUT")
	private WebElement logoutButton;
	
	@AndroidFindBy(accessibility = "STAY LOGGED IN")
	private WebElement stayLoggedInButton;

	public AutoLogoutPageTamil(AppiumDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}
	
	public boolean isAutoLogoutPopupDisplayed() {
	    try {
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMinutes(10));
	        wait.until(ExpectedConditions.visibilityOf(autoLogoutPopup));
	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	public RegistrationTasksPage clickOnStayLoggedInButton() {
		clickOnElement(stayLoggedInButton);
		return new RegistrationTasksPageTamil(driver);
	}
	
	public LoginPage clickOnStaylogoutButton() {
		clickOnElement(logoutButton);
		return new LoginPageTamil(driver);
	}

}
