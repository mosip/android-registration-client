package regclient.page;

import java.time.Duration;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;

public abstract class KeycloakPage extends BasePage {

	private WebDriverWait wait;

	public KeycloakPage(AppiumDriver driver) {
		super(driver);
	}

	@FindBy(id = "kc-page-title")
	private WebElement keycloakPageTitle;

	@FindBy(id = "English˅")
	private WebElement languageDropdown;

	@FindBy(id = "English")
	private WebElement englishLanguage;

	@FindBy(id = "username")
	private WebElement usernameTextBox;

	@FindBy(id = "password")
	private WebElement passwordTextBox;

	@FindBy(id = "kc-login")
	private WebElement loginButton;

	@AndroidFindBy(uiAutomator = "new UiSelector().text(\"Password\")")
	private WebElement passwordOption;

	@FindBy(id = "password")
	private WebElement passwordTextbox;

	@FindBy(id = "password-new")
	private WebElement newPasswordTextbox;

	@FindBy(id = "password-confirm")
	private WebElement confirmPasswordTextbox;

	@FindBy(xpath = "//button[text()='Save']")
	private WebElement saveButton;

	@FindBy(xpath = "//*[contains(text(),'Your password has been updated.')]")
	private WebElement passwordUpdatedMessage;

	@FindBy(xpath = "//*[contains(text(),'Sign Out')]")
	private WebElement signoutButton;

	@AndroidFindBy(accessibility = "LOGOUT")
	private WebElement logoutButton;

	public boolean openKeycloakWebView() {
		switchContext("WEBVIEW_chrome");
		retryFindElement(keycloakPageTitle, Duration.ofSeconds(10));
		return isElementDisplayed(keycloakPageTitle);
	}

	public boolean openKeycloakPassword() {
		switchContext("NATIVE_APP");
		retryFindElement(passwordOption, Duration.ofSeconds(10));
		return isElementDisplayed(passwordOption);
	}

	public String getPageTitle() {
		return keycloakPageTitle.getText();
	}

	public void clickOnLanguageDropdown() {
		clickOnElement(languageDropdown);
	}

	public void clickOnEnglishLanguage() {
		clickOnElement(englishLanguage);
	}

	public void enterUserName(String username) {
		clickAndsendKeysToTextBox(usernameTextBox, username);
	}

	public void enterPassword(String password) {
		retryFindElement(passwordTextBox, Duration.ofSeconds(10));
		clickAndsendKeysToTextBox(passwordTextBox, password);
	}

	public void clickOnLoginButton() {
		clickOnElement(loginButton);
	}

	public void clickOnPasswordOption() {
		switchContext("NATIVE_APP");
		clickOnElement(passwordOption);
	}

	public void enterExistPassword(String password) {
		switchContext("WEBVIEW_chrome");
		retryFindElement(passwordTextbox, Duration.ofSeconds(10));
		clickAndsendKeysToTextBox(passwordTextbox, password);
	}

	public void enterNewPassword(String password) {
		switchContext("WEBVIEW_chrome");
		retryFindElement(newPasswordTextbox, Duration.ofSeconds(10));
		clickAndsendKeysToTextBox(newPasswordTextbox, password);
	}

	public void enterConfirmPassword(String password) {
		switchContext("WEBVIEW_chrome");
		retryFindElement(confirmPasswordTextbox, Duration.ofSeconds(10));
		clickAndsendKeysToTextBox(confirmPasswordTextbox, password);
	}

	public void clickOnSaveButton() {
		switchContext("WEBVIEW_chrome");
		clickOnElement(saveButton);
	}

	public boolean isPasswordUpdatedMessageDisplayed() {
		switchContext("WEBVIEW_chrome");
		return isElementDisplayed(passwordUpdatedMessage);
	}

	public void clickOnSignoutButton() {
		switchContext("WEBVIEW_chrome");
		clickOnElement(signoutButton);
	}

	public boolean resumeArcApplication() {
		openArcApplication("NATIVE_APP");
		return isElementDisplayed(logoutButton);
	}
}
