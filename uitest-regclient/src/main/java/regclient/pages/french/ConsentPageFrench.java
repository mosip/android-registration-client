package regclient.pages.french;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.ConsentPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.RegistrationTasksPage;


public class ConsentPageFrench extends ConsentPage{
	
	@AndroidFindBy(accessibility = "Consentement")
	private WebElement consentPage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().className(\"android.widget.CheckBox\"))")
	private WebElement termAndConditionCheckBox;

	@AndroidFindBy(accessibility = "INFORMÉ")
	private WebElement informedButton;
	
	@AndroidFindBy(accessibility = "ANNULER")
	private WebElement cancelButton;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"/J'ai lu et j'accepte les termes et conditions pour partager mes PII \"))")
	private WebElement checkBoxDiscription;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Mettre à jour l'UIN\"))")
	private WebElement updateUINTitle;



	public ConsentPageFrench(AppiumDriver driver) {
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
		return new DemographicDetailsPageFrench(driver);
	}
	
	public RegistrationTasksPage clickOnCancelButton() {
		clickOnElement(cancelButton);
		return new  RegistrationTasksPageFrench(driver);
	}

	public boolean updateUINTitleDisplayed() {
		return isElementDisplayed(updateUINTitle);
	}

}
