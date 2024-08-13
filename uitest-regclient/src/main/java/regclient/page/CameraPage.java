package regclient.page;

import org.openqa.selenium.By;

import io.appium.java_client.AppiumDriver;
import regclient.utils.TestDataReader;
;

public class CameraPage  extends BasePage{

	public CameraPage(AppiumDriver driver) {
		super(driver);
	}
	
	public  void clickOkButton() {
		if(!TestDataReader.readData("okButton").equals("")) {
			waitTime(2);
			clickOnElement(driver.findElement(By.id(TestDataReader.readData("okButton"))));		
		}else {
			waitTime(7);
			clickAtCoordinates(633,2042);
		}
	}
	
	public  void clickimage() {
		waitTime(2);
		isElementDisplayed(driver.findElement(By.id(TestDataReader.readData("id"))));
		clickOnElement(driver.findElement(By.id(TestDataReader.readData("id"))));

	}

}
