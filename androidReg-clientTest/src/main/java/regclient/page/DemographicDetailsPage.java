package regclient.page;

import io.appium.java_client.AppiumDriver;


public abstract class DemographicDetailsPage extends BasePage{

	public DemographicDetailsPage(AppiumDriver driver) {
		super(driver);
	}
	
	public abstract boolean isDemographicDetailsPageDisplayed();
	
	public abstract boolean isErrorMessageInvalidInputTextDisplayed();
	
	public abstract  ConsentPage clickOnConsentPageTitle();

	public abstract  void enterFullName(String fullName);

	public abstract  void enterAddressLine1(String addressLine1);

	public abstract  void enterAddressLine2(String addressLine2);

	public abstract  void enterAddressLine3(String addressLine3);

	public abstract  void enterAge(String age);

	public abstract boolean checkFullNameSecondLanguageTextBoxNotNull();

	public abstract boolean isResidenceStatusHeaderDisplayed();

	public abstract boolean isRegionHeaderDisplayed();

	public abstract boolean isProvinceHeaderDisplayed();

	public abstract boolean isCityHeaderDisplayed();

	public abstract boolean isPostalCodeHeaderDisplayed();

	public abstract boolean isMobileNumberHeaderDisplayed();

	public abstract boolean isZoneHeaderDisplayed();

	public abstract boolean isEmailHeaderDisplayed();
	
	public abstract boolean isIntroducerNameHeaderDisplayed();
	
	public abstract boolean isIntroducerRidHeaderDisplayed();
	
	public abstract  void selectGender(String gender);

	public abstract  void selectMaritalStatus();

	public abstract  void selectResidenceStatus(String ResidenceStatus);

	public abstract  void selectRegionStatus(String region);
	
	public abstract  void selectProvinceStatus(String province);

	public abstract  void selectCityStatus(String city);

	public abstract  void selectZoneStatus();

	public abstract  void selectPostalStatus();

	public abstract  DocumentUploadPage clickOnContinueButton();

	public abstract  void enterMobileNumber(String mobileNumber);

	public abstract  void enterEmailID(String EmailID);
	
	public abstract  void enterIntroducerName(String EmailID);
	
	public abstract  void enterIntroducerRid(String EmailID);

}
