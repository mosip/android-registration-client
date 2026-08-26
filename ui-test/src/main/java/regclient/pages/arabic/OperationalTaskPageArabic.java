package regclient.pages.arabic;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.OperationalTaskPage;
import regclient.page.SupervisorBiometricVerificationpage;

public class OperationalTaskPageArabic extends OperationalTaskPage {

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionMatches(\".*(تحديث التحقق البيومتري للضابط|تحديث المشرف الحيوي).*\")")
	private WebElement updateOperatorBiometricsButton;

	@AndroidFindBy(accessibility = "System Storage Usage")
	private WebElement systemStorageUsageTitle;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"مزامنة البيانات\")")
	private WebElement synchronizeDataButton;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"تحميل التطبيق\")")
	private WebElement applicationUploadTitle;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"ما زال يحتاج بتصدير\")")
	private WebElement pendingApprovalTitle;

	@AndroidFindBy(accessibility = "المهام التشغيلية")
	private WebElement operationalTaskPageTitle;

	@AndroidFindBy(accessibility = "مزامنة. تم بنجاح")
	private WebElement syncCompletedPopup;

	@AndroidFindBy(accessibility = "إعادة التشغيل")
	private WebElement restartButton;

	public OperationalTaskPageArabic(AppiumDriver driver) {
		super(driver);
	}

	public SupervisorBiometricVerificationpage clickOnUpdateOperatorBiometricsButton() {
		By updateOperatorBiometricsButton = MobileBy.AndroidUIAutomator("new UiSelector().descriptionMatches(\".*(تحديث التحقق البيومتري للضابط|تحديث المشرف الحيوي).*\")");
		click(updateOperatorBiometricsButton);
		return new SupervisorBiometricVerificationpageArabic(driver);
	}

	public boolean isOperationalTaskPageLoaded() {
		return isElementDisplayed(operationalTaskPageTitle);
	}

	public void clickSynchronizeDataButton() {
		clickOnElement(synchronizeDataButton);
		waitTime(50);
	}

	public boolean checkLastSyncDate() {
		String contentDesc = synchronizeDataButton.getAttribute("content-desc");
		if (contentDesc.contains("مزامنة البيانات\n" + getCurrentDateWord() + ","))
			return true;
		else
			return false;
	}

	public void clickApplicationUploadTitle() {
		clickOnElement(applicationUploadTitle);
	}

	public boolean isApplicationUploadTitleDisplayed() {
		if (!isElementDisplayedOnScreen(applicationUploadTitle)) {
			swipeUp();
		}
		return isElementDisplayed(applicationUploadTitle);
	}

	public void clickPendingApprovalTitle() {
		clickOnElement(pendingApprovalTitle);
	}

	public boolean isPendingApprovalTitleDisplayed() {
		if (!isElementDisplayedOnScreen(pendingApprovalTitle)) {
			swipeUp();
		}
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
