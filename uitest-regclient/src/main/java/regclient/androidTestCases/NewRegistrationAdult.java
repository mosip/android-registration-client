package regclient.androidTestCases;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import regclient.BaseTest.AndroidBaseTest;
import regclient.api.ConfigManager;
import regclient.api.FetchUiSpec;
import regclient.api.KeycloakUserManager;
import regclient.page.AcknowledgementPage;
import regclient.page.ApplicantBiometricsPage;
import regclient.page.AuthenticationPage;
import regclient.page.BasePage;
import regclient.page.BiometricDetailsPage;
import regclient.page.CameraPage;
import regclient.page.ConsentPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.DocumentUploadPage;
import regclient.page.IdentityProofPage;
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
import regclient.pages.arabic.IdentityProofPageArabic;
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
import regclient.pages.english.IdentityProofPageEnglish;
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
import regclient.pages.french.IdentityProofPageFrench;
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
import regclient.pages.hindi.IdentityProofPageHindi;
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
import regclient.pages.kannada.IdentityProofPageKannada;
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
import regclient.pages.tamil.IdentityProofPageTamil;
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

public class NewRegistrationAdult extends AndroidBaseTest {

	@Test
	public void newRegistrationAdult(){
		BasePage.disableAutoRotation();
		FetchUiSpec.getUiSpec("newProcess");
		LoginPage loginPage = null;
		RegistrationTasksPage registrationTasksPage=null;
		SelectLanguagePage selectLanguagePage=null;
		ConsentPage consentPage=null;
		DemographicDetailsPage demographicPage=null;
		DocumentUploadPage documentuploadPage=null;
		IdentityProofPage identityProofPage=null;
		BiometricDetailsPage biometricDetailsPage=null;
		ApplicantBiometricsPage applicantBiometricsPage=null;
		PreviewPage previewPage=null;
		AuthenticationPage authenticationPage=null;
		AcknowledgementPage acknowledgementPage=null;
		OperationalTaskPage operationalTaskPage=null;
		PendingApproval pendingApproval=null;
		ManageApplicationsPage manageApplicationsPage=null;
		ProfilePage profilePage=null;

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
		loginPage.enterUserName(KeycloakUserManager.moduleSpecificUser);
		loginPage.clickOnNextButton();

		loginPage.enterPassword(ConfigManager.getIAMUsersPassword());
		loginPage.clickOnloginButton();

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
		assertTrue(registrationTasksPage.isRegistrationTasksPageLoaded(),"Verify if registration tasks page is loaded");
		registrationTasksPage.clickOnNewRegistrationButton();

		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			selectLanguagePage=new SelectLanguagePageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			selectLanguagePage=new SelectLanguagePageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			selectLanguagePage=new SelectLanguagePageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			selectLanguagePage=new SelectLanguagePageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			selectLanguagePage=new SelectLanguagePageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			selectLanguagePage=new SelectLanguagePageArabic(driver);
		}
		assertTrue(selectLanguagePage.isSelectLanguagePageLoaded(),"Verify if select language page  is loaded");
		selectLanguagePage.clickOnSubmitButtonWithoutSelectingLanguage();

		assertTrue(selectLanguagePage.isSelectLanguagePageLoaded(),"Verify if user should not be allow to navigate to next screen.");
		selectLanguagePage.selectSecondLanguage();

		assertTrue(selectLanguagePage.isNotificationLanguageEnglishDisplayed(),"verify if the notification language display in english");
		selectLanguagePage.selectNotificationlanguage(TestDataReader.readData("notificationLanguage"));

