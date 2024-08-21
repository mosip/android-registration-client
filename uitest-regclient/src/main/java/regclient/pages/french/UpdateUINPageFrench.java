package regclient.pages.french;


import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.api.FetchUiSpec;
import regclient.page.ConsentPage;
import regclient.page.UpdateUINPage;
import regclient.pages.english.ConsentPageEnglish;

public class UpdateUINPageFrench extends UpdateUINPage{
	
	@AndroidFindBy(xpath = "//android.widget.EditText")
	private WebElement UINNumberTextBox;
	
	@AndroidFindBy(accessibility = "CONTINUER")
	private WebElement continueButton;
	
	@AndroidFindBy(accessibility = "Veuillez saisir un UIN valide")
	private WebElement invalidUINErrorMessage;

	public UpdateUINPageFrench(AppiumDriver driver) {
		super(driver);
	}
	
	@SuppressWarnings("deprecation")
	public boolean isUpdateMyUINTitleDisplayed() {
		return isElementDisplayed (findElementWithRetry(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"" + FetchUiSpec.getTitleUsingId("UPDATE") + "\"))")));
	}
	public  void enterUIN(String UIN) {
		clickAndsendKeysToTextBox(UINNumberTextBox,UIN);
	}
	
	@SuppressWarnings("deprecation")
	public  void clickOnConsentButton() {
		clickOnElement(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("consentText")+"\")")));
	}
	
	@SuppressWarnings("deprecation")
	public boolean isConsentTitleDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("consentText")+"\")")));
	}
	
	@SuppressWarnings("deprecation")
	public  void clickOnPreferredLanguageButton() {
		clickOnElement(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("preferredLang")+"\")")));

	}
	
	@SuppressWarnings("deprecation")
	public boolean isPreferredLanguageTitleDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("preferredLang")+"\")")));

	}
		
	@SuppressWarnings("deprecation")
	public  void clickOnFullNameButton() {
		clickOnElement(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("firstName")+"\")")));

	}
	
	@SuppressWarnings("deprecation")
	public boolean isFullNameTitleDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("firstName")+"\")")));

	}
	
	
	@SuppressWarnings("deprecation")
	public  void clickOnDOBButton() {
		clickOnElement(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("dateOfBirth")+"\")")));
	}
	
	@SuppressWarnings("deprecation")
	public boolean isDOBTitleDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("dateOfBirth")+"\")")));

	}
	
	@SuppressWarnings("deprecation")
	public  void clickOnGenderButton() {
		clickOnElement(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("gender")+"\")")));
	}
	
	@SuppressWarnings("deprecation")
	public boolean isnGenderTitleDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("gender")+"\")")));

	}
	
	@SuppressWarnings("deprecation")
	public  void clickOnAddressButton() {
		clickOnElement(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("permanentAddress")+"\")")));

	}
	
	@SuppressWarnings("deprecation")
	public boolean isAddressTitleDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("permanentAddress")+"\")")));

	}
	
	@SuppressWarnings("deprecation")
	public  void clickOnMaritalStatusButton() {
		clickOnElement(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("maritalStatus")+"\")")));

	}
	
	@SuppressWarnings("deprecation")
	public boolean isMaritalStatusTitleDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("maritalStatus")+"\")")));

	}
	
	@SuppressWarnings("deprecation")
	public  void clickOnApplicantLanguageButton() {
		clickOnElement(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("applicantLanguage")+"\")")));

	}
	
	@SuppressWarnings("deprecation")
	public boolean isApplicantLanguageTitleDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("applicantLanguage")+"\")")));

	}
	
	@SuppressWarnings("deprecation")
	public  void clickOnResidenceStatusButton() {
		clickOnElement(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("residenceStatus")+"\")")));

	}
	
	@SuppressWarnings("deprecation")
	public boolean isResidenceStatusTitleDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("residenceStatus")+"\")")));

	}
	
	@SuppressWarnings("deprecation")
	public  void clickOnLocationButton() {
		clickOnElement(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("region")+"\")")));

	}
	
	@SuppressWarnings("deprecation")
	public boolean isLocationTitleDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("region")+"\")")));

	}
	
	@SuppressWarnings("deprecation")
	public  void clickOnPhoneButton() {
		clickOnElement(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("phone")+"\")")));

	}
	
	@SuppressWarnings("deprecation")
	public boolean isPhoneTitleDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("phone")+"\")")));

	}
	
	@SuppressWarnings("deprecation")
	public  void clickOnEmailButton() {
		clickOnElement(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("email")+"\")")));

	}
	
	@SuppressWarnings("deprecation")
	public boolean isEmailTitleDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("email")+"\")")));

	}
	
	@SuppressWarnings("deprecation")
	public  void clickOnIntroducerDetailsButton() {
		clickOnElement(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("introducerName")+"\")")));

	}
	
	@SuppressWarnings("deprecation")
	public boolean isIntroducerDetailsTitleDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("introducerName")+"\")")));

	}
	
	@SuppressWarnings("deprecation")
	public  void clickOnBiometricsButton() {
		clickOnElement(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("individualBiometrics")+"\")")));

	}
	
	@SuppressWarnings("deprecation")
	public boolean isnBiometricsTitleDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("individualBiometrics")+"\")")));

	}
	
	@SuppressWarnings("deprecation")
	public  void clickOnDocumentsButton() {
		clickOnElement(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("proofOfAddress")+"\")")));

	}
	
	@SuppressWarnings("deprecation")
	public boolean isDocumentsTitleDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getGroupValueUsingId("proofOfAddress")+"\")")));

	}

	public  ConsentPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new ConsentPageEnglish(driver);
	}
	
	public boolean isInvalidUINErrorMessageDisplayed() {
		return isElementDisplayed(invalidUINErrorMessage);
	}
}
