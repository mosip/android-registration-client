package regclient.pages;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;

public class AcknowledgementPage extends BasePage {

	@AndroidFindBy(accessibility = "Registration Acknowledgement")
	private WebElement acknowledgementPageTitle;
	
	@AndroidFindBy(xpath = "//*[contains(@text, 'Application ID')]")
	private WebElement applicationID;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.Image\")")
	private WebElement qrCodeImage;
	
	@AndroidFindBy(accessibility = "NEW REGISTRATION")
	private WebElement newRegistrationButton;
	
	@AndroidFindBy(xpath = "//*[@text=\"Demographic Information\"]")
	private WebElement demographicInformationInPreviewPage;
	
	@AndroidFindBy(xpath = "//*[contains(@text, \"Application ID\")]")
	private WebElement applicationIDPreviewPage;
	
	@AndroidFindBy(xpath = "//*[@text=\"Documents\"]")
	private WebElement documentsInformationInPreviewPage;
	
	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Biometrics\"]")
	private WebElement biometricsInformationInPreviewPage;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"Demographic Details\"))")
	private WebElement demographicDetailsTitle;
	
	public AcknowledgementPage(AppiumDriver driver) {
		super(driver);
	}
	
	public SelectLanguagePage clickOnNewRegistrationButton() {
		clickOnElement(newRegistrationButton);
		return new SelectLanguagePage(driver);

	}
	
	public boolean isAcknowledgementPageDisplayed() {
		return isElementDisplayed(acknowledgementPageTitle);
	}
	
	public boolean isApplicationIDDisplayed() {
		return isElementDisplayed(applicationID);
	}
	
	public boolean isQrCodeImageDisplayed() {
		return isElementDisplayed(qrCodeImage);
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
	
	public boolean isApplicationIDPreviewPagePageDisplayed() {
		return isElementDisplayed(applicationIDPreviewPage);
	}
	
	public DemographicDetailsPage clickOnDemographicDetailsTitle() {
		clickOnElement(demographicDetailsTitle);
		return new DemographicDetailsPage(driver);
	}
}
