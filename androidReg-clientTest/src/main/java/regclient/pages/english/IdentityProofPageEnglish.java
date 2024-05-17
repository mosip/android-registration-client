package regclient.pages.english;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.DocumentUploadPage;
import regclient.page.IdentityProofPage;

public class IdentityProofPageEnglish extends IdentityProofPage{

	@AndroidFindBy(accessibility = "SAVE")
	private WebElement saveButton;

	@AndroidFindBy(accessibility = "RETAKE")
	private WebElement retakeButton;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.view.View\").instance(8)")
	private WebElement imageleftCorner;
	
	@AndroidFindBy(className = "android.widget.ImageView")
	private WebElement captureImage;
	
	public IdentityProofPageEnglish(AppiumDriver driver) {
		super(driver);
	}

	public  DocumentUploadPage clickOnSaveButton() {
		clickOnElement(saveButton);
		return new DocumentuploadPageEnglish(driver);
	}

	public boolean isRetakeButtonDisplayed() {
		return isElementDisplayed(retakeButton);
	}

	public void cropCaptureImage() {
		isElementDisplayed(captureImage);
		cropCaptureImage(imageleftCorner);
	}

}
