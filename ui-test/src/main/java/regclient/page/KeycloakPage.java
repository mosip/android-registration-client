
package regclient.page;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.SupportsContextSwitching;

public class KeycloakPage extends BasePage {

	private static final Logger logger = LoggerFactory.getLogger(KeycloakPage.class);

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

	@FindBy(xpath = "//*[@resource-id='com.android.chrome:id/negative_button']")
	private WebElement formResubmissionCancelButton;

	public boolean openKeycloakWebView() {
		String webCtx = findWebViewContext(Duration.ofSeconds(5));
		if (webCtx != null) {
			((SupportsContextSwitching) driver).context(webCtx);
			try {
				Thread.sleep(250);
			} catch (InterruptedException ignored) {
			}
			switchToVisibleTabAndCloseOthers();
			signOutIfStillLoggedIn();
		} else {
			try {
				((SupportsContextSwitching) driver).context("NATIVE_APP");
			} catch (Exception ignored) {
			}
		}

		retryFindElement(keycloakPageTitle, Duration.ofSeconds(10));
		return isElementDisplayed(keycloakPageTitle);
	}

	private void switchToVisibleTabAndCloseOthers() {
		switchToVisibleTab(true);
	}

	private void switchToVisibleTab(boolean closeOthers) {
		try {
			String visibleTab = null;
			for (String handle : driver.getWindowHandles()) {
				driver.switchTo().window(handle);
				Object state = ((JavascriptExecutor) driver).executeScript("return document.visibilityState;");
				if ("visible".equals(state)) {
					visibleTab = handle;
					break;
				}
			}
			if (visibleTab == null || !closeOthers) {
				if (visibleTab != null) {
					driver.switchTo().window(visibleTab);
				}
				return;
			}
			for (String handle : driver.getWindowHandles()) {
				if (!handle.equals(visibleTab)) {
					driver.switchTo().window(handle);
					driver.close();
				}
			}
			driver.switchTo().window(visibleTab);
		} catch (Exception e) {
			logger.warn("Could not select visible Chrome tab: {}", e.getMessage());
		}
	}

	private static final By WEB_PASSWORD_LINK = By
			.xpath("//a[normalize-space()='Password' or contains(@href,'/account/password')]");
	private static final By WEB_SAVE_BUTTON = By
			.xpath("//button[normalize-space()='Save'] | //input[@type='submit' and @value='Save']");
	private static final By WEB_PASSWORD_UPDATED = By
			.xpath("//*[contains(@class,'alert-success')] | //*[contains(text(),'password has been updated')]");
	private static final By WEB_SIGN_OUT = By.xpath("//a[normalize-space()='Sign Out']");

