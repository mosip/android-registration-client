package regclient.pages.tamil;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.OperationalTaskPage;
import regclient.page.SupervisorBiometricVerificationpage;


public class OperationalTaskPageTamil extends OperationalTaskPage{

	@AndroidFindBy(accessibility = "ஆபரேட்டர் பயோமெட்ரிக்ஸைப் புதுப்பிக்கவும்")
	private WebElement updateOperatorBiometricsButton;
	
	@AndroidFindBy(accessibility = "System Storage Usage")
	private WebElement systemStorageUsageTitle;
	
	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"தரவை ஒத்திசைக்கவும்\")")
	private WebElement synchronizeDataButton ;

	@AndroidFindBy(accessibility = "விண்ணப்பப் பதிவேற்றம்")
	private WebElement applicationUploadTitle;
	
	@AndroidFindBy(accessibility = "நிலுவையிலுள்ள ஒப்புதல்")
	private WebElement pendingApprovalTitle;
	
	public OperationalTaskPageTamil(AppiumDriver driver) {
		super(driver);
	}

	public  SupervisorBiometricVerificationpage clickOnUpdateOperatorBiometricsButton() {
		clickOnElement(updateOperatorBiometricsButton);
		return new SupervisorBiometricVerificationpageTamil(driver);

	}

	public boolean isOperationalTaskPageLoaded() {
		return isElementDisplayed(systemStorageUsageTitle);
	}


	public  void clickSynchronizeDataButton() {
		clickOnElement(synchronizeDataButton);
		waitTime(50);
	}
	
	public boolean checkLastSyncDate() {
		String contentDesc = synchronizeDataButton.getAttribute("content-desc");
		if(contentDesc.contains("தரவை ஒத்திசைக்கவும்\n"+getCurrentDateWord()+","))
			return true;
		else
			return false;
	}
	
	public  void clickApplicationUploadTitle() {
		clickOnElement(applicationUploadTitle);
	}
	
	public boolean isApplicationUploadTitleDisplayed() {
		if(!isElementDisplayedOnScreen(applicationUploadTitle)) {
			swipeOrScroll();
		}
		return isElementDisplayed(applicationUploadTitle);
	}
	
	public  void clickPendingApprovalTitle() {
		clickOnElement(pendingApprovalTitle);
	}
	
	public boolean isPendingApprovalTitleDisplayed() {
		if(!isElementDisplayedOnScreen(pendingApprovalTitle)) {
			swipeOrScroll();
		}
		return isElementDisplayed(pendingApprovalTitle);
	}

}
