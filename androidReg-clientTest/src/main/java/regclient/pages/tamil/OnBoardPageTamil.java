package regclient.pages.tamil;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.OnBoardPage;
import regclient.page.RegistrationTasksPage;
import regclient.page.SupervisorBiometricVerificationpage;


public class OnBoardPageTamil extends OnBoardPage{

	@AndroidFindBy(accessibility = "உதவி")
	private WebElement helpButton;

	@AndroidFindBy(accessibility = "இணையத்தில் பெறவும்")
	private WebElement getOnBoardTitle;
	
	@AndroidFindBy(accessibility ="வீட்டிற்கு செல்க")
	private WebElement skipToHomeScreenButton;

	@AndroidFindBy(accessibility = "ஆன்போர்டிங் செயல்முறையைத் தொடங்க, 'ஆன்போர்டைப் பெறு' என்பதைத் தட்டவும்.")
	private WebElement onBoardWelcomeMessage;

	public OnBoardPageTamil(AppiumDriver driver) {
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
		return new SupervisorBiometricVerificationpageTamil(driver);
	}
	
	public RegistrationTasksPage clickOnSkipToHomeScreen() {
		clickOnElement(skipToHomeScreenButton);
		return new RegistrationTasksPageTamil(driver);
	}

}
