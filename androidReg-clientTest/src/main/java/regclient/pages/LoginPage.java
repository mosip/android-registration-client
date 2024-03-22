package regclient.pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;


import org.openqa.selenium.WebElement;

public class LoginPage extends BasePage {

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(1)")
	private WebElement mosipLogo;

	@AndroidFindBy(xpath = "//android.widget.EditText")
	private WebElement userNameTextBox;

	@AndroidFindBy(accessibility = "NEXT")
	private WebElement nextButton;

	@AndroidFindBy(xpath = "//android.widget.EditText")
	private WebElement passwordTextBox;

	@AndroidFindBy(accessibility = "LOGIN")
	private WebElement loginButton;

	@AndroidFindBy(accessibility = "Please login to access the features.")
	private WebElement loginMessage;

	@AndroidFindBy(accessibility = "Password")
	private WebElement passwordHeader;

	@AndroidFindBy(accessibility = "Username")
	private WebElement userNameHeader;

	@AndroidFindBy(accessibility = "Welcome to")
	private WebElement welcomeMessage;

	@AndroidFindBy(accessibility = "HELP")
	private WebElement helpButton;

	@AndroidFindBy(accessibility = "BACK")
	private WebElement backButton;

	@AndroidFindBy(accessibility = "Forgot Password?")
	private WebElement forgetPasswordButton;

	@AndroidFindBy(accessibility = "User not found!")
	private WebElement userNotFoundErrorMessage;

	@AndroidFindBy(accessibility = "Password incorrect!")
	private WebElement passwordIncorrectErrorMessage;

	@AndroidFindBy(accessibility = "English")
	private WebElement englishButton;

	@AndroidFindBy(accessibility = "French")
	private WebElement frenchButton;

	@AndroidFindBy(accessibility = "Arabic")
	private WebElement arabicButton;

	@AndroidFindBy(accessibility = "ಕನ್ನಡ")
	private WebElement kannadaButton;

	@AndroidFindBy(accessibility = "हिन्दी")
	private WebElement hindiButton;

	@AndroidFindBy(accessibility = "தமிழ்")
	private WebElement tamilButton;

	@AndroidFindBy(accessibility = "spanish")
	private WebElement spanishButton;

	public LoginPage(AppiumDriver driver) {
		super(driver);
	}


	public  void enterUserName(String username) {
		clickAndsendKeysToTextBox(userNameTextBox,username);
	}

	public  void clickOnNextButton() {
		clickOnElement(nextButton);
	}

	public  void clickOnBackButton() {
		clickOnElement(backButton);
	}

	public  void enterPassword(String password) {
		clickAndsendKeysToTextBox(passwordTextBox,password);
	}

	public  RegistrationTasksPage clickOnloginButton() {
		clickOnElement(loginButton);
		return new RegistrationTasksPage(driver);
	}

	public  boolean isNextButtonEnabled() {
		return isElementEnabled(nextButton);
	}

	public  boolean isLoginButtonEnabled() {
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

	public void selectLanguage(String language) {
		switch (language) {
		case "eng":
			clickOnElement(englishButton);
		case "fra":
			clickOnElement(frenchButton);
		case "ara":
			clickOnElement(arabicButton);
		case "kan":
			clickOnElement(kannadaButton);
		case "hin":
			clickOnElement(hindiButton);
		case "tam":
			clickOnElement(tamilButton);
		case "spa":
			clickOnElement(spanishButton);
		default:
			clickOnElement(englishButton);
		}
	}

}
