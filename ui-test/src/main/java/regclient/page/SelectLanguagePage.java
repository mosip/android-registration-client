package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class SelectLanguagePage extends BasePage {

	public SelectLanguagePage(AppiumDriver driver) {
		super(driver);
	}

	public abstract boolean isSelectLanguagePageLoaded();

	public abstract void clickOnSubmitButtonWithoutSelectingLanguage();

	public abstract void selectSecondLanguage();

	public abstract void selectNotificationlanguage(String notificationLanguage);

	public abstract void selectAllSecondLanguage();

	public abstract boolean isArabicLanguageButtonEnabled();

	public abstract boolean isSubmitButtonEnabled();

	public abstract ConsentPage clickOnSubmitButton();

	public abstract boolean isNotificationLanguageEnglishDisplayed();

}
