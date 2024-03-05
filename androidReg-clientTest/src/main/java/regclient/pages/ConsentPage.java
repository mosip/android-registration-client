package regclient.pages;


import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
;

public class ConsentPage extends BasePage{

	@AndroidFindBy(accessibility = "Consent")
	private WebElement consentPage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().className(\"android.widget.CheckBox\"))")
	private WebElement termAndConditionCheckBox;

	@AndroidFindBy(accessibility = "INFORMED")
	private WebElement informedButton;

	public ConsentPage(AppiumDriver driver) {
		super(driver);
	}

	public boolean isConsentPageDisplayed() {
		return isElementDisplayed(consentPage);
	}

	public  void selectTermAndConditionCheckbox() {
		clickOnElement(termAndConditionCheckBox);
	}

	public  boolean isInformedButtonEnabled() {
		return isElementEnabled(informedButton);
	}

	public  DemographicPage clickOnInformedButton() {
		clickOnElement(informedButton);
		return new DemographicPage(driver);
	}
}
