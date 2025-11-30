package regclient.pages.arabic;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.ManageApplicationsPage;

public class ManageApplicationsPageArabic extends ManageApplicationsPage {

	@AndroidFindBy(accessibility = "إدارة التطبيقات")
	private WebElement manageApplicationsTitle;

	@AndroidFindBy(xpath = "//android.widget.EditText")
	private WebElement ApplicationIDTextBox;

	@AndroidFindBy(accessibility = "تحميل")
	private WebElement uploadButton;

	@AndroidFindBy(accessibility = "حالة العميل")
	private WebElement clientStatusDropdown;

	@AndroidFindBy(accessibility = "حالة الخادم")
	private WebElement serverStatusDropdown;

	@AndroidFindBy(accessibility = "تصدير")
	private WebElement exportButton;

	@AndroidFindBy(accessibility = "تم الإنشاء")
	private WebElement createdOption;

	@AndroidFindBy(accessibility = "تمت الموافقة")
	private WebElement approvedOption;

	@AndroidFindBy(accessibility = "مرفوض")
	private WebElement rejectedOption;

	@AndroidFindBy(accessibility = "تمت المزامنة")
	private WebElement syncedOption;

	@AndroidFindBy(accessibility = "تم التحميل")
	private WebElement uploadedOption;

	@AndroidFindBy(accessibility = "تم التصدير")
	private WebElement exportedsOption;

	@AndroidFindBy(accessibility = "تجاهل")
	private WebElement dismissButton;

	@AndroidFindBy(accessibility = "عرض 0 من الطلبات")
	private WebElement displayZeroApplication;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.CheckBox\").instance(0)")
	private WebElement searchCheckBoxButton;

	@AndroidFindBy(accessibility = "تم الاستلام")
	private WebElement receivedValueDropdown;

	@AndroidFindBy(accessibility = "قيد المعالجة")
	private WebElement processingValueDropdown;

	@AndroidFindBy(accessibility = "تم القبول")
	private WebElement acceptedValueDropdown;

	@AndroidFindBy(accessibility = "تجاهل")
	private WebElement deletionValueDropdown;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text='Documents'][2]")
	private WebElement documentsHeader;

	@AndroidFindBy(id = "android:id/button1")
	private WebElement useThisFolderButton;

	@AndroidFindBy(id = "android:id/button1")
	private WebElement allowButton;

	@AndroidFindBy(xpath = "//android.widget.TextView[@resource-id='android:id/title' and @text='Documents']")
	private WebElement documentsFolder;

	@AndroidFindBy(accessibility = "لم يتم العثور على شبكة!")
	private WebElement noNetworkFound;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.CheckBox\").instance(1)")
	private WebElement latestAidCheckBox;

	public ManageApplicationsPageArabic(AppiumDriver driver) {
		super(driver);
	}

	public boolean isManageApplicationPageDisplayed() {
		return isElementDisplayed(manageApplicationsTitle);
	}

	public void enterAID(String AID) {
		clickAndsendKeysToTextBox(ApplicationIDTextBox, AID);
	}

	public void enterWrongAID(String AID) {
		clickAndsendKeysToTextBox(ApplicationIDTextBox, AID);
	}

	public boolean isSearchAIDDisplayed(String AID) {
		waitTime(2);
		return isElementDisplayed(
				driver.findElement(By.xpath("//android.view.View[contains(@content-desc,'" + AID + "')]")));
	}

	public boolean isZeroApplicationDisplayed() {
		waitTime(2);
		return isElementDisplayed(displayZeroApplication);
	}

	public void clickOnUploadButton() {
		clickOnElement(uploadButton);
		waitTime(10);
	}

	public boolean isPacketUploadDone(String AID) {
		waitTime(2);
		WebElement element = driver.findElement(By.xpath("//android.view.View[contains(@content-desc,'" + AID + "')]"));
		if (element.getAttribute("contentDescription").contains("NOT UPLOADED")) {
			waitTime(10);
			element = driver.findElement(By.xpath("//android.view.View[contains(@content-desc,'" + AID + "')]"));
			if (element.getAttribute("contentDescription").contains("NOT UPLOADED"))
				return false;
			else
				return true;
		} else
			return true;
	}

	public boolean isPacketApproved(String AID) {
		waitTime(2);
		WebElement element = driver.findElement(By.xpath("//android.view.View[contains(@content-desc,'" + AID + "')]"));
		if (element.getAttribute("contentDescription").contains("APPROVED"))
			return true;
		else
			return false;
	}

	public boolean isPacketSynned(String AID) {
		waitTime(2);
		WebElement element = driver.findElement(By.xpath("//android.view.View[contains(@content-desc,'" + AID + "')]"));
		if (element.getAttribute("contentDescription").contains("SYNCED"))
			return true;
		else
			return false;
	}

	public boolean isPacketRejected(String AID) {
		waitTime(2);
		WebElement element = driver.findElement(By.xpath("//android.view.View[contains(@content-desc,'" + AID + "')]"));
		if (element.getAttribute("contentDescription").contains("REJECTED"))
			return true;
		else
			return false;
	}

	public void clickClientStatusDropdown() {
		clickOnElement(clientStatusDropdown);
	}

	public void clickServerStatusDropdown() {
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

	public void clickDismissButton() {
		clickOnElement(dismissButton);
	}

	public void clickOnSearchCheckBox() {
		clickOnElement(searchCheckBoxButton);
	}

	public void selectApprovedValueDropdown() {
		clickOnElement(clientStatusDropdown);
		clickOnElement(approvedOption);
	}

	public void selectSyncedOptionDropdown() {
		clickOnElement(clientStatusDropdown);
		clickOnElement(syncedOption);
	}

	public void selectUploadedOptionDropdown() {
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

	public void clickOnExportButton() {
		clickOnElement(exportButton);
		waitTime(10);
	}

	public void clickOnUseThisFolderButton() {
		if (isElementDisplayed(documentsHeader)) {
			clickOnElement(useThisFolderButton);
			if (isElementDisplayed(allowButton)) {
				clickOnElement(allowButton);
			}
		} else if (isElementDisplayed(documentsFolder)) {
			clickOnElement(documentsFolder);
			clickOnElement(useThisFolderButton);
			if (isElementDisplayed(allowButton)) {
				clickOnElement(allowButton);
			}
		} else {
			throw new RuntimeException("Documents folder or header not found on screen");
		}
	}

	public boolean isNoNetworkFoundDisplayed() {
		return isElementDisplayed(noNetworkFound);
	}

	public void selectLatestAidCheckBox() {
		clickOnElement(latestAidCheckBox);
	}

}
