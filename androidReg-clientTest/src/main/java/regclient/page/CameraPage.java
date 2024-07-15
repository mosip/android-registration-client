package regclient.page;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
;

public class CameraPage  extends BasePage{

	@AndroidFindBy(id = "com.motorola.camera3:id/capture_bar_shutter_button")
	private WebElement clickImageButton;
	
	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"OK\"]")
	private WebElement okButton;
	
	public CameraPage(AppiumDriver driver) {
		super(driver);
	}
	
	public  void clickOkButton() {
		if(isElementDisplayed(okButton))
			clickOnElement(okButton);		
	}
	
	public  void clickimage() {
		clickOnElement(clickImageButton);
	}

}
