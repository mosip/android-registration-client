package regclient.pages.english;

import static org.testng.Assert.assertTrue;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.api.FetchUiSpec;
import regclient.page.BiometricDetailsPage;
import regclient.page.CameraPage;
import regclient.page.DocumentUploadPage;


public class DocumentuploadPageEnglish extends DocumentUploadPage {

	@AndroidFindBy(accessibility = "Scrim")
	private WebElement PopUpCloseButton;

	@AndroidFindBy(accessibility = "Back")
	private WebElement backButton;

	@AndroidFindBy(accessibility = "CONTINUE")
	private WebElement continueButton;

	@AndroidFindBy(accessibility = "DELETE")
	private WebElement deleteButton;

	@AndroidFindBy(accessibility = "SAVE")
	private WebElement saveButton;

	@AndroidFindBy(accessibility = "RETAKE")
	private WebElement retakeButton;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.view.View\").instance(8)")
	private WebElement imageleftCorner;

	@AndroidFindBy(className = "android.widget.ImageView")
	private WebElement captureImage;

	public DocumentuploadPageEnglish(AppiumDriver driver) {
		super(driver);
	}

	public  BiometricDetailsPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new BiometricDetailsPageEnglish(driver);
	}

	@SuppressWarnings("deprecation")
	public boolean isDoccumentUploadPageDisplayed() {
//		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"" + FetchUiSpec.getScreenTitle("Documents") + "\"))")));
		return true;
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

	public void uploadDoccuments(String age,String type) {
		List<String> idList=FetchUiSpec.getAllIds("Documents");
		for(String id : idList) {
			if(FetchUiSpec.getRequiredTypeUsingId(id)) {
				if(type.equalsIgnoreCase("ReferenceNumber")) {
					clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.EditText")),"1234567890");
					clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.view.View")));
					if(!isElementDisplayedOnScreen(PopUpCloseButton)) {
						swipeOrScroll();
						clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.view.View")));
					}
					clickOnElement(PopUpCloseButton);
					waitTime(1);
					boolean isEnabled = isElementEnabled(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
					assertTrue(isEnabled,"Verify if scan  button enabled for "+FetchUiSpec.getValueUsingId(id));
					clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
					CameraPage cameraPage=new CameraPage(driver);
					cameraPage.clickimage();
					cameraPage.clickOkButton();
					assertTrue(isRetakeButtonDisplayed(),"Verify if retake  button displayed");
					cropCaptureImage();
					clickOnSaveButton();
					assertTrue(isDoccumentUploadPageDisplayed(),"Verify if doccumentupload page is displayed after upload of "+FetchUiSpec.getValueUsingId(id));
				}else {
					clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.view.View")));
					if(!isElementDisplayedOnScreen(PopUpCloseButton)) {
						swipeOrScroll();
						clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.view.View")));
					}
					clickOnElement(PopUpCloseButton);
					waitTime(1);
					boolean isEnabled = isElementEnabled(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
					assertTrue(isEnabled,"Verify if scan  button enabled for "+FetchUiSpec.getValueUsingId(id));
					clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
					CameraPage cameraPage=new CameraPage(driver);
					cameraPage.clickimage();
					cameraPage.clickOkButton();
					assertTrue(isRetakeButtonDisplayed(),"Verify if retake  button displayed");
					cropCaptureImage();
					clickOnSaveButton();
					assertTrue(isDoccumentUploadPageDisplayed(),"Verify if doccumentupload page is displayed after upload of "+FetchUiSpec.getValueUsingId(id));
				}
			}if(id.equals("proofOfRelationship")) {
				if(age.equals("minor") ||  age.equals("infant") ||  age.equals("currentCalenderDate")) {
					clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.view.View")));
					if(!isElementDisplayedOnScreen(PopUpCloseButton)) {
						swipeOrScroll();
						clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.view.View")));
					}
					clickOnElement(PopUpCloseButton);
					waitTime(1);
					boolean isEnabled = isElementEnabled(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
					assertTrue(isEnabled,"Verify if scan  button enabled for "+FetchUiSpec.getValueUsingId(id));
					clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
					CameraPage cameraPage=new CameraPage(driver);
					cameraPage.clickimage();
					cameraPage.clickOkButton();
					assertTrue(isRetakeButtonDisplayed(),"Verify if retake  button displayed");
					cropCaptureImage();
					clickOnSaveButton();
					assertTrue(isDoccumentUploadPageDisplayed(),"Verify if doccumentupload page is displayed after upload of "+FetchUiSpec.getValueUsingId(id));
				}
			}
		}

	}
	public void uploadDoccumentsUpdate(String age,String type) {
		List<String> idList=FetchUiSpec.getAllIds("Documents");
		for(String id : idList) {
			if(type.equals("all") && !id.equals("proofOfException") && !id.equals("proofOfRelationship")) {
				clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.view.View")));
				if(!isElementDisplayedOnScreen(PopUpCloseButton)) {
					swipeOrScroll();
					clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.view.View")));
				}
				clickOnElement(PopUpCloseButton);
				waitTime(1);
				boolean isEnabled = isElementEnabled(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
				assertTrue(isEnabled,"Verify if scan  button enabled for "+FetchUiSpec.getValueUsingId(id));
				clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
				CameraPage cameraPage=new CameraPage(driver);
				cameraPage.clickimage();
				cameraPage.clickOkButton();
				assertTrue(isRetakeButtonDisplayed(),"Verify if retake  button displayed");
				cropCaptureImage();
				clickOnSaveButton();
			}if(id.equals("proofOfRelationship")) {
				if(age.equals("minor") ||  age.equals("infant") ||  age.equals("currentCalenderDate")) {
					clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.view.View")));
					if(!isElementDisplayedOnScreen(PopUpCloseButton)) {
						swipeOrScroll();
						clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.view.View")));
					}
					clickOnElement(PopUpCloseButton);
					waitTime(1);
					boolean isEnabled = isElementEnabled(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
					assertTrue(isEnabled,"Verify if scan  button enabled for "+FetchUiSpec.getValueUsingId(id));
					clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
					CameraPage cameraPage=new CameraPage(driver);
					cameraPage.clickimage();
					cameraPage.clickOkButton();
					assertTrue(isRetakeButtonDisplayed(),"Verify if retake  button displayed");
					cropCaptureImage();
					clickOnSaveButton();
				}
			}

		}
	}


}
