package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class DashboardPage extends BasePage{

	public DashboardPage(AppiumDriver driver) {
		super(driver);
	}

	public abstract boolean isDashboardTitleDisplayed();

	public abstract boolean isPacketsUploadedValueDisplayed();

	public abstract boolean isPacketsSyncedValueDisplayed();

	public abstract boolean isUserIDDisplayed();

	public abstract boolean isUserNameDisplayed();

	public abstract boolean isStatusTitleDisplayed();
}
