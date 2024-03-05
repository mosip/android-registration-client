package regclient.pages;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;

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

	public boolean isRetakeButtonDisplayed() {
		return isElementDisplayed(retakeButton);
	}

}
