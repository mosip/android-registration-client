package regclient.pages;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;


public class AddressAndContactPage extends BasePage{

	@AndroidFindBy(accessibility = "Address and contact")
	private WebElement addressAndContactPageTitle;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(0)")
	private WebElement mobileNumberTextBox;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(1)")
	private WebElement emailTextBox;

	@AndroidFindBy(accessibility = "CONTINUE")
	private WebElement continueButton;

	public AddressAndContactPage(AppiumDriver driver) {
		super(driver);
	}

	public boolean  isAddressAndContactPageTitleDisplayed() {
		return isElementDisplayed(addressAndContactPageTitle);
	}

	public  void enterMobileNumber(String mobileNumber) {
		clickAndsendKeysToTextBox(mobileNumberTextBox,mobileNumber);
	}

	public  void enterEmailID(String EmailID) {
		clickAndsendKeysToTextBox(emailTextBox,EmailID);
	}

	public  IntroducerDetails clickOnContinueButton() {
		clickOnElement(continueButton);
		return new IntroducerDetails(driver);
	}
}
