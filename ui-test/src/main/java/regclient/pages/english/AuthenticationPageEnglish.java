package regclient.pages.english;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.AcknowledgementPage;
import regclient.page.AuthenticationPage;

public class AuthenticationPageEnglish extends AuthenticationPage {

	@AndroidFindBy(accessibility = "Authentication using Password")
	private WebElement authenticationPageTitle;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(0)")
	private WebElement userNameTextBox;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(1)")
	private WebElement passwordTextBox;

	@AndroidFindBy(accessibility = "AUTHENTICATE")
	private WebElement authenticateButton;

	@AndroidFindBy(xpath = "//android.view.View[@content-desc='Authentication using Password']/preceding-sibling::android.widget.ImageView")
	private WebElement authenticationImage;

	public AuthenticationPageEnglish(AppiumDriver driver) {
		super(driver);
	}

	public void enterUserName(String username) {
		clickAndsendKeysToTextBox(userNameTextBox, username);
	}

	public void enterPassword(String password) {
		clickAndsendKeysToTextBox(passwordTextBox, password);
	}

	public boolean isAuthenticationPageDisplayed() {
		return isElementDisplayed(authenticationPageTitle);
	}

	public AcknowledgementPage clickOnAuthenticatenButton() {
		clickOnElement(authenticateButton);
		return new AcknowledgementPageEnglish(driver);
	}

	public boolean isAuthenticationImageDisplayed() {
		return isElementDisplayed(authenticationImage);
	}

}
