package regclient.pages.english;

import static org.testng.Assert.assertTrue;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.remote.SupportsContextSwitching;
import io.mosip.testrig.apirig.testrunner.OTPListener;
import regclient.api.FetchUiSpec;
import regclient.page.ApplicantBiometricsPage;
import regclient.page.AutoLogoutPage;
import regclient.page.BiometricDetailsPage;
import regclient.page.IntroducerBiometricPage;
import regclient.page.PreviewPage;
import regclient.page.RegistrationTasksPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiometricDetailsPageEnglish extends BiometricDetailsPage {

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Iris\"))")
	private WebElement irisScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Right\"))")
	private WebElement rightHandScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Left\"))")
	private WebElement leftHandScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Thumbs\"))")
	private WebElement thumbsScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Face\"))")
	private WebElement faceScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Exception Scan\"))")
	private WebElement exceptionScanIcon;

	@AndroidFindBy(accessibility = "CONTINUE")
	private WebElement continueButton;

	@AndroidFindBy(xpath = "//android.widget.ScrollView/android.view.View/android.view.View[2]")
	private WebElement menuOptionInSelecetedLanguage;

	@AndroidFindBy(xpath = "//android.widget.EditText[contains(@hint, 'Additional Info Request ID')]")
	private WebElement additionalInfoRequestIdTextbox;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"You have been idle\")")
	private WebElement autoLogoutPopup;

	@AndroidFindBy(accessibility = "LOG OUT")
	private WebElement logoutButton;

	@AndroidFindBy(accessibility = "STAY LOGGED IN")
	private WebElement stayLoggedInButton;

	public BiometricDetailsPageEnglish(AppiumDriver driver) {
		super(driver);
	}

	@SuppressWarnings("deprecation")
	public boolean isBiometricDetailsPageDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator(
				"new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\""
						+ FetchUiSpec.getValueUsingId("individualBiometrics") + "\"))")));
	}

	@SuppressWarnings("deprecation")
	public boolean isAuthenticationBiometricTitleDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator(
				"new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\""
						+ FetchUiSpec.getValueUsingId("individualAuthBiometrics") + "\"))")));
	}

	public ApplicantBiometricsPage clickOnIrisScan() {
		clickOnElement(irisScanIcon);
		return new ApplicantBiometricsPageEnglish(driver);
	}

	public ApplicantBiometricsPage clickOnRightHandScanIcon() {
		clickOnElement(rightHandScanIcon);
		return new ApplicantBiometricsPageEnglish(driver);
	}

	public ApplicantBiometricsPage clickOnLeftHandScanIcon() {

		WebElement leftHandScanIcon = driver.findElement(MobileBy
				.AndroidUIAutomator("new UiScrollable(new UiSelector().className(\"android.widget.ScrollView\"))"
						+ ".scrollIntoView(new UiSelector().description(\"Left Hand Scan\"))"));

		clickOnElement(leftHandScanIcon);
		return new ApplicantBiometricsPageEnglish(driver);
	}

	public ApplicantBiometricsPage clickOnThumbsScanIcon() {
		WebElement thumbsScanIcon = driver.findElement(MobileBy
				.AndroidUIAutomator("new UiScrollable(new UiSelector().className(\"android.widget.ScrollView\"))"
						+ ".scrollIntoView(new UiSelector().description(\"Thumbs Scan\"))"));
		clickOnElement(thumbsScanIcon);
		return new ApplicantBiometricsPageEnglish(driver);
	}

	public ApplicantBiometricsPage clickOnFaceScanIcon() {
		WebElement faceScanIcon = driver.findElement(MobileBy
				.AndroidUIAutomator("new UiScrollable(new UiSelector().className(\"android.widget.ScrollView\"))"
						+ ".scrollIntoView(new UiSelector().description(\"Face Scan\"))"));
		clickOnElement(faceScanIcon);
	    return new ApplicantBiometricsPageEnglish(driver);
	}


	public ApplicantBiometricsPage clickOnExceptionScanIcon() {
		clickOnElement(exceptionScanIcon);
		return new ApplicantBiometricsPageEnglish(driver);

	}

	public IntroducerBiometricPage clickOnIntroducerIrisScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Iris Scan\"]")));
		return new IntroducerBiometricPageEnglish(driver);

	}

	public IntroducerBiometricPage clickOnIntroducerRightHandScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Right\"]")));
		return new IntroducerBiometricPageEnglish(driver);

	}

	public IntroducerBiometricPage clickOnIntroducerLeftHandScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Left\"]")));
		return new IntroducerBiometricPageEnglish(driver);

	}

	public IntroducerBiometricPage clickOnIntroducerThumbScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Thumbs\"]")));
		return new IntroducerBiometricPageEnglish(driver);

	}

	public IntroducerBiometricPage clickOnIntroducerFaceScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Face\"]")));
		return new IntroducerBiometricPageEnglish(driver);

	}

	public PreviewPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new PreviewPageEnglish(driver);
	}

	public boolean isAdditionalInfoRequestIdTextboxDisplayed() {
		return isElementDisplayed(additionalInfoRequestIdTextbox);
	}

	public boolean isAutoLogoutPopupDisplayed() {
		try {
			WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
			shortWait.until(ExpectedConditions.visibilityOf(autoLogoutPopup));
			return true;
		} catch (TimeoutException e) {
			return false;
		}
	}

	public RegistrationTasksPage clickOnStayLoggedInButton() {
		clickOnElement(stayLoggedInButton);
		return new RegistrationTasksPageEnglish(driver);
	}

	public void enterAdditionalInfoUsingEmail(String emailId) {

		final int totalTimeoutMinutes = 15;
		final int pollIntervalSeconds = 20;
		final String SUFFIX = "-BIOMETRIC_CORRECTION-1";

		long startMs = System.currentTimeMillis();
		long timeoutMs = TimeUnit.MINUTES.toMillis(totalTimeoutMinutes);

		try {
			logger.info("Waiting 30 seconds for email delivery...");
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		while (System.currentTimeMillis() - startMs < timeoutMs) {

			String id = null;

			try {
				id = OTPListener.getAdditionalReqId(emailId);
			} catch (Exception e) {
				logger.info("OTPListener.getAdditionalReqId threw: {}", e.getMessage());
			}

			if (id != null) {

				String trimmedId = id.trim();

				if (!trimmedId.isEmpty() && !"[Failed]".equals(trimmedId)) {

					String finalId = trimmedId.endsWith(SUFFIX) ? trimmedId : trimmedId + SUFFIX;

					logger.info("Found id: {} -> finalId: {}", trimmedId, finalId);

					try {
						if (typeAndVerify(additionalInfoRequestIdTextbox, finalId)) {
							logger.info("typeAndVerify succeeded.");
							return; // SUCCESS → exit method
						} else {
							throw new AssertionError("Textbox did not accept the id: " + finalId);
						}
					} catch (Exception e) {
						throw new AssertionError("Failed while typing/verifying finalId: " + finalId, e);
					}
				}
			}

			// Handle auto logout popup (quick check version recommended)
			try {
				if (isAutoLogoutPopupDisplayed()) {
					logger.info("Auto-logout popup displayed — staying logged in.");
					clickOnStayLoggedInButton();
				}
			} catch (Exception ignored) {
			}

			long elapsed = System.currentTimeMillis() - startMs;
			long remainingMs = Math.max(0, timeoutMs - elapsed);

			logger.info("ID not found yet. Elapsed {}s, remaining {}s. Sleeping {}s.", elapsed / 1000,
					remainingMs / 1000, pollIntervalSeconds);

			try {
				Thread.sleep(TimeUnit.SECONDS.toMillis(pollIntervalSeconds));
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				throw new AssertionError("Interrupted while waiting for AdditionalInfoReqId", ie);
			}
		}

		throw new AssertionError(
				"AdditionalInfoReqId not found within " + totalTimeoutMinutes + " minutes for " + emailId);
	}

	private boolean typeAndVerify(WebElement el, String value) {
		el.click();
		el.clear();
		el.sendKeys(value);

		// read the visible/real value in a safe way
		String curr = readElementValue(el);

		// exact match (keeps your previous behavior)
		return value.equals(curr);
	}

	private String readElementValue(WebElement el) {
		try {
			String ctx = "";
			try {
				ctx = ((SupportsContextSwitching) driver).getContext();
			} catch (Exception ignored) {
			}

			if (ctx != null && ctx.toUpperCase().contains("WEBVIEW")) {
				String v = el.getAttribute("value");
				return v == null ? "" : v;
			}
		} catch (Exception ignored) {
		}

		try {
			String t = el.getText();
			if (t != null && !t.isEmpty())
				return t;
		} catch (Exception ignored) {
		}

		for (String attr : new String[] { "text", "content-desc", "name" }) {
			try {
				String v = el.getAttribute(attr);
				if (v != null && !v.isEmpty())
					return v;
			} catch (Exception ignored) {
			}
		}

		return "";
	}

	private static final Logger logger = LoggerFactory.getLogger(BiometricDetailsPageEnglish.class);

}
