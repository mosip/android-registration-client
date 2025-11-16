package regclient.page;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.HidesKeyboard;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.remote.SupportsContextSwitching;
import io.netty.handler.timeout.TimeoutException;


import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
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
import java.util.Random;
import java.util.Set;

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

	protected void clickAndsendKeysToTextBox2(WebElement element, String text) {
		this.waitForElementToBeVisible(element);
		element.click();
		waitTime(1);
		element.clear();
		waitTime(1);
		element.sendKeys(text);
		waitTime(1);
		driver.navigate().back();
	}

	protected void sendKeysToTextBox(WebElement element, String text) {
		this.waitForElementToBeVisible(element);
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
				.addAction(finger1.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(),
						getCenterOfElement(element.getLocation(), element.getSize()))) // ,43,1166
				.addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
				.addAction(new Pause(finger1, Duration.ofMillis(200)))
				.addAction(finger1.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), 414, 598))
				.addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
		driver.perform(Collections.singletonList(sequence));
	}

	private org.openqa.selenium.Point getCenterOfElement(org.openqa.selenium.Point point, Dimension size) {
		int x = (int) (point.getX() + size.getWidth() / 2);
		int y = (int) (point.getY() + size.getHeight() / 2);
		return new org.openqa.selenium.Point(x, y);
	}

	protected void waitForElementToBeVisible(WebElement element, int waitTime) {
		WebDriverWait wait = new WebDriverWait(driver, ofSeconds(waitTime));
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	protected void swipeOrScroll() {

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
				.addAction(
						finger1.createPointerMove(Duration.ofMillis(100), PointerInput.Origin.viewport(), endX, endY))
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
				processBuilder = new ProcessBuilder("cmd.exe", "/c",
						"adb shell settings put system accelerometer_rotation 0");

			} else {
				processBuilder = new ProcessBuilder("/bin/bash", "-c",
						"adb shell settings put system accelerometer_rotation 0");
			}
			processBuilder.redirectErrorStream(true);
			processBuilder.start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected String getCurrentDate() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		return currentDateTime.format(formatter);
	}

	protected String getCurrentDateWord() {
		LocalDate today = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.ENGLISH);
		String formattedDate = today.format(formatter);
		return formattedDate;
	}

	public static void waitTime(int sec) {
		try {
			Thread.sleep(sec * 1000);
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
				.addAction(finger1.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), 541, 1846))
				.addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
				.addAction(new Pause(finger1, Duration.ofSeconds(2)))
				.addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

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
		int RETRY_DELAY_MS = 2000;
		WebElement element = null;

		for (int i = 0; i < MAX_RETRIES; i++) {
			try {
				try {
					Thread.sleep(RETRY_DELAY_MS); // Wait before retrying
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt(); // Restore interrupted status
				}
				element = driver.findElement(by);
				break; // Exit loop if the element is found
			} catch (NoSuchElementException e) {
				if (i < MAX_RETRIES - 1) {
					swipeOrScroll(); // Call swipeOrScroll() after retry attempt fails
				} else {
					System.out.println("Element not found after " + MAX_RETRIES + " attempts.");
					// throw e; // Optionally re-throw the exception if all retries fail
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
		int attempts = 0;
		while (attempts < 4) {
			try {
				waitForElementToBeVisible(driver.findElement(by));
				return driver.findElement(by).isDisplayed();
			} catch (Exception e) {
				attempts++;
				if (attempts == 4) {
					return false; // After 3 attempts, return false
				}
			}
		}
		return false;
	}

	protected void clickAtCoordinates(int x, int y) {
		PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
		Sequence clickSequence = new Sequence(finger, 1)
				.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y)) // Move to x,
																											// y
																											// coordinates
				.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg())) // Press down at x, y
																							// coordinates
				.addAction(new Pause(finger, Duration.ofMillis(200))) // Pause for 200ms
				.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg())); // Release at x, y
																							// coordinates
		driver.perform(Collections.singletonList(clickSequence));
	}

	private static final Random random = new Random();

	public static String generateData(String validator) {
		if (validator == null || validator.isEmpty()) {
			return generateStringOfLength(3, 30);
		}

		switch (validator) {
		case "^(?=.{2,50}$).*":
			return generateStringOfLength(2, 30);

		case "^([0-9]{10})$":
			return generateTenDigitNumber();

		case "^(1869|18[7-9][0-9]|19[0-9][0-9]|20[0-9][0-9])/([0][1-9]|1[0-2])/([0][1-9]|[1-2][0-9]|3[01])$":
			return generateDateInRange();

		case "^(?=.{3,50}$).*":
			return generateStringOfLength(3, 30);

		case "^[+]*([0-9]{1})([0-9]{9})$":
			return generateNineDigitNumber() + "1";

		case "^[0-9]{9}$":
			return generateNineDigitNumber();

		case "^[A-Za-z0-9_\\-]+(\\.[A-Za-z0-9_]+)*@[A-Za-z0-9_-]+(\\.[A-Za-z0-9_]+)*(\\.[a-zA-Z]{2,})$":
			return generateEmail();

		case "^([0-9]{10,30})$":
			return generateTenDigitNumber();

		default:
			return "abcd";
		}
	}

	private static String generateStringOfLength(int min, int max) {
		int length = min + random.nextInt(max - min + 1);
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			char c = (char) (random.nextInt(26) + 'a');
			sb.append(c);
		}
		return sb.toString();
	}

	private static String generateSixDigitNumber() {
		return String.format("%06d", random.nextInt(1000000));
	}

	private static String generateTwoDigitNumber() {
		return String.format("%02d", random.nextInt(100));
	}

	private static String generateOneDigitNumber() {
		return String.valueOf(random.nextInt(10));
	}

	private static String generateDateInRange() {
		int year = 1869 + random.nextInt(200); // Generates a year between 1869 and 2068
		int month = 1 + random.nextInt(12); // Generates a month between 1 and 12
		int day = 1 + random.nextInt(28); // Generates a day between 1 and 28 (to keep it simple)

		return String.format("%04d/%02d/%02d", year, month, day);
	}

	private static String generateNineDigitNumber() {
		return String.format("%09d", random.nextInt(1000000000));
	}

	private static final Random randomten = new Random();

	private static String generateTenDigitNumber() {
		long number = 1000000000L + (long) (randomten.nextDouble() * 9000000000L);
		return String.valueOf(number);
	}

	private static String generateEmail() {
		String[] domains = { "example.com", "test.com", "email.com" };
		String localPart = generateStringOfLength(3, 10);
		String domain = domains[random.nextInt(domains.length)];
		return localPart + "@" + domain;
	}

	protected void switchContext(String targetContext) {
		Set<String> contexts = ((SupportsContextSwitching) driver).getContextHandles();
		for (String context : contexts) {
			System.out.println("Available context: " + context);
		}

		for (String context : contexts) {
			if (context.toLowerCase().contains(targetContext.toLowerCase())) {
				((SupportsContextSwitching) driver).context(context);
				System.out.println("Switched to context: " + context);
				return;
			}
		}

		throw new RuntimeException("Target context not found: " + targetContext);
	}

	protected void openArcApplication(String targetContext) {
		AndroidDriver driver = (AndroidDriver) this.driver;

		if (driver.isAppInstalled("com.android.chrome")) {
			driver.terminateApp("com.android.chrome");
		}

		driver.activateApp("io.mosip.registration_client");

		try {
			switchContext(targetContext);
		} catch (RuntimeException ex) {
			System.out.println("Target context not available: " + targetContext);
			throw ex;
		}
	}

	public static void enableWifiAndData() throws IOException {

		Process wifiProcess = new ProcessBuilder("adb", "shell", "svc", "wifi", "enable").start();
		Process dataProcess = new ProcessBuilder("adb", "shell", "svc", "data", "enable").start();
		try {
			if (wifiProcess.waitFor() != 0 || dataProcess.waitFor() != 0) {
				throw new IOException("Failed to enable WiFi/Data");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException("Interrupted while enabling WiFi/Data", e);
		}
	}

	public static void disableWifiAndData() throws IOException {
		new ProcessBuilder("adb", "shell", "svc", "wifi", "disable").start();
		new ProcessBuilder("adb", "shell", "svc", "data", "disable").start();
	}

	public WebElement findElementIfExists(By locator) {
		try {
			return findElementWithRetry(locator); // reuse your existing retry logic
		} catch (Exception e) {
			// Optional: log for debugging
			System.out.println("⚠️ Element not found after retries: " + locator);
			return null; // prevents NoSuchElementException / NPE
		}
	}

	public String extractValue(WebElement e) {
		if (e == null)
			return "";
		try {
			String t = safeTrim(e.getText());
			if (!t.isEmpty())
				return t;
		} catch (Exception ignored) {
		}
		try {
			String cd = safeTrim(e.getAttribute("content-desc"));
			if (!cd.isEmpty())
				return cd;
		} catch (Exception ignored) {
		}
		try {
			WebElement child = e.findElement(By.xpath(".//*"));
			if (child != null) {
				String ct = safeTrim(child.getText());
				if (!ct.isEmpty())
					return ct;
				String ccd = safeTrim(child.getAttribute("content-desc"));
				if (!ccd.isEmpty())
					return ccd;
			}
		} catch (Exception ignored) {
		}
		return "";
	}

	private String safeTrim(String s) {
		return s == null ? "" : s.trim();
	}

	public String getVisibleValue(WebElement e) {
		if (e == null)
			return "";
		try {
			String t = e.getText();
			if (t != null && !t.trim().isEmpty())
				return t.trim();
		} catch (Exception ignored) {
		}
		try {
			String cd = e.getAttribute("content-desc");
			if (cd != null && !cd.trim().isEmpty())
				return cd.trim();
		} catch (Exception ignored) {
		}
		try {
			WebElement child = e.findElement(By.xpath(".//*"));
			if (child != null) {
				String ct = child.getText();
				if (ct != null && !ct.trim().isEmpty())
					return ct.trim();
				String ccd = child.getAttribute("content-desc");
				if (ccd != null && !ccd.trim().isEmpty())
					return ccd.trim();
			}
		} catch (Exception ignored) {
		}
		return "";
	}

	public String safeGetAttr(WebElement e, String name) {
		try {
			String v = e.getAttribute(name);
			return v == null ? "" : v;
		} catch (Exception ex) {
			return "";
		}
	}

	public String extract(WebElement e) {
		if (e == null)
			return "";
		try {
			String t = e.getText();
			if (t != null && !t.trim().isEmpty())
				return t.trim();
		} catch (Exception ignored) {
		}
		try {
			String cd = e.getAttribute("content-desc");
			if (cd != null && !cd.trim().isEmpty())
				return cd.trim();
		} catch (Exception ignored) {
		}
		try {
			WebElement child = e.findElement(By.xpath(".//*"));
			if (child != null) {
				try {
					String ct = child.getText();
					if (ct != null && !ct.trim().isEmpty())
						return ct.trim();
				} catch (Exception ignored) {
				}
				try {
					String ccd = child.getAttribute("content-desc");
					if (ccd != null && !ccd.trim().isEmpty())
						return ccd.trim();
				} catch (Exception ignored) {
				}
			}
		} catch (Exception ignored) {
		}
		return "";
	}

	protected void scrollToTop() {
		Dimension size = driver.manage().window().getSize();
		int startX = size.getWidth() / 2;
		int startY = (int) (size.getHeight() * 0.75);
		int endX = startX;
		int endY = (int) (size.getHeight() * 0.25);

		PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
		Sequence scrollUp = new Sequence(finger, 1)
				.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY))
				.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
				.addAction(new Pause(finger, Duration.ofMillis(200)))
				.addAction(finger.createPointerMove(Duration.ofMillis(400), PointerInput.Origin.viewport(), endX, endY))
				.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
		for (int i = 0; i < 5; i++) {
			driver.perform(Collections.singletonList(scrollUp));
		}
	}
}
