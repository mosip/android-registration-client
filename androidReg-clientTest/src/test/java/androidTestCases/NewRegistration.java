package androidTestCases;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import BaseTest.AndroidBaseTest;
import regclient.pages.AddressAndContactPage;
import regclient.pages.CameraPage;
import regclient.pages.ConsentPage;
import regclient.pages.DemographicPage;
import regclient.pages.DocumentuploadPage;
import regclient.pages.IntroducerDetails;
import regclient.pages.LoginPage;
import regclient.pages.RegistrationTasksPage;
import regclient.pages.SelectLanguagePage;
import regclient.utils.TestDataReader;

public class NewRegistration extends AndroidBaseTest {

	@Test
	public void NewRegistration() {
		LoginPage loginPage=new LoginPage(driver);

		loginPage.enterusername(TestDataReader.readData("username"));
		loginPage.clickOnNextButton();

		
		loginPage.enterpassword(TestDataReader.readData("password"));
		RegistrationTasksPage RegistrationTasksPage=loginPage.clickOnloginButton();

		assertTrue(RegistrationTasksPage.isRegistrationTasksPageLoaded(),"Verify if registration tasks page is loaded");
		SelectLanguagePage selectLanguagePage=RegistrationTasksPage.clickOnNewRegistration();
		
		
		assertTrue(selectLanguagePage.isSelectLanguagePageLoaded(),"Verify if select language page  is loaded");
		selectLanguagePage.clickOnSubmitButtonWithoutSelectingLanguage();
		
		assertTrue(selectLanguagePage.isSelectLanguagePageLoaded(),"Verify if user should not be allow to navigate to next screen.");
		selectLanguagePage.selectArabiclanguage();
		
		assertTrue(selectLanguagePage.isNotificationLanguageEnglishArabicDisplay(),"verify if the notification language display in arabic and english");
		assertTrue(selectLanguagePage.isArabicLanguageButtonEnable(),"verify if the Arabic  button enable");
		selectLanguagePage.selectNotificationlanguage();
		
		assertTrue(selectLanguagePage.isSubmitButtonEnable(),"verify if the submit  button enable");
		ConsentPage consentPage=selectLanguagePage.clickOnSubmit();
		
		assertTrue(consentPage.isConsentpageDisplay(),"Verify if Consent page is loaded");
		//consentPage.clickOnCheckBoxButton();
		
		assertTrue(consentPage.isInformedButtonEnable(),"Verify if informed  button enable");
		DemographicPage demographicPage=consentPage.clickOnInformedButton();
		
		assertTrue(demographicPage.isDemographicDetailsPageDisplay(),"Verify if demographic details page is display");
		demographicPage.enterFirstName(TestDataReader.readData("firstname"));
		
		assertTrue(demographicPage.getenterfirstNameTextBoxSecondlang(),"Verify if first name is enter in second language text box");
		demographicPage.enterLastName(TestDataReader.readData("lastname"));
		
		assertTrue(demographicPage.getenterLastNameTextBoxSecondlang(),"Verify if last name is enter in second language text box");
		demographicPage.enterAge(TestDataReader.readData("adultage"));
		demographicPage.SelectGender(TestDataReader.readData("gender"));
		//demographicPage.SelectMaritalStatus();
		AddressAndContactPage addressAndContactPage=demographicPage.clickOnContinueButton();
		
		assertTrue(addressAndContactPage.isAddressAndContactPageTitleDisplay(),"Verify if Address and Contact page is display");
		addressAndContactPage.enterMobileNumber(TestDataReader.readData("mobileNumber"));
		addressAndContactPage.enterEmailID(TestDataReader.readData("emailId"));
		IntroducerDetails introducerDetails=addressAndContactPage.clickOnContinueButton();
		DocumentuploadPage documentuploadPage=introducerDetails.clickOnContinueButton();
		
		assertTrue(documentuploadPage.isDoccumentUploadPageDisplay(),"Verify if doccumentupload page is display");
		documentuploadPage.selectIdentityProof();
		
		assertTrue(documentuploadPage.isScanButtonEnable(),"Verify if scan  button enable");
		CameraPage cameraPage=documentuploadPage.clickOnScanButton();
		
		
		
		
		
		
		
		
		
		
		
	}
}
