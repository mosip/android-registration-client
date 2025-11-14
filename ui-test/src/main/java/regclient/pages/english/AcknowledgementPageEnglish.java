package regclient.pages.english;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.api.FetchUiSpec;
import regclient.page.AcknowledgementPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.RegistrationTasksPage;

public class AcknowledgementPageEnglish extends AcknowledgementPage {

	@AndroidFindBy(accessibility = "Registration Acknowledgement")
	private WebElement acknowledgementPageTitle;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"Application ID\"))")
	private WebElement applicationID;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.Image\")")
	private WebElement qrCodeImage;

	@AndroidFindBy(accessibility = "Go To Home")
	private WebElement goToHomeButton;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"Demographic Information\"))")
	private WebElement demographicInformationInAcknowledgementPage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"Documents\"))")
	private WebElement documentsInformationInAcknowledgementPage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"Biometrics\"))")
	private WebElement biometricsInformationInAcknowledgementPage;

	@AndroidFindBy(accessibility = "Sync Packet")
	private WebElement syncPacketButton;

	@AndroidFindBy(accessibility = "//*[@text=\"Packet synced successfully\"]")
	private WebElement packetSyncSuccessfullyMessage;

	@AndroidFindBy(xpath = "Upload Packet")
	private WebElement uploadPacketButton;

	public AcknowledgementPageEnglish(AppiumDriver driver) {
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

	@SuppressWarnings("deprecation")
	public DemographicDetailsPage clickOnDemographicDetailsTitle() {
		clickOnElement(findElementWithRetry(MobileBy.AndroidUIAutomator(
				"new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\""
						+ FetchUiSpec.getScreenTitle("DemographicDetails") + "\"))")));
		return new DemographicDetailsPageEnglish(driver);
	}

	public void clickOnSyncPacketButton() {
		waitTime(10);
		clickOnElement(syncPacketButton);
	}

	public void clickOnUploadPacketButton() {
		clickOnElement(uploadPacketButton);
	}

	public String getAID() {
		return getTextFromLocator(applicationID);
	}

}
