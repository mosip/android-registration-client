package regclient.androidTestCases;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import regclient.BaseTest.AndroidBaseTest;
import regclient.api.ArcConfigManager;
import regclient.api.KeycloakUserManager;
import regclient.page.BasePage;
import regclient.page.LoginPage;
import regclient.page.MockSBIPage;
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

	@Test(priority = 0, description = "Verify machine settings functionality")
	public void settings() throws InterruptedException {

		LoginPage loginPage = null;
		RegistrationTasksPage registrationTasksPage = null;
		SettingsPage settingsPage = null;

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
		registrationTasksPage.handleLocationPermission();
		assertTrue(registrationTasksPage.isRegistrationTasksPageLoaded(),
				"Verify if registration tasks page is loaded");

		assertTrue(registrationTasksPage.isSettingsButtonDisplayed(), "Verify if settings button displayed");

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

		assertTrue(settingsPage.isDeviceSettingsLabelDisplayedInLoggedLanguage(),
				"Verify if device settings label displayed in logged language");

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
		mockSBIPage.clickOnMockSbiSettingsButton();
		mockSBIPage.setAllToNotReadyAndSave();

		mockSBIPage.switchBackToArcApp();

		settingsPage.clickOnDeviceSettingsTab();

		assertTrue(settingsPage.isScanNowButtonDisplayed(), "Verify if scan now button displayed");

		assertTrue(settingsPage.isNoDevicesFoundDisplayed(), "Verify if no devices found displayed");

		settingsPage.clickOnGlobalConfigSettingsTab();

		mockSBIPage.switchToMockSBI();
		mockSBIPage.clickOnMockSbiSettingsButton();
		mockSBIPage.setAllToReadyAndSave();

		mockSBIPage.switchBackToArcApp();

		settingsPage.clickOnDeviceSettingsTab();

		assertTrue(settingsPage.isScanNowButtonDisplayed(), "Verify if scan now button displayed");

//		 Verify Scheduled Job Settings Tab
		settingsPage.clickOnScheduledJobsSettingsTab();

		assertTrue(settingsPage.isScheduledJobSettingsPageHeaderDisplayed(),
				"Verify if scheduled job settings header Displayed");

		assertTrue(settingsPage.isJobDisplayed("Master Data Sync"), "Verify if Masterdata Sync Card Displayed");

		assertTrue(settingsPage.validateJobCardFields("Master Data Sync"), "Validate job card fields");

		BasePage.waitTime(10);
		settingsPage.clickOnSyncButton("Master Data Sync");

		assertTrue(settingsPage.isMasterDataToastMessageDisplayed(), "Verify if masterdata toast message Displayed");

		// Verify Global config Settings Tab
		settingsPage.clickOnGlobalConfigSettingsTab();
		assertTrue(settingsPage.isGlobalConfigSettingsHeaderDisplayed(),
				"Verify if global config settings header Displayed");
		assertTrue(settingsPage.isKeyLabelDisplayed(), "Verify if key label Displayed");
		assertTrue(settingsPage.isLocalValueLabelDisplayed(), "Verify if local value label Displayed");
		assertTrue(settingsPage.isServerValueLabelDisplayed(), "Verify if server value label Displayed");

		assertTrue(settingsPage.isConfigListPresent(), "Verify config keys are present under Global Config Settings");

		assertTrue(settingsPage.isLocalValueBoxDisplayed(), "Verify local value box is displayed");

		assertTrue(settingsPage.isGlobalConfigSettingsSearchBoxDisplayed(),
				"Verify if global config settings search box is displayed");

	}

}
