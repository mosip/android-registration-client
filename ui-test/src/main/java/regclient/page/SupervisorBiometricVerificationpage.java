package regclient.page;

import io.appium.java_client.AppiumDriver;

public abstract class SupervisorBiometricVerificationpage extends BasePage{

	public SupervisorBiometricVerificationpage(AppiumDriver driver) {
		super(driver);
	}
	public abstract boolean isSupervisorBiometricVerificationPageLoaded();

	public abstract void clickOnIrisScan();

	public abstract void clickOnRightHandScanIcon();

	public abstract void clickOnLeftHandScanIcon();

	public abstract void clickOnThumbsScanIcon();

	public abstract void clickOnFaceScanIcon();

	public abstract void clickOnExceptionScanIcon();

	public abstract void clickOnScanButton();

	public abstract void clickOnNextButton();

	public abstract void closeScanCapturePopUp();

	public abstract boolean isRightHandScanTitleDisplyed();

	public abstract boolean isLeftHandScanTitleDisplyed();

	public abstract boolean isThumbsScanTitleDisplyed();

	public abstract boolean isFaceScanTitleDisplyed();

	public abstract boolean isExceptionScanTitleDisplyed();

	public abstract boolean isIrisScan();

	public abstract boolean isRightHandScan();

	public abstract boolean isLeftHandScan();

	public abstract boolean isThumbsScan();

	public abstract boolean isFaceScan();

	public abstract boolean isIrisScanQualityDisplyed();

	public abstract boolean isRightHandScanQualityDisplyed();

	public abstract boolean isLeftHandScanQualityDisplyed();

	public abstract boolean isThumbsScanQualityDisplyed();

	public abstract boolean isFaceScanQualityDisplyed();
	
	public abstract void clickOnMarkExceptionButton();

    public abstract void markOneEyeException();

    public abstract void markOneFingureException();
    
    public abstract boolean isExceptionTypeTitleDisplyed();

    public abstract void clickOnExceptionTypeTemporaryButton();

    public abstract boolean isCommentHeaderDisplyed();

    public abstract void enterCommentsInTextBox(String comments);

    public abstract void clickOnIrisScanTitle();
    
    public abstract void clickOnThumbsScanTitle();
    
    public abstract boolean isMarkingExceptionsOnFaceIsNotAllowedTextDisplyed();
    
    public abstract boolean isZoomButtonDisplyed();
    
    public abstract void clickOnRightHandScanTitle();

    public abstract void clickOnleftHandScanTitle();
    
    public abstract void clickOnFaceScanTitle();
    
    public abstract boolean checkThresholdValueIris();
    
    public abstract boolean checkThresholdValueRightHand();
	
	public abstract boolean checkThresholdValueLeftHand();
	
	public abstract boolean checkThresholdValueThumbs();
	
	public abstract boolean checkThresholdValueFace();
	
	public abstract boolean isVerifyAndSaveButtonEnabled();
	
	public abstract void clickOnVerifyAndSaveButton();
	
	public abstract boolean isDismissPageLoaded();
	
	public abstract boolean isOperatorOnboardedPopupLoaded();
	
	public abstract void clickOnHomeButton();
	
	public abstract void clickOnBackButton();
}
