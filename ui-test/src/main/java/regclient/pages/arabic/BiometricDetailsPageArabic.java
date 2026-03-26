package regclient.pages.arabic;


import java.time.Duration;

import org.openqa.selenium.By;
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

public class BiometricDetailsPageArabic extends BiometricDetailsPage {

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
		return new ApplicantBiometricsPageArabic(driver);
	}

	public ApplicantBiometricsPage clickOnRightHandScanIcon() {
		scrollUntilElementVisible(rightHandScanIcon);
		clickOnElement(rightHandScanIcon);
		return new ApplicantBiometricsPageArabic(driver);
	}

	public ApplicantBiometricsPage clickOnLeftHandScanIcon() {
		scrollUntilElementVisible(leftHandScanIcon);
		clickOnElement(leftHandScanIcon);
		return new ApplicantBiometricsPageArabic(driver);
	}

	public ApplicantBiometricsPage clickOnThumbsScanIcon() {
		scrollUntilElementVisible(thumbsScanIcon);
		clickOnElement(thumbsScanIcon);
		return new ApplicantBiometricsPageArabic(driver);
	}

	public ApplicantBiometricsPage clickOnFaceScanIcon() {
		scrollUntilElementVisible(faceScanIcon);
		clickOnElement(faceScanIcon);
		return new ApplicantBiometricsPageArabic(driver);
	}

	public ApplicantBiometricsPage clickOnExceptionScanIcon() {
		scrollUntilElementVisible(exceptionScanIcon);
		clickOnElement(exceptionScanIcon);
		return new ApplicantBiometricsPageArabic(driver);
	}

	public IntroducerBiometricPage clickOnIntroducerIrisScan() {
		clickOnElement(findElementWithRetry(By.xpath(
				"//android.view.View[contains(@content-desc,\"" + FetchUiSpec.getValueUsingId("introducerBiometrics")
						+ "\")]/following-sibling::android.view.View//android.view.View[@content-desc='Iris']")));
		return new IntroducerBiometricPageArabic(driver);
	}

	public IntroducerBiometricPage clickOnIntroducerRightHandScan() {
		clickOnElement(findElementWithRetry(By.xpath(
				"//android.view.View[contains(@content-desc,\"" + FetchUiSpec.getValueUsingId("introducerBiometrics")
						+ "\")]/following-sibling::android.view.View//android.view.View[@content-desc='Right Hand']")));
		return new IntroducerBiometricPageArabic(driver);
	}

	public IntroducerBiometricPage clickOnIntroducerLeftHandScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Left Hand\"]")));
		return new IntroducerBiometricPageArabic(driver);
	}

	public IntroducerBiometricPage clickOnIntroducerThumbScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Thumbs\"]")));
		return new IntroducerBiometricPageArabic(driver);
	}

	public IntroducerBiometricPage clickOnIntroducerFaceScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Face\"]")));
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
