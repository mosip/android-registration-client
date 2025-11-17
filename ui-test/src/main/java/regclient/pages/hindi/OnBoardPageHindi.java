package regclient.pages.hindi;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.OnBoardPage;
import regclient.page.RegistrationTasksPage;
import regclient.page.SupervisorBiometricVerificationpage;


public class OnBoardPageHindi extends OnBoardPage{

	@AndroidFindBy(accessibility = "मदद")
	private WebElement helpButton;

	@AndroidFindBy(accessibility = "सवार होना")
	private WebElement getOnBoardTitle;
	
	@AndroidFindBy(accessibility ="होम पेज पर जाएं")
	private WebElement skipToHomeScreenButton;

	@AndroidFindBy(accessibility = "कृपया ऑनबोर्डिंग प्रक्रिया आरंभ करने के लिए 'ऑनबोर्ड प्राप्त करें' पर टैप करें।")
	private WebElement onBoardWelcomeMessage;

	public OnBoardPageHindi(AppiumDriver driver) {
		super(driver);
	}

	public boolean isGetOnBoardTitleDisplayed() {
		return isElementDisplayed(getOnBoardTitle);
	}

	public boolean isHelpButtonDisplayed() {
		return isElementDisplayed(helpButton);
	}
	
	public boolean isOnBoardWelcomeMessageDisplayed() {
		return isElementDisplayed(onBoardWelcomeMessage);
	}
	
	public SupervisorBiometricVerificationpage clickOnGetOnBoardTitle() {
		clickOnElement(getOnBoardTitle);
		return new SupervisorBiometricVerificationpageHindi(driver);
	}
	
	public RegistrationTasksPage clickOnSkipToHomeScreen() {
		clickOnElement(skipToHomeScreenButton);
		return new RegistrationTasksPageHindi(driver);
	}
}
