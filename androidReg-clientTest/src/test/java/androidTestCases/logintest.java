package androidTestCases;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import BaseTest.AndroidBaseTest;
import regclient.pages.LoginPage;
import regclient.pages.RegistrationTasksPage;
import regclient.utils.TestDataReader;

public class logintest  extends AndroidBaseTest {

	@Test
	public void login() {
		LoginPage loginPage=new LoginPage(driver);
		
		assertTrue(loginPage.isLoginPageLoaded(),"verify if login page is displayeded");
		assertTrue(loginPage.isMosipLogoDisplayed(),"verify if mosip logo is displayeded");
		assertTrue(loginPage.isWelcomeMessageDisplayed(),"Verify if welcome note \"welcome to community registration client!\" message should be displayeded.");
		assertTrue(loginPage.isHelpButtonDisplayed(),"Verify if check help button on the top right of the page");
		
		loginPage.enterUserName(TestDataReader.readData("username"));
		
		assertTrue(loginPage.isNextButtonEnabled(),"verify if the next button enabled");
		loginPage.clickOnNextButton();
		
		assertTrue(loginPage.isBackButtonDisplayed(),"Verify if back button is displayeded");
		assertTrue(loginPage.isForgetOptionDisplayed(),"Verify if forget password option is displayeded");
		assertTrue(loginPage.isPasswordHeaderDisplayed(),"Verify if the password  input box header  displayed");
		loginPage.enterPassword(TestDataReader.readData("password"));
		
		assertTrue(loginPage.isLoginButtonEnabled(),"Verify if the login button enabled");
		RegistrationTasksPage RegistrationTasksPage=loginPage.clickOnloginButton();
		
		assertTrue(RegistrationTasksPage.isRegistrationTasksPageLoaded(),"Verify if registration tasks page is loaded");
		
		RegistrationTasksPage.clickOnSynchronizeDataButton();
		
		assertTrue(RegistrationTasksPage.isMasterDataSyncCompletedDisplayed(),"Verify if masterData sync should be completed successfully");
		assertTrue(RegistrationTasksPage.isScriptSyncCompletedDisplayed(),"Verify if script sync should be completed successfully");
		assertTrue(RegistrationTasksPage.isPolicykeySyncCompletedDisplayed(),"Verify if policykey sync should be completed successfully");
		
		
	}
	@Test
	public void loginWithInvalidPasscode() {
		LoginPage loginPage=new LoginPage(driver);
		
		loginPage.selectLanguage(TestDataReader.readData("language"));
		loginPage.enterUserName(TestDataReader.readData("nonRegisteredUsername"));
		
		assertTrue(loginPage.isNextButtonEnabled(),"verify if the next button enabled");
		loginPage.clickOnNextButton();
		
		assertTrue(loginPage.isUserNotFoundErrorMessageDisplayed(),"verify if error message should be displayeded as “user not found”");
		loginPage.enterUserName(TestDataReader.readData("username"));
		
		assertTrue(loginPage.isNextButtonEnabled(),"verify if the next button enabled");
		loginPage.clickOnNextButton();
		
		//assertFalse(loginPage.isLoginButtonEnabled(),"verify if the login button is disable without entering password");
		
		loginPage.enterPassword(TestDataReader.readData("InvalidPassword"));
		assertTrue(loginPage.isLoginButtonEnabled(),"Verify if the login button enabled");
		
		loginPage.clickOnloginButton();
		assertTrue(loginPage.isPasswordIncorrectErrorMessageDisplayed(),"verify if error message should be displayeded as password incorrect!");
		
		loginPage.clickOnBackButton();
		assertTrue(loginPage.isUserNameHeaderDisplayed(),"Verify if the username  input box header  displayed");
		
		
		
	}
}
