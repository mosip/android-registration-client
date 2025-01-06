package regclient.pages.french;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.DashboardPage;
import regclient.page.OperationalTaskPage;
import regclient.page.ProfilePage;
import regclient.page.RegistrationTasksPage;
import regclient.page.SelectLanguagePage;

public class RegistrationTasksPageFrench extends RegistrationTasksPage{

	public RegistrationTasksPageFrench(AppiumDriver driver) {
		super(driver);
	}

	@AndroidFindBy(accessibility = "Tâches d'inscription")
	private WebElement registrationTasksTitle;

	@AndroidFindBy(accessibility = "Tâches opérationnelles")
	private WebElement operationalTaskPageTitle;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(2)")
	private WebElement dashboardButton;
	
	@AndroidFindBy(accessibility = "Nouvelle inscription")
	private WebElement newRegistrationButton;

	@AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.ImageView\").instance(0)")
	private WebElement synchronizeDataButton ;

	@AndroidFindBy(xpath = "//android.widget.Toast[@text=\"Policy key Sync Completed\"]")
	private WebElement policykeySyncCompletedMessage ;

	@AndroidFindBy(xpath = "//*[contains(@text,'Sync Completed')]")
	private WebElement masterDataSyncCompletedMessage ;

	@AndroidFindBy(xpath = "//android.widget.Toast[@text=\"Script Sync Completed\"]")
	private WebElement scriptSyncCompletedMessage ;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"Profil\")")
	private WebElement profileButton;
	
	@AndroidFindBy(accessibility = "Mettre à jour l'UIN")
	private WebElement updateUinButton;
	
	@AndroidFindBy(accessibility = "UIN perdu")
	private WebElement lostUinButton;
	
	@AndroidFindBy(accessibility = "Correction biométrique")
	private WebElement biometricCorrectionButton;
	
	public  SelectLanguagePage clickOnNewRegistrationButton() {
		clickOnElement(newRegistrationButton);
		return new SelectLanguagePageFrench(driver);
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
		return new DashboardPageFrench(driver);
	}
	
	public  OperationalTaskPage clickOnOperationalTasksTitle() {
		clickOnElement(operationalTaskPageTitle);
		return new OperationalTaskPageFrench(driver);
	}
	
	public boolean isProfileTitleDisplayed() {
		return isElementDisplayed(profileButton);
	}
	
	public  ProfilePage clickProfileButton() {
		clickOnElement(profileButton);
		return new ProfilePageFrench(driver);
	}
	
	public  SelectLanguagePage clickUpdateMyUINButton() {
		clickOnElement(updateUinButton);
		return new SelectLanguagePageFrench(driver);
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
	
	public  void clickSynchronizeDataButton() {
		clickOnElement(synchronizeDataButton);
		waitTime(50);
	}
	
	public boolean checkLastSyncDate() {
		String contentDesc = synchronizeDataButton.getAttribute("content-desc");
		if(contentDesc.contains("Synchronize Data\n"+getCurrentDateWord()+","))
			return true;
		else
			return false;
	}
}
