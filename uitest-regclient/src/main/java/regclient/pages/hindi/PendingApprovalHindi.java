/**
 * 
 */
package regclient.pages.hindi;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.PendingApproval;

public class PendingApprovalHindi extends PendingApproval{

	@AndroidFindBy(accessibility = "लंबित अनुमोदन")
	private WebElement pendingApprovalTitle;

	@AndroidFindBy(accessibility = "मंज़ूरी देना")
	private WebElement approveButton;

	@AndroidFindBy(accessibility = "स्क्रिम")
	private WebElement backGroundScreen;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.CheckBox\").instance(0)")
	private WebElement searchCheckBoxButton;

	@AndroidFindBy(accessibility = "जमा करना")
	private WebElement submitButton;

	@AndroidFindBy(accessibility = "पर्यवेक्षक का प्रमाणीकरण")
	private WebElement supervisorAuthenticationTitle;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(0)")
	private WebElement userNameTextBox;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(1)")
	private WebElement passwordTextBox;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc,\"लंबित अनुमोदन\")]//preceding-sibling::android.widget.Button")
	private WebElement backButton;

	public PendingApprovalHindi(AppiumDriver driver) {
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

}
