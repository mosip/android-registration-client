package regclient.pages.kannada;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.DashboardPage;
import regclient.page.OperationalTaskPage;
import regclient.page.RegistrationTasksPage;
import regclient.page.SelectLanguagePage;


public class RegistrationTasksPageKannada extends RegistrationTasksPage{

	@AndroidFindBy(accessibility = "ನೋಂದಣಿ ಕಾರ್ಯಗಳು")
	private WebElement registrationTasksTitle;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(2)")
	private WebElement dashboardButton;
	
	@AndroidFindBy(accessibility = "ಕಾರ್ಯಾಚರಣೆಯ ಕಾರ್ಯಗಳು")
	private WebElement operationalTaskPageTitle;

	@AndroidFindBy(accessibility = "ಹೊಸ ನೋಂದಣಿ")
	private WebElement newRegistrationButton;

	@AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.ImageView\").instance(0)")
	private WebElement synchronizeDataButton ;

	@AndroidFindBy(xpath = "//android.widget.Toast[@text=\"Policy key Sync Completed\"]")
	private WebElement policykeySyncCompletedMessage ;

	@AndroidFindBy(xpath = "//*[contains(@text,'Sync Completed')]")
	private WebElement masterDataSyncCompletedMessage ;

	@AndroidFindBy(xpath = "//android.widget.Toast[@text=\"Script Sync Completed\"]")
	private WebElement scriptSyncCompletedMessage ;

	public RegistrationTasksPageKannada(AppiumDriver driver) {
		super(driver);
	}

	public  SelectLanguagePage clickOnNewRegistrationButton() {
		clickOnElement(newRegistrationButton);
		return new SelectLanguagePageKannada(driver);
	}

	public  void clickOnSynchronizeDataButton() {
		clickOnElement(synchronizeDataButton);
	}

	public boolean isRegistrationTasksPageLoaded() {
		return isElementDisplayed(registrationTasksTitle);
	}

	public boolean isOperationalTaskDisplayed() {
		return isElementDisplayed(operationalTaskPageTitle);
	}

	public boolean isPolicykeySyncCompletedDisplayed() {
		return isElementDisplayed(policykeySyncCompletedMessage);
	}

	public boolean isMasterDataSyncCompletedDisplayed() {
		return isElementDisplayed(masterDataSyncCompletedMessage);
	}

	public boolean isScriptSyncCompletedDisplayed() {
		return isElementDisplayed(scriptSyncCompletedMessage);
	}
	
	public  DashboardPage clickOnDashboardButton() {
		clickOnElement(dashboardButton);
		return new DashboardPageKannada(driver);
	}

	public  OperationalTaskPage clickOnOperationalTasksTitle() {
		clickOnElement(operationalTaskPageTitle);
		return new OperationalTaskPageKannada(driver);
	}

}
