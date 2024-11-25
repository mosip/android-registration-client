package regclient.pages.arabic;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.LoginPage;
import regclient.page.RegistrationTasksPage;

public class LoginPageArabic extends LoginPage {
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(1)")
	private WebElement mosipLogo;

	@AndroidFindBy(xpath = "//android.widget.EditText")
	private WebElement userNameTextBox;

	@AndroidFindBy(accessibility = "مقبل")
	private WebElement nextButton;

	@AndroidFindBy(xpath = "//android.widget.EditText")
	private WebElement passwordTextBox;

	@AndroidFindBy(xpath = "(//android.view.View[@content-desc=\"تسجيل الدخول\"])[2]")
	private WebElement loginButton;

	@AndroidFindBy(accessibility = "يرجى تسجيل الدخول للوصول إلى الميزات.")
	private WebElement loginMessage;

	@AndroidFindBy(accessibility = "شعار")
	private WebElement passwordHeader;

	@AndroidFindBy(accessibility = "اسم المستخدم")
	private WebElement userNameHeader;

	@AndroidFindBy(accessibility = "مرحبا بكم في")
	private WebElement welcomeMessage;

	@AndroidFindBy(accessibility = "تعليمات")
	private WebElement helpButton;

	@AndroidFindBy(accessibility = "ظهر")
	private WebElement backButton;

	@AndroidFindBy(accessibility = "نسيت كلمة المرور?")
	private WebElement forgetPasswordButton;

	@AndroidFindBy(accessibility = "لم يتم العثور على المستخدم!")
	private WebElement userNotFoundErrorMessage;

	@AndroidFindBy(accessibility = "كلمة المرور غير صحيحة!")
	private WebElement passwordIncorrectErrorMessage;

	@AndroidFindBy(accessibility = "Arabic")
	private WebElement arabicButton;
	
	public LoginPageArabic(AppiumDriver driver) {
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
		return new RegistrationTasksPageArabic(driver);
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
		clickOnElement(arabicButton);
	}

}
