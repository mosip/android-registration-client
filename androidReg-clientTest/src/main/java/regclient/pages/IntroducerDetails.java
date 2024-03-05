package regclient.pages;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;

public class IntroducerDetails extends BasePage {

	@AndroidFindBy(accessibility = "CONTINUE")
	private WebElement continueButton;
	
	public IntroducerDetails(AppiumDriver driver) {
		super(driver);
	}

	public  DocumentuploadPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new DocumentuploadPage(driver);
		
	}
}
