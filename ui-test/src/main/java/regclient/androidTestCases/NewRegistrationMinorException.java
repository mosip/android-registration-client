package regclient.androidTestCases;

import static org.testng.Assert.assertFalse;

import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.Test;

import regclient.BaseTest.AndroidBaseTest;
import regclient.api.ArcConfigManager;
import regclient.api.FetchUiSpec;
import regclient.api.KeycloakUserManager;
import regclient.page.AcknowledgementPage;
import regclient.page.ApplicantBiometricsPage;
import regclient.page.AuthenticationPage;
import regclient.page.BasePage;
import regclient.page.BiometricDetailsPage;
import regclient.page.ConsentPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.DocumentUploadPage;
import regclient.page.IntroducerBiometricPage;
import regclient.page.LoginPage;
import regclient.page.ManageApplicationsPage;
import regclient.page.OperationalTaskPage;
import regclient.page.PendingApproval;
import regclient.page.PreviewPage;
import regclient.page.ProfilePage;
import regclient.page.RegistrationTasksPage;
import regclient.page.SelectLanguagePage;
import regclient.pages.arabic.AcknowledgementPageArabic;
import regclient.pages.arabic.ApplicantBiometricsPageArabic;
import regclient.pages.arabic.AuthenticationPageArabic;
import regclient.pages.arabic.BiometricDetailsPageArabic;
import regclient.pages.arabic.ConsentPageArabic;
import regclient.pages.arabic.DemographicDetailsPageArabic;
import regclient.pages.arabic.DocumentuploadPageArabic;
import regclient.pages.arabic.IntroducerBiometricPageArabic;
import regclient.pages.arabic.LoginPageArabic;
import regclient.pages.arabic.ManageApplicationsPageArabic;
import regclient.pages.arabic.OperationalTaskPageArabic;
import regclient.pages.arabic.PendingApprovalArabic;
import regclient.pages.arabic.PreviewPageArabic;
import regclient.pages.arabic.ProfilePageArabic;
import regclient.pages.arabic.RegistrationTasksPageArabic;
import regclient.pages.arabic.SelectLanguagePageArabic;
import regclient.pages.english.AcknowledgementPageEnglish;
import regclient.pages.english.ApplicantBiometricsPageEnglish;
import regclient.pages.english.AuthenticationPageEnglish;
import regclient.pages.english.BiometricDetailsPageEnglish;
import regclient.pages.english.ConsentPageEnglish;
import regclient.pages.english.DemographicDetailsPageEnglish;
import regclient.pages.english.DocumentuploadPageEnglish;
import regclient.pages.english.IntroducerBiometricPageEnglish;
import regclient.pages.english.LoginPageEnglish;
import regclient.pages.english.ManageApplicationsPageEnglish;
import regclient.pages.english.OperationalTaskPageEnglish;
import regclient.pages.english.PendingApprovalEnglish;
import regclient.pages.english.PreviewPageEnglish;
import regclient.pages.english.ProfilePageEnglish;
import regclient.pages.english.RegistrationTasksPageEnglish;
import regclient.pages.english.SelectLanguagePageEnglish;
import regclient.pages.french.AcknowledgementPageFrench;
import regclient.pages.french.ApplicantBiometricsPageFrench;
import regclient.pages.french.AuthenticationPageFrench;
import regclient.pages.french.BiometricDetailsPageFrench;
import regclient.pages.french.ConsentPageFrench;
import regclient.pages.french.DemographicDetailsPageFrench;
import regclient.pages.french.DocumentUploadPageFrench;
import regclient.pages.french.IntroducerBiometricPageFrench;
import regclient.pages.french.LoginPageFrench;
import regclient.pages.french.ManageApplicationsPageFrench;
import regclient.pages.french.OperationalTaskPageFrench;
import regclient.pages.french.PendingApprovalFrench;
import regclient.pages.french.PreviewPageFrench;
import regclient.pages.french.ProfilePageFrench;
import regclient.pages.french.RegistrationTasksPageFrench;
import regclient.pages.french.SelectLanguagePageFrench;
import regclient.pages.hindi.AcknowledgementPageHindi;
import regclient.pages.hindi.ApplicantBiometricsPageHindi;
import regclient.pages.hindi.AuthenticationPageHindi;
import regclient.pages.hindi.BiometricDetailsPageHindi;
import regclient.pages.hindi.ConsentPageHindi;
import regclient.pages.hindi.DemographicDetailsPageHindi;
import regclient.pages.hindi.DocumentUploadPageHindi;
import regclient.pages.hindi.IntroducerBiometricPageHindi;
import regclient.pages.hindi.LoginPageHindi;
import regclient.pages.hindi.ManageApplicationsPageHindi;
import regclient.pages.hindi.OperationalTaskPageHindi;
import regclient.pages.hindi.PendingApprovalHindi;
import regclient.pages.hindi.PreviewPageHindi;
import regclient.pages.hindi.ProfilePageHindi;
import regclient.pages.hindi.RegistrationTasksPageHindi;
import regclient.pages.hindi.SelectLanguagePageHindi;
import regclient.pages.kannada.AcknowledgementPageKannada;
import regclient.pages.kannada.ApplicantBiometricsPageKannada;
import regclient.pages.kannada.AuthenticationPageKannada;
import regclient.pages.kannada.BiometricDetailsPageKannada;
import regclient.pages.kannada.ConsentPageKannada;
import regclient.pages.kannada.DemographicDetailsPageKannada;
import regclient.pages.kannada.DocumentuploadPageKannada;
import regclient.pages.kannada.IntroducerBiometricPageKannada;
import regclient.pages.kannada.LoginPageKannada;
import regclient.pages.kannada.ManageApplicationsPageKannada;
import regclient.pages.kannada.OperationalTaskPageKannada;
import regclient.pages.kannada.PendingApprovalKannada;
import regclient.pages.kannada.PreviewPageKannada;
import regclient.pages.kannada.ProfilePageKannada;
import regclient.pages.kannada.RegistrationTasksPageKannada;
import regclient.pages.kannada.SelectLanguagePageKannada;
import regclient.pages.tamil.AcknowledgementPageTamil;
import regclient.pages.tamil.ApplicantBiometricsPageTamil;
import regclient.pages.tamil.AuthenticationPageTamil;
import regclient.pages.tamil.BiometricDetailsPageTamil;
import regclient.pages.tamil.ConsentPageTamil;
import regclient.pages.tamil.DemographicDetailsPageTamil;
import regclient.pages.tamil.DocumentuploadPageTamil;
import regclient.pages.tamil.IntroducerBiometricPageTamil;
import regclient.pages.tamil.LoginPageTamil;
import regclient.pages.tamil.ManageApplicationsPageTamil;
import regclient.pages.tamil.OperationalTaskPageTamil;
import regclient.pages.tamil.PendingApprovalTamil;
import regclient.pages.tamil.PreviewPageTamil;
import regclient.pages.tamil.ProfilePageTamil;
import regclient.pages.tamil.RegistrationTasksPageTamil;
import regclient.pages.tamil.SelectLanguagePageTamil;
import regclient.utils.TestDataReader;

