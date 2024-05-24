package androidTestCases;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import BaseTest.AndroidBaseTest;
import regclient.page.BasePage;
import regclient.page.DashboardPage;
import regclient.page.LoginPage;
import regclient.page.OnBoardPage;
import regclient.page.OperationalTaskPage;
import regclient.page.RegistrationTasksPage;
import regclient.page.SupervisorBiometricVerificationpage;
import regclient.pages.arabic.DashboardPageArabic;
import regclient.pages.arabic.LoginPageArabic;
import regclient.pages.arabic.OnBoardPageArabic;
import regclient.pages.arabic.OperationalTaskPageArabic;
import regclient.pages.arabic.RegistrationTasksPageArabic;
import regclient.pages.arabic.SupervisorBiometricVerificationpageArabic;
import regclient.pages.english.DashboardPageEnglish;
import regclient.pages.english.LoginPageEnglish;
import regclient.pages.english.OnBoardPageEnglish;
import regclient.pages.english.OperationalTaskPageEnglish;
import regclient.pages.english.RegistrationTasksPageEnglish;
import regclient.pages.english.SupervisorBiometricVerificationpageEnglish;
import regclient.pages.french.DashboardPageFrench;
import regclient.pages.french.LoginPageFrench;
import regclient.pages.french.OnBoardPageFrench;
import regclient.pages.french.OperationalTaskPageFrench;
import regclient.pages.french.RegistrationTasksPageFrench;
import regclient.pages.french.SupervisorBiometricVerificationpageFrench;
import regclient.pages.hindi.DashboardPageHindi;
import regclient.pages.hindi.LoginPageHindi;
import regclient.pages.hindi.OnBoardPageHindi;
import regclient.pages.hindi.OperationalTaskPageHindi;
import regclient.pages.hindi.RegistrationTasksPageHindi;
import regclient.pages.hindi.SupervisorBiometricVerificationpageHindi;
import regclient.pages.kannada.DashboardPageKannada;
import regclient.pages.kannada.LoginPageKannada;
import regclient.pages.kannada.OnBoardPageKannada;
import regclient.pages.kannada.OperationalTaskPageKannada;
import regclient.pages.kannada.RegistrationTasksPageKannada;
import regclient.pages.kannada.SupervisorBiometricVerificationpageKannada;
import regclient.pages.tamil.DashboardPageTamil;
import regclient.pages.tamil.LoginPageTamil;
import regclient.pages.tamil.OnBoardPageTamil;
import regclient.pages.tamil.OperationalTaskPageTamil;
import regclient.pages.tamil.RegistrationTasksPageTamil;
import regclient.pages.tamil.SupervisorBiometricVerificationpageTamil;

import regclient.utils.TestDataReader;

public class logintest  extends AndroidBaseTest {

	@Test
	public void initallLaunch() {
		BasePage.disableAutoRotation();
		LoginPageEnglish   loginPage= new LoginPageEnglish(driver);

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
		
		assertTrue(loginPage.isSyncCompletedSuccessfullyMessageDisplayed(), "Verify if the sync is completed");

	}

