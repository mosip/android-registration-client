package regclient.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.utils.TestDataReader;
;

public class CameraPage  extends BasePage{

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"OK\"]")
	private WebElement okButton;

	public CameraPage(AppiumDriver driver) {
		super(driver);
	}

	public  void clickOkButton() {
//		if(isElementDisplayed(okButton))
//			clickOnElement(okButton);		
//		else {
			waitTime(7);
			clickAtCoordinates(633,2042);
//		}
	}

	public  void clickimage() {
		waitTime(2);
		isElementDisplayed(driver.findElement(By.id(TestDataReader.readData("id"))));
		clickOnElement(driver.findElement(By.id(TestDataReader.readData("id"))));

	}

}
