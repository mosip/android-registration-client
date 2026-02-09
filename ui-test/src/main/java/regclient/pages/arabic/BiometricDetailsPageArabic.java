package regclient.pages.arabic;

import static org.junit.Assert.assertTrue;

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
import regclient.pages.english.RegistrationTasksPageEnglish;

public class BiometricDetailsPageArabic extends BiometricDetailsPage {

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"القزحية مسح\"))")
	private WebElement irisScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"اليد اليمنى مسح\"))")
	private WebElement rightHandScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"اليد اليسرى مسح\"))")
	private WebElement leftHandScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"الأبهام مسح\"))")
	private WebElement thumbsScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"الوجه مسح\"))")
	private WebElement faceScanIcon;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"استثناء مسح\"))")
	private WebElement exceptionScanIcon;

	@AndroidFindBy(accessibility = "يكمل")
	private WebElement continueButton;

	@AndroidFindBy(xpath = "//android.widget.EditText[contains(@hint, 'أدخل معرف طلب المعلومات الإضافية')]")
	private WebElement additionalInfoRequestIdTextbox;
	
	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"لقد كنت خاملاً\")")
	private WebElement autoLogoutPopup;
	
	@AndroidFindBy(accessibility = "البقاء مسجلاً الدخول")
	private WebElement stayLoggedInButton;

	public BiometricDetailsPageArabic(AppiumDriver driver) {
		super(driver);
	}

	@SuppressWarnings("deprecation")
	public boolean isBiometricDetailsPageDisplayed() {
		scrollToTop();
		return isElementDisplayed(
				findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""
						+ FetchUiSpec.getValueUsingId("individualBiometrics") + "\")")));
	}

	@SuppressWarnings("deprecation")
	public boolean isAuthenticationBiometricTitleDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator(
				"new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\""
						+ FetchUiSpec.getValueUsingId("individualAuthBiometrics") + "\"))")));
	}

	public ApplicantBiometricsPage clickOnIrisScan() {
		clickOnElement(irisScanIcon);
		return new ApplicantBiometricsPageArabic(driver);
	}

	public ApplicantBiometricsPage clickOnRightHandScanIcon() {
		clickOnElement(rightHandScanIcon);
		return new ApplicantBiometricsPageArabic(driver);
	}

	public ApplicantBiometricsPage clickOnLeftHandScanIcon() {
		clickOnElement(leftHandScanIcon);
		return new ApplicantBiometricsPageArabic(driver);
	}

	public ApplicantBiometricsPage clickOnThumbsScanIcon() {
		clickOnElement(thumbsScanIcon);
		return new ApplicantBiometricsPageArabic(driver);
	}

	public ApplicantBiometricsPage clickOnFaceScanIcon() {
		clickOnElement(faceScanIcon);
		return new ApplicantBiometricsPageArabic(driver);
	}

	public ApplicantBiometricsPage clickOnExceptionScanIcon() {
		clickOnElement(exceptionScanIcon);
		return new ApplicantBiometricsPageArabic(driver);

	}

	public IntroducerBiometricPage clickOnIntroducerIrisScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"القزحية مسح\"]")));
		return new IntroducerBiometricPageArabic(driver);

	}

	public IntroducerBiometricPage clickOnIntroducerRightHandScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"اليد اليمنى مسح\"]")));
		return new IntroducerBiometricPageArabic(driver);

	}

	public IntroducerBiometricPage clickOnIntroducerLeftHandScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"اليد اليسرى مسح\"]")));
		return new IntroducerBiometricPageArabic(driver);

	}

	public IntroducerBiometricPage clickOnIntroducerThumbScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"الأبهام مسح\"]")));
		return new IntroducerBiometricPageArabic(driver);

	}

	public IntroducerBiometricPage clickOnIntroducerFaceScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"الوجه مسح\"]")));
		return new IntroducerBiometricPageArabic(driver);

	}

	public PreviewPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new PreviewPageArabic(driver);
	}
	
	public RegistrationTasksPage clickOnStayLoggedInButton() {
		clickOnElement(stayLoggedInButton);
		return new RegistrationTasksPageArabic(driver);
	}

	public boolean isAdditionalInfoRequestIdTextboxDisplayed() {
		By additionalInfoRequestIdTextbox = MobileBy
				.AndroidUIAutomator("new UiSelector().className(\"android.widget.EditText\").instance(0)");
		return isElementDisplayed(additionalInfoRequestIdTextbox);
	}

	public void enterAdditionalInfoUsingEmail(String emailId) {
		final int totalTimeoutMinutes = 15; // stop after this many minutes
		final int pollIntervalSeconds = 10; // poll every N seconds
		final String SUFFIX = "-BIOMETRIC_CORRECTION-1";

		long startMs = System.currentTimeMillis();
		long timeoutMs = TimeUnit.MINUTES.toMillis(totalTimeoutMinutes);

		while (System.currentTimeMillis() - startMs < timeoutMs) {
			String id = null;
			try {
				id = OTPListener.getAdditionalReqId(emailId);
			} catch (Exception e) {
				// If getAdditionalReqId can throw, log and continue polling
				System.out.println("OTPListener.getAdditionalReqId threw: " + e.getMessage());
			}

			if (id != null && !id.isEmpty() && !"{Failed}".equals(id)) {
				String finalId = id.trim() + (id.endsWith(SUFFIX) ? "" : SUFFIX);
				System.out.println("Found id: " + id + " -> finalId: " + finalId);

				// typeAndVerify should return true on success; handle its failure/exception
				try {
				    typeAndVerify(additionalInfoRequestIdTextbox, finalId);
				    System.out.println("Entered finalId: " + finalId);
				    return; // success
				} catch (Exception e) {
				    throw new AssertionError(
				        "Failed while typing finalId: " + finalId + " : " + e.getMessage(), e);
				}

			}

			// handle auto logout popup
			try {
				if (isAutoLogoutPopupDisplayed()) {
					System.out.println("Auto-logout popup displayed — staying logged in.");
					clickOnStayLoggedInButton();
				}
			} catch (Exception ignored) {
			}

			// log remaining time
			long elapsed = System.currentTimeMillis() - startMs;
			long remainingMs = Math.max(0, timeoutMs - elapsed);
			System.out.println("ID not found yet. Elapsed " + (elapsed / 1000) + "s, remaining " + (remainingMs / 1000)
					+ "s. Sleeping " + pollIntervalSeconds + "s.");

			try {
				Thread.sleep(TimeUnit.SECONDS.toMillis(pollIntervalSeconds));
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				throw new AssertionError("Interrupted while waiting for AdditionalInfoReqId", ie);
			}
		}

		// If we reach here, timeout expired
		throw new AssertionError(
				"AdditionalInfoReqId not found within " + totalTimeoutMinutes + " minutes for " + emailId);
	}

	private void typeAndVerify(WebElement el, String value) {
	    el.click();
	    el.clear();
	    el.sendKeys(value);
	}
	public boolean isAutoLogoutPopupDisplayed() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofMinutes(10));
			wait.until(ExpectedConditions.visibilityOf(autoLogoutPopup));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(BiometricDetailsPageArabic.class);

	// Below comment-out code required in future
	/*
	 * public void handleBiometricDetails() {
	 * 
	 * List<String> biometricIds = FetchUiSpec.getAllIds("BiometricDetails");
	 * 
	 * for (String id : biometricIds) {
	 * 
	 * if (!FetchUiSpec.getRequiredTypeUsingId(id)) { continue; }
	 * 
	 * List<String> bioAttributes = FetchUiSpec.getBioAttributesUsingId(id);
	 * 
	 * for (String attribute : bioAttributes) { clickOnBiometric(attribute); } } }
	 * 
	 * public void clickOnBiometric(String attribute) {
	 * 
	 * String label = FetchUiSpec.getBiometricLabel(attribute);
	 * 
	 * By biometricTile = By.xpath("//android.widget.ImageView[@content-desc='" +
	 * label + "']");
	 * 
	 * while (!isElementDisplayed(biometricTile)) { swipeOrScroll(); }
	 * 
	 * clickOnElement(findElementWithRetry(biometricTile)); }
	 * 
	 * public void performBiometricCapture(String attribute) {
	 * 
	 * // 1️⃣ Click biometric tile (iris / finger / face – spec driven)
	 * clickOnBiometric(attribute);
	 * 
	 * ApplicantBiometricsPageArabic applicantBiometricsPage = new
	 * ApplicantBiometricsPageArabic(driver);
	 * 
	 * // 2️⃣ Validate biometric page opened assertTrue(
	 * applicantBiometricsPage.isApplicantBiometricsPageDisplayed(),
	 * "Verify applicant biometric page is displayed for " + attribute );
	 * 
	 * // 3️⃣ Click scan applicantBiometricsPage.clickOnScanButton();
	 * 
	 * // 4️⃣ Validate correct scan screen opened assertTrue(
	 * applicantBiometricsPage.isBiometricScan(attribute),
	 * "Verify biometric scan screen for " + attribute );
	 * 
	 * // 5️⃣ Close capture popup applicantBiometricsPage.closeScanCapturePopUp();
	 * 
	 * // 6️⃣ Navigate back to biometric details page
	 * applicantBiometricsPage.clickOnBackButton(); }
	 * 
	 * public boolean isBiometricScan(String attribute) {
	 * 
	 * String label = FetchUiSpec.getBiometricLabel(attribute);
	 * 
	 * return isElementDisplayed(
	 * By.xpath("//android.view.View[contains(@content-desc,'" + label + "')]") ); }
	 */

}
