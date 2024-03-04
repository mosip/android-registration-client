package regclient.pages;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;


public class RegistrationTasksPage  extends BasePage{

	@AndroidFindBy(accessibility = "Registration Tasks")
	private WebElement registrationTasks;
	
	@AndroidFindBy(accessibility = "Operational Tasks")
	private WebElement operationalTask;

	@AndroidFindBy(accessibility = "New Registration")
	private WebElement newRegistration;

	@AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.ImageView\").instance(0)")
	private WebElement synchronizeData ;
	
	@AndroidFindBy(xpath = "//android.widget.Toast[@text=\"Policy key Sync Completed\"]")
	private WebElement policykeySyncCompleted ;
	
	@AndroidFindBy(xpath = "//*[contains(@text,'Sync Completed')]")
	private WebElement masterDataSyncCompleted ;
	
	@AndroidFindBy(xpath = "//android.widget.Toast[@text=\"Script Sync Completed\"]")
	private WebElement scriptSyncCompleted ;

	public RegistrationTasksPage(AppiumDriver driver) {
		super(driver);

	}

	public  SelectLanguagePage clickOnNewRegistration() {
		clickOnElement(newRegistration);
		return new SelectLanguagePage(driver);
	}
	
	public  void clickOnSynchronizeDataButton() {
		clickOnElement(synchronizeData);
	}
	
	public boolean isRegistrationTasksPageLoaded() {

		return isElementDisplayed(registrationTasks);
	}
	
	public boolean isOperationalTaskDisplay() {

		return isElementDisplayed(operationalTask);
	}
	
	public boolean isPolicykeySyncCompletedDisplay() {

		return isElementDisplayed(policykeySyncCompleted);
	}
	
	public boolean isMasterDataSyncCompletedDisplay() {

		return isElementDisplayed(masterDataSyncCompleted);
	}
	
	public boolean isScriptSyncCompletedDisplay() {

		return isElementDisplayed(scriptSyncCompleted);
	}

	
}
