package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class IdentityProofPage extends BasePage {

	public IdentityProofPage(AppiumDriver driver) {
		super(driver);
	}

	public abstract DocumentUploadPage clickOnSaveButton();

	public abstract boolean isRetakeButtonDisplayed();

	public abstract void cropCaptureImage();
}
