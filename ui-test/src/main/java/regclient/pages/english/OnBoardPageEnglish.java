package regclient.pages.english;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.OnBoardPage;
import regclient.page.RegistrationTasksPage;
import regclient.page.SupervisorBiometricVerificationpage;

public class OnBoardPageEnglish extends OnBoardPage{

	@AndroidFindBy(accessibility = "HELP")
	private WebElement helpButton;

	@AndroidFindBy(accessibility = "GET ONBOARDED")
	private WebElement getOnBoardTitle;
	
	@AndroidFindBy(accessibility = "SKIP TO HOME")
	private WebElement skipToHomeScreenButton;

	@AndroidFindBy(accessibility = "Please tap on 'GET ONBOARDED' to get started with the onboarding process.")
	private WebElement onBoardWelcomeMessage;

	public OnBoardPageEnglish(AppiumDriver driver) {
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
		return new SupervisorBiometricVerificationpageEnglish(driver);
	}
	
	public RegistrationTasksPage clickOnSkipToHomeScreen() {
		clickOnElement(skipToHomeScreenButton);
		return new RegistrationTasksPageEnglish(driver);
	}

}