	@Test
	public void loginTest() {
		BasePage.disableAutoRotation();
		LoginPage   loginPage=null;
		OnBoardPage onBoardPage=null;
		OperationalTaskPage operationalTaskPage=null;
		RegistrationTasksPage registrationTasksPage=null;
		DashboardPage dashboardPage=null;

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
		
		loginPage.enterUserName(TestDataReader.readData("onBoardUsername"));

		assertTrue(loginPage.isNextButtonEnabled(),"verify if the next button enabled");
		loginPage.clickOnNextButton();

		//assertFalse(loginPage.isLoginButtonEnabled(),"verify if the login button is disable without entering password");
		assertTrue(loginPage.isBackButtonDisplayed(), "Verify if back button is displayed");
		assertTrue(loginPage.isForgetOptionDisplayed(), "Verify if forget password option is displayed");
		assertTrue(loginPage.isPasswordHeaderDisplayed(), "Verify if the password input box header displayed");


		loginPage.enterPassword(TestDataReader.readData("InvalidPassword"));
		assertTrue(loginPage.isLoginButtonEnabled(),"Verify if the login button enabled");

		loginPage.clickOnloginButton();
		assertTrue(loginPage.isPasswordIncorrectErrorMessageDisplayed(),"verify if error message should be displayeded as password incorrect!");

		loginPage.clickOnBackButton();
		assertTrue(loginPage.isUserNameHeaderDisplayed(),"Verify if the username  input box header  displayed");

		loginPage.enterUserName(TestDataReader.readData("onBoardUsername"));

		assertTrue(loginPage.isNextButtonEnabled(),"verify if the next button enabled");
		loginPage.clickOnNextButton();

		loginPage.enterPassword(TestDataReader.readData("password"));
		assertTrue(loginPage.isLoginButtonEnabled(),"Verify if the login button enabled");

		loginPage.clickOnloginButton();
		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			onBoardPage=new OnBoardPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			onBoardPage=new OnBoardPageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			onBoardPage=new OnBoardPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			onBoardPage=new OnBoardPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			onBoardPage=new OnBoardPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			onBoardPage=new OnBoardPageArabic(driver);
		}
		onBoardPage.clickOnSkipToHomeScreen();
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
		registrationTasksPage.clickOnOperationalTasksTitle();	
		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			operationalTaskPage=new OperationalTaskPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			operationalTaskPage=new OperationalTaskPageHindi(driver);

		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			operationalTaskPage=new OperationalTaskPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			operationalTaskPage=new OperationalTaskPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			operationalTaskPage=new OperationalTaskPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			operationalTaskPage=new OperationalTaskPageArabic(driver);
		}
		assertTrue(operationalTaskPage.isOperationalTaskPageLoaded(), "Verify if operational Task Page is loaded");

		operationalTaskPage.clickSynchronizeDataButton();
		assertTrue(operationalTaskPage.checkLastSyncDate(), "Verify  last sync date and time");
		
		registrationTasksPage.clickOnDashboardButton();

		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			dashboardPage=new DashboardPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			dashboardPage=new DashboardPageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			dashboardPage=new DashboardPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			dashboardPage=new DashboardPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			dashboardPage=new DashboardPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			dashboardPage=new DashboardPageArabic(driver);
		}

		assertTrue(dashboardPage.isDashboardTitleDisplayed(),"Verify if dashboard  page is loaded");
		assertTrue(dashboardPage.isPacketsUploadedValueDisplayed(),"Verify if packet uploaded value displayed");
		assertTrue(dashboardPage.isPacketsSyncedValueDisplayed(),"Verify if packet synced value displayed");
		assertTrue(dashboardPage.isUserIDDisplayed(),"Verify if user ID displayed");
		assertTrue(dashboardPage.isUserNameDisplayed(),"Verify if user name displayed");
		assertTrue(dashboardPage.isStatusTitleDisplayed(),"Verify if status displayed");
	}

	@Test
	public void getOnBoard() {
		BasePage.disableAutoRotation();
		LoginPage   loginPage=null;
		OnBoardPage onBoardPage=null;
		SupervisorBiometricVerificationpage supervisorBiometricVerificationpage=null;

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
		loginPage.enterUserName(TestDataReader.readData("onBoardUsername"));

		assertTrue(loginPage.isNextButtonEnabled(), "verify if the next button enabled");
		loginPage.clickOnNextButton();

		assertTrue(loginPage.isBackButtonDisplayed(), "Verify if back button is displayed");
		assertTrue(loginPage.isForgetOptionDisplayed(), "Verify if forget password option is displayed");
		assertTrue(loginPage.isPasswordHeaderDisplayed(), "Verify if the password input box header displayed");
		loginPage.enterPassword(TestDataReader.readData("password"));

		assertTrue(loginPage.isLoginButtonEnabled(), "Verify if the login button enabled");
		loginPage.clickOnloginButton();

		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			onBoardPage=new OnBoardPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			onBoardPage=new OnBoardPageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			onBoardPage=new OnBoardPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			onBoardPage=new OnBoardPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			onBoardPage=new OnBoardPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			onBoardPage=new OnBoardPageArabic(driver);
		}
		assertTrue(onBoardPage.isGetOnBoardTitleDisplayed(), "Verify if on board page is loaded");
		assertTrue(onBoardPage.isHelpButtonDisplayed(), "Verify if help button is displayed");
		assertTrue(onBoardPage.isOnBoardWelcomeMessageDisplayed(), "Verify if on board page hello message is loaded");
		onBoardPage.clickOnGetOnBoardTitle();

		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			supervisorBiometricVerificationpage=new SupervisorBiometricVerificationpageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			supervisorBiometricVerificationpage=new SupervisorBiometricVerificationpageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			supervisorBiometricVerificationpage=new SupervisorBiometricVerificationpageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			supervisorBiometricVerificationpage=new SupervisorBiometricVerificationpageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			supervisorBiometricVerificationpage=new SupervisorBiometricVerificationpageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			supervisorBiometricVerificationpage=new SupervisorBiometricVerificationpageArabic(driver);
		}
		assertTrue(supervisorBiometricVerificationpage.isSupervisorBiometricVerificationPageLoaded(), "Verify if operational tasks page is loaded");
		supervisorBiometricVerificationpage.clickOnIrisScan();;

		supervisorBiometricVerificationpage.clickOnMarkExceptionButton();

		assertTrue(supervisorBiometricVerificationpage.isExceptionTypeTitleDisplyed(),"Verify if  mark exception is displayed");
		supervisorBiometricVerificationpage.markOneEyeException();

		//	assertFalse(supervisorBiometricVerificationpage.isZoomButtonDisplyed(),"Verify if zoom button  is not  displayed for iris");
		supervisorBiometricVerificationpage.clickOnExceptionTypeTemporaryButton();

		assertTrue(supervisorBiometricVerificationpage.isCommentHeaderDisplyed(),"Verify if Comments header is displayed");
	//	supervisorBiometricVerificationpage.enterCommentsInTextBox(TestDataReader.readData("comments"));

		supervisorBiometricVerificationpage.clickOnIrisScanTitle();
		supervisorBiometricVerificationpage.clickOnScanButton();

		assertTrue(supervisorBiometricVerificationpage.isIrisScan(),"Verify if iris scan 1st attempt");
		supervisorBiometricVerificationpage.closeScanCapturePopUp();

		assertTrue(supervisorBiometricVerificationpage.isIrisScanQualityDisplyed(),"Verify if iris scan threshold , Quality displayed");
		assertTrue(supervisorBiometricVerificationpage.checkThresholdValueIris(),"Verify if  biometric score exceeds/meets the threshold for iris");

		supervisorBiometricVerificationpage.clickOnNextButton();
		//righthand

		assertTrue(supervisorBiometricVerificationpage.isRightHandScanTitleDisplyed(),"Verify if applicant right hand scan is displayed");
		supervisorBiometricVerificationpage.clickOnMarkExceptionButton();

		assertTrue(supervisorBiometricVerificationpage.isZoomButtonDisplyed(),"Verify if zoom button  is displayed");
		supervisorBiometricVerificationpage.clickOnRightHandScanTitle();
		supervisorBiometricVerificationpage.clickOnScanButton();

		assertTrue(supervisorBiometricVerificationpage.isRightHandScan(),"Verify if right hand scan 1st attempt");
		supervisorBiometricVerificationpage.closeScanCapturePopUp();

		assertTrue(supervisorBiometricVerificationpage.isRightHandScanQualityDisplyed(),"Verify if right hand scan threshold , Quality displayed");
		assertTrue(supervisorBiometricVerificationpage.checkThresholdValueRightHand(),"Verify if  biometric score exceeds/meets the threshold for right hand");

		supervisorBiometricVerificationpage.clickOnNextButton();
		//lefthand

		assertTrue(supervisorBiometricVerificationpage.isLeftHandScanTitleDisplyed(),"Verify if applicant right hand scan is displayed");
		supervisorBiometricVerificationpage.clickOnMarkExceptionButton();

		assertTrue(supervisorBiometricVerificationpage.isZoomButtonDisplyed(),"Verify if zoom button  is displayed");
		supervisorBiometricVerificationpage.clickOnleftHandScanTitle();
		supervisorBiometricVerificationpage.clickOnScanButton();

		assertTrue(supervisorBiometricVerificationpage.isLeftHandScan(),"Verify if Left hand scan 1st attempt");
		supervisorBiometricVerificationpage.closeScanCapturePopUp();

		assertTrue(supervisorBiometricVerificationpage.isLeftHandScanQualityDisplyed(),"Verify if left hand scan threshold , Quality displayed");
		assertTrue(supervisorBiometricVerificationpage.checkThresholdValueLeftHand(),"Verify if  biometric score exceeds/meets the threshold for left hand");

		supervisorBiometricVerificationpage.clickOnNextButton();
		//thumbs

		assertTrue(supervisorBiometricVerificationpage.isThumbsScanTitleDisplyed(),"Verify if thumbs scan page is displayed");
		supervisorBiometricVerificationpage.clickOnMarkExceptionButton();

		assertTrue(supervisorBiometricVerificationpage.isExceptionTypeTitleDisplyed(),"Verify if applicant biometric mark exception is displayed");
		supervisorBiometricVerificationpage.markOneFingureException();

		//	assertFalse(supervisorBiometricVerificationpage.isZoomButtonDisplyed(),"Verify if zoom button  is not  displayed for thumb");
		supervisorBiometricVerificationpage.clickOnExceptionTypeTemporaryButton();

		assertTrue(supervisorBiometricVerificationpage.isCommentHeaderDisplyed(),"Verify if Comments header is displayed");
	//	supervisorBiometricVerificationpage.enterCommentsInTextBox(TestDataReader.readData("comments"));

		supervisorBiometricVerificationpage.clickOnThumbsScanTitle();
		supervisorBiometricVerificationpage.clickOnScanButton();

		assertTrue(supervisorBiometricVerificationpage.isThumbsScan(),"Verify if thumbs scan 1st attempt");
		supervisorBiometricVerificationpage.closeScanCapturePopUp();

		assertTrue(supervisorBiometricVerificationpage.isThumbsScanQualityDisplyed(),"Verify if thumbs scan threshold , Quality displayed");
		assertTrue(supervisorBiometricVerificationpage.checkThresholdValueThumbs(),"Verify if  biometric score exceeds/meets the threshold for thumbs");
		supervisorBiometricVerificationpage.clickOnNextButton();
		//face
		assertTrue(supervisorBiometricVerificationpage.isFaceScanTitleDisplyed(),"Verify if face scan page is displayed");
		supervisorBiometricVerificationpage.clickOnMarkExceptionButton();

		assertTrue(supervisorBiometricVerificationpage.isMarkingExceptionsOnFaceIsNotAllowedTextDisplyed(),"Verify if is Marking Exceptions On Face Is Not Allowed Text Displyed");
		supervisorBiometricVerificationpage.clickOnFaceScanTitle();
		supervisorBiometricVerificationpage.clickOnScanButton();

		assertTrue(supervisorBiometricVerificationpage.isFaceScan(),"Verify if face scan 1st attempt");
		supervisorBiometricVerificationpage.closeScanCapturePopUp();

		assertTrue(supervisorBiometricVerificationpage.isFaceScanQualityDisplyed(),"Verify if face scan threshold , Quality displayed");
		assertTrue(supervisorBiometricVerificationpage.checkThresholdValueFace(),"Verify if  biometric score exceeds/meets the threshold for face");
		supervisorBiometricVerificationpage.clickOnNextButton();

		assertTrue(supervisorBiometricVerificationpage.isSupervisorBiometricVerificationPageLoaded(), "Verify if operational tasks page is loaded");
		assertTrue(supervisorBiometricVerificationpage.clickOnVerifyAndSaveButton(), "Verify if verify and save button is display and enable");

		assertFalse(supervisorBiometricVerificationpage.isExceptionScanTitleDisplyed(),"Verify if exception scan icon is displayed");

	}
}
