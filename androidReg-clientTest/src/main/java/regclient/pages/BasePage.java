package regclient.pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.HidesKeyboard;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static java.time.Duration.ofSeconds;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;




public class BasePage {
	protected AppiumDriver driver;

	public BasePage(AppiumDriver driver) {
		this.driver = driver;
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}

	protected boolean isElementDisplayed(WebElement element) {
		try {
			waitForElementToBeVisible(element);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	protected void clickOnElement(WebElement element) {
		waitForElementToBeVisible(element);
		element.click();
	}

	private void waitForElementToBeVisible(WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	protected boolean isElementDisplayed(WebElement element, int waitTime) {
		try {
			waitForElementToBeVisible(element, waitTime);
			return true;
		} catch (Exception e) {
			return false;
		}
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

	protected void cropCaptureImage(WebElement element) {
		PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
		Sequence sequence = new Sequence(finger1, 1)
				.addAction(finger1.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(),element.getLocation()))
				.addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
				.addAction(new Pause(finger1, Duration.ofMillis(200)))
				.addAction(finger1.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(),414,598))
				.addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
		driver.perform(Collections.singletonList(sequence));
	}

	protected void clickOnCheckBox() {
		PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
		Sequence sequence = new Sequence(finger1, 1)
				.addAction(finger1.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), 69, 1158)) //temporary solution to click on checkbox using x and y axis
				.addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
				.addAction(new Pause(finger1, Duration.ofMillis(100))) // Add a small pause (adjust duration as needed)
				.addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
		driver.perform(Collections.singletonList(sequence));
	}

	private void waitForElementToBeVisible(WebElement element, int waitTime) {
		WebDriverWait wait = new WebDriverWait(driver, ofSeconds(waitTime));
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	protected void swipeOrScroll()  {

		Dimension size = driver.manage().window().getSize();
		int startX = size.getWidth() / 2;
		int startY = size.getHeight() / 2;
		int endX = startX;
		int endY = (int) (size.getHeight() * 0.28);
		PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
		Sequence sequence = new Sequence(finger1, 1)
				.addAction(finger1.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY))
				.addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
				.addAction(new Pause(finger1, Duration.ofMillis(200)))
				.addAction(finger1.createPointerMove(Duration.ofMillis(100), PointerInput.Origin.viewport(), endX, endY))
				.addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

		driver.perform(Collections.singletonList(sequence));
	}

	protected boolean isElementDisplayedOnScreen(WebElement element) {
		try {
			element.isDisplayed();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
