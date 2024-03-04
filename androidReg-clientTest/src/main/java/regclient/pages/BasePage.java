package regclient.pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.HidesKeyboard;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;


import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static java.time.Duration.ofSeconds;

import java.awt.Dimension;
import java.io.IOException;
import java.util.HashMap;

import javax.lang.model.element.Element;



public class BasePage {
	protected AppiumDriver driver;

	public BasePage(AppiumDriver driver) {
		this.driver = driver;
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}

	protected boolean isElementDisplayed(By locator) {
		return isElementDisplayed(locator, 30);
	}

	protected boolean isElementDisplayed(By locator, long seconds) {
		WebDriverWait wait = new WebDriverWait(driver, ofSeconds(seconds));
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	protected boolean isElementDisplayed(WebElement element) {
		try {
			waitForElementToBeVisible(element);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	protected boolean isElementDisplayed(WebElement element, int waitTime) {
		try {
			waitForElementToBeVisible(element, waitTime);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	protected boolean isElementInvisibleYet(WebElement element) {
		try {
			waitForElementToBeInvisible(element);
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	protected boolean WaitTillElementVisible(WebElement element, int waitTime) {
		try {
			waitForElementToBeInvisible(element, waitTime);
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	protected void clickOnElement(WebElement element) {
		waitForElementToBeVisible(element);
		element.click();

	}

	protected void clickOnElement(By locator) {
		driver.findElement(locator).click();
	}

	private void waitForElementToBeVisible(WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	private void waitForElementToBeVisible(WebElement element, int waitTime) {
		WebDriverWait wait = new WebDriverWait(driver, ofSeconds(waitTime));
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	private void waitForElementToBeInvisible(WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, ofSeconds(30));
		wait.until(ExpectedConditions.invisibilityOf(element));
	}

	private void waitForElementToBeInvisible(WebElement element, int waitTime) {
		WebDriverWait wait = new WebDriverWait(driver, ofSeconds(waitTime));
		wait.until(ExpectedConditions.invisibilityOf(element));
	}

	protected boolean isElementEnabled(WebElement element) {
		try {
			waitForElementToBeVisible(element);
			element.isEnabled();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	protected void clearTextBoxAndSendKeys(WebElement element, String text) {
		this.waitForElementToBeVisible(element);
		element.clear();
		element.sendKeys(text);
	}

	protected void sendKeysToTextBox(WebElement element, String text) {
		this.waitForElementToBeVisible(element);
		element.sendKeys(text);
	}

	protected void clickAndsendKeysToTextBox(WebElement element, String text) {
		this.waitForElementToBeVisible(element);
		element.click();
		element.clear();
		element.sendKeys(text);
		((HidesKeyboard) driver).hideKeyboard();
	}

	protected String getTextFromLocator(WebElement element) {
		this.waitForElementToBeVisible(element);
		return element.getText();
	}

	protected  String retrieToGetElement(WebElement element) {
		int maxRetries = 3; 
		String text = null;
		for (int i = 0; i < maxRetries; i++) {
			try {
				text = getTextFromLocator(element);
				break; 
			} catch (StaleElementReferenceException e) {
				if (i == maxRetries - 1) {
					throw e; 
				}
			}
		}
		return text;

	}
	
}
