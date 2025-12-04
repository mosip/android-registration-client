package regclient.androidTestCases;

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
import regclient.pages.arabic.DocumentUploadPageArabic;
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
import regclient.pages.english.DocumentUploadPageEnglish;
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
import regclient.pages.kannada.DocumentUploadPageKannada;
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
import regclient.pages.tamil.DocumentUploadPageTamil;
import regclient.pages.tamil.LoginPageTamil;
import regclient.pages.tamil.ManageApplicationsPageTamil;
import regclient.pages.tamil.OperationalTaskPageTamil;
import regclient.pages.tamil.PendingApprovalTamil;
import regclient.pages.tamil.PreviewPageTamil;
import regclient.pages.tamil.ProfilePageTamil;
import regclient.pages.tamil.RegistrationTasksPageTamil;
import regclient.pages.tamil.SelectLanguagePageTamil;
import regclient.utils.TestDataReader;

public class LostUin extends AndroidBaseTest {

	@Test(priority = 0, description = "Verify lost UIN")
	public void lostUinAdult() {
		BasePage.disableAutoRotation();
		FetchUiSpec.getUiSpec("newProcess");
		FetchUiSpec.getBiometricDetails("individualBiometrics");
		List<String> screenOrder = FetchUiSpec.getAllScreenOrder();
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
		OperationalTaskPage operationalTaskPage = null;
		PendingApproval pendingApproval = null;
		ManageApplicationsPage manageApplicationsPage = null;
		ProfilePage profilePage = null;

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
		registrationTasksPage.clickOnLostUinButton();

		if ("eng".equalsIgnoreCase(language)) {
			selectLanguagePage = new SelectLanguagePageEnglish(driver);
		} else if ("hin".equalsIgnoreCase(language)) {
			selectLanguagePage = new SelectLanguagePageHindi(driver);
		} else if ("fra".equalsIgnoreCase(language)) {
			selectLanguagePage = new SelectLanguagePageFrench(driver);
		} else if ("kan".equalsIgnoreCase(language)) {
			selectLanguagePage = new SelectLanguagePageKannada(driver);
		} else if ("tam".equalsIgnoreCase(language)) {
			selectLanguagePage = new SelectLanguagePageTamil(driver);
		} else if ("ara".equalsIgnoreCase(language)) {
			selectLanguagePage = new SelectLanguagePageArabic(driver);
		} else {
			throw new IllegalStateException("Unsupported language in testdata.json: " + language);
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
				if ("eng".equalsIgnoreCase(language)) {
					consentPage = new ConsentPageEnglish(driver);
				} else if ("hin".equalsIgnoreCase(language)) {
					consentPage = new ConsentPageHindi(driver);
				} else if ("fra".equalsIgnoreCase(language)) {
					consentPage = new ConsentPageFrench(driver);
				} else if ("kan".equalsIgnoreCase(language)) {
					consentPage = new ConsentPageKannada(driver);
				} else if ("tam".equalsIgnoreCase(language)) {
					consentPage = new ConsentPageTamil(driver);
				} else if ("ara".equalsIgnoreCase(language)) {
					consentPage = new ConsentPageArabic(driver);
				} else {
					throw new IllegalStateException("Unsupported language in testdata.json: " + language);
				}
				assertTrue(consentPage.isPageDisplayed("consentdet"), "Verify if Consent page is loaded");
//				assertTrue(consentPage.isCheckBoxReadable(), "Verify if the check box in readable");

				assertTrue(consentPage.isInformedButtonEnabled(), "Verify if informed  button enabled");
				consentPage.clickOnInformedButton();

			} else if (screen.equals("DemographicDetails")) {

				if ("eng".equalsIgnoreCase(language)) {
					demographicPage = new DemographicDetailsPageEnglish(driver);
				} else if ("hin".equalsIgnoreCase(language)) {
					demographicPage = new DemographicDetailsPageHindi(driver);
				} else if ("fra".equalsIgnoreCase(language)) {
					demographicPage = new DemographicDetailsPageFrench(driver);
				} else if ("kan".equalsIgnoreCase(language)) {
					demographicPage = new DemographicDetailsPageKannada(driver);
				} else if ("tam".equalsIgnoreCase(language)) {
					demographicPage = new DemographicDetailsPageTamil(driver);
				} else if ("ara".equalsIgnoreCase(language)) {
					demographicPage = new DemographicDetailsPageArabic(driver);
				} else {
					throw new IllegalStateException("Unsupported language in testdata.json: " + language);
				}
				assertTrue(demographicPage.isPageDisplayed("DemographicDetails"),
						"Verify if demographic details page is displayed");

				demographicPage.clickOnContinueButton();
			} else if (screen.equals("Documents")) {
				if ("eng".equalsIgnoreCase(language)) {
					documentuploadPage = new DocumentUploadPageEnglish(driver);
				} else if ("hin".equalsIgnoreCase(language)) {
					documentuploadPage = new DocumentUploadPageHindi(driver);
				} else if ("fra".equalsIgnoreCase(language)) {
					documentuploadPage = new DocumentUploadPageFrench(driver);
				} else if ("kan".equalsIgnoreCase(language)) {
					documentuploadPage = new DocumentUploadPageKannada(driver);
				} else if ("tam".equalsIgnoreCase(language)) {
					documentuploadPage = new DocumentUploadPageTamil(driver);
				} else if ("ara".equalsIgnoreCase(language)) {
					documentuploadPage = new DocumentUploadPageArabic(driver);
				} else {
					throw new IllegalStateException("Unsupported language in testdata.json: " + language);
				}
				assertTrue(documentuploadPage.isDoccumentUploadPageDisplayed(),
						"Verify if doccumentupload page is displayed");
				documentuploadPage.clickOnContinueButton();

			} else if (screen.equals("BiometricDetails")) {
				if ("eng".equalsIgnoreCase(language)) {
					biometricDetailsPage = new BiometricDetailsPageEnglish(driver);
				} else if ("hin".equalsIgnoreCase(language)) {
					biometricDetailsPage = new BiometricDetailsPageHindi(driver);
				} else if ("fra".equalsIgnoreCase(language)) {
					biometricDetailsPage = new BiometricDetailsPageFrench(driver);
				} else if ("kan".equalsIgnoreCase(language)) {
					biometricDetailsPage = new BiometricDetailsPageKannada(driver);
				} else if ("tam".equalsIgnoreCase(language)) {
					biometricDetailsPage = new BiometricDetailsPageTamil(driver);
				} else if ("ara".equalsIgnoreCase(language)) {
					biometricDetailsPage = new BiometricDetailsPageArabic(driver);
				} else {
					throw new IllegalStateException("Unsupported language in testdata.json: " + language);
				}
				if ("eng".equalsIgnoreCase(language)) {
					applicantBiometricsPage = new ApplicantBiometricsPageEnglish(driver);
				} else if ("hin".equalsIgnoreCase(language)) {
					applicantBiometricsPage = new ApplicantBiometricsPageHindi(driver);
				} else if ("fra".equalsIgnoreCase(language)) {
					applicantBiometricsPage = new ApplicantBiometricsPageFrench(driver);
				} else if ("kan".equalsIgnoreCase(language)) {
					applicantBiometricsPage = new ApplicantBiometricsPageKannada(driver);
				} else if ("tam".equalsIgnoreCase(language)) {
					applicantBiometricsPage = new ApplicantBiometricsPageTamil(driver);
				} else if ("ara".equalsIgnoreCase(language)) {
					applicantBiometricsPage = new ApplicantBiometricsPageArabic(driver);
				} else {
					throw new IllegalStateException("Unsupported language in testdata.json: " + language);
				}
				assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),
						"Verify if biometric details page is displayed");
				biometricDetailsPage.clickOnContinueButton();
				assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),
						"Verify if biometric details page is displayed");

				if (FetchUiSpec.eye.equals("yes")) {
					biometricDetailsPage.clickOnIrisScan();

					assertTrue(applicantBiometricsPage.isApplicantBiometricsPageDisplayed(),
							"Verify if applicant biometric page is displayed");
					applicantBiometricsPage.clickOnScanButton();

					assertTrue(applicantBiometricsPage.isIrisScan(), "Verify if iris scan 1st attempt");
					applicantBiometricsPage.closeScanCapturePopUp();

					applicantBiometricsPage.clickOnScanButton();
					assertTrue(applicantBiometricsPage.isIrisScan(), "Verify if iris scan 2nd attempt");
					applicantBiometricsPage.closeScanCapturePopUp();

					applicantBiometricsPage.clickOnScanButton();
					assertTrue(applicantBiometricsPage.isIrisScan(), "Verify if iris scan 3rd attempt");
					applicantBiometricsPage.closeScanCapturePopUp();

					biometricDetailsPage = applicantBiometricsPage.clickOnBackButton();
				}
				// righthand
				if (FetchUiSpec.rightHand.equals("yes")) {
					assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),
							"Verify if biometric details page is displayed");
					applicantBiometricsPage = biometricDetailsPage.clickOnRightHandScanIcon();

					assertTrue(applicantBiometricsPage.isApplicantBiometricsPageDisplayed(),
							"Verify if applicant biometric page is displayed");
					applicantBiometricsPage.clickOnScanButton();

					assertTrue(applicantBiometricsPage.isRightHandScan(), "Verify if right hand scan 1st attempt");
					applicantBiometricsPage.closeScanCapturePopUp();
					biometricDetailsPage = applicantBiometricsPage.clickOnBackButton();
				}
				// lefthand
				if (FetchUiSpec.leftHand.equals("yes")) {
					assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),
							"Verify if biometric details page is displayed");
					applicantBiometricsPage = biometricDetailsPage.clickOnLeftHandScanIcon();

					assertTrue(applicantBiometricsPage.isApplicantBiometricsPageDisplayed(),
							"Verify if applicant biometric page is displayed");
					applicantBiometricsPage.clickOnScanButton();

					assertTrue(applicantBiometricsPage.isLeftHandScan(), "Verify if Left hand scan 1st attempt");
					applicantBiometricsPage.closeScanCapturePopUp();
					biometricDetailsPage = applicantBiometricsPage.clickOnBackButton();
				}
				// thumb
				if (FetchUiSpec.thumb.equals("yes")) {
					assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),
							"Verify if biometric details page is displayed");
					applicantBiometricsPage = biometricDetailsPage.clickOnThumbsScanIcon();

					assertTrue(applicantBiometricsPage.isApplicantBiometricsPageDisplayed(),
							"Verify if applicant biometric page is displayed");
					applicantBiometricsPage.clickOnScanButton();

					assertTrue(applicantBiometricsPage.isThumbsScan(), "Verify if thumbs scan 1st attempt");
					applicantBiometricsPage.closeScanCapturePopUp();
					biometricDetailsPage = applicantBiometricsPage.clickOnBackButton();
				}
				// face
				if (FetchUiSpec.face.equals("yes")) {
					assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),
							"Verify if biometric details page is displayed");
					biometricDetailsPage.clickOnFaceScanIcon();

					assertTrue(applicantBiometricsPage.isApplicantBiometricsPageDisplayed(),
							"Verify if applicant biometric page is displayed");
					applicantBiometricsPage.clickOnScanButton();

					assertTrue(applicantBiometricsPage.isFaceScan(), "Verify if face scan 1st attempt");
					applicantBiometricsPage.closeScanCapturePopUp();
					applicantBiometricsPage.clickOnBackButton();
				}
				assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),
						"Verify if biometric details page is displayed");
				biometricDetailsPage.clickOnContinueButton();
			}
		}
		if ("eng".equalsIgnoreCase(language)) {
			previewPage = new PreviewPageEnglish(driver);
		} else if ("hin".equalsIgnoreCase(language)) {
			previewPage = new PreviewPageHindi(driver);
		} else if ("fra".equalsIgnoreCase(language)) {
			previewPage = new PreviewPageFrench(driver);
		} else if ("kan".equalsIgnoreCase(language)) {
			previewPage = new PreviewPageKannada(driver);
		} else if ("tam".equalsIgnoreCase(language)) {
			previewPage = new PreviewPageTamil(driver);
		} else if ("ara".equalsIgnoreCase(language)) {
			previewPage = new PreviewPageArabic(driver);
		} else {
			throw new IllegalStateException("Unsupported language in testdata.json: " + language);
		}

		assertTrue(previewPage.isLostUinTitleDisplayed(), "Verify if lost uin title is displayed");
		assertTrue(previewPage.isApplicationIDPreviewPagePageDisplayed(),
				"Verify if application ID In PreviewPage is displayed");
		assertTrue(previewPage.isDemographicInformationInPreviewPageDisplayed(),
				"Verify if Demographic Information In PreviewPage is displayed");
		assertTrue(previewPage.isDocumentsInformationInPreviewPageDisplayed(),
				"Verify if Documents Information In PreviewPage is displayed");
		assertTrue(previewPage.isBiometricsInformationInPreviewPagePageDisplayed(),
				"Verify if Biometrics Information In PreviewPage is displayed");
		String Aid = previewPage.getAID();
		previewPage.clickOnContinueButton();
		if ("eng".equalsIgnoreCase(language)) {
			authenticationPage = new AuthenticationPageEnglish(driver);
		} else if ("hin".equalsIgnoreCase(language)) {
			authenticationPage = new AuthenticationPageHindi(driver);
		} else if ("fra".equalsIgnoreCase(language)) {
			authenticationPage = new AuthenticationPageFrench(driver);
		} else if ("kan".equalsIgnoreCase(language)) {
			authenticationPage = new AuthenticationPageKannada(driver);
		} else if ("tam".equalsIgnoreCase(language)) {
			authenticationPage = new AuthenticationPageTamil(driver);
		} else if ("ara".equalsIgnoreCase(language)) {
			authenticationPage = new AuthenticationPageArabic(driver);
		} else {
			throw new IllegalStateException("Unsupported language in testdata.json: " + language);
		}
		assertTrue(authenticationPage.isAuthenticationPageDisplayed(),
				"Verify if authentication details page is displayed");
		authenticationPage.enterUserName(KeycloakUserManager.moduleSpecificUser);
		authenticationPage.enterPassword(ArcConfigManager.getIAMUsersPassword());
		authenticationPage.clickOnAuthenticatenButton();
		if ("eng".equalsIgnoreCase(language)) {
			acknowledgementPage = new AcknowledgementPageEnglish(driver);
		} else if ("hin".equalsIgnoreCase(language)) {
			acknowledgementPage = new AcknowledgementPageHindi(driver);
		} else if ("fra".equalsIgnoreCase(language)) {
			acknowledgementPage = new AcknowledgementPageFrench(driver);
		} else if ("kan".equalsIgnoreCase(language)) {
			acknowledgementPage = new AcknowledgementPageKannada(driver);
		} else if ("tam".equalsIgnoreCase(language)) {
			acknowledgementPage = new AcknowledgementPageTamil(driver);
		} else if ("ara".equalsIgnoreCase(language)) {
			acknowledgementPage = new AcknowledgementPageArabic(driver);
		} else {
			throw new IllegalStateException("Unsupported language in testdata.json: " + language);
		}
		assertTrue(acknowledgementPage.isAcknowledgementPageDisplayed(),
				"Verify if acknowledgement details page is displayed");

		// assertTrue(acknowledgementPage.isQrCodeImageDisplayed(),"Verify if qr code
		// image is displayed");
		acknowledgementPage.clickOnGoToHomeButton();

		assertTrue(registrationTasksPage.isRegistrationTasksPageLoaded(),
				"Verify if registration tasks page is loaded");
		registrationTasksPage.clickOnOperationalTasksTitle();
		if ("eng".equalsIgnoreCase(language)) {
			operationalTaskPage = new OperationalTaskPageEnglish(driver);
		} else if ("hin".equalsIgnoreCase(language)) {
			operationalTaskPage = new OperationalTaskPageHindi(driver);
		} else if ("fra".equalsIgnoreCase(language)) {
			operationalTaskPage = new OperationalTaskPageFrench(driver);
		} else if ("kan".equalsIgnoreCase(language)) {
			operationalTaskPage = new OperationalTaskPageKannada(driver);
		} else if ("tam".equalsIgnoreCase(language)) {
			operationalTaskPage = new OperationalTaskPageTamil(driver);
		} else if ("ara".equalsIgnoreCase(language)) {
			operationalTaskPage = new OperationalTaskPageArabic(driver);
		} else {
			throw new IllegalStateException("Unsupported language in testdata.json: " + language);
		}
		assertTrue(operationalTaskPage.isOperationalTaskPageLoaded(), "Verify if operational Task Page is loaded");
		assertTrue(operationalTaskPage.isPendingApprovalTitleDisplayed(), "Verify if pending approval tite displayed");
		operationalTaskPage.clickPendingApprovalTitle();

		if ("eng".equalsIgnoreCase(language)) {
			pendingApproval = new PendingApprovalEnglish(driver);
		} else if ("hin".equalsIgnoreCase(language)) {
			pendingApproval = new PendingApprovalHindi(driver);
		} else if ("fra".equalsIgnoreCase(language)) {
			pendingApproval = new PendingApprovalFrench(driver);
		} else if ("kan".equalsIgnoreCase(language)) {
			pendingApproval = new PendingApprovalKannada(driver);
		} else if ("tam".equalsIgnoreCase(language)) {
			pendingApproval = new PendingApprovalTamil(driver);
		} else if ("ara".equalsIgnoreCase(language)) {
			pendingApproval = new PendingApprovalArabic(driver);
		} else {
			throw new IllegalStateException("Unsupported language in testdata.json: " + language);
		}
		assertTrue(pendingApproval.isPendingApprovalTitleDisplayed(), "Verify if pending approval page  displayed");
		pendingApproval.clickOnAID(Aid);

		assertTrue(pendingApproval.isApprovalButtonDisplayed(), "Verify if  approval button  displayed");
		pendingApproval.clickOnApproveButton();
		pendingApproval.clickOnClosePopUpButton();

		assertTrue(pendingApproval.isPendingApprovalTitleDisplayed(),
				"Verify if pending approval page  displayed after approving packet");
		pendingApproval.clickOnCheckBox();

		assertTrue(pendingApproval.isAuthenticateButtonEnabled(),
				"Verify if authenticate button is enable after selecting packet");
		pendingApproval.clickOnAuthenticateButton();

		assertTrue(pendingApproval.isSupervisorAuthenticationTitleDisplayed(),
				"Verify if Supervisor Authentication page displayed");

		pendingApproval.clickOnSubmitButton();
		assertTrue(pendingApproval.isSubmitButtonEnabledWithEmptyUsername(),
				"Verify if error empty username submit button enabled");

		pendingApproval.enterUserName(KeycloakUserManager.moduleSpecificUser + "123");

		assertTrue(pendingApproval.isInvalidUsernameMessageDisplayed(),
				"Verify if invalid username messgae is displayed");
		pendingApproval.enterUserName(KeycloakUserManager.moduleSpecificUser);

		pendingApproval.enterPassword(ArcConfigManager.getIAMUsersPassword());
		pendingApproval.clickOnSubmitButton();
		pendingApproval.clickOnBackButton();
		assertTrue(operationalTaskPage.isApplicationUploadTitleDisplayed(),
				"Verify if application upload tite displayed");

		operationalTaskPage.clickApplicationUploadTitle();
		if ("eng".equalsIgnoreCase(language)) {
			manageApplicationsPage = new ManageApplicationsPageEnglish(driver);
		} else if ("hin".equalsIgnoreCase(language)) {
			manageApplicationsPage = new ManageApplicationsPageHindi(driver);
		} else if ("fra".equalsIgnoreCase(language)) {
			manageApplicationsPage = new ManageApplicationsPageFrench(driver);
		} else if ("kan".equalsIgnoreCase(language)) {
			manageApplicationsPage = new ManageApplicationsPageKannada(driver);
		} else if ("tam".equalsIgnoreCase(language)) {
			manageApplicationsPage = new ManageApplicationsPageTamil(driver);
		} else if ("ara".equalsIgnoreCase(language)) {
			manageApplicationsPage = new ManageApplicationsPageArabic(driver);
		} else {
			throw new IllegalStateException("Unsupported language in testdata.json: " + language);
		}
		assertTrue(manageApplicationsPage.isManageApplicationPageDisplayed(),
				"Verify if manage Applications Page displayed");
		manageApplicationsPage.enterAID(Aid);

		assertTrue(manageApplicationsPage.isSearchAIDDisplayed(Aid), "Verify if  Search Aid should  displayed");
		manageApplicationsPage.selectApprovedValueDropdown();

		assertTrue(manageApplicationsPage.isPacketApproved(Aid), "Verify if Filtre packet is approved ");

		assertTrue(manageApplicationsPage.isManageApplicationPageDisplayed(),
				"Verify if manage Applications Page displayed");
		manageApplicationsPage.enterAID(Aid);

		assertTrue(manageApplicationsPage.isSearchAIDDisplayed(Aid), "Verify if  Search Aid should  displayed");
		manageApplicationsPage.clickOnSearchCheckBox();
		manageApplicationsPage.clickOnUploadButton();

		// assertTrue(manageApplicationsPage.isPacketUploadDone(Aid), "Verify if packet
		// upload is done");
		manageApplicationsPage.clickOnBackButton();

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
		// assertTrue(profilePage.isProfileTitleDisplayed(),"Verify if profile title
		// display on Profilepage");
		profilePage.clickOnLogoutButton();

		profilePage.clickOnLogoutButton();

		assertTrue(loginPage.isLoginPageLoaded(), "verify if login page is displayeded in Selected language");
	}

}
