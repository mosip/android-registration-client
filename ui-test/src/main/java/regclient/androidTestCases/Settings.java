package regclient.androidTestCases;


import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import org.testng.annotations.Test;

import regclient.BaseTest.AndroidBaseTest;
import regclient.api.ArcConfigManager;
import regclient.api.FetchUiSpec;
import regclient.api.KeycloakUserManager;
import regclient.page.BasePage;
import regclient.page.LoginPage;
import regclient.page.MockSBIPage;
import regclient.page.ProfilePage;
import regclient.page.RegistrationTasksPage;
import regclient.page.SettingsPage;
import regclient.pages.arabic.LoginPageArabic;
import regclient.pages.arabic.RegistrationTasksPageArabic;
import regclient.pages.arabic.SettingsPageArabic;
import regclient.pages.english.LoginPageEnglish;
import regclient.pages.english.RegistrationTasksPageEnglish;
import regclient.pages.english.SettingsPageEnglish;
import regclient.pages.french.LoginPageFrench;
import regclient.pages.french.RegistrationTasksPageFrench;
import regclient.pages.french.SettingsPageFrench;
import regclient.pages.hindi.LoginPageHindi;
import regclient.pages.hindi.RegistrationTasksPageHindi;
import regclient.pages.hindi.SettingsPageHindi;
import regclient.pages.kannada.LoginPageKannada;
import regclient.pages.kannada.RegistrationTasksPageKannada;
import regclient.pages.kannada.SettingsPageKannada;
import regclient.pages.tamil.LoginPageTamil;
import regclient.pages.tamil.RegistrationTasksPageTamil;
import regclient.pages.tamil.SettingsPageTamil;
import regclient.utils.TestDataReader;

public class Settings extends AndroidBaseTest {

	@Test
	public void settings() throws InterruptedException {

		BasePage.disableAutoRotation();
		FetchUiSpec.getUiSpec("newProcess");
		FetchUiSpec.getBiometricDetails("individualBiometrics");
		LoginPage loginPage = null;
		RegistrationTasksPage registrationTasksPage = null;
		ProfilePage profilePage = null;
		SettingsPage settingsPage = null;

		if (TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			loginPage = new LoginPageEnglish(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("hin")) {
			loginPage = new LoginPageHindi(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("fra")) {
			loginPage = new LoginPageFrench(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("kan")) {
			loginPage = new LoginPageKannada(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("tam")) {
			loginPage = new LoginPageTamil(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("ara")) {
			loginPage = new LoginPageArabic(driver);
		}
		loginPage.selectLanguage();

		assertTrue(loginPage.isWelcomeMessageInSelectedLanguageDisplayed(),
				"verify if the welcome msg in selected language displayed");
		loginPage.enterUserName(KeycloakUserManager.moduleSpecificUser);
		loginPage.clickOnNextButton();

		loginPage.enterPassword(ArcConfigManager.getIAMUsersPassword());
		loginPage.clickOnloginButton();

		if (TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			registrationTasksPage = new RegistrationTasksPageEnglish(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("hin")) {
			registrationTasksPage = new RegistrationTasksPageHindi(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("fra")) {
			registrationTasksPage = new RegistrationTasksPageFrench(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("kan")) {
			registrationTasksPage = new RegistrationTasksPageKannada(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("tam")) {
			registrationTasksPage = new RegistrationTasksPageTamil(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("ara")) {
			registrationTasksPage = new RegistrationTasksPageArabic(driver);
		}
		assertTrue(registrationTasksPage.isRegistrationTasksPageLoaded(),
				"Verify if registration tasks page is loaded");
		registrationTasksPage.clickOnSettingsButton();

		if (TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			settingsPage = new SettingsPageEnglish(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("hin")) {
			settingsPage = new SettingsPageHindi(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("fra")) {
			settingsPage = new SettingsPageFrench(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("kan")) {
			settingsPage = new SettingsPageKannada(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("tam")) {
			settingsPage = new SettingsPageTamil(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("ara")) {
			settingsPage = new SettingsPageArabic(driver);
		}

		assertTrue(settingsPage.isScheduledJobsSettingsTabDisplayed(), "Verify if sceduled job settings tab displayed");
		assertTrue(settingsPage.isGlobalConfigSettingsTabDisplayed(), "Verify if global config settings tab displayed");
		assertTrue(settingsPage.isDeviceSettingsTabDisplayed(), "Verify if device settings tab displayed");

//		Verify Device Settings Tab
		settingsPage.clickOnDeviceSettingsTab();

		assertTrue(settingsPage.isDeviceSettingsPageDisplayed(), "Verify if device settings page displayed");

		assertTrue(settingsPage.isScanNowButtonDisplayed(), "Verify if scan now button displayed");

		settingsPage.clickOnScanNowButton();

		assertTrue(settingsPage.isFaceDeviceCardDisplayed(), "Verify if face device card displayed");

		assertTrue(settingsPage.isIrisDeviceCardDisplayed(), "Verify if iris device card displayed");

		assertTrue(settingsPage.isFingerDeviceCardDisplayed(), "Verify if finger device card displayed");

		settingsPage.validateDeviceCard("io.mosip.mock.sbi.face");
		settingsPage.validateDeviceCard("io.mosip.mock.sbi.iris");
		settingsPage.validateDeviceCard("io.mosip.mock.sbi.finger");

		settingsPage.clickOnGlobalConfigSettingsTab();

		MockSBIPage mockSBIPage = new MockSBIPage(driver);
		mockSBIPage.switchToMockSBI();

		mockSBIPage.setAllToNotReadyAndSave();

		mockSBIPage.switchBackToArcApp();

		settingsPage.clickOnDeviceSettingsTab();

		assertFalse(settingsPage.isScanNowButtonDisplayed(), "Verify if scan now button displayed");

		assertTrue(settingsPage.isNoDevicesFoundDisplayed(), "Verify if no devices found displayed");

		settingsPage.clickOnGlobalConfigSettingsTab();

		mockSBIPage.switchToMockSBI();

		mockSBIPage.setAllToReadyAndSave();

		mockSBIPage.switchBackToArcApp();

		settingsPage.clickOnDeviceSettingsTab();

		assertTrue(settingsPage.isScanNowButtonDisplayed(), "Verify if scan now button displayed");

		// Verify Global config Settings Tab

//		settingsPage.clickOnGlobalConfigSettingsTab();
//
//		assertTrue(settingsPage.isGlobalConfigSettingsHeaderDisplayed(),
//				"Verify if global config settings header Displayed");
//
//		settingsPage.clickOnSubmitButton();
//
//		assertTrue(settingsPage.isSubmitChangesPopupDisplayed(), "Verify if no changes to save Displayed");
//
//		settingsPage.clickOnChangesConfirmButton();
//
//		assertTrue(settingsPage.isNoChangesToSaveDisplayed(), "Verify if no changes to save Displayed");
//
//		settingsPage.clickOnSubmitButton();
//
		// Verify Scheduled Job Settings Tab
//		settingsPage.clickOnScheduledJobsSettingsTab();

	}

}
