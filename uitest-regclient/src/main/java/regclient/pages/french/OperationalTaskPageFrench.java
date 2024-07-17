package regclient.pages.french;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.OperationalTaskPage;
import regclient.page.SupervisorBiometricVerificationpage;


public class OperationalTaskPageFrench extends OperationalTaskPage{

	@AndroidFindBy(accessibility = "Mettre à jour les données biométriques de l'opérateur")
	private WebElement updateOperatorBiometricsButton;
	
	@AndroidFindBy(accessibility = "System Storage Usage")
	private WebElement systemStorageUsageTitle;
	
	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"Synchroniser les données\")")
	private WebElement synchronizeDataButton ;
	
	@AndroidFindBy(accessibility = "Téléchargement d'application")
	private WebElement applicationUploadTitle;


	public OperationalTaskPageFrench(AppiumDriver driver) {
		super(driver);
	}

	public  SupervisorBiometricVerificationpage clickOnUpdateOperatorBiometricsButton() {
		clickOnElement(updateOperatorBiometricsButton);
		return new SupervisorBiometricVerificationpageFrench(driver);

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
		if(contentDesc.contains("Synchroniser les données\n"+getCurrentDateWord()+","))
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
