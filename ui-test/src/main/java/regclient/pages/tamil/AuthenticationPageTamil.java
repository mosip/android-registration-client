package regclient.pages.tamil;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.AcknowledgementPage;
import regclient.page.AuthenticationPage;

public class AuthenticationPageTamil extends AuthenticationPage {

	@AndroidFindBy(accessibility = "கடவுச்சொல் பயன்படுத்தி அங்கீகரித்தல்")
	private WebElement authenticationPageTitle;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(0)")
	private WebElement userNameTextBox;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(1)")
	private WebElement passwordTextBox;
	
	@AndroidFindBy(accessibility = "அங்கீகரிக்க")
	private WebElement authenticateButton;
	
	@AndroidFindBy(xpath = "//android.view.View[@content-desc='Authentication using Password']/preceding-sibling::android.widget.ImageView")
	private WebElement authenticationImage;
	
	public AuthenticationPageTamil(AppiumDriver driver) {
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
		return new AcknowledgementPageTamil(driver);
	}
	
	public boolean isAuthenticationImageDisplayed() {
		return isElementDisplayed(authenticationImage);
	}

}
