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
		
		assertTrue(loginPage.isLoginPageLoaded(),"verify if login page is displayed");
		assertTrue(loginPage.isMosipLogoDisplay(),"verify if mosip logo is displayed");
		assertTrue(loginPage.isWelcomeMsgDisplay(),"Verify if welcome note \"welcome to community registration client!\" message should be displayed.");
		assertTrue(loginPage.isHelpButtonDisplay(),"Verify if check help button on the top right of the page");
		
		loginPage.enterusername(TestDataReader.readData("username"));
		
		assertTrue(loginPage.isNextButtonEnable(),"verify if the next button enable");
		loginPage.clickOnNextButton();
		
		assertTrue(loginPage.isBackButtonDisplay(),"Verify if back button is displayed");
		assertTrue(loginPage.isForgetOptionDisplay(),"Verify if forget password option is displayed");
		assertTrue(loginPage.isPasswordHeaderDisplay(),"Verify if the password  input box header  display");
		loginPage.enterpassword(TestDataReader.readData("password"));
		
		assertTrue(loginPage.isLoginButtonEnable(),"Verify if the login button enable");
		RegistrationTasksPage RegistrationTasksPage=loginPage.clickOnloginButton();
		
		assertTrue(RegistrationTasksPage.isRegistrationTasksPageLoaded(),"Verify if registration tasks page is loaded");
		
		RegistrationTasksPage.clickOnSynchronizeDataButton();
		
		assertTrue(RegistrationTasksPage.isMasterDataSyncCompletedDisplay(),"Verify if MasterData Sync should be completed successfully");
		assertTrue(RegistrationTasksPage.isScriptSyncCompletedDisplay(),"Verify if Script Sync should be completed successfully");
		assertTrue(RegistrationTasksPage.isPolicykeySyncCompletedDisplay(),"Verify if Policykey sync should be completed successfully");
		
		
	}
	@Test
	public void loginWithInvalidPasscode() {
		LoginPage loginPage=new LoginPage(driver);
		
		loginPage.selectLanguage(TestDataReader.readData("language"));
		loginPage.enterusername(TestDataReader.readData("nonRegisteredUsername"));
		
		assertTrue(loginPage.isNextButtonEnable(),"verify if the next button enable");
		loginPage.clickOnNextButton();
		
		assertTrue(loginPage.isUserNotFoundErrorMsgDisplay(),"verify if error message should be displayed as “user not found”");
		loginPage.enterusername(TestDataReader.readData("username"));
		
		assertTrue(loginPage.isNextButtonEnable(),"verify if the next button enable");
		loginPage.clickOnNextButton();
		
		//assertFalse(loginPage.isLoginButtonEnable(),"verify if the login button is disable without entering password");
		
		loginPage.enterpassword(TestDataReader.readData("InvalidPassword"));
		assertTrue(loginPage.isLoginButtonEnable(),"Verify if the login button enable");
		
		loginPage.clickOnloginButton();
		assertTrue(loginPage.isPasswordIncorrectErrorMsgDisplay(),"verify if error message should be displayed as password incorrect!");
		
		loginPage.clickOnBackButton();
		assertTrue(loginPage.isUsernameHeaderDisplay(),"Verify if the username  input box header  display");
		
		
		
	}
}
