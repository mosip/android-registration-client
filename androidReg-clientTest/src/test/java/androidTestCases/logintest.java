package androidTestCases;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import BaseTest.AndroidBaseTest;
import regclient.page.BasePage;
import regclient.page.LoginPage;
import regclient.page.RegistrationTasksPage;
import regclient.pages.arabic.LoginPageArabic;
import regclient.pages.arabic.RegistrationTasksPageArabic;
import regclient.pages.english.LoginPageEnglish;
import regclient.pages.english.RegistrationTasksPageEnglish;
import regclient.pages.french.LoginPageFrench;
import regclient.pages.french.RegistrationTasksPageFrench;
import regclient.pages.hindi.LoginPageHindi;
import regclient.pages.hindi.RegistrationTasksPageHindi;
import regclient.pages.kannada.LoginPageKannada;
import regclient.pages.kannada.RegistrationTasksPageKannada;
import regclient.pages.tamil.LoginPageTamil;
import regclient.pages.tamil.RegistrationTasksPageTamil;
import regclient.utils.TestDataReader;

public class logintest  extends AndroidBaseTest {

	@Test
	public void login() {
		BasePage.disableAutoRotation();
		LoginPage   loginPage=null;
		RegistrationTasksPage registrationTasksPage=null;

		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			loginPage = new LoginPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			loginPage = new LoginPageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			loginPage = new LoginPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			loginPage = new LoginPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			loginPage = new LoginPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			loginPage = new LoginPageArabic(driver);
		}
		loginPage.selectLanguage();

		assertTrue(loginPage.isWelcomeMessageInSelectedLanguageDisplayed(), "verify if the welcome msg in selected language displayed");
		loginPage.enterUserName(TestDataReader.readData("username"));

		assertTrue(loginPage.isNextButtonEnabled(), "verify if the next button enabled");
		loginPage.clickOnNextButton();

		assertTrue(loginPage.isBackButtonDisplayed(), "Verify if back button is displayed");
		assertTrue(loginPage.isForgetOptionDisplayed(), "Verify if forget password option is displayed");
		assertTrue(loginPage.isPasswordHeaderDisplayed(), "Verify if the password input box header displayed");
		loginPage.enterPassword(TestDataReader.readData("password"));

		assertTrue(loginPage.isLoginButtonEnabled(), "Verify if the login button enabled");
		loginPage.clickOnloginButton();

		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			registrationTasksPage=new RegistrationTasksPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			registrationTasksPage=new RegistrationTasksPageHindi(driver);

		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			registrationTasksPage=new RegistrationTasksPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			registrationTasksPage=new RegistrationTasksPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			registrationTasksPage=new RegistrationTasksPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			registrationTasksPage=new RegistrationTasksPageArabic(driver);
		}
		assertTrue(registrationTasksPage.isRegistrationTasksPageLoaded(), "Verify if registration tasks page is loaded");
		registrationTasksPage.clickOnSynchronizeDataButton();

		// assertTrue(registrationTasksPage.isMasterDataSyncCompletedDisplayed(),"Verify if masterData sync should be completed successfully");
		// assertTrue(registrationTasksPage.isScriptSyncCompletedDisplayed(),"Verify if script sync should be completed successfully");
		// assertTrue(registrationTasksPage.isPolicykeySyncCompletedDisplayed(),"Verify if policykey sync should be completed successfully");
	}

	@Test
	public void loginWithInvalidPasscode() {
		BasePage.disableAutoRotation();
		LoginPage   loginPage=null;

		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			loginPage = new LoginPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			loginPage = new LoginPageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			loginPage = new LoginPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			loginPage = new LoginPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			loginPage = new LoginPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			loginPage = new LoginPageArabic(driver);
		}

		loginPage.selectLanguage();

		assertTrue(loginPage.isLoginPageLoaded(),"verify if login page is displayeded");
		assertTrue(loginPage.isMosipLogoDisplayed(),"verify if mosip logo is displayeded");
		assertTrue(loginPage.isWelcomeMessageInSelectedLanguageDisplayed(),"Verify if welcome note \"welcome to community registration client!\" message should be displayeded.");
		assertTrue(loginPage.isHelpButtonDisplayed(),"Verify if check help button on the top right of the page");
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
