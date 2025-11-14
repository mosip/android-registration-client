package regclient.page;

import io.appium.java_client.AppiumDriver;;

public abstract class LoginPage extends BasePage {

	public LoginPage(AppiumDriver driver) {
		super(driver);
	}

	public abstract void enterUserName(String username);

	public abstract void clickOnNextButton();

	public abstract void clickOnBackButton();

	public abstract void enterPassword(String password);

	public abstract RegistrationTasksPage clickOnloginButton();

	public abstract boolean isNextButtonEnabled();

	public abstract boolean isLoginButtonEnabled();

	public abstract boolean isLoginPageLoaded();

	public abstract boolean isPasswordHeaderDisplayed();

	public abstract boolean isUserNameHeaderDisplayed();

	public abstract boolean isWelcomeMessageDisplayed();

	public abstract boolean isWelcomeMessageInSelectedLanguageDisplayed();

	public abstract boolean isHelpButtonDisplayed();

	public abstract boolean isBackButtonDisplayed();

	public abstract boolean isForgetOptionDisplayed();

	public abstract boolean isUserNotFoundErrorMessageDisplayed();

	public abstract boolean isPasswordIncorrectErrorMessageDisplayed();

	public abstract boolean isMosipLogoDisplayed();

	public abstract void selectLanguage();

	public abstract void clickOnSkipToHomeButton();

}
