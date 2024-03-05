package regclient.pages;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;

public class DemographicPage extends BasePage{

	@AndroidFindBy(accessibility = "Demographic Details")
	private WebElement demographicDetails;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(0)")
	private WebElement firstNameTextBox;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(1)")
	private WebElement firstNameTextBoxSecondLanguage;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(2)")
	private WebElement lastNameTextBox;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(3)")
	private WebElement lastNameTextBoxSecondLanguage;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(4)")
	private WebElement ageTextBox;

	@AndroidFindBy(accessibility = "Male")
	private WebElement maleButton;

	@AndroidFindBy(accessibility = "Female")
	private WebElement femaleButton;

	@AndroidFindBy(accessibility = "Select Option")
	private WebElement selectMaritalStatus;

	@AndroidFindBy(accessibility = "Single")
	private WebElement singleValueFromDropdown;

	@AndroidFindBy(accessibility = "CONTINUE")
	private WebElement continueButton;

	public DemographicPage(AppiumDriver driver) {
		super(driver);
	}

	public boolean isDemographicDetailsPageDisplayed() {
		return isElementDisplayed(demographicDetails);
	}

	public  void enterFirstName(String firstName) {
		clickAndsendKeysToTextBox(firstNameTextBox,firstName);
	}

	public  void enterLastName(String lastName) {
		clickAndsendKeysToTextBox(lastNameTextBox,lastName);
	}

	public  void enterAge(String age) {
		clickAndsendKeysToTextBox(ageTextBox,age);
	}

	public boolean checkFirstNameSecondLanguageTextBoxNotNull() {
		if(getTextFromLocator(firstNameTextBoxSecondLanguage)==null || getTextFromLocator(firstNameTextBoxSecondLanguage)=="" )
			return	false;
		else
			return	true;
	}

	public boolean  checkLastNameSecondLanguageTextBoxNotNull() {
		if(getTextFromLocator(lastNameTextBoxSecondLanguage)==null || getTextFromLocator(lastNameTextBoxSecondLanguage)=="")
			return	false;
		else
			return	true;
	}

	public  void selectGender(String gender) {
		if(gender.equalsIgnoreCase("male"))
			clickOnElement(maleButton);
		if(gender.equalsIgnoreCase("female"))
			clickOnElement(femaleButton);
	}

	public  void selectMaritalStatus() {
		clickOnElement(selectMaritalStatus);
		clickOnElement(singleValueFromDropdown);
	}

	public  AddressAndContactPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new AddressAndContactPage(driver);

	}

}
