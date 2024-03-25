package androidTestCases;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import BaseTest.AndroidBaseTest;
import regclient.pages.AcknowledgementPage;
import regclient.pages.ApplicantBiometricsPage;
import regclient.pages.AuthenticationPage;
import regclient.pages.BiometricDetailsPage;
import regclient.pages.CameraPage;
import regclient.pages.ConsentPage;
import regclient.pages.DemographicDetailsPage;
import regclient.pages.DocumentuploadPage;
import regclient.pages.IdentityProofPage;
import regclient.pages.IntroducerBiometricPage;
import regclient.pages.LoginPage;
import regclient.pages.PreviewPage;
import regclient.pages.RegistrationTasksPage;
import regclient.pages.SelectLanguagePage;
import regclient.utils.TestDataReader;

public class NewRegistrationMinor extends AndroidBaseTest {

	@Test
	public void newRegistrationMinor(){
		LoginPage loginPage=new LoginPage(driver);

		loginPage.enterUserName(TestDataReader.readData("username"));
		loginPage.clickOnNextButton();

		loginPage.enterPassword(TestDataReader.readData("password"));
		RegistrationTasksPage RegistrationTasksPage=loginPage.clickOnloginButton();

		assertTrue(RegistrationTasksPage.isRegistrationTasksPageLoaded(),"Verify if registration tasks page is loaded");
		SelectLanguagePage selectLanguagePage=RegistrationTasksPage.clickOnNewRegistrationButton();

		assertTrue(selectLanguagePage.isSelectLanguagePageLoaded(),"Verify if select language page  is loaded");
		selectLanguagePage.clickOnSubmitButtonWithoutSelectingLanguage();

		assertTrue(selectLanguagePage.isSelectLanguagePageLoaded(),"Verify if user should not be allow to navigate to next screen.");
		selectLanguagePage.selectSecondLanguage(TestDataReader.readData("secondLanguage"));

		assertTrue(selectLanguagePage.isNotificationLanguageEnglishDisplayed(),"verify if the notification language display in english");
		selectLanguagePage.selectNotificationlanguage(TestDataReader.readData("notificationLanguage"));

		assertTrue(selectLanguagePage.isSubmitButtonEnabled(),"verify if the submit  button enabled");
		ConsentPage consentPage=selectLanguagePage.clickOnSubmitButton();

		assertTrue(consentPage.isConsentPageDisplayed(),"Verify if Consent page is loaded");
		consentPage.selectTermAndConditionCheckbox();

		assertTrue(consentPage.isInformedButtonEnabled(),"Verify if informed  button enabled");
		DemographicDetailsPage demographicPage=consentPage.clickOnInformedButton();


		assertTrue(demographicPage.isDemographicDetailsPageDisplayed(),"Verify if demographic details page is displayed");
		demographicPage.enterFullName(TestDataReader.readData("fullname"));

		assertTrue(demographicPage.checkFullNameSecondLanguageTextBoxNotNull(),"Verify if first name is enter in second language text box");
		demographicPage.enterAge(TestDataReader.readData("minorAge"));
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

		assertTrue(demographicPage.isIntroducerNameHeaderDisplayed(),"Verify if introducer name header is displayed");
		demographicPage.enterIntroducerName(TestDataReader.readData("fullname"));

		assertTrue(demographicPage.isIntroducerRidHeaderDisplayed(),"Verify if introducer rid header is displayed");
		demographicPage.enterIntroducerRid(TestDataReader.readData("RID"));
		DocumentuploadPage documentuploadPage=demographicPage.clickOnContinueButton();

		assertTrue(documentuploadPage.isDoccumentUploadPageDisplayed(),"Verify if doccumentupload page is displayed");
		documentuploadPage.selectAddressProof();
		documentuploadPage.closePopUpClose();

		assertTrue(documentuploadPage.isScanButtonAddressProofEnabled(),"Verify if scan  button enabled");
		CameraPage cameraPage=documentuploadPage.clickOnAddressProofScanButton();

		cameraPage.clickimage();
		IdentityProofPage identityProofPage=cameraPage.clickOkButton();
	

		assertTrue(identityProofPage.isRetakeButtonDisplayed(),"Verify if retake  button displayed");
		identityProofPage.cropCaptureImage();
		documentuploadPage=identityProofPage.clickOnSaveButton();

		assertTrue(documentuploadPage.isDoccumentUploadPageDisplayed(),"Verify if doccumentupload page is displayed");
		documentuploadPage.selectIdentityProof();
		documentuploadPage.closePopUpClose();

		assertTrue(documentuploadPage.isScanButtonIdentityProofEnabled(),"Verify if scan  button enabled");
		cameraPage=documentuploadPage.clickOnScanButtonIdentityProof();

		cameraPage.clickimage();
		identityProofPage=cameraPage.clickOkButton();

		assertTrue(identityProofPage.isRetakeButtonDisplayed(),"Verify if retake  button displayed");
		identityProofPage.cropCaptureImage();
		documentuploadPage=identityProofPage.clickOnSaveButton();

		assertTrue(documentuploadPage.isDeleteButtonDisplyed(),"Verify if delete button is displayed");
		documentuploadPage.selectRelationshipProof();
		documentuploadPage.closePopUpClose();

		assertTrue(documentuploadPage.isScanButtonRelationshipProoffEnabled(),"Verify if scan  button enabled");
		cameraPage=documentuploadPage.clickOnScanButtonRelationshipProof();

		cameraPage.clickimage();
		identityProofPage=cameraPage.clickOkButton();

		assertTrue(identityProofPage.isRetakeButtonDisplayed(),"Verify if retake  button displayed");
		identityProofPage.cropCaptureImage();
		documentuploadPage=identityProofPage.clickOnSaveButton();

		assertTrue(documentuploadPage.isDeleteButtonDisplyed(),"Verify if delete button is displayed");
		documentuploadPage.selectDobProof();
		documentuploadPage.closePopUpClose();

		assertTrue(documentuploadPage.isDobHeaderDisplayed(),"Verify if DOB header is displayed");
		cameraPage=documentuploadPage.clickOnScanButtonDobProof();

		cameraPage.clickimage();
		identityProofPage=cameraPage.clickOkButton();

		assertTrue(identityProofPage.isRetakeButtonDisplayed(),"Verify if retake  button displayed");
		identityProofPage.cropCaptureImage();
		documentuploadPage=identityProofPage.clickOnSaveButton();
		BiometricDetailsPage biometricDetailsPage=documentuploadPage.clickOnContinueButton();

		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if biometric details page is displayed");
		ApplicantBiometricsPage applicantBiometricsPage=biometricDetailsPage.clickOnIrisScan();

		assertTrue(applicantBiometricsPage.isApplicantBiometricsPageDisplyed(),"Verify if applicant biometric page is displayed");
		applicantBiometricsPage.clickOnScanButton();

		assertTrue(applicantBiometricsPage.isIrisScan(),"Verify if iris scan 1st attempt");
		applicantBiometricsPage.closeScanCapturePopUp();
		applicantBiometricsPage.clickOnNextButton();
		//righthand
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
		applicantBiometricsPage.clickOnNextButton();

		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if biometric details page is displayed");
		IntroducerBiometricPage introducerBiometricPage=biometricDetailsPage.clickOnIntroducerIrisScan();

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

		PreviewPage previewPage=biometricDetailsPage.clickOnContinueButton();
		
		assertTrue(previewPage.isNewRegistrationTitleDisplayed(),"Verify if new Registration title is displayed");
		assertTrue(previewPage.isDemographicInformationInPreviewPageDisplayed(),"Verify if Demographic Information In PreviewPage is displayed");
		assertTrue(previewPage.isBiometricsInformationInPreviewPagePageDisplayed(),"Verify if Biometrics Information In PreviewPage is displayed");
		assertTrue(previewPage.isDocumentsInformationInPreviewPageDisplayed(),"Verify if Documents Information In PreviewPage is displayed");
		AuthenticationPage authenticationPage=previewPage.clickOnContinueButton();

		assertTrue(authenticationPage.isAuthenticationPageDisplayed(),"Verify if authentication details page is displayed");
		authenticationPage.enterUserName(TestDataReader.readData("username"));
		authenticationPage.enterPassword(TestDataReader.readData("password"));
		AcknowledgementPage acknowledgementPage=authenticationPage.clickOnAuthenticatenButton();

		assertTrue(acknowledgementPage.isAcknowledgementPageDisplayed(),"Verify if acknowledgement details page is displayed");
		assertTrue(acknowledgementPage.isDemographicInformationInPreviewPageDisplayed(),"Verify if Demographic Information In authenticationPage is displayed");
		assertTrue(acknowledgementPage.isBiometricsInformationInPreviewPagePageDisplayed(),"Verify if Biometrics Information In authenticationPage is displayed");
		assertTrue(acknowledgementPage.isDocumentsInformationInPreviewPageDisplayed(),"Verify if Documents Information In authenticationPage is displayed");
		assertTrue(acknowledgementPage.isQrCodeImageDisplayed(),"Verify if qr code image  is displayed");
		selectLanguagePage=acknowledgementPage.clickOnNewRegistrationButton();

		assertTrue(selectLanguagePage.isSelectLanguagePageLoaded(),"Verify if select language page  is loaded");

	}

}
