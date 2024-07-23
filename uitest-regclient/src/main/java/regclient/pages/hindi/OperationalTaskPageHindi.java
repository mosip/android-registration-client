package regclient.pages.hindi;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.OperationalTaskPage;
import regclient.page.SupervisorBiometricVerificationpage;

public class OperationalTaskPageHindi extends OperationalTaskPage{

	@AndroidFindBy(accessibility = "ऑपरेटर बायोमेट्रिक्स अपडेट करें")
	private WebElement updateOperatorBiometricsButton;
	
	@AndroidFindBy(accessibility = "System Storage Usage")
	private WebElement systemStorageUsageTitle;
	
	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"डेटा सिंक्रनाइज़ करें\")")
	private WebElement synchronizeDataButton ;

	@AndroidFindBy(accessibility = "आवेदन अपलोड करें")
	private WebElement applicationUploadTitle;

	public OperationalTaskPageHindi(AppiumDriver driver) {
		super(driver);
	}

	public  SupervisorBiometricVerificationpage clickOnUpdateOperatorBiometricsButton() {
		clickOnElement(updateOperatorBiometricsButton);
		return new SupervisorBiometricVerificationpageHindi(driver);

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
		if(contentDesc.contains("डेटा सिंक्रनाइज़ करें\n"+getCurrentDateWord()+","))
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

}
