package regclient.pages.french;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.OnBoardPage;
import regclient.page.RegistrationTasksPage;
import regclient.page.SupervisorBiometricVerificationpage;

public class OnBoardPageFrench extends OnBoardPage{

	@AndroidFindBy(accessibility = "AIDE")
	private WebElement helpButton;

	@AndroidFindBy(accessibility = "MONTER À BORD")
	private WebElement getOnBoardTitle;
	
	@AndroidFindBy(accessibility ="PASSEZ À LA MAISON")
	private WebElement skipToHomeScreenButton;

	@AndroidFindBy(accessibility = "Veuillez appuyer sur 'S'INSCRIRE' pour commencer le processus d'intégration.")
	private WebElement onBoardWelcomeMessage;

	public OnBoardPageFrench(AppiumDriver driver) {
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
		return new SupervisorBiometricVerificationpageFrench(driver);
	}
	
	public RegistrationTasksPage clickOnSkipToHomeScreen() {
		clickOnElement(skipToHomeScreenButton);
		return new RegistrationTasksPageFrench(driver);
	}

}
