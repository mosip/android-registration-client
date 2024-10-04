package regclient.pages.tamil;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.ManageApplicationsPage;

public class ManageApplicationsPageTamil extends ManageApplicationsPage{

	@AndroidFindBy(accessibility = "Manage Applications")
	private WebElement manageApplicationsTitle;

	@AndroidFindBy(xpath = "//android.widget.EditText")
	private WebElement ApplicationIDTextBox;

	@AndroidFindBy(accessibility = "UPLOAD")
	private WebElement uploadButton;

	@AndroidFindBy(accessibility = "Client Status")
	private WebElement clientStatusDropdown;

	@AndroidFindBy(accessibility = "Server Status")
	private WebElement serverStatusDropdown;

	@AndroidFindBy(accessibility = "EXPORT")
	private WebElement exportButton;

	@AndroidFindBy(accessibility = "Created")
	private WebElement createdOption;

	@AndroidFindBy(accessibility = "Approved")
	private WebElement approvedOption;

	@AndroidFindBy(accessibility = "Rejected")
	private WebElement rejectedOption;

	@AndroidFindBy(accessibility = "Synced")
	private WebElement syncedOption;

	@AndroidFindBy(accessibility = "Uploaded")
	private WebElement uploadedOption;

	@AndroidFindBy(accessibility = "Exported")
	private WebElement exportedsOption;
	
	@AndroidFindBy(accessibility = "Dismiss")
	private WebElement dismissButton;
	
	@AndroidFindBy(accessibility = "Displaying 0 Applications")
	private WebElement displayZeroApplication;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.CheckBox\").instance(0)")
	private WebElement searchCheckBoxButton;
	
	@AndroidFindBy(accessibility = "Received")
	private WebElement receivedValueDropdown;
	
	@AndroidFindBy(accessibility = "Processing")
	private WebElement processingValueDropdown;
	
	@AndroidFindBy(accessibility = "Accepted")
	private WebElement acceptedValueDropdown;
	
	@AndroidFindBy(accessibility = "Dismiss")
	private WebElement deletionValueDropdown;

	public ManageApplicationsPageTamil(AppiumDriver driver) {
		super(driver);
	}

	public boolean isManageApplicationPageDisplayed() {
		return isElementDisplayed(manageApplicationsTitle);
	}

	public  void enterAID(String AID) {
		clickAndsendKeysToTextBox(ApplicationIDTextBox,AID);
	}

	public  void enterWrongAID(String AID) {
		clickAndsendKeysToTextBox(ApplicationIDTextBox,AID);
	} 

	public boolean isSearchAIDDisplayed(String AID) {
		waitTime(2);
		return isElementDisplayed(driver.findElement(By.xpath("//android.view.View[contains(@content-desc,'"+AID+"')]")));
	}
	
	public boolean isZeroApplicationDisplayed() {
		waitTime(2);
		return isElementDisplayed(displayZeroApplication);
	}

	public  void clickOnUploadButton() {
		clickOnElement(uploadButton);
		waitTime(20);
	}

	public boolean isPacketUploadDone(String AID) {
		waitTime(2);
		WebElement element =driver.findElement(By.xpath("//android.view.View[contains(@content-desc,'"+AID+"')]"));
		if(element.getAttribute("contentDescription").contains("NOT UPLOADED"))
			return false;
		else
			return true;
	}
	
	public boolean isPacketApproved(String AID) {
		waitTime(2);
		WebElement element =driver.findElement(By.xpath("//android.view.View[contains(@content-desc,'"+AID+"')]"));
		if(element.getAttribute("contentDescription").contains("APPROVED"))
			return true;
		else
			return false;
	}
	
	public boolean isPacketSynned(String AID) {
		waitTime(2);
		WebElement element =driver.findElement(By.xpath("//android.view.View[contains(@content-desc,'"+AID+"')]"));
		if(element.getAttribute("contentDescription").contains("SYNCED"))
			return true;
		else
			return false;
	}

	public  void clickClientStatusDropdown() {
		clickOnElement(clientStatusDropdown);
	}

	public  void clickServerStatusDropdown() {
		clickOnElement(serverStatusDropdown);
	}

	public boolean isCreatedDropdownOptionDisplayed() {
		return isElementDisplayed(createdOption);
	}

	public boolean isApprovedDropdownOptionDisplayed() {
		return isElementDisplayed(approvedOption);
	}

	public boolean isRejectedDropdownOptionDisplayed() {
		return isElementDisplayed(rejectedOption);
	}

	public boolean isSyncedDropdownOptionDisplayed() {
		return isElementDisplayed(syncedOption);
	}

	public boolean isUploadedDropdownOptionDisplayed() {
		return isElementDisplayed(uploadedOption);
	}

	public boolean isExportedsDropdownOptionDisplayed() {
		return isElementDisplayed(exportedsOption);
	}

	public  void clickDismissButton() {
		clickOnElement(dismissButton);
	}

	public  void clickOnSearchCheckBox() {
		clickOnElement(searchCheckBoxButton);
	}
	
	public  void selectApprovedValueDropdown() {
		clickOnElement(clientStatusDropdown);
		clickOnElement(approvedOption);
	}	
	
	public  void selectSyncedOptionDropdown() {
		clickOnElement(clientStatusDropdown);
		clickOnElement(syncedOption);
	}
	
	public  void selectUploadedOptionDropdown() {
		clickOnElement(clientStatusDropdown);
		clickOnElement(uploadedOption);
		
	}

	public boolean isReceivedDropdownOptionDisplayed() {
		return isElementDisplayed(receivedValueDropdown);
	}

	public boolean isProcessingDropdownOptionDisplayed() {
		return isElementDisplayed(processingValueDropdown);
	}

	public boolean isAcceptedDropdownOptionDisplayed() {
		return isElementDisplayed(acceptedValueDropdown);
	}

	public boolean isDeletionDropdownOptionDisplayed() {
		return isElementDisplayed(deletionValueDropdown);
	}
	
	public void clickOnBackButton() {
		driver.navigate().back();
	}

}
