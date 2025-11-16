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
import regclient.pages.arabic.ProfilePageArabic;
import regclient.pages.arabic.RegistrationTasksPageArabic;
import regclient.pages.arabic.SettingsPageArabic;
import regclient.pages.english.LoginPageEnglish;
import regclient.pages.english.ProfilePageEnglish;
import regclient.pages.english.RegistrationTasksPageEnglish;
import regclient.pages.english.SettingsPageEnglish;
import regclient.pages.french.LoginPageFrench;
import regclient.pages.french.ProfilePageFrench;
import regclient.pages.french.RegistrationTasksPageFrench;
import regclient.pages.french.SettingsPageFrench;
import regclient.pages.hindi.LoginPageHindi;
import regclient.pages.hindi.ProfilePageHindi;
import regclient.pages.hindi.RegistrationTasksPageHindi;
import regclient.pages.hindi.SettingsPageHindi;
import regclient.pages.kannada.LoginPageKannada;
import regclient.pages.kannada.ProfilePageKannada;
import regclient.pages.kannada.RegistrationTasksPageKannada;
import regclient.pages.kannada.SettingsPageKannada;
import regclient.pages.tamil.LoginPageTamil;
import regclient.pages.tamil.ProfilePageTamil;
import regclient.pages.tamil.RegistrationTasksPageTamil;
import regclient.pages.tamil.SettingsPageTamil;
import regclient.utils.TestDataReader;

public class Settings extends AndroidBaseTest {

	@SuppressWarnings("null")
	@Test
	public void settings() throws InterruptedException {

		BasePage.disableAutoRotation();
		FetchUiSpec.getUiSpec("newProcess");
		FetchUiSpec.getBiometricDetails("individualBiometrics");
		LoginPage loginPage = null;
		RegistrationTasksPage registrationTasksPage = null;
		ProfilePage profilePage = null;
		SettingsPage settingsPage = null;

		final String language = TestDataReader.readData("language");
		if ("eng".equalsIgnoreCase(language)) {
			profilePage = new ProfilePageEnglish(driver);
		} else if ("hin".equalsIgnoreCase(language)) {
			profilePage = new ProfilePageHindi(driver);
		} else if ("fra".equalsIgnoreCase(language)) {
			profilePage = new ProfilePageFrench(driver);
		} else if ("kan".equalsIgnoreCase(language)) {
			profilePage = new ProfilePageKannada(driver);
		} else if ("tam".equalsIgnoreCase(language)) {
			profilePage = new ProfilePageTamil(driver);
		} else if ("ara".equalsIgnoreCase(language)) {
			profilePage = new ProfilePageArabic(driver);
		} else {
			throw new IllegalStateException("Unsupported language in testdata.json: " + language);
		}
		loginPage.selectLanguage();

		assertTrue(loginPage.isWelcomeMessageInSelectedLanguageDisplayed(),
				"verify if the welcome msg in selected language displayed");
		loginPage.enterUserName(KeycloakUserManager.moduleSpecificUser);
		loginPage.clickOnNextButton();

		loginPage.enterPassword(ArcConfigManager.getIAMUsersPassword());
		loginPage.clickOnloginButton();

		if ("eng".equalsIgnoreCase(language)) {
			registrationTasksPage = new RegistrationTasksPageEnglish(driver);
		} else if ("hin".equalsIgnoreCase(language)) {
			registrationTasksPage = new RegistrationTasksPageHindi(driver);
		} else if ("fra".equalsIgnoreCase(language)) {
			registrationTasksPage = new RegistrationTasksPageFrench(driver);
		} else if ("kan".equalsIgnoreCase(language)) {
			registrationTasksPage = new RegistrationTasksPageKannada(driver);
		} else if ("tam".equalsIgnoreCase(language)) {
			registrationTasksPage = new RegistrationTasksPageTamil(driver);
		} else if ("ara".equalsIgnoreCase(language)) {
			registrationTasksPage = new RegistrationTasksPageArabic(driver);
		} else {
			throw new IllegalStateException("Unsupported language in testdata.json: " + language);
		}
		assertTrue(registrationTasksPage.isRegistrationTasksPageLoaded(),
				"Verify if registration tasks page is loaded");
		registrationTasksPage.clickOnSettingsButton();

		if ("eng".equalsIgnoreCase(language)) {
		    settingsPage = new SettingsPageEnglish(driver);
		} else if ("hin".equalsIgnoreCase(language)) {
		    settingsPage = new SettingsPageHindi(driver);
		} else if ("fra".equalsIgnoreCase(language)) {
		    settingsPage = new SettingsPageFrench(driver);
		} else if ("kan".equalsIgnoreCase(language)) {
		    settingsPage = new SettingsPageKannada(driver);
		} else if ("tam".equalsIgnoreCase(language)) {
		    settingsPage = new SettingsPageTamil(driver);
		} else if ("ara".equalsIgnoreCase(language)) {
		    settingsPage = new SettingsPageArabic(driver);
		} else {
		    throw new IllegalStateException("Unsupported language in testdata.json: " + language);
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
