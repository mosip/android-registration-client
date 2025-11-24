package regclient.pages.hindi;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.LoginPage;
import regclient.page.RegistrationTasksPage;

import java.time.Duration;

import org.openqa.selenium.WebElement;

public class LoginPageHindi extends LoginPage {

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(1)")
	private WebElement mosipLogo;

	@AndroidFindBy(xpath = "//android.widget.EditText")
	private WebElement userNameTextBox;

	@AndroidFindBy(accessibility = "अगला")
	private WebElement nextButton;

	@AndroidFindBy(xpath = "//android.widget.EditText")
	private WebElement passwordTextBox;

	@AndroidFindBy(xpath = "(//android.view.View[@content-desc=\"लॉगिन\"])[2]")
	private WebElement loginButton;

	@AndroidFindBy(accessibility = "सुविधाओं तक पहुँचने के लिए कृपया लॉगिन करें.")
	private WebElement loginMessage;

	@AndroidFindBy(accessibility = "पासवर्ड")
	private WebElement passwordHeader;

	@AndroidFindBy(accessibility = "उपयोगकर्ता नाम")
	private WebElement userNameHeader;

	@AndroidFindBy(accessibility = "आपका स्वागत है")
	private WebElement welcomeMessage;

	@AndroidFindBy(accessibility = "मदद")
	private WebElement helpButton;

	@AndroidFindBy(accessibility = "वापस")
	private WebElement backButton;

	@AndroidFindBy(accessibility = "पासवर्ड भूल गए?")
	private WebElement forgetPasswordButton;

	@AndroidFindBy(accessibility = "उपयोगकर्ता नहीं मिला!")
	private WebElement userNotFoundErrorMessage;

	@AndroidFindBy(accessibility = "पासवर्ड गलत है!")
	private WebElement passwordIncorrectErrorMessage;

	@AndroidFindBy(accessibility = "हिन्दी")
	private WebElement hindiButton;
	
	@AndroidFindBy(accessibility = "होम पेज पर जाएं")
	private WebElement skipToHomeButton;

	public LoginPageHindi(AppiumDriver driver) {
		super(driver);
	}

	public void enterUserName(String username) {
		clickAndsendKeysToTextBox(userNameTextBox, username);
	}

	public void clickOnNextButton() {
		clickOnElement(nextButton);
	}

	public void clickOnBackButton() {
		clickOnElement(backButton);
	}

	public void enterPassword(String password) {
		retryFindElement(passwordTextBox, Duration.ofSeconds(10));
		clickAndsendKeysToTextBox(passwordTextBox, password);
	}

	public RegistrationTasksPage clickOnloginButton() {
		clickOnElement(loginButton);
		return new RegistrationTasksPageHindi(driver);
	}

	public boolean isNextButtonEnabled() {
		return isElementEnabled(nextButton);
	}

	public boolean isLoginButtonEnabled() {
		return isElementEnabled(loginButton);
	}

	public boolean isLoginPageLoaded() {
		return isElementDisplayed(loginMessage);
	}

	public boolean isPasswordHeaderDisplayed() {
		return isElementDisplayed(passwordHeader);
	}

	public boolean isUserNameHeaderDisplayed() {
		return isElementDisplayed(userNameHeader);
	}

	public boolean isWelcomeMessageDisplayed() {
		return isElementDisplayed(welcomeMessage);
	}

	public boolean isWelcomeMessageInSelectedLanguageDisplayed() {
		return isElementDisplayed(welcomeMessage);
	}

	public boolean isHelpButtonDisplayed() {
		return isElementDisplayed(helpButton);
	}

	public boolean isBackButtonDisplayed() {
		return isElementDisplayed(backButton);
	}

	public boolean isForgetOptionDisplayed() {
		return isElementDisplayed(forgetPasswordButton);
	}

	public boolean isUserNotFoundErrorMessageDisplayed() {
		return isElementDisplayed(userNotFoundErrorMessage);
	}

	public boolean isPasswordIncorrectErrorMessageDisplayed() {
		return isElementDisplayed(passwordIncorrectErrorMessage);
	}

	public boolean isMosipLogoDisplayed() {
		return isElementDisplayed(mosipLogo);
	}

	public void selectLanguage() {
		clickOnElement(hindiButton);
	}

	public void clickOnSkipToHomeButton() {
		clickOnElement(skipToHomeButton);
	}
}
