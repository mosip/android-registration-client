package regclient.pages.arabic;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.api.FetchUiSpec;
import regclient.page.ConsentPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.DocumentUploadPage;
import regclient.pages.english.ConsentPageEnglish;
import regclient.pages.english.DocumentuploadPageEnglish;

public class DemographicDetailsPageArabic extends DemographicDetailsPage{

	@AndroidFindBy(accessibility = "آحرون")
	private WebElement maleButton;

	@AndroidFindBy(accessibility = "أنثى")
	private WebElement femaleButton;
	
	@AndroidFindBy(accessibility = "يكمل")
	private WebElement continueButton;
	
	@AndroidFindBy(accessibility = "مدخل غير صالح")
	private WebElement errorMessageInvalidInputText;
	
	@AndroidFindBy(accessibility = "تمويه")
	private WebElement backgroundScreen;
	
	@AndroidFindBy(accessibility = "FETCH DATA")
	private WebElement fetchDataButton;

	public DemographicDetailsPageArabic(AppiumDriver driver) {
		super(driver);
	}

	@SuppressWarnings("deprecation")
	public boolean isDemographicDetailsPageDisplayed() {
		WebElement  demographicDetailspage = findElementWithRetry(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"" + FetchUiSpec.getScreenTitle("DemographicDetails") + "\"))"));
		return isElementDisplayed(demographicDetailspage);
	}

	public boolean isErrorMessageInvalidInputTextDisplayed() {
		return isElementDisplayed(errorMessageInvalidInputText);
	}

	@SuppressWarnings("deprecation")
	public  ConsentPage clickOnConsentPageTitle() {
		WebElement  consentTitle = findElementWithRetry(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"" + FetchUiSpec.getScreenTitle("consentdet") + "\"))"));
		clickOnElement(consentTitle);
		return new ConsentPageEnglish(driver);
	}

	public  void enterFullName(String fullName) {
		clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId("fullName") + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),fullName);
	}

	public  void enterAddressLine1(String addressLine1) {
		clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId("addressLine1") + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),addressLine1);
	}

	public  void enterAddressLine2(String addressLine2) {
		clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId("addressLine2") + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),addressLine2);
	}

	public  void enterAddressLine3(String addressLine3) {
		clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId("addressLine3") + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),addressLine3);
	}

	public  void enterAge(String age) {
		clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+ FetchUiSpec.getValueUsingId("dateOfBirth") +"\")]/parent::android.view.View/following-sibling::android.widget.EditText[1]")),age);
	}

	public boolean checkFullNameSecondLanguageTextBoxNotNull() {
		if(getTextFromLocator(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId("fullName") + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[2]")))==null || getTextFromLocator(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId("fullName") + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[2]")))=="" )
			return	false;
		else
			return	true;
	}

	public boolean checkAddress1SecondLanguageTextBoxNotNull() {
		if(getTextFromLocator(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId("addressLine1") + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[2]")))==null || getTextFromLocator(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId("addressLine1") + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[2]")))=="" )
			return	false;
		else
			return	true;
	}

	public boolean checkAddress2SecondLanguageTextBoxNotNull() {
		if(getTextFromLocator(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId("addressLine2") + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[2]")))==null || getTextFromLocator(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId("addressLine2") + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[2]")))=="" )
			return	false;
		else
			return	true;
	}

	public boolean checkAddress3SecondLanguageTextBoxNotNull() {
		if(getTextFromLocator(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId("addressLine3") + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[2]")))==null || getTextFromLocator(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId("addressLine3") + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[2]")))=="" )
			return	false;
		else
			return	true;
	}

	public boolean checkIntroducerNameTextBoxSecondLangaugeTextBoxNotNull() {
		if(getTextFromLocator(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId("introducerName") + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[2]")))==null || getTextFromLocator(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId("introducerName") + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[2]")))=="" )
			return	false;
		else
			return	true;
	}

	@SuppressWarnings("deprecation")
	public boolean isResidenceStatusHeaderDisplayed() {
		waitTime(2);
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId("residenceStatus")+"\")")));
	}

	@SuppressWarnings("deprecation")
	public boolean isRegionHeaderDisplayed() {
		waitTime(2);
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId("region")+"\")")));

	}

	@SuppressWarnings("deprecation")
	public boolean isProvinceHeaderDisplayed() {
		waitTime(2);
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId("province")+"\")")));

	}

	@SuppressWarnings("deprecation")
	public boolean isCityHeaderDisplayed() {
		waitTime(2);
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId("city")+"\")")));

	}

	@SuppressWarnings("deprecation")
	public boolean isPostalCodeHeaderDisplayed() {
		waitTime(2);
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId("postalCode")+"\")")));

	}

	@SuppressWarnings("deprecation")
	public boolean isMobileNumberHeaderDisplayed() {
		waitTime(2);
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId("phone")+"\")")));

	}

	@SuppressWarnings("deprecation")
	public boolean isZoneHeaderDisplayed() {
		waitTime(2);
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId("zone")+"\")")));

	}

	@SuppressWarnings("deprecation")
	public boolean isEmailHeaderDisplayed() {
		waitTime(2);
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId("email")+"\")")));

	}

	@SuppressWarnings("deprecation")
	public boolean isIntroducerNameHeaderDisplayed() {
		waitTime(2);
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId("introducerName")+"\")")));
	}

	@SuppressWarnings("deprecation")
	public boolean isIntroducerRidHeaderDisplayed() {
		waitTime(2);
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId("introducerRID")+"\")")));
	}

	public  void selectGender(String gender) {
		if(gender.equalsIgnoreCase("male"))
			clickOnElement(maleButton);
		if(gender.equalsIgnoreCase("female"))
			clickOnElement(femaleButton);
	}

	public  void selectMaritalStatus() {
		WebElement selectMaritalStatus=findElement(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("maritalStatus")+"\")]/parent::android.view.View/parent::android.widget.Button"));
		clickOnElement(selectMaritalStatus);
		waitTime(2);
		if(!isElementDisplayed(selectMaritalStatus)) {
			clickOnElement(findElement(By.xpath("//android.view.View[contains(@content-desc, \" \")]")));
		}else {
			swipeOrScroll();
			clickOnElement(selectMaritalStatus);
			waitTime(2);
			clickOnElement(findElement(By.xpath("//android.view.View[contains(@content-desc, \" \")]")));
		}
	}

	public  void selectResidenceStatus(String ResidenceStatus) {
		WebElement selectResidentStatus=findElement(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("residenceStatus")+"\")]/parent::android.view.View/parent::android.widget.Button"));
		clickOnElement(selectResidentStatus);
		waitTime(2);
		if(!isElementDisplayed(selectResidentStatus)) {				
			clickOnElement(findElement(By.xpath("//android.view.View[contains(@content-desc, \" \")]")));
		}else {
			swipeOrScroll();
			clickOnElement(selectResidentStatus);
			waitTime(2);
			clickOnElement(findElement(By.xpath("//android.view.View[contains(@content-desc, \" \")]")));
		}
	}

	public  void selectRegionStatus(String region) {
		WebElement selectRegionStatus=findElement(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("region")+"\")]/parent::android.view.View/parent::android.widget.Button"));
		clickOnElement(selectRegionStatus);
		waitTime(2);
		if(!isElementDisplayed(selectRegionStatus)) {				
			clickOnElement(findElement(By.xpath("//android.view.View[contains(@content-desc, \" \")]")));
		}else {
			swipeOrScroll();
			clickOnElement(selectRegionStatus);
			waitTime(2);
			clickOnElement(findElement(By.xpath("//android.view.View[contains(@content-desc, \" \")]")));
		}
	}

	public  void selectProvinceStatus(String province) {
		WebElement selectProvinceStatus=findElement(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("province")+"\")]/parent::android.view.View/parent::android.widget.Button"));
		clickOnElement(selectProvinceStatus);
		waitTime(2);
		if(!isElementDisplayed(selectProvinceStatus)) {				
			clickOnElement(findElement(By.xpath("//android.view.View[contains(@content-desc, \" \")]")));
		}else {
			swipeOrScroll();
			clickOnElement(selectProvinceStatus);
			waitTime(2);
			clickOnElement(findElement(By.xpath("//android.view.View[contains(@content-desc, \" \")]")));
		}
	}

	public  void selectCityStatus(String city) {
		WebElement selectCityStatus=findElement(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("city")+"\")]/parent::android.view.View/parent::android.widget.Button"));
		clickOnElement(selectCityStatus);
		waitTime(2);
		if(!isElementDisplayed(selectCityStatus)) {				
			clickOnElement(findElement(By.xpath("//android.view.View[contains(@content-desc, \" \")]")));
		}else {
			swipeOrScroll();
			clickOnElement(selectCityStatus);
			waitTime(2);
			clickOnElement(findElement(By.xpath("//android.view.View[contains(@content-desc, \" \")]")));
		}
	}

	public  void selectZoneStatus() {
		WebElement selectZoneStatus=findElement(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("zone")+"\")]/parent::android.view.View/parent::android.widget.Button"));
		clickOnElement(selectZoneStatus);
		waitTime(2);
		if(!isElementDisplayed(selectZoneStatus)) {				
			clickOnElement(findElement(By.xpath("//android.view.View[contains(@content-desc, \" \")]")));
		}else {
			swipeOrScroll();
			clickOnElement(selectZoneStatus);
			waitTime(2);
			clickOnElement(findElement(By.xpath("//android.view.View[contains(@content-desc, \" \")]")));
		}
	}

	public  void selectPostalStatus() {
		WebElement selectPostal=findElement(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("postalCode")+"\")]/parent::android.view.View/parent::android.widget.Button"));
		clickOnElement(selectPostal);
		waitTime(2);
		if(!isElementDisplayed(selectPostal)) {				
			clickOnElement(findElement(By.xpath("//android.view.View[contains(@content-desc, \" \")]")));
		}else {
			swipeOrScroll();
			clickOnElement(selectPostal);
			waitTime(2);
			clickOnElement(findElement(By.xpath("//android.view.View[contains(@content-desc, \" \")]")));
		}
	}

	public  DocumentUploadPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new DocumentuploadPageEnglish(driver);

	}

	public  void enterMobileNumber(String mobileNumber) {
		clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+ FetchUiSpec.getValueUsingId("phone") +"\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText")),mobileNumber);

	}

	public  void enterEmailID(String EmailID) {
		clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+ FetchUiSpec.getValueUsingId("email") +"\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText")),EmailID);

	}

	public  void enterIntroducerName(String introducerName) {
		clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+ FetchUiSpec.getValueUsingId("introducerName") +"\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),introducerName);
	}

	public  void enterIntroducerRid(String introducerRid) {
		clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+ FetchUiSpec.getValueUsingId("introducerRID") +"\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),introducerRid);
	}

	public  void selectCurrentCalenderDate() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+ FetchUiSpec.getValueUsingId("dateOfBirth") +"\")]/parent::android.view.View/following-sibling::android.view.View")));		
	}

	public  void closeCalender() {
		clickOnElement(backgroundScreen);		
	}

	public  boolean checkDateFormatAndCurrectDate() {
		if(getTextFromLocator(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+ FetchUiSpec.getValueUsingId("dateOfBirth") +"\")]/parent::android.view.View/following-sibling::android.view.View"))).equalsIgnoreCase(getCurrentDate())) 
			return	true;
		else
			return false;
	}

	public boolean isPreRegFetchDataTextBoxDisplay() {
		return isElementDisplayed(fetchDataButton);
	}
}
