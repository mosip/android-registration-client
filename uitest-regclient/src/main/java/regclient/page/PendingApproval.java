package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class PendingApproval  extends BasePage{

	public PendingApproval(AppiumDriver driver) {
		super(driver);
	}
	
	public abstract boolean isPendingApprovalTitleDisplayed();

    public abstract void clickOnAID(String AID);

    public abstract void clickOnApproveButton();

    public abstract void clickOnClosePopUpButton();

    public abstract void clickOnCheckBox();

    public abstract void clickOnSubmitButton();

    public abstract boolean isSupervisorAuthenticationTitleDisplayed();

    public abstract void enterUserName(String username);

    public abstract void enterPassword(String password);

    public abstract void clickOnBackButton();
    
    public abstract boolean isApprovalButtonDisplayed();

}
