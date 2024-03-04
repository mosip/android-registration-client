package regclient.pages;

import java.io.IOException;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

public class ConsentPage extends BasePage{

	@AndroidFindBy(accessibility = "Consent")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement Consentpage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().className(\"android.widget.CheckBox\"))")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement checkBox;
	
	@AndroidFindBy(accessibility = "INFORMED")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement informed;

	public ConsentPage(AppiumDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	public boolean isConsentpageDisplay() {

		return isElementDisplayed(Consentpage);
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
