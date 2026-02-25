package regclient.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;

public class ExportPage extends BasePage {

	@FindBy(id = "com.android.permissioncontroller:id/permission_message")
	private WebElement accessConsent;

	@FindBy(id = "com.android.permissioncontroller:id/permission_allow_button")
	private WebElement accessAllowButton;

	@FindBy(id = "com.android.permissioncontroller:id/permission_deny_button")
	private WebElement accessDenyButton;

	@AndroidFindBy(accessibility = "New folder")
	private WebElement newFolderIcon;

	@AndroidFindBy(id = "com.google.android.documentsui:id/action_button")
	private WebElement createNewFolderBtn;

	@AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='android:id/text1']")
	private WebElement newfolderTextBox;

	@AndroidFindBy(id = "com.google.android.documentsui:id/alertTitle")
	private WebElement newfolderPopup;

	@AndroidFindBy(id = "android:id/button1")
	private WebElement okButton;

	@AndroidFindBy(id = "android:id/button1")
	private WebElement useThisFolderButton;

	@AndroidFindBy(id = "com.google.android.documentsui:id/alertTitle")
	private WebElement accessFolderAlertPopup;

	@AndroidFindBy(id = "android:id/button1")
	private WebElement allowFolderButton;

	@FindBy(xpath = "//android.widget.TextView[@resource-id='android:id/title' and @text='Documents']")
	private WebElement documentsFolder;

	@FindBy(xpath = "//android.widget.TextView[@resource-id='android:id/title' and @text='packets']")
	private WebElement packetsFolder;

	@FindBy(xpath = "//android.widget.TextView[@resource-id='android:id/title' and @text='PACKET_MANAGER_ACCOUNT']")
	private WebElement packetManagerAccountFolder;

	@FindBy(xpath = "(//android.widget.TextView[@text='PACKET_MANAGER_ACCOUNT'])[2]")
	private WebElement packetManagerTitle;
	
	@FindBy(xpath = "(//android.widget.TextView[@text=\"ExportPacket\"])[2]")
	private WebElement exportPacketTitle;

	public ExportPage(AppiumDriver driver) {
		super(driver);
	}

	public void handleAccessConsentIfPresent() {

		try {
			if (isElementDisplayed(accessConsent)) {
				clickOnElement(accessAllowButton);
				System.out.println("✅ Access consent displayed and clicked.");
			} else {
				System.out.println("ℹ️ Access consent not displayed. Skipping.");
			}
		} catch (Exception e) {
			System.out.println("ℹ️ Access consent not present. Continuing flow.");
		}
	}

	public void clickNewFolderButton() {
		try {
			if (isElementDisplayed(newFolderIcon)) {
				clickOnElement(newFolderIcon);
				System.out.println("Clicked New Folder icon.");
			} else if (isElementDisplayed(createNewFolderBtn)) {
				clickOnElement(createNewFolderBtn);
				System.out.println("Clicked CREATE NEW FOLDER button.");
			} else {
				System.out.println("Neither New Folder icon nor CREATE NEW FOLDER button displayed.");
			}
		} catch (Exception e) {
			System.out.println("Error while clicking New Folder: " + e.getMessage());
		}
	}

	public boolean isNewFolderPopupDisplayed() {
		return isElementDisplayed(newfolderPopup);
	}

	public void enterFolderName(String foldername) {
		clickAndsendKeysToTextBox(newfolderTextBox, foldername);
	}

	public void clickOnOkButton() {
		clickOnElement(okButton);
		driver.navigate().back();

	}

	public void selectFolderByName(String folderName) {

		By folderLocator = MobileBy.AndroidUIAutomator(
				"new UiSelector().className(\"android.widget.TextView\")" + ".text(\"" + folderName + "\")");

		click(folderLocator);
	}

	public void clickOnUseThisFolderButton() {
		clickOnElement(useThisFolderButton);
	}

	public void handleAllowFolderConsentIfPresent() {

		try {
			if (isElementDisplayed(accessFolderAlertPopup)) {
				clickOnElement(allowFolderButton);
				System.out.println("Access consent displayed and clicked.");
			} else {
				System.out.println("Access consent not displayed. Skipping.");
			}
		} catch (Exception e) {
			System.out.println("Access consent not present. Continuing flow.");
		}
	}

	public boolean isPacketManagerTitleDisplayed() {

		if (isElementDisplayed(packetManagerTitle)) {
			System.out.println("Already inside PACKET_MANAGER_ACCOUNT screen.");
			return true;
		}

		try {

			if (!isElementDisplayed(documentsFolder)) {
				scrollToText("Documents");
			}
			clickOnElement(documentsFolder);

			if (!isElementDisplayed(packetsFolder)) {
				scrollToText("packets");
			}
			clickOnElement(packetsFolder);

			if (!isElementDisplayed(packetManagerAccountFolder)) {
				scrollToText("PACKET_MANAGER_ACCOUNT");
			}
			clickOnElement(packetManagerAccountFolder);

		} catch (Exception e) {
			System.out.println("Navigation failed: " + e.getMessage());
			return false;
		}

		return isElementDisplayed(packetManagerTitle);
	}

	private void scrollToText(String text) {
		driver.findElement(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true))"
				+ ".scrollIntoView(new UiSelector().text(\"" + text + "\"))"));
	}
	
	public void exportPacketIntoFolder(String folderName) {
	    if (isExportPacketTitleDisplayed()) {
	        System.out.println(folderName + " folder already exists. Using it.");
	        clickOnUseThisFolderButton();
	        handleAllowFolderConsentIfPresent();
	    } else {
	        System.out.println(folderName + " folder not found. Creating new folder.");
	        clickNewFolderButton();
	        if (!isNewFolderPopupDisplayed()) {
	            throw new RuntimeException("New Folder popup not displayed");
	        }
	        enterFolderName(folderName);
	        clickOnOkButton();
	        selectFolderByName(folderName);
	        clickOnUseThisFolderButton();
	        handleAllowFolderConsentIfPresent();
	    }
	}
	
	public boolean isExportPacketTitleDisplayed() {
		return isElementDisplayed(exportPacketTitle);
	}
	
	public void exportPacketIntoFolderIfReady(String folderName) {

	    if (isPacketManagerTitleDisplayed()) {
	        System.out.println("Packet Manager page verified.");
	    } else {
	        System.out.println("Packet Manager title not visible, proceeding anyway.");
	    }

	    exportPacketIntoFolder(folderName);
	}

}
