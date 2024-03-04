package regclient.pages;

import java.io.IOException;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
;

public class ConsentPage extends BasePage{

	@AndroidFindBy(accessibility = "Consent")
	private WebElement consentpage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().className(\"android.widget.CheckBox\"))")
	private WebElement checkBox;
	
	@AndroidFindBy(accessibility = "INFORMED")
	private WebElement informed;

	public ConsentPage(AppiumDriver driver) {
		super(driver);
	}

	public boolean isConsentpageDisplay() {

		return isElementDisplayed(consentpage);
	}

	public  void clickOnCheckBoxButton() {
		clickOnElement(checkBox);
	}

	public  boolean isInformedButtonEnable() {
		return isElementEnabled(informed);
	}
	
	public  DemographicPage clickOnInformedButton() {
		clickOnElement(informed);
		return new DemographicPage(driver);
	}
}
