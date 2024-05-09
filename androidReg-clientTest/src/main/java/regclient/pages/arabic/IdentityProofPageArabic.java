package regclient.pages.arabic;



import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.DocumentUploadPage;
import regclient.page.IdentityProofPage;

public class IdentityProofPageArabic extends IdentityProofPage{

	@AndroidFindBy(accessibility = "يحفظ")
	private WebElement saveButton;

	@AndroidFindBy(accessibility = "استعادة")
	private WebElement retakeButton;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.view.View\").instance(8)")
	private WebElement imageleftCorner;

	public IdentityProofPageArabic(AppiumDriver driver) {
		super(driver);
	}

	public  DocumentUploadPage clickOnSaveButton() {
		clickOnElement(saveButton);
		return new DocumentuploadPageArabic(driver);
	}

	public boolean isRetakeButtonDisplayed() {
		return isElementDisplayed(retakeButton);
	}

	public void cropCaptureImage() {
		cropCaptureImage(imageleftCorner);
	}

}
