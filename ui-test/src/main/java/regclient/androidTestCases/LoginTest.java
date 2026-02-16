package regclient.androidTestCases;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.time.Duration;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import regclient.BaseTest.AndroidBaseTest;
import regclient.api.ArcConfigManager;
import regclient.api.FetchUiSpec;
import regclient.api.KeycloakUserManager;
import regclient.page.BasePage;
import regclient.page.DashboardPage;
import regclient.page.LoginPage;
import regclient.page.OnBoardPage;

import regclient.page.OperationalTaskPage;
import regclient.page.ProfilePage;
import regclient.page.RegistrationTasksPage;
import regclient.page.SupervisorBiometricVerificationpage;
import regclient.page.UpdateOperatorBiometricspage;
import regclient.pages.arabic.DashboardPageArabic;
import regclient.pages.arabic.LoginPageArabic;
import regclient.pages.arabic.OnBoardPageArabic;
import regclient.pages.arabic.OperationalTaskPageArabic;
import regclient.pages.arabic.ProfilePageArabic;
import regclient.pages.arabic.RegistrationTasksPageArabic;
import regclient.pages.arabic.SupervisorBiometricVerificationpageArabic;
import regclient.pages.arabic.UpdateOperatorBiometricspageArabic;
import regclient.pages.english.DashboardPageEnglish;
import regclient.pages.english.LoginPageEnglish;
import regclient.pages.english.OnBoardPageEnglish;
import regclient.pages.english.OperationalTaskPageEnglish;
import regclient.pages.english.ProfilePageEnglish;
import regclient.pages.english.RegistrationTasksPageEnglish;
import regclient.pages.english.SupervisorBiometricVerificationpageEnglish;
import regclient.pages.english.UpdateOperatorBiometricspageEnglish;
import regclient.pages.french.DashboardPageFrench;
import regclient.pages.french.LoginPageFrench;
import regclient.pages.french.OnBoardPageFrench;
import regclient.pages.french.OperationalTaskPageFrench;
import regclient.pages.french.ProfilePageFrench;
import regclient.pages.french.RegistrationTasksPageFrench;
import regclient.pages.french.SupervisorBiometricVerificationpageFrench;
import regclient.pages.french.UpdateOperatorBiometricspageFrench;
import regclient.pages.hindi.DashboardPageHindi;
import regclient.pages.hindi.LoginPageHindi;
import regclient.pages.hindi.OnBoardPageHindi;
import regclient.pages.hindi.OperationalTaskPageHindi;
import regclient.pages.hindi.ProfilePageHindi;
import regclient.pages.hindi.RegistrationTasksPageHindi;
import regclient.pages.hindi.SupervisorBiometricVerificationpageHindi;
import regclient.pages.hindi.UpdateOperatorBiometricspageHindi;
import regclient.pages.kannada.DashboardPageKannada;
import regclient.pages.kannada.LoginPageKannada;
import regclient.pages.kannada.OnBoardPageKannada;
import regclient.pages.kannada.OperationalTaskPageKannada;
import regclient.pages.kannada.ProfilePageKannada;
import regclient.pages.kannada.RegistrationTasksPageKannada;
import regclient.pages.kannada.SupervisorBiometricVerificationpageKannada;
import regclient.pages.kannada.UpdateOperatorBiometricspageKannada;
import regclient.pages.tamil.DashboardPageTamil;
import regclient.pages.tamil.LoginPageTamil;
import regclient.pages.tamil.OnBoardPageTamil;
import regclient.pages.tamil.OperationalTaskPageTamil;
import regclient.pages.tamil.ProfilePageTamil;
import regclient.pages.tamil.RegistrationTasksPageTamil;
import regclient.pages.tamil.SupervisorBiometricVerificationpageTamil;
import regclient.pages.tamil.UpdateOperatorBiometricspageTamil;
import regclient.utils.TestDataReader;

@Test
public class LoginTest extends AndroidBaseTest {

