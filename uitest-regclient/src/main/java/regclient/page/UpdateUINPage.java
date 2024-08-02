package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class UpdateUINPage extends BasePage{

	public UpdateUINPage(AppiumDriver driver) {
		super(driver);
	}

	 public abstract boolean isUpdateMyUINTitleDisplayed();

	    public abstract void enterUIN(String UIN);

	    public abstract void clickOnConsentButton();

	    public abstract boolean isConsentTitleDisplayed();

	    public abstract void clickOnPreferredLanguageButton();

	    public abstract boolean isPreferredLanguageTitleDisplayed();

	    public abstract void clickOnFullNameButton();

	    public abstract boolean isFullNameTitleDisplayed();

	    public abstract void clickOnDOBButton();

	    public abstract boolean isDOBTitleDisplayed();

	    public abstract void clickOnGenderButton();

	    public abstract boolean isnGenderTitleDisplayed();

	    public abstract void clickOnAddressButton();

	    public abstract boolean isAddressTitleDisplayed();

	    public abstract void clickOnMaritalStatusButton();

	    public abstract boolean isMaritalStatusTitleDisplayed();

	    public abstract void clickOnApplicantLanguageButton();

	    public abstract boolean isApplicantLanguageTitleDisplayed();

	    public abstract void clickOnResidenceStatusButton();

	    public abstract boolean isResidenceStatusTitleDisplayed();

	    public abstract void clickOnLocationButton();

	    public abstract boolean isLocationTitleDisplayed();

	    public abstract void clickOnPhoneButton();

	    public abstract boolean isPhoneTitleDisplayed();

	    public abstract void clickOnEmailButton();

	    public abstract boolean isEmailTitleDisplayed();

	    public abstract void clickOnIntroducerDetailsButton();

	    public abstract boolean isIntroducerDetailsTitleDisplayed();

	    public abstract void clickOnBiometricsButton();

	    public abstract boolean isnBiometricsTitleDisplayed();

	    public abstract void clickOnDocumentsButton();

	    public abstract boolean isDocumentsTitleDisplayed();
	    
	    public abstract  ConsentPage clickOnContinueButton();
	    
	    public abstract boolean isInvalidUINErrorMessageDisplayed();

}
