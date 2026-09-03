package regclient.pages.arabic;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.api.FetchUiSpec;
import regclient.page.ApplicantBiometricsPage;
import regclient.page.BiometricDetailsPage;
import regclient.pages.english.BiometricDetailsPageEnglish;

public class ApplicantBiometricsPageArabic extends ApplicantBiometricsPage {

	@AndroidFindBy(accessibility = "القزحية مسح")
	private WebElement irisScanButton;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"القزحية مسح\"))")
	private WebElement irisScanButtonTitle;

	@AndroidFindBy(accessibility = "دائم")
	private WebElement permanentButton;

	@AndroidFindBy(accessibility = "مؤقت")
	private WebElement temporaryButton;

	@AndroidFindBy(accessibility = "وضع علامة استثناء")
	private WebElement markExceptionButton;

	@AndroidFindBy(accessibility = "وضع علامة استثناء")
	private WebElement exceptionTypeTitle;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(1)")
	private WebElement oneEyeException;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(2)")
	private WebElement firstFingureExceptionImage;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(1)")
	private WebElement secondFingureExceptionImage;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(3)")
	private WebElement thirdFingureExceptionImage;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(4)")
	private WebElement forthFingureExceptionImage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"اليد اليمنى مسح\"))")
	private WebElement rightHandScanTitle;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"اليد اليسرى مسح\"))")
	private WebElement leftHandScanTitle;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"الأبهام مسح\"))")
	private WebElement thumbsScanTitle;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"الوجه مسح\"))")
	private WebElement faceScanTitle;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"استثناء مسح\"))")
	private WebElement exceptionScanTitle;

	@AndroidFindBy(accessibility = "مسح")
	private WebElement scanButton;

	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Iris')]")
	private WebElement irisCapturerHeader;

	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'RightHand')]")
	private WebElement rightHandCapturerHeader;

	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'LeftHand')]")
	private WebElement leftHandCapturerHeader;

	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Thumbs')]")
	private WebElement thumbsCapturerHeader;

	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Face')]")
	private WebElement faceCapturerHeader;

	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Exception')]")
	private WebElement exceptionCapturerHeader;

	@AndroidFindBy(accessibility = "zoom_in_button")
	private WebElement zoomButton;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"التعليقات\")]/following-sibling::android.widget.EditText")
	private WebElement commentsTextBox;

	@AndroidFindBy(className = "android.widget.Button")
	private WebElement popUpCloseButton;

	@AndroidFindBy(accessibility = "مقبل")
	private WebElement nextButton;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"التعليقات\"))")
	private WebElement commentsHeader;

	@AndroidFindBy(xpath = "//android.view.View[@content-desc=\"الاستثناءات\"]/following-sibling::android.view.View[@content-desc=\"1\"]")
	private WebElement exceptionCount;

	@AndroidFindBy(accessibility = "menu_back_button")
	private WebElement biometricsMenuButton;

	public ApplicantBiometricsPageArabic(AppiumDriver driver) {
		super(driver);
	}

	public void enterCommentsInTextBox(String comments) {
		if (!isElementDisplayedOnScreen(commentsTextBox)) {
			swipeUp();
		}
		clickAndsendKeysToTextBox(commentsTextBox, comments);
	}

	public void clickOnScanButton() {
		scrollUntilElementVisible(scanButton);
		clickOnElement(scanButton);
	}

	public void clickOnExceptionTypePermanentButton() {
		if (!isElementDisplayedOnScreen(permanentButton)) {
			swipeUp();
		}
		clickOnElement(permanentButton);
	}

	public void clickOnExceptionTypeTemporaryButton() {
		if (!isElementDisplayedOnScreen(temporaryButton)) {
			swipeUp();
		}
		clickOnElement(temporaryButton);
	}

	public void markOneEyeException() {
		clickOnElement(oneEyeException);
	}

	public void markOneFingureException() {
		clickOnElement(firstFingureExceptionImage);
	}

	public void markFourFingureExceptionThenRemoveOne() {
		clickOnElement(firstFingureExceptionImage);
		clickOnElement(secondFingureExceptionImage);
		clickOnElement(thirdFingureExceptionImage);
		clickOnElement(forthFingureExceptionImage);
		clickOnElement(firstFingureExceptionImage);
	}

	public void clickOnClosePopUp() {
		clickOnElement(popUpCloseButton);
	}

	public void clickOnMarkExceptionButton() {
		clickOnElement(markExceptionButton);
	}

	public void clickOnIrisScanButton() {
		clickOnElement(irisScanButton);
	}

	public BiometricDetailsPage clickOnNextButton() {
		clickOnElement(nextButton);
		return new BiometricDetailsPageArabic(driver);
	}

	public void clickOnZoomButton() {
		clickOnElement(zoomButton);
	}

	public void clickOnIrisScanTitle() {
		clickOnElement(irisScanButtonTitle);
	}

	public void clickOnRightHandScanTitle() {
		clickOnElement(rightHandScanTitle);
	}

	public void clickOnleftHandScanTitle() {
		clickOnElement(leftHandScanTitle);
	}

	public void clickOnThumbsScanTitle() {
		clickOnElement(thumbsScanTitle);
	}

	public void closeScanCapturePopUp() {
		driver.navigate().back();
	}

	public BiometricDetailsPage clickOnBackButton() {
		driver.navigate().back();
		return new BiometricDetailsPageArabic(driver);
	}

	@SuppressWarnings("deprecation")
	public boolean isApplicantBiometricsPageDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator(
				"new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\""
						+ FetchUiSpec.getValueUsingId("individualBiometrics") + "\"))")));
	}

	@SuppressWarnings("deprecation")
	public boolean isAuthenticationBiometricsPageDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator(
				"new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\""
						+ FetchUiSpec.getValueUsingId("individualAuthBiometrics") + "\"))")));
	}

	public boolean isExceptionTypeTitleDisplayed() {
		return isElementDisplayed(exceptionTypeTitle);
	}

	public boolean isRightHandScanTitleDisplayed() {
		scrollToTop();
		return isElementDisplayed(rightHandScanTitle);
	}

	public boolean isLeftHandScanTitleDisplayed() {
		scrollToTop();
		return isElementDisplayed(leftHandScanTitle);
	}

	public boolean isThumbsScanTitleDisplayed() {
		scrollToTop();
		return isElementDisplayed(thumbsScanTitle);
	}

	public boolean isFaceScanTitleDisplayed() {
		scrollToTop();
		return isElementDisplayed(faceScanTitle);
	}

	public boolean isExceptionScanTitleDisplayed() {
		scrollToTop();
		return isElementDisplayed(exceptionScanTitle);
	}

	public boolean isIrisScan() {
		return isElementDisplayed(irisCapturerHeader, 2000);
	}

	public boolean isRightHandScan() {
		return isElementDisplayed(rightHandCapturerHeader, 2000);
	}

	public boolean isLeftHandScan() {
		return isElementDisplayed(leftHandCapturerHeader, 2000);
	}

	public boolean isThumbsScan() {
		return isElementDisplayed(thumbsCapturerHeader, 2000);
	}

	public boolean isFaceScan() {
		return isElementDisplayed(faceCapturerHeader, 2000);
	}

	public boolean isExceptionScan() {
		return isElementDisplayed(exceptionCapturerHeader, 2000);
	}

	public boolean isCommentHeaderDisplayed() {
		return isElementDisplayed(commentsHeader);
	}

	public boolean isExceptionCountDisplayed() {
		if (!isElementDisplayedOnScreen(exceptionCount)) {
			swipeUp();
			isElementDisplayed(exceptionCount);
		}
		return isElementDisplayed(exceptionCount);
	}

	public BiometricDetailsPage clickOnBiometricsMenuButton() {
		clickOnElement(biometricsMenuButton);
		return new BiometricDetailsPageArabic(driver);
	}

	public int getThresholdScore() {
		String scoreText = findElement(By.xpath("//android.view.View[contains(@content-desc, '%')]"))
		        .getAttribute("contentDescription");
		return Integer.parseInt(scoreText.replaceAll("[^0-9]", ""));
	}

	public int irisAttemptLeft() {
		String attemptText = irisCapturerHeader.getAttribute("contentDescription");
		String count = attemptText.replaceAll("\\D+", "");
		return Integer.parseInt(count);
	}

	@SuppressWarnings("deprecation")
	public boolean isApplicantBiometricsPageDisplayedForCorrection() {
		return isDisplayedForCorrectionByLabel("individualBiometrics", "Applicant Biometrics");
	}

}
