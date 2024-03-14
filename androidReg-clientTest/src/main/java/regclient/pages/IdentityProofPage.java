package regclient.pages;

import java.awt.Dimension;
import java.awt.Point;
import java.time.Duration;
import java.util.Collections;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;

public class IdentityProofPage extends BasePage{

	@AndroidFindBy(accessibility = "SAVE")
	private WebElement saveButton;

	@AndroidFindBy(accessibility = "RETAKE")
	private WebElement retakeButton;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.view.View\").instance(8)")
	private WebElement imageleftCorner;

	public IdentityProofPage(AppiumDriver driver) {
		super(driver);
	}

	public  DocumentuploadPage clickOnSaveButton() {
		clickOnElement(saveButton);
		return new DocumentuploadPage(driver);
	}

	public boolean isRetakeButtonDisplayed() {
		return isElementDisplayed(retakeButton);
	}

	public void cropCaptureImage() {
		cropCaptureImage(imageleftCorner);
	}

}