	@Test(priority = 1, description = "Verify operator onboarding process")
	public void onboardingTest() {

		LoginPage loginPage = null;
		OnBoardPage onBoardPage = null;
		SupervisorBiometricVerificationpage supervisorBiometricVerificationpage = null;

		final String language = TestDataReader.readData("language");

		if ("eng".equalsIgnoreCase(language)) {
			loginPage = new LoginPageEnglish(driver);
		} else if ("hin".equalsIgnoreCase(language)) {
			loginPage = new LoginPageHindi(driver);
		} else if ("fra".equalsIgnoreCase(language)) {
			loginPage = new LoginPageFrench(driver);
		} else if ("kan".equalsIgnoreCase(language)) {
			loginPage = new LoginPageKannada(driver);
		} else if ("tam".equalsIgnoreCase(language)) {
			loginPage = new LoginPageTamil(driver);
		} else if ("ara".equalsIgnoreCase(language)) {
			loginPage = new LoginPageArabic(driver);
		} else {
			throw new IllegalStateException("Unsupported language in testdata.json: " + language);
		}
		loginPage.selectLanguage();

		assertTrue(loginPage.isWelcomeMessageInSelectedLanguageDisplayed(),
				"verify if the welcome msg in selected language displayed");
		loginPage.enterUserName(KeycloakUserManager.onboardingUser);

		assertTrue(loginPage.isNextButtonEnabled(), "verify if the next button enabled");
		loginPage.clickOnNextButton();

		assertTrue(loginPage.isBackButtonDisplayed(), "Verify if back button is displayed");
		assertTrue(loginPage.isForgetOptionDisplayed(), "Verify if forget password option is displayed");
		assertTrue(loginPage.isPasswordHeaderDisplayed(), "Verify if the password input box header displayed");
		loginPage.enterPassword(ArcConfigManager.getIAMUsersPassword());

		assertTrue(loginPage.isLoginButtonEnabled(), "Verify if the login button enabled");
		loginPage.clickOnloginButton();

		if ("eng".equalsIgnoreCase(language)) {
			onBoardPage = new OnBoardPageEnglish(driver);
		} else if ("hin".equalsIgnoreCase(language)) {
			onBoardPage = new OnBoardPageHindi(driver);
		} else if ("fra".equalsIgnoreCase(language)) {
			onBoardPage = new OnBoardPageFrench(driver);
		} else if ("kan".equalsIgnoreCase(language)) {
			onBoardPage = new OnBoardPageKannada(driver);
		} else if ("tam".equalsIgnoreCase(language)) {
			onBoardPage = new OnBoardPageTamil(driver);
		} else if ("ara".equalsIgnoreCase(language)) {
			onBoardPage = new OnBoardPageArabic(driver);
		} else {
			throw new IllegalStateException("Unsupported language in testdata.json: " + language);
		}
		assertTrue(onBoardPage.isGetOnBoardTitleDisplayed(), "Verify if on board page is loaded");
		assertTrue(onBoardPage.isOnBoardWelcomeMessageDisplayed(), "Verify if on board page hello message is loaded");
		onBoardPage.clickOnGetOnBoardTitle();

		if ("eng".equalsIgnoreCase(language)) {
			supervisorBiometricVerificationpage = new SupervisorBiometricVerificationpageEnglish(driver);
		} else if ("hin".equalsIgnoreCase(language)) {
			supervisorBiometricVerificationpage = new SupervisorBiometricVerificationpageHindi(driver);
		} else if ("fra".equalsIgnoreCase(language)) {
			supervisorBiometricVerificationpage = new SupervisorBiometricVerificationpageFrench(driver);
		} else if ("kan".equalsIgnoreCase(language)) {
			supervisorBiometricVerificationpage = new SupervisorBiometricVerificationpageKannada(driver);
		} else if ("tam".equalsIgnoreCase(language)) {
			supervisorBiometricVerificationpage = new SupervisorBiometricVerificationpageTamil(driver);
		} else if ("ara".equalsIgnoreCase(language)) {
			supervisorBiometricVerificationpage = new SupervisorBiometricVerificationpageArabic(driver);
		} else {
			throw new IllegalStateException("Unsupported language in testdata.json: " + language);
		}

		assertTrue(supervisorBiometricVerificationpage.isOperatorBiometricVerificationPageLoaded(),
				"Verify if operator biometric verification page is loaded");

		if (FetchUiSpec.eye.equals("yes")) {
			supervisorBiometricVerificationpage.clickOnIrisScan();
			supervisorBiometricVerificationpage.clickOnMarkExceptionButton();

			assertTrue(supervisorBiometricVerificationpage.isExceptionTypeTitleDisplayed(),
					"Verify if  mark exception is displayed");
			supervisorBiometricVerificationpage.markOneEyeException();

			supervisorBiometricVerificationpage.clickOnIrisScanTitle();
			supervisorBiometricVerificationpage.clickOnScanButton();

			assertTrue(supervisorBiometricVerificationpage.isIrisScan(), "Verify if iris scan 1st attempt");
			supervisorBiometricVerificationpage.closeScanCapturePopUp();

			assertTrue(supervisorBiometricVerificationpage.isIrisScanQualityDisplayed(),
					"Verify if iris scan threshold , Quality displayed");
			assertTrue(supervisorBiometricVerificationpage.checkThresholdValueIris(),
					"Verify if  biometric score exceeds/meets the threshold for iris");

			supervisorBiometricVerificationpage.clickOnNextButton();
		}

		if (FetchUiSpec.rightHand.equals("yes")) {

			assertTrue(supervisorBiometricVerificationpage.isRightHandScanTitleDisplayed(),
					"Verify if applicant right hand scan is displayed");
			supervisorBiometricVerificationpage.clickOnMarkExceptionButton();

			assertTrue(supervisorBiometricVerificationpage.isZoomButtonDisplayed(),
					"Verify if zoom button  is displayed");
			supervisorBiometricVerificationpage.clickOnRightHandScanTitle();
			supervisorBiometricVerificationpage.clickOnScanButton();

			assertTrue(supervisorBiometricVerificationpage.isRightHandScan(), "Verify if right hand scan 1st attempt");
			supervisorBiometricVerificationpage.closeScanCapturePopUp();

			assertTrue(supervisorBiometricVerificationpage.isRightHandScanQualityDisplayed(),
					"Verify if right hand scan threshold , Quality displayed");
			assertTrue(supervisorBiometricVerificationpage.checkThresholdValueRightHand(),
					"Verify if  biometric score exceeds/meets the threshold for right hand");

			supervisorBiometricVerificationpage.clickOnNextButton();
		}

		if (FetchUiSpec.leftHand.equals("yes")) {
			assertTrue(supervisorBiometricVerificationpage.isLeftHandScanTitleDisplayed(),
					"Verify if applicant right hand scan is displayed");
			supervisorBiometricVerificationpage.clickOnMarkExceptionButton();

			assertTrue(supervisorBiometricVerificationpage.isZoomButtonDisplayed(),
					"Verify if zoom button  is displayed");
			supervisorBiometricVerificationpage.clickOnLeftHandScanTitle();
			supervisorBiometricVerificationpage.clickOnScanButton();

			assertTrue(supervisorBiometricVerificationpage.isLeftHandScan(), "Verify if Left hand scan 1st attempt");
			supervisorBiometricVerificationpage.closeScanCapturePopUp();

			assertTrue(supervisorBiometricVerificationpage.isLeftHandScanQualityDisplayed(),
					"Verify if left hand scan threshold , Quality displayed");
			assertTrue(supervisorBiometricVerificationpage.checkThresholdValueLeftHand(),
					"Verify if  biometric score exceeds/meets the threshold for left hand");

			supervisorBiometricVerificationpage.clickOnNextButton();
		}

		if (FetchUiSpec.thumb.equals("yes")) {
			assertTrue(supervisorBiometricVerificationpage.isThumbsScanTitleDisplayed(),
					"Verify if thumbs scan page is displayed");
			supervisorBiometricVerificationpage.clickOnMarkExceptionButton();

			assertTrue(supervisorBiometricVerificationpage.isExceptionTypeTitleDisplayed(),
					"Verify if applicant biometric mark exception is displayed");
			supervisorBiometricVerificationpage.markOneFingerException();

			supervisorBiometricVerificationpage.clickOnThumbsScanTitle();
			supervisorBiometricVerificationpage.clickOnScanButton();

			assertTrue(supervisorBiometricVerificationpage.isThumbsScan(), "Verify if thumbs scan 1st attempt");
			supervisorBiometricVerificationpage.closeScanCapturePopUp();

			assertTrue(supervisorBiometricVerificationpage.isThumbsScanQualityDisplayed(),
					"Verify if thumbs scan threshold , Quality displayed");
			assertTrue(supervisorBiometricVerificationpage.checkThresholdValueThumbs(),
					"Verify if  biometric score exceeds/meets the threshold for thumbs");

			supervisorBiometricVerificationpage.clickOnNextButton();
		}

		if (FetchUiSpec.face.equals("yes")) {
			assertTrue(supervisorBiometricVerificationpage.isFaceScanTitleDisplayed(),
					"Verify if face scan page is displayed");
			supervisorBiometricVerificationpage.clickOnMarkExceptionButton();

			supervisorBiometricVerificationpage.clickOnFaceScanTitle();
			supervisorBiometricVerificationpage.clickOnScanButton();

			assertTrue(supervisorBiometricVerificationpage.isFaceScan(), "Verify if face scan 1st attempt");
			supervisorBiometricVerificationpage.closeScanCapturePopUp();

			assertTrue(supervisorBiometricVerificationpage.isFaceScanQualityDisplayed(),
					"Verify if face scan threshold , Quality displayed");
			assertTrue(supervisorBiometricVerificationpage.checkThresholdValueFace(),
					"Verify if  biometric score exceeds/meets the threshold for face");
			supervisorBiometricVerificationpage.clickOnNextButton();
		}

		assertTrue(supervisorBiometricVerificationpage.isOperatorBiometricVerificationPageLoaded(),
				"Verify if operational tasks page is loaded");
		assertTrue(supervisorBiometricVerificationpage.isVerifyAndSaveButtonEnabled(),
				"Verify if verify and save button is display and enable");
		assertFalse(supervisorBiometricVerificationpage.isExceptionScanTitleDisplayed(),
				"Verify if exception scan icon is displayed");

		boolean isDismissLoaded = false;

		for (int i = 0; i < 3; i++) {
			supervisorBiometricVerificationpage.clickOnVerifyAndSaveButton();

			if (supervisorBiometricVerificationpage.isDismissPageLoaded()) {
				isDismissLoaded = true;
				break;
			}
		}

		if (!isDismissLoaded) {
			System.out.println("INFO: Dismiss page not loaded after clicking Verify & Save 3 times");
		}
		assertTrue(supervisorBiometricVerificationpage.isOperatorOnboardedPopupLoaded(),
				"Verify if operator biometrics updated success message is displayed");
		supervisorBiometricVerificationpage.clickOnHomeButton();

	}

}
