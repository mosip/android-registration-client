package regclient.pages.french;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.AcknowledgementPage;
import regclient.page.AuthenticationPage;

public class AuthenticationPageFrench extends AuthenticationPage{

	@AndroidFindBy(accessibility = "Authentification en utilisant un mot de passe")
	private WebElement authenticationPageTitle;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(0)")
	private WebElement userNameTextBox;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(1)")
	private WebElement passwordTextBox;
	
	@AndroidFindBy(accessibility = "AUTHENTIFIER")
	private WebElement authenticateButton;
	
	public AuthenticationPageFrench(AppiumDriver driver) {
		super(driver);
	}
	
	public  void enterUserName(String username) {
		clickAndsendKeysToTextBox(userNameTextBox,username);
	}
	
	public  void enterPassword(String password) {
		clickAndsendKeysToTextBox(passwordTextBox,password);
	}
	
	public boolean isAuthenticationPageDisplayed() {
		return isElementDisplayed(authenticationPageTitle);
	}
	
	public AcknowledgementPage clickOnAuthenticatenButton() {
		clickOnElement(authenticateButton);
		return new AcknowledgementPageFrench(driver);
	}

}
