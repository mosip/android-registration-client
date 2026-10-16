
package regclient.page;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
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

	@FindBy(xpath = "//android.widget.Button[@resource-id='com.android.chrome:id/negative_button']")
	private WebElement formResubmissionCancelButton;

	@FindBy(xpath = "//android.widget.EditText[@resource-id='username']")
	private WebElement nativeUsernameField;

	@FindBy(css = "#kc-page-title")
	private WebElement webLoginTitle;

	@FindBy(css = "#username")
	private WebElement webUsernameField;

	@FindBy(xpath = "//a[normalize-space()='Password' or contains(@href,'/account/password')]")
	private WebElement webPasswordLink;

	@FindBy(css = "#password")
	private WebElement webPasswordField;

	@FindBy(css = "#password-new")
	private WebElement webNewPasswordField;

	@FindBy(css = "#password-confirm")
	private WebElement webConfirmPasswordField;

	@FindBy(xpath = "//button[normalize-space()='Save'] | //input[@type='submit' and @value='Save']")
	private WebElement webSaveButton;

	@FindBy(css = ".alert")
	private WebElement webAlert;

	@FindBy(xpath = "//div[contains(@class,'alert-success')] | //span[contains(text(),'password has been updated')]")
	private WebElement webPasswordUpdatedMessage;

	@FindBy(xpath = "//a[normalize-space()='Sign Out']")
	private WebElement webSignOutLink;

	@FindBy(css = "#kc-logout")
	private WebElement webLogoutConfirmButton;

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

		WebElement title = isInWebView() ? webLoginTitle : keycloakPageTitle;
		retryFindElement(title, Duration.ofSeconds(10));
		return isElementDisplayed(title);
	}

	private boolean isInWebView() {
		try {
			return String.valueOf(((SupportsContextSwitching) driver).getContext()).startsWith("WEBVIEW");
		} catch (Exception e) {
			return false;
		}
	}

	private void switchToVisibleTabAndCloseOthers() {
		switchToVisibleTab(true);
	}

	// Picks the tab by URL, never by running JavaScript: Chrome freezes background tabs, and a script
	// sent to a frozen tab blocks ChromeDriver for its full 240s timeout
	private void switchToVisibleTab(boolean closeOthers) {
		try {
			List<String> handles = new ArrayList<>(driver.getWindowHandles());
			if (handles.isEmpty()) {
				return;
			}
			String keycloakTab = null;
			String fallbackTab = null;
			for (String handle : handles) {
				driver.switchTo().window(handle);
				String url = String.valueOf(driver.getCurrentUrl());
				if (url.contains("/auth/") || url.contains("/realms/")) {
					keycloakTab = handle;
				} else if (fallbackTab == null && !url.startsWith("about:")) {
					fallbackTab = handle;
				}
			}
			String chosen = keycloakTab != null ? keycloakTab : fallbackTab != null ? fallbackTab : handles.get(0);
			if (closeOthers) {
				for (String handle : handles) {
					if (!handle.equals(chosen)) {
						closeTab(handle);
					}
				}
			}
			driver.switchTo().window(chosen);
			logger.info("Using Chrome tab {} of {}", handles.indexOf(chosen) + 1, handles.size());
		} catch (Exception e) {
			logger.warn("Could not select Chrome tab: {}", firstLine(e));
		}
	}

	private void closeTab(String handle) {
		try {
			driver.switchTo().window(handle);
			driver.close();
		} catch (Exception e) {
			logger.debug("Could not close Chrome tab: {}", firstLine(e));
		}
	}

	// Closes every Chrome tab so the next run starts with only the tab the app opens
	private void closeAllChromeTabs() {
		try {
			String webCtx = findWebViewContext(Duration.ofSeconds(3));
			if (webCtx != null) {
				((SupportsContextSwitching) driver).context(webCtx);
				for (String handle : new ArrayList<>(driver.getWindowHandles())) {
					closeTab(handle);
				}
			}
		} catch (Exception e) {
			logger.debug("Could not close Chrome tabs: {}", firstLine(e));
		}
	}

	private void signOutIfStillLoggedIn() {
		try {
			new WebDriverWait(driver, Duration.ofSeconds(20))
					.until(d -> isPresent(webLoginTitle) || isPresent(webSignOutLink));
			if (isPresent(webLoginTitle)) {
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

	private boolean switchToKeycloakWebView() {
		String webCtx = findWebViewContext(Duration.ofSeconds(3));
		if (webCtx == null) {
			return false;
		}
		((SupportsContextSwitching) driver).context(webCtx);
		switchToVisibleTab(false);
		return true;
	}

	// PageFactory re-locates the element on every call, so this reflects the current page
	private static boolean isPresent(WebElement element) {
		try {
			element.getTagName();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private WebElement findInWeb(WebElement element, String name, int seconds) {
		try {
			if (!switchToKeycloakWebView()) {
				return null;
			}
			return new WebDriverWait(driver, Duration.ofSeconds(seconds))
					.until(ExpectedConditions.visibilityOf(element));
		} catch (Exception e) {
			logger.info("Not found in web view: {} ({})", name, firstLine(e));
			return null;
		}
	}

	private boolean typeInWeb(WebElement element, String name, String text) {
		WebElement field = findPresentInWeb(element, name, 15);
		if (field == null) {
			logWebPage();
			return false;
		}
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", field);
		field.clear();
		field.sendKeys(text);
		return true;
	}

	private WebElement findPresentInWeb(WebElement element, String name, int seconds) {
		try {
			if (!switchToKeycloakWebView()) {
				return null;
			}
			return new WebDriverWait(driver, Duration.ofSeconds(seconds))
					.until(d -> isPresent(element) ? element : null);
		} catch (Exception e) {
			logger.info("Not present in web view: {} ({})", name, firstLine(e));
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
			if (findInWeb(webPasswordLink, "Password link", 10) != null) {
				return true;
			}
			logger.info("Keycloak account page not ready, attempt {}", attempt);
			logWebPage();
		}

		scrollToTopSafe();
		By nativePwd = By.xpath("//android.widget.TextView[@text='Password']");
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
		return (isInWebView() ? webLoginTitle : keycloakPageTitle).getText();
	}

	public void clickOnLanguageDropdown() {
		clickOnElement(languageDropdown);
	}

	public void clickOnEnglishLanguage() {
		clickOnElement(englishLanguage);
	}

	By usernameTextBox1 = By.id("username");

	public void enterUserName(String username) {
		typeInLoginField("username", username);
	}

	By passwordTextBox1 = By.id("password");

	public void enterPassword(String password) {
		typeInLoginField("password", password);
	}

	private void typeInLoginField(String fieldId, String value) {
		if (isInWebView()) {
			try {
				sendKeys(By.cssSelector("#" + fieldId), value);
				return;
			} catch (Exception e) {
				logger.info("Webview entry for '{}' failed, falling back to native: {}", fieldId, e.getMessage());
				switchToNativeContext();
			}
		}
		// Chrome's EditText resource-id has no package prefix, so By.id may not match it; use XPath like nativeUsernameField
		sendKeys(By.xpath("//android.widget.EditText[@resource-id='" + fieldId + "']"), value);
	}

	public void clickOnLoginButton() {
		if (isInWebView()) {
			driver.findElement(By.cssSelector("#kc-login")).click();
		} else {
			clickOnElement(loginButton);
		}
	}

	public void clickOnPasswordOption() {
		if (isPasswordPageOpen(2)) {
			return;
		}
		WebElement link = findInWeb(webPasswordLink, "Password link", 10);
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
			js.executeScript("arguments[0].click();", webPasswordLink);
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
		boolean open = findPresentInWeb(webNewPasswordField, "New password field", seconds) != null;
		if (open) {
			logger.info("Keycloak password page opened.");
		}
		return open;
	}

	public void enterExistPassword(String password) {
		if (!typeInWeb(webPasswordField, "Password field", password)) {
			switchToNativeContext();
			sendKeysToTextBox(passwordField, password);
		}
	}

	public void enterNewPassword(String password) {
		if (!typeInWeb(webNewPasswordField, "New password field", password)) {
			switchToNativeContext();
			clickAndsendKeysToTextBox(newPasswordField, password);
		}
	}

	public void enterConfirmPassword(String password) {
		if (!typeInWeb(webConfirmPasswordField, "Confirm password field", password)) {
			switchToNativeContext();
			clickAndsendKeysToTextBox(confirmPasswordField, password);
		}
	}

	public void clickOnSaveButton() {
		WebElement save = findInWeb(webSaveButton, "Save button", 5);
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
			js.executeScript("arguments[0].click();", webSaveButton);
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
					webSaveButton);
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
		WebElement alert = findPresentInWeb(webAlert, "Keycloak message", seconds);
		if (alert == null) {
			return false;
		}
		logger.debug("Keycloak message: {}", alert.getText());
		return true;
	}

	public boolean isPasswordUpdatedMessageDisplayed() {
		if (findInWeb(webPasswordUpdatedMessage, "Password updated message", 20) != null) {
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
		WebElement signOut = findInWeb(webSignOutLink, "Sign Out link", 5);
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
			js.executeScript("arguments[0].click();", webSignOutLink);
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
		try {
			new WebDriverWait(driver, Duration.ofSeconds(seconds))
					.until(d -> isPresent(webUsernameField) || isPresent(webLogoutConfirmButton));
			if (!isPresent(webUsernameField)) {
				logger.info("Confirming Keycloak logout.");
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", webLogoutConfirmButton);
				new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> isPresent(webUsernameField));
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
		// clearChromeTabs() closes the tabs itself (via closeChrome()), so no separate pass is needed
		clearChromeTabs();
	}

	private static final String CHROME_PACKAGE = "com.android.chrome";

	public void clearChromeTabs() {
		AndroidDriver android = (AndroidDriver) driver;
		try {
			switchToNativeContext();
			android.activateApp(CHROME_PACKAGE);
			waitTime(2);
			if (closeAllTabsFromTabSwitcher()) {
				logger.info("Closed all Chrome tabs.");
			} else {
				logger.warn("Could not close Chrome tabs from the tab switcher.");
			}
		} catch (Exception e) {
			logger.warn("Chrome tab cleanup skipped: {}", firstLine(e));
		} finally {
			closeChrome();
			openArcApplication();
		}
	}

	private boolean closeAllTabsFromTabSwitcher() {
		if (!clickIfShown(By.id("com.android.chrome:id/tab_switcher_button"), 5)) {
			return false;
		}
		waitTime(2);
		By closeAll = MobileBy.AndroidUIAutomator(
				"new UiSelector().textMatches(\"(?i).*close all tabs.*\")");
		By closeAllDesc = MobileBy.AndroidUIAutomator(
				"new UiSelector().descriptionMatches(\"(?i).*close all tabs.*\")");
		if (clickIfShown(By.id("com.android.chrome:id/menu_button"), 5)
				&& (clickIfShown(closeAll, 4) || clickIfShown(closeAllDesc, 1))) {
			if (!clickIfShown(By.id("com.android.chrome:id/positive_button"), 3)) {
				clickIfShown(MobileBy.AndroidUIAutomator(
						"new UiSelector().className(\"android.widget.Button\").textMatches(\"(?i)close( all)?.*\")"), 2);
			}
			return true;
		}
		saveChromePageSource();
		((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
		waitTime(1);
		return closeTabsOneByOne();
	}

	// Fallback: tap each tab card's close (X) button in the tab grid
	private boolean closeTabsOneByOne() {
		By tabClose = MobileBy.AndroidUIAutomator(
				"new UiSelector().packageName(\"com.android.chrome\").descriptionMatches(\"(?i)close .*tab.*\")");
		int closed = 0;
		for (int i = 0; i < 100; i++) {
			if (!clickIfShown(tabClose, i == 0 ? 3 : 1)) {
				break;
			}
			closed++;
		}
		logger.info("Closed {} Chrome tabs one by one", closed);
		return closed > 0;
	}

	private void saveChromePageSource() {
		try {
			java.nio.file.Path out = java.nio.file.Paths.get("testng-report", "chrome-tab-switcher-pagesource.xml");
			java.nio.file.Files.createDirectories(out.getParent());
			java.nio.file.Files.write(out, driver.getPageSource().getBytes(java.nio.charset.StandardCharsets.UTF_8));
			logger.info("Saved Chrome tab switcher page source to {}", out.toAbsolutePath());
		} catch (Exception e) {
			logger.warn("Could not save Chrome page source: {}", firstLine(e));
		}
	}

	private boolean clickIfShown(By locator, int seconds) {
		try {
			new WebDriverWait(driver, Duration.ofSeconds(seconds))
					.until(ExpectedConditions.visibilityOfElementLocated(locator)).click();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void closeChrome() {
		closeAllChromeTabs();
		try {
			switchToNativeContext();
			((AndroidDriver) driver).terminateApp(CHROME_PACKAGE);
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
			new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.visibilityOf(nativeUsernameField));
		} catch (Exception e) {
			throw new RuntimeException("Keycloak login page not shown after sign out", e);
		}
	}

	public void waitForLoginPage() {
		for (int i = 0; i < 15; i++) {
			try {
				driver.findElement(isInWebView() ? By.cssSelector("#username") : By.id("username"));
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
