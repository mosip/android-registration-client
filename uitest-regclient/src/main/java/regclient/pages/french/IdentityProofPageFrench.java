package regclient.pages.french;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.DocumentUploadPage;
import regclient.page.IdentityProofPage;

public class IdentityProofPageFrench extends IdentityProofPage{

	@AndroidFindBy(accessibility = "SAUVEGARDER")
	private WebElement saveButton;

	@AndroidFindBy(accessibility = "REPRENDRE")
	private WebElement retakeButton;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.view.View\").instance(8)")
	private WebElement imageleftCorner;
	
	@AndroidFindBy(className = "android.widget.ImageView")
	private WebElement captureImage;
	
	public IdentityProofPageFrench(AppiumDriver driver) {
		super(driver);
	}

	public  DocumentUploadPage clickOnSaveButton() {
		clickOnElement(saveButton);
		return new DocumentUploadPageFrench(driver);
	}

	public boolean isRetakeButtonDisplayed() {
		return isElementDisplayed(retakeButton);
	}

	public void cropCaptureImage() {
		isElementDisplayed(captureImage);
		cropCaptureImage(imageleftCorner);
	}
}
