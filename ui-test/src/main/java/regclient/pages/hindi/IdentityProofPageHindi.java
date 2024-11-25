package regclient.pages.hindi;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.DocumentUploadPage;
import regclient.page.IdentityProofPage;

public class IdentityProofPageHindi extends IdentityProofPage{

	@AndroidFindBy(accessibility = "बचाना")
	private WebElement saveButton;

	@AndroidFindBy(accessibility = "फिर से लेना")
	private WebElement retakeButton;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.view.View\").instance(8)")
	private WebElement imageleftCorner;
	
	@AndroidFindBy(className = "android.widget.ImageView")
	private WebElement captureImage;
	
	public IdentityProofPageHindi(AppiumDriver driver) {
		super(driver);
	}
	
	public  DocumentUploadPage clickOnSaveButton() {
		clickOnElement(saveButton);
		return new DocumentUploadPageHindi(driver);
	}

	public boolean isRetakeButtonDisplayed() {
		return isElementDisplayed(retakeButton);
	}

	public void cropCaptureImage() {
		isElementDisplayed(captureImage);
		cropCaptureImage(imageleftCorner);
	}

}
