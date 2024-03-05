package regclient.pages;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
public class SelectLanguagePage extends BasePage{

	@AndroidFindBy(accessibility = "Select Language")
	private WebElement selectLanguageTitle;

	@AndroidFindBy(accessibility = "SUBMIT")
	private WebElement submitButton;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.view.View\").instance(11)")
	private WebElement selectSecondLanguage;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.view.View\").instance(18)")
	private WebElement selectNotificationLanguage;

	@AndroidFindBy(accessibility = "Arabic")
	private WebElement arabicLanguageButton;

	@AndroidFindBy(accessibility = "Notification Languages / لغات الإخطار")
	private WebElement notificationLanguageHeaderInEnglishArabic;

	public SelectLanguagePage(AppiumDriver driver) {
		super(driver);
	}

	public boolean isSelectLanguagePageLoaded() {
		return isElementDisplayed(selectLanguageTitle);
	}

	public  void clickOnSubmitButtonWithoutSelectingLanguage() {
		clickOnElement(submitButton);
	}

	public  void selectSecondlanguage() {
		clickOnElement(selectSecondLanguage);
	}

	public  void selectNotificationlanguage() {
		clickOnElement(selectNotificationLanguage);
	}

	public  void selectArabiclanguage() {
		clickOnElement(arabicLanguageButton);
	}

	public  boolean isArabicLanguageButtonEnabled() {
		return isElementEnabled(arabicLanguageButton);
	}

	public  boolean isSubmitButtonEnabled() {
		return isElementEnabled(submitButton);
	}

	public  ConsentPage clickOnSubmitButton() {
		clickOnElement(submitButton);
		return new ConsentPage(driver);
	}

	public boolean isNotificationLanguageEnglishArabicDisplay() {
		return isElementDisplayed(notificationLanguageHeaderInEnglishArabic);
	}
}
