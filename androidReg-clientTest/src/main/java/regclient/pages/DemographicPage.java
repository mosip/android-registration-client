package regclient.pages;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

public class DemographicPage extends BasePage{

	@AndroidFindBy(accessibility = "Demographic Details")
	private WebElement demographicdetails;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(0)")
	private WebElement firstNameTextBox;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(1)")
	private WebElement firstNameTextBoxSecondlang;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(2)")
	private WebElement lasttNameTextBox;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(3)")
	private WebElement lastNameTextBoxSecondLang;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(4)")
	private WebElement ageTextBox;
	
	@AndroidFindBy(accessibility = "Male")
	private WebElement male;
	
	@AndroidFindBy(accessibility = "Female")
	private WebElement female;
	
	@AndroidFindBy(accessibility = "Select Option")
	private WebElement selectMaritalStatus;
	
	@AndroidFindBy(accessibility = "Single")
	private WebElement single;
	
	@AndroidFindBy(accessibility = "CONTINUE")
	private WebElement continuebutton;

	public DemographicPage(AppiumDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	public boolean isDemographicDetailsPageDisplay() {

		return isElementDisplayed(demographicdetails);
	}

	public  void enterFirstName(String firstName) {
		clickAndsendKeysToTextBox(firstNameTextBox,firstName);
	}

	public  void enterLastName(String lastName) {
		clickAndsendKeysToTextBox(lasttNameTextBox,lastName);
	}
	
	public  void enterAge(String age) {
		clickAndsendKeysToTextBox(ageTextBox,age);
	}

	public boolean getenterfirstNameTextBoxSecondlang() {
		if(getTextFromLocator(firstNameTextBoxSecondlang)==null)
			return	false;
		else
			return	true;
	}
	
	public boolean getenterLastNameTextBoxSecondlang() {
		if(getTextFromLocator(lastNameTextBoxSecondLang)==null)
			return	false;
		else
			return	true;
	}
	
	public  void SelectGender(String gender) {
		if(gender.equalsIgnoreCase("male"))
		clickOnElement(male);
		if(gender.equalsIgnoreCase("female"))
			clickOnElement(female);
	}
	
	public  void SelectMaritalStatus() {
		clickOnElement(selectMaritalStatus);
		clickOnElement(single);
	}
	
	public  AddressAndContactPage clickOnContinueButton() {
		clickOnElement(continuebutton);
		return new AddressAndContactPage(driver);
		
	}

}
