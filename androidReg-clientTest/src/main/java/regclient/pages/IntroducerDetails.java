package regclient.pages;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

public class IntroducerDetails extends BasePage {

	@AndroidFindBy(accessibility = "CONTINUE")
	private WebElement continuebutton;
	
	public IntroducerDetails(AppiumDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	public  DocumentuploadPage clickOnContinueButton() {
		clickOnElement(continuebutton);
		return new DocumentuploadPage(driver);
		
	}
}
