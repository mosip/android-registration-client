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
		registrationTasksPage.clickSynchronizeDataButton();
		assertTrue(registrationTasksPage.checkLastSyncDate(), "Verify  last sync date and time");

		assertTrue(registrationTasksPage.isProfileTitleDisplayed(), "Verify if profile title display on homepage");
		registrationTasksPage.clickProfileButton();

		if (TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			profilePage = new ProfilePageEnglish(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("hin")) {
			profilePage = new ProfilePageHindi(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("fra")) {
			profilePage = new ProfilePageFrench(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("kan")) {
			profilePage = new ProfilePageKannada(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("tam")) {
			profilePage = new ProfilePageTamil(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("ara")) {
			profilePage = new ProfilePageArabic(driver);
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

		if (TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			profilePage = new ProfilePageEnglish(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("hin")) {
			profilePage = new ProfilePageHindi(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("fra")) {
			profilePage = new ProfilePageFrench(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("kan")) {
			profilePage = new ProfilePageKannada(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("tam")) {
			profilePage = new ProfilePageTamil(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("ara")) {
			profilePage = new ProfilePageArabic(driver);
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
