package regclient.page;

import io.appium.java_client.AppiumDriver;


public abstract class RegistrationTasksPage extends BasePage {

	public RegistrationTasksPage(AppiumDriver driver) {
		super(driver);
	}

	public abstract  SelectLanguagePage clickOnNewRegistrationButton();

	public abstract void clickOnSynchronizeDataButton() ;
	
	public abstract boolean isRegistrationTasksPageLoaded();

	public abstract  boolean isOperationalTaskDisplayed();
	
	public abstract boolean isPolicykeySyncCompletedDisplayed();

	public abstract boolean isMasterDataSyncCompletedDisplayed();

	public abstract boolean isScriptSyncCompletedDisplayed();
	
	public abstract  DashboardPage clickOnDashboardButton();
	
	public abstract  OperationalTaskPage clickOnOperationalTasksTitle();

	public abstract boolean isProfileTitleDisplayed();
	
	public abstract  ProfilePage clickProfileButton();
	
	public abstract  SelectLanguagePage clickUpdateMyUINButton();
	
	public abstract boolean isUpdateUINTitleDisplayed();
	
	public abstract boolean isLostUINTitleDisplayed();
		
	public abstract boolean isBiometricCorrectionTitleDisplayed();
	
    public abstract void clickSynchronizeDataButton();

    public abstract boolean checkLastSyncDate();

}
