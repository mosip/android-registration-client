package regclient.page;

import io.appium.java_client.AppiumDriver;

import io.appium.java_client.HidesKeyboard;
import io.appium.java_client.MobileBy;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.remote.SupportsContextSwitching;
import io.appium.java_client.remote.SupportsRotation;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import regclient.pages.english.BiometricDetailsPageEnglish;
import regclient.utils.TestDataReader;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.time.Duration.ofSeconds;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class BasePage {
	protected AppiumDriver driver;
	private static String signPublicKey;
	private static String publicKey;
	private static String name;
	public static String email;

	public BasePage(AppiumDriver driver) {
		this.driver = driver;
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);

	}

	private static final Logger logger = LoggerFactory.getLogger(BasePage.class);

	protected boolean isElementDisplayed(WebElement element) {
		try {
			waitForElementToBeVisible(element);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	protected boolean isElementDisplayed(By locator) {
		try {
			waitForElementToBeVisible(locator, 20);
			return driver.findElement(locator).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	protected void clickOnElement(WebElement element) {
		waitForElementToBeVisible(element);
		element.click();
	}

	protected void clickOnElement2(WebElement element) {
		waitForElementToBeClickable(element);
		element.click();
	}

	public void click(By locator) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		wait.ignoring(StaleElementReferenceException.class);
		WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
		element.click();
	}

	private void waitForElementToBeVisible(WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, ofSeconds(20));
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	protected WebElement waitForElementToBeClickable(WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		return wait.until(ExpectedConditions.elementToBeClickable(element));
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
			return element.isEnabled();
		} catch (Exception e) {
			return false;
		}
	}

	protected boolean isElementDisabled(WebElement element) {
		try {
			waitForElementToBeVisible(element);
			return !element.isEnabled() || "false".equalsIgnoreCase(element.getAttribute("clickable"));
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
	
	protected void clickAndsendKeysToTextBox4(WebElement element, String text) {
		this.waitForElementToBeVisible(element);
		element.click();
		waitTime(1);
		element.clear();
		waitTime(1);
		element.sendKeys(text);
	}

	protected void clickAndsendKeysToTextBox3(WebElement element, String text) {
		this.waitForElementToBeClickable(element);
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

	protected void sendKeys(By locator, String text) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
		el.click();
		el.clear();
		el.sendKeys(text);
		((HidesKeyboard) driver).hideKeyboard();
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
				.addAction(finger1.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), 623, 261))
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
			Thread.sleep(sec * 1000L);
		} catch (InterruptedException e) {
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
				logger.info("StaleElementReferenceException caught. Retrying... " + attempts);
				attempts++;
			} catch (TimeoutException e) {
				logger.info("TimeoutException caught. Retrying... " + attempts);
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
				element = driver.findElement(by);

				element.isDisplayed();

				return element;

			} catch (NoSuchElementException | StaleElementReferenceException e) {

				if (i < MAX_RETRIES - 1) {
					try {
						Thread.sleep(RETRY_DELAY_MS);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
					}

					// scroll and retry
					swipeOrScroll();

				} else {
					throw new NoSuchElementException(
							"Element not found or stale after " + MAX_RETRIES + " attempts: " + by);
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

	protected void clickAtCoordinates(int x, int y) {
		PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
		Sequence clickSequence = new Sequence(finger, 1)
				.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y))
				.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
				.addAction(new Pause(finger, Duration.ofMillis(600))) // Pause for 600ms
				.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg())); // Release at x, y
																							// coordinates
		driver.perform(Collections.singletonList(clickSequence));
	}

	private static final Random random = new Random();

	public static String generateData(String validator) {
		if (validator == null || validator.isEmpty()) {
			return generateStringOfLength(3, 10);
		}

		switch (validator) {
		case "^(?=.{2,50}$).*":
			return generateStringOfLength(2, 10);

		case "^([0-9]{10})$":
			return generateTenDigitNumber();

		case "^(1869|18[7-9][0-9]|19[0-9][0-9]|20[0-9][0-9])/([0][1-9]|1[0-2])/([0][1-9]|[1-2][0-9]|3[01])$":
			return generateDateInRange();

		case "^(?=.{3,50}$).*":
			return generateStringOfLength(3, 10);

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

	private static String generateDateInRange() {
		int currentYear = java.time.Year.now().getValue();
		int year = 1869 + random.nextInt(currentYear - 1869 + 1); // Up to current year
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
		email = localPart + "@" + domain;
		return email;
	}

	protected boolean switchToWebViewIfAvailable() {
		for (int i = 0; i < 10; i++) { // wait up to ~5 seconds
			for (String ctx : ((SupportsContextSwitching) driver).getContextHandles()) {
				if (ctx.toUpperCase().contains("WEBVIEW")) {
					((SupportsContextSwitching) driver).context(ctx);
					return true;
				}
			}
			try {
				Thread.sleep(500);
			} catch (Exception ignored) {
			}
		}
		return false; // no webview
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
		Process wifiProcess = new ProcessBuilder("adb", "shell", "svc", "wifi", "disable").start();
		Process dataProcess = new ProcessBuilder("adb", "shell", "svc", "data", "disable").start();
		try {
			if (wifiProcess.waitFor() != 0 || dataProcess.waitFor() != 0) {
				throw new IOException("Failed to disable WiFi/Data");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException("Interrupted while disabling WiFi/Data", e);
		}
	}

	public WebElement findElementIfExists(By locator) {
		try {
			return findElementWithRetry(locator); // reuse your existing retry logic
		} catch (Exception e) {
			// Optional: log for debugging
			logger.info("Element not found after retries: " + locator);
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
		int startY = (int) (size.getHeight() * 0.25);
		int endX = startX;
		int endY = (int) (size.getHeight() * 0.75);

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

	protected void ensureVisibleBySwiping(By fullId, By shortId) {
		AndroidDriver ad = (AndroidDriver) driver;
		int tries = 0;
		while (tries++ < 6 && ad.findElements(fullId).isEmpty() && ad.findElements(shortId).isEmpty()) {
			Dimension d = ad.manage().window().getSize();
			int x = d.width / 2;
			int startY = (int) (d.height * 0.65);
			int endY = (int) (d.height * 0.35);
			new TouchAction<>(ad).press(PointOption.point(x, startY))
					.waitAction(WaitOptions.waitOptions(Duration.ofMillis(300))).moveTo(PointOption.point(x, endY))
					.release().perform();
			try {
				Thread.sleep(300);
			} catch (InterruptedException ignored) {
			}
		}
	}

	protected String findWebViewContext(Duration timeout) {
		long end = System.currentTimeMillis() + timeout.toMillis();
		while (System.currentTimeMillis() < end) {
			Set<String> contexts = ((SupportsContextSwitching) driver).getContextHandles();
			for (String c : contexts) {
				if (c != null && c.toUpperCase().contains("WEBVIEW"))
					return c;
			}
			try {
				Thread.sleep(300);
			} catch (InterruptedException ignored) {
			}
		}
		return null;
	}

	protected void openArcApplication() {
		AndroidDriver driver = (AndroidDriver) this.driver;
		driver.activateApp("io.mosip.registration_client");
		switchToNativeContext();
	}

	public void switchToNativeContext() {
		SupportsContextSwitching ctx = (SupportsContextSwitching) driver;
		if (!"NATIVE_APP".equals(ctx.getContext())) {
			ctx.context("NATIVE_APP");
		}
	}

	public void scrollToTopSafe() {
		try {
			if (!((SupportsContextSwitching) driver).getContext().equals("NATIVE_APP")) {
				((SupportsContextSwitching) driver).context("NATIVE_APP");
			}
			driver.manage().window().getSize(); // safe now
			scrollToTop();
		} catch (Exception e) {
			logger.info("scrollToTop skipped — not in a native window");
		}
	}

	protected void dismissAutoSaveOrKeyboard() {
		if (!driver.findElements(By.id("com.android.chrome:id/sheet_container")).isEmpty()) {
			try {
				List<WebElement> bg = driver.findElements(By.id("com.android.chrome:id/background"));
				if (!bg.isEmpty())
					bg.get(0).click();
				else
					((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
			} catch (Exception ignored) {
			}
		}
		try {
			((HidesKeyboard) driver).hideKeyboard();
		} catch (Exception ignored) {
		}
	}

	protected void scrollTo(String contentDescFragment) {
		for (int i = 0; i < 7; i++) {
			try {
				WebElement el = driver
						.findElement(By.xpath("//*[contains(@content-desc,'" + contentDescFragment + "')]"));
				if (el.isDisplayed()) {
					return;
				}
			} catch (Exception ignore) {
			}
			swipeUp();
		}

		throw new NoSuchElementException(
				"Element with content-desc containing '" + contentDescFragment + "' not found after scrolling.");
	}

	protected void swipeUp() {
		hideKeyboardIfVisible(); // ⭐ EXTRA SAFETY

		Dimension size = driver.manage().window().getSize();
		int startX = size.width / 2;
		int startY = (int) (size.height * 0.85);
		int endY = (int) (size.height * 0.40);

		PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
		Sequence swipe = new Sequence(finger, 1)
				.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY))
				.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
				.addAction(
						finger.createPointerMove(Duration.ofMillis(700), PointerInput.Origin.viewport(), startX, endY))
				.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

		driver.perform(Collections.singletonList(swipe));
	}

	protected void scrollUntilElementVisible(By locator) {

		hideKeyboardIfVisible();

		try {
			WebElement el = driver.findElement(locator);
			if (el.isDisplayed()) {
				return;
			}
		} catch (Exception ignored) {

		}

		for (int i = 0; i < 6; i++) {
			swipeUp();
			try {
				WebElement el = driver.findElement(locator);
				if (el.isDisplayed()) {
					return;
				}
			} catch (Exception ignored) {
			}
		}

		scrollToTopSafe();
		hideKeyboardIfVisible();

		for (int i = 0; i < 6; i++) {
			swipeUp();
			try {
				WebElement el = driver.findElement(locator);
				if (el.isDisplayed()) {
					return;
				}
			} catch (Exception ignored) {
			}
		}

		throw new NoSuchElementException("Element not visible after scrolling: " + locator);
	}

	public boolean isElementEnabled(By locator) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
			wait.ignoring(StaleElementReferenceException.class);

			return wait.until(Webdriver -> {
				WebElement element = Webdriver.findElement(locator);
				return element.isEnabled();
			});

		} catch (TimeoutException | NoSuchElementException e) {
			return false;
		}
	}

	public void hideKeyboardAndClick(By locator) {
		hideKeyboardIfVisible();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		wait.ignoring(StaleElementReferenceException.class);

		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));

		el.click();
	}

	public void hideKeyboardIfVisible() {
		try {
			((HidesKeyboard) driver).hideKeyboard();
		} catch (Exception e) {
			// Keyboard not visible – ignore
		}
	}

	public void scrollUntilVisible(By locator, int maxScrolls) {
		int count = 0;
		while (count < maxScrolls) {
			if (isElementDisplayed(locator)) {
				return;
			}
			swipeOrScroll();
			count++;
		}
		throw new NoSuchElementException("Element not visible after scrolling: " + locator);
	}

	protected void clickAndSendKeysToTextBox(By locator, String text) {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		for (int i = 0; i < 2; i++) {
			try {
				WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));

				element.click();
				element.clear();
				element.sendKeys(text);

				hideKeyboardIfVisible();
				return;

			} catch (StaleElementReferenceException e) {

			}
		}

		throw new RuntimeException("Unable to interact with textbox");
	}

	protected void waitForElementToBeVisible(By locator, int waitTime) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	private ScreenOrientation desiredOrientation;

	public void applyOrientation() {
		String orientation = TestDataReader.readData("orientation");
		ScreenOrientation finalOrientation = ScreenOrientation.PORTRAIT; // default fallback
		if (orientation != null && !orientation.isBlank()) {
			try {
				finalOrientation = ScreenOrientation.valueOf(orientation.trim().toUpperCase());
			} catch (IllegalArgumentException e) {
				logger.info(
						"Invalid orientation value in testdata.json: " + orientation + ". Falling back to PORTRAIT.");
			}

		} else {
			logger.info("Orientation not provided or empty. Using default PORTRAIT.");
		}

		lockSystemRotation(finalOrientation);
		((SupportsRotation) driver).rotate(finalOrientation);

		logger.info("Orientation applied: " + finalOrientation);
	}

	private void lockSystemRotation(ScreenOrientation orientation) {

		String rotationValue = "0"; // Portrait default

		if (orientation == ScreenOrientation.LANDSCAPE) {
			rotationValue = "1";
		}

		executeAdbCommand(new String[] { "adb", "shell", "settings", "put", "system", "accelerometer_rotation", "0" });

		executeAdbCommand(new String[] { "adb", "shell", "settings", "put", "system", "user_rotation", rotationValue });
	}

	private void executeAdbCommand(String[] command) {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			processBuilder.redirectErrorStream(true);

			Process process = processBuilder.start();

			// Read output (important to prevent stream blocking)
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				while (reader.readLine() != null) {
					// optionally log output
				}
			}

			int exitCode = process.waitFor();

			if (exitCode != 0) {
				System.out.println("ADB command failed with exit code: " + exitCode);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void scrollUntilElementVisible(WebElement element) {
		hideKeyboardIfVisible();
		try {
			if (element.isDisplayed()) {
				return;
			}
		} catch (Exception ignored) {
		}

		for (int i = 0; i < 6; i++) {
			swipeUp();
			try {
				if (element.isDisplayed()) {
					return;
				}
			} catch (Exception ignored) {
			}
		}

		scrollToTopSafe();
		hideKeyboardIfVisible();

		for (int i = 0; i < 6; i++) {
			swipeUp();
			try {
				if (element.isDisplayed()) {
					return;
				}
			} catch (Exception ignored) {
			}
		}

		throw new NoSuchElementException("Element not visible after scrolling: " + element);
	}
	
	protected void tapScreenCenter() {

	    Dimension size = driver.manage().window().getSize();

	    int x = size.width / 2;
	    int y = size.height / 2;

	    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");

	    Sequence tap = new Sequence(finger, 1);
	    tap.addAction(finger.createPointerMove(Duration.ZERO,
	            PointerInput.Origin.viewport(), x, y));
	    tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
	    tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

	    driver.perform(Arrays.asList(tap));
	}

}
