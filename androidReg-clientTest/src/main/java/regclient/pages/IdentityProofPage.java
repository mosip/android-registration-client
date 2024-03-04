package regclient.pages;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

public class IdentityProofPage extends BasePage{

	@AndroidFindBy(accessibility = "SAVE")
	private WebElement saveButton;

	@AndroidFindBy(accessibility = "RETAKE")
	private WebElement retakeButton;

	public IdentityProofPage(AppiumDriver driver) {
		super(driver);
	}

	public  DocumentuploadPage clickOnSaveButton() {
		clickOnElement(saveButton);
		return new DocumentuploadPage(driver);
	}

	public boolean isRetakeButtonDisplay() {
		return isElementDisplayed(retakeButton);
	}

}
