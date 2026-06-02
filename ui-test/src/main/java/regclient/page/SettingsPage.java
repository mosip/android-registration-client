package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class SettingsPage extends BasePage {

	public SettingsPage(AppiumDriver driver) {
		super(driver);
	}

	public abstract boolean isScheduledJobsSettingsTabDisplayed();

	public abstract boolean isGlobalConfigSettingsTabDisplayed();

	public abstract boolean isDeviceSettingsTabDisplayed();

	public abstract void clickOnGlobalConfigSettingsTab();

	public abstract boolean isGlobalConfigSettingsHeaderDisplayed();

	public abstract void clickOnSubmitButton();

	public abstract boolean isNoChangesToSaveDisplayed();

	public abstract void clickOnDeviceSettingsTab();

	public abstract boolean isDeviceSettingsPageDisplayed();

	public abstract boolean isScanNowButtonDisplayed();

	public abstract void clickOnScanNowButton();

	public abstract boolean isFaceDeviceCardDisplayed();

	public abstract boolean isIrisDeviceCardDisplayed();

	public abstract boolean isFingerDeviceCardDisplayed();

	public abstract void validateDeviceCard(String deviceName);

	public abstract void clickOnScheduledJobsSettingsTab();

	public abstract boolean isNoDevicesFoundDisplayed();

	public abstract boolean isSubmitChangesPopupDisplayed();

	public abstract void clickOnChangesConfirmButton();

	public abstract boolean isScheduledJobSettingsPageHeaderDisplayed();

	public abstract boolean isMasterDataToastMessageDisplayed();

	public abstract void clickOnSyncButton(String jobName);

	public abstract boolean validateJobCardFields(String jobName);

	public abstract boolean isJobDisplayed(String jobName);
	
	public abstract boolean isDeviceSettingsLabelDisplayedInLoggedLanguage();

	public abstract boolean isKeyLabelDisplayed();

	public abstract boolean isServerValueLabelDisplayed();

	public abstract boolean isLocalValueLabelDisplayed();

	public abstract boolean isConfigListPresent();
	
	public abstract boolean isLocalValueBoxDisplayed();
	
	public abstract boolean isGlobalConfigSettingsSearchBoxDisplayed();

}
