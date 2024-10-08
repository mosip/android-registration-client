package regclient.pages.arabic;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.ConsentPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.RegistrationTasksPage;



public class ConsentPageArabic extends ConsentPage {

	@AndroidFindBy(accessibility = "موافقة")
	private WebElement consentPage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().className(\"android.widget.CheckBox\"))")
	private WebElement termAndConditionCheckBox;

	@AndroidFindBy(accessibility = "أُبلغ")
	private WebElement informedButton;
	
	@AndroidFindBy(accessibility = "يلغي")
	private WebElement cancelButton;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"الاسم الكامل الكامل الكامل\"))")
	private WebElement checkBoxDiscription;
		
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"تحديث UIN\"))")
	private WebElement updateUINTitle;


	public ConsentPageArabic(AppiumDriver driver) {
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
		return new DemographicDetailsPageArabic(driver);
	}
	
	public RegistrationTasksPage clickOnCancelButton() {
		clickOnElement(cancelButton);
		return new  RegistrationTasksPageArabic(driver);
	}

	public boolean updateUINTitleDisplayed() {
		return isElementDisplayed(updateUINTitle);
	}

}
