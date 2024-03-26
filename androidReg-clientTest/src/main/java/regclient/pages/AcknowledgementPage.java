package regclient.pages;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;

public class AcknowledgementPage extends BasePage {

	@AndroidFindBy(accessibility = "Registration Acknowledgement")
	private WebElement acknowledgementPageTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"Application ID\"))")
	private WebElement applicationID;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.Image\")")
	private WebElement qrCodeImage;
	
	@AndroidFindBy(accessibility = "NEW REGISTRATION")
	private WebElement newRegistrationButton;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"Demographic Information\"))")
	private WebElement demographicInformationInAcknowledgementPage;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"Documents\"))")
	private WebElement documentsInformationInAcknowledgementPage;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"Biometrics\"))")
	private WebElement biometricsInformationInAcknowledgementPage;
	
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
	
	public boolean isDemographicInformationInAcknowledgementPageDisplayed() {
		return isElementDisplayed(demographicInformationInAcknowledgementPage);
	}
	
	public boolean isDocumentsInformationInAcknowledgementPageDisplayed() {
		return isElementDisplayed(documentsInformationInAcknowledgementPage);
	}
	
	public boolean isBiometricsInformationInAcknowledgementPageDisplayed() {
		return isElementDisplayed(biometricsInformationInAcknowledgementPage);
	}
	
	public DemographicDetailsPage clickOnDemographicDetailsTitle() {
		clickOnElement(demographicDetailsTitle);
		return new DemographicDetailsPage(driver);
	}
}
