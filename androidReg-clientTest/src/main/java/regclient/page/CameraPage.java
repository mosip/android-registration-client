package regclient.page;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
;

public class CameraPage  extends BasePage{

	@AndroidFindBy(id = "com.sec.android.app.camera:id/bottom_background")
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
