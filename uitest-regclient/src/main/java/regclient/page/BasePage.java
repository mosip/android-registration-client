package regclient.page;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.HidesKeyboard;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.netty.handler.timeout.TimeoutException;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.time.Duration.ofSeconds;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Locale;

public class BasePage {
	protected AppiumDriver driver;
	private static String signPublicKey;
	private static String publicKey;
	private static String name;

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
		waitTime(1);
		element.clear();
		waitTime(1);
		element.sendKeys(text);
		waitTime(1);
		((HidesKeyboard) driver).hideKeyboard();
	}
	
	protected void sendKeysToTextBox(WebElement element, String text) {
		this.waitForElementToBeVisible(element);
		element.click();
		waitTime(1);
		element.clear();
		waitTime(1);
		element.sendKeys(text);
		waitTime(1);
		driver.navigate().back();	
	}

	protected String getTextFromLocator(WebElement element) {
		this.waitForElementToBeVisible(element);
		return element.getText();
	}

	protected void cropCaptureImage(WebElement element) {
		PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
		Sequence sequence = new Sequence(finger1, 1)
				.addAction(finger1.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(),getCenterOfElement(element.getLocation(),element.getSize()))) //,43,1166
				.addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
				.addAction(new Pause(finger1, Duration.ofMillis(200)))
				.addAction(finger1.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(),414,598))
				.addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
		driver.perform(Collections.singletonList(sequence));
	}

	private org.openqa.selenium.Point getCenterOfElement(org.openqa.selenium.Point point, Dimension size) {
		int x = (int) (point.getX() + size.getWidth() / 2);
		int y = (int) (point.getY() + size.getHeight()/ 2);
		return new org.openqa.selenium.Point(x, y);
	}


	protected void waitForElementToBeVisible(WebElement element, int waitTime) {
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

	public static void disableAutoRotation() {
		try {
			ProcessBuilder processBuilder;
			String osName = System.getProperty("os.name");
			if (osName.contains("Windows")) {
				processBuilder = new ProcessBuilder("cmd.exe", "/c", "adb shell settings put system accelerometer_rotation 0");

			} else {
				processBuilder = new ProcessBuilder("/bin/bash", "-c", "adb shell settings put system accelerometer_rotation 0");
			}
			processBuilder.redirectErrorStream(true);
			processBuilder.start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected String  getCurrentDate() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");      
		return  currentDateTime.format(formatter);
	}

	protected String  getCurrentDateWord() {
		LocalDate today = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.ENGLISH);
		String formattedDate = today.format(formatter);
		return formattedDate;
	}

	public static void waitTime(int sec) {
		try {
			Thread.sleep(sec*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public WebElement retryFindElement(WebElement element, Duration timeout) {
		int attempts = 0;
		int maxAttempts = 5;

		while (attempts < maxAttempts) {
			try {
				WebDriverWait wait = new WebDriverWait(driver, timeout);
				wait.until(ExpectedConditions.visibilityOf(element));
				return element;
			} catch (StaleElementReferenceException e) {
				System.out.println("StaleElementReferenceException caught. Retrying... " + attempts);
				attempts++;
			} catch (TimeoutException e) {
				System.out.println("TimeoutException caught. Retrying... " + attempts);
				attempts++;
			}
		}
		throw new RuntimeException("Element not found after " + maxAttempts + " attempts");
	}



	protected void clickAndHold() {
		PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
		Sequence sequence = new Sequence(finger1, 1)
				.addAction(finger1.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(),547,2198)) //,43,1166
				.addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
				.addAction(new Pause(finger1, Duration.ofMillis(20000)));
		driver.perform(Collections.singletonList(sequence));
	}

	protected void getMachineDetail() throws UnsupportedFlavorException, IOException, InterruptedException {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		Transferable contents = clipboard.getContents(null);
		if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {

			String copiedText = (String) contents.getTransferData(DataFlavor.stringFlavor);
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(copiedText);

			signPublicKey = jsonNode.get("signPublicKey").asText();
			publicKey = jsonNode.get("publicKey").asText();
			name = jsonNode.get("name").asText();
		} else {
			throw new UnsupportedFlavorException(DataFlavor.stringFlavor);
		}
	}

	public static String getSignPublicKey() {
		return signPublicKey;
	}

	public static String getPublicKey() {
		return publicKey;
	}

	public static String getName() {
		return name;
	}
	
	public WebElement findElementWithRetry(By by) {
	    int MAX_RETRIES = 10;
	    int RETRY_DELAY_MS = 1000;
	    WebElement element = null;

	    for (int i = 0; i < MAX_RETRIES; i++) {
	        try {
	            element = driver.findElement(by);
	            break; // Exit loop if the element is found
	        } catch (NoSuchElementException e) {
	            if (i < MAX_RETRIES - 1) {
	                try {
	                    Thread.sleep(RETRY_DELAY_MS); // Wait before retrying
	                } catch (InterruptedException ie) {
	                    Thread.currentThread().interrupt(); // Restore interrupted status
	                }
	                swipeOrScroll(); // Call swipeOrScroll() after retry attempt fails
	            } else {
	                System.out.println("Element not found after " + MAX_RETRIES + " attempts.");
	                throw e; // Optionally re-throw the exception if all retries fail
	            }
	        }
	    }

	    return element;
	}

	 
	 public WebElement findElement(By by) {
		    int MAX_RETRIES = 10;
		    int RETRY_DELAY_MS = 1000;
		    WebElement element = null;

		    for (int i = 0; i < MAX_RETRIES; i++) {
		        try {
		            element = driver.findElement(by);
		            break; // Exit loop if the element is found
		        } catch (NoSuchElementException e) {
		            if (i < MAX_RETRIES - 1) {
		                try {
		                    Thread.sleep(RETRY_DELAY_MS); // Wait before retrying
		                } catch (InterruptedException ie) {
		                    Thread.currentThread().interrupt(); // Restore interrupted status
		                }
		            } else {
		                throw new NoSuchElementException("Element not found after " + MAX_RETRIES + " attempts.");
		            }
		        }
		    }

		    return element;
		}

	 
	 protected boolean isElementDisplayed(By by) {
			try {
				waitForElementToBeVisible(driver.findElement(by));
				driver.findElement(by).isDisplayed();
				return true;
			} catch (Exception e) {
				return false;
			}
		}
	 
	 protected void clickAtCoordinates(int x, int y) {
		    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
		    Sequence clickSequence = new Sequence(finger, 1)
		            .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y)) // Move to x, y coordinates
		            .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg())) // Press down at x, y coordinates
		            .addAction(new Pause(finger, Duration.ofMillis(200))) // Pause for 200ms
		            .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg())); // Release at x, y coordinates
		    driver.perform(Collections.singletonList(clickSequence));
		}
}
