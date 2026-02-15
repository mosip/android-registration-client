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

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Iris\"))")
	private WebElement irisScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Main droite\"))")
	private WebElement rightHandScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Main gauche\"))")
	private WebElement leftHandScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Pouces ANALYSE\"))")
	private WebElement thumbsScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Visage ANALYSE\"))")
	private WebElement faceScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Exception\"))")
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
		clickOnElement(irisScanIcon);
		return new ApplicantBiometricsPageFrench(driver);
	}

	public ApplicantBiometricsPage clickOnRightHandScanIcon() {
		clickOnElement(rightHandScanIcon);
		return new ApplicantBiometricsPageFrench(driver);
	}

	public ApplicantBiometricsPage clickOnLeftHandScanIcon() {
		clickOnElement(leftHandScanIcon);
		return new ApplicantBiometricsPageFrench(driver);
	}

	public ApplicantBiometricsPage clickOnThumbsScanIcon() {
		clickOnElement(thumbsScanIcon);
		return new ApplicantBiometricsPageFrench(driver);
	}

	public ApplicantBiometricsPage clickOnFaceScanIcon() {
		clickOnElement(faceScanIcon);
		return new ApplicantBiometricsPageFrench(driver);
	}

	public ApplicantBiometricsPage clickOnExceptionScanIcon() {
		clickOnElement(exceptionScanIcon);
		return new ApplicantBiometricsPageFrench(driver);

	}

	public IntroducerBiometricPage clickOnIntroducerIrisScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Iris ANALYSE\"]")));
		return new IntroducerBiometricPageFrench(driver);

	}

	public IntroducerBiometricPage clickOnIntroducerRightHandScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Main droite ANALYSE\"]")));
		return new IntroducerBiometricPageFrench(driver);

	}

	public IntroducerBiometricPage clickOnIntroducerLeftHandScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Main gauche ANALYSE\"]")));
		return new IntroducerBiometricPageFrench(driver);

	}

	public IntroducerBiometricPage clickOnIntroducerThumbScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Pouces ANALYSE\"]")));
		return new IntroducerBiometricPageFrench(driver);

	}

	public IntroducerBiometricPage clickOnIntroducerFaceScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Visage ANALYSE\"]")));
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
		return new RegistrationTasksPageArabic(driver);
	}

	private void typeAndVerify(WebElement el, String value) {
		el.click();
		el.clear();
		el.sendKeys(value);
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
				id = OTPListener.getAdditionalReqId(emailId.trim().toLowerCase());
				logger.info("Polled raw id = [" + id + "]");
			} catch (Exception e) {
				logger.info("Error fetching id: " + e.getMessage());
			}

			if (id != null) {
				id = id.trim();
				if (!id.isEmpty()) {
					String finalId = id.endsWith(SUFFIX) ? id : id + SUFFIX;
					logger.info("Using finalId = " + finalId);
					try {
						typeAndVerify(additionalInfoRequestIdTextbox, finalId);
						logger.info("Successfully entered: " + finalId);
						return;
					} catch (Exception e) {
						throw new AssertionError("Failed while typing finalId: " + finalId + " : " + e.getMessage(), e);
					}
				}
			}

			try {
				WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
				shortWait.until(ExpectedConditions.visibilityOf(autoLogoutPopup));
				logger.info("Auto logout popup detected. Clicking Stay Logged In.");
				clickOnStayLoggedInButton();
			} catch (Exception ignored) {

			}

			long elapsed = System.currentTimeMillis() - startMs;
			long remainingMs = Math.max(0, timeoutMs - elapsed);

			logger.info("ID not found. Elapsed " + (elapsed / 1000) + "s, remaining " + (remainingMs / 1000)
					+ "s. Sleeping " + pollIntervalSeconds + "s.");

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

	private static final Logger logger = LoggerFactory.getLogger(BiometricDetailsPageFrench.class);

}
