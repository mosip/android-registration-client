package regclient.pages.tamil;


import static org.testng.Assert.assertTrue;

import java.util.List;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.api.FetchUiSpec;
import regclient.page.ConsentPage;
import regclient.page.UpdateUINPage;
import regclient.pages.english.ConsentPageEnglish;

public class UpdateUINPageTamil extends UpdateUINPage{
	
	@AndroidFindBy(xpath = "//android.widget.EditText")
	private WebElement UINNumberTextBox;
	
	@AndroidFindBy(accessibility = "தொடர்க")
	private WebElement continueButton;
	
	@AndroidFindBy(accessibility = "சரியான UIN ஐ உள்ளிடவும்")
	private WebElement invalidUINErrorMessage;

	public UpdateUINPageTamil(AppiumDriver driver) {
		super(driver);
	}
	
	@SuppressWarnings("deprecation")
	public boolean isUpdateMyUINTitleDisplayed() {
		return isElementDisplayed (findElementWithRetry(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"" + FetchUiSpec.getTitleUsingId("UPDATE") + "\"))")));
	}
	public  void enterUIN(String UIN) {
		clickAndsendKeysToTextBox(UINNumberTextBox,UIN);
	}

	public  ConsentPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new ConsentPageEnglish(driver);
	}
	
	public boolean isInvalidUINErrorMessageDisplayed() {
		return isElementDisplayed(invalidUINErrorMessage);
	}
	
	public void selectUpdateValue(String page) {
		List<String> groupLabelList=FetchUiSpec.getAllGroupLabelUsingId(page);
		for(String title : groupLabelList) {
				WebElement webelement =findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+title+"\")"));
				assertTrue(isElementDisplayed(webelement),"Verify if "+title+" title is displayed in update uin page");
				clickOnElement(webelement);			
			}		
	}
	
	public void selectUpdateIntroducerDetails() {
		WebElement webelement =findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("introducerName")+"\")"));
		assertTrue(isElementDisplayed(webelement),"Verify if "+FetchUiSpec.getGroupValueUsingId("introducerName")+" title is displayed in update uin page");
		clickOnElement(webelement);			
	}
}
