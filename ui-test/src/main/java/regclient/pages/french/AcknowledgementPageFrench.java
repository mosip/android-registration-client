package regclient.pages.french;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.api.FetchUiSpec;
import regclient.page.AcknowledgementPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.RegistrationTasksPage;
import regclient.pages.english.DemographicDetailsPageEnglish;
import regclient.pages.english.RegistrationTasksPageEnglish;

public class AcknowledgementPageFrench extends AcknowledgementPage {

	@AndroidFindBy(accessibility = "Accusé de réception d'inscription")
	private WebElement acknowledgementPageTitle;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0))"
			+ ".scrollIntoView(new UiSelector().textContains(\"ID de l’application\"))")
	private WebElement applicationID;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.Image\")")
	private WebElement qrCodeImage;

	@AndroidFindBy(accessibility = "Aller à la maison")
	private WebElement goToHomeButton;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"Informations démographiques\"))")
	private WebElement demographicInformationInAcknowledgementPage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"Documents\"))")
	private WebElement documentsInformationInAcknowledgementPage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"Biométrie\"))")
	private WebElement biometricsInformationInAcknowledgementPage;

	public AcknowledgementPageFrench(AppiumDriver driver) {
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

	public String getAID() {
		return getTextFromLocator(applicationID);
	}
}
