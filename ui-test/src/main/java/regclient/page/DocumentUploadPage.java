package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class DocumentUploadPage extends BasePage {

	public DocumentUploadPage(AppiumDriver driver) {
		super(driver);
	}

	public abstract BiometricDetailsPage clickOnContinueButton();

	public abstract boolean isDoccumentUploadPageDisplayed();

	public abstract DocumentUploadPage clickOnSaveButton();

	public abstract boolean isRetakeButtonDisplayed();

	public abstract void cropCaptureImage();

	public abstract void uploadDoccuments(String age, String type);

	public abstract void uploadDoccumentsUpdate(String age, String type);
}
