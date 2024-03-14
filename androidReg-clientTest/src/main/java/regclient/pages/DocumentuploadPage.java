package regclient.pages;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;


public class DocumentuploadPage extends BasePage {

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"Document Upload\"))")
	private WebElement doccumentUploadPage;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.Button\")")
	private WebElement identityProofScanButton;

	@AndroidFindBy(xpath = "//android.widget.ScrollView/android.view.View[3]/android.view.View")
	private WebElement addressProofSelectValue;
	
	@AndroidFindBy(xpath = "//android.widget.ScrollView/android.view.View[4]/android.view.View")
	private WebElement identityProofSelectValue;
	
	@AndroidFindBy(xpath = "//android.widget.ScrollView/android.view.View[2]/android.view.View")
	private WebElement dobProofSelectValue;

	@AndroidFindBy(accessibility = "Scrim")
	private WebElement identityProofPopUpClose;

	@AndroidFindBy(accessibility = "Scan")
	private WebElement scanButton;
	
	@AndroidFindBy(xpath = "(//android.widget.Button[@content-desc=\"Scan\"])[2]")
	private WebElement dobscanButton;

	@AndroidFindBy(accessibility = "Back")
	private WebElement backButton;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\")")
	private WebElement previewCaptureImage;

	@AndroidFindBy(accessibility = "CONTINUE")
	private WebElement continueButton;

	@AndroidFindBy(accessibility = "DELETE")
	private WebElement deleteButton;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"DOB Proof\"))")
	private WebElement dobsHeader;

	public DocumentuploadPage(AppiumDriver driver) {
		super(driver);
	}



	public void selectAddressProof() {
		clickOnElement(addressProofSelectValue);
	}
	
	public void selectIndentityProof() {
		clickOnElement(identityProofSelectValue);
	}

	public void selectDobProof() {
		clickOnElement(dobProofSelectValue);
	}
	
	public void selectOnCaptureImage() {
		clickOnElement(previewCaptureImage);
	}

	public void closePopUpClose() {
		clickOnElement(identityProofPopUpClose);
	}

	public CameraPage clickOnScanButton() {
		clickOnElement(scanButton);
		return new CameraPage(driver);
	}
	
	public CameraPage clickOnDobScanButton() {
		clickOnElement(dobscanButton);
		return new CameraPage(driver);
	}

	public void clickOnBackButton() {
		clickOnElement(backButton);

	}

	public  BiometricDetailsPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new BiometricDetailsPage(driver);
	}

	public  boolean isScanButtonEnabled() {
		return isElementEnabled(scanButton);
	}

	public  boolean isImageDisplyed() {
		return isElementEnabled(backButton);
	}
	
	public  boolean isDeleteButtonDisplyed() {
		return isElementEnabled(deleteButton);
	}

	public boolean isDoccumentUploadPageDisplayed() {
		return isElementDisplayed(doccumentUploadPage);
	}
	
	public boolean isDobHeaderDisplayed() {
		return isElementDisplayed(dobsHeader);
	}

}
