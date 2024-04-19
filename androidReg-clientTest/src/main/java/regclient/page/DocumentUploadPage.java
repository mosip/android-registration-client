package regclient.page;

import io.appium.java_client.AppiumDriver;


public abstract class DocumentUploadPage extends BasePage {

    public DocumentUploadPage(AppiumDriver driver) {
        super(driver);
    }

    public abstract void selectAddressProof();

	public abstract void selectIdentityProof();

	public abstract void selectDobProof();

	public abstract void selectRelationshipProof();

	public abstract void selectOnCaptureImage();

	public abstract void closePopUpClose();

	public abstract CameraPage clickOnAddressProofScanButton();

	public abstract CameraPage clickOnScanButtonDobProof();

	public abstract CameraPage clickOnScanButtonIdentityProof();

	public abstract CameraPage clickOnScanButtonRelationshipProof();

	public abstract void clickOnBackButton();

	public abstract void clickOnDeleteButton();

	public abstract  BiometricDetailsPage clickOnContinueButton();

	public abstract  boolean isImageDisplyed();

	public abstract  boolean isSecondImageDisplyed();

	public abstract  boolean isThirdImageDisplyed();

	public abstract  boolean isDeleteButtonDisplyed();

	public abstract boolean isDoccumentUploadPageDisplayed();

	public abstract boolean isDobHeaderDisplayed();

	public abstract  boolean isScanButtonAddressProofEnabled();

	public abstract  boolean isScanButtonIdentityProofEnabled() ;

	public abstract  boolean isscanButtonDobProofEnabled();

	public abstract  boolean isScanButtonRelationshipProoffEnabled();
}
