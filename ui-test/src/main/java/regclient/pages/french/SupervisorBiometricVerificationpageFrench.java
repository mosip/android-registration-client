package regclient.pages.french;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.SupervisorBiometricVerificationpage;

public class SupervisorBiometricVerificationpageFrench extends SupervisorBiometricVerificationpage{

	@AndroidFindBy(accessibility = "Supervisor's Biometric Verification")
	private WebElement supervisorBiometricVerificationPageTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Iris\"))")
	private WebElement irisScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Main droite\"))")
	private WebElement rightHandScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Main gauche\"))")
	private WebElement leftHandScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Pouces ANALYSE\"))")
	private WebElement thumbsScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Visage ANALYSE\"))")
	private WebElement faceScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Exception\"))")
	private WebElement exceptionScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Iris ANALYSE\"))")
	private WebElement irisScanButtonTitle;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Main droite ANALYSE\"))")
	private WebElement rightHandScanTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Main gauche ANALYSE\"))")
	private WebElement leftHandScanTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Pouces ANALYSE\"))")
	private WebElement thumbsScanTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Visage ANALYSE\"))")
	private WebElement faceScanTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Exception ANALYSE\"))")
	private WebElement exceptionScanTitle;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"ANALYSE\"))")
	private WebElement scanButton;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Iris Capturer')]")
	private WebElement irisCapturerHeader;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'RightHand Capturer')]")
	private WebElement rightHandCapturerHeader;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'LeftHand Capturer')]")
	private WebElement leftHandCapturerHeader;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Thumbs Capturer')]")
	private WebElement thumbsCapturerHeader;
	
	@AndroidFindBy(xpath = "//*[contains(@content-desc, 'Face Capturer')]")
	private WebElement faceCapturerHeader;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Seuil \")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement irisScanQuality;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Seuil \")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement rightHandScanQuality;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Seuil \")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement leftHandScanQuality;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Seuil \")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement thumbsScanQuality;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Seuil \")]/following-sibling::android.view.View[contains(@content-desc, \"%\")]")
	private WebElement faceScanQuality;
	
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(1)")
	private WebElement oneEyeException;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.ImageView\").instance(2)")
	private WebElement firstFingureExceptionImage;
	
	@AndroidFindBy(accessibility = "Marquage des exceptions sur Visage n'est pas autorisé")
	private WebElement markingExceptionsOnFaceIsNotAllowedText;
	
	@AndroidFindBy(xpath = "//android.widget.ScrollView/android.view.View[3]")
	private WebElement zoomButton;
	
	@AndroidFindBy(accessibility = "Permanent")
	private WebElement permanentButton;
	
	@AndroidFindBy(accessibility = "Temporaire")
	private WebElement temporaryButton;
	
	@AndroidFindBy(accessibility = "Marquer l'exception")
	private WebElement markExceptionButton;
	
	@AndroidFindBy(accessibility = "Marquer l'exception")
	private WebElement exceptionTypeTitle;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Commentaires\")]/following-sibling::android.widget.EditText")
	private WebElement commentsTextBox;
	
	@AndroidFindBy(className = "android.widget.Button")
	private WebElement popUpCloseButton;
	
	@AndroidFindBy(accessibility = "PROCHAINE")
	private WebElement nextButton;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"Commentaires\"))")
	private WebElement commentsHeader;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Seuil \")]/following-sibling::android.view.View")
	private WebElement scanQualityText;
	
	@AndroidFindBy(accessibility = "VÉRIFIER ET ENREGISTRER")
	private WebElement verifyAndSaveButton;
	
	@AndroidFindBy(accessibility = "Ignorer")
	private WebElement dismissPage;
	
	@AndroidFindBy(accessibility = "Vous avez été intégré avec succès.")
	private WebElement successPopup;
	
	@AndroidFindBy(accessibility = "MAISON")
	private WebElement homeButton;
	
	public SupervisorBiometricVerificationpageFrench(AppiumDriver driver) {
		super(driver);
	}

	public void clickOnIrisScan() {
		clickOnElement(irisScanIcon);
	}
	
	public boolean isSupervisorBiometricVerificationPageLoaded() {
		return isElementDisplayed(supervisorBiometricVerificationPageTitle);
	}
	
	public void clickOnRightHandScanIcon() {
		clickOnElement(rightHandScanIcon);
	}
	
	public void clickOnLeftHandScanIcon() {
		clickOnElement(leftHandScanIcon);
	}
	
	public void clickOnThumbsScanIcon() {
		clickOnElement(thumbsScanIcon);
	}
	
	public void clickOnFaceScanIcon() {
		clickOnElement(faceScanIcon);
	}
	
	public void clickOnExceptionScanIcon() {
		clickOnElement(exceptionScanIcon);
	}
	
	public void clickOnScanButton() {
		clickOnElement(scanButton);
	}	
	
	public void clickOnNextButton() {
		clickOnElement(nextButton);
	}
	
	public void closeScanCapturePopUp() {
		driver.navigate().back();
	}
	
	
	public  boolean isRightHandScanTitleDisplyed() {
		return isElementDisplayed(rightHandScanTitle);
	}
	
	public  boolean isLeftHandScanTitleDisplyed() {
		return isElementDisplayed(leftHandScanTitle);
	}
	
	public  boolean isThumbsScanTitleDisplyed() {
		return isElementDisplayed(thumbsScanTitle);
	}
	
	public  boolean isFaceScanTitleDisplyed() {
		return isElementDisplayed(faceScanTitle);
	}
	
	public  boolean isExceptionScanTitleDisplyed() {
		return isElementDisplayed(exceptionScanTitle);
	}
	
	public  boolean isIrisScan() {
		return isElementDisplayed(irisCapturerHeader,2000);
	}
	
	public  boolean isRightHandScan() {
		return isElementDisplayed(rightHandCapturerHeader,2000);
	}
	
	public  boolean isLeftHandScan() {
		return isElementDisplayed(leftHandCapturerHeader,2000);
	}
	
	public  boolean isThumbsScan() {
		return isElementDisplayed(thumbsCapturerHeader,2000);
	}
	
	public  boolean isFaceScan() {
		return isElementDisplayed(faceCapturerHeader,2000);
	}
	
	public  boolean isIrisScanQualityDisplyed() {
		return isElementDisplayed(irisScanQuality);
	}
	
	public  boolean isRightHandScanQualityDisplyed() {
		return isElementDisplayed(rightHandScanQuality);
	}
	
	public  boolean isLeftHandScanQualityDisplyed() {
		return isElementDisplayed(leftHandScanQuality);
	}
	
	public  boolean isThumbsScanQualityDisplyed() {
		return isElementDisplayed(thumbsScanQuality);
	}
	
	public  boolean isFaceScanQualityDisplyed() {
		return isElementDisplayed(faceScanQuality);
	}
	
	public void clickOnMarkExceptionButton() {
		clickOnElement(markExceptionButton);
	}
	
	public void markOneEyeException() {
		clickOnElement(oneEyeException);
	}
	
	public void markOneFingureException() {
		clickOnElement(firstFingureExceptionImage);
	}
	
	public  boolean isExceptionTypeTitleDisplyed() {
		return isElementDisplayed(exceptionTypeTitle);
	}
	
	public void clickOnExceptionTypeTemporaryButton() {
		if(!isElementDisplayedOnScreen(temporaryButton)) {
			swipeOrScroll();
		}
		clickOnElement(temporaryButton);	
	}
	
	public  boolean isCommentHeaderDisplyed() {
		return isElementDisplayed(commentsHeader);
	}
	
	public  void enterCommentsInTextBox(String comments) {
		if(!isElementDisplayedOnScreen(commentsTextBox)) {
			swipeOrScroll();
		}
		clickAndsendKeysToTextBox(commentsTextBox,comments);
	}
	
	public void clickOnIrisScanTitle() {
		clickOnElement(irisScanButtonTitle);
	}
	
	public void clickOnThumbsScanTitle() {
		clickOnElement(thumbsScanTitle);
	}
	
	public  boolean isMarkingExceptionsOnFaceIsNotAllowedTextDisplyed() {
		return isElementDisplayed(markingExceptionsOnFaceIsNotAllowedText);
	}
	
	public  boolean isZoomButtonDisplyed() {
		return isElementDisplayed(zoomButton);
	}
	
	public void clickOnRightHandScanTitle() {
		clickOnElement(rightHandScanTitle);
	}
	
	public void clickOnleftHandScanTitle() {
		clickOnElement(leftHandScanTitle);
	}
	
	public void clickOnFaceScanTitle() {
		clickOnElement(faceScanTitle);
	}
	
	public boolean checkThresholdValueIris() {
		int requiredValue=60;
		String scanValue =scanQualityText.getAttribute("contentDescription");
		int value=Integer.valueOf(scanValue.replace("%", ""));
		if(value>=requiredValue) 
			return true;
		else
			return false;
	}
	
	public boolean checkThresholdValueRightHand() {
		int requiredValue=40;
		String scanValue =scanQualityText.getAttribute("contentDescription");
		int value=Integer.valueOf(scanValue.replace("%", ""));
		if(value>=requiredValue) 
			return true;
		else
			return false;
	}
	
	public boolean checkThresholdValueLeftHand() {
		int requiredValue=60;
		String scanValue =scanQualityText.getAttribute("contentDescription");
		int value=Integer.valueOf(scanValue.replace("%", ""));
		if(value>=requiredValue) 
			return true;
		else
			return false;
	}
	
	public boolean checkThresholdValueThumbs() {
		int requiredValue=40;
		String scanValue =scanQualityText.getAttribute("contentDescription");
		int value=Integer.valueOf(scanValue.replace("%", ""));
		if(value>=requiredValue) 
			return true;
		else
			return false;
	}
	
	public boolean checkThresholdValueFace() {
		int requiredValue=90;
		String scanValue =scanQualityText.getAttribute("contentDescription");
		int value=Integer.valueOf(scanValue.replace("%", ""));
		if(value>=requiredValue) 
			return true;
		else
			return false;
	}
	
	public boolean isVerifyAndSaveButtonEnabled() {
		return isElementDisplayed(verifyAndSaveButton);
		
		
	}

	@Override
	public void clickOnVerifyAndSaveButton() {
		clickOnElement(verifyAndSaveButton);
		
	}
	
	public boolean isDismissPageLoaded() {
		return isElementDisplayed(dismissPage);
	}
	
	
	public boolean isOperatorOnboardedPopupLoaded() {
		return isElementDisplayed(successPopup);	
	}
	
	public void clickOnHomeButton() {
		clickOnElement(homeButton);
	}

	public void clickOnBackButton() {
		driver.navigate().back();
	}
}
