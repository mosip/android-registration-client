/**
 * 
 */
package regclient.pages.kannada;

import static org.testng.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.PendingApproval;

public class PendingApprovalKannada extends PendingApproval{

	@AndroidFindBy(accessibility = "ಒಪ್ಪಿಗೆಗಾಗಿ ಕಾದಿರುವ")
	private WebElement pendingApprovalTitle;

	@AndroidFindBy(accessibility = "ಅನುಮೋದಿಸಿ")
	private WebElement approveButton;

	@AndroidFindBy(accessibility = "ಸ್ಕ್ರಿಮ್")
	private WebElement backGroundScreen;
	@AndroidFindBy(accessibility = "ಬಳಕೆದಾರ ಹೆಸರು ಅಮಾನ್ಯ!")
	private WebElement invalidUsernameMessage;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.CheckBox\").instance(0)")
	private WebElement searchCheckBoxButton;

	@AndroidFindBy(accessibility = "ಸಲ್ಲಿಸು")
	private WebElement submitButton;

	@AndroidFindBy(accessibility = "ಮೇಲ್ವಿಚಾರಕರ ದೃಢೀಕರಣ")
	private WebElement supervisorAuthenticationTitle;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(0)")
	private WebElement userNameTextBox;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(1)")
	private WebElement passwordTextBox;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc,\"ಒಪ್ಪಿಗೆಗಾಗಿ ಕಾದಿರುವ\")]//preceding-sibling::android.widget.Button")
	private WebElement backButton;

	@AndroidFindBy(accessibility = "REJECT")
	private WebElement rejectButton;
	
	@AndroidFindBy(accessibility = "Sl.no Application ID Reg. Date Client Status Review Status Operator ID")
	private WebElement pageAttributes ;
	
	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"CREATED\")")
	private WebElement clientStatus;
	
	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"Pending\").instance(1)")
	private WebElement reviewStatus;
	
	@AndroidFindBy(xpath = "//android.widget.EditText")
	private WebElement applicationIdTextbox;
	
	@AndroidFindBy(accessibility = "Reject Packet?")
	private WebElement rejectPacketTitle;
	
	@AndroidFindBy(accessibility = "Please select a value")
	private WebElement rejectReasonDropdown;
	
	public PendingApprovalKannada(AppiumDriver driver) {
		super(driver);
	}

	public boolean isPendingApprovalTitleDisplayed() {
		return isElementDisplayed(pendingApprovalTitle);		
	}

	@SuppressWarnings("deprecation")
	public void clickOnAID(String AID) {
		clickOnElement(findElementWithRetry(MobileBy.AccessibilityId(AID)));
	}

	public void clickOnApproveButton() {
		clickOnElement(approveButton);
	}

	public void clickOnClosePopUpButton() {
		clickOnElement(backGroundScreen);
	}

	public void clickOnCheckBox() {
		clickOnElement(searchCheckBoxButton);
	}

	public void clickOnSubmitButton() {
		clickOnElement(submitButton);
	}

	public boolean isSupervisorAuthenticationTitleDisplayed() {
		return isElementDisplayed(supervisorAuthenticationTitle);		
	}

	public  void enterUserName(String username) {
		sendKeysToTextBox(userNameTextBox,username);
	}

	public  void enterPassword(String password) {
		sendKeysToTextBox(passwordTextBox,password);
	}
	
	public void clickOnBackButton() {
		clickOnElement(backButton);
	}

	public boolean isApprovalButtonDisplayed() {
		return isElementDisplayed(approveButton);		
	}
	
	public boolean isRejectButtonDisplayed() {
		return isElementDisplayed(rejectButton);		
	}
	
	
	public boolean isPageAttributesDisplayed() {
		return isElementDisplayed(pageAttributes);		
	}
	
	public boolean isClientStatusDisplayed() {
		return isElementDisplayed(clientStatus);		
	}

	public boolean isReviewStatusDisplayed() {
		return isElementDisplayed(reviewStatus);		
	}
	
	public  void enterAID(String AID) {
		clickAndsendKeysToTextBox(applicationIdTextbox,AID);
	}
	
	public void clickOnRejectButton() {
		clickOnElement(rejectButton);
	}
	public boolean isInvalidUsernameMessageDisplayed() {
		return isElementDisplayed(invalidUsernameMessage);		
	}
	
	public boolean isRejectPacketTitleDisplayed() {
		return isElementDisplayed(rejectPacketTitle);		
	}
	
	
	public void selectRejectionReasonDropdown() {
		boolean isdisplayed =isElementDisplayed(rejectReasonDropdown);
		assertTrue(isdisplayed,"Verify if "+rejectReasonDropdown+" header is displayed");
		clickOnElement(rejectReasonDropdown);
		waitTime(2);
		if(!isElementDisplayed(rejectReasonDropdown)) {				
			clickOnElement(findElement(By.className("android.view.View")));
		}else {
			clickOnElement(rejectReasonDropdown);
			waitTime(2);
			clickOnElement(findElement(By.className("android.view.View")));
		}	
	}
	
	public boolean isSubmitButtonEnabled() {
		return isElementEnabled(submitButton);		
	}
}
