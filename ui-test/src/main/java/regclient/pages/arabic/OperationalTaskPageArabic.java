package regclient.pages.arabic;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.OperationalTaskPage;
import regclient.page.SupervisorBiometricVerificationpage;

public class OperationalTaskPageArabic extends OperationalTaskPage{

	@AndroidFindBy(accessibility = "تحديث القياسات الحيوية للمشغل")
	private WebElement updateOperatorBiometricsButton;
	
	@AndroidFindBy(accessibility = "System Storage Usage")
	private WebElement systemStorageUsageTitle;
	
	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"مزامنة البيانات\")")
	private WebElement synchronizeDataButton ;
	
	@AndroidFindBy(accessibility = "تحميل التطبيق")
	private WebElement applicationUploadTitle;
	
	@AndroidFindBy(accessibility = "ما زال يحتاج بتصدير")
	private WebElement pendingApprovalTitle;
	
	public OperationalTaskPageArabic(AppiumDriver driver) {
		super(driver);
	}

	public  SupervisorBiometricVerificationpage clickOnUpdateOperatorBiometricsButton() {
		clickOnElement(updateOperatorBiometricsButton);
		return new SupervisorBiometricVerificationpageArabic(driver);
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
		if(contentDesc.contains("مزامنة البيانات\n"+getCurrentDateWord()+","))
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
