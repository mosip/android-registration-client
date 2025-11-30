
package regclient.page;

import java.time.Duration;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.remote.SupportsContextSwitching;

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

	@FindBy(xpath = "//android.widget.TextView[@text='Password']")
	private WebElement passwordOption;

	@FindBy(xpath = "//android.widget.EditText[@resource-id='password']")
	private WebElement passwordField;

	@FindBy(xpath = "//android.widget.EditText[@resource-id='password-new']")
	private WebElement newPasswordField;

	@FindBy(xpath = "//android.widget.EditText[@resource-id='password-confirm']")
	private WebElement confirmPasswordField;

	@FindBy(xpath = "//android.widget.Button[@text='Save']")
	private WebElement saveButton;

	@FindBy(xpath = "//android.widget.TextView[contains(@text,'Your password has been updated.')]")
	private WebElement passwordUpdatedMessage;

	@FindBy(xpath = "//android.widget.TextView[@text='Sign Out']")
	private WebElement signoutButton;

	@AndroidFindBy(accessibility = "LOGOUT")
	private WebElement logoutButton;

	public boolean openKeycloakWebView() {
	    String webCtx = findWebViewContext(Duration.ofSeconds(5));
	    if (webCtx != null) {
	        ((SupportsContextSwitching) driver).context(webCtx);
	        try { Thread.sleep(250); } catch (InterruptedException ignored) {}
	    } else {
	        try { ((SupportsContextSwitching) driver).context("NATIVE_APP"); } catch (Exception ignored) {}
	    }

	    retryFindElement(keycloakPageTitle, Duration.ofSeconds(10));
	    return isElementDisplayed(keycloakPageTitle);
	}

	public boolean openKeycloakPassword() {

	    scrollToTopSafe();

	    By nativePwd = By.xpath("//android.widget.TextView[@text='Password']");

	    try {
	        ((SupportsContextSwitching) driver).context("NATIVE_APP");
	        new WebDriverWait(driver, Duration.ofSeconds(8))
	                .until(ExpectedConditions.visibilityOfElementLocated(nativePwd));
	        return true;
	    } catch (Exception e) {
	        System.out.println("Password not found in native.");
	    }

	    for (String c : ((SupportsContextSwitching) driver).getContextHandles()) {
	        if (c.contains("WEBVIEW")) {
	            ((SupportsContextSwitching) driver).context(c);
	            try {
	                new WebDriverWait(driver, Duration.ofSeconds(8))
	                        .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#password")));
	                return true;
	            } catch (Exception ignore) {}
	        }
	    }
	    return false;
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
		clickOnElement(passwordOption);
	}
	
	public void enterExistPassword(String password) {
//		switchContext("WEBVIEW_chrome");
		sendKeysToTextBox(passwordField, password);
	}
	
	public void enterNewPassword(String password) {
//		switchContext("WEBVIEW_chrome");
		clickAndsendKeysToTextBox(newPasswordField, password);
	}
	
	public void enterConfirmPassword(String password) {
//		switchContext("WEBVIEW_chrome");
		clickAndsendKeysToTextBox(confirmPasswordField, password);
	}
	
	public void clickOnSaveButton() {
//		switchContext("WEBVIEW_chrome");
		clickOnElement(saveButton);
	}

	public boolean isPasswordUpdatedMessageDisplayed() {
//		switchContext("WEBVIEW_chrome");
		return isElementDisplayed(passwordUpdatedMessage);
	}

	public void clickOnSignoutButton() {
//		switchContext("WEBVIEW_chrome");
		scrollToTop();
		clickOnElement(signoutButton);
	}

	public boolean resumeArcApplication() {
		openArcApplication("NATIVE_APP");
		return isElementDisplayed(logoutButton);
	}
}
