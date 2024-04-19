package regclient.pages.kannada;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.BiometricDetailsPage;
import regclient.page.CameraPage;
import regclient.page.DocumentUploadPage;


public class DocumentuploadPageKannada extends DocumentUploadPage{
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"ಡಾಕ್ಯುಮೆಂಟ್ ಅಪ್ಲೋಡ್\"))")
	private WebElement doccumentUploadPage;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.Button\")")
	private WebElement identityProofScanButton;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Address Proof\")]/parent::android.view.View/parent::android.view.View")
	private WebElement addressProofSelectValue;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Identity Proof\")]/parent::android.view.View/parent::android.view.View")
	private WebElement identityProofSelectValue;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Relationship Proof\")]/parent::android.view.View/parent::android.view.View")
	private WebElement relationshipProofSelectValue;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"DOB Proof\")]/parent::android.view.View/parent::android.view.View")
	private WebElement dobProofSelectValue;

	@AndroidFindBy(accessibility = "ಸ್ಕ್ರಿಮ್")
	private WebElement PopUpCloseButton;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Address Proof\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")
	private WebElement scanButtonAddressProof;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Identity Proof\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")
	private WebElement scanButtonIdentityProof;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"DOB Proof\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")
	private WebElement scanButtonDobProof;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Relationship Proof\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")
	private WebElement scanButtonRelationshipProof;

	@AndroidFindBy(accessibility = "ಹಿಂತಿರುಗಿ")
	private WebElement backButton;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Address Proof\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.ImageView")
	private WebElement previewCaptureImage;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Address Proof\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.ImageView[2]")
	private WebElement previewSecondCaptureImage;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Address Proof\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.ImageView[3]")
	private WebElement previewThirdCaptureImage;

	@AndroidFindBy(accessibility = "ಮುಂದುವರಿಸಿ")
	private WebElement continueButton;

	@AndroidFindBy(accessibility = "ಅಳಿಸಿ")
	private WebElement deleteButton;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"DOB Proof\"))")
	private WebElement dobsHeader;

	@AndroidFindBy(xpath = "//android.widget.ImageView")
	private WebElement captureImage;

	public DocumentuploadPageKannada(AppiumDriver driver) {
		super(driver);
	}
	
	public void selectAddressProof() {
		while(!isElementDisplayedOnScreen(addressProofSelectValue) || !isElementDisplayedOnScreen(scanButtonAddressProof)) {
			swipeOrScroll();
		}
		clickOnElement(addressProofSelectValue);
		if(!isElementDisplayedOnScreen(PopUpCloseButton)) {
			swipeOrScroll();
			clickOnElement(addressProofSelectValue);	
		}
	}

	public void selectIdentityProof() {
		while(!isElementDisplayedOnScreen(identityProofSelectValue)|| !isElementDisplayedOnScreen(scanButtonIdentityProof) ) {
			swipeOrScroll();
		}
		clickOnElement(identityProofSelectValue);
		if(!isElementDisplayedOnScreen(PopUpCloseButton)) {
			swipeOrScroll();
			clickOnElement(identityProofSelectValue);	
		}
	}

	public void selectDobProof() {
		while(!isElementDisplayedOnScreen(dobProofSelectValue) || !isElementDisplayedOnScreen(scanButtonDobProof)) {
			swipeOrScroll();
		}
		clickOnElement(dobProofSelectValue);
		if(!isElementDisplayedOnScreen(PopUpCloseButton)) {
			swipeOrScroll();
			clickOnElement(dobProofSelectValue);	
		}
	}

	public void selectRelationshipProof() {
		while(!isElementDisplayedOnScreen(relationshipProofSelectValue) || !isElementDisplayedOnScreen(scanButtonRelationshipProof)) {
			swipeOrScroll();
		}
		clickOnElement(relationshipProofSelectValue);
		if(!isElementDisplayedOnScreen(PopUpCloseButton)) {
			swipeOrScroll();
			clickOnElement(relationshipProofSelectValue);	
		}
	}

	public void selectOnCaptureImage() {
		clickOnElement(previewCaptureImage);
	}

	public void closePopUpClose() {
		clickOnElement(PopUpCloseButton);
	}

	public CameraPage clickOnAddressProofScanButton() {
		if(!isElementDisplayedOnScreen(scanButtonAddressProof)) {
			swipeOrScroll();
		}
		clickOnElement(scanButtonAddressProof);
		return new CameraPage(driver);
	}

	public CameraPage clickOnScanButtonDobProof() {
		if(!isElementDisplayedOnScreen(scanButtonDobProof)) {
			swipeOrScroll();
		}
		clickOnElement(scanButtonDobProof);
		return new CameraPage(driver);
	}

	public CameraPage clickOnScanButtonIdentityProof() {
		if(!isElementDisplayedOnScreen(scanButtonIdentityProof)) {
			swipeOrScroll();
		}
		clickOnElement(scanButtonIdentityProof);
		return new CameraPage(driver);
	}

	public CameraPage clickOnScanButtonRelationshipProof() {
		if(!isElementDisplayedOnScreen(scanButtonRelationshipProof)) {
			swipeOrScroll();
		}
		clickOnElement(scanButtonRelationshipProof);
		return new CameraPage(driver);
	}

	public void clickOnBackButton() {
		clickOnElement(backButton);

	}

	public void clickOnDeleteButton() {
		clickOnElement(deleteButton);

	}

	public  BiometricDetailsPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new BiometricDetailsPageKannada(driver);
	}

	public  boolean isImageDisplyed() {
		return isElementDisplayed(captureImage);
	}

	public  boolean isSecondImageDisplyed() {
		return isElementDisplayed(previewSecondCaptureImage);
	}

	public  boolean isThirdImageDisplyed() {
		return isElementDisplayed(previewThirdCaptureImage);
	}

	public  boolean isDeleteButtonDisplyed() {
		if(!isElementDisplayedOnScreen(deleteButton)) {
			swipeOrScroll();
		}
		return isElementDisplayed(deleteButton);
	}

	public boolean isDoccumentUploadPageDisplayed() {
		return isElementDisplayed(doccumentUploadPage);
	}

	public boolean isDobHeaderDisplayed() {
		return isElementDisplayed(dobsHeader);
	}

	public  boolean isScanButtonAddressProofEnabled() {
		if(!isElementDisplayedOnScreen(scanButtonAddressProof)) {
			swipeOrScroll();
		}
		return isElementEnabled(scanButtonAddressProof);
	}

	public  boolean isScanButtonIdentityProofEnabled() {
		if(!isElementDisplayedOnScreen(scanButtonIdentityProof)) {
			swipeOrScroll();
		}
		return isElementEnabled(scanButtonIdentityProof);
	}

	public  boolean isscanButtonDobProofEnabled() {
		if(!isElementDisplayedOnScreen(scanButtonDobProof)) {
			swipeOrScroll();
		}
		return isElementEnabled(scanButtonDobProof);
	}

	public  boolean isScanButtonRelationshipProoffEnabled() {
		if(!isElementDisplayedOnScreen(scanButtonRelationshipProof)) {
			swipeOrScroll();
		}
		return isElementEnabled(scanButtonRelationshipProof);
	}


}
