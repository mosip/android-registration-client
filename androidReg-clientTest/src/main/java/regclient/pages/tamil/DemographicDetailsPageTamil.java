package regclient.pages.tamil;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.ConsentPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.DocumentUploadPage;


public class DemographicDetailsPageTamil extends DemographicDetailsPage{

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"முழு பெயர்\"))")
	private WebElement demographicDetailspage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"ஒப்புதல்\"))")
	private WebElement consentTitle;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(0)")
	private WebElement fullNameTextBox;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(1)")
	private WebElement fullNameTextBoxSecondLanguage;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(2)")
	private WebElement ageTextBox;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"நிரந்தர முகவரி1\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")
	private WebElement addressLine1TextBox;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"தற்காலிக முகவரி\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")
	private WebElement addressLine2TextBox;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"தற்காலிக முகவரி3\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")
	private WebElement addressLine3TextBox;

	@AndroidFindBy(accessibility = "ஆண்")
	private WebElement maleButton;

	@AndroidFindBy(accessibility = "பெண்")
	private WebElement femaleButton;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"சிவில் நிலை\")]/parent::android.view.View/parent::android.widget.Button")
	private WebElement selectMaritalStatus;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"குடியிருப்பு நிலை\")]/parent::android.view.View/parent::android.widget.Button")
	private WebElement selectResidentStatus;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"பிராந்தியம்\")]/parent::android.view.View/parent::android.widget.Button")
	private WebElement selectRegionStatus;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"மாகாணம்\")]/parent::android.view.View/parent::android.widget.Button")
	private WebElement selectProvinceStatus;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"நகரம்\")]/parent::android.view.View/parent::android.widget.Button")
	private WebElement selectCityStatus;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"மண்டலம்\")]/parent::android.view.View/parent::android.widget.Button")
	private WebElement selectZoneStatus;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"அஞ்சல்\")]/parent::android.view.View/parent::android.widget.Button")
	private WebElement selectPostal;

	@AndroidFindBy(accessibility = "14022")
	private WebElement selectPostalCode;

	@AndroidFindBy(accessibility = "வெளிநாட்டவர் அல்லாதவர்")
	private WebElement nonForeignerValueFromDropdown;

	@AndroidFindBy(accessibility = "வெளிநாட்டவர்")
	private WebElement foreignerValueFromDropdown;

	@AndroidFindBy(accessibility = "ஒற்றை")
	private WebElement singleValueFromDropdown;

	@AndroidFindBy(accessibility = "ரபத் சேல் கெனிட்ரா")
	private WebElement rabatSaleKenitraValueFromDropdown;

	@AndroidFindBy(accessibility = "கெனிட்ரா")
	private WebElement kenitraValueFromDropdown;

	@AndroidFindBy(accessibility = "ரபாத்")
	private WebElement rabatValueFromDropdown;

	@AndroidFindBy(accessibility = "பென் மன்சூர்")
	private WebElement benMansourFromDropdown;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"குடியிருப்பு நிலை\"))")
	private WebElement residenceStatusHeader;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"பிராந்தியம்\"))")
	private WebElement regionHeader;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"மாகாணம்\"))")
	private WebElement provinceHeader;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"நகரம்\"))")
	private WebElement cityHeader;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"மண்டலம்\"))")
	private WebElement zoneHeader;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"அஞ்சல்\"))")
	private WebElement postalCodeHeader;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"தொலைபேசி\"))")
	private WebElement mobileNumberHeader;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"மின்னஞ்சல்\"))")
	private WebElement emailHeader;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"அறிமுகம் பெயர்\"))")
	private WebElement introducerNameHeader;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"அறிமுகம் RID\"))")
	private WebElement introducerRidHeader;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"தொலைபேசி\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText")
	private WebElement mobileNumberTextBox;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"மின்னஞ்சல்\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")
	private WebElement emailIdTextBox;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"அறிமுகம் பெயர்\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")
	private WebElement introducerNameTextBox;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"அறிமுகம் RID\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")
	private WebElement introducerRidTextBox;

	@AndroidFindBy(accessibility = "தொடர்க")
	private WebElement continueButton;
	
	@AndroidFindBy(accessibility = "ಅಮಾನ್ಯ ಇನ್‌ಪುಟ್")
	private WebElement errorMessageInvalidInputText;

	public DemographicDetailsPageTamil(AppiumDriver driver) {
		super(driver);
	}

	public boolean isDemographicDetailsPageDisplayed() {
		return isElementDisplayed(demographicDetailspage);
	}
	
	public boolean isErrorMessageInvalidInputTextDisplayed() {
		return isElementDisplayed(errorMessageInvalidInputText);
	}
	
	public  ConsentPage clickOnConsentPageTitle() {
		clickOnElement(consentTitle);
		return new ConsentPageTamil(driver);
	}

	public  void enterFullName(String fullName) {
		clickAndsendKeysToTextBox(fullNameTextBox,fullName);
	}

	public  void enterAddressLine1(String addressLine1) {
		if(!isElementDisplayedOnScreen(addressLine1TextBox)) {
			swipeOrScroll();
		}
		clickAndsendKeysToTextBox(addressLine1TextBox,addressLine1);
	}

	public  void enterAddressLine2(String addressLine2) {
		if(!isElementDisplayedOnScreen(addressLine2TextBox)) {
			swipeOrScroll();
		}
		clickAndsendKeysToTextBox(addressLine2TextBox,addressLine2);
	}

	public  void enterAddressLine3(String addressLine3) {
		if(!isElementDisplayedOnScreen(addressLine3TextBox)) {
			swipeOrScroll();
		}
		clickAndsendKeysToTextBox(addressLine3TextBox,addressLine3);
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
		return isElementDisplayed(zoneHeader);
	}

	public boolean isEmailHeaderDisplayed() {
		return isElementDisplayed(emailHeader);
	}
	
	public boolean isIntroducerNameHeaderDisplayed() {
		return isElementDisplayed(introducerNameHeader);
	}
	
	public boolean isIntroducerRidHeaderDisplayed() {
		return isElementDisplayed(introducerRidHeader);
	}

	public  void selectGender(String gender) {
		if(gender.equalsIgnoreCase("male"))
			clickOnElement(maleButton);
		if(gender.equalsIgnoreCase("female"))
			clickOnElement(femaleButton);
	}

	public  void selectMaritalStatus() {
		clickOnElement(selectMaritalStatus);
		if(!isElementDisplayedOnScreen(singleValueFromDropdown)) {
			swipeOrScroll();
			clickOnElement(selectMaritalStatus);
			clickOnElement(singleValueFromDropdown);
		}else
			clickOnElement(singleValueFromDropdown);
	}

	public  void selectResidenceStatus(String ResidenceStatus) {
		clickOnElement(selectResidentStatus);
		if(ResidenceStatus.equalsIgnoreCase("Foreigner")) {
			if(!isElementDisplayedOnScreen(foreignerValueFromDropdown)) {
				swipeOrScroll();
				clickOnElement(selectResidentStatus);
				clickOnElement(foreignerValueFromDropdown);
			}else
				clickOnElement(foreignerValueFromDropdown);
		}else {
			if(!isElementDisplayedOnScreen(nonForeignerValueFromDropdown)) {
				swipeOrScroll();
				clickOnElement(selectResidentStatus);
				clickOnElement(nonForeignerValueFromDropdown);
			}else
				clickOnElement(nonForeignerValueFromDropdown);
		}

	}

	public  void selectRegionStatus(String region) {
		clickOnElement(selectRegionStatus);
		if(region.equalsIgnoreCase("Rabat Sale Kenitra")) {
			if(!isElementDisplayedOnScreen(rabatSaleKenitraValueFromDropdown)) {
				swipeOrScroll();
				clickOnElement(selectRegionStatus);
				clickOnElement(rabatSaleKenitraValueFromDropdown);
			}else {
				clickOnElement(rabatSaleKenitraValueFromDropdown);
			}
		}

	}

	public  void selectProvinceStatus(String province) {
		clickOnElement(selectProvinceStatus);
		if(province.equalsIgnoreCase("Kenitra"))
			if(!isElementDisplayedOnScreen(kenitraValueFromDropdown)) {
				swipeOrScroll();
				clickOnElement(selectProvinceStatus);
				clickOnElement(kenitraValueFromDropdown);
			}else
				clickOnElement(kenitraValueFromDropdown);
		if(province.equalsIgnoreCase("Rabat"))
			if(!isElementDisplayedOnScreen(rabatValueFromDropdown)) {
				swipeOrScroll();
				clickOnElement(selectProvinceStatus);
				clickOnElement(rabatValueFromDropdown);
			}else
				clickOnElement(rabatValueFromDropdown);
	}

	public  void selectCityStatus(String city) {
		clickOnElement(selectCityStatus);
		if(city.equalsIgnoreCase("Kenitra"))
			if(!isElementDisplayedOnScreen(kenitraValueFromDropdown)) {
				swipeOrScroll();
				clickOnElement(selectCityStatus);
				clickOnElement(kenitraValueFromDropdown);
			}else
				clickOnElement(kenitraValueFromDropdown);
		if(city.equalsIgnoreCase("Rabat"))
			if(!isElementDisplayedOnScreen(rabatValueFromDropdown)) {
				swipeOrScroll();
				clickOnElement(selectCityStatus);
				clickOnElement(rabatValueFromDropdown);
			}else
				clickOnElement(rabatValueFromDropdown);
	}

	public  void selectZoneStatus() {
		clickOnElement(selectZoneStatus);
		if(!isElementDisplayedOnScreen(benMansourFromDropdown)) {
			swipeOrScroll();
			clickOnElement(selectZoneStatus);
			clickOnElement(benMansourFromDropdown);
		}else
			clickOnElement(benMansourFromDropdown);
	}

	public  void selectPostalStatus() {
		clickOnElement(selectPostal);
		if(!isElementDisplayedOnScreen(selectPostalCode)) {
			swipeOrScroll();
			clickOnElement(selectPostal);
			clickOnElement(selectPostalCode);
		}else
			clickOnElement(selectPostalCode);
	}

	public  DocumentUploadPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new DocumentuploadPageTamil(driver);

	}

	public  void enterMobileNumber(String mobileNumber) {
		if(!isElementDisplayedOnScreen(mobileNumberTextBox)) {
			swipeOrScroll();
			clickAndsendKeysToTextBox(mobileNumberTextBox,mobileNumber);
		}else
		clickAndsendKeysToTextBox(mobileNumberTextBox,mobileNumber);
	}

	public  void enterEmailID(String EmailID) {
		if(!isElementDisplayedOnScreen(emailIdTextBox)) {
			swipeOrScroll();
			clickAndsendKeysToTextBox(emailIdTextBox,EmailID);
		}else
		clickAndsendKeysToTextBox(emailIdTextBox,EmailID);
	}
	
	public  void enterIntroducerName(String EmailID) {
		if(!isElementDisplayedOnScreen(introducerNameTextBox)) {
			swipeOrScroll();
			clickAndsendKeysToTextBox(introducerNameTextBox,EmailID);
		}else
		clickAndsendKeysToTextBox(introducerNameTextBox,EmailID);
	}
	
	public  void enterIntroducerRid(String EmailID) {
		if(!isElementDisplayedOnScreen(introducerRidTextBox)) {
			swipeOrScroll();
			clickAndsendKeysToTextBox(introducerRidTextBox,EmailID);
		}else
		clickAndsendKeysToTextBox(introducerRidTextBox,EmailID);
	}
}
