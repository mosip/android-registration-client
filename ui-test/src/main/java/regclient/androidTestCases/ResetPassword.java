package regclient.androidTestCases;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import org.testng.annotations.Test;
import regclient.BaseTest.AndroidBaseTest;
import regclient.api.ArcConfigManager;
import regclient.api.KeycloakUserManager;
import regclient.page.BasePage;
import regclient.page.KeycloakPage;
import regclient.page.LoginPage;
import regclient.page.ProfilePage;
import regclient.page.RegistrationTasksPage;
import regclient.pages.arabic.LoginPageArabic;
import regclient.pages.arabic.ProfilePageArabic;
import regclient.pages.arabic.RegistrationTasksPageArabic;
import regclient.pages.english.LoginPageEnglish;
import regclient.pages.english.ProfilePageEnglish;
import regclient.pages.english.RegistrationTasksPageEnglish;
import regclient.pages.french.LoginPageFrench;
import regclient.pages.french.ProfilePageFrench;
import regclient.pages.french.RegistrationTasksPageFrench;
import regclient.pages.hindi.LoginPageHindi;
import regclient.pages.hindi.ProfilePageHindi;
import regclient.pages.hindi.RegistrationTasksPageHindi;
import regclient.pages.kannada.LoginPageKannada;
import regclient.pages.kannada.ProfilePageKannada;
import regclient.pages.kannada.RegistrationTasksPageKannada;
import regclient.pages.tamil.LoginPageTamil;
import regclient.pages.tamil.ProfilePageTamil;
import regclient.pages.tamil.RegistrationTasksPageTamil;
import regclient.utils.TestDataReader;

public class ResetPassword extends AndroidBaseTest {

	@SuppressWarnings("null")
	@Test(priority = 1)
	public void resetPassword() throws IOException {
		BasePage.disableAutoRotation();
		LoginPage loginPage = null;
		RegistrationTasksPage registrationTasksPage = null;
		ProfilePage profilePage = null;
		KeycloakPage keycloakPage = null;

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
		registrationTasksPage.clickSynchronizeDataButton();
		assertTrue(registrationTasksPage.checkLastSyncDate(), "Verify  last sync date and time");

		assertTrue(registrationTasksPage.isProfileTitleDisplayed(), "Verify if profile title display on homepage");
		registrationTasksPage.clickProfileButton();

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
		profilePage.clickOnLogoutButton();
		profilePage.clickOnLogoutButton();
		assertTrue(loginPage.isLoginPageLoaded(), "verify if login page is displayeded in Selected language");
		loginPage.enterUserName(KeycloakUserManager.onboardUser);
		loginPage.clickOnNextButton();

		loginPage.enterPassword(ArcConfigManager.getIAMUsersPassword());
		loginPage.clickOnloginButton();

		loginPage.clickOnSkipToHomeButton();

		assertTrue(registrationTasksPage.isProfileTitleDisplayed(), "Verify if profile title display on homepage");
		registrationTasksPage.clickProfileButton();

		assertTrue(profilePage.isResetPasswordButtonDisplayed(),
				"Verify if reset password button displayed in profile page");
		profilePage.clickOnResetPasswordButton();

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

		assertTrue(keycloakPage.openKeycloakWebView(), "Verify if keycloak login page displayed");

		keycloakPage.enterUserName(KeycloakUserManager.onboardUser);

		keycloakPage.enterPassword(ArcConfigManager.getIAMUsersPassword());

		keycloakPage.clickOnLoginButton();

		assertTrue(keycloakPage.openKeycloakPassword(), "Verify if keycloak login page displayed");

		keycloakPage.clickOnPasswordOption();

		keycloakPage.enterExistPassword(ArcConfigManager.getIAMUsersPassword());

		keycloakPage.enterNewPassword(ArcConfigManager.getIAMUsersPassword() + "121");

		keycloakPage.enterConfirmPassword(ArcConfigManager.getIAMUsersPassword() + "121");

		keycloakPage.clickOnSaveButton();

		assertTrue(keycloakPage.isPasswordUpdatedMessageDisplayed(),
				"Verify if password updated message displayed in keycloak page");

		keycloakPage.clickOnSignoutButton();

		assertTrue(keycloakPage.resumeArcApplication(), "Verify if logout displayed in profile page");

		profilePage.clickOnLogoutButton();

		BasePage.disableWifiAndData();

		// Try to login using new password in offline mode.
		loginPage.enterUserName(KeycloakUserManager.onboardUser);
		loginPage.clickOnNextButton();

		loginPage.enterPassword(ArcConfigManager.getIAMUsersPassword() + "121");
		loginPage.clickOnloginButton();

		assertTrue(loginPage.isPasswordIncorrectErrorMessageDisplayed(),
				"verify if error message should be displayeded as password incorrect!");

		loginPage.clickOnBackButton();

		// Try to login using old password in offline mode.
		loginPage.enterUserName(KeycloakUserManager.onboardUser);
		loginPage.clickOnNextButton();

		loginPage.enterPassword(ArcConfigManager.getIAMUsersPassword());
		loginPage.clickOnloginButton();
		loginPage.clickOnSkipToHomeButton();
		registrationTasksPage.clickProfileButton();
		profilePage.clickOnLogoutButton();

		BasePage.enableWifiAndData();

		// Try to login using new password in online mode.
		loginPage.enterUserName(KeycloakUserManager.onboardUser);
		loginPage.clickOnNextButton();

		loginPage.enterPassword(ArcConfigManager.getIAMUsersPassword() + "121");
		loginPage.clickOnloginButton();
		loginPage.clickOnSkipToHomeButton();
		registrationTasksPage.clickProfileButton();
		profilePage.clickOnLogoutButton();

		BasePage.disableWifiAndData();

		// Try to login using new password in offline mode.
		loginPage.enterUserName(KeycloakUserManager.onboardUser);
		loginPage.clickOnNextButton();

		loginPage.enterPassword(ArcConfigManager.getIAMUsersPassword() + "121");
		loginPage.clickOnloginButton();

		loginPage.clickOnSkipToHomeButton();
		registrationTasksPage.clickProfileButton();
		profilePage.clickOnLogoutButton();

		// Try to login using old password in offline mode.
		loginPage.enterUserName(KeycloakUserManager.onboardUser);
		loginPage.clickOnNextButton();

		loginPage.enterPassword(ArcConfigManager.getIAMUsersPassword());
		loginPage.clickOnloginButton();
		assertTrue(loginPage.isPasswordIncorrectErrorMessageDisplayed(),
				"verify if error message should be displayeded as password incorrect!");
		BasePage.enableWifiAndData();

	}
}