public class NewRegistrationMinorException extends AndroidBaseTest {

	@Test
	public void newRegistrationMinorException() throws InterruptedException {
		FetchUiSpec.getUiSpec("newProcess");
		FetchUiSpec.getBiometricDetails("individualBiometrics");
		List<String> screenOrder = FetchUiSpec.getAllScreenOrder();
		BasePage.disableAutoRotation();
		LoginPage loginPage = null;
		RegistrationTasksPage registrationTasksPage = null;
		SelectLanguagePage selectLanguagePage = null;
		ConsentPage consentPage = null;
		DemographicDetailsPage demographicPage = null;
		DocumentUploadPage documentuploadPage = null;
		BiometricDetailsPage biometricDetailsPage = null;
		ApplicantBiometricsPage applicantBiometricsPage = null;
		PreviewPage previewPage = null;
		AuthenticationPage authenticationPage = null;
		AcknowledgementPage acknowledgementPage = null;
		IntroducerBiometricPage introducerBiometricPage = null;
		OperationalTaskPage operationalTaskPage = null;
		ManageApplicationsPage manageApplicationsPage = null;
		ProfilePage profilePage = null;
		PendingApproval pendingApproval = null;

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
		registrationTasksPage.clickOnNewRegistrationButton();
		if (TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			selectLanguagePage = new SelectLanguagePageEnglish(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("hin")) {
			selectLanguagePage = new SelectLanguagePageHindi(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("fra")) {
			selectLanguagePage = new SelectLanguagePageFrench(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("kan")) {
			selectLanguagePage = new SelectLanguagePageKannada(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("tam")) {
			selectLanguagePage = new SelectLanguagePageTamil(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("ara")) {
			selectLanguagePage = new SelectLanguagePageArabic(driver);
		}
		assertTrue(selectLanguagePage.isSelectLanguagePageLoaded(), "Verify if select language page  is loaded");
		selectLanguagePage.clickOnSubmitButtonWithoutSelectingLanguage();

		assertTrue(selectLanguagePage.isSelectLanguagePageLoaded(),
				"Verify if user should not be allow to navigate to next screen.");
		selectLanguagePage.selectSecondLanguage();

		assertTrue(selectLanguagePage.isNotificationLanguageEnglishDisplayed(),
				"verify if the notification language display in english");
		selectLanguagePage.selectNotificationlanguage(TestDataReader.readData("notificationLanguage"));

		assertTrue(selectLanguagePage.isSubmitButtonEnabled(), "verify if the submit  button enabled");
		selectLanguagePage.clickOnSubmitButton();
		for (String screen : screenOrder) {
			if (screen.equals("consentdet") || screen.equals("consent")) {
				if (TestDataReader.readData("language").equalsIgnoreCase("eng")) {
					consentPage = new ConsentPageEnglish(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("hin")) {
					consentPage = new ConsentPageHindi(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("fra")) {
					consentPage = new ConsentPageFrench(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("kan")) {
					consentPage = new ConsentPageKannada(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("tam")) {
					consentPage = new ConsentPageTamil(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("ara")) {
					consentPage = new ConsentPageArabic(driver);
				}
				consentPage.clickOnInformedButton();
			} else if (screen.equals("DemographicDetails")) {
				if (TestDataReader.readData("language").equalsIgnoreCase("eng")) {
					demographicPage = new DemographicDetailsPageEnglish(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("hin")) {
					demographicPage = new DemographicDetailsPageHindi(driver);

				} else if (TestDataReader.readData("language").equalsIgnoreCase("fra")) {
					demographicPage = new DemographicDetailsPageFrench(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("kan")) {
					demographicPage = new DemographicDetailsPageKannada(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("tam")) {
					demographicPage = new DemographicDetailsPageTamil(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("ara")) {
					demographicPage = new DemographicDetailsPageArabic(driver);
				}
				assertTrue(demographicPage.isPageDisplayed("DemographicDetails"),
						"Verify if demographic details page is displayed");
				demographicPage.fillDemographicDetailsPage("minor");

				demographicPage.clickOnContinueButton();
			} else if (screen.equals("Documents")) {
				if (TestDataReader.readData("language").equalsIgnoreCase("eng")) {
					documentuploadPage = new DocumentuploadPageEnglish(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("hin")) {
					documentuploadPage = new DocumentUploadPageHindi(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("fra")) {
					documentuploadPage = new DocumentUploadPageFrench(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("kan")) {
					documentuploadPage = new DocumentuploadPageKannada(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("tam")) {
					documentuploadPage = new DocumentuploadPageTamil(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("ara")) {
					documentuploadPage = new DocumentuploadPageArabic(driver);
				}
				assertTrue(documentuploadPage.isDoccumentUploadPageDisplayed(),
						"Verify if doccumentupload page is displayed");
				documentuploadPage.uploadDoccuments("minor", "withoutReferenceNumber");

				documentuploadPage.clickOnContinueButton();
			} else if (screen.equals("BiometricDetails")) {
				if (TestDataReader.readData("language").equalsIgnoreCase("eng")) {
					biometricDetailsPage = new BiometricDetailsPageEnglish(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("hin")) {
					biometricDetailsPage = new BiometricDetailsPageHindi(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("fra")) {
					biometricDetailsPage = new BiometricDetailsPageFrench(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("kan")) {
					biometricDetailsPage = new BiometricDetailsPageKannada(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("tam")) {
					biometricDetailsPage = new BiometricDetailsPageTamil(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("ara")) {
					biometricDetailsPage = new BiometricDetailsPageArabic(driver);
				}
				// Iris
				assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),
						"Verify if biometric details page is displayed");
				if (TestDataReader.readData("language").equalsIgnoreCase("eng")) {
					applicantBiometricsPage = new ApplicantBiometricsPageEnglish(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("hin")) {
					applicantBiometricsPage = new ApplicantBiometricsPageHindi(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("fra")) {
					applicantBiometricsPage = new ApplicantBiometricsPageFrench(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("kan")) {
					applicantBiometricsPage = new ApplicantBiometricsPageKannada(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("tam")) {
					applicantBiometricsPage = new ApplicantBiometricsPageTamil(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("ara")) {
					applicantBiometricsPage = new ApplicantBiometricsPageArabic(driver);
				}
				if (FetchUiSpec.eye.equals("yes")) {
					biometricDetailsPage.clickOnIrisScan();
					assertTrue(applicantBiometricsPage.isApplicantBiometricsPageDisplyed(),
							"Verify if applicant biometric page is displayed");
					applicantBiometricsPage.clickOnMarkExceptionButton();

					assertTrue(applicantBiometricsPage.isExceptionTypeTitleDisplyed(),
							"Verify if applicant biometric mark exception is displayed");
					applicantBiometricsPage.markOneEyeException();
					applicantBiometricsPage.clickOnIrisScanButton();
					applicantBiometricsPage.clickOnScanButton();

					assertTrue(applicantBiometricsPage.isIrisScan(), "Verify if iris scan 1st attempt");
					applicantBiometricsPage.closeScanCapturePopUp();
					applicantBiometricsPage.clickOnBackButton();
				}
				// righthand
				if (FetchUiSpec.rightHand.equals("yes")) {
					biometricDetailsPage.clickOnRightHandScanIcon();
					assertTrue(applicantBiometricsPage.isRightHandScanTitleDisplyed(),
							"Verify if right hand scan is displayed");
					applicantBiometricsPage.clickOnMarkExceptionButton();

					assertTrue(applicantBiometricsPage.isExceptionTypeTitleDisplyed(),
							"Verify if applicant biometric mark exception is displayed");
					applicantBiometricsPage.clickOnZoomButton();

					assertTrue(applicantBiometricsPage.isRightHandScanTitleDisplyed(),
							"Verify if applicant right hand scan is displayed");
					applicantBiometricsPage.markFourFingureExceptionThenRemoveOne();
					applicantBiometricsPage.clickOnClosePopUp();

					assertTrue(applicantBiometricsPage.isExceptionTypeTitleDisplyed(),
							"Verify if applicant biometric mark exception is displayed");
					applicantBiometricsPage.clickOnRightHandScanTitle();

					assertTrue(applicantBiometricsPage.isRightHandScanTitleDisplyed(),
							"Verify if right hand scan is displayed");
					applicantBiometricsPage.clickOnScanButton();

					assertTrue(applicantBiometricsPage.isRightHandScan(), "Verify if right hand scan 1st attempt");
					applicantBiometricsPage.closeScanCapturePopUp();
					applicantBiometricsPage.clickOnBackButton();
				}
				// lefthand
				if (FetchUiSpec.leftHand.equals("yes")) {
					biometricDetailsPage.clickOnLeftHandScanIcon();
					assertTrue(applicantBiometricsPage.isLeftHandScanTitleDisplyed(),
							"Verify if applicant left hand scan title is displayed");
					applicantBiometricsPage.clickOnMarkExceptionButton();

					assertTrue(applicantBiometricsPage.isExceptionTypeTitleDisplyed(),
							"Verify if applicant biometric mark exception is displayed");
					applicantBiometricsPage.clickOnZoomButton();

					assertTrue(applicantBiometricsPage.isLeftHandScanTitleDisplyed(),
							"Verify if applicant right hand scan is displayed");
					applicantBiometricsPage.markOneFingureException();
					applicantBiometricsPage.clickOnClosePopUp();

					assertTrue(applicantBiometricsPage.isExceptionTypeTitleDisplyed(),
							"Verify if applicant biometric mark exception is displayed");
					applicantBiometricsPage.clickOnleftHandScanTitle();

					assertTrue(applicantBiometricsPage.isLeftHandScanTitleDisplyed(),
							"Verify if applicant left hand scan title is displayed");
					applicantBiometricsPage.clickOnScanButton();

					assertTrue(applicantBiometricsPage.isLeftHandScan(), "Verify if Left hand scan 1st attempt");
					applicantBiometricsPage.closeScanCapturePopUp();
					applicantBiometricsPage.clickOnBackButton();
				}
				// thumb
				if (FetchUiSpec.thumb.equals("yes")) {
					biometricDetailsPage.clickOnThumbsScanIcon();
					assertTrue(applicantBiometricsPage.isThumbsScanTitleDisplyed(),
							"Verify if thumbs scan page is displayed");
					applicantBiometricsPage.clickOnMarkExceptionButton();

					assertTrue(applicantBiometricsPage.isExceptionTypeTitleDisplyed(),
							"Verify if applicant biometric mark exception is displayed");
					applicantBiometricsPage.markOneFingureException();
					applicantBiometricsPage.clickOnThumbsScanTitle();

					assertTrue(applicantBiometricsPage.isThumbsScanTitleDisplyed(),
							"Verify if thumbs scan page is displayed");
					applicantBiometricsPage.clickOnScanButton();

					assertTrue(applicantBiometricsPage.isThumbsScan(), "Verify if thumbs scan 1st attempt");
					applicantBiometricsPage.closeScanCapturePopUp();
					applicantBiometricsPage.clickOnBackButton();
				}
				// face
				if (FetchUiSpec.face.equals("yes")) {
					assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),
							"Verify if biometric details page is displayed");
					biometricDetailsPage.clickOnFaceScanIcon();

					assertTrue(applicantBiometricsPage.isApplicantBiometricsPageDisplyed(),
							"Verify if applicant biometric page is displayed");
					applicantBiometricsPage.clickOnScanButton();

					assertTrue(applicantBiometricsPage.isFaceScan(), "Verify if face scan 1st attempt");
					applicantBiometricsPage.closeScanCapturePopUp();
					applicantBiometricsPage.clickOnNextButton();
				}

				// Exception
				assertTrue(applicantBiometricsPage.isExceptionScanTitleDisplyed(),
						"Verify if exception scan page is displayed");
				applicantBiometricsPage.clickOnScanButton();

				assertTrue(applicantBiometricsPage.isExceptionScan(), "Verify if exception scan 1st attempt");
				applicantBiometricsPage.closeScanCapturePopUp();
				biometricDetailsPage = applicantBiometricsPage.clickOnNextButton();

				assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),
						"Verify if biometric details page is displayed");

				if (TestDataReader.readData("language").equalsIgnoreCase("eng")) {
					introducerBiometricPage = new IntroducerBiometricPageEnglish(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("hin")) {
					introducerBiometricPage = new IntroducerBiometricPageHindi(driver);

				} else if (TestDataReader.readData("language").equalsIgnoreCase("fra")) {
					introducerBiometricPage = new IntroducerBiometricPageFrench(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("kan")) {
					introducerBiometricPage = new IntroducerBiometricPageKannada(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("tam")) {
					introducerBiometricPage = new IntroducerBiometricPageTamil(driver);
				} else if (TestDataReader.readData("language").equalsIgnoreCase("ara")) {
					introducerBiometricPage = new IntroducerBiometricPageArabic(driver);
				}
				FetchUiSpec.getBiometricDetails("introducerBiometrics");
				if (FetchUiSpec.eye.equals("yes")) {
					biometricDetailsPage.clickOnIntroducerIrisScan();
					assertTrue(introducerBiometricPage.isIntroducerBiometricsPageDisplyed(),
							"Verify if introducer biometric page is displayed");

					introducerBiometricPage.clickOnMarkExceptionButton();
					assertTrue(introducerBiometricPage.isExceptionTypeTitleDisplyed(),
							"Verify if applicant biometric mark exception is displayed");
					introducerBiometricPage.markOneEyeException();
					introducerBiometricPage.clickOnIrisScanButton();

					introducerBiometricPage.clickOnScanButton();

					assertTrue(introducerBiometricPage.isIrisScan(), "Verify if iris scan 1st attempt");
					introducerBiometricPage.closeScanCapturePopUp();
					introducerBiometricPage.clickOnNextButton();
				}
				// righthand
				if (FetchUiSpec.rightHand.equals("yes")) {
					if (!FetchUiSpec.eye.equals("yes")) {
						biometricDetailsPage.clickOnIntroducerRightHandScan();
					}
					assertTrue(introducerBiometricPage.isIntroducerBiometricsPageDisplyed(),
							"Verify if introducer biometric page is displayed");

					assertTrue(introducerBiometricPage.isRightHandScanTitleDisplyed(),
							"Verify if right hand scan is displayed");
					introducerBiometricPage.clickOnScanButton();

					assertTrue(introducerBiometricPage.isRightHandScan(), "Verify if right hand scan 1st attempt");
					introducerBiometricPage.closeScanCapturePopUp();
					introducerBiometricPage.clickOnNextButton();
				}
				// lefthand
				if (FetchUiSpec.leftHand.equals("yes")) {
					assertTrue(introducerBiometricPage.isIntroducerBiometricsPageDisplyed(),
							"Verify if introducer biometric page is displayed");

					assertTrue(introducerBiometricPage.isLeftHandScanTitleDisplyed(),
							"Verify if applicant left hand scan title is displayed");
					introducerBiometricPage.clickOnScanButton();

					assertTrue(introducerBiometricPage.isLeftHandScan(), "Verify if Left hand scan 1st attempt");
					introducerBiometricPage.closeScanCapturePopUp();
					introducerBiometricPage.clickOnNextButton();
				}
				// thumb
				if (FetchUiSpec.thumb.equals("yes")) {
					assertTrue(introducerBiometricPage.isIntroducerBiometricsPageDisplyed(),
							"Verify if introducer biometric page is displayed");

					assertTrue(introducerBiometricPage.isThumbsScanTitleDisplyed(),
							"Verify if thumbs scan page is displayed");
					introducerBiometricPage.clickOnScanButton();

					assertTrue(introducerBiometricPage.isThumbsScan(), "Verify if thumbs scan 1st attempt");
					introducerBiometricPage.closeScanCapturePopUp();
					introducerBiometricPage.clickOnNextButton();
				}
				// face
				if (FetchUiSpec.face.equals("yes")) {
					assertTrue(introducerBiometricPage.isIntroducerBiometricsPageDisplyed(),
							"Verify if introducer biometric page is displayed");

					assertTrue(introducerBiometricPage.isFaceScanTitleDisplyed(),
							"Verify if face scan page is displayed");
					introducerBiometricPage.clickOnScanButton();

					assertTrue(introducerBiometricPage.isFaceScan(), "Verify if face scan 1st attempt");
					introducerBiometricPage.closeScanCapturePopUp();
					introducerBiometricPage.clickOnNextButton();
				}
				// Exception
				assertTrue(introducerBiometricPage.isExceptionScanTitleDisplyed(),
						"Verify if exception scan page is displayed");
				introducerBiometricPage.clickOnScanButton();

				assertTrue(introducerBiometricPage.isExceptionScan(), "Verify if exception scan 1st attempt");
				introducerBiometricPage.closeScanCapturePopUp();
				introducerBiometricPage.clickOnNextButton();

				assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),
						"Verify if biometric details page is displayed");
				biometricDetailsPage.clickOnContinueButton();
			}
		}
		if (TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			previewPage = new PreviewPageEnglish(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("hin")) {
			previewPage = new PreviewPageHindi(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("fra")) {
			previewPage = new PreviewPageFrench(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("kan")) {
			previewPage = new PreviewPageKannada(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("tam")) {
			previewPage = new PreviewPageTamil(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("ara")) {
			previewPage = new PreviewPageArabic(driver);
		}

		assertTrue(previewPage.isNewRegistrationTitleDisplayed(), "Verify if new Registration title is displayed");
		assertTrue(previewPage.isApplicationIDPreviewPagePageDisplayed(),
				"Verify if application ID In PreviewPage is displayed");
		assertTrue(previewPage.isDemographicInformationInPreviewPageDisplayed(),
				"Verify if Demographic Information In PreviewPage is displayed");
		assertTrue(previewPage.isDocumentsInformationInPreviewPageDisplayed(),
				"Verify if Documents Information In PreviewPage is displayed");
//		assertTrue(previewPage.isBiometricsInformationInPreviewPagePageDisplayed(),"Verify if Biometrics Information In PreviewPage is displayed");
		String Aid = previewPage.getAID();
		previewPage.clickOnContinueButton();
		if (TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			authenticationPage = new AuthenticationPageEnglish(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("hin")) {
			authenticationPage = new AuthenticationPageHindi(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("fra")) {
			authenticationPage = new AuthenticationPageFrench(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("kan")) {
			authenticationPage = new AuthenticationPageKannada(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("tam")) {
			authenticationPage = new AuthenticationPageTamil(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("ara")) {
			authenticationPage = new AuthenticationPageArabic(driver);
		}
		assertTrue(authenticationPage.isAuthenticationPageDisplayed(),
				"Verify if authentication details page is displayed");
		authenticationPage.enterUserName(KeycloakUserManager.moduleSpecificUser);
		authenticationPage.enterPassword(ArcConfigManager.getIAMUsersPassword());
		authenticationPage.clickOnAuthenticatenButton();
		if (TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			acknowledgementPage = new AcknowledgementPageEnglish(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("hin")) {
			acknowledgementPage = new AcknowledgementPageHindi(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("fra")) {
			acknowledgementPage = new AcknowledgementPageFrench(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("kan")) {
			acknowledgementPage = new AcknowledgementPageKannada(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("tam")) {
			acknowledgementPage = new AcknowledgementPageTamil(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("ara")) {
			acknowledgementPage = new AcknowledgementPageArabic(driver);
		}
		assertTrue(acknowledgementPage.isAcknowledgementPageDisplayed(),
				"Verify if acknowledgement details page is displayed");
		acknowledgementPage.clickOnGoToHomeButton();

		assertTrue(registrationTasksPage.isRegistrationTasksPageLoaded(),
				"Verify if registration tasks page is loaded");
		registrationTasksPage.clickOnOperationalTasksTitle();
		if (TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			operationalTaskPage = new OperationalTaskPageEnglish(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("hin")) {
			operationalTaskPage = new OperationalTaskPageHindi(driver);

		} else if (TestDataReader.readData("language").equalsIgnoreCase("fra")) {
			operationalTaskPage = new OperationalTaskPageFrench(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("kan")) {
			operationalTaskPage = new OperationalTaskPageKannada(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("tam")) {
			operationalTaskPage = new OperationalTaskPageTamil(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("ara")) {
			operationalTaskPage = new OperationalTaskPageArabic(driver);
		}
		assertTrue(operationalTaskPage.isOperationalTaskPageLoaded(), "Verify if operational Task Page is loaded");
		operationalTaskPage.clickPendingApprovalTitle();

		if (TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			pendingApproval = new PendingApprovalEnglish(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("hin")) {
			pendingApproval = new PendingApprovalHindi(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("fra")) {
			pendingApproval = new PendingApprovalFrench(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("kan")) {
			pendingApproval = new PendingApprovalKannada(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("tam")) {
			pendingApproval = new PendingApprovalTamil(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("ara")) {
			pendingApproval = new PendingApprovalArabic(driver);
		}
		assertTrue(pendingApproval.isPendingApprovalTitleDisplayed(), "Verify if pending approval page  displayed");
		pendingApproval.clickOnAID(Aid);

		assertTrue(pendingApproval.isApprovalButtonDisplayed(), "Verify if  approval button  displayed");
		pendingApproval.clickOnApproveButton();
		pendingApproval.clickOnClosePopUpButton();

		pendingApproval.clickOnCheckBox();

		assertTrue(pendingApproval.isAuthenticateButtonEnabled(),
				"Verify if authenticate button is enable after selecting packet");

		boolean isPageDisplayed = false;
		for (int i = 0; i < 3; i++) {
			pendingApproval.clickOnAuthenticateButton();
			Thread.sleep(2000);
			if (pendingApproval.isSupervisorAuthenticationTitleDisplayed()) {
				isPageDisplayed = true;
				break;
			}
		}

		assertTrue(pendingApproval.isSupervisorAuthenticationTitleDisplayed(),
				"Verify if Supervisor Authentication page displayed");
		pendingApproval.enterUserName(KeycloakUserManager.moduleSpecificUser);
		pendingApproval.enterPassword(ArcConfigManager.getIAMUsersPassword());
		pendingApproval.clickOnSubmitButton();
		pendingApproval.clickOnBackButton();
		assertTrue(operationalTaskPage.isApplicationUploadTitleDisplayed(),
				"Verify if application upload tite displayed");

		operationalTaskPage.clickApplicationUploadTitle();
		if (TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			manageApplicationsPage = new ManageApplicationsPageEnglish(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("hin")) {
			manageApplicationsPage = new ManageApplicationsPageHindi(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("fra")) {
			manageApplicationsPage = new ManageApplicationsPageFrench(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("kan")) {
			manageApplicationsPage = new ManageApplicationsPageKannada(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("tam")) {
			manageApplicationsPage = new ManageApplicationsPageTamil(driver);
		} else if (TestDataReader.readData("language").equalsIgnoreCase("ara")) {
			manageApplicationsPage = new ManageApplicationsPageArabic(driver);
		}
		assertTrue(manageApplicationsPage.isManageApplicationPageDisplayed(),
				"Verify if manage Applications Page displayed");
		manageApplicationsPage.enterAID(Aid);

		assertTrue(manageApplicationsPage.isSearchAIDDisplayed(Aid), "Verify if  Search Aid should  displayed");
		assertTrue(manageApplicationsPage.isPacketApproved(Aid),
				"Verify if  packet is approved after approve in pending approval");

		manageApplicationsPage.clickOnSearchCheckBox();
		manageApplicationsPage.clickOnUploadButton();

		manageApplicationsPage.clickClientStatusDropdown();

		assertTrue(manageApplicationsPage.isApprovedDropdownOptionDisplayed(),
				"Verify if Approved Dropdown Option Displayed displayed");
		assertTrue(manageApplicationsPage.isRejectedDropdownOptionDisplayed(),
				"Verify if Rejected Dropdown Option Displayed");
		assertTrue(manageApplicationsPage.isExportedsDropdownOptionDisplayed(),
				"Verify if Exported Dropdown Option Displayed");

		manageApplicationsPage.clickDismissButton();
		manageApplicationsPage.clickOnBackButton();
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

	}
}
