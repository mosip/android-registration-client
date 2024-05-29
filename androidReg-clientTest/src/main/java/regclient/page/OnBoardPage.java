package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class OnBoardPage extends BasePage {

	public OnBoardPage(AppiumDriver driver) {
		super(driver);
	}
	public abstract boolean isGetOnBoardTitleDisplayed();
	
    public abstract boolean isHelpButtonDisplayed();
    
    public abstract boolean isOnBoardWelcomeMessageDisplayed();
    
    public abstract SupervisorBiometricVerificationpage clickOnGetOnBoardTitle();
    
    public abstract  RegistrationTasksPage clickOnSkipToHomeScreen();
    
}
