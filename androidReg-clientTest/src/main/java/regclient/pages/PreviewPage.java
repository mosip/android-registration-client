package regclient.pages;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;

public class PreviewPage extends BasePage {

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"New Registration\"))")
	private WebElement newRegistrationTitle;
	
	@AndroidFindBy(xpath = "//*[@text=\"Demographic Information\"]")
	private WebElement demographicInformationInPreviewPage;
	
	@AndroidFindBy(xpath = "//*[@text=\"Documents\"]")
	private WebElement documentsInformationInPreviewPage;
	
	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Biometrics\"]")
	private WebElement biometricsInformationInPreviewPage;
	
	@AndroidFindBy(accessibility = "CONTINUE")
	private WebElement continueButton;
	
	public PreviewPage(AppiumDriver driver) {
		super(driver);
	}
	
	public  AuthenticationPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new AuthenticationPage(driver);
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

}
