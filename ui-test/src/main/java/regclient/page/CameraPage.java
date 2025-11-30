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

	public CameraPage(AppiumDriver driver) {
		super(driver);
	}

	public void clickimage() {
		waitTime(1);
		clickAtCoordinates(1840, 598);
	}

	public void clickOkButton() {
		if (isElementDisplayed(okButton))
			clickOnElement(okButton);
		else {
			waitTime(1);
			clickAtCoordinates(43, 78);
		}
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

}
