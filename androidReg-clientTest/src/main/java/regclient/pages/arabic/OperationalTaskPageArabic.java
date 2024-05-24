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

}
