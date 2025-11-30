package regclient.pages.english;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.LoginPage;
import regclient.page.RegistrationTasksPage;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.time.Duration;

import org.openqa.selenium.WebElement;

public class LoginPageEnglish extends LoginPage {

	public LoginPageEnglish(AppiumDriver driver) {
		super(driver);
	}

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(1)")
	private WebElement mosipLogo;

	@AndroidFindBy(xpath = "//android.widget.EditText")
	private WebElement userNameTextBox;

	@AndroidFindBy(accessibility = "NEXT")
	private WebElement nextButton;

	@AndroidFindBy(accessibility = "Machine not found!")
	private WebElement machineNotFound;

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
	private WebElement welcomeMessageEnglish;

	@AndroidFindBy(accessibility = "Bienvenue à")
	private WebElement welcomeMessageFrench;

	@AndroidFindBy(accessibility = "مرحبا بكم في")
	private WebElement welcomeMessageArabic;

	@AndroidFindBy(accessibility = "ಇದಕ್ಕೆ ಸ್ವಾಗತ")
	private WebElement welcomeMessageKannada;

	@AndroidFindBy(accessibility = "आपका स्वागत है")
	private WebElement welcomeMessageHindi;

	@AndroidFindBy(accessibility = "உங்களை வரவேற்கிறோம்")
	private WebElement welcomeMessageTamil;

	@AndroidFindBy(accessibility = "HELP")
	private WebElement helpButton;

	@AndroidFindBy(accessibility = "BACK")
	private WebElement backButton;

	@AndroidFindBy(accessibility = "FORGOT PASSWORD?")
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

	@AndroidFindBy(accessibility = "Sync Completed Successfully")
	private WebElement syncCompletedSuccessfullyMessage;

	@AndroidFindBy(accessibility = "COPY TEXT")
	private WebElement copyTextButton;

	@AndroidFindBy(accessibility = "SKIP TO HOME")
	private WebElement skipToHomeButton;

	public void enterUserName(String username) {
		clickAndsendKeysToTextBox(userNameTextBox, username);
	}

	public void clickOnCopyTextButton() {
		clickOnElement(copyTextButton);
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
		return new RegistrationTasksPageEnglish(driver);
	}

	public boolean isNextButtonEnabled() {
		return isElementEnabled(nextButton);
	}

	public boolean isLoginButtonEnabled() {
		return isElementEnabled(loginButton);
	}

	public boolean isLoginPageLoaded() {
		return true;
	}

	public boolean isPasswordHeaderDisplayed() {
		return isElementDisplayed(passwordHeader);
	}

	public boolean isUserNameHeaderDisplayed() {
		return isElementDisplayed(userNameHeader);
	}

	public boolean isWelcomeMessageDisplayed() {
		return isElementDisplayed(welcomeMessageEnglish);
	}

	public boolean isMachineNotFoundMessageDisplayed() {
		return isElementDisplayed(machineNotFound);
	}

	public boolean isWelcomeMessageInSelectedLanguageDisplayed() {
		return true;
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
		clickOnElement(englishButton);
	}

	public boolean isSyncCompletedSuccessfullyMessageDisplayed() {
		return isElementDisplayed(syncCompletedSuccessfullyMessage, 2000);
	}

	public void clickandHold() {
		clickAndHold();
	}

	public void getMachineDetails() {
		try {
			getMachineDetail();
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void clickOnSkipToHomeButton() {
		clickOnElement(skipToHomeButton);
	}

	public boolean isCopyTextPopupDisplayed() {
		return isElementDisplayed(copyTextButton, 2000);
	}
}
