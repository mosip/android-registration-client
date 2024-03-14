package regclient.pages;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;

public class DemographicDetailsPage extends BasePage{

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"Demographic Details\"))")
	private WebElement demographicDetailsTitle;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(0)")
	private WebElement fullNameTextBox;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(1)")
	private WebElement fullNameTextBoxSecondLanguage;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(2)")
	private WebElement ageTextBox;

	@AndroidFindBy(xpath = "//android.widget.ScrollView/android.view.View[6]/android.view.View[2]/android.view.View/android.widget.EditText")
	private WebElement addressLine1TextBox;

	@AndroidFindBy(xpath = "//android.widget.ScrollView/android.view.View[5]/android.view.View[2]/android.view.View/android.widget.EditText[1]")
	private WebElement addressLine2TextBox;

	@AndroidFindBy(xpath = "//android.widget.ScrollView/android.view.View[4]/android.view.View[2]/android.view.View/android.widget.EditText[1]")
	private WebElement addressLine3TextBox;

	@AndroidFindBy(accessibility = "Male")
	private WebElement maleButton;

	@AndroidFindBy(accessibility = "Female")
	private WebElement femaleButton;

	@AndroidFindBy(xpath = "(//android.widget.Button[@content-desc=\"Select Option\"])[1]")
	private WebElement selectMaritalStatus;

	@AndroidFindBy(xpath = "(//android.widget.Button[@content-desc=\"Select Option\"])[2]")
	private WebElement selectResidentStatus;

	@AndroidFindBy(xpath = "(//android.widget.Button[@content-desc=\"Select Option\"])[2]")
	private WebElement selectRegionStatus;

	@AndroidFindBy(xpath = "(//android.widget.Button[@content-desc=\"Select Option\"])[2]")
	private WebElement selectProvinceStatus;

	@AndroidFindBy(xpath = "//android.widget.Button[@content-desc=\"Select Option\"]")
	private WebElement selectCityStatus;

	@AndroidFindBy(xpath = "//android.widget.Button[@content-desc=\"Select Option\"]")
	private WebElement selectZoneStatus;

	@AndroidFindBy(xpath = "//android.widget.Button[@content-desc=\"Select Option\"]")
	private WebElement selectPostal;

	@AndroidFindBy(accessibility = "14022")
	private WebElement selectPostalCode;

	@AndroidFindBy(accessibility = "Non-Foreigner")
	private WebElement nonForeignerValueFromDropdown;

	@AndroidFindBy(accessibility = "Foreigner")
	private WebElement foreignerValueFromDropdown;

	@AndroidFindBy(accessibility = "Single")
	private WebElement singleValueFromDropdown;

	@AndroidFindBy(accessibility = "Rabat Sale Kenitra")
	private WebElement rabatSaleKenitraValueFromDropdown;

	@AndroidFindBy(xpath = "//android.view.View[@content-desc=\"Kenitra\"]")
	private WebElement kenitraValueFromDropdown;

	@AndroidFindBy(accessibility = "Rabat")
	private WebElement rabatValueFromDropdown;

	@AndroidFindBy(accessibility = "Ben Mansour")
	private WebElement benMansourFromDropdown;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Residence Status\"))")
	private WebElement residenceStatusHeader;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Region\"))")
	private WebElement regionHeader;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Province\"))")
	private WebElement provinceHeader;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"City\"))")
	private WebElement cityHeader;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Zone\"))")
	private WebElement zoneHeader;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Postal\"))")
	private WebElement postalCodeHeader;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Phone\"))")
	private WebElement mobileNumberHeader;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Email\"))")
	private WebElement emailHeader;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(0)")
	private WebElement mobileNumberTextBox;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(1)")
	private WebElement emailIdTextBox;

	@AndroidFindBy(accessibility = "CONTINUE")
	private WebElement continueButton;

	public DemographicDetailsPage(AppiumDriver driver) {
		super(driver);
	}

	public boolean isDemographicDetailsPageDisplayed() {
		return isElementDisplayed(demographicDetailsTitle);
	}

	public  void enterFullName(String fullName) {
		clickAndsendKeysToTextBox(fullNameTextBox,fullName);
	}

	public  void enterAddressLine1(String addressLine1) {
		clickAndsendKeysToTextBoxByCmd(addressLine1TextBox,addressLine1);
	}

	public  void enterAddressLine2(String addressLine2) {
		clickAndsendKeysToTextBoxByCmd(addressLine2TextBox,addressLine2);
	}

	public  void enterAddressLine3(String addressLine3) {
		clickAndsendKeysToTextBoxByCmd(addressLine3TextBox,addressLine3);
	}

	public  void enterAge(String age) {
		clickAndsendKeysToTextBox(ageTextBox,age);
	}

	public boolean checkFullNameSecondLanguageTextBoxNotNull() {
		if(getTextFromLocator(fullNameTextBoxSecondLanguage)==null || getTextFromLocator(fullNameTextBoxSecondLanguage)=="" )
			return	false;
		else
			return	true;
	}

	public boolean isResidenceStatusHeaderDisplayed() {
		return isElementDisplayed(residenceStatusHeader);
	}

	public boolean isRegionHeaderDisplayed() {
		return isElementDisplayed(regionHeader);
	}

	public boolean isProvinceHeaderDisplayed() {
		return isElementDisplayed(provinceHeader);
	}

	public boolean isCityHeaderDisplayed() {
		return isElementDisplayed(cityHeader);
	}
	
	public boolean isPostalCodeHeaderDisplayed() {
		return isElementDisplayed(postalCodeHeader);
	}
	
	public boolean isMobileNumberHeaderDisplayed() {
		return isElementDisplayed(mobileNumberHeader);
	}
	
	public boolean isZoneHeaderDisplayed() {
		return isElementDisplayed(cityHeader);
	}

	public boolean isEmailHeaderDisplayed() {
		return isElementDisplayed(emailHeader);
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

	public  void selectResidenceStatus(String ResidenceStatus) {
		clickOnElement(selectResidentStatus);
		if(ResidenceStatus.equalsIgnoreCase("Foreigner"))
			clickOnElement(foreignerValueFromDropdown);
		else
			clickOnElement(nonForeignerValueFromDropdown);
	}

	public  void selectRegionStatus(String region) {
		clickOnElement(selectRegionStatus);
		if(region.equalsIgnoreCase("Rabat Sale Kenitra"))
			clickOnElement(rabatSaleKenitraValueFromDropdown);
	}

	public  void selectProvinceStatus(String province) {
		clickOnElement(selectProvinceStatus);
		if(province.equalsIgnoreCase("Kenitra"))
			clickOnElement(kenitraValueFromDropdown);
		if(province.equalsIgnoreCase("Rabat"))
			clickOnElement(rabatValueFromDropdown);
	}

	public  void selectCityStatus(String city) {
		clickOnElement(selectCityStatus);
		if(city.equalsIgnoreCase("Kenitra"))
			clickOnElement(kenitraValueFromDropdown);
		if(city.equalsIgnoreCase("Rabat"))
			clickOnElement(rabatValueFromDropdown);
	}

	public  void selectZoneStatus() {
		clickOnElement(selectZoneStatus);
		clickOnElement(benMansourFromDropdown);
	}

	public  void selectPostalStatus() {
		clickOnElement(selectPostal);
		clickOnElement(selectPostalCode);
	}

	public  DocumentuploadPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new DocumentuploadPage(driver);

	}

	public  void enterMobileNumber(String mobileNumber) {
		clickAndsendKeysToTextBox(mobileNumberTextBox,mobileNumber);
	}

	public  void enterEmailID(String EmailID) {
		clickAndsendKeysToTextBox(emailIdTextBox,EmailID);
	}

}
