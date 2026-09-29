
package regclient.page;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.SupportsContextSwitching;

public class KeycloakPage extends BasePage {

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

	// xpath, not id: a native By.id lookup makes Selenium remember "id" for all By.id
	// lookups, which the Chrome web view then rejects as an invalid locator
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

	// Chrome keeps tabs from earlier runs, and the web view context can attach to a
	// background tab. Use the tab the user sees and close the rest.
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
			System.out.println("Could not select visible Chrome tab: " + e.getMessage());
		}
	}

	// Chrome does not always expose the Keycloak page to the native (accessibility)
	// tree, so the account steps look in the web view first and fall back to native.
	private static final By WEB_PASSWORD_LINK = By
			.xpath("//a[normalize-space()='Password' or contains(@href,'/account/password')]");
	private static final By WEB_SAVE_BUTTON = By
			.xpath("//button[normalize-space()='Save'] | //input[@type='submit' and @value='Save']");
	private static final By WEB_PASSWORD_UPDATED = By
			.xpath("//*[contains(@class,'alert-success')] | //*[contains(text(),'password has been updated')]");
	private static final By WEB_SIGN_OUT = By.xpath("//a[normalize-space()='Sign Out']");

	// A run that stopped midway leaves Chrome signed in, so Keycloak opens the Account
	// page instead of the login page. Sign out first (web view: the native tree is empty).
	private void signOutIfStillLoggedIn() {
		try {
			By loginTitle = By.cssSelector("#kc-page-title");
			new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions
					.or(ExpectedConditions.presenceOfElementLocated(loginTitle),
							ExpectedConditions.presenceOfElementLocated(WEB_SIGN_OUT)));
			if (!driver.findElements(loginTitle).isEmpty()) {
				return;
			}
			System.out.println("Keycloak session still active, signing out first.");
			logWebPage();
			signOutInWeb();
		} catch (Exception e) {
			System.out.println("Keycloak sign-out check skipped: " + e.getMessage());
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
			System.out.println("Not found in web view: " + locator + " (" + firstLine(e) + ")");
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

	// Presence, not visibility: fields can be in the DOM but off screen or under the keyboard
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
			System.out.println("Not present in web view: " + locator + " (" + firstLine(e) + ")");
			return null;
		}
	}

	private static String firstLine(Exception e) {
		String msg = String.valueOf(e.getMessage());
		int nl = msg.indexOf('\n');
		return e.getClass().getSimpleName() + ": " + (nl > 0 ? msg.substring(0, nl) : msg);
	}

	private void logWebPage() {
		try {
			System.out.println("Keycloak web view URL: " + driver.getCurrentUrl() + " | title: " + driver.getTitle());
		} catch (Exception ignored) {
		}
	}

	public boolean openKeycloakPassword() {
		// Keycloak may open straight on the password page (it remembers the last account
		// page), and the page can reload after login, so retry a few times
		for (int attempt = 1; attempt <= 3; attempt++) {
			if (isPasswordPageOpen(3)) {
				return true;
			}
			if (findInWeb(WEB_PASSWORD_LINK, 10) != null) {
				return true;
			}
			System.out.println("Keycloak account page not ready, attempt " + attempt);
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
			System.out.println("Password not found in native.");
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
		System.out.println("Keycloak password link: " + passwordPageUrl);
		JavascriptExecutor js = (JavascriptExecutor) driver;

		// 1. Normal click
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", link);
		link.click();
		if (isPasswordPageOpen(10)) {
			return;
		}

		// 2. JavaScript click
		System.out.println("Password link click did not open the page, trying JavaScript click.");
		try {
			js.executeScript("arguments[0].click();", driver.findElement(WEB_PASSWORD_LINK));
		} catch (Exception e) {
			System.out.println("JavaScript click failed: " + e.getMessage());
		}
		if (isPasswordPageOpen(10)) {
			return;
		}

		// 3. Load the link's address
		if (passwordPageUrl != null) {
			System.out.println("Opening Keycloak password page directly: " + passwordPageUrl);
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
			System.out.println("Keycloak password page opened.");
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
		System.out.println("Save click did not submit, trying JavaScript click.");
		try {
			js.executeScript("arguments[0].click();", driver.findElement(WEB_SAVE_BUTTON));
		} catch (Exception e) {
			System.out.println("JavaScript click on Save failed: " + e.getMessage());
		}
		if (isKeycloakMessageShown(10)) {
			return;
		}

		// 3. Submit the form, keeping Save as the submitter (sends submitAction=Save)
		System.out.println("Submitting Keycloak password form directly.");
		try {
			js.executeScript("var b = arguments[0]; if (b.form.requestSubmit) { b.form.requestSubmit(b); } else { b.form.submit(); }",
					driver.findElement(WEB_SAVE_BUTTON));
		} catch (Exception e) {
			System.out.println("Form submit failed: " + e.getMessage());
		}
		isKeycloakMessageShown(15);
	}

	// Keycloak shows the result (success or error) in an .alert box
	private boolean isKeycloakMessageShown(int seconds) {
		WebElement alert = findPresentInWeb(By.cssSelector(".alert"), seconds);
		if (alert == null) {
			return false;
		}
		System.out.println("Keycloak message: " + alert.getText());
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

	// Returns null if there is no Sign Out link in the web view, otherwise whether the
	// login page came back. Tries a normal click, a JavaScript click, then the link's address.
	private Boolean signOutInWeb() {
		WebElement signOut = findInWeb(WEB_SIGN_OUT, 5);
		if (signOut == null) {
			return null;
		}
		String signOutUrl = signOut.getAttribute("href");
		System.out.println("Keycloak sign out link: " + signOutUrl);
		JavascriptExecutor js = (JavascriptExecutor) driver;

		// 1. Normal click
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", signOut);
		signOut.click();
		if (isLoginPageBack(10)) {
			return true;
		}

		// 2. JavaScript click
		System.out.println("Sign Out click did not sign out, trying JavaScript click.");
		try {
			js.executeScript("arguments[0].click();", driver.findElement(WEB_SIGN_OUT));
		} catch (Exception e) {
			System.out.println("JavaScript click on Sign Out failed: " + firstLine(e));
		}
		if (isLoginPageBack(10)) {
			return true;
		}

		// 3. Load the link's address
		if (signOutUrl != null) {
			System.out.println("Opening Keycloak sign out address directly: " + signOutUrl);
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
				System.out.println("Confirming Keycloak logout.");
				((JavascriptExecutor) driver).executeScript("arguments[0].click();",
						driver.findElement(logoutConfirm));
				new WebDriverWait(driver, Duration.ofSeconds(15))
						.until(ExpectedConditions.presenceOfElementLocated(username));
			}
			System.out.println("Keycloak login page is back after sign out.");
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
			System.out.println("Could not terminate Chrome: " + e.getMessage());
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
				System.out.println("Keycloak session still active, signing out first.");
				clickOnElement(signoutButton);
				waitForNativeLoginForm();
			}
		} catch (Exception e) {
			System.out.println("Stale Keycloak session check skipped: " + e.getMessage());
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
