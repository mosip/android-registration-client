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
import regclient.pages.LoginPage;
import regclient.pages.PreviewPage;
import regclient.pages.RegistrationTasksPage;
import regclient.pages.SelectLanguagePage;
import regclient.utils.TestDataReader;

public class NewRegistrationAdultException extends AndroidBaseTest {

	@Test
	public void newRegistrationAdultException(){
		LoginPage loginPage=new LoginPage(driver);

		loginPage.enterUserName(TestDataReader.readData("username"));
		loginPage.clickOnNextButton();

		loginPage.enterPassword(TestDataReader.readData("password"));
		RegistrationTasksPage RegistrationTasksPage=loginPage.clickOnloginButton();

		assertTrue(RegistrationTasksPage.isRegistrationTasksPageLoaded(),"Verify if registration tasks page is loaded");
		SelectLanguagePage selectLanguagePage=RegistrationTasksPage.clickOnNewRegistrationButton();

		assertTrue(selectLanguagePage.isSelectLanguagePageLoaded(),"Verify if select language page  is loaded");
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
		consentPage=demographicPage.clickOnConsentPageTitle();

		assertTrue(consentPage.isConsentPageDisplayed(),"Verify if Consent page is loaded");
		demographicPage=consentPage.clickOnInformedButton();

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

		assertTrue(demographicPage.isEmailHeaderDisplayed(),"Verify if email header is displayed");
		assertTrue(demographicPage.isCityHeaderDisplayed(),"Verify if city header is displayed");
		demographicPage.selectCityStatus(TestDataReader.readData("city"));

		assertTrue(demographicPage.isZoneHeaderDisplayed(),"Verify if zone header is displayed");
		demographicPage.selectZoneStatus();

		assertTrue(demographicPage.isPostalCodeHeaderDisplayed(),"Verify if postal code header is displayed");
		demographicPage.selectPostalStatus();

		assertTrue(demographicPage.isMobileNumberHeaderDisplayed(),"Verify if mobile number header is displayed");
		demographicPage.enterMobileNumber(TestDataReader.readData("mobileNumber"));


		demographicPage.enterEmailID(TestDataReader.readData("emailId"));
		DocumentuploadPage documentuploadPage=demographicPage.clickOnContinueButton();


		assertTrue(documentuploadPage.isDoccumentUploadPageDisplayed(),"Verify if doccumentupload page is displayed");
		documentuploadPage.selectAddressProof();
		documentuploadPage.closePopUpClose();

		assertTrue(documentuploadPage.isScanButtonEnabled(),"Verify if scan  button enabled");
		CameraPage cameraPage=documentuploadPage.clickOnScanButton();

		cameraPage.clickimage();
		IdentityProofPage identityProofPage=cameraPage.clickOkButton();

		assertTrue(identityProofPage.isRetakeButtonDisplayed(),"Verify if retake  button displayed");
		identityProofPage.cropCaptureImage();
		documentuploadPage=identityProofPage.clickOnSaveButton();

		assertTrue(documentuploadPage.isDoccumentUploadPageDisplayed(),"Verify if doccumentupload page is displayed");
		documentuploadPage.selectOnCaptureImage();

		assertTrue(documentuploadPage.isImageDisplyed(),"Verify if capture image is displayed");
		documentuploadPage.clickOnBackButton();

		assertTrue(documentuploadPage.isDeleteButtonDisplyed(),"Verify if delete button is displayed");
		documentuploadPage.selectIndentityProof();
		documentuploadPage.closePopUpClose();

		assertTrue(documentuploadPage.isDobHeaderDisplayed(),"Verify if DOB header is displayed");
		assertTrue(documentuploadPage.isScanButtonEnabled(),"Verify if scan  button enabled");
		cameraPage=documentuploadPage.clickOnScanButton();

		cameraPage.clickimage();
		identityProofPage=cameraPage.clickOkButton();

		assertTrue(identityProofPage.isRetakeButtonDisplayed(),"Verify if retake  button displayed");
		identityProofPage.cropCaptureImage();
		documentuploadPage=identityProofPage.clickOnSaveButton();

		assertTrue(documentuploadPage.isDeleteButtonDisplyed(),"Verify if delete button is displayed");
		documentuploadPage.selectDobProof();
		documentuploadPage.closePopUpClose();

		assertTrue(documentuploadPage.isDobHeaderDisplayed(),"Verify if DOB header is displayed");
		cameraPage=documentuploadPage.clickOnDobScanButton();

		cameraPage.clickimage();
		identityProofPage=cameraPage.clickOkButton();

		assertTrue(identityProofPage.isRetakeButtonDisplayed(),"Verify if retake  button displayed");
		identityProofPage.cropCaptureImage();
		documentuploadPage=identityProofPage.clickOnSaveButton();
		BiometricDetailsPage biometricDetailsPage=documentuploadPage.clickOnContinueButton();

		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if biometric details page is displayed");
		biometricDetailsPage.clickOnContinueButton();

		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if user is not navigated to the next page without biometric scan");
		ApplicantBiometricsPage applicantBiometricsPage=biometricDetailsPage.clickOnIrisScan();

		assertTrue(applicantBiometricsPage.isApplicantBiometricsPageDisplyed(),"Verify if applicant biometric page is displayed");
		applicantBiometricsPage.clickOnMarkExceptionButton();

		assertTrue(applicantBiometricsPage.isExceptionTypeTitleDisplyed(),"Verify if applicant biometric mark exception is displayed");
		applicantBiometricsPage.markOneEyeException();
		applicantBiometricsPage.clickOnIrisScanButton();
		applicantBiometricsPage.clickOnScanButton();

		assertTrue(applicantBiometricsPage.isIrisScan(),"Verify if iris scan 1st attempt");
		applicantBiometricsPage.CloseScanCapturePopUp();
		applicantBiometricsPage.clickOnNextButton();
		//righthand
		assertTrue(applicantBiometricsPage.isRightHandScanTitleDisplyed(),"Verify if right hand scan is displayed");
		applicantBiometricsPage.clickOnMarkExceptionButton();

		assertTrue(applicantBiometricsPage.isExceptionTypeTitleDisplyed(),"Verify if applicant biometric mark exception is displayed");
		applicantBiometricsPage.clickOnZoomButton();

		assertTrue(applicantBiometricsPage.isRightHandScanTitleDisplyed(),"Verify if applicant right hand scan is displayed");
		applicantBiometricsPage.markOneFingureException();
		applicantBiometricsPage.clickOnClosePopUp();

		assertTrue(applicantBiometricsPage.isExceptionTypeTitleDisplyed(),"Verify if applicant biometric mark exception is displayed");
		applicantBiometricsPage.clickOnRightHandScanTitle();
		applicantBiometricsPage.clickOnScanButton();

		assertTrue(applicantBiometricsPage.isRightHandScan(),"Verify if right hand scan 1st attempt");
		applicantBiometricsPage.CloseScanCapturePopUp();
		applicantBiometricsPage.clickOnNextButton();
		//lefthand
		assertTrue(applicantBiometricsPage.isLeftHandScanTitleDisplyed(),"Verify if applicant left hand scan title is displayed");
		applicantBiometricsPage.clickOnMarkExceptionButton();

		assertTrue(applicantBiometricsPage.isExceptionTypeTitleDisplyed(),"Verify if applicant biometric mark exception is displayed");
		applicantBiometricsPage.clickOnZoomButton();

		assertTrue(applicantBiometricsPage.isLeftHandScanTitleDisplyed(),"Verify if applicant right hand scan is displayed");
		applicantBiometricsPage.markOneFingureException();
		applicantBiometricsPage.clickOnClosePopUp();

		assertTrue(applicantBiometricsPage.isExceptionTypeTitleDisplyed(),"Verify if applicant biometric mark exception is displayed");
		applicantBiometricsPage.clickOnleftHandScanTitle();
		applicantBiometricsPage.clickOnScanButton();

		assertTrue(applicantBiometricsPage.isLeftHandScan(),"Verify if Left hand scan 1st attempt");
		applicantBiometricsPage.CloseScanCapturePopUp();
		biometricDetailsPage=applicantBiometricsPage.clickOnBackButton();
		//thumb
		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if biometric details page is displayed");
		biometricDetailsPage.clickOnContinueButton();

		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if user is not navigated to the next page without biometric scan");
		applicantBiometricsPage=biometricDetailsPage.clickOnThumbsScanIcon();

		assertTrue(applicantBiometricsPage.isThumbsScanTitleDisplyed(),"Verify if thumbs scan page is displayed");
		applicantBiometricsPage.clickOnMarkExceptionButton();

		assertTrue(applicantBiometricsPage.isExceptionTypeTitleDisplyed(),"Verify if applicant biometric mark exception is displayed");
		applicantBiometricsPage.markOneFingureException();
		applicantBiometricsPage.clickOnThumbsScanTitle();
		applicantBiometricsPage.clickOnScanButton();

		assertTrue(applicantBiometricsPage.isThumbsScan(),"Verify if thumbs scan 1st attempt");
		applicantBiometricsPage.CloseScanCapturePopUp();
		applicantBiometricsPage.clickOnNextButton();
		//face
		assertTrue(applicantBiometricsPage.isFaceScanTitleDisplyed(),"Verify if face scan page is displayed");
		applicantBiometricsPage.clickOnScanButton();

		assertTrue(applicantBiometricsPage.isFaceScan(),"Verify if face scan 1st attempt");
		applicantBiometricsPage.CloseScanCapturePopUp();
		applicantBiometricsPage.clickOnNextButton();
		//Exception
		assertTrue(applicantBiometricsPage.isExceptionScanTitleDisplyed(),"Verify if exception scan page is displayed");
		applicantBiometricsPage.clickOnScanButton();

		assertTrue(applicantBiometricsPage.isExceptionScan(),"Verify if exception scan 1st attempt");
		applicantBiometricsPage.CloseScanCapturePopUp();
		biometricDetailsPage=applicantBiometricsPage.clickOnNextButton();
		
		assertTrue(biometricDetailsPage.isBiometricDetailsPageDisplayed(),"Verify if biometric details page is displayed");
		PreviewPage previewPage=biometricDetailsPage.clickOnContinueButton();

		assertTrue(previewPage.isDemographicInformationInPreviewPageDisplayed(),"Verify if Demographic Information In PreviewPage is displayed");
		assertTrue(previewPage.isBiometricsInformationInPreviewPagePageDisplayed(),"Verify if Biometrics Information In PreviewPage is displayed");
		assertTrue(previewPage.isDocumentsInformationInPreviewPageDisplayed(),"Verify if Documents Information In PreviewPage is displayed");
		AuthenticationPage authenticationPage=previewPage.clickOnContinueButton();

		assertTrue(authenticationPage.isAuthenticationPageDisplayed(),"Verify if authentication details page is displayed");
		authenticationPage.enterUserName(TestDataReader.readData("username"));
		authenticationPage.enterPassword(TestDataReader.readData("password"));
		AcknowledgementPage acknowledgementPage=authenticationPage.clickOnAuthenticatenButton();

		assertTrue(acknowledgementPage.isAcknowledgementPageDisplayed(),"Verify if acknowledgement details page is displayed");
		assertTrue(acknowledgementPage.isQrCodeImageDisplayed(),"Verify if qr code image  is displayed");
		selectLanguagePage=acknowledgementPage.clickOnNewRegistrationButton();

		assertTrue(selectLanguagePage.isSelectLanguagePageLoaded(),"Verify if select language page  is loaded");

	}
}
