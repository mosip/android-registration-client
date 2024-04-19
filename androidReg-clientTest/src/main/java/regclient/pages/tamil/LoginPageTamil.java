package regclient.pages.tamil;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.LoginPage;
import regclient.page.RegistrationTasksPage;

public class LoginPageTamil extends LoginPage{

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(1)")
	private WebElement mosipLogo;

	@AndroidFindBy(xpath = "//android.widget.EditText")
	private WebElement userNameTextBox;

	@AndroidFindBy(accessibility = "அடுத்தவர்")
	private WebElement nextButton;

	@AndroidFindBy(xpath = "//android.widget.EditText")
	private WebElement passwordTextBox;

	@AndroidFindBy(xpath = "(//android.view.View[@content-desc=\"உள்நுழைவு\"])[2]")
	private WebElement loginButton;

	@AndroidFindBy(accessibility = "அம்சங்களை அணுக உள்நுழைக.")
	private WebElement loginMessage;

	@AndroidFindBy(accessibility = "அடையாளச் சொல்")
	private WebElement passwordHeader;

	@AndroidFindBy(accessibility = "பயனர் பெயர்")
	private WebElement userNameHeader;

	@AndroidFindBy(accessibility = "உங்களை வரவேற்கிறோம்")
	private WebElement welcomeMessage;

	@AndroidFindBy(accessibility = "உதவி")
	private WebElement helpButton;

	@AndroidFindBy(accessibility = "முதுகு")
	private WebElement backButton;

	@AndroidFindBy(accessibility = "கடவுச்சொல் மறந்துவிட்டதா?")
	private WebElement forgetPasswordButton;

	@AndroidFindBy(accessibility = "பயனர் காணப்படவில்லை!")
	private WebElement userNotFoundErrorMessage;

	@AndroidFindBy(accessibility = "கடவுச்சொல் தவறானது!")
	private WebElement passwordIncorrectErrorMessage;

	@AndroidFindBy(accessibility = "தமிழ்")
	private WebElement tamilButton;
	
	public LoginPageTamil(AppiumDriver driver) {
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
		return new RegistrationTasksPageTamil(driver);
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
		clickOnElement(tamilButton);
	}

}
