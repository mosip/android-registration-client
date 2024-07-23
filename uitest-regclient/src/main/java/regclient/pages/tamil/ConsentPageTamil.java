package regclient.pages.tamil;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.ConsentPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.RegistrationTasksPage;


public class ConsentPageTamil extends ConsentPage{

	@AndroidFindBy(accessibility = "ஒப்புதல்")
	private WebElement consentPage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().className(\"android.widget.CheckBox\"))")
	private WebElement termAndConditionCheckBox;

	@AndroidFindBy(accessibility = "அறிந்தவர்")
	private WebElement informedButton;
	
	@AndroidFindBy(accessibility = "ரத்துசெய்")
	private WebElement cancelButton;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"நான் என் பி ஐ ஐ ஐப் பகிர்ந்து கொள்வதற்கான விதிமுறைகளையும் நிபந்தனைகளையும் படித்து ஏற்றுக்கொண்டேன்\"))")
	private WebElement checkBoxDiscription;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"UIN ஐப் புதுப்பிக்கவும்\"))")
	private WebElement updateUINTitle;

	
	public ConsentPageTamil(AppiumDriver driver) {
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
		return new DemographicDetailsPageTamil(driver);
	}

	
	public RegistrationTasksPage clickOnCancelButton() {
		clickOnElement(cancelButton);
		return new  RegistrationTasksPageTamil(driver);
	}
	
	public boolean updateUINTitleDisplayed() {
		return isElementDisplayed(updateUINTitle);
	}
}
