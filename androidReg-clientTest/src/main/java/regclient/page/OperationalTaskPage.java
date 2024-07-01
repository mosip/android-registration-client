package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class OperationalTaskPage extends BasePage{

	public OperationalTaskPage(AppiumDriver driver) {
		super(driver);
	}

	public abstract  SupervisorBiometricVerificationpage clickOnUpdateOperatorBiometricsButton();

	public abstract boolean isOperationalTaskPageLoaded();
	
	public abstract  void clickSynchronizeDataButton();
	
	public abstract boolean checkLastSyncDate();
}
