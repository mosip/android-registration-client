package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class DemographicDetailsPage extends BasePage {

	public DemographicDetailsPage(AppiumDriver driver) {
		super(driver);
	}

	public abstract boolean isPageDisplayed(String pageKey);

	public abstract boolean isErrorMessageInvalidInputTextDisplayed();

	public abstract ConsentPage clickOnPageTitle(String pageKey);

	public abstract DocumentUploadPage clickOnContinueButton();

	public abstract boolean isPreRegFetchDataTextBoxDisplay();

	public abstract void fillDemographicDetailsPage(String age);

	public abstract void editDemographicDetailsPage(String age);

	public abstract boolean checkSecondLanguageTextBoxNotNull(String id);

	public abstract boolean checkDateFormatAndCurrectDate(String id);

	public abstract void fillIntroducerDetailsInDemographicDetailsPage(String age);

	public abstract boolean isContinueButtonEnable();

	public abstract boolean isApplicationIdTextBoxDisplay();

	public abstract void clickOnScanButton();

	public abstract void fetchPreregApplicationId(String age);

	public abstract void validateFetchedDemographicData();

	public abstract void fillRemainDemographicDetailsPage(String age);

}
