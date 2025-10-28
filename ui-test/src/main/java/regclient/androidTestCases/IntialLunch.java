package regclient.androidTestCases;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import regclient.BaseTest.AndroidBaseTest;
import regclient.api.ArcConfigManager;
import regclient.api.KeycloakUserManager;
import regclient.page.BasePage;
import regclient.pages.english.LoginPageEnglish;

public class IntialLunch extends AndroidBaseTest {

	@Test
	public void initallLaunch() {
		BasePage.disableAutoRotation();
		LoginPageEnglish   loginPage= new LoginPageEnglish(driver);

//		assertTrue(loginPage.isWelcomeMessageInSelectedLanguageDisplayed(), "verify if the welcome msg in selected language displayed");
		loginPage.enterUserName(KeycloakUserManager.moduleSpecificUser);

		assertTrue(loginPage.isNextButtonEnabled(), "verify if the next button enabled");
		loginPage.clickOnNextButton();

		assertTrue(loginPage.isBackButtonDisplayed(), "Verify if back button is displayed");
		assertTrue(loginPage.isForgetOptionDisplayed(), "Verify if forget password option is displayed");
		assertTrue(loginPage.isPasswordHeaderDisplayed(), "Verify if the password input box header displayed");
		loginPage.enterPassword(ArcConfigManager.getIAMUsersPassword());

		assertTrue(loginPage.isLoginButtonEnabled(), "Verify if the login button enabled");
		loginPage.clickOnloginButton();
		
		assertTrue(loginPage.isSyncCompletedSuccessfullyMessageDisplayed(), "Verify if the sync is completed");

	}
}
