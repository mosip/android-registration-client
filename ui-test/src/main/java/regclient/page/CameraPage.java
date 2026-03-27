package regclient.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.utils.TestDataReader;

public class CameraPage extends BasePage {

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"OK\"]")
	private WebElement okButton;

	@AndroidFindBy(xpath = "//android.view.View[@content-desc='CANCEL']")
	private WebElement cancelButton;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_message")
	private WebElement permissionMessage;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_allow_foreground_only_button")
	private WebElement allowWhileUsingButton;

	@AndroidFindBy(accessibility = "RETAKE")
	private WebElement retakeButton;
	
	@AndroidFindBy(id = "com.android.camera2:id/done_button")
	private WebElement okBtn;

	public CameraPage(AppiumDriver driver) {
		super(driver);
	}

	public void clickimage() {
		for (int i = 0; i < 3; i++) {
			clickAtCoordinates(732, 696);

			if (isElementDisplayed(okBtn, 2)) {
				break;
			}
		}
	}

	public void clickOkButton() {
		waitTime(1);
		clickOnElement2(okBtn);
	}

	public void clickCancelButtonOfQrScanner() {
		clickOnElement(cancelButton);
	}

	public void handleCameraPermission() {
		try {
			if (permissionMessage != null && isElementDisplayed(permissionMessage, 3)) {
				clickOnElement(allowWhileUsingButton);
				waitTime(1);
			}
		} catch (Exception ignored) {
		}
	}

	public boolean isRetakeButtonDisplayed() {
		return isElementDisplayed(retakeButton);
	}

}
