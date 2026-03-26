package regclient.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	@AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.TextView\").text(\"ExportPacket\")")
	private WebElement exportPacketFolder;

	@AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.TextView\").text(\"Documents\")")
	private WebElement documentsTitleHeader;

	@AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.TextView\").text(\"packets\")")
	private WebElement packetsTitleHeader;

	public ExportPage(AppiumDriver driver) {
		super(driver);
	}

	private static final Logger logger = LoggerFactory.getLogger(ExportPage.class);

	public void handleAccessConsentIfPresent() {

		try {
			if (isElementDisplayed(accessConsent)) {
				clickOnElement(accessAllowButton);
				logger.info("Access consent displayed and clicked.");
			} else {
				logger.info("Access consent not displayed. Skipping.");
			}
		} catch (Exception e) {
			logger.info("Access consent not present. Continuing flow.");
		}
	}

	public void clickNewFolderButton() {
		try {
			if (isElementDisplayed(newFolderIcon)) {
				clickOnElement(newFolderIcon);
				logger.info("Clicked New Folder icon.");
			} else if (isElementDisplayed(createNewFolderBtn)) {
				clickOnElement(createNewFolderBtn);
				logger.info("Clicked CREATE NEW FOLDER button.");
			} else {
				logger.info("Neither New Folder icon nor CREATE NEW FOLDER button displayed.");
			}
		} catch (Exception e) {
			logger.info("Error while clicking New Folder: " + e.getMessage());
		}
	}

	public boolean isNewFolderPopupDisplayed() {
		return isElementDisplayed(newfolderPopup);
	}

	public boolean isFolderDisplayed(String folderName) {
		try {
			return driver.findElement(MobileBy.AndroidUIAutomator("new UiSelector().text(\"" + folderName + "\")"))
					.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	public void enterFolderName(String foldername) {
		clickAndsendKeysToTextBox4(newfolderTextBox, foldername);
	}

	public void clickOnOkButton() {
		clickOnElement(okButton);
	}

	public void selectFolderByName(String folderName) {
		String safeName = folderName.replace("\"", "\\\"");
		By folderLocator = MobileBy.AndroidUIAutomator(
				"new UiSelector()" + ".className(\"android.widget.TextView\")" + ".text(\"" + safeName + "\")");

		click(folderLocator);
	}

	public void clickOnUseThisFolderButton() {
		clickOnElement(useThisFolderButton);
	}

	public void handleAllowFolderConsentIfPresent() {
		try {
			if (isElementDisplayed(accessFolderAlertPopup)) {
				clickOnElement(allowFolderButton);
				logger.info("Allow folder consent displayed and clicked.");
			} else {
				logger.info("Allow folder consent not displayed. Skipping.");
			}
		} catch (Exception e) {
			logger.info("Allow folder consent not present. Continuing flow.");
		}
	}

	public boolean isPacketManagerTitleDisplayed() {
		return isElementDisplayed(packetManagerTitle);
	}

	private void scrollToText(String text) {
		String safeText = text.replace("\\", "\\\\").replace("\"", "\\\"");
		driver.findElement(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true))"
				+ ".scrollIntoView(new UiSelector().text(\"" + safeText + "\"))"));
	}

	public void exportPacketIntoFolder(String folderName) {

		// STEP 1: If already opened → just return
		if (exportIfTargetFolderAlreadyOpened(folderName)) {
			logger.info("Target folder already opened: " + folderName);
			return;
		}

		// STEP 2: Scroll till end and check folder
		boolean folderExists = false;

		try {
			scrollToText(folderName); // ideally should scroll till end
			folderExists = isFolderDisplayed(folderName);
		} catch (Exception e) {
			logger.info("Folder not found during scroll: " + folderName);
		}

		// STEP 3: If folder exists → select + use
		if (folderExists) {
			logger.info(folderName + " folder exists. Selecting it.");

			selectFolderByName(folderName);

			clickOnUseThisFolderButton();
			handleAllowFolderConsentIfPresent();

			return;
		}

		// STEP 4: If folder NOT exists → create new
		logger.info(folderName + " folder not found. Creating new folder.");

		clickNewFolderButton();

		if (!isNewFolderPopupDisplayed()) {
			throw new RuntimeException("New Folder popup not displayed");
		}

		enterFolderName(folderName);
		clickOnOkButton();

		clickOnUseThisFolderButton();
		handleAllowFolderConsentIfPresent();
	}

	public boolean isFolderTitleDisplayed(String folderName) {
		By folderLocator = By.xpath("//android.widget.TextView[@text='" + folderName + "']");
		return isElementDisplayed(folderLocator);
	}

	public void navigateToFolderPath(String... folderPath) {

		try {

			String targetFolder = folderPath[folderPath.length - 1];

			// ✅ STEP 1: If already inside final folder → skip everything
			if (isFolderTitleDisplayed(targetFolder)) {
				logger.info("Already inside target folder: " + targetFolder);
				return;
			}

			// ✅ STEP 2: Navigate step by step
			for (String folder : folderPath) {

				logger.info("Navigating to folder: " + folder);

				if (isFolderHeaderDisplayed(folder)) {
					logger.info("Already inside folder: " + folder);
					continue;
				}

				scrollToText(folder);
				click(getFolderLocator(folder));

				logger.info("Opened folder: " + folder);
			}

		} catch (Exception e) {
			logger.error("Folder navigation failed", e);
			throw new RuntimeException("Unable to navigate folder path", e);
		}
	}

	private By getFolderLocator(String folderName) {

		return MobileBy.AndroidUIAutomator(
				"new UiSelector()" + ".className(\"android.widget.TextView\")" + ".text(\"" + folderName + "\")");
	}

	private boolean isFolderHeaderDisplayed(String folderName) {

		By headerLocator = MobileBy.AndroidUIAutomator("new UiSelector()"
				+ ".resourceId(\"com.google.android.documentsui:id/title\")" + ".text(\"" + folderName + "\")");

		return isElementDisplayed(headerLocator);
	}

	public void exportToFolder(String folderName) {

		if (exportIfTargetFolderAlreadyOpened(folderName)) {
			return;
		}

		navigateToFolderPath("Documents", "packets", "PACKET_MANAGER_ACCOUNT");

		exportPacketIntoFolder(folderName);
	}

	private boolean exportIfTargetFolderAlreadyOpened(String folderName) {

		if (isFolderTitleDisplayed(folderName)) {

			logger.info(folderName + " folder already opened. Using it.");
			clickOnUseThisFolderButton();
			handleAllowFolderConsentIfPresent();
			return true;
		}

		return false;
	}

}
