package regclient.pages.kannada;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.AcknowledgementPage;
import regclient.page.AuthenticationPage;


public class AuthenticationPageKannada extends AuthenticationPage {

	@AndroidFindBy(accessibility = "ಪಾಸ್‌ವರ್ಡ್ ಬಳಸಿ ಅಂಗೀಕರಿಸಿ")
	private WebElement authenticationPageTitle;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(0)")
	private WebElement userNameTextBox;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(1)")
	private WebElement passwordTextBox;
	
	@AndroidFindBy(accessibility = "ಪ್ರಮಾಣಿತಗೊಳಿಸಿ")
	private WebElement authenticateButton;
	
	public AuthenticationPageKannada(AppiumDriver driver) {
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
		return new AcknowledgementPageKannada(driver);
	}

}
