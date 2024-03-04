package regclient.pages;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import regclient.utils.AndroidUtil;

public class RegistrationTasksPage  extends BasePage{

	@AndroidFindBy(accessibility = "Registration Tasks")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement RegistrationTasks;
	
	@AndroidFindBy(accessibility = "Operational Tasks")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement operationalTask;

	@AndroidFindBy(accessibility = "New Registration")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement newRegistration;

	@AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.ImageView\").instance(0)")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement synchronizeData ;
	
	@AndroidFindBy(xpath = "//android.widget.Toast[@text=\"Policy key Sync Completed\"]")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement policykeySyncCompleted ;
	
	@AndroidFindBy(xpath = "//*[contains(@text,'Sync Completed')]")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement masterDataSyncCompleted ;
	
	@AndroidFindBy(xpath = "//android.widget.Toast[@text=\"Script Sync Completed\"]")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement ScriptSyncCompleted ;

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

		return isElementDisplayed(RegistrationTasks);
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

		return isElementDisplayed(ScriptSyncCompleted);
	}

	
}
