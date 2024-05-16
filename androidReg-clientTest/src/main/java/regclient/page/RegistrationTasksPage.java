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

}
