package regclient.pages.arabic;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
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

	@AndroidFindBy(xpath = "//android.widget.EditText[contains(@hint, 'معرّف طلب المعلومات الإضافية')]")
	private WebElement additionalInfoRequestIdTextbox;

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
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Iris Scan\"]")));
		return new IntroducerBiometricPageArabic(driver);

	}

	public IntroducerBiometricPage clickOnIntroducerRightHandScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Right\"]")));
		return new IntroducerBiometricPageArabic(driver);

	}

	public IntroducerBiometricPage clickOnIntroducerLeftHandScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\""
				+ FetchUiSpec.getValueUsingId("introducerBiometrics")
				+ "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Left\"]")));
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

	public boolean isAdditionalInfoRequestIdTextboxDisplayed() {
		return isElementDisplayed(additionalInfoRequestIdTextbox);
	}

	public void enterAdditionalInfoUsingEmail(String emailId) {
		int retries = 20, waitSeconds = 10;
		final String SUFFIX = "-BIOMETRIC_CORRECTION-1";

		for (int i = 1; i <= retries; i++) {
			String id = OTPListener.getAdditionalReqId(emailId);
			if (id != null && !id.isEmpty() && !id.equals("{Failed}")) {
				String sanitized = id.trim().replaceAll("\\p{C}", "");
				String finalId = sanitized.endsWith(SUFFIX) ? sanitized : sanitized + SUFFIX;

				try {
					WebElement el = additionalInfoRequestIdTextbox;
					try {
						el.clear();
						el.sendKeys(finalId);
					} catch (Exception ignored) {
					}
					if (finalId.equals(el.getAttribute("value")))
						return;

					((JavascriptExecutor) driver).executeScript(
							"arguments[0].value=arguments[1];arguments[0].dispatchEvent(new Event('input',{bubbles:true}));",
							el, finalId);
					if (finalId.equals(el.getAttribute("value")))
						return;
				} catch (Exception e) {
					logger.error("Enter ID failed: ", e);
				}
				throw new RuntimeException("Textbox not accepting: " + finalId);
			}
			sleepSeconds(waitSeconds);
		}
		throw new RuntimeException("AdditionalInfoReqId not found after wait.");
	}

	private void sleepSeconds(int s) {
		try {
			Thread.sleep(s * 1000L);
		} catch (InterruptedException ignored) {
			Thread.currentThread().interrupt();
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(BiometricDetailsPageArabic.class);

}
