package regclient.pages.hindi;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.DashboardPage;
import regclient.page.OperationalTaskPage;
import regclient.page.ProfilePage;
import regclient.page.RegistrationTasksPage;
import regclient.page.SelectLanguagePage;


public class RegistrationTasksPageHindi  extends RegistrationTasksPage{

	@AndroidFindBy(accessibility = "पंजीकरण कार्य")
	private WebElement registrationTasksTitle;

	@AndroidFindBy(accessibility = "परिचालन कार्य")
	private WebElement operationalTaskPageTitle;

	@AndroidFindBy(accessibility = "नया पंजीकरण")
	private WebElement newRegistrationButton;

	@AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.ImageView\").instance(0)")
	private WebElement synchronizeDataButton ;

	@AndroidFindBy(xpath = "//android.widget.Toast[@text=\"Policy key Sync Completed\"]")
	private WebElement policykeySyncCompletedMessage ;

	@AndroidFindBy(xpath = "//*[contains(@text,'Sync Completed')]")
	private WebElement masterDataSyncCompletedMessage ;

	@AndroidFindBy(xpath = "//android.widget.Toast[@text=\"Script Sync Completed\"]")
	private WebElement scriptSyncCompletedMessage ;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(2)")
	private WebElement dashboardButton;
	
	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"प्रोफ़ाइल\")")
	private WebElement profileButton;
	
	@AndroidFindBy(accessibility = "UIN अपडेट करें")
	private WebElement updateUinButton;
	
	@AndroidFindBy(accessibility = "खोया यूआईएन")
	private WebElement lostUinButton;
	
	@AndroidFindBy(accessibility = "Biometric correction")
	private WebElement biometricCorrectionButton;

	public RegistrationTasksPageHindi(AppiumDriver driver) {
		super(driver);
	}

	public  SelectLanguagePage clickOnNewRegistrationButton() {
		clickOnElement(newRegistrationButton);
		return new SelectLanguagePageHindi(driver);
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
		return new DashboardPageHindi(driver);
	}

	public  OperationalTaskPage clickOnOperationalTasksTitle() {
		clickOnElement(operationalTaskPageTitle);
		return new OperationalTaskPageHindi(driver);
	}
	
	public boolean isProfileTitleDisplayed() {
		return isElementDisplayed(profileButton);
	}
	
	public  ProfilePage clickProfileButton() {
		clickOnElement(profileButton);
		return new ProfilePageHindi(driver);
	}
	
	public  SelectLanguagePage clickUpdateMyUINButton() {
		clickOnElement(updateUinButton);
		return new SelectLanguagePageHindi(driver);
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
