package regclient.pages.hindi;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.AcknowledgementPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.SelectLanguagePage;

public class AcknowledgementPageHindi extends AcknowledgementPage {
	
	@AndroidFindBy(accessibility = "रजिस्ट्रेशन स्वीकृति")
	private WebElement acknowledgementPageTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"Application ID\"))")
	private WebElement applicationID;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.Image\")")
	private WebElement qrCodeImage;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.Button\")")
	private WebElement newRegistrationButton;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"जनसांख्यिकीय जानकारी\"))")
	private WebElement demographicInformationInAcknowledgementPage;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"दस्तावेज़\"))")
	private WebElement documentsInformationInAcknowledgementPage;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"बॉयोमेट्रिक्स\"))")
	private WebElement biometricsInformationInAcknowledgementPage;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"जनसांख्यिकीय विवरण\"))")
	private WebElement demographicDetailsTitle;

	public AcknowledgementPageHindi(AppiumDriver driver) {
		super(driver);
	}
	
	public SelectLanguagePage clickOnNewRegistrationButton() {
		clickOnElement(newRegistrationButton);
		return new SelectLanguagePageHindi(driver);

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
		return new DemographicDetailsPageHindi(driver);
	}

	
}
