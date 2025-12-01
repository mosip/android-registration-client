package regclient.androidTestCases;

import static org.testng.Assert.assertTrue;

import java.io.IOException;

import org.testng.annotations.Test;

import regclient.BaseTest.AndroidBaseTest;
import regclient.api.ArcConfigManager;
import regclient.api.KeycloakUserManager;
import regclient.page.AutoLogoutPage;
import regclient.page.BasePage;
import regclient.page.LoginPage;
import regclient.page.RegistrationTasksPage;
import regclient.pages.arabic.AutoLogoutPageArabic;
import regclient.pages.arabic.LoginPageArabic;
import regclient.pages.arabic.RegistrationTasksPageArabic;
import regclient.pages.english.AutoLogoutPageEnglish;
import regclient.pages.english.LoginPageEnglish;
import regclient.pages.english.RegistrationTasksPageEnglish;
import regclient.pages.french.AutoLogoutPageFrench;
import regclient.pages.french.LoginPageFrench;
import regclient.pages.french.RegistrationTasksPageFrench;
import regclient.pages.hindi.AutoLogoutPageHindi;
import regclient.pages.hindi.LoginPageHindi;
import regclient.pages.hindi.RegistrationTasksPageHindi;
import regclient.pages.kannada.AutoLogoutPageKannada;
import regclient.pages.kannada.LoginPageKannada;
import regclient.pages.kannada.RegistrationTasksPageKannada;
import regclient.pages.tamil.AutoLogoutPageTamil;
import regclient.pages.tamil.LoginPageTamil;
import regclient.pages.tamil.RegistrationTasksPageTamil;
import regclient.utils.TestDataReader;

public class AutoLogout extends AndroidBaseTest {

	@Test(priority = 0, description = "Verify auto-logout when the machine is online")
	public void onlineAutoLogout() throws InterruptedException {
		BasePage.disableAutoRotation();
		LoginPage loginPage = null;
		RegistrationTasksPage registrationTasksPage = null;
		AutoLogoutPage autoLogoutPage = null;

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

		if ("eng".equalsIgnoreCase(language)) {
			autoLogoutPage = new AutoLogoutPageEnglish(driver);
		} else if ("hin".equalsIgnoreCase(language)) {
			autoLogoutPage = new AutoLogoutPageHindi(driver);
		} else if ("fra".equalsIgnoreCase(language)) {
			autoLogoutPage = new AutoLogoutPageFrench(driver);
		} else if ("kan".equalsIgnoreCase(language)) {
			autoLogoutPage = new AutoLogoutPageKannada(driver);
		} else if ("tam".equalsIgnoreCase(language)) {
			autoLogoutPage = new AutoLogoutPageTamil(driver);
		} else if ("ara".equalsIgnoreCase(language)) {
			autoLogoutPage = new AutoLogoutPageArabic(driver);
		} else {
			throw new IllegalStateException("Unsupported language in testdata.json: " + language);
		}

		assertTrue(autoLogoutPage.isAutoLogoutPopupDisplayed(), "Verify if auto-logout popup is displayed");

		autoLogoutPage.clickOnStayLoggedInButton();

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

		if ("eng".equalsIgnoreCase(language)) {
			autoLogoutPage = new AutoLogoutPageEnglish(driver);
		} else if ("hin".equalsIgnoreCase(language)) {
			autoLogoutPage = new AutoLogoutPageHindi(driver);
		} else if ("fra".equalsIgnoreCase(language)) {
			autoLogoutPage = new AutoLogoutPageFrench(driver);
		} else if ("kan".equalsIgnoreCase(language)) {
			autoLogoutPage = new AutoLogoutPageKannada(driver);
		} else if ("tam".equalsIgnoreCase(language)) {
			autoLogoutPage = new AutoLogoutPageTamil(driver);
		} else if ("ara".equalsIgnoreCase(language)) {
			autoLogoutPage = new AutoLogoutPageArabic(driver);
		} else {
			throw new IllegalStateException("Unsupported language in testdata.json: " + language);
		}

		assertTrue(autoLogoutPage.isAutoLogoutPopupDisplayed(), "Verify if auto-logout popup is displayed");

		autoLogoutPage.clickOnStayLogoutButton();

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

		assertTrue(loginPage.isWelcomeMessageInSelectedLanguageDisplayed(),
				"verify if the welcome msg in selected language displayed");

	}

	@Test(priority = 1, description = "Verify auto-logout when the machine is offline")
	public void offlineAutoLogout() throws InterruptedException, IOException {
		try {
			BasePage.disableAutoRotation();
			LoginPage loginPage = null;
			RegistrationTasksPage registrationTasksPage = null;
			AutoLogoutPage autoLogoutPage = null;

			final String language = TestDataReader.readData("language");

			BasePage.disableWifiAndData();

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

			if ("eng".equalsIgnoreCase(language)) {
				autoLogoutPage = new AutoLogoutPageEnglish(driver);
			} else if ("hin".equalsIgnoreCase(language)) {
				autoLogoutPage = new AutoLogoutPageHindi(driver);
			} else if ("fra".equalsIgnoreCase(language)) {
				autoLogoutPage = new AutoLogoutPageFrench(driver);
			} else if ("kan".equalsIgnoreCase(language)) {
				autoLogoutPage = new AutoLogoutPageKannada(driver);
			} else if ("tam".equalsIgnoreCase(language)) {
				autoLogoutPage = new AutoLogoutPageTamil(driver);
			} else if ("ara".equalsIgnoreCase(language)) {
				autoLogoutPage = new AutoLogoutPageArabic(driver);
			} else {
				throw new IllegalStateException("Unsupported language in testdata.json: " + language);
			}

			assertTrue(autoLogoutPage.isAutoLogoutPopupDisplayed(), "Verify if auto-logout popup is displayed");

			autoLogoutPage.clickOnStayLoggedInButton();

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

			if ("eng".equalsIgnoreCase(language)) {
				autoLogoutPage = new AutoLogoutPageEnglish(driver);
			} else if ("hin".equalsIgnoreCase(language)) {
				autoLogoutPage = new AutoLogoutPageHindi(driver);
			} else if ("fra".equalsIgnoreCase(language)) {
				autoLogoutPage = new AutoLogoutPageFrench(driver);
			} else if ("kan".equalsIgnoreCase(language)) {
				autoLogoutPage = new AutoLogoutPageKannada(driver);
			} else if ("tam".equalsIgnoreCase(language)) {
				autoLogoutPage = new AutoLogoutPageTamil(driver);
			} else if ("ara".equalsIgnoreCase(language)) {
				autoLogoutPage = new AutoLogoutPageArabic(driver);
			} else {
				throw new IllegalStateException("Unsupported language in testdata.json: " + language);
			}

			assertTrue(autoLogoutPage.isAutoLogoutPopupDisplayed(), "Verify if auto-logout popup is displayed");

			autoLogoutPage.clickOnStayLogoutButton();

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

			assertTrue(loginPage.isWelcomeMessageInSelectedLanguageDisplayed(),
					"verify if the welcome msg in selected language displayed");
		} finally {
			BasePage.enableWifiAndData();
		}

	}
}
