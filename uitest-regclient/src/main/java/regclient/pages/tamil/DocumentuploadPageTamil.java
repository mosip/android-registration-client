package regclient.pages.tamil;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.api.FetchUiSpec;
import regclient.page.BiometricDetailsPage;
import regclient.page.CameraPage;
import regclient.page.DocumentUploadPage;
import regclient.pages.english.BiometricDetailsPageEnglish;

public class DocumentuploadPageTamil extends DocumentUploadPage {

	@AndroidFindBy(accessibility = "ஸ்க்ரிம்")
	private WebElement PopUpCloseButton;

	@AndroidFindBy(accessibility = "முந்தைய பக்கம்")
	private WebElement backButton;

	@AndroidFindBy(accessibility = "தொடர்க")
	private WebElement continueButton;

	@AndroidFindBy(accessibility = "அழி")
	private WebElement deleteButton;

	@AndroidFindBy(xpath = "//android.widget.ImageView")
	private WebElement captureImage;

	public DocumentuploadPageTamil(AppiumDriver driver) {
		super(driver);
	}

	public void selectAddressProof() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("proofOfAddress")+"\")]/parent::android.view.View/parent::android.view.View")));

	}

	public void selectIdentityProof() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("proofOfIdentity")+"\")]/parent::android.view.View/parent::android.view.View")));
		if(!isElementDisplayedOnScreen(PopUpCloseButton)) {
			swipeOrScroll();
			clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("proofOfIdentity")+"\")]/parent::android.view.View/parent::android.view.View")));
		}
	}

	public void selectDobProof() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("proofOfDateOfBirth")+"\")]/parent::android.view.View/parent::android.view.View")));
		if(!isElementDisplayedOnScreen(PopUpCloseButton)) {
			swipeOrScroll();
			clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("proofOfDateOfBirth")+"\")]/parent::android.view.View/parent::android.view.View")));
		}
	}

	public void selectRelationshipProof() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("proofOfRelationship")+"\")]/parent::android.view.View/parent::android.view.View")));
		if(!isElementDisplayedOnScreen(PopUpCloseButton)) {
			swipeOrScroll();
			clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("proofOfRelationship")+"\")]/parent::android.view.View/parent::android.view.View")));
		}
	}

	public void selectOnCaptureImage() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("proofOfAddress")+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.ImageView")));
	}

	public void closePopUpClose() {
		clickOnElement(PopUpCloseButton);
	}

	public CameraPage clickOnAddressProofScanButton() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("proofOfAddress")+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
		return new CameraPage(driver);
	}

	public CameraPage clickOnScanButtonDobProof() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("proofOfDateOfBirth")+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
		return new CameraPage(driver);
	}

	public CameraPage clickOnScanButtonIdentityProof() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("proofOfIdentity")+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
		return new CameraPage(driver);
	}

	public CameraPage clickOnScanButtonRelationshipProof() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("proofOfRelationship")+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
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
		return new BiometricDetailsPageEnglish(driver);
	}

	public  boolean isImageDisplyed() {
		waitTime(1);
		return isElementDisplayed(captureImage);
	}

	public  boolean isSecondImageDisplyed() {
		waitTime(1);
		return isElementDisplayed(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("proofOfAddress")+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.ImageView[2]")));
	}

	public  boolean isThirdImageDisplyed() {
		waitTime(1);
			return isElementDisplayed(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("proofOfAddress")+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.ImageView[3]"));
	}

	public  boolean isDeleteButtonDisplyed() {
		if(!isElementDisplayedOnScreen(deleteButton)) {
			swipeOrScroll();
		}
		return isElementDisplayed(deleteButton);
	}

	@SuppressWarnings("deprecation")
	public boolean isDoccumentUploadPageDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"" + FetchUiSpec.getScreenTitle("Documents") + "\"))")));
	}

	@SuppressWarnings("deprecation")
	public boolean isDobHeaderDisplayed() {
		return isElementDisplayed (findElementWithRetry(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"" + FetchUiSpec.getValueUsingId("proofOfDateOfBirth") + "\"))")));
	}

	public  boolean isScanButtonAddressProofEnabled() {
		return isElementEnabled(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("proofOfAddress")+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
	}

	public  boolean isScanButtonIdentityProofEnabled() {
		return isElementEnabled(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("proofOfIdentity")+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
	}

	public  boolean isScanButtonDobProofEnabled() {
		return isElementEnabled(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("proofOfDateOfBirth")+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));

	}

	public  boolean isScanButtonRelationshipProoffEnabled() {
		return isElementEnabled(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("proofOfRelationship")+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));

	}
	
	public  void enterReferenceNumberInAdressProof() {
		clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("proofOfAddress")+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.EditText")),"1234567890");
	}
	
	public  void enterReferenceNumberInIdentityProof() {
		clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("proofOfIdentity")+"\")]//parent::android.view.View/parent::android.view.View/following-sibling::android.widget.EditText")),"1234567890");
	}
	
	public  void enterReferenceNumberInDobProof() {
		clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId("proofOfDateOfBirth")+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.EditText")),"1234567890");
	}

}
