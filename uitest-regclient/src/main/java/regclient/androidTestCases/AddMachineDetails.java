package regclient.androidTestCases;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.testng.annotations.Test;

import regclient.BaseTest.AndroidBaseTest;
import regclient.api.AdminTestUtil;
import regclient.api.ConfigManager;
import regclient.api.KeycloakUserManager;
import regclient.page.BasePage;
import regclient.pages.english.LoginPageEnglish;


public class AddMachineDetails extends AndroidBaseTest{
	
	@Test
	public void addMachineDetails() throws UnsupportedFlavorException, IOException {
		
		BasePage.disableAutoRotation();
		LoginPageEnglish   loginPage= new LoginPageEnglish(driver);
		assertTrue(loginPage.isWelcomeMessageInSelectedLanguageDisplayed(), "verify if the welcome msg in selected language displayed");
		loginPage.enterUserName(KeycloakUserManager.moduleSpecificUser);
		loginPage.clickOnNextButton();

		loginPage.enterPassword(ConfigManager.getIAMUsersPassword());
		loginPage.clickOnloginButton();
		
//		assertTrue(loginPage.isMachineNotFoundMessageDisplayed(), "verify if the machine not found message displayed");
		loginPage.clickandHold();
		loginPage.clickOnCopyTextButton();
		loginPage.getMachineDetails();
		String id=AdminTestUtil.creteaMachine(BasePage.getSignPublicKey(),BasePage.getPublicKey(),BasePage.getName());
		String responce=AdminTestUtil.activateMachine(id);
		assertEquals(responce, "Status updated successfully for machine","verify if machine is created or not ");

	}

}
