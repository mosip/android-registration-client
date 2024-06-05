package regclient.pages.kannada;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.AuthenticationPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.PreviewPage;

public class PreviewPageKannada extends PreviewPage {

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"ಹೊಸ ನೋಂದಣಿ\"))")
	private WebElement newRegistrationTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"ಜನಸಂಖ್ಯಾ ಮಾಹಿತಿ\"))")
	private WebElement demographicInformationInPreviewPage;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"Application ID\"))")
	private WebElement applicationIDPreviewPage;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"ದಾಖಲೆಗಳು\"))")
	private WebElement documentsInformationInPreviewPage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"ಜನಸಂಖ್ಯಾ ವಿವರಗಳು\"))")
	private WebElement demographicDetailsTitle;
	
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
	
	public PreviewPageKannada(AppiumDriver driver) {
		super(driver);
	}
	
	public  AuthenticationPage clickOnContinueButton() {
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
	
	public boolean isNewRegistrationTitleDisplayed() {
		return isElementDisplayed(newRegistrationTitle);
	}
	
	public boolean isApplicationIDPreviewPagePageDisplayed() {
		return isElementDisplayed(applicationIDPreviewPage);
	}
	
	public DemographicDetailsPage clickOnDemographicDetailsTitle() {
		clickOnElement(demographicDetailsTitle);
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


}
