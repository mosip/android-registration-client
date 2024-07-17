package regclient.pages.tamil;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.AcknowledgementPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.RegistrationTasksPage;
import regclient.pages.english.RegistrationTasksPageEnglish;

public class AcknowledgementPageTamil extends AcknowledgementPage {

	@AndroidFindBy(accessibility = "பதிவு அறிந்தார்")
	private WebElement acknowledgementPageTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"Application ID\"))")
	private WebElement applicationID;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.Image\")")
	private WebElement qrCodeImage;
	
	@AndroidFindBy(accessibility = "வீட்டிற்கு போ")
	private WebElement goToHomeButton;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"மக்கள்தொகை தகவல்\"))")
	private WebElement demographicInformationInAcknowledgementPage;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"ஆவணங்கள்\"))")
	private WebElement documentsInformationInAcknowledgementPage;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"பயோமெட்ரிக்ஸ்\"))")
	private WebElement biometricsInformationInAcknowledgementPage;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"மக்கள்தொகை விவரங்கள்\"))")
	private WebElement demographicDetailsTitle;
	
	@AndroidFindBy(accessibility = "Sync Packet")
	private WebElement syncPacketButton;
	
	public AcknowledgementPageTamil(AppiumDriver driver) {
		super(driver);
	}
	
	public RegistrationTasksPage clickOnGoToHomeButton() {
		clickOnElement(goToHomeButton);
		return new RegistrationTasksPageEnglish(driver);

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
		return new DemographicDetailsPageTamil(driver);
	}
	
	public String getAID() {
		return getTextFromLocator(applicationID);
	}
	
	public void clickOnSyncPacketButton() {
		waitTime(10);
		clickOnElement(syncPacketButton);
	}


}
