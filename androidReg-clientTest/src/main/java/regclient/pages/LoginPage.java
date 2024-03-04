package regclient.pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import regclient.utils.AndroidUtil;
import regclient.utils.TestDataReader;

import org.openqa.selenium.WebElement;

public class LoginPage extends BasePage {

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(1)")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement mosipLogo;

	@AndroidFindBy(xpath = "//android.widget.EditText")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement usernametextbox;

	@AndroidFindBy(accessibility = "NEXT")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement nextbutton;

	@AndroidFindBy(xpath = "//android.widget.EditText")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement passwordtextbox;

	@AndroidFindBy(accessibility = "LOGIN")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement LoginButton;

	@AndroidFindBy(accessibility = "Please login to access the features.")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement Loginmsg;

	@AndroidFindBy(accessibility = "Password")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement passwordheader;

	@AndroidFindBy(accessibility = "Username")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement usernameHeader;

	@AndroidFindBy(accessibility = "Welcome to")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement welcome;

	@AndroidFindBy(accessibility = "HELP")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement HELP;

	@AndroidFindBy(accessibility = "BACK")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement Back;

	@AndroidFindBy(accessibility = "Forgot Password?")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement forgetPassword;

	@AndroidFindBy(accessibility = "User not found!")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement userNotFoundErrorMsg;

	@AndroidFindBy(accessibility = "Password incorrect!")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement passwordIncorrectErrorMsg;
	
	@AndroidFindBy(accessibility = "English")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement English;
	
	@AndroidFindBy(accessibility = "French")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement French;
	
	@AndroidFindBy(accessibility = "Arabic")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement Arabic;
	
	@AndroidFindBy(accessibility = "ಕನ್ನಡ")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement Kannada;
	
	@AndroidFindBy(accessibility = "हिन्दी")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement Hindi;
	
	@AndroidFindBy(accessibility = "தமிழ்")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement Tamil;
	
	@AndroidFindBy(accessibility = "spanish")
	@iOSXCUITFindBy(accessibility = "")
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
