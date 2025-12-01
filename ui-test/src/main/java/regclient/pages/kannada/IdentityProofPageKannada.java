package regclient.pages.kannada;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.DocumentUploadPage;
import regclient.page.IdentityProofPage;

public class IdentityProofPageKannada extends IdentityProofPage{

	@AndroidFindBy(accessibility = "ಉಳಿಸಿ")
	private WebElement saveButton;

	@AndroidFindBy(accessibility = "ರೀಟೇಕ್")
	private WebElement retakeButton;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.view.View\").instance(8)")
	private WebElement imageleftCorner;
	
	@AndroidFindBy(className = "android.widget.ImageView")
	private WebElement captureImage;

	public IdentityProofPageKannada(AppiumDriver driver) {
		super(driver);
	}

	public  DocumentUploadPage clickOnSaveButton() {
		clickOnElement(saveButton);
		return new DocumentUploadPageKannada(driver);
	}

	public boolean isRetakeButtonDisplayed() {
		return isElementDisplayed(retakeButton);
	}

	public void cropCaptureImage() {
		isElementDisplayed(captureImage);
		cropCaptureImage(imageleftCorner);
	}

}
