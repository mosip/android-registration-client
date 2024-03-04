package regclient.pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import regclient.utils.AndroidUtil;
import regclient.utils.TestDataReader;

import org.openqa.selenium.WebElement;

public class LoginPage extends BasePage {

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(1)")
	private WebElement mosipLogo;

	@AndroidFindBy(xpath = "//android.widget.EditText")
	private WebElement usernametextbox;

	@AndroidFindBy(accessibility = "NEXT")
	private WebElement nextbutton;

	@AndroidFindBy(xpath = "//android.widget.EditText")
	private WebElement passwordtextbox;

	@AndroidFindBy(accessibility = "LOGIN")
	private WebElement LoginButton;

	@AndroidFindBy(accessibility = "Please login to access the features.")
	private WebElement Loginmsg;

	@AndroidFindBy(accessibility = "Password")
	private WebElement passwordheader;

	@AndroidFindBy(accessibility = "Username")
	private WebElement usernameHeader;

	@AndroidFindBy(accessibility = "Welcome to")
	private WebElement welcome;

	@AndroidFindBy(accessibility = "HELP")
	private WebElement HELP;

	@AndroidFindBy(accessibility = "BACK")
	private WebElement Back;

	@AndroidFindBy(accessibility = "Forgot Password?")
	private WebElement forgetPassword;

	@AndroidFindBy(accessibility = "User not found!")
	private WebElement userNotFoundErrorMsg;

	@AndroidFindBy(accessibility = "Password incorrect!")
	private WebElement passwordIncorrectErrorMsg;

	@AndroidFindBy(accessibility = "English")
	private WebElement English;

	@AndroidFindBy(accessibility = "French")
	private WebElement French;

	@AndroidFindBy(accessibility = "Arabic")
	private WebElement Arabic;

	@AndroidFindBy(accessibility = "ಕನ್ನಡ")
	private WebElement Kannada;

	@AndroidFindBy(accessibility = "हिन्दी")
	private WebElement Hindi;

	@AndroidFindBy(accessibility = "தமிழ்")
	private WebElement Tamil;

	@AndroidFindBy(accessibility = "spanish")
	private WebElement spanish;

	public LoginPage(AppiumDriver driver) {
		super(driver);
	}


	public  void enterusername(String username) {
		clickAndsendKeysToTextBox(usernametextbox,username);
	}

	public  void clickOnNextButton() {
		clickOnElement(nextbutton);
	}

	public  void clickOnBackButton() {
		clickOnElement(Back);
	}

	public  void enterpassword(String password) {
		clickAndsendKeysToTextBox(passwordtextbox,password);
	}

	public  RegistrationTasksPage clickOnloginButton() {
		clickOnElement(LoginButton);
		return new RegistrationTasksPage(driver);
	}

	public  boolean isNextButtonEnable() {
		return isElementEnabled(nextbutton);
	}

	public  boolean isLoginButtonEnable() {
		return isElementEnabled(LoginButton);
	}

	public boolean isLoginPageLoaded() {

		return isElementDisplayed(Loginmsg);
	}

	public boolean isPasswordHeaderDisplay() {
		return isElementDisplayed(passwordheader);
	}

	public boolean isUsernameHeaderDisplay() {
		return isElementDisplayed(usernameHeader);
	}

	public boolean isWelcomeMsgDisplay() {

		return isElementDisplayed(welcome);
	}

	public boolean isHelpButtonDisplay() {

		return isElementDisplayed(HELP);
	}

	public boolean isBackButtonDisplay() {

		return isElementDisplayed(Back);
	}

	public boolean isForgetOptionDisplay() {
		return isElementDisplayed(forgetPassword);

	}

	public boolean isUserNotFoundErrorMsgDisplay() {
		return isElementDisplayed(userNotFoundErrorMsg);

	}

	public boolean isPasswordIncorrectErrorMsgDisplay() {
		return isElementDisplayed(passwordIncorrectErrorMsg);

	}

	public boolean isMosipLogoDisplay() {
		return isElementDisplayed(mosipLogo);

	}

	public void selectLanguage(String language) {
		if(language.equalsIgnoreCase("eng"))
			clickOnElement(English);
		if(language.equalsIgnoreCase("fra"))
			clickOnElement(French);
		if(language.equalsIgnoreCase("ara"))
			clickOnElement(Arabic);
		if(language.equalsIgnoreCase("kan"))
			clickOnElement(Kannada);
		if(language.equalsIgnoreCase("hin"))
			clickOnElement(Hindi);
		if(language.equalsIgnoreCase("tam"))
			clickOnElement(Tamil);
		if(language.equalsIgnoreCase("spa"))
			clickOnElement(spanish);
	}
}
