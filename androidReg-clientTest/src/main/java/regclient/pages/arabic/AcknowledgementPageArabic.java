package regclient.pages.arabic;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.AcknowledgementPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.SelectLanguagePage;

public class AcknowledgementPageArabic extends AcknowledgementPage {

	@AndroidFindBy(accessibility = "اعتراف بالتسجيل")
	private WebElement acknowledgementPageTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"Application ID\"))")
	private WebElement applicationID;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.Image\")")
	private WebElement qrCodeImage;
	
	@AndroidFindBy(xpath = "//android.widget.Button[@content-desc=\"تسجيل جديد\"]")
	private WebElement newRegistrationButton;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"Demographic Information\"))")
	private WebElement demographicInformationInAcknowledgementPage;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"Documents\"))")
	private WebElement documentsInformationInAcknowledgementPage;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"Biometrics\"))")
	private WebElement biometricsInformationInAcknowledgementPage;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"التفاصيل الديموغرافية\"))")
	private WebElement demographicDetailsTitle;
	
	public AcknowledgementPageArabic(AppiumDriver driver) {
		super(driver);
	}
	
	public SelectLanguagePage clickOnNewRegistrationButton() {
		clickOnElement(newRegistrationButton);
		return new SelectLanguagePageArabic(driver);

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
		return new DemographicDetailsPageArabic(driver);
	}

}
