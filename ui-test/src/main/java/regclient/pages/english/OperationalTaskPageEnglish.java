package regclient.pages.english;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.OperationalTaskPage;
import regclient.page.SupervisorBiometricVerificationpage;

public class OperationalTaskPageEnglish extends OperationalTaskPage {

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"Supervisor's Biometric Update\")")
	private WebElement updateOperatorBiometricsButton;

	@AndroidFindBy(accessibility = "System Storage Usage")
	private WebElement systemStorageUsageTitle;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionStartsWith(\"Synchronize Data\")")
	private WebElement synchronizeDataButton;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"Application Upload\")")
	private WebElement applicationUploadTitle;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"Pending Approval\")")
	private WebElement pendingApprovalTitle;
	
	@AndroidFindBy(accessibility = "Sync Completed Successfully")
	private WebElement syncCompletedPopup;
	
	@AndroidFindBy(accessibility = "Restart")
	private WebElement restartButton;

	public OperationalTaskPageEnglish(AppiumDriver driver) {
		super(driver);
	}

	public SupervisorBiometricVerificationpage clickOnUpdateOperatorBiometricsButton() {
		clickOnElement(updateOperatorBiometricsButton);
		return new SupervisorBiometricVerificationpageEnglish(driver);
	}

	public boolean isOperationalTaskPageLoaded() {
		return true;
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

	public void clickApplicationUploadTitle() {
		clickOnElement(applicationUploadTitle);
	}

	public boolean isApplicationUploadTitleDisplayed() {
		return isElementDisplayed(applicationUploadTitle);
	}

	public void clickPendingApprovalTitle() {
		clickOnElement(pendingApprovalTitle);
	}

	public boolean isPendingApprovalTitleDisplayed() {
		return isElementDisplayed(pendingApprovalTitle);
	}
	
	public void handleIfSyncPopUpDisplayed() {
	    for (int i = 0; i < 120; i++) {
	        if (isElementDisplayed(syncCompletedPopup)) {
	            clickOnElement(restartButton);
	            return;
	        }
	        waitTime(5);
	    }
	    throw new RuntimeException("Sync popup not displayed");
	}

}
