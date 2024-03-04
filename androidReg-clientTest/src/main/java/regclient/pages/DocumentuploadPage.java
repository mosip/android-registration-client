package regclient.pages;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;


public class DocumentuploadPage extends BasePage {

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.Button\")")
	private WebElement doccumentUploadPage;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.Button\")")
	private WebElement identityProofScanButton;

	@AndroidFindBy(xpath = "//android.widget.ScrollView/android.view.View[3]/android.view.View")
	private WebElement identityProofSelectValue;

	@AndroidFindBy(accessibility = "Scrim")
	private WebElement identityProofPopUpClose;

	@AndroidFindBy(accessibility = "Scan")
	private WebElement scanButton;


	public DocumentuploadPage(AppiumDriver driver) {
		super(driver);
	}

	public boolean isDoccumentUploadPageDisplay() {
		return isElementDisplayed(doccumentUploadPage);
	}

	public void selectIdentityProof() {
		clickOnElement(identityProofSelectValue);
		clickOnElement(identityProofPopUpClose);
	}

	public CameraPage clickOnScanButton() {
		clickOnElement(scanButton);
		return new CameraPage(driver);
	}

	public  boolean isScanButtonEnable() {
		return isElementEnabled(scanButton);
	}

}
