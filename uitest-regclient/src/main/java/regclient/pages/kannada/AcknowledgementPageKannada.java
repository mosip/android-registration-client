package regclient.pages.kannada;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.AcknowledgementPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.RegistrationTasksPage;
import regclient.pages.english.RegistrationTasksPageEnglish;

public class AcknowledgementPageKannada extends AcknowledgementPage {

	@AndroidFindBy(accessibility = "ನೋಂದಾಯಿತ ಸ್ವೀಕೃತಿ")
	private WebElement acknowledgementPageTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"Application ID\"))")
	private WebElement applicationID;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.Image\")")
	private WebElement qrCodeImage;
	
	@AndroidFindBy(accessibility = "ಮನೆಗೆ ಹೋಗು")
	private WebElement goToHomeButton;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"ಜನಸಂಖ್ಯಾ ಮಾಹಿತಿ\"))")
	private WebElement demographicInformationInAcknowledgementPage;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"ದಾಖಲೆಗಳು\"))")
	private WebElement documentsInformationInAcknowledgementPage;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"ಬಯೋಮೆಟ್ರಿಕ್ಸ್\"))")
	private WebElement biometricsInformationInAcknowledgementPage;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"ಜನಸಂಖ್ಯಾ ವಿವರಗಳು\"))")
	private WebElement demographicDetailsTitle;
	
	public AcknowledgementPageKannada(AppiumDriver driver) {
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
		return new DemographicDetailsPageKannada(driver);
	}
	
	public String getAID() {
		return getTextFromLocator(applicationID);
	}

}
