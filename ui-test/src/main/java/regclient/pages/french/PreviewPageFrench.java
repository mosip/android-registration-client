package regclient.pages.french;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.api.AdminTestUtil;
import regclient.api.FetchUiSpec;
import regclient.page.AuthenticationPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.PreviewPage;

public class PreviewPageFrench extends PreviewPage {

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"Informations démographiques\"))")
	private WebElement demographicInformationInPreviewPage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"Application ID\"))")
	private WebElement applicationIDPreviewPage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"Documents\"))")
	private WebElement documentsInformationInPreviewPage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"Biométrie\"))")
	private WebElement biometricsInformationInPreviewPage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"Iris ( 1 )\"))")
	private WebElement singleIrisImage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"Iris ( 2 )\"))")
	private WebElement bothIrisImage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"Fingers ( 7 )\"))")
	private WebElement fingerExceptionText;

	@AndroidFindBy(accessibility = "CONTINUER")
	private WebElement continueButton;

	@AndroidFindBy(xpath = "//android.view.View[contains(@text,'Email')]/../following-sibling::android.view.View[1]")
	private WebElement emailIdPreviewPage;

	@AndroidFindBy(accessibility = "UIN perdu")
	private WebElement lostUinTitle;

	@AndroidFindBy(accessibility = "Correction biométrique")
	private WebElement biometricCorrectionTitle;

	public PreviewPageFrench(AppiumDriver driver) {
		super(driver);
	}

	public AuthenticationPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new AuthenticationPageFrench(driver);
	}

	public boolean isDemographicInformationInPreviewPageDisplayed() {
		try {
			By locator = MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true))"
					+ ".scrollIntoView(new UiSelector().textContains(\"Informations démographiques\"))");
			driver.findElement(locator);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isDocumentsInformationInPreviewPageDisplayed() {
		try {
			scrollToText("Documents");
			return isElementDisplayed(documentsInformationInPreviewPage);
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isBiometricsInformationInPreviewPageDisplayed() {
		try {
			scrollToText("Biométrie");
			return isElementDisplayed(biometricsInformationInPreviewPage);
		} catch (Exception e) {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	public boolean isNewRegistrationTitleDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator(
				"new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\""
						+ FetchUiSpec.getTitleUsingId("NEW") + "\"))")));
	}

	public boolean updateUINTitleDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator(
				"new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\""
						+ FetchUiSpec.getTitleUsingId("UPDATE") + "\"))")));
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
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public DemographicDetailsPage clickOnDemographicDetailsTitle() {
		clickOnElement(findElementWithRetry(MobileBy.AndroidUIAutomator(
				"new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\""
						+ FetchUiSpec.getScreenTitle("DemographicDetails") + "\"))")));
		return new DemographicDetailsPageFrench(driver);
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

	public boolean isBiometricCorrectionTitleDisplayed() {
		return isElementDisplayed(biometricCorrectionTitle);
	}

	public void scrollToText(String text) {
		((AndroidDriver) driver)
				.findElement(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true))"
						+ ".scrollIntoView(new UiSelector().text(\"" + text + "\"))"));
	}
}
