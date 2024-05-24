package regclient.pages.kannada;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.ConsentPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.DocumentUploadPage;
import regclient.pages.english.ConsentPageEnglish;
import regclient.pages.english.DocumentuploadPageEnglish;

public class DemographicDetailsPageKannada extends DemographicDetailsPage{

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"ಪೂರ್ಣ ಹೆಸರು\"))")
	private WebElement demographicDetailspage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"ಒಪ್ಪಿಗೆ\"))")
	private WebElement consentTitle;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(0)")
	private WebElement fullNameTextBox;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(1)")
	private WebElement fullNameTextBoxSecondLanguage;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(2)")
	private WebElement ageTextBox;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"ಶಾಶ್ವತ ವಿಳಾಸ1\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")
	private WebElement addressLine1TextBox;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"ತಾತ್ಕಾಲಿಕ ವಿಳಾಸ 2\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")
	private WebElement addressLine2TextBox;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"ತಾತ್ಕಾಲಿಕ ವಿಳಾಸ 3\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")
	private WebElement addressLine3TextBox;

	@AndroidFindBy(accessibility = "ಪುರುಷ")
	private WebElement maleButton;

	@AndroidFindBy(accessibility = "ಹೆಣ್ಣು")
	private WebElement femaleButton;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"ನಾಗರಿಕ ಸ್ಥಿತಿ\")]/parent::android.view.View/parent::android.widget.Button")
	private WebElement selectMaritalStatus;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"ನಿವಾಸ ಸ್ಥಿತಿ\")]/parent::android.view.View/parent::android.widget.Button")
	private WebElement selectResidentStatus;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"ಪ್ರದೇಶ\")]/parent::android.view.View/parent::android.widget.Button")
	private WebElement selectRegionStatus;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"ಪ್ರಾಂತ್ಯ\")]/parent::android.view.View/parent::android.widget.Button")
	private WebElement selectProvinceStatus;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"ಸಿಟಿ\")]/parent::android.view.View/parent::android.widget.Button")
	private WebElement selectCityStatus;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"ವಲಯ\")]/parent::android.view.View/parent::android.widget.Button")
	private WebElement selectZoneStatus;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"ಅಂಚೆ\")]/parent::android.view.View/parent::android.widget.Button")
	private WebElement selectPostal;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"14022\")")
	private WebElement selectPostalCode;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"ವಿದೇಶಿಯರಲ್ಲದವರು\")")
	private WebElement nonForeignerValueFromDropdown;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"ವಿದೇಶಿಗ\")")
	private WebElement foreignerValueFromDropdown;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"ಏಕ\")")
	private WebElement singleValueFromDropdown;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"ರಬತ್ ಸಲೆ ಕೆನಿತ್ರ\")")
	private WebElement rabatSaleKenitraValueFromDropdown;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"ಕೆನಿತ್ರಾ\")]")
	private WebElement kenitraValueFromDropdown;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"ರಬತ್\")]")
	private WebElement rabatValueFromDropdown;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"ಬಿನ್ ಮನ್ಸೂರ್\")")
	private WebElement benMansourFromDropdown;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"ನಿವಾಸ ಸ್ಥಿತಿ\")")
	private WebElement residenceStatusHeader;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"ಪ್ರದೇಶ\")")
	private WebElement regionHeader;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"ಪ್ರಾಂತ್ಯ\")")
	private WebElement provinceHeader;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"ಸಿಟಿ\")")
	private WebElement cityHeader;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"ವಲಯ\")")
	private WebElement zoneHeader;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"ಅಂಚೆ\")")
	private WebElement postalCodeHeader;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"ಫೋನ್\")")
	private WebElement mobileNumberHeader;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"ಇಮೇಲ್\")")
	private WebElement emailHeader;
	
	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"ಪರಿಚಯಕಾರರ ಹೆಸರು\")")
	private WebElement introducerNameHeader;
	
	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"ಪರಿಚಯಕಾರ ಆರ್ಐಡಿ\")")
	private WebElement introducerRidHeader;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"ಫೋನ್\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText")
	private WebElement mobileNumberTextBox;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"ಇಮೇಲ್\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")
	private WebElement emailIdTextBox;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"ಪರಿಚಯಕಾರರ ಹೆಸರು\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")
	private WebElement introducerNameTextBox;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"ಪರಿಚಯಕಾರ ಆರ್ಐಡಿ\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")
	private WebElement introducerRidTextBox;

	@AndroidFindBy(accessibility = "ಮುಂದುವರಿಸಿ")
	private WebElement continueButton;
	
	@AndroidFindBy(accessibility = "ಅಮಾನ್ಯ ಇನ್‌ಪುಟ್")
	private WebElement errorMessageInvalidInputText;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"DOB\")]/parent::android.view.View/following-sibling::android.view.View")
	private WebElement calenderIcon;
	
	@AndroidFindBy(accessibility = "ಸ್ಕ್ರಿಮ್")
	private WebElement backgroundScreen;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"DOB\")]/parent::android.view.View/following-sibling::android.view.View")
	private WebElement getSelectedDate;

	public DemographicDetailsPageKannada(AppiumDriver driver) {
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
		return new ConsentPageEnglish(driver);
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
		while(!isElementDisplayedOnScreen(residenceStatusHeader)) {
			swipeOrScroll();
		}
		return isElementDisplayed(residenceStatusHeader);
	}

	public boolean isRegionHeaderDisplayed() {
		while(!isElementDisplayedOnScreen(regionHeader)) {
			swipeOrScroll();
		}
		return isElementDisplayed(regionHeader);
	}

	public boolean isProvinceHeaderDisplayed() {
		while(!isElementDisplayedOnScreen(provinceHeader)) {
			swipeOrScroll();
		}
		return isElementDisplayed(provinceHeader);
	}

	public boolean isCityHeaderDisplayed() {
		while(!isElementDisplayedOnScreen(cityHeader)) {
			swipeOrScroll();
		}
		return isElementDisplayed(cityHeader);
	}

	public boolean isPostalCodeHeaderDisplayed() {
		while(!isElementDisplayedOnScreen(postalCodeHeader)) {
			swipeOrScroll();
		}
		return isElementDisplayed(postalCodeHeader);
	}

	public boolean isMobileNumberHeaderDisplayed() {
		while(!isElementDisplayedOnScreen(mobileNumberHeader)) {
			swipeOrScroll();
		}
		return isElementDisplayed(mobileNumberHeader);
	}

	public boolean isZoneHeaderDisplayed() {
		while(!isElementDisplayedOnScreen(zoneHeader)) {
			swipeOrScroll();
		}
		return isElementDisplayed(zoneHeader);
	}

	public boolean isEmailHeaderDisplayed() {
		while(!isElementDisplayedOnScreen(emailHeader)) {
			swipeOrScroll();
		}
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
			if(!isElementDisplayed(kenitraValueFromDropdown)) {
				swipeOrScroll();
				clickOnElement(selectProvinceStatus);
				clickOnElement(kenitraValueFromDropdown);
			}else
				clickOnElement(kenitraValueFromDropdown);
		if(province.equalsIgnoreCase("Rabat"))
			if(!isElementDisplayed(rabatValueFromDropdown)) {
				swipeOrScroll();
				clickOnElement(selectProvinceStatus);
				clickOnElement(rabatValueFromDropdown);
			}else
				clickOnElement(rabatValueFromDropdown);
	}

	public  void selectCityStatus(String city) {
		clickOnElement(selectCityStatus);
		if(city.equalsIgnoreCase("Kenitra"))
			if(!isElementDisplayed(kenitraValueFromDropdown)) {
				swipeOrScroll();
				clickOnElement(selectCityStatus);
				clickOnElement(kenitraValueFromDropdown);
			}else
				clickOnElement(kenitraValueFromDropdown);
		if(city.equalsIgnoreCase("Rabat"))
			if(!isElementDisplayed(rabatValueFromDropdown)) {
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
		return new DocumentuploadPageEnglish(driver);

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
	
	public  void enterIntroducerName(String introducerName) {
		if(!isElementDisplayedOnScreen(introducerNameTextBox)) {
			swipeOrScroll();
			clickAndsendKeysToTextBox(introducerNameTextBox,introducerName);
		}else
		clickAndsendKeysToTextBox(introducerNameTextBox,introducerName);
	}
	
	public  void enterIntroducerRid(String introducerRid) {
		if(!isElementDisplayedOnScreen(introducerRidTextBox)) {
			swipeOrScroll();
			clickAndsendKeysToTextBox(introducerRidTextBox,introducerRid);
		}else
		clickAndsendKeysToTextBox(introducerRidTextBox,introducerRid);
	}
	
	public  void selectCurrentCalenderDate() {
		clickOnElement(calenderIcon);		
	}
	
	public  void closeCalender() {
		clickOnElement(backgroundScreen);		
	}
	
	public  boolean checkDateFormatAndCurrectDate() {
		getTextFromLocator(getSelectedDate);
		if(getTextFromLocator(getSelectedDate).equalsIgnoreCase(getCurrentDate())) 
			return	true;
		else
			return false;
	}
}
