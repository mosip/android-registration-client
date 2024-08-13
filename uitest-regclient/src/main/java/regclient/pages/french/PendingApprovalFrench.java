/**
 * 
 */
package regclient.pages.french;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.PendingApproval;

public class PendingApprovalFrench extends PendingApproval{

	@AndroidFindBy(accessibility = "En attente de validation")
	private WebElement pendingApprovalTitle;

	@AndroidFindBy(accessibility = "APPROUVER")
	private WebElement approveButton;

	@AndroidFindBy(accessibility = "Fond")
	private WebElement backGroundScreen;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.CheckBox\").instance(0)")
	private WebElement searchCheckBoxButton;

	@AndroidFindBy(accessibility = "SOUMETTRE")
	private WebElement submitButton;

	@AndroidFindBy(accessibility = "Authentification du superviseur")
	private WebElement supervisorAuthenticationTitle;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(0)")
	private WebElement userNameTextBox;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(1)")
	private WebElement passwordTextBox;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc,\"En attente de validation\")]//preceding-sibling::android.widget.Button")
	private WebElement backButton;

	public PendingApprovalFrench(AppiumDriver driver) {
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
