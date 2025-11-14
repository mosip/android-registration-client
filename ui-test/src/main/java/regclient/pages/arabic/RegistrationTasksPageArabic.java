package regclient.pages.arabic;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.DashboardPage;
import regclient.page.OperationalTaskPage;
import regclient.page.ProfilePage;
import regclient.page.RegistrationTasksPage;
import regclient.page.SelectLanguagePage;
import regclient.pages.english.SelectLanguagePageEnglish;

public class RegistrationTasksPageArabic extends RegistrationTasksPage {

	@AndroidFindBy(accessibility = "مهام التسجيل")
	private WebElement registrationTasksTitle;

	@AndroidFindBy(accessibility = "المهام التشغيلية")
	private WebElement operationalTaskPageTitle;

	@AndroidFindBy(accessibility = "تسجيل جديد")
	private WebElement newRegistrationButton;

	@AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.ImageView\").instance(0)")
	private WebElement synchronizeDataButton;

	@AndroidFindBy(xpath = "//android.widget.Toast[@text=\"اكتمل مزامنة مفتاح السياسة\"]")
	private WebElement policykeySyncCompletedMessage;

	@AndroidFindBy(xpath = "//*[contains(@text,'اكتملت المزامنة')]")
	private WebElement masterDataSyncCompletedMessage;

	@AndroidFindBy(xpath = "//android.widget.Toast[@text=\"اكتملت مزامنة البرنامج النصي\"]")
	private WebElement scriptSyncCompletedMessage;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(2)")
	private WebElement dashboardButton;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"حساب تعريفي\")")
	private WebElement profileButton;

	@AndroidFindBy(accessibility = "تحديث UIN")
	private WebElement updateUinButton;

	@AndroidFindBy(accessibility = "فقدت UIN")
	private WebElement lostUinButton;

	@AndroidFindBy(accessibility = "التصحيح البيومتري")
	private WebElement biometricCorrectionButton;

	@AndroidFindBy(accessibility = "إعدادات\nعلامة التبويب 2 من 4")
	private WebElement settingsButton;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_message")
	private WebElement locationPermissionMessage;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_allow_foreground_only_button")
	private WebElement allowWhileUsingButton;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_allow_one_time_button")
	private WebElement allowOnceButton;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_deny_button")
	private WebElement dontAllowButton;

	public RegistrationTasksPageArabic(AppiumDriver driver) {
		super(driver);
	}

	public SelectLanguagePage clickOnNewRegistrationButton() {
		clickOnElement(newRegistrationButton);
		return new SelectLanguagePageArabic(driver);
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
		return new DashboardPageArabic(driver);
	}

	public OperationalTaskPage clickOnOperationalTasksTitle() {
		clickOnElement(operationalTaskPageTitle);
		return new OperationalTaskPageArabic(driver);
	}

	public boolean isProfileTitleDisplayed() {
		return isElementDisplayed(profileButton);
	}

	public ProfilePage clickProfileButton() {
		clickOnElement(profileButton);
		return new ProfilePageArabic(driver);
	}

	public SelectLanguagePage clickUpdateMyUINButton() {
		clickOnElement(updateUinButton);
		return new SelectLanguagePageArabic(driver);
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
		return new SelectLanguagePageEnglish(driver);
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
