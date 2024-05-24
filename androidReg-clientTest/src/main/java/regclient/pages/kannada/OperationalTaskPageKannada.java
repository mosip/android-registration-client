package regclient.pages.kannada;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.OperationalTaskPage;
import regclient.page.SupervisorBiometricVerificationpage;
import regclient.pages.english.SupervisorBiometricVerificationpageEnglish;

public class OperationalTaskPageKannada extends OperationalTaskPage{

	@AndroidFindBy(accessibility = "ಆಪರೇಟರ್ ಬಯೋಮೆಟ್ರಿಕ್ಸ್ ಅನ್ನು ನವೀಕರಿಸಿ")
	private WebElement updateOperatorBiometricsButton;
	
	@AndroidFindBy(accessibility = "System Storage Usage")
	private WebElement systemStorageUsageTitle;
	
	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"ಡೇಟಾವನ್ನು ಸಿಂಕ್ರೊನೈಸ್ ಮಾಡಿ\")")
	private WebElement synchronizeDataButton ;

	
	public OperationalTaskPageKannada(AppiumDriver driver) {
		super(driver);
	}

	public  SupervisorBiometricVerificationpage clickOnUpdateOperatorBiometricsButton() {
		clickOnElement(updateOperatorBiometricsButton);
		return new SupervisorBiometricVerificationpageEnglish(driver);
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
		if(contentDesc.contains("ಡೇಟಾವನ್ನು ಸಿಂಕ್ರೊನೈಸ್ ಮಾಡಿ\n"+getCurrentDateWord()+","))
			return true;
		else
			return false;
	}

}
