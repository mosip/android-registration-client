package regclient.pages;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;

public class AuthenticationPage extends BasePage {

	@AndroidFindBy(accessibility = "Authentication using Password")
	private WebElement authenticationPageTitle;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(0)")
	private WebElement userNameTextBox;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(1)")
	private WebElement passwordTextBox;
	
	@AndroidFindBy(accessibility = "AUTHENTICATE")
	private WebElement authenticateButton;
	
	public AuthenticationPage(AppiumDriver driver) {
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
		return new AcknowledgementPage(driver);
	}

}
