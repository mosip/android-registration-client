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
import regclient.api.FetchUiSpec;
import regclient.page.ApplicantBiometricsPage;
import regclient.page.AutoLogoutPage;
import regclient.page.BiometricDetailsPage;
import regclient.page.IntroducerBiometricPage;
import regclient.page.PreviewPage;
import regclient.page.RegistrationTasksPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.budgets.model.Notification;

public class BiometricDetailsPageEnglish extends BiometricDetailsPage {

	@AndroidFindBy(accessibility = "Iris")
	private WebElement irisScanIcon;

	@AndroidFindBy(accessibility = "Face")
	private WebElement faceScanIcon;

	@AndroidFindBy(accessibility = "Thumbs")
	private WebElement thumbsScanIcon;

	@AndroidFindBy(accessibility = "Left Hand")
	private WebElement leftHandScanIcon;

	@AndroidFindBy(accessibility = "Right Hand")
	private WebElement rightHandScanIcon;

	@AndroidFindBy(accessibility = "Exception")
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
		scrollUntilElementVisible(irisScanIcon);
		clickOnElement(irisScanIcon);
		return new ApplicantBiometricsPageEnglish(driver);
	}

	public ApplicantBiometricsPage clickOnRightHandScanIcon() {
		scrollUntilElementVisible(rightHandScanIcon);
		clickOnElement(rightHandScanIcon);
		return new ApplicantBiometricsPageEnglish(driver);
	}

	public ApplicantBiometricsPage clickOnLeftHandScanIcon() {
		scrollUntilElementVisible(leftHandScanIcon);
		clickOnElement(leftHandScanIcon);
		return new ApplicantBiometricsPageEnglish(driver);
	}

	public ApplicantBiometricsPage clickOnThumbsScanIcon() {
		scrollUntilElementVisible(thumbsScanIcon);
		clickOnElement(thumbsScanIcon);
		return new ApplicantBiometricsPageEnglish(driver);
	}

	public ApplicantBiometricsPage clickOnFaceScanIcon() {
		scrollUntilElementVisible(faceScanIcon);
		clickOnElement(faceScanIcon);
		return new ApplicantBiometricsPageEnglish(driver);
	}

	public ApplicantBiometricsPage clickOnExceptionScanIcon() {
		scrollUntilElementVisible(exceptionScanIcon);
		clickOnElement(exceptionScanIcon);
		return new ApplicantBiometricsPageEnglish(driver);
	}

	public IntroducerBiometricPage clickOnIntroducerIrisScan() {
		clickOnElement(findElementWithRetry(By.xpath(
				"//android.view.View[contains(@content-desc,\"" + FetchUiSpec.getValueUsingId("introducerBiometrics")
						+ "\")]/following-sibling::android.view.View//android.view.View[@content-desc='Iris']")));
		return new IntroducerBiometricPageEnglish(driver);
	}

	public IntroducerBiometricPage clickOnIntroducerRightHandScan() {
		clickOnElement(findElementWithRetry(By.xpath(
				"//android.view.View[contains(@content-desc,\"" + FetchUiSpec.getValueUsingId("introducerBiometrics")
						+ "\")]/following-sibling::android.view.View//android.view.View[@content-desc='Right Hand']")));
		return new IntroducerBiometricPageEnglish(driver);
	}

	public IntroducerBiometricPage clickOnIntroducerLeftHandScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Left Hand\"]")));
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
		logger.info(emailId);
	    String additionalInfoReqId = waitForAdditionalReqId(emailId, 20, 10) + "-BIOMETRIC_CORRECTION-1";

	  
	    try {
			if (typeAndVerify(additionalInfoRequestIdTextbox, additionalInfoReqId)) {
				logger.info("typeAndVerify succeeded.");
				return; // SUCCESS → exit method
			} else {
				throw new AssertionError("Textbox did not accept the id: " + additionalInfoReqId);
			}
		} catch (Exception e) {
			throw new AssertionError("Failed while typing/verifying finalId: " + additionalInfoReqId, e);
		}

	    // continue logic if valid
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

	public boolean isBiometricDetailsPageDisplayedForCorrection() {
		return isDisplayedForCorrectionByLabel("individualBiometrics", "Applicant Biometrics");
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
