package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class KeycloakPage extends BasePage {

	public KeycloakPage(AppiumDriver driver) {
		super(driver);

	}

	public abstract boolean openKeycloakWebView();

	public abstract String getPageTitle();

	public abstract void clickOnLanguageDropdown();

	public abstract void clickOnEnglishLanguage();

	public abstract void enterUserName(String username);

	public abstract void enterPassword(String password);

	public abstract void clickOnloginButton();

	public abstract void clickOnPasswordOption();

	public abstract void enterExistPassword(String password);

	public abstract void enterNewPassword(String password);

	public abstract void enterConfirmPassword(String password);

	public abstract void clickOnSaveButton();

	public abstract boolean isPasswordUpdatedMessageDisplayed();

	public abstract void clickOnSignoutButton();

	public abstract boolean openKeycloakPassword();

	public abstract boolean resumeArcApplication();

}
