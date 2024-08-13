package regclient.pages.arabic;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.api.FetchUiSpec;
import regclient.page.AuthenticationPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.PreviewPage;
import regclient.pages.english.AuthenticationPageEnglish;
import regclient.pages.english.DemographicDetailsPageEnglish;

public class PreviewPageArabic extends PreviewPage {

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
	
	@AndroidFindBy(accessibility = "يكمل")
	private WebElement continueButton;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"Fingers ( 7 )\"))")
	private WebElement fingerExceptionText;
	
	public PreviewPageArabic(AppiumDriver driver) {
		super(driver);
	}
	
	public  AuthenticationPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new AuthenticationPageEnglish(driver);
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
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"" + FetchUiSpec.getTitleUsingId("NEW") + "\"))")));
	}
	
	public boolean updateUINTitleDisplayed() {
		return isElementDisplayed (findElementWithRetry(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"" + FetchUiSpec.getTitleUsingId("UPDATE") + "\"))")));
	}
	
	public boolean isApplicationIDPreviewPagePageDisplayed() {
		return isElementDisplayed(applicationIDPreviewPage);
	}
	
	@SuppressWarnings("deprecation")
	public DemographicDetailsPage clickOnDemographicDetailsTitle() {
		clickOnElement(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"" + FetchUiSpec.getScreenTitle("DemographicDetails") + "\"))")));
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
		String applicationID = getTextFromLocator(applicationIDPreviewPage).replaceAll(".*Application ID (\\d+).*", "$1");
		return applicationID;
	}
	
}
