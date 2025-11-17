package regclient.pages.kannada;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.DashboardPage;
import regclient.page.OperationalTaskPage;
import regclient.page.ProfilePage;
import regclient.page.RegistrationTasksPage;
import regclient.page.SelectLanguagePage;
import regclient.pages.english.SelectLanguagePageEnglish;

public class RegistrationTasksPageKannada extends RegistrationTasksPage {

	@AndroidFindBy(accessibility = "ನೋಂದಣಿ ಕಾರ್ಯಗಳು")
	private WebElement registrationTasksTitle;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(2)")
	private WebElement dashboardButton;

	@AndroidFindBy(accessibility = "ಕಾರ್ಯಾಚರಣೆಯ ಕಾರ್ಯಗಳು")
	private WebElement operationalTaskPageTitle;

	@AndroidFindBy(accessibility = "ಹೊಸ ನೋಂದಣಿ")
	private WebElement newRegistrationButton;

	@AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.ImageView\").instance(0)")
	private WebElement synchronizeDataButton;

	@AndroidFindBy(xpath = "//android.widget.Toast[@text=\"Policy key Sync Completed\"]")
	private WebElement policykeySyncCompletedMessage;

	@AndroidFindBy(xpath = "//*[contains(@text,'Sync Completed')]")
	private WebElement masterDataSyncCompletedMessage;

	@AndroidFindBy(xpath = "//android.widget.Toast[@text=\"Script Sync Completed\"]")
	private WebElement scriptSyncCompletedMessage;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"ಪ್ರೊಫೈಲ್\")")
	private WebElement profileButton;

	@AndroidFindBy(accessibility = "UIN ನವೀಕರಿಸಿ")
	private WebElement updateUinButton;

	@AndroidFindBy(accessibility = "UIN ಕಳೆದುಕೊಂಡಿದೆ")
	private WebElement lostUinButton;

	@AndroidFindBy(accessibility = "Biometric correction")
	private WebElement biometricCorrectionButton;

	@AndroidFindBy(accessibility = "Settings\nTab 2 of 4")
	private WebElement settingsButton;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_message")
	private WebElement locationPermissionMessage;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_allow_foreground_only_button")
	private WebElement allowWhileUsingButton;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_allow_one_time_button")
	private WebElement allowOnceButton;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_deny_button")
	private WebElement dontAllowButton;

	public RegistrationTasksPageKannada(AppiumDriver driver) {
		super(driver);
	}

	public SelectLanguagePage clickOnNewRegistrationButton() {
		clickOnElement(newRegistrationButton);
		return new SelectLanguagePageKannada(driver);
	}

	public void clickOnSynchronizeDataButton() {
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

	public DashboardPage clickOnDashboardButton() {
		clickOnElement(dashboardButton);
		return new DashboardPageKannada(driver);
	}

	public OperationalTaskPage clickOnOperationalTasksTitle() {
		clickOnElement(operationalTaskPageTitle);
		return new OperationalTaskPageKannada(driver);
	}

	public boolean isProfileTitleDisplayed() {
		return isElementDisplayed(profileButton);
	}

	public ProfilePage clickProfileButton() {
		clickOnElement(profileButton);
		return new ProfilePageKannada(driver);
	}

	public SelectLanguagePage clickUpdateMyUINButton() {
		clickOnElement(updateUinButton);
		return new SelectLanguagePageKannada(driver);
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

	public void clickSynchronizeDataButton() {
		clickOnElement(synchronizeDataButton);
		waitTime(50);
	}

	public boolean checkLastSyncDate() {
		String contentDesc = synchronizeDataButton.getAttribute("content-desc");
		if (contentDesc.contains("Synchronize Data\n" + getCurrentDateWord() + ","))
			return true;
		else
			return false;
	}

	public void clickOnLostUinButton() {
		clickOnElement(lostUinButton);
	}

	public void clickOnSettingsButton() {
		clickOnElement(settingsButton);
	}

	public SelectLanguagePage clickOnBiometricCorrectionButton() {
		clickOnElement(biometricCorrectionButton);
		return new SelectLanguagePageKannada(driver);
	}

	public void handleLocationPermission() {
		try {
			if (isElementDisplayed(locationPermissionMessage)) {
				clickOnElement(allowWhileUsingButton);
			}
		} catch (Exception e) {
		}
	}

	public void clickOnRegistrationTasksTab() {
		clickOnElement(registrationTasksTitle);
	}

}
