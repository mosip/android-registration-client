package regclient.pages.kannada;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.OnBoardPage;
import regclient.page.RegistrationTasksPage;
import regclient.page.SupervisorBiometricVerificationpage;

public class OnBoardPageKannada extends OnBoardPage{

	@AndroidFindBy(accessibility = "ಸಹಾಯ")
	private WebElement helpButton;

	@AndroidFindBy(accessibility = "ಆನ್‌ಬೋರ್ಡ್ ಪಡೆಯಿರಿ")
	private WebElement getOnBoardTitle;
	
	@AndroidFindBy(accessibility ="ಮನೆಗೆ ತೆರಳಿ")
	private WebElement skipToHomeScreenButton;

	@AndroidFindBy(accessibility = "ಆನ್‌ಬೋರ್ಡಿಂಗ್ ಪ್ರಕ್ರಿಯೆಯೊಂದಿಗೆ ಪ್ರಾರಂಭಿಸಲು ದಯವಿಟ್ಟು 'ಆನ್‌ಬೋರ್ಡ್ ಪಡೆಯಿರಿ' ಅನ್ನು ಟ್ಯಾಪ್ ಮಾಡಿ.")
	private WebElement onBoardWelcomeMessage;

	public OnBoardPageKannada(AppiumDriver driver) {
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
		return new SupervisorBiometricVerificationpageKannada(driver);
	}
	
	public RegistrationTasksPage clickOnSkipToHomeScreen() {
		clickOnElement(skipToHomeScreenButton);
		return new RegistrationTasksPageKannada(driver);
	}

}