	private void signOutIfStillLoggedIn() {
		try {
			By loginTitle = By.cssSelector("#kc-page-title");
			new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions
					.or(ExpectedConditions.presenceOfElementLocated(loginTitle),
							ExpectedConditions.presenceOfElementLocated(WEB_SIGN_OUT)));
			if (!driver.findElements(loginTitle).isEmpty()) {
				return;
			}
			logger.info("Keycloak session still active, signing out first.");
			logWebPage();
			signOutInWeb();
		} catch (Exception e) {
			logger.warn("Keycloak sign-out check skipped: {}", e.getMessage());
			logWebPage();
		}
	}

	private WebElement findInWeb(By locator, int seconds) {
		try {
			String webCtx = findWebViewContext(Duration.ofSeconds(3));
			if (webCtx == null) {
				return null;
			}
			((SupportsContextSwitching) driver).context(webCtx);
			switchToVisibleTab(false);
			return new WebDriverWait(driver, Duration.ofSeconds(seconds))
					.until(ExpectedConditions.visibilityOfElementLocated(locator));
		} catch (Exception e) {
			logger.info("Not found in web view: {} ({})", locator, firstLine(e));
			return null;
		}
	}

	private boolean typeInWeb(String cssSelector, String text) {
		WebElement field = findPresentInWeb(By.cssSelector(cssSelector), 15);
		if (field == null) {
			logWebPage();
			return false;
		}
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", field);
		field.clear();
		field.sendKeys(text);
		return true;
	}

	private WebElement findPresentInWeb(By locator, int seconds) {
		try {
			String webCtx = findWebViewContext(Duration.ofSeconds(3));
			if (webCtx == null) {
				return null;
			}
			((SupportsContextSwitching) driver).context(webCtx);
			switchToVisibleTab(false);
			return new WebDriverWait(driver, Duration.ofSeconds(seconds))
					.until(ExpectedConditions.presenceOfElementLocated(locator));
		} catch (Exception e) {
			logger.info("Not present in web view: {} ({})", locator, firstLine(e));
			return null;
		}
	}

	private static String firstLine(Exception e) {
		String msg = String.valueOf(e.getMessage());
		int nl = msg.indexOf('\n');
		return e.getClass().getSimpleName() + ": " + (nl > 0 ? msg.substring(0, nl) : msg);
	}

	// Keycloak URLs can carry state / id_token_hint in the query string: never log it
	private static String pathOnly(String url) {
		if (url == null) {
			return null;
		}
		int cut = url.length();
		for (char c : new char[] { '?', '#' }) {
			int i = url.indexOf(c);
			if (i >= 0 && i < cut) {
				cut = i;
			}
		}
		return url.substring(0, cut);
	}

	private void logWebPage() {
		try {
			logger.debug("Keycloak web view path: {} | title: {}", pathOnly(driver.getCurrentUrl()), driver.getTitle());
		} catch (Exception ignored) {
		}
	}

	public boolean openKeycloakPassword() {
		for (int attempt = 1; attempt <= 3; attempt++) {
			if (isPasswordPageOpen(3)) {
				return true;
			}
			if (findInWeb(WEB_PASSWORD_LINK, 10) != null) {
				return true;
			}
			logger.info("Keycloak account page not ready, attempt {}", attempt);
			logWebPage();
		}

		scrollToTopSafe();
		By nativePwd = By.xpath("//*[@text='Password']");
		try {
			scrollHorizontallyUntilVisible(nativePwd);
			((SupportsContextSwitching) driver).context("NATIVE_APP");
			new WebDriverWait(driver, Duration.ofSeconds(8))
					.until(ExpectedConditions.visibilityOfElementLocated(nativePwd));
			return true;
		} catch (Exception e) {
			logger.info("Password not found in native.");
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

	By usernameTextBox1 = By.id("username");

	public void enterUserName(String username) {
		sendKeys(usernameTextBox1, username);
	}

	By passwordTextBox1 = By.id("password");

	public void enterPassword(String password) {
		sendKeys(passwordTextBox1, password);
	}

	public void clickOnLoginButton() {
		clickOnElement(loginButton);
	}

	public void clickOnPasswordOption() {
		if (isPasswordPageOpen(2)) {
			return;
		}
		WebElement link = findInWeb(WEB_PASSWORD_LINK, 10);
		if (link == null) {
			logWebPage();
			switchToNativeContext();
			scrollHorizontallyUntilVisible(passwordOption);
			clickOnElement(passwordOption);
			return;
		}

		String passwordPageUrl = link.getAttribute("href");
		logger.debug("Keycloak password link path: {}", pathOnly(passwordPageUrl));
		JavascriptExecutor js = (JavascriptExecutor) driver;

		// 1. Normal click
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", link);
		link.click();
		if (isPasswordPageOpen(10)) {
			return;
		}

		// 2. JavaScript click
		logger.info("Password link click did not open the page, trying JavaScript click.");
		try {
			js.executeScript("arguments[0].click();", driver.findElement(WEB_PASSWORD_LINK));
		} catch (Exception e) {
			logger.warn("JavaScript click failed: {}", e.getMessage());
		}
		if (isPasswordPageOpen(10)) {
			return;
		}

		// 3. Load the link's address
		if (passwordPageUrl != null) {
			logger.info("Opening Keycloak password page directly.");
			logger.debug("Keycloak password page path: {}", pathOnly(passwordPageUrl));
			js.executeScript("window.location.href = arguments[0];", passwordPageUrl);
			if (isPasswordPageOpen(15)) {
				return;
			}
		}
		logWebPage();
		throw new RuntimeException("Keycloak password page did not open after clicking Password");
	}

	private boolean isPasswordPageOpen(int seconds) {
		boolean open = findPresentInWeb(By.cssSelector("#password-new"), seconds) != null;
		if (open) {
			logger.info("Keycloak password page opened.");
		}
		return open;
	}

	public void enterExistPassword(String password) {
		if (!typeInWeb("#password", password)) {
			switchToNativeContext();
			sendKeysToTextBox(passwordField, password);
		}
	}

	public void enterNewPassword(String password) {
		if (!typeInWeb("#password-new", password)) {
			switchToNativeContext();
			clickAndsendKeysToTextBox(newPasswordField, password);
		}
	}

	public void enterConfirmPassword(String password) {
		if (!typeInWeb("#password-confirm", password)) {
			switchToNativeContext();
			clickAndsendKeysToTextBox(confirmPasswordField, password);
		}
	}

	public void clickOnSaveButton() {
		WebElement save = findInWeb(WEB_SAVE_BUTTON, 5);
		if (save == null) {
			switchToNativeContext();
			clickOnElement(saveButton);
			return;
		}
		JavascriptExecutor js = (JavascriptExecutor) driver;

		// 1. Normal click
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", save);
		save.click();
		if (isKeycloakMessageShown(10)) {
			return;
		}

		// 2. JavaScript click
		logger.info("Save click did not submit, trying JavaScript click.");
		try {
			js.executeScript("arguments[0].click();", driver.findElement(WEB_SAVE_BUTTON));
		} catch (Exception e) {
			logger.warn("JavaScript click on Save failed: {}", e.getMessage());
		}
		if (isKeycloakMessageShown(10)) {
			return;
		}

		// 3. Submit the form, keeping Save as the submitter (sends submitAction=Save)
		logger.info("Submitting Keycloak password form directly.");
		try {
			js.executeScript("var b = arguments[0]; if (b.form.requestSubmit) { b.form.requestSubmit(b); } else { b.form.submit(); }",
					driver.findElement(WEB_SAVE_BUTTON));
		} catch (Exception e) {
			logger.warn("Form submit failed: {}", e.getMessage());
		}
		if (!isKeycloakMessageShown(15)) {
			logWebPage();
			throw new RuntimeException("Keycloak password form was not submitted after clicking Save");
		}
	}

	// Keycloak shows the result (success or error) in an .alert box
	private boolean isKeycloakMessageShown(int seconds) {
		WebElement alert = findPresentInWeb(By.cssSelector(".alert"), seconds);
		if (alert == null) {
			return false;
		}
		logger.debug("Keycloak message: {}", alert.getText());
		return true;
	}

	public boolean isPasswordUpdatedMessageDisplayed() {
		if (findInWeb(WEB_PASSWORD_UPDATED, 20) != null) {
			return true;
		}
		logWebPage();
		isKeycloakMessageShown(1);
		switchToNativeContext();
		return isElementDisplayed(passwordUpdatedMessage);
	}

	public void clickOnSignoutButton() {
		Boolean signedOut = signOutInWeb();
		if (signedOut != null) {
			if (!signedOut) {
				throw new RuntimeException("Keycloak login page not shown after sign out");
			}
			switchToNativeContext();
			return;
		}

		switchToNativeContext();
		scrollToTop();

		dismissFormResubmissionDialogIfPresent();

		clickOnElement(signoutButton);
		waitForNativeLoginForm();
	}

	private Boolean signOutInWeb() {
		WebElement signOut = findInWeb(WEB_SIGN_OUT, 5);
		if (signOut == null) {
			return null;
		}
		String signOutUrl = signOut.getAttribute("href");
		logger.debug("Keycloak sign out link path: {}", pathOnly(signOutUrl));
		JavascriptExecutor js = (JavascriptExecutor) driver;

		// 1. Normal click
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", signOut);
		signOut.click();
		if (isLoginPageBack(10)) {
			return true;
		}

		// 2. JavaScript click
		logger.info("Sign Out click did not sign out, trying JavaScript click.");
		try {
			js.executeScript("arguments[0].click();", driver.findElement(WEB_SIGN_OUT));
		} catch (Exception e) {
			logger.warn("JavaScript click on Sign Out failed: {}", firstLine(e));
		}
		if (isLoginPageBack(10)) {
			return true;
		}

		// 3. Load the link's address
		if (signOutUrl != null) {
			logger.info("Opening Keycloak sign out address directly.");
			js.executeScript("window.location.href = arguments[0];", signOutUrl);
			if (isLoginPageBack(15)) {
				return true;
			}
		}
		logWebPage();
		return false;
	}

	// Newer Keycloak shows a "Do you want to log out?" page first; confirm it if shown
	private boolean isLoginPageBack(int seconds) {
		By username = By.cssSelector("#username");
		By logoutConfirm = By.cssSelector("#kc-logout");
		try {
			new WebDriverWait(driver, Duration.ofSeconds(seconds)).until(ExpectedConditions.or(
					ExpectedConditions.presenceOfElementLocated(username),
					ExpectedConditions.presenceOfElementLocated(logoutConfirm)));
			if (driver.findElements(username).isEmpty()) {
				logger.info("Confirming Keycloak logout.");
				((JavascriptExecutor) driver).executeScript("arguments[0].click();",
						driver.findElement(logoutConfirm));
				new WebDriverWait(driver, Duration.ofSeconds(15))
						.until(ExpectedConditions.presenceOfElementLocated(username));
			}
			logger.info("Keycloak login page is back after sign out.");
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void dismissFormResubmissionDialogIfPresent() {
		if (isElementDisplayed(formResubmissionCancelButton, 3)) {
			clickOnElement(formResubmissionCancelButton);
		}
	}

	public void resumeArcApplication() {
		closeChrome();
		openArcApplication();
	}

	private void closeChrome() {
		try {
			switchToNativeContext();
			((AndroidDriver) driver).terminateApp("com.android.chrome");
		} catch (Exception e) {
			logger.warn("Could not terminate Chrome: {}", e.getMessage());
		}
	}

	public void openKeycloakPage() {
		clearStaleKeycloakSession();
		openKeycloakWebView();
		waitForLoginPage();
	}

	// Chrome may reopen a tab left over from a previous run that is still signed in
	private void clearStaleKeycloakSession() {
		try {
			switchToNativeContext();
			dismissFormResubmissionDialogIfPresent();
			if (isElementDisplayed(signoutButton, 5)) {
				logger.info("Keycloak session still active, signing out first.");
				clickOnElement(signoutButton);
				waitForNativeLoginForm();
			}
		} catch (Exception e) {
			logger.warn("Stale Keycloak session check skipped: {}", e.getMessage());
		}
	}

	private void waitForNativeLoginForm() {
		try {
			new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//android.widget.EditText[@resource-id='username']")));
		} catch (Exception e) {
			throw new RuntimeException("Keycloak login page not shown after sign out", e);
		}
	}

	public void waitForLoginPage() {
		for (int i = 0; i < 15; i++) {
			try {
				driver.findElement(By.id("username"));
				return;
			} catch (Exception e) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ignored) {
				}
			}
		}
		throw new RuntimeException("Login page not loaded");
	}

}
