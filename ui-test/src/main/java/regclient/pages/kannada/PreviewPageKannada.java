package regclient.pages.kannada;

import org.openqa.selenium.WebElement;
import org.testng.Assert;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.api.AdminTestUtil;
import regclient.api.FetchUiSpec;
import regclient.page.AuthenticationPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.PreviewPage;
import regclient.pages.english.AuthenticationPageEnglish;
import regclient.pages.english.DemographicDetailsPageEnglish;

public class PreviewPageKannada extends PreviewPage {

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"ಜನಸಂಖ್ಯಾ ಮಾಹಿತಿ\"))")
	private WebElement demographicInformationInPreviewPage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"Application ID\"))")
	private WebElement applicationIDPreviewPage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"ದಾಖಲೆಗಳು\"))")
	private WebElement documentsInformationInPreviewPage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"ಬಯೋಮೆಟ್ರಿಕ್ಸ್\"))")
	private WebElement biometricsInformationInPreviewPage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"Iris ( 1 )\"))")
	private WebElement singleIrisImage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"Iris ( 2 )\"))")
	private WebElement bothIrisImage;

	@AndroidFindBy(accessibility = "ಮುಂದುವರಿಸಿ")
	private WebElement continueButton;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"Fingers ( 7 )\"))")
	private WebElement fingerExceptionText;

	@AndroidFindBy(xpath = "//android.view.View[contains(@text,'Email')]/../following-sibling::android.view.View[1]")
	private WebElement emailIdPreviewPage;

	@AndroidFindBy(accessibility = "ಕಳೆದುಹೋದ UIN")
	private WebElement lostUinTitle;

	public PreviewPageKannada(AppiumDriver driver) {
		super(driver);
	}

	public AuthenticationPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new AuthenticationPageKannada(driver);
	}

	public boolean isDemographicInformationInPreviewPageDisplayed() {
		return isElementDisplayed(demographicInformationInPreviewPage);
	}

	public boolean isDocumentsInformationInPreviewPageDisplayed() {
		return isElementDisplayed(documentsInformationInPreviewPage);
	}

	public boolean isBiometricsInformationInPreviewPagePageDisplayed() {
		return isElementDisplayed(biometricsInformationInPreviewPage);
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

	public boolean isApplicationIDPreviewPagePageDisplayed() {
		return isElementDisplayed(applicationIDPreviewPage);
	}

	@SuppressWarnings("deprecation")
	public DemographicDetailsPage clickOnDemographicDetailsTitle() {
		clickOnElement(findElementWithRetry(MobileBy.AndroidUIAutomator(
				"new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\""
						+ FetchUiSpec.getScreenTitle("DemographicDetails") + "\"))")));
		return new DemographicDetailsPageKannada(driver);
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

}
