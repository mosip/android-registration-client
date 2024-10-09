package regclient.pages.tamil;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.DashboardPage;
import regclient.page.OperationalTaskPage;
import regclient.page.ProfilePage;
import regclient.page.RegistrationTasksPage;
import regclient.page.SelectLanguagePage;


public class RegistrationTasksPageTamil extends RegistrationTasksPage{

	@AndroidFindBy(accessibility = "பதிவு பணிகள்")
	private WebElement registrationTasksTitle;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(2)")
	private WebElement dashboardButton;

	@AndroidFindBy(accessibility = "செயல்பாட்டு பணிகள்")
	private WebElement operationalTaskPageTitle;

	@AndroidFindBy(accessibility = "புதிய பதிவு")
	private WebElement newRegistrationButton;

	@AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.ImageView\").instance(0)")
	private WebElement synchronizeDataButton ;

	@AndroidFindBy(xpath = "//android.widget.Toast[@text=\"Policy key Sync Completed\"]")
	private WebElement policykeySyncCompletedMessage ;

	@AndroidFindBy(xpath = "//*[contains(@text,'Sync Completed')]")
	private WebElement masterDataSyncCompletedMessage ;

	@AndroidFindBy(xpath = "//android.widget.Toast[@text=\"Script Sync Completed\"]")
	private WebElement scriptSyncCompletedMessage ;
	
	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"சுயவிவரம்\")")
	private WebElement profileButton;
	
	@AndroidFindBy(accessibility = "UIN ஐப் புதுப்பிக்கவும்")
	private WebElement updateUinButton;
	
	@AndroidFindBy(accessibility = "தொலைந்த UIN")
	private WebElement lostUinButton;
	
	@AndroidFindBy(accessibility = "Biometric correction")
	private WebElement biometricCorrectionButton;

	public RegistrationTasksPageTamil(AppiumDriver driver) {
		super(driver);
	}

		public  SelectLanguagePage clickOnNewRegistrationButton() {
		clickOnElement(newRegistrationButton);
		return new SelectLanguagePageTamil(driver);
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
		return new DashboardPageTamil(driver);
	}
	
	public  OperationalTaskPage clickOnOperationalTasksTitle() {
		clickOnElement(operationalTaskPageTitle);
		return new OperationalTaskPageTamil(driver);
	}

	public boolean isProfileTitleDisplayed() {
		return isElementDisplayed(profileButton);
	}
	
	public  ProfilePage clickProfileButton() {
		clickOnElement(profileButton);
		return new ProfilePageTamil(driver);
	}
	
	public  SelectLanguagePage clickUpdateMyUINButton() {
		clickOnElement(updateUinButton);
		return new SelectLanguagePageTamil(driver);
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
