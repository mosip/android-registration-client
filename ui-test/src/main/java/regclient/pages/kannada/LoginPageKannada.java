package regclient.pages.kannada;

import java.time.Duration;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.LoginPage;
import regclient.page.RegistrationTasksPage;

public class LoginPageKannada extends LoginPage {

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(1)")
	private WebElement mosipLogo;

	@AndroidFindBy(xpath = "//android.widget.EditText")
	private WebElement userNameTextBox;

	@AndroidFindBy(accessibility = "ಮುಂದೆ")
	private WebElement nextButton;

	@AndroidFindBy(xpath = "//android.widget.EditText")
	private WebElement passwordTextBox;

	@AndroidFindBy(accessibility = "ಲಾಗಿನ್")
	private WebElement loginButton;

	@AndroidFindBy(accessibility = "ವೈಶಿಷ್ಟ್ಯಗಳನ್ನು ಪ್ರವೇಶಿಸಲು ದಯವಿಟ್ಟು ಲಾಗಿನ್ ಮಾಡಿ.")
	private WebElement loginMessage;

	@AndroidFindBy(accessibility = "ಪಾಸ್ ವರ್ಡ್")
	private WebElement passwordHeader;

	@AndroidFindBy(accessibility = "ಬಳಕೆದಾರ ಹೆಸರು")
	private WebElement userNameHeader;

	@AndroidFindBy(accessibility = "ಇದಕ್ಕೆ ಸ್ವಾಗತ")
	private WebElement welcomeMessage;

	@AndroidFindBy(accessibility = "ಸಹಾಯ")
	private WebElement helpButton;

	@AndroidFindBy(accessibility = "ಹಿಂದೆ")
	private WebElement backButton;

	@AndroidFindBy(accessibility = "ಪಾಸ್ ವರ್ಡ್ ಮರೆತಿದ್ದೀರಾ?")
	private WebElement forgetPasswordButton;

	@AndroidFindBy(accessibility = "ಬಳಕೆದಾರ ಸಿಗಲಿಲ್ಲ!")
	private WebElement userNotFoundErrorMessage;

	@AndroidFindBy(accessibility = "ಪಾಸ್ ವರ್ಡ್ ತಪ್ಪಾಗಿದೆ!")
	private WebElement passwordIncorrectErrorMessage;

	@AndroidFindBy(accessibility = "ಕನ್ನಡ")
	private WebElement kannadaButton;

	@AndroidFindBy(accessibility = "ಮನೆಗೆ ತೆರಳಿ")
	private WebElement skipToHomeButton;

	public LoginPageKannada(AppiumDriver driver) {
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
		return new RegistrationTasksPageKannada(driver);
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
		clickOnElement(kannadaButton);
	}

	public void clickOnSkipToHomeButton() {
		clickOnElement(skipToHomeButton);
	}

}
