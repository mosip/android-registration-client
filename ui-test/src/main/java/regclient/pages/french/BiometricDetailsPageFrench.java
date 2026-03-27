package regclient.pages.french;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.remote.SupportsContextSwitching;
import io.mosip.testrig.apirig.testrunner.OTPListener;
import regclient.api.FetchUiSpec;
import regclient.page.ApplicantBiometricsPage;
import regclient.page.BiometricDetailsPage;
import regclient.page.IntroducerBiometricPage;
import regclient.page.PreviewPage;
import regclient.page.RegistrationTasksPage;
import regclient.pages.arabic.BiometricDetailsPageArabic;
import regclient.pages.arabic.RegistrationTasksPageArabic;
import regclient.pages.english.ApplicantBiometricsPageEnglish;
import regclient.pages.english.BiometricDetailsPageEnglish;
import regclient.pages.english.IntroducerBiometricPageEnglish;
import regclient.pages.english.PreviewPageEnglish;

public class BiometricDetailsPageFrench extends BiometricDetailsPage {

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

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc,\"Introducteur BiomÃ©trie\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Iris ANALYSE\"]")
	private WebElement introducerIrisScanIcon;

	@AndroidFindBy(accessibility = "CONTINUER")
	private WebElement continueButton;

	@AndroidFindBy(xpath = "//android.widget.EditText[contains(@hint, \"Identifiant de la demande d'information supplémentaire\")]")
	private WebElement additionalInfoRequestIdTextbox;

	@AndroidFindBy(accessibility = "DÉCONNEXION")
	private WebElement stayLoggedInButton;

	@AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.view.View\").descriptionContains(\"Tu as été\")")
	private WebElement autoLogoutPopup;

	public BiometricDetailsPageFrench(AppiumDriver driver) {
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
		return new ApplicantBiometricsPageFrench(driver);
	}

	public ApplicantBiometricsPage clickOnRightHandScanIcon() {
		scrollUntilElementVisible(rightHandScanIcon);
		clickOnElement(rightHandScanIcon);
		return new ApplicantBiometricsPageFrench(driver);
	}

	public ApplicantBiometricsPage clickOnLeftHandScanIcon() {
		scrollUntilElementVisible(leftHandScanIcon);
		clickOnElement(leftHandScanIcon);
		return new ApplicantBiometricsPageFrench(driver);
	}

	public ApplicantBiometricsPage clickOnThumbsScanIcon() {
		scrollUntilElementVisible(thumbsScanIcon);
		clickOnElement(thumbsScanIcon);
		return new ApplicantBiometricsPageFrench(driver);
	}

	public ApplicantBiometricsPage clickOnFaceScanIcon() {
		scrollUntilElementVisible(faceScanIcon);
		clickOnElement(faceScanIcon);
		return new ApplicantBiometricsPageFrench(driver);
	}

	public ApplicantBiometricsPage clickOnExceptionScanIcon() {
		scrollUntilElementVisible(exceptionScanIcon);
		clickOnElement(exceptionScanIcon);
		return new ApplicantBiometricsPageFrench(driver);
	}

	public IntroducerBiometricPage clickOnIntroducerIrisScan() {
		clickOnElement(findElementWithRetry(By.xpath(
				"//android.view.View[contains(@content-desc,\"" + FetchUiSpec.getValueUsingId("introducerBiometrics")
						+ "\")]/following-sibling::android.view.View//android.view.View[@content-desc='Iris']")));
		return new IntroducerBiometricPageFrench(driver);
	}

	public IntroducerBiometricPage clickOnIntroducerRightHandScan() {
		clickOnElement(findElementWithRetry(By.xpath(
				"//android.view.View[contains(@content-desc,\"" + FetchUiSpec.getValueUsingId("introducerBiometrics")
						+ "\")]/following-sibling::android.view.View//android.view.View[@content-desc='Right Hand']")));
		return new IntroducerBiometricPageFrench(driver);
	}

	public IntroducerBiometricPage clickOnIntroducerLeftHandScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Left Hand\"]")));
		return new IntroducerBiometricPageFrench(driver);
	}

	public IntroducerBiometricPage clickOnIntroducerThumbScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Thumbs\"]")));
		return new IntroducerBiometricPageFrench(driver);
	}

	public IntroducerBiometricPage clickOnIntroducerFaceScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Face\"]")));
		return new IntroducerBiometricPageFrench(driver);
	}

	public PreviewPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new PreviewPageFrench(driver);
	}

	public boolean isAdditionalInfoRequestIdTextboxDisplayed() {
		return isElementDisplayed(additionalInfoRequestIdTextbox);
	}

	public RegistrationTasksPage clickOnStayLoggedInButton() {
		clickOnElement(stayLoggedInButton);
		return new RegistrationTasksPageFrench(driver);
	}

	public boolean isAutoLogoutPopupDisplayed() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
			wait.until(ExpectedConditions.visibilityOf(autoLogoutPopup));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void enterAdditionalInfoUsingEmail(String emailId) {
		logger.info(emailId);
	    String additionalInfoReqId = OTPListener.getAdditionalReqId(emailId);
	    if (additionalInfoReqId == null || additionalInfoReqId.trim().isEmpty()) {
	        throw new IllegalStateException("Additional Info Request ID is missing for email: " + emailId);
	    }
	    additionalInfoReqId = additionalInfoReqId + "-BIOMETRIC_CORRECTION-1";

	  
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

	}

	private boolean typeAndVerify(WebElement el, String value) {
	    el.click();
	    el.clear();
	    el.sendKeys(value);
	    waitTime(1);
	    String curr = readElementValue(el);
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

	private static final Logger logger = LoggerFactory.getLogger(BiometricDetailsPageFrench.class);

}
