package regclient.androidTestCases;

import static org.testng.Assert.assertEquals;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.testng.annotations.Test;

import regclient.BaseTest.AndroidBaseTest;
import regclient.api.AdminTestUtil;
import regclient.page.BasePage;
import regclient.pages.english.LoginPageEnglish;


public class AddMachineDetails extends AndroidBaseTest{
	
	@Test
	public void addMachineDetails() throws UnsupportedFlavorException, IOException {
		
		BasePage.disableAutoRotation();
		LoginPageEnglish   loginPage= new LoginPageEnglish(driver);
		loginPage.clickandHold();
		loginPage.clickOnCopyTextButton();
		loginPage.getMachineDetails();
		String id=AdminTestUtil.creteaMachine(BasePage.getSignPublicKey(),BasePage.getPublicKey(),BasePage.getName());
		String responce=AdminTestUtil.activateMachine(id);
		assertEquals(responce, "Status updated successfully for machine","verify if machine is created or not ");

	}

}
