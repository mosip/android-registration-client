package regclient.pages;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

public class SelectLanguagePage extends BasePage{

	@AndroidFindBy(accessibility = "Select Language")
	private WebElement selectLanguageTitle;
	
	@AndroidFindBy(accessibility = "SUBMIT")
	private WebElement submitButton;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.view.View\").instance(11)")
	private WebElement secondlanguage;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.view.View\").instance(18)")
	private WebElement notificationlanguage;
	
	@AndroidFindBy(accessibility = "Arabic")
	private WebElement arabiclanguage;
	
	@AndroidFindBy(accessibility = "Notification Languages / لغات الإخطار")
	private WebElement notificationLanguageEnglishArabic;
	
	public SelectLanguagePage(AppiumDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	public boolean isSelectLanguagePageLoaded() {

		return isElementDisplayed(selectLanguageTitle);
	}
	
	public  void clickOnSubmitButtonWithoutSelectingLanguage() {
		clickOnElement(submitButton);
	}
	
	public  void selectSecondlanguage() {
		clickOnElement(secondlanguage);
	}
	
	public  void selectNotificationlanguage() {
		clickOnElement(notificationlanguage);
	}
	
	public  void selectArabiclanguage() {
		clickOnElement(arabiclanguage);
	}
	
	public  boolean isArabicLanguageButtonEnable() {
		return isElementEnabled(arabiclanguage);
	}
	
	public  boolean isSubmitButtonEnable() {
		return isElementEnabled(submitButton);
	}
	
	public  ConsentPage clickOnSubmit() {
		clickOnElement(submitButton);
		return new ConsentPage(driver);
	}
	
	public boolean isNotificationLanguageEnglishArabicDisplay() {

		return isElementDisplayed(notificationLanguageEnglishArabic);
	}
}
