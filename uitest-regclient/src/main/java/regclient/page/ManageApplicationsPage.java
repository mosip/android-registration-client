package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class ManageApplicationsPage extends BasePage{

	public ManageApplicationsPage(AppiumDriver driver) {
		super(driver);
	}

	public abstract boolean isManageApplicationPageDisplayed();

	public abstract void enterAID(String AID);

	public abstract void enterWrongAID(String AID);

	public abstract boolean isSearchAIDDisplayed(String AID);

	public abstract boolean isZeroApplicationDisplayed();

	public abstract void clickOnUploadButton();

	public abstract boolean isPacketUploadDone(String AID);

	public abstract boolean isPacketApproved(String AID);

	public abstract boolean isPacketSynned(String AID);

	public abstract void clickClientStatusDropdown();

	public abstract void clickServerStatusDropdown();

	public abstract boolean isCreatedDropdownOptionDisplayed();

	public abstract boolean isApprovedDropdownOptionDisplayed();

	public abstract boolean isRejectedDropdownOptionDisplayed();

	public abstract boolean isSyncedDropdownOptionDisplayed();

	public abstract boolean isUploadedDropdownOptionDisplayed();

	public abstract boolean isExportedsDropdownOptionDisplayed();

	public abstract void clickDismissButton();

	public abstract void clickOnSearchCheckBox();

	public abstract void selectApprovedValueDropdown();

	public abstract void selectSyncedOptionDropdown();

	public abstract void selectUploadedOptionDropdown();
	
	public abstract boolean isReceivedDropdownOptionDisplayed();

	public abstract boolean isProcessingDropdownOptionDisplayed();

	public abstract boolean isAcceptedDropdownOptionDisplayed();

	public abstract boolean isDeletionDropdownOptionDisplayed();
	
	public abstract void clickOnBackButton();
}
