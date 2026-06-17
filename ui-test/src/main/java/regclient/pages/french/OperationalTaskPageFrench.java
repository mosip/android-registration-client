package regclient.pages.french;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.OperationalTaskPage;
import regclient.page.SupervisorBiometricVerificationpage;

public class OperationalTaskPageFrench extends OperationalTaskPage {

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"Mettre à jour les données biométriques\")")
	private WebElement updateOperatorBiometricsButton;

	@AndroidFindBy(accessibility = "Tâches opérationnelles")
	private WebElement operationalTaskTitle;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"Synchroniser les données\")")
	private WebElement synchronizeDataButton;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"Téléchargement d'application\")")
	private WebElement applicationUploadTitle;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"En attente de validation\")")
	private WebElement pendingApprovalTitle;

	@AndroidFindBy(accessibility = "Synchro. Complété avec succès")
	private WebElement syncCompletedPopup;

	@AndroidFindBy(accessibility = "Redémarrer")
	private WebElement restartButton;

	public OperationalTaskPageFrench(AppiumDriver driver) {
		super(driver);
	}

	public SupervisorBiometricVerificationpage clickOnUpdateOperatorBiometricsButton() {
		clickOnElement(updateOperatorBiometricsButton);
		return new SupervisorBiometricVerificationpageFrench(driver);

	}

	public boolean isOperationalTaskPageLoaded() {
		return isElementDisplayed(operationalTaskTitle);
	}

	public void clickSynchronizeDataButton() {
		clickOnElement(synchronizeDataButton);
		waitTime(50);
	}

	public boolean checkLastSyncDate() {
		String contentDesc = synchronizeDataButton.getAttribute("content-desc");
		if (contentDesc.contains("Synchroniser les données\n" + getCurrentDateWord() + ","))
			return true;
		else
			return false;
	}

	public void clickApplicationUploadTitle() {
		clickOnElement(applicationUploadTitle);
	}

	public boolean isApplicationUploadTitleDisplayed() {
		swipeUp();
		return isElementDisplayed(applicationUploadTitle);
	}

	public void clickPendingApprovalTitle() {
		clickOnElement(pendingApprovalTitle);
	}

	public boolean isPendingApprovalTitleDisplayed() {
		swipeUp();
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
