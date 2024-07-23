package regclient.pages.arabic;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.OnBoardPage;
import regclient.page.RegistrationTasksPage;
import regclient.page.SupervisorBiometricVerificationpage;

public class OnBoardPageArabic extends OnBoardPage{

	@AndroidFindBy(accessibility = "تعليمات")
	private WebElement helpButton;

	@AndroidFindBy(accessibility = "إركب")
	private WebElement getOnBoardTitle;
	
	@AndroidFindBy(accessibility = "انتقل إلى المنزل")
	private WebElement skipToHomeScreenButton;

	@AndroidFindBy(accessibility = "الرجاء الضغط على 'البدء في الانضمام' للبدء في عملية الإعداد.")
	private WebElement onBoardWelcomeMessage;

	public OnBoardPageArabic(AppiumDriver driver) {
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
		return new SupervisorBiometricVerificationpageArabic(driver);
	}
	
	public RegistrationTasksPage clickOnSkipToHomeScreen() {
		clickOnElement(skipToHomeScreenButton);
		return new RegistrationTasksPageArabic(driver);
	}

}
