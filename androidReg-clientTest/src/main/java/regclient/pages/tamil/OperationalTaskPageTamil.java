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

}
