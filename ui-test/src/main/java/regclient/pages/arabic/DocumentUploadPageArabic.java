package regclient.pages.arabic;

import static org.testng.Assert.assertTrue;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import regclient.api.FetchUiSpec;
import regclient.page.BiometricDetailsPage;
import regclient.page.CameraPage;
import regclient.page.DocumentUploadPage;

public class DocumentUploadPageArabic extends DocumentUploadPage {

	@AndroidFindBy(accessibility = "يزيل")
	private WebElement PopUpCloseButton;

	@AndroidFindBy(accessibility = "رجوع")
	private WebElement backButton;

	@AndroidFindBy(accessibility = "يكمل")
	private WebElement continueButton;

	@AndroidFindBy(accessibility = "يمسح")
	private WebElement deleteButton;

	@AndroidFindBy(xpath = "//android.widget.ImageView")
	private WebElement captureImage;

	@AndroidFindBy(accessibility = "يحفظ")
	private WebElement saveButton;

	@AndroidFindBy(accessibility = "استعادة")
	private WebElement retakeButton;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.view.View\").instance(8)")
	private WebElement imageleftCorner;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"OK\"]")
	private WebElement okButton;

	public DocumentUploadPageArabic(AppiumDriver driver) {
		super(driver);
	}

	public BiometricDetailsPage clickOnContinueButton() {
		waitTime(1);
		clickOnElement(continueButton);
		waitTime(1);
		return new BiometricDetailsPageArabic(driver);
	}

	@SuppressWarnings("deprecation")
	public boolean isDoccumentUploadPageDisplayed() {
		scrollToTop();
		swipeRightUntilTabDisplayed("تحميل المستندات");

		return driver.findElements(MobileBy.AccessibilityId(FetchUiSpec.getScreenTitle("Documents"))).size() > 0;
	}

	public DocumentUploadPage clickOnSaveButton() {
		clickOnElement(saveButton);
		return new DocumentUploadPageArabic(driver);
	}

	public boolean isRetakeButtonDisplayed() {
		return isElementDisplayed(retakeButton);
	}

	public void cropCaptureImage() {
		isElementDisplayed(captureImage);
		cropCaptureImage(imageleftCorner);
	}

	public void uploadDoccuments(String age, String type) {
		List<String> idList = FetchUiSpec.getAllIds("Documents");
		for (String id : idList) {
			if (FetchUiSpec.getRequiredTypeUsingId(id)) {
				if (type.equalsIgnoreCase("ReferenceNumber")) {
					clickAndsendKeysToTextBox(
							findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
									+ FetchUiSpec.getValueUsingId(id)
									+ "\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.EditText")),
							"1234567890");
					clickOnElement(findElementWithRetry(
							By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id)
									+ "\")]/parent::android.view.View/parent::android.view.View")));
					if (!isElementDisplayedOnScreen(PopUpCloseButton)) {
						swipeUp();
						clickOnElement(findElementWithRetry(By.xpath(
								"//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id)
										+ "\")]/parent::android.view.View/parent::android.view.View")));
					}
					clickOnElement(PopUpCloseButton);
					waitTime(1);
					boolean isEnabled = isElementEnabled(findElementWithRetry(
							By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id)
									+ "\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
					assertTrue(isEnabled, "Verify if scan  button enabled for " + FetchUiSpec.getValueUsingId(id));
					clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
							+ FetchUiSpec.getValueUsingId(id)
							+ "\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
					CameraPage cameraPage = new CameraPage(driver);
					cameraPage.handleCameraPermission();
					cameraPage.clickimage();
					cameraPage.clickOkButton();
					waitTime(1);
					applyOrientation();
					waitTime(1);
					assertTrue(isRetakeButtonDisplayed(), "Verify if retake  button displayed");
					cropCaptureImage();
					clickOnSaveButton();
					assertTrue(isDoccumentUploadPageDisplayed(),
							"Verify if doccumentupload page is displayed after upload of "
									+ FetchUiSpec.getValueUsingId(id));
				} else {
					clickOnElement(findElementWithRetry(
							By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id)
									+ "\")]/parent::android.view.View/parent::android.view.View")));
					if (!isElementDisplayedOnScreen(PopUpCloseButton)) {
						swipeUp();
						clickOnElement(findElementWithRetry(By.xpath(
								"//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id)
										+ "\")]/parent::android.view.View/parent::android.view.View")));
					}
					waitTime(1);
					clickOnElement(PopUpCloseButton);
					waitTime(1);
					boolean isEnabled = isElementEnabled(findElementWithRetry(
							By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id)
									+ "\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
					assertTrue(isEnabled, "Verify if scan  button enabled for " + FetchUiSpec.getValueUsingId(id));
					clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
							+ FetchUiSpec.getValueUsingId(id)
							+ "\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
					CameraPage cameraPage = new CameraPage(driver);
					cameraPage.handleCameraPermission();
					cameraPage.clickimage();
					waitTime(1);
					cameraPage.clickOkButton();
					waitTime(1);
					applyOrientation();
					waitTime(1);
					assertTrue(isRetakeButtonDisplayed(), "Verify if retake  button displayed");
					cropCaptureImage();
					clickOnSaveButton();
					assertTrue(isDoccumentUploadPageDisplayed(),
							"Verify if doccumentupload page is displayed after upload of "
									+ FetchUiSpec.getValueUsingId(id));
				}
			}
			if (id.equals("proofOfRelationship")) {
				if (age.equals("minor") || age.equals("infant") || age.equals("currentCalenderDate")) {
					clickOnElement(findElementWithRetry(
							By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id)
									+ "\")]/parent::android.view.View/parent::android.view.View")));
					if (!isElementDisplayedOnScreen(PopUpCloseButton)) {
						swipeUp();
						clickOnElement(findElementWithRetry(By.xpath(
								"//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id)
										+ "\")]/parent::android.view.View/parent::android.view.View")));
					}
					waitTime(1);
					clickOnElement(PopUpCloseButton);
					waitTime(1);
					boolean isEnabled = isElementEnabled(findElementWithRetry(
							By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id)
									+ "\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
					assertTrue(isEnabled, "Verify if scan  button enabled for " + FetchUiSpec.getValueUsingId(id));
					clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
							+ FetchUiSpec.getValueUsingId(id)
							+ "\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
					CameraPage cameraPage = new CameraPage(driver);
					cameraPage.handleCameraPermission();
					cameraPage.clickimage();
					waitTime(1);
					cameraPage.clickOkButton();
					waitTime(1);
					applyOrientation();
					waitTime(1);
					assertTrue(isRetakeButtonDisplayed(), "Verify if retake  button displayed");
					cropCaptureImage();
					clickOnSaveButton();
					assertTrue(isDoccumentUploadPageDisplayed(),
							"Verify if doccumentupload page is displayed after upload of "
									+ FetchUiSpec.getValueUsingId(id));
				}
			}
		}

	}

	public void uploadDoccumentsUpdate(String age, String type) {
		List<String> idList = FetchUiSpec.getAllIds("Documents");
		for (String id : idList) {
			if (type.equals("all") && !id.equals("proofOfException") && !id.equals("proofOfRelationship")) {
				clickOnElement(findElementWithRetry(
						By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id)
								+ "\")]/parent::android.view.View/parent::android.view.View")));
				if (!isElementDisplayedOnScreen(PopUpCloseButton)) {
					swipeUp();
					clickOnElement(findElementWithRetry(
							By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id)
									+ "\")]/parent::android.view.View/parent::android.view.View")));
				}
				clickOnElement(PopUpCloseButton);
				waitTime(1);
				boolean isEnabled = isElementEnabled(findElementWithRetry(
						By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id)
								+ "\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
				assertTrue(isEnabled, "Verify if scan  button enabled for " + FetchUiSpec.getValueUsingId(id));
				clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
						+ FetchUiSpec.getValueUsingId(id)
						+ "\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
				CameraPage cameraPage = new CameraPage(driver);
				cameraPage.handleCameraPermission();
				cameraPage.clickimage();
				cameraPage.clickOkButton();
				waitTime(1);
				applyOrientation();
				waitTime(1);
				assertTrue(isRetakeButtonDisplayed(), "Verify if retake  button displayed");
				cropCaptureImage();
				clickOnSaveButton();
			}
			if (id.equals("proofOfRelationship")) {
				if (age.equals("minor") || age.equals("infant") || age.equals("currentCalenderDate")) {
					clickOnElement(findElementWithRetry(
							By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id)
									+ "\")]/parent::android.view.View/parent::android.view.View")));
					if (!isElementDisplayedOnScreen(PopUpCloseButton)) {
						swipeUp();
						clickOnElement(findElementWithRetry(By.xpath(
								"//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id)
										+ "\")]/parent::android.view.View/parent::android.view.View")));
					}
					clickOnElement(PopUpCloseButton);
					waitTime(1);
					boolean isEnabled = isElementEnabled(findElementWithRetry(
							By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id)
									+ "\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
					assertTrue(isEnabled, "Verify if scan  button enabled for " + FetchUiSpec.getValueUsingId(id));
					clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
							+ FetchUiSpec.getValueUsingId(id)
							+ "\")]/parent::android.view.View/parent::android.view.View/following-sibling::android.widget.Button")));
					CameraPage cameraPage = new CameraPage(driver);
					cameraPage.handleCameraPermission();
					cameraPage.clickimage();
					cameraPage.clickOkButton();
					waitTime(1);
					applyOrientation();
					waitTime(1);
					assertTrue(isRetakeButtonDisplayed(), "Verify if retake  button displayed");
					cropCaptureImage();
					clickOnSaveButton();
				}
			}

		}
	}

	public boolean isPacketSizeDisplayed() {
		try {
			WebElement packetSize = driver
					.findElement(By.xpath("//android.view.View[contains(@content-desc,'Size:')]"));

			String sizeText = packetSize.getAttribute("contentDescription");
			return sizeText.matches("Size: \\d+(\\.\\d+)?\\s?(KB|MB)");
		} catch (Exception e) {
			return false;
		}
	}

	public void swipeRightUntilTabDisplayed(String tabContentDesc) {
		// If already displayed, no need to swipe
		if (!driver.findElements(By.xpath("//android.view.View[@content-desc='" + tabContentDesc + "']")).isEmpty()) {
			return;
		}

		for (int i = 0; i < 10; i++) {
			if (!driver.findElements(By.xpath("//android.view.View[@content-desc='" + tabContentDesc + "']")).isEmpty())
				break;

			// Check if swipe anchor tab exists before using it
			List<WebElement> anchorList = driver
					.findElements(By.xpath("//android.view.View[@content-desc='التفاصيل الديموغرافية']"));

			if (anchorList.isEmpty())
				break;

			WebElement startElement = anchorList.get(0);
			int startX = startElement.getLocation().getX() + startElement.getSize().getWidth() / 2;
			int startY = startElement.getLocation().getY() + startElement.getSize().getHeight() / 2;

			PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
			Sequence swipe = new Sequence(finger, 1);

			swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
			swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
			swipe.addAction(finger.createPointerMove(Duration.ofMillis(1000), PointerInput.Origin.viewport(),
					startX + 500, startY));
			swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

			driver.perform(Arrays.asList(swipe));

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
