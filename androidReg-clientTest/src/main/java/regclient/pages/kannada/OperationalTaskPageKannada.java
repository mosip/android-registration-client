package regclient.pages.kannada;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.OperationalTaskPage;
import regclient.page.SupervisorBiometricVerificationpage;

public class OperationalTaskPageKannada extends OperationalTaskPage{

	@AndroidFindBy(accessibility = "ಆಪರೇಟರ್ ಬಯೋಮೆಟ್ರಿಕ್ಸ್ ಅನ್ನು ನವೀಕರಿಸಿ")
	private WebElement updateOperatorBiometricsButton;
	
	@AndroidFindBy(accessibility = "System Storage Usage")
	private WebElement systemStorageUsageTitle;
	
	public OperationalTaskPageKannada(AppiumDriver driver) {
		super(driver);
	}

	public  SupervisorBiometricVerificationpage clickOnUpdateOperatorBiometricsButton() {
		clickOnElement(updateOperatorBiometricsButton);
		return new SupervisorBiometricVerificationpageKannada(driver);
	}

	public boolean isOperationalTaskPageLoaded() {
		return isElementDisplayed(systemStorageUsageTitle);
	}

}
