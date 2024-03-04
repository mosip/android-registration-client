package regclient.pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;


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
	private WebElement loginButton;

	@AndroidFindBy(accessibility = "Please login to access the features.")
	private WebElement loginMessage;

	@AndroidFindBy(accessibility = "Password")
	private WebElement passwordheader;

	@AndroidFindBy(accessibility = "Username")
	private WebElement usernameHeader;

	@AndroidFindBy(accessibility = "Welcome to")
	private WebElement welcome;

	@AndroidFindBy(accessibility = "HELP")
	private WebElement help;

	@AndroidFindBy(accessibility = "BACK")
	private WebElement back;

	@AndroidFindBy(accessibility = "Forgot Password?")
	private WebElement forgetPassword;

	@AndroidFindBy(accessibility = "User not found!")
	private WebElement userNotFoundErrorMsg;

	@AndroidFindBy(accessibility = "Password incorrect!")
	private WebElement passwordIncorrectErrorMsg;
	
	@AndroidFindBy(accessibility = "English")
	private WebElement english;
	
	@AndroidFindBy(accessibility = "French")
	private WebElement french;
	
	@AndroidFindBy(accessibility = "Arabic")
	private WebElement arabic;
	
	@AndroidFindBy(accessibility = "ಕನ್ನಡ")
	private WebElement kannada;
	
	@AndroidFindBy(accessibility = "हिन्दी")
	private WebElement hindi;
	
	@AndroidFindBy(accessibility = "தமிழ்")
	private WebElement tamil;
	
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
		clickOnElement(back);
	}

	public  void enterpassword(String password) {
		clickAndsendKeysToTextBox(passwordtextbox,password);
	}

	public  RegistrationTasksPage clickOnloginButton() {
		clickOnElement(loginButton);
		return new RegistrationTasksPage(driver);
	}

	public  boolean isNextButtonEnable() {
		return isElementEnabled(nextbutton);
	}

	public  boolean isLoginButtonEnable() {
		return isElementEnabled(loginButton);
	}

	public boolean isLoginPageLoaded() {

		return isElementDisplayed(loginMessage);
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

		return isElementDisplayed(help);
	}

	public boolean isBackButtonDisplay() {

		return isElementDisplayed(back);
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
			clickOnElement(english);
		if(language.equalsIgnoreCase("fra"))
			clickOnElement(french);
		if(language.equalsIgnoreCase("ara"))
			clickOnElement(arabic);
		if(language.equalsIgnoreCase("kan"))
			clickOnElement(kannada);
		if(language.equalsIgnoreCase("hin"))
			clickOnElement(hindi);
		if(language.equalsIgnoreCase("tam"))
			clickOnElement(tamil);
		if(language.equalsIgnoreCase("spa"))
			clickOnElement(spanish);
	}
}
