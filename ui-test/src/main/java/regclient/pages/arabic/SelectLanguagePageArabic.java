package regclient.pages.arabic;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.ConsentPage;
import regclient.page.SelectLanguagePage;
import regclient.utils.TestDataReader;


public class SelectLanguagePageArabic extends SelectLanguagePage{

	@AndroidFindBy(accessibility = "اختار اللغة")
	private WebElement selectLanguageTitle;

	@AndroidFindBy(accessibility = "يُقدِّم")
	private WebElement submitButton;

	@AndroidFindBy(accessibility = "French")
	private WebElement frenchLanguageButton;

	@AndroidFindBy(accessibility = "Arabic")
	private WebElement arabicLanguageButton;

	@AndroidFindBy(xpath = "(//android.view.View[@content-desc=\"ಕನ್ನಡ\"])[1]")
	private WebElement kannadaLanguageButton;

	@AndroidFindBy(accessibility = "हिन्दी")
	private WebElement hindiLanguageButton;

	@AndroidFindBy(xpath = "(//android.view.View[@content-desc=\"தமிழ்\"])[1]")
	private WebElement tamilLanguageButton;

	@AndroidFindBy(accessibility = "spanish")
	private WebElement spanishLanguageButton;

	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'لغة التواصل المفضلة')]")
	private WebElement notificationLanguageHeaderInArabic;

	@AndroidFindBy(xpath = "(//android.view.View[@content-desc=\"English\"])[1]")
	private WebElement englishLanguageButton;

	@AndroidFindBy(xpath = "(//android.view.View[@content-desc=\"English\"])[2]")
	private WebElement englishLanguageNotificationButton;

	@AndroidFindBy(accessibility = "français")
	private WebElement frenchLanguageNotificationButton;

	@AndroidFindBy(accessibility = "عربي")
	private WebElement arabicLanguageNotificationButton;

	@AndroidFindBy(xpath = "(//android.view.View[@content-desc=\"ಕನ್ನಡ\"])[2]")
	private WebElement kannadaLanguageNotificationButton;

	@AndroidFindBy(accessibility = "हिंदी")
	private WebElement hindiLanguageNotificationButton;

	@AndroidFindBy(xpath = "(//android.view.View[@content-desc=\"தமிழ்\"])[2]")
	private WebElement tamilLanguageNotificationButton;

	@AndroidFindBy(accessibility = "Española")
	private WebElement spanishLanguageNotificationButton;

	public SelectLanguagePageArabic(AppiumDriver driver) {
		super(driver);
	}

	public boolean isSelectLanguagePageLoaded() {
		return isElementDisplayed(selectLanguageTitle);
	}

	public  void clickOnSubmitButtonWithoutSelectingLanguage() {
		clickOnElement(submitButton);
	}

	public  void selectSecondLanguage() {	
		if(TestDataReader.readData("defaultlanguage").equalsIgnoreCase("ara"))
			clickOnElement(englishLanguageButton);
		else
			clickOnElement(arabicLanguageButton);
	}

	public void selectNotificationlanguage(String notificationLanguage) {
		switch (notificationLanguage) {
		case "eng":
			clickOnElement(englishLanguageNotificationButton);
			break;
		case "fra":
			clickOnElement(frenchLanguageNotificationButton);
			break;
		case "ara":
			clickOnElement(arabicLanguageNotificationButton);
			break;
		case "kan":
			clickOnElement(kannadaLanguageNotificationButton);
			break;
		case "hin":
			clickOnElement(hindiLanguageNotificationButton);
			break;
		case "tam":
			clickOnElement(tamilLanguageNotificationButton);
			break;
		case "spa":
			clickOnElement(spanishLanguageNotificationButton);
			break;
		default:
			// Handle the default case if needed
			break;
		}
	}

	public  void selectAllSecondLanguage() {

		clickOnElement(englishLanguageButton);

		clickOnElement(frenchLanguageButton);

		clickOnElement(arabicLanguageButton);

		clickOnElement(kannadaLanguageButton);

		clickOnElement(hindiLanguageButton);

		clickOnElement(tamilLanguageButton);	
	}


	public  boolean isArabicLanguageButtonEnabled() {
		return isElementEnabled(arabicLanguageButton);
	}

	public  boolean isSubmitButtonEnabled() {
		return isElementEnabled(submitButton);
	}

	public  ConsentPage clickOnSubmitButton() {
		clickOnElement(submitButton);
		return new ConsentPageArabic(driver);
	}

	public boolean isNotificationLanguageEnglishDisplayed() {
		return isElementDisplayed(notificationLanguageHeaderInArabic);
	}

}
