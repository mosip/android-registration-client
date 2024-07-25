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
		if(!TestDataReader.readData("okButton").equals("") || TestDataReader.readData("okButton").equals(null) )
			clickOnElement(driver.findElement(By.id(TestDataReader.readData("okButton"))));		
	}
	
	public  void clickimage() {
		isElementDisplayed(driver.findElement(By.id(TestDataReader.readData("id"))));
		clickOnElement(driver.findElement(By.id(TestDataReader.readData("id"))));

	}

}
