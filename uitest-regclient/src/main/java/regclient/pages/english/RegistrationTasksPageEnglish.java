package regclient.pages.english;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.DashboardPage;
import regclient.page.OperationalTaskPage;
import regclient.page.ProfilePage;
import regclient.page.RegistrationTasksPage;
import regclient.page.SelectLanguagePage;


public class RegistrationTasksPageEnglish  extends RegistrationTasksPage{

	@AndroidFindBy(accessibility = "Registration Tasks")
	private WebElement registrationTasksTitle;
	
	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"Dashboard\")")
	private WebElement dashboardButton;
	
	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"Profile\")")
	private WebElement profileButton;

	@AndroidFindBy(accessibility = "Operational Tasks")
	private WebElement operationalTaskPageTitle;

	@AndroidFindBy(accessibility = "New Registration")
	private WebElement newRegistrationButton;
	
	@AndroidFindBy(accessibility = "Update UIN")
	private WebElement updateUinButton;
	
	@AndroidFindBy(accessibility = "Lost UIN")
	private WebElement lostUinButton;
	
	@AndroidFindBy(accessibility = "Biometric correction")
	private WebElement biometricCorrectionButton;

	@AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.ImageView\").instance(0)")
	private WebElement synchronizeDataButton ;

	@AndroidFindBy(xpath = "//android.widget.Toast[@text=\"Policy key Sync Completed\"]")
	private WebElement policykeySyncCompletedMessage ;

	@AndroidFindBy(xpath = "//android.widget.Toast[@text=\"Master Data Sync Completed\"]")
	private WebElement masterDataSyncCompletedMessage ;

	@AndroidFindBy(xpath = "//android.widget.Toast[@text=\"Script Sync Completed\"]")
	private WebElement scriptSyncCompletedMessage ;

	public RegistrationTasksPageEnglish(AppiumDriver driver) {
		super(driver);
	}

	public  SelectLanguagePage clickOnNewRegistrationButton() {
		clickOnElement(newRegistrationButton);
		return new SelectLanguagePageEnglish(driver);
	}

	public  void clickOnSynchronizeDataButton() {
		clickOnElement(synchronizeDataButton);
	}

	public boolean isRegistrationTasksPageLoaded() {
		return isElementDisplayed(registrationTasksTitle,20);
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
		return new DashboardPageEnglish(driver);
	}
	
	public  OperationalTaskPage clickOnOperationalTasksTitle() {
		clickOnElement(operationalTaskPageTitle);
		return new OperationalTaskPageEnglish(driver);
	}
	
	public boolean isProfileTitleDisplayed() {
		return isElementDisplayed(profileButton);
	}
	
	public  ProfilePage clickProfileButton() {
		clickOnElement(profileButton);
		return new ProfilePageEnglish(driver);
	}
	
	public  SelectLanguagePage clickUpdateMyUINButton() {
		clickOnElement(updateUinButton);
		return new SelectLanguagePageEnglish(driver);
	}
	
	public boolean isUpdateUINTitleDisplayed() {
		return isElementDisplayed(updateUinButton);
	}
	
	public boolean isLostUINTitleDisplayed() {
		return isElementDisplayed(lostUinButton);
	}
	
	public boolean isBiometricCorrectionTitleDisplayed() {
		return isElementDisplayed(biometricCorrectionButton);
	}

}
