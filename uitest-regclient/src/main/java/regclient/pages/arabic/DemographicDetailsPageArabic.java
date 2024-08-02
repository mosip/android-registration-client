package regclient.pages.arabic;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.ConsentPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.DocumentUploadPage;
import regclient.pages.english.ConsentPageEnglish;
import regclient.pages.english.DocumentuploadPageEnglish;

public class DemographicDetailsPageArabic extends DemographicDetailsPage{

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"الاسم الكامل\"))")
	private WebElement demographicDetailspage;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"موافقة\"))")
	private WebElement consentTitle;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"الاسم الكامل\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")
	private WebElement fullNameTextBox;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"الاسم الكامل\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[2]")
	private WebElement fullNameTextBoxSecondLanguage;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"DOB\")]/parent::android.view.View/following-sibling::android.widget.EditText[1]")
	private WebElement ageTextBox;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"عنوان دائم1\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")
	private WebElement addressLine1TextBox;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"عنوان دائم1\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[2]")
	private WebElement addressLine1TextBoxSecondLangauge;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"عنوان مؤقت2\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")
	private WebElement addressLine2TextBox;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"عنوان مؤقت2\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[2]")
	private WebElement addressLine2TextBoxSecondLangauge;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"عنوان مؤقت3\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")
	private WebElement addressLine3TextBox;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"عنوان مؤقت3\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[2]")
	private WebElement addressLine3TextBoxSecondLangauge;

	@AndroidFindBy(accessibility = "آحرون")
	private WebElement maleButton;

	@AndroidFindBy(accessibility = "أنثى")
	private WebElement femaleButton;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"الأحوال المدنية\")]/parent::android.view.View/parent::android.widget.Button")
	private WebElement selectMaritalStatus;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"حالة الإقامة\")]/parent::android.view.View/parent::android.widget.Button")
	private WebElement selectResidentStatus;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"منطقة\")]/parent::android.view.View/parent::android.widget.Button")
	private WebElement selectRegionStatus;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"المحافظة\")]/parent::android.view.View/parent::android.widget.Button")
	private WebElement selectProvinceStatus;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"مدينة\")]/parent::android.view.View/parent::android.widget.Button")
	private WebElement selectCityStatus;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"منطقة\")]/parent::android.view.View/parent::android.widget.Button")
	private WebElement selectZoneStatus;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"بريدي\")]/parent::android.view.View/parent::android.widget.Button")
	private WebElement selectPostal;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"14022\")")
	private WebElement selectPostalCode;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"غير أجنبي\")")
	private WebElement nonForeignerValueFromDropdown;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"أجنبي\")")
	private WebElement foreignerValueFromDropdown;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"الأرامل\")")
	private WebElement singleValueFromDropdown;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"الرباط سلا القنيطرة\")")
	private WebElement rabatSaleKenitraValueFromDropdown;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"القنيطرة\")]")
	private WebElement kenitraValueFromDropdown;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"الرباط\")]")
	@AndroidFindBy(accessibility = "الرباط")
	private WebElement rabatValueFromDropdown;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"بن منصور\")")
	private WebElement benMansourFromDropdown;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"حالة الإقامة\")")
	private WebElement residenceStatusHeader;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"منطقة\")")
	private WebElement regionHeader;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"المحافظة\")")
	private WebElement provinceHeader;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"مدينة\")")
	private WebElement cityHeader;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"منطقة\")")
	private WebElement zoneHeader;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"بريدي\")")
	private WebElement postalCodeHeader;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"هاتف\")")
	private WebElement mobileNumberHeader;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"البريد الإلكتروني\")")
	private WebElement emailHeader;
	
	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"اسم مقدم العرض\")")
	private WebElement introducerNameHeader;
	
	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"معرف RID\")")
	private WebElement introducerRidHeader;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"هاتف\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText")
	private WebElement mobileNumberTextBox;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"البريد الإلكتروني\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")
	private WebElement emailIdTextBox;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"اسم مقدم العرض\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")
	private WebElement introducerNameTextBox;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"اسم مقدم العرض\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")
	private WebElement introducerNameTextBoxSecondLangauge;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"معرف RID\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")
	private WebElement introducerRidTextBox;

	@AndroidFindBy(accessibility = "يكمل")
	private WebElement continueButton;
	
	@AndroidFindBy(accessibility = "مدخل غير صالح")
	private WebElement errorMessageInvalidInputText;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"DOB\")]/parent::android.view.View/following-sibling::android.view.View")
	private WebElement calenderIcon;
	
	@AndroidFindBy(accessibility = "تمويه")
	private WebElement backgroundScreen;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"DOB\")]/parent::android.view.View/following-sibling::android.view.View")
	private WebElement getSelectedDate;

	@AndroidFindBy(accessibility = "FETCH DATA")
	private WebElement fetchDataButton;

	public DemographicDetailsPageArabic(AppiumDriver driver) {
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

	public boolean checkAddress1SecondLanguageTextBoxNotNull() {
		if(getTextFromLocator(addressLine1TextBoxSecondLangauge)==null || getTextFromLocator(addressLine1TextBoxSecondLangauge)=="" )
			return	false;
		else
			return	true;
	}

	public boolean checkAddress2SecondLanguageTextBoxNotNull() {
		if(getTextFromLocator(addressLine2TextBoxSecondLangauge)==null || getTextFromLocator(addressLine2TextBoxSecondLangauge)=="" )
			return	false;
		else
			return	true;
	}

	public boolean checkAddress3SecondLanguageTextBoxNotNull() {
		if(getTextFromLocator(addressLine3TextBoxSecondLangauge)==null || getTextFromLocator(addressLine3TextBoxSecondLangauge)=="" )
			return	false;
		else
			return	true;
	}

	public boolean checkIntroducerNameTextBoxSecondLangaugeTextBoxNotNull() {
		if(getTextFromLocator(introducerNameTextBoxSecondLangauge)==null || getTextFromLocator(introducerNameTextBoxSecondLangauge)=="" )
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
	
	public boolean isPreRegFetchDataTextBoxDisplay() {
		return isElementDisplayed(fetchDataButton);
	}
}