		assertTrue(selectLanguagePage.isSubmitButtonEnabled(),"verify if the submit  button enabled");
		selectLanguagePage.clickOnSubmitButton();

		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			consentPage =new ConsentPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			consentPage =new ConsentPageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			consentPage =new ConsentPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			consentPage =new ConsentPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			consentPage =new ConsentPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			consentPage =new ConsentPageArabic(driver);
		}		
		assertTrue(consentPage.isConsentPageDisplayed(),"Verify if Consent page is loaded");
		assertTrue(consentPage.isCheckBoxReadable(),"Verify if the check box in readable");

		assertTrue(consentPage.isInformedButtonEnabled(),"Verify if informed  button enabled");
		consentPage.clickOnInformedButton();

		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			demographicPage=new DemographicDetailsPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			demographicPage=new DemographicDetailsPageHindi(driver);

		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			demographicPage=new DemographicDetailsPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			demographicPage=new DemographicDetailsPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			demographicPage=new DemographicDetailsPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			demographicPage=new DemographicDetailsPageArabic(driver);
		}
		assertTrue(demographicPage.isDemographicDetailsPageDisplayed(),"Verify if demographic details page is displayed");
		demographicPage.enterFullName(TestDataReader.readData("fullname"));

		assertTrue(demographicPage.checkFullNameSecondLanguageTextBoxNotNull(),"Verify if first name is enter in second language text box");
		demographicPage.enterAge(TestDataReader.readData("adultage"));
		demographicPage.selectGender(TestDataReader.readData("gender"));
		demographicPage.enterAddressLine1(TestDataReader.readData("address"));
		demographicPage.enterAddressLine2(TestDataReader.readData("address"));
		demographicPage.enterAddressLine3(TestDataReader.readData("address"));
		demographicPage.selectMaritalStatus();

		assertTrue(demographicPage.isResidenceStatusHeaderDisplayed(),"Verify if residence status header is displayed");
		demographicPage.selectResidenceStatus(TestDataReader.readData("residenceStatus"));

		assertTrue(demographicPage.isRegionHeaderDisplayed(),"Verify if region status header is displayed");
		demographicPage.selectRegionStatus(TestDataReader.readData("region"));

		assertTrue(demographicPage.isProvinceHeaderDisplayed(),"Verify if province status header is displayed");
		demographicPage.selectProvinceStatus(TestDataReader.readData("province"));

		assertTrue(demographicPage.isCityHeaderDisplayed(),"Verify if city header is displayed");
		demographicPage.selectCityStatus(TestDataReader.readData("city"));

		assertTrue(demographicPage.isZoneHeaderDisplayed(),"Verify if zone header is displayed");
		demographicPage.selectZoneStatus();

		assertTrue(demographicPage.isPostalCodeHeaderDisplayed(),"Verify if postal code header is displayed");
		demographicPage.selectPostalStatus();

		assertTrue(demographicPage.isMobileNumberHeaderDisplayed(),"Verify if mobile number header is displayed");
		demographicPage.enterMobileNumber(TestDataReader.readData("mobileNumber"));

		assertTrue(demographicPage.isEmailHeaderDisplayed(),"Verify if email header is displayed");
		demographicPage.enterEmailID(TestDataReader.readData("emailId"));
		demographicPage.clickOnContinueButton();
		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			documentuploadPage=new DocumentuploadPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			documentuploadPage=new DocumentUploadPageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			documentuploadPage=new DocumentUploadPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			documentuploadPage=new DocumentuploadPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			documentuploadPage=new DocumentuploadPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			documentuploadPage=new DocumentuploadPageArabic(driver);
		}
		assertTrue(documentuploadPage.isDoccumentUploadPageDisplayed(),"Verify if doccumentupload page is displayed");
		documentuploadPage.enterReferenceNumberInAdressProof();
		documentuploadPage.selectAddressProof();
		documentuploadPage.closePopUpClose();

		assertTrue(documentuploadPage.isScanButtonAddressProofEnabled(),"Verify if scan  button enabled");
		CameraPage cameraPage=documentuploadPage.clickOnAddressProofScanButton();

		cameraPage.clickimage();
		cameraPage.clickOkButton();
		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			identityProofPage=new IdentityProofPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			identityProofPage=new IdentityProofPageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			identityProofPage=new IdentityProofPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			identityProofPage=new IdentityProofPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			identityProofPage=new IdentityProofPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			identityProofPage=new IdentityProofPageArabic(driver);
		}
		assertTrue(identityProofPage.isRetakeButtonDisplayed(),"Verify if retake  button displayed");
		identityProofPage.cropCaptureImage();
		identityProofPage.clickOnSaveButton();

		assertTrue(documentuploadPage.isDoccumentUploadPageDisplayed(),"Verify if doccumentupload page is displayed");
		documentuploadPage.selectOnCaptureImage();

		assertTrue(documentuploadPage.isImageDisplyed(),"Verify if capture image is displayed");
		documentuploadPage.clickOnBackButton();

		assertTrue(documentuploadPage.isDeleteButtonDisplyed(),"Verify if delete button is displayed");
		documentuploadPage.enterReferenceNumberInIdentityProof();
		documentuploadPage.selectIdentityProof();
		documentuploadPage.closePopUpClose();

		assertTrue(documentuploadPage.isScanButtonIdentityProofEnabled(),"Verify if scan  button enabled");
		cameraPage=documentuploadPage.clickOnScanButtonIdentityProof();

		cameraPage.clickimage();
		cameraPage.clickOkButton();

		assertTrue(identityProofPage.isRetakeButtonDisplayed(),"Verify if retake  button displayed");
		identityProofPage.cropCaptureImage();
		documentuploadPage=identityProofPage.clickOnSaveButton();

		assertTrue(documentuploadPage.isDeleteButtonDisplyed(),"Verify if delete button is displayed");
		documentuploadPage.enterReferenceNumberInDobProof();
		documentuploadPage.selectDobProof();
		documentuploadPage.closePopUpClose();

		assertTrue(documentuploadPage.isDobHeaderDisplayed(),"Verify if DOB header is displayed");
		cameraPage=documentuploadPage.clickOnScanButtonDobProof();

		cameraPage.clickimage();
		cameraPage.clickOkButton();

		assertTrue(identityProofPage.isRetakeButtonDisplayed(),"Verify if retake  button displayed");
		identityProofPage.cropCaptureImage();
		documentuploadPage=identityProofPage.clickOnSaveButton();
		documentuploadPage.clickOnContinueButton();
		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			biometricDetailsPage=new BiometricDetailsPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			biometricDetailsPage=new BiometricDetailsPageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			biometricDetailsPage=new BiometricDetailsPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			biometricDetailsPage=new BiometricDetailsPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			biometricDetailsPage=new BiometricDetailsPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			biometricDetailsPage=new BiometricDetailsPageArabic(driver);
		}
		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if biometric details page is displayed");
		biometricDetailsPage.clickOnIrisScan();
		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			applicantBiometricsPage=new ApplicantBiometricsPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			applicantBiometricsPage=new ApplicantBiometricsPageHindi(driver);

		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			applicantBiometricsPage=new ApplicantBiometricsPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			applicantBiometricsPage=new ApplicantBiometricsPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			applicantBiometricsPage=new ApplicantBiometricsPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			applicantBiometricsPage=new ApplicantBiometricsPageArabic(driver);
		}
		assertTrue(applicantBiometricsPage.isApplicantBiometricsPageDisplyed(),"Verify if applicant biometric page is displayed");
		applicantBiometricsPage.clickOnScanButton();

		assertTrue(applicantBiometricsPage.isIrisScan(),"Verify if iris scan 1st attempt");
		applicantBiometricsPage.closeScanCapturePopUp();

		applicantBiometricsPage.clickOnScanButton();
		assertTrue(applicantBiometricsPage.isIrisScan(),"Verify if iris scan 2nd attempt");
		applicantBiometricsPage.closeScanCapturePopUp();

		applicantBiometricsPage.clickOnScanButton();
		assertTrue(applicantBiometricsPage.isIrisScan(),"Verify if iris scan 3rd attempt");
		applicantBiometricsPage.closeScanCapturePopUp();

		biometricDetailsPage=applicantBiometricsPage.clickOnBackButton();
		//righthand
		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if biometric details page is displayed");
		applicantBiometricsPage=biometricDetailsPage.clickOnRightHandScanIcon();

		assertTrue(applicantBiometricsPage.isApplicantBiometricsPageDisplyed(),"Verify if applicant biometric page is displayed");
		applicantBiometricsPage.clickOnScanButton();

		assertTrue(applicantBiometricsPage.isRightHandScan(),"Verify if right hand scan 1st attempt");
		applicantBiometricsPage.closeScanCapturePopUp();
		biometricDetailsPage=applicantBiometricsPage.clickOnBackButton();
		//lefthand
		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if biometric details page is displayed");
		applicantBiometricsPage=biometricDetailsPage.clickOnLeftHandScanIcon();

		assertTrue(applicantBiometricsPage.isApplicantBiometricsPageDisplyed(),"Verify if applicant biometric page is displayed");
		applicantBiometricsPage.clickOnScanButton();

		assertTrue(applicantBiometricsPage.isLeftHandScan(),"Verify if Left hand scan 1st attempt");
		applicantBiometricsPage.closeScanCapturePopUp();
		biometricDetailsPage=applicantBiometricsPage.clickOnBackButton();
		//thumb
		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if biometric details page is displayed");
		applicantBiometricsPage=biometricDetailsPage.clickOnThumbsScanIcon();

		assertTrue(applicantBiometricsPage.isApplicantBiometricsPageDisplyed(),"Verify if applicant biometric page is displayed");
		applicantBiometricsPage.clickOnScanButton();

		assertTrue(applicantBiometricsPage.isThumbsScan(),"Verify if thumbs scan 1st attempt");
		applicantBiometricsPage.closeScanCapturePopUp();
		biometricDetailsPage=applicantBiometricsPage.clickOnBackButton();
		//face
		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if biometric details page is displayed");
		biometricDetailsPage.clickOnFaceScanIcon();

		assertTrue(applicantBiometricsPage.isApplicantBiometricsPageDisplyed(),"Verify if applicant biometric page is displayed");
		applicantBiometricsPage.clickOnScanButton();

		assertTrue(applicantBiometricsPage.isFaceScan(),"Verify if face scan 1st attempt");
		applicantBiometricsPage.closeScanCapturePopUp();
		applicantBiometricsPage.clickOnBackButton();

		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if biometric details page is displayed");
		biometricDetailsPage.clickOnContinueButton();
		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			previewPage=new PreviewPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			previewPage=new PreviewPageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			previewPage=new PreviewPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			previewPage=new PreviewPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			previewPage=new PreviewPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			previewPage=new PreviewPageArabic(driver);
		}

		assertTrue(previewPage.isNewRegistrationTitleDisplayed(),"Verify if new Registration title is displayed");
		assertTrue(previewPage.isApplicationIDPreviewPagePageDisplayed(),"Verify if application ID In PreviewPage is displayed");
		assertTrue(previewPage.isDemographicInformationInPreviewPageDisplayed(),"Verify if Demographic Information In PreviewPage is displayed");
		assertTrue(previewPage.isDocumentsInformationInPreviewPageDisplayed(),"Verify if Documents Information In PreviewPage is displayed");
		assertTrue(previewPage.isBiometricsInformationInPreviewPagePageDisplayed(),"Verify if Biometrics Information In PreviewPage is displayed");
		String Aid=previewPage.getAID();
		previewPage.clickOnContinueButton();
		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			authenticationPage=new AuthenticationPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			authenticationPage=new AuthenticationPageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			authenticationPage=new AuthenticationPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			authenticationPage=new AuthenticationPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			authenticationPage=new AuthenticationPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			authenticationPage=new AuthenticationPageArabic(driver);
		}
		assertTrue(authenticationPage.isAuthenticationPageDisplayed(),"Verify if authentication details page is displayed");
		authenticationPage.enterUserName(KeycloakUserManager.moduleSpecificUser);
		authenticationPage.enterPassword(ConfigManager.getIAMUsersPassword());
		authenticationPage.clickOnAuthenticatenButton();
		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			acknowledgementPage=new AcknowledgementPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			acknowledgementPage=new AcknowledgementPageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			acknowledgementPage=new AcknowledgementPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			acknowledgementPage=new AcknowledgementPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			acknowledgementPage=new AcknowledgementPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			acknowledgementPage=new AcknowledgementPageArabic(driver);
		}
		assertTrue(acknowledgementPage.isAcknowledgementPageDisplayed(),"Verify if acknowledgement details page is displayed");

		//assertTrue(acknowledgementPage.isQrCodeImageDisplayed(),"Verify if qr code image  is displayed");		
		acknowledgementPage.clickOnGoToHomeButton();

		assertTrue(registrationTasksPage.isRegistrationTasksPageLoaded(),"Verify if registration tasks page is loaded");
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
		assertTrue(operationalTaskPage.isPendingApprovalTitleDisplayed(), "Verify if pending approval tite displayed");
		operationalTaskPage.clickPendingApprovalTitle();

		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			pendingApproval=new PendingApprovalEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			pendingApproval=new PendingApprovalHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			pendingApproval=new PendingApprovalFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			pendingApproval=new PendingApprovalKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			pendingApproval=new PendingApprovalTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			pendingApproval=new PendingApprovalArabic(driver);
		}
		assertTrue(pendingApproval.isPendingApprovalTitleDisplayed(), "Verify if pending approval page  displayed");
		pendingApproval.clickOnAID(Aid);

		assertTrue(pendingApproval.isApprovalButtonDisplayed(), "Verify if  approval button  displayed");
		pendingApproval.clickOnApproveButton();
		pendingApproval.clickOnClosePopUpButton();
		pendingApproval.clickOnCheckBox();
		pendingApproval.clickOnSubmitButton();

		assertTrue(pendingApproval.isSupervisorAuthenticationTitleDisplayed(), "Verify if Supervisor Authentication page displayed");
		pendingApproval.enterUserName(KeycloakUserManager.moduleSpecificUser);
		pendingApproval.enterPassword(ConfigManager.getIAMUsersPassword());
		pendingApproval.clickOnSubmitButton();
		pendingApproval.clickOnBackButton();
		assertTrue(operationalTaskPage.isApplicationUploadTitleDisplayed(), "Verify if application upload tite displayed");

		operationalTaskPage.clickApplicationUploadTitle();       
		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			manageApplicationsPage=new ManageApplicationsPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			manageApplicationsPage=new ManageApplicationsPageHindi(driver);

		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			manageApplicationsPage=new ManageApplicationsPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			manageApplicationsPage=new ManageApplicationsPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			manageApplicationsPage=new ManageApplicationsPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			manageApplicationsPage=new ManageApplicationsPageArabic(driver);
		}
		assertTrue(manageApplicationsPage.isManageApplicationPageDisplayed(), "Verify if manage Applications Page displayed");
		manageApplicationsPage.enterWrongAID(Aid+123);

		assertTrue(manageApplicationsPage.isZeroApplicationDisplayed(), "Verify if wrong Aid should not display");
		manageApplicationsPage.enterAID(Aid);

		assertTrue(manageApplicationsPage.isSearchAIDDisplayed(Aid), "Verify if  Search Aid should  displayed");
		manageApplicationsPage.selectApprovedValueDropdown();

		assertTrue(manageApplicationsPage.isPacketApproved(Aid), "Verify if Filtre packet is approved ");
		manageApplicationsPage.clickOnSearchCheckBox();
		manageApplicationsPage.clickOnUploadButton();

		//		assertTrue(manageApplicationsPage.isPacketUploadDone(Aid), "Verify if packet upload is done");
		manageApplicationsPage.clickOnBackButton();

		assertTrue(registrationTasksPage.isProfileTitleDisplayed(),"Verify if profile title display on homepage");
		registrationTasksPage.clickProfileButton();

		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			profilePage=new ProfilePageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			profilePage=new ProfilePageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			profilePage=new ProfilePageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			profilePage=new ProfilePageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			profilePage=new ProfilePageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			profilePage=new ProfilePageArabic(driver);
		}
		//assertTrue(profilePage.isProfileTitleDisplayed(),"Verify if profile title display on Profilepage");
		profilePage.clickOnLogoutButton();

		profilePage.clickOnLogoutButton();

		assertTrue(loginPage.isLoginPageLoaded(),"verify if login page is displayeded in Selected language");		
	}

	@Test
	public void newRegistrationAdultUploadMultipleDoccuments(){	
		FetchUiSpec.getUiSpec("newProcess");

		BasePage.disableAutoRotation();
		LoginPage loginPage = null;
		RegistrationTasksPage registrationTasksPage=null;
		SelectLanguagePage selectLanguagePage=null;
		ConsentPage consentPage=null;
		DemographicDetailsPage demographicPage=null;
		DocumentUploadPage documentuploadPage=null;
		IdentityProofPage identityProofPage=null;
		BiometricDetailsPage biometricDetailsPage=null;
		ApplicantBiometricsPage applicantBiometricsPage=null;
		PreviewPage previewPage=null;
		AuthenticationPage authenticationPage=null;
		AcknowledgementPage acknowledgementPage=null;
		PendingApproval pendingApproval=null;

		IntroducerBiometricPage introducerBiometricPage=null;
		OperationalTaskPage operationalTaskPage=null;
		ManageApplicationsPage manageApplicationsPage=null;

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

		loginPage.enterUserName(KeycloakUserManager.moduleSpecificUser);
		loginPage.clickOnNextButton();

		loginPage.enterPassword(ConfigManager.getIAMUsersPassword());
		loginPage.clickOnloginButton();
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
		assertTrue(registrationTasksPage.isRegistrationTasksPageLoaded(),"Verify if registration tasks page is loaded");
		registrationTasksPage.clickOnNewRegistrationButton();
		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			selectLanguagePage=new SelectLanguagePageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			selectLanguagePage=new SelectLanguagePageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			selectLanguagePage=new SelectLanguagePageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			selectLanguagePage=new SelectLanguagePageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			selectLanguagePage=new SelectLanguagePageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			selectLanguagePage=new SelectLanguagePageArabic(driver);
		}
		assertTrue(selectLanguagePage.isSelectLanguagePageLoaded(),"Verify if select language page  is loaded");
		selectLanguagePage.clickOnSubmitButtonWithoutSelectingLanguage();

		assertTrue(selectLanguagePage.isSelectLanguagePageLoaded(),"Verify if user should not be allow to navigate to next screen.");
		selectLanguagePage.selectSecondLanguage();

		assertTrue(selectLanguagePage.isNotificationLanguageEnglishDisplayed(),"verify if the notification language display in english");
		selectLanguagePage.selectNotificationlanguage(TestDataReader.readData("notificationLanguage"));

		assertTrue(selectLanguagePage.isSubmitButtonEnabled(),"verify if the submit  button enabled");
		selectLanguagePage.clickOnSubmitButton();
		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			consentPage =new ConsentPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			consentPage =new ConsentPageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			consentPage =new ConsentPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			consentPage =new ConsentPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			consentPage =new ConsentPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			consentPage =new ConsentPageArabic(driver);
		}
		assertTrue(consentPage.isConsentPageDisplayed(),"Verify if Consent page is loaded");

		assertTrue(consentPage.isInformedButtonEnabled(),"Verify if informed  button enabled");
		consentPage.clickOnInformedButton();
		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			demographicPage=new DemographicDetailsPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			demographicPage=new DemographicDetailsPageHindi(driver);

		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			demographicPage=new DemographicDetailsPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			demographicPage=new DemographicDetailsPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			demographicPage=new DemographicDetailsPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			demographicPage=new DemographicDetailsPageArabic(driver);
		}
		assertTrue(demographicPage.isDemographicDetailsPageDisplayed(),"Verify if demographic details page is displayed");
		demographicPage.enterFullName(TestDataReader.readData("fullname"));

		assertTrue(demographicPage.checkFullNameSecondLanguageTextBoxNotNull(),"Verify if first name is enter in second language text box");
		demographicPage.enterAge(TestDataReader.readData("adultage"));
		demographicPage.selectGender(TestDataReader.readData("gender"));
		demographicPage.enterAddressLine1(TestDataReader.readData("address"));
		demographicPage.enterAddressLine2(TestDataReader.readData("address"));
		demographicPage.enterAddressLine3(TestDataReader.readData("address"));
		demographicPage.selectMaritalStatus();

		assertTrue(demographicPage.isResidenceStatusHeaderDisplayed(),"Verify if residence status header is displayed");
		demographicPage.selectResidenceStatus(TestDataReader.readData("residenceStatus"));

		assertTrue(demographicPage.isRegionHeaderDisplayed(),"Verify if region status header is displayed");
		demographicPage.selectRegionStatus(TestDataReader.readData("region"));

		assertTrue(demographicPage.isProvinceHeaderDisplayed(),"Verify if province status header is displayed");
		demographicPage.selectProvinceStatus(TestDataReader.readData("province"));

		assertTrue(demographicPage.isCityHeaderDisplayed(),"Verify if city header is displayed");
		demographicPage.selectCityStatus(TestDataReader.readData("city"));

		assertTrue(demographicPage.isZoneHeaderDisplayed(),"Verify if zone header is displayed");
		demographicPage.selectZoneStatus();

		assertTrue(demographicPage.isPostalCodeHeaderDisplayed(),"Verify if postal code header is displayed");
		demographicPage.selectPostalStatus();

		assertTrue(demographicPage.isMobileNumberHeaderDisplayed(),"Verify if mobile number header is displayed");
		demographicPage.enterMobileNumber(TestDataReader.readData("fullname"));

		assertTrue(demographicPage.isErrorMessageInvalidInputTextDisplayed(),"Verify if invalid text error message text  is displayed");
		demographicPage.enterMobileNumber(TestDataReader.readData("mobileNumber"));

		assertTrue(demographicPage.isEmailHeaderDisplayed(),"Verify if email header is displayed");
		demographicPage.enterEmailID(TestDataReader.readData("emailId"));
		demographicPage.clickOnContinueButton();
		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			documentuploadPage=new DocumentuploadPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			documentuploadPage=new DocumentUploadPageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			documentuploadPage=new DocumentUploadPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			documentuploadPage=new DocumentuploadPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			documentuploadPage=new DocumentuploadPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			documentuploadPage=new DocumentuploadPageArabic(driver);
		}
		assertTrue(documentuploadPage.isDoccumentUploadPageDisplayed(),"Verify if doccumentupload page is displayed");
		documentuploadPage.selectAddressProof();
		documentuploadPage.closePopUpClose();

		assertTrue(documentuploadPage.isScanButtonAddressProofEnabled(),"Verify if scan  button enabled");
		CameraPage cameraPage=documentuploadPage.clickOnAddressProofScanButton();

		cameraPage.clickimage();
		cameraPage.clickOkButton();
		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			identityProofPage=new IdentityProofPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			identityProofPage=new IdentityProofPageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			identityProofPage=new IdentityProofPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			identityProofPage=new IdentityProofPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			identityProofPage=new IdentityProofPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			identityProofPage=new IdentityProofPageArabic(driver);
		}
		assertTrue(identityProofPage.isRetakeButtonDisplayed(),"Verify if retake  button displayed");
		identityProofPage.cropCaptureImage();
		documentuploadPage=identityProofPage.clickOnSaveButton();

		assertTrue(documentuploadPage.isImageDisplyed(),"Verify if 1st capture image is displayed");

		assertTrue(documentuploadPage.isDoccumentUploadPageDisplayed(),"Verify if doccumentupload page is displayed");
		documentuploadPage.selectAddressProof();
		documentuploadPage.closePopUpClose();

		assertTrue(documentuploadPage.isScanButtonAddressProofEnabled(),"Verify if scan  button enabled");
		documentuploadPage.clickOnAddressProofScanButton();

		cameraPage.clickimage();
		cameraPage.clickOkButton();

		assertTrue(identityProofPage.isRetakeButtonDisplayed(),"Verify if retake  button displayed");
		identityProofPage.cropCaptureImage();
		documentuploadPage=identityProofPage.clickOnSaveButton();

		assertTrue(documentuploadPage.isSecondImageDisplyed(),"Verify if 2nd capture image is displayed");

		assertTrue(documentuploadPage.isDoccumentUploadPageDisplayed(),"Verify if doccumentupload page is displayed");
		documentuploadPage.selectAddressProof();
		documentuploadPage.closePopUpClose();

		assertTrue(documentuploadPage.isScanButtonAddressProofEnabled(),"Verify if scan  button enabled");
		documentuploadPage.clickOnAddressProofScanButton();

		cameraPage.clickimage();
		cameraPage.clickOkButton();

		assertTrue(identityProofPage.isRetakeButtonDisplayed(),"Verify if retake  button displayed");
		identityProofPage.cropCaptureImage();
		documentuploadPage=identityProofPage.clickOnSaveButton();

		assertTrue(documentuploadPage.isThirdImageDisplyed(),"Verify if 3nd capture image is displayed");

		assertTrue(documentuploadPage.isDoccumentUploadPageDisplayed(),"Verify if doccumentupload page is displayed");
		documentuploadPage.selectOnCaptureImage();

		assertTrue(documentuploadPage.isImageDisplyed(),"Verify if capture image is displayed");
		documentuploadPage.clickOnBackButton();

		assertTrue(documentuploadPage.isDeleteButtonDisplyed(),"Verify if delete button is displayed");
		documentuploadPage.clickOnDeleteButton();

		assertFalse(documentuploadPage.isThirdImageDisplyed(),"Verify if 3nd capture image is deleted");
		documentuploadPage.selectIdentityProof();
		documentuploadPage.closePopUpClose();

		assertTrue(documentuploadPage.isScanButtonIdentityProofEnabled(),"Verify if scan  button enabled");
		documentuploadPage.clickOnScanButtonIdentityProof();

		cameraPage.clickimage();
		cameraPage.clickOkButton();

		assertTrue(identityProofPage.isRetakeButtonDisplayed(),"Verify if retake  button displayed");
		identityProofPage.cropCaptureImage();
		documentuploadPage=identityProofPage.clickOnSaveButton();

		assertTrue(documentuploadPage.isDeleteButtonDisplyed(),"Verify if delete button is displayed");
		documentuploadPage.selectDobProof();
		documentuploadPage.closePopUpClose();

		assertTrue(documentuploadPage.isDobHeaderDisplayed(),"Verify if DOB header is displayed");
		documentuploadPage.clickOnScanButtonDobProof();

		cameraPage.clickimage();
		cameraPage.clickOkButton();

		assertTrue(identityProofPage.isRetakeButtonDisplayed(),"Verify if retake  button displayed");
		identityProofPage.cropCaptureImage();
		documentuploadPage=identityProofPage.clickOnSaveButton();
		documentuploadPage.clickOnContinueButton();
		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			biometricDetailsPage=new BiometricDetailsPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			biometricDetailsPage=new BiometricDetailsPageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			biometricDetailsPage=new BiometricDetailsPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			biometricDetailsPage=new BiometricDetailsPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			biometricDetailsPage=new BiometricDetailsPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			biometricDetailsPage=new BiometricDetailsPageArabic(driver);
		}
		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if Reference id field  an optional in document upload page.");
		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if biometric details page is displayed");
		biometricDetailsPage.clickOnIrisScan();
		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			applicantBiometricsPage=new ApplicantBiometricsPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			applicantBiometricsPage=new ApplicantBiometricsPageHindi(driver);

		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			applicantBiometricsPage=new ApplicantBiometricsPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			applicantBiometricsPage=new ApplicantBiometricsPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			applicantBiometricsPage=new ApplicantBiometricsPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			applicantBiometricsPage=new ApplicantBiometricsPageArabic(driver);
		}
		assertTrue(applicantBiometricsPage.isApplicantBiometricsPageDisplyed(),"Verify if applicant biometric page is displayed");
		applicantBiometricsPage.clickOnScanButton();

		assertTrue(applicantBiometricsPage.isIrisScan(),"Verify if iris scan 1st attempt");
		applicantBiometricsPage.closeScanCapturePopUp();
		biometricDetailsPage=applicantBiometricsPage.clickOnBackButton();
		//righthand
		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if biometric details page is displayed");
		applicantBiometricsPage=biometricDetailsPage.clickOnRightHandScanIcon();

		assertTrue(applicantBiometricsPage.isApplicantBiometricsPageDisplyed(),"Verify if applicant biometric page is displayed");
		applicantBiometricsPage.clickOnScanButton();

		assertTrue(applicantBiometricsPage.isRightHandScan(),"Verify if right hand scan 1st attempt");
		applicantBiometricsPage.closeScanCapturePopUp();
		biometricDetailsPage=applicantBiometricsPage.clickOnBackButton();
		//lefthand
		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if biometric details page is displayed");
		applicantBiometricsPage=biometricDetailsPage.clickOnLeftHandScanIcon();

		assertTrue(applicantBiometricsPage.isApplicantBiometricsPageDisplyed(),"Verify if applicant biometric page is displayed");
		applicantBiometricsPage.clickOnScanButton();

		assertTrue(applicantBiometricsPage.isLeftHandScan(),"Verify if Left hand scan 1st attempt");
		applicantBiometricsPage.closeScanCapturePopUp();
		biometricDetailsPage=applicantBiometricsPage.clickOnBackButton();
		//thumb
		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if biometric details page is displayed");
		applicantBiometricsPage=biometricDetailsPage.clickOnThumbsScanIcon();

		assertTrue(applicantBiometricsPage.isApplicantBiometricsPageDisplyed(),"Verify if applicant biometric page is displayed");
		applicantBiometricsPage.clickOnScanButton();

		assertTrue(applicantBiometricsPage.isThumbsScan(),"Verify if thumbs scan 1st attempt");
		applicantBiometricsPage.closeScanCapturePopUp();
		biometricDetailsPage=applicantBiometricsPage.clickOnBackButton();
		//face
		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if biometric details page is displayed");
		applicantBiometricsPage=biometricDetailsPage.clickOnFaceScanIcon();

		assertTrue(applicantBiometricsPage.isApplicantBiometricsPageDisplyed(),"Verify if applicant biometric page is displayed");
		applicantBiometricsPage.clickOnScanButton();

		assertTrue(applicantBiometricsPage.isFaceScan(),"Verify if face scan 1st attempt");
		applicantBiometricsPage.closeScanCapturePopUp();
		biometricDetailsPage=applicantBiometricsPage.clickOnBackButton();

		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if biometric details page is displayed");
		biometricDetailsPage.clickOnContinueButton();
		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			previewPage=new PreviewPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			previewPage=new PreviewPageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			previewPage=new PreviewPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			previewPage=new PreviewPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			previewPage=new PreviewPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			previewPage=new PreviewPageArabic(driver);
		}

		assertTrue(previewPage.isNewRegistrationTitleDisplayed(),"Verify if new Registration title is displayed");
		assertTrue(previewPage.isBothIrisImageDisplayed(),"Verify if both iris  image In PreviewPage is displayed");
		assertTrue(previewPage.isDemographicInformationInPreviewPageDisplayed(),"Verify if Demographic Information In PreviewPage is displayed");
		assertTrue(previewPage.isBiometricsInformationInPreviewPagePageDisplayed(),"Verify if Biometrics Information In PreviewPage is displayed");
		assertTrue(previewPage.isDocumentsInformationInPreviewPageDisplayed(),"Verify if Documents Information In PreviewPage is displayed");
		previewPage.clickOnDemographicDetailsTitle();

		assertTrue(demographicPage.isDemographicDetailsPageDisplayed(),"Verify if demographic details page is displayed");
		demographicPage.enterFullName(TestDataReader.readData("editData"));
		demographicPage.enterAge(TestDataReader.readData("minorAge"));
		demographicPage.enterAddressLine1(TestDataReader.readData("editData"));
		demographicPage.enterAddressLine2(TestDataReader.readData("editData"));
		demographicPage.enterAddressLine3(TestDataReader.readData("editData"));

		assertTrue(demographicPage.isIntroducerNameHeaderDisplayed(),"Verify if introducer name header is displayed");
		demographicPage.enterIntroducerName(TestDataReader.readData("fullname"));

		assertTrue(demographicPage.checkIntroducerNameTextBoxSecondLangaugeTextBoxNotNull(),"Verify if introduceR name is enter in second language text box");

		assertTrue(demographicPage.isIntroducerRidHeaderDisplayed(),"Verify if introducer rid header is displayed");
		demographicPage.enterIntroducerRid(TestDataReader.readData("RID"));


		demographicPage.clickOnContinueButton();

		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			documentuploadPage=new DocumentuploadPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			documentuploadPage=new DocumentUploadPageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			documentuploadPage=new DocumentUploadPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			documentuploadPage=new DocumentuploadPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			documentuploadPage=new DocumentuploadPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			documentuploadPage=new DocumentuploadPageArabic(driver);
		}
		assertTrue(documentuploadPage.isDoccumentUploadPageDisplayed(),"Verify if doccumentupload page is displayed");
		documentuploadPage.selectAddressProof();
		documentuploadPage.closePopUpClose();

		assertTrue(documentuploadPage.isScanButtonAddressProofEnabled(),"Verify if scan  button enabled");
		documentuploadPage.clickOnAddressProofScanButton();

		cameraPage.clickimage();
		cameraPage.clickOkButton();
		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			identityProofPage=new IdentityProofPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			identityProofPage=new IdentityProofPageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			identityProofPage=new IdentityProofPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			identityProofPage=new IdentityProofPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			identityProofPage=new IdentityProofPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			identityProofPage=new IdentityProofPageArabic(driver);
		}
		assertTrue(identityProofPage.isRetakeButtonDisplayed(),"Verify if retake  button displayed");
		identityProofPage.cropCaptureImage();
		documentuploadPage=identityProofPage.clickOnSaveButton();

		assertTrue(documentuploadPage.isDoccumentUploadPageDisplayed(),"Verify if doccumentupload page is displayed");
		documentuploadPage.selectIdentityProof();
		documentuploadPage.closePopUpClose();

		assertTrue(documentuploadPage.isScanButtonIdentityProofEnabled(),"Verify if scan  button enabled");
		cameraPage=documentuploadPage.clickOnScanButtonIdentityProof();

		cameraPage.clickimage();
		cameraPage.clickOkButton();

		assertTrue(identityProofPage.isRetakeButtonDisplayed(),"Verify if retake  button displayed");
		identityProofPage.cropCaptureImage();
		documentuploadPage=identityProofPage.clickOnSaveButton();

		assertTrue(documentuploadPage.isDeleteButtonDisplyed(),"Verify if delete button is displayed");
		documentuploadPage.selectRelationshipProof();
		documentuploadPage.closePopUpClose();

		assertTrue(documentuploadPage.isScanButtonRelationshipProoffEnabled(),"Verify if scan  button enabled");
		cameraPage=documentuploadPage.clickOnScanButtonRelationshipProof();

		cameraPage.clickimage();
		cameraPage.clickOkButton();

		assertTrue(identityProofPage.isRetakeButtonDisplayed(),"Verify if retake  button displayed");
		identityProofPage.cropCaptureImage();
		documentuploadPage=identityProofPage.clickOnSaveButton();

		assertTrue(documentuploadPage.isDeleteButtonDisplyed(),"Verify if delete button is displayed");
		documentuploadPage.selectDobProof();
		documentuploadPage.closePopUpClose();

		assertTrue(documentuploadPage.isDobHeaderDisplayed(),"Verify if DOB header is displayed");
		cameraPage=documentuploadPage.clickOnScanButtonDobProof();

		cameraPage.clickimage();
		cameraPage.clickOkButton();

		assertTrue(identityProofPage.isRetakeButtonDisplayed(),"Verify if retake  button displayed");
		identityProofPage.cropCaptureImage();
		documentuploadPage=identityProofPage.clickOnSaveButton();
		documentuploadPage.clickOnContinueButton();

		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if biometric details page is displayed");

		biometricDetailsPage.clickOnIrisScan();

		assertTrue(applicantBiometricsPage.isApplicantBiometricsPageDisplyed(),"Verify if applicant biometric page is displayed");
		applicantBiometricsPage.clickOnMarkExceptionButton();

		assertTrue(applicantBiometricsPage.isExceptionTypeTitleDisplyed(),"Verify if applicant biometric mark exception is displayed");
		applicantBiometricsPage.markOneEyeException();
		//		applicantBiometricsPage.clickOnExceptionTypeTemporaryButton();
		//
		//		assertTrue(applicantBiometricsPage.isCommentHeaderDisplyed(),"Verify if Comments header is displayed");
		//applicantBiometricsPage.enterCommentsInTextBox(TestDataReader.readData("comments"));

		applicantBiometricsPage.clickOnIrisScanTitle();
		applicantBiometricsPage.clickOnScanButton();

		assertTrue(applicantBiometricsPage.isIrisScan(),"Verify if iris scan 1st attempt");
		applicantBiometricsPage.closeScanCapturePopUp();

		applicantBiometricsPage.clickOnNextButton();
		assertTrue(applicantBiometricsPage.isRightHandScanTitleDisplyed(),"Verify if right hand scan is displayed");
		applicantBiometricsPage.clickOnScanButton();


		assertTrue(applicantBiometricsPage.isRightHandScan(),"Verify if right hand scan 1st attempt");
		applicantBiometricsPage.closeScanCapturePopUp();
		applicantBiometricsPage.clickOnNextButton();
		//lefthand
		assertTrue(applicantBiometricsPage.isLeftHandScanTitleDisplyed(),"Verify if applicant left hand scan title is displayed");
		applicantBiometricsPage.clickOnScanButton();

		assertTrue(applicantBiometricsPage.isLeftHandScan(),"Verify if Left hand scan 1st attempt");
		applicantBiometricsPage.closeScanCapturePopUp();
		applicantBiometricsPage.clickOnNextButton();
		//thumb
		assertTrue(applicantBiometricsPage.isThumbsScanTitleDisplyed(),"Verify if thumbs scan page is displayed");
		applicantBiometricsPage.clickOnScanButton();

		assertTrue(applicantBiometricsPage.isThumbsScan(),"Verify if thumbs scan 1st attempt");
		applicantBiometricsPage.closeScanCapturePopUp();
		applicantBiometricsPage.clickOnNextButton();
		//face
		assertTrue(applicantBiometricsPage.isFaceScanTitleDisplyed(),"Verify if face scan page is displayed");
		applicantBiometricsPage.clickOnScanButton();

		assertTrue(applicantBiometricsPage.isFaceScan(),"Verify if face scan 1st attempt");
		applicantBiometricsPage.closeScanCapturePopUp();
		applicantBiometricsPage.clickOnBackButton();

		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if biometric details page is displayed");
		biometricDetailsPage.clickOnContinueButton();
		//Exception
		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if biometric details page is still displayed without capture exception");
		applicantBiometricsPage=biometricDetailsPage.clickOnExceptionScanIcon();

		assertTrue(applicantBiometricsPage.isExceptionScanTitleDisplyed(),"Verify if exception scan page is displayed");
		applicantBiometricsPage.clickOnScanButton();

		assertTrue(applicantBiometricsPage.isExceptionScan(),"Verify if exception scan 1st attempt");
		applicantBiometricsPage.closeScanCapturePopUp();
		biometricDetailsPage=applicantBiometricsPage.clickOnNextButton();

		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if biometric details page is displayed");
		biometricDetailsPage.clickOnContinueButton();

		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if biometric details page is displayed");
		biometricDetailsPage.clickOnIntroducerIrisScan();
		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			introducerBiometricPage=new IntroducerBiometricPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			introducerBiometricPage=new IntroducerBiometricPageHindi(driver);

		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			introducerBiometricPage=new IntroducerBiometricPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			introducerBiometricPage=new IntroducerBiometricPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			introducerBiometricPage=new IntroducerBiometricPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			introducerBiometricPage=new IntroducerBiometricPageArabic(driver);
		}
		assertTrue(introducerBiometricPage.isIntroducerBiometricsPageDisplyed(),"Verify if introducer biometric page is displayed");
		introducerBiometricPage.clickOnScanButton();

		assertTrue(introducerBiometricPage.isIrisScan(),"Verify if iris scan 1st attempt");
		introducerBiometricPage.closeScanCapturePopUp();
		introducerBiometricPage.clickOnNextButton();
		//righthand
		assertTrue(introducerBiometricPage.isRightHandScanTitleDisplyed(),"Verify if right hand scan is displayed");
		introducerBiometricPage.clickOnScanButton();

		assertTrue(introducerBiometricPage.isRightHandScan(),"Verify if right hand scan 1st attempt");
		introducerBiometricPage.closeScanCapturePopUp();
		introducerBiometricPage.clickOnNextButton();
		//lefthand
		assertTrue(introducerBiometricPage.isLeftHandScanTitleDisplyed(),"Verify if applicant left hand scan title is displayed");
		introducerBiometricPage.clickOnScanButton();

		assertTrue(introducerBiometricPage.isLeftHandScan(),"Verify if Left hand scan 1st attempt");
		introducerBiometricPage.closeScanCapturePopUp();
		introducerBiometricPage.clickOnNextButton();
		//thumb
		assertTrue(introducerBiometricPage.isThumbsScanTitleDisplyed(),"Verify if thumbs scan page is displayed");
		introducerBiometricPage.clickOnScanButton();

		assertTrue(introducerBiometricPage.isThumbsScan(),"Verify if thumbs scan 1st attempt");
		introducerBiometricPage.closeScanCapturePopUp();
		introducerBiometricPage.clickOnNextButton();
		//face
		assertTrue(introducerBiometricPage.isFaceScanTitleDisplyed(),"Verify if face scan page is displayed");
		introducerBiometricPage.clickOnScanButton();

		assertTrue(introducerBiometricPage.isFaceScan(),"Verify if face scan 1st attempt");
		introducerBiometricPage.closeScanCapturePopUp();
		biometricDetailsPage=introducerBiometricPage.clickOnNextButton();

		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if biometric details page is displayed");
		biometricDetailsPage.clickOnContinueButton();

		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			previewPage=new PreviewPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			previewPage=new PreviewPageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			previewPage=new PreviewPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			previewPage=new PreviewPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			previewPage=new PreviewPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			previewPage=new PreviewPageArabic(driver);
		}
		assertTrue(previewPage.isNewRegistrationTitleDisplayed(),"Verify if new Registration title is displayed");
		assertTrue(previewPage.isApplicationIDPreviewPagePageDisplayed(),"Verify if application ID In PreviewPage is displayed");
		assertTrue(previewPage.isDemographicInformationInPreviewPageDisplayed(),"Verify if Demographic Information In PreviewPage is displayed");
		assertTrue(previewPage.isDocumentsInformationInPreviewPageDisplayed(),"Verify if Documents Information In PreviewPage is displayed");
		assertTrue(previewPage.isBiometricsInformationInPreviewPagePageDisplayed(),"Verify if Biometrics Information In PreviewPage is displayed");
		assertTrue(previewPage.isSingleIrisImageDisplayed(),"Verify if single iris exception image In PreviewPage is displayed");

		assertTrue(previewPage.isSingleIrisImageDisplayed(),"Verify if single iris exception image In PreviewPage is displayed");
		String Aid=previewPage.getAID();
		previewPage.clickOnContinueButton();
		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			authenticationPage=new AuthenticationPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			authenticationPage=new AuthenticationPageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			authenticationPage=new AuthenticationPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			authenticationPage=new AuthenticationPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			authenticationPage=new AuthenticationPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			authenticationPage=new AuthenticationPageArabic(driver);
		}
		assertTrue(authenticationPage.isAuthenticationPageDisplayed(),"Verify if authentication details page is displayed");
		authenticationPage.enterUserName(KeycloakUserManager.moduleSpecificUser);
		authenticationPage.enterPassword(ConfigManager.getIAMUsersPassword());
		authenticationPage.clickOnAuthenticatenButton();
		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			acknowledgementPage=new AcknowledgementPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			acknowledgementPage=new AcknowledgementPageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			acknowledgementPage=new AcknowledgementPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			acknowledgementPage=new AcknowledgementPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			acknowledgementPage=new AcknowledgementPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			acknowledgementPage=new AcknowledgementPageArabic(driver);
		}
		assertTrue(acknowledgementPage.isAcknowledgementPageDisplayed(),"Verify if acknowledgement details page is displayed");
		//assertTrue(acknowledgementPage.isQrCodeImageDisplayed(),"Verify if qr code image  is displayed");		

		acknowledgementPage.clickOnGoToHomeButton();
		assertTrue(registrationTasksPage.isRegistrationTasksPageLoaded(),"Verify if registration tasks page is loaded");
		registrationTasksPage.clickOnNewRegistrationButton();

		assertTrue(selectLanguagePage.isSelectLanguagePageLoaded(),"Verify if select language page  is loaded");
		selectLanguagePage.selectAllSecondLanguage();

		assertTrue(selectLanguagePage.isNotificationLanguageEnglishDisplayed(),"verify if the notification language display in english");
		selectLanguagePage.selectNotificationlanguage(TestDataReader.readData("notificationLanguage"));
		selectLanguagePage.clickOnSubmitButton();

		assertFalse(consentPage.isConsentPageDisplayed(),"Verify if Consent page is loaded");
		consentPage.clickOnCancelButton();

		assertTrue(registrationTasksPage.isRegistrationTasksPageLoaded(),"Verify if registration tasks page is loaded");
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
		assertTrue(operationalTaskPage.isPendingApprovalTitleDisplayed(), "Verify if pending approval tite displayed");
		operationalTaskPage.clickPendingApprovalTitle();

		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			pendingApproval=new PendingApprovalEnglish(driver);
		} 
		//		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
		//			pendingApproval=new PendingApprovalHindi(driver);
		//		}
		//		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
		//			pendingApproval=new PendingApprovalFrench(driver);
		//		}
		//		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
		//			pendingApproval=new PendingApprovalKannada(driver);
		//		}
		//		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
		//			pendingApproval=new PendingApprovalTamil(driver);
		//		}
		//		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
		//			pendingApproval=new PendingApprovalArabic(driver);
		//		}
		assertTrue(pendingApproval.isPendingApprovalTitleDisplayed(), "Verify if pending approval page  displayed");
		pendingApproval.clickOnAID(Aid);

		assertTrue(pendingApproval.isApprovalButtonDisplayed(), "Verify if  approval button  displayed");
		pendingApproval.clickOnApproveButton();
		pendingApproval.clickOnClosePopUpButton();
		pendingApproval.clickOnCheckBox();
		pendingApproval.clickOnSubmitButton();

		assertTrue(pendingApproval.isSupervisorAuthenticationTitleDisplayed(), "Verify if Supervisor Authentication page displayed");
		pendingApproval.enterUserName(KeycloakUserManager.moduleSpecificUser);
		pendingApproval.enterPassword(ConfigManager.getIAMUsersPassword());
		pendingApproval.clickOnSubmitButton();
		pendingApproval.clickOnBackButton();
		assertTrue(operationalTaskPage.isApplicationUploadTitleDisplayed(), "Verify if application upload tite displayed");

		operationalTaskPage.clickApplicationUploadTitle();       
		if(TestDataReader.readData("language").equalsIgnoreCase("eng")) {
			manageApplicationsPage=new ManageApplicationsPageEnglish(driver);
		} 
		else if(TestDataReader.readData("language").equalsIgnoreCase("hin")){
			manageApplicationsPage=new ManageApplicationsPageHindi(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("fra")){
			manageApplicationsPage=new ManageApplicationsPageFrench(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("kan")){
			manageApplicationsPage=new ManageApplicationsPageKannada(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("tam")){
			manageApplicationsPage=new ManageApplicationsPageTamil(driver);
		}
		else if(TestDataReader.readData("language").equalsIgnoreCase("ara")){
			manageApplicationsPage=new ManageApplicationsPageArabic(driver);
		}
		assertTrue(manageApplicationsPage.isManageApplicationPageDisplayed(), "Verify if manage Applications Page displayed");
		manageApplicationsPage.enterAID(Aid);

		assertTrue(manageApplicationsPage.isSearchAIDDisplayed(Aid), "Verify if  Search Aid should  displayed");
		manageApplicationsPage.clickOnSearchCheckBox();
		manageApplicationsPage.clickOnUploadButton();

		//		manageApplicationsPage.selectUploadedOptionDropdown();
		//		assertTrue(manageApplicationsPage.isPacketUploadDone(Aid), "Verify if packet upload is done");
	}

}
