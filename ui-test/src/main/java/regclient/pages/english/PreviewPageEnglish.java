package regclient.pages.english;

import java.time.Duration;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.api.AdminTestUtil;
import regclient.api.FetchUiSpec;
import regclient.page.AuthenticationPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.PreviewPage;
import org.testng.Assert;

public class PreviewPageEnglish extends PreviewPage {

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"Demographic Information\"))")
	private WebElement demographicInformationInPreviewPage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"Application ID\"))")
	private WebElement applicationIDPreviewPage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"Documents\"))")
	private WebElement documentsInformationInPreviewPage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"Biometrics\"))")
	private WebElement biometricsInformationInPreviewPage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"Iris ( 1 )\"))")
	private WebElement singleIrisImage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"Iris ( 2 )\"))")
	private WebElement bothIrisImage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"Fingers ( 7 )\"))")
	private WebElement fingerExceptionText;

	@AndroidFindBy(accessibility = "CONTINUE")
	private WebElement continueButton;

	@AndroidFindBy(xpath = "//android.view.View[contains(@text,'Email')]/../following-sibling::android.view.View[1]")
	private WebElement emailIdPreviewPage;

	@AndroidFindBy(accessibility = "Lost UIN")
	private WebElement lostUinTitle;

	@AndroidFindBy(accessibility = "Biometric correction")
	private WebElement biometricCorrectionTitle;

	public PreviewPageEnglish(AppiumDriver driver) {
		super(driver);
	}

	public AuthenticationPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new AuthenticationPageEnglish(driver);
	}

	public boolean isDemographicInformationInPreviewPageDisplayed() {
		try {

			By locator = MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true))"
					+ ".scrollIntoView(new UiSelector().textContains(\"Demographic\"))");

			driver.findElement(locator);

			return true;

		} catch (Exception e) {
			return false;
		}
	}

	public boolean isDocumentsInformationInPreviewPageDisplayed() {
		try {

			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

			// wait until preview page loads
			wait.until(ExpectedConditions.presenceOfElementLocated(By.className("android.webkit.WebView")));

			scrollToText("Documents");

			wait.until(ExpectedConditions.visibilityOf(documentsInformationInPreviewPage));

			return true;

		} catch (Exception e) {
			return false;
		}
	}

	public boolean isBiometricsInformationInPreviewPageDisplayed() {
		try {

			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

			// wait for WebView
			wait.until(ExpectedConditions.presenceOfElementLocated(By.className("android.webkit.WebView")));

			scrollToText("Biometrics");

			wait.until(ExpectedConditions.visibilityOf(biometricsInformationInPreviewPage));

			return true;

		} catch (Exception e) {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	public boolean isNewRegistrationTitleDisplayed() {
		scrollToTop();
		return isElementDisplayed(findElement(MobileBy.AndroidUIAutomator(
				"new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\""
						+ FetchUiSpec.getTitleUsingId("NEW") + "\"))")));
	}

	@SuppressWarnings("deprecation")
	public boolean updateUINTitleDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator(
				"new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\""
						+ FetchUiSpec.getTitleUsingId("UPDATE") + "\"))")));
	}

	public boolean isBiometricCorrectionTitleDisplayed() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			wait.until(ExpectedConditions.visibilityOfElementLocated(
					MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true))"
							+ ".scrollIntoView(new UiSelector().description(\"Biometric correction\"))")));

			return true;

		} catch (Exception e) {
			return false;
		}
	}

	public boolean isApplicationIDPreviewPageDisplayed() {

		By applicationIdLocator = By.xpath("//android.view.View[contains(@text,'Application ID')]");

		for (int i = 0; i < 5; i++) {

			try {
				waitTime(1);
				tapScreenCenter();
				waitTime(1);

				if (driver.findElement(applicationIdLocator).isDisplayed()) {
					return true;
				}

			} catch (Exception e) {
				// retry
			}
		}

		return false;
	}

	@SuppressWarnings("deprecation")
	public DemographicDetailsPage clickOnDemographicDetailsTitle() {
		clickOnElement(findElementWithRetry(MobileBy.AndroidUIAutomator(
				"new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\""
						+ FetchUiSpec.getScreenTitle("DemographicDetails") + "\"))")));
		return new DemographicDetailsPageEnglish(driver);
	}

	public boolean isBothIrisImageDisplayed() {
		return isElementDisplayed(bothIrisImage);
	}

	public boolean isSingleIrisImageDisplayed() {
		return isElementDisplayed(singleIrisImage);
	}

	public boolean isFingerExceptionText() {
		return isElementDisplayed(fingerExceptionText);
	}

	public String getAID() {
		String applicationID = getTextFromLocator(applicationIDPreviewPage).replaceAll(".*Application ID (\\d+).*",
				"$1");
		return applicationID;
	}

	public void validatePreRegAndApplicationIdMatch(String age) {
		String preRegId = AdminTestUtil.getPreRegistrationFlow(age);

		String applicationID = getAID();

		Assert.assertEquals(applicationID, preRegId, "Mismatch between API PreReg ID and UI Application ID!");
	}

	public String getEmailId() {
		String emailId = getTextFromLocator(emailIdPreviewPage)
				.replaceAll(".*?([A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}).*", "$1");
		return emailId;
	}

	public boolean isLostUinTitleDisplayed() {
		return isElementDisplayed(lostUinTitle);
	}

	public void scrollToText(String text) {
		((AndroidDriver) driver)
				.findElement(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true))"
						+ ".scrollIntoView(new UiSelector().text(\"" + text + "\"))"));
	}

}
