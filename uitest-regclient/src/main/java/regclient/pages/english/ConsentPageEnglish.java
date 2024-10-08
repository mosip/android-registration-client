package regclient.pages.english;


import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.ConsentPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.RegistrationTasksPage;


public class ConsentPageEnglish extends ConsentPage{

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Consent\"))")
	private WebElement consentPage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().className(\"android.widget.CheckBox\"))")
	private WebElement termAndConditionCheckBox;

	@AndroidFindBy(accessibility = "INFORMED")
	private WebElement informedButton;
	
	@AndroidFindBy(accessibility = "CANCEL")
	private WebElement cancelButton;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"I have read and accept terms and conditions to share my PII\"))")
	private WebElement checkBoxDiscription;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Update UIN\"))")
	private WebElement updateUINTitle;

	public ConsentPageEnglish(AppiumDriver driver) {
		super(driver);
	}

	public boolean isConsentPageDisplayed() {
		return isElementDisplayed(consentPage);
	}

	public boolean isCheckBoxReadable() {
		return isElementDisplayed(checkBoxDiscription);
	}
	
	public  boolean isInformedButtonEnabled() {
		return isElementEnabled(informedButton);
	}

	public  DemographicDetailsPage clickOnInformedButton() {
		clickOnElement(informedButton);
		return new DemographicDetailsPageEnglish(driver);
	}


	public RegistrationTasksPage clickOnCancelButton() {
		clickOnElement(cancelButton);
		return new  RegistrationTasksPageEnglish(driver);
	}
	
	public boolean updateUINTitleDisplayed() {
		return isElementDisplayed(updateUINTitle);
	}
	
}
