package regclient.pages.french;

import java.time.Duration;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.LoginPage;
import regclient.page.RegistrationTasksPage;

public class LoginPageFrench extends LoginPage {

	public LoginPageFrench(AppiumDriver driver) {
		super(driver);
	}

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(1)")
	private WebElement mosipLogo;

	@AndroidFindBy(xpath = "//android.widget.EditText")
	private WebElement userNameTextBox;

	@AndroidFindBy(accessibility = "PROCHAINE")
	private WebElement nextButton;

	@AndroidFindBy(xpath = "//android.widget.EditText")
	private WebElement passwordTextBox;

	@AndroidFindBy(accessibility = "CONNECTEZ-VOUS")
	private WebElement loginButton;

	@AndroidFindBy(accessibility = "Veuillez vous connecter pour accéder aux fonctionnalités.")
	private WebElement loginMessage;

	@AndroidFindBy(accessibility = "Mot de passe")
	private WebElement passwordHeader;

	@AndroidFindBy(accessibility = "Nom d’utilisateur")
	private WebElement userNameHeader;

	@AndroidFindBy(accessibility = "Bienvenue à")
	private WebElement welcomeMessage;

	@AndroidFindBy(accessibility = "AIDE")
	private WebElement helpButton;

	@AndroidFindBy(accessibility = "PRÉCÉDENT")
	private WebElement backButton;

	@AndroidFindBy(accessibility = "Mot de passe oublié?")
	private WebElement forgetPasswordButton;

	@AndroidFindBy(accessibility = "Utilisateur introuvable !")
	private WebElement userNotFoundErrorMessage;

	@AndroidFindBy(accessibility = "Mot de passe incorrect!")
	private WebElement passwordIncorrectErrorMessage;

	@AndroidFindBy(accessibility = "French")
	private WebElement frenchButton;

	@AndroidFindBy(accessibility = "PASSEZ À LA MAISON")
	private WebElement skipToHomeButton;

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
		return new RegistrationTasksPageFrench(driver);
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
		clickOnElement(frenchButton);
	}

	public void clickOnSkipToHomeButton() {
		clickOnElement(skipToHomeButton);
	}
}
