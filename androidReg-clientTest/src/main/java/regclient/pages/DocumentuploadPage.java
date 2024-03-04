package regclient.pages;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

public class DocumentuploadPage extends BasePage {

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.Button\")")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement doccumentUploadPage;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.Button\")")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement identityProofScanButton;
	
	@AndroidFindBy(xpath = "//android.widget.ScrollView/android.view.View[3]/android.view.View")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement IdentityProofSelectValue;
	
	@AndroidFindBy(accessibility = "Scrim")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement IdentityProofPopUpClose;
	
	@AndroidFindBy(accessibility = "Scan")
	@iOSXCUITFindBy(accessibility = "")
	private WebElement scanButton;
	
	
	public DocumentuploadPage(AppiumDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	public boolean isDoccumentUploadPageDisplay() {
		return isElementDisplayed(doccumentUploadPage);

	}
	
	public void selectIdentityProof() {
		clickOnElement(IdentityProofSelectValue);
		clickOnElement(IdentityProofPopUpClose);
	}
	
	public CameraPage clickOnScanButton() {
		clickOnElement(scanButton);
		return new CameraPage(driver);
		
	}
	
	public  boolean isScanButtonEnable() {
		return isElementEnabled(scanButton);
	}

}
