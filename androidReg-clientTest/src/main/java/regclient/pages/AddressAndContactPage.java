package regclient.pages;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

public class AddressAndContactPage extends BasePage{

	@AndroidFindBy(accessibility = "Address and contact")
	private WebElement addressAndContactPageTitle;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(0)")
	private WebElement mNumberTextBox;
	
	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(1)")
	private WebElement EmailTextBox;
	
	@AndroidFindBy(accessibility = "CONTINUE")
	private WebElement continuebutton;
	
	public AddressAndContactPage(AppiumDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}
	public boolean isAddressAndContactPageTitleDisplay() {
		return isElementDisplayed(addressAndContactPageTitle);

	}
	
	public  void enterMobileNumber(String mobileNumber) {
		clickAndsendKeysToTextBox(mNumberTextBox,mobileNumber);
	}
	
	public  void enterEmailID(String EmailID) {
		clickAndsendKeysToTextBox(EmailTextBox,EmailID);
	}
	
	public  IntroducerDetails clickOnContinueButton() {
		clickOnElement(continuebutton);
		return new IntroducerDetails(driver);
		
	}
}
