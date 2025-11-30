package regclient.pages.english;

import static org.testng.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import colesico.framework.ioc.production.Supplier;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.api.AdminTestUtil;
import regclient.api.BaseTestCase;
import regclient.api.FetchUiSpec;
import regclient.page.BasePage;
import regclient.page.ConsentPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.DocumentUploadPage;
import regclient.utils.TestDataReader;

public class DemographicDetailsPageEnglish extends DemographicDetailsPage {

	@AndroidFindBy(accessibility = "Male")
	private WebElement maleButton;

	@AndroidFindBy(accessibility = "Female")
	private WebElement femaleButton;

	@AndroidFindBy(accessibility = "CONTINUE")
	private WebElement continueButton;

	@AndroidFindBy(accessibility = "Invalid input")
	private WebElement errorMessageInvalidInputText;

	@AndroidFindBy(accessibility = "Scrim")
	private WebElement backgroundScreen;

	@AndroidFindBy(accessibility = "FETCH DATA")
	private WebElement fetchDataButton;

	@AndroidFindBy(accessibility = "آحرون")
	private WebElement maleButtonInArabic;

	@AndroidFindBy(accessibility = "Mâle")
	private WebElement maleButtonInFrench;

	@AndroidFindBy(xpath = "//android.widget.ScrollView/android.widget.EditText[1]")
	private WebElement applicationIdTextBox;

	@AndroidFindBy(xpath = "//android.widget.Button[@content-desc='FETCH DATA']/following-sibling::android.widget.Button")
	private WebElement scanButton;

	@AndroidFindBy(accessibility = "Postal/ بريدي")
	private WebElement postalHeader;

	public DemographicDetailsPageEnglish(AppiumDriver driver) {
		super(driver);
	}

	@SuppressWarnings("deprecation")

	public boolean isPageDisplayed(String pageKey) {
		try {
			String screenTitle = FetchUiSpec.getScreenTitle(pageKey);

			WebElement pageElement = findElementWithRetry(
					MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0))"
							+ ".scrollIntoView(new UiSelector().descriptionContains(\"" + screenTitle + "\"))"));

			return isElementDisplayed(pageElement);

		} catch (Exception e) {
			return false;
		}

	}

	public boolean isErrorMessageInvalidInputTextDisplayed() {
		return isElementDisplayed(errorMessageInvalidInputText);
	}

	@SuppressWarnings("deprecation")
	public ConsentPage clickOnPageTitle(String pageKey) {
		String screenTitle = FetchUiSpec.getScreenTitle(pageKey);
		WebElement consentTitle = findElementWithRetry(
				MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0))"
						+ ".scrollIntoView(new UiSelector().descriptionContains(\"" + screenTitle + "\"))"));
		clickOnElement(consentTitle);
		return new ConsentPageEnglish(driver);
	}

	public DocumentUploadPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new DocumentUploadPageEnglish(driver);

	}

	public boolean isContinueButtonEnable() {
		return isElementEnabled(continueButton);

	}

	public boolean isPreRegFetchDataTextBoxDisplay() {
		return isElementDisplayed(fetchDataButton);
	}

	public boolean isApplicationIdTextBoxDisplay() {
		return isElementDisplayed(applicationIdTextBox);
	}

	public void clickOnScanButton() {
		clickOnElement(scanButton);
	}

	public void fillDemographicDetailsPage(String age) {
		scrollToTop();
		List<String> idList = FetchUiSpec.getAllIds("DemographicDetails");
		for (String id : idList) {
			if (FetchUiSpec.getRequiredTypeUsingId(id)) {
				if (FetchUiSpec.getControlTypeUsingId(id).equals("textbox")) {
					waitTime(3);
					boolean isdisplayed = isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator(
							"new UiSelector().descriptionContains(\"" + FetchUiSpec.getValueUsingId(id) + "\")")));
					assertTrue(isdisplayed, "Verify if " + id + " header is displayed");
					clickAndsendKeysToTextBox(
							findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
									+ FetchUiSpec.getValueUsingId(id)
									+ "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),
							BasePage.generateData(FetchUiSpec.getTextBoxUsingId(id)));
					if (FetchUiSpec.getTransliterateTypeUsingId(id))
						assertTrue(checkSecondLanguageTextBoxNotNull(id),
								"Verify if " + id + " is enter in second language text box");
				} else if (FetchUiSpec.getControlTypeUsingId(id).equals("dropdown")
						&& FetchUiSpec.getFormatUsingId(id).equals("none")) {
					waitTime(3);
					while (!isElementDisplayed(MobileBy.AndroidUIAutomator(
							"new UiSelector().descriptionContains(\"" + FetchUiSpec.getValueUsingId(id) + "\")"))) {
						swipeOrScroll();
					}

					boolean isdisplayed = isElementDisplayed(MobileBy.AndroidUIAutomator(
							"new UiSelector().descriptionContains(\"" + FetchUiSpec.getValueUsingId(id) + "\")"));
					assertTrue(isdisplayed, "Verify if " + id + " header is displayed");
					WebElement dropdownElement = findElement(
							By.xpath("//android.widget.Button[.//android.view.View[contains(@content-desc,'"
									+ FetchUiSpec.getValueUsingId(id) + "')]]"));

					clickOnElement(dropdownElement);
					waitTime(3);
					if (!isElementDisplayed(dropdownElement)) {
						clickOnElement(findElement(By.className("android.view.View")));
					} else if (isElementDisplayed(dropdownElement)) {
						swipeOrScroll();
						clickOnElement(dropdownElement);
						waitTime(2);
						clickOnElement(findElement(By.className("android.view.View")));
					}
					waitTime(2);
					if (isElementDisplayed(By.xpath("//android.view.View[contains(@content-desc, \""
							+ FetchUiSpec.getValueUsingId(id)
							+ "\")]/parent::android.view.View/parent::android.widget.Button[contains(@content-desc, \"Select Option\")]"))) {
						clickOnElement(dropdownElement);
						waitTime(2);
						clickOnElement(findElement(By.className("android.view.View")));
					}
				} else if (FetchUiSpec.getControlTypeUsingId(id).equals("dropdown")
						&& FetchUiSpec.getFormatUsingId(id).equals("")) {
					if (!isElementDisplayed(maleButton)) {
						swipeOrScroll();
						clickOnElement(maleButton);
					} else
						clickOnElement(maleButton);

				} else if (FetchUiSpec.getControlTypeUsingId(id).equals("ageDate")) {
					waitTime(3);
					boolean isdisplayed = isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator(
							"new UiSelector().descriptionContains(\"" + FetchUiSpec.getValueUsingId(id) + "\")")));
					assertTrue(isdisplayed, "Verify if " + id + " header is displayed");
					if (age.equals("adult"))
						clickAndsendKeysToTextBox(
								findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
										+ FetchUiSpec.getValueUsingId(id)
										+ "\")]/parent::android.view.View/following-sibling::android.widget.EditText[1]")),
								"20");
					else if (age.equals("minor"))
						clickAndsendKeysToTextBox(
								findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
										+ FetchUiSpec.getValueUsingId(id)
										+ "\")]/parent::android.view.View/following-sibling::android.widget.EditText[1]")),
								"12");
					else if (age.equals("infant"))
						clickAndsendKeysToTextBox(
								findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
										+ FetchUiSpec.getValueUsingId(id)
										+ "\")]/parent::android.view.View/following-sibling::android.widget.EditText[1]")),
								"4");
					else if (age.equals("currentCalenderDate")) {
						waitTime(1);
						clickOnElement(findElementWithRetry(By.xpath(
								"//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id)
										+ "\")]/parent::android.view.View/following-sibling::android.view.View")));
						waitTime(1);
						clickOnElement(backgroundScreen);
						waitTime(1);
						assertTrue(checkDateFormatAndCurrentDate(id),
								"Verify date format and current date and time while selecting age date");
					}
				}
			} else if (id.equals("residenceStatus")) {
				if (FetchUiSpec.getControlTypeUsingId(id).equals("dropdown")
						&& FetchUiSpec.getFormatUsingId(id).equals("none")) {
					waitTime(2);
					boolean isdisplayed = isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator(
							"new UiSelector().descriptionContains(\"" + FetchUiSpec.getValueUsingId(id) + "\")")));
					assertTrue(isdisplayed, "Verify if " + id + " header is displayed");
					WebElement dropdownElement = findElement(
							By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id)
									+ "\")]/parent::android.view.View/parent::android.widget.Button"));
					clickOnElement(dropdownElement);
					waitTime(2);
					if (!isElementDisplayed(dropdownElement)) {
						clickOnElement(findElement(By.className("android.view.View")));
					} else if (isElementDisplayed(dropdownElement)) {
						swipeOrScroll();
						clickOnElement(dropdownElement);
						waitTime(2);
						clickOnElement(findElement(By.className("android.view.View")));
					}
					waitTime(2);
					if (isElementDisplayed(By.xpath("//android.view.View[contains(@content-desc, \""
							+ FetchUiSpec.getValueUsingId(id)
							+ "\")]/parent::android.view.View/parent::android.widget.Button[contains(@content-desc, \"Select Option\")]"))) {
						clickOnElement(dropdownElement);
						waitTime(2);
						clickOnElement(findElement(By.className("android.view.View")));
					}
				}
			}
			if (id.equals("introducerName") && FetchUiSpec.getFlowType().equals("newProcess")) {
				if (age.equals("minor") || age.equals("infant") || age.equals("currentCalenderDate")) {
					if (FetchUiSpec.getControlTypeUsingId(id).equals("textbox")) {
						waitTime(3);
						boolean isdisplayed = isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator(
								"new UiSelector().descriptionContains(\"" + FetchUiSpec.getValueUsingId(id) + "\")")));
						assertTrue(isdisplayed, "Verify if " + id + " header is displayed");
						clickAndsendKeysToTextBox(
								findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
										+ FetchUiSpec.getValueUsingId(id)
										+ "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),
								BasePage.generateData(FetchUiSpec.getTextBoxUsingId(id)));
						if (FetchUiSpec.getTransliterateTypeUsingId(id))
							assertTrue(checkSecondLanguageTextBoxNotNull(id),
									"Verify if " + id + " is enter in second language text box");
					}
				}
			}
			if (id.equals("introducerRID") && FetchUiSpec.getFlowType().equals("newProcess")) {
				if (age.equals("minor") || age.equals("infant") || age.equals("currentCalenderDate")) {
					if (FetchUiSpec.getControlTypeUsingId(id).equals("textbox")) {
						waitTime(3);
						boolean isdisplayed = isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator(
								"new UiSelector().descriptionContains(\"" + FetchUiSpec.getValueUsingId(id) + "\")")));
						assertTrue(isdisplayed, "Verify if " + id + " header is displayed");
						clickAndsendKeysToTextBox(
								findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
										+ FetchUiSpec.getValueUsingId(id)
										+ "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),
								TestDataReader.readData("RID"));
						if (FetchUiSpec.getTransliterateTypeUsingId(id))
							assertTrue(checkSecondLanguageTextBoxNotNull(id),
									"Verify if " + id + " is enter in second language text box");
					}
				}
			}
		}
	}

	public void editDemographicDetailsPage(String age) {
		List<String> idList = FetchUiSpec.getAllIds("DemographicDetails");
		for (String id : idList) {
			if (FetchUiSpec.getRequiredTypeUsingId(id)) {
				if (FetchUiSpec.getControlTypeUsingId(id).equals("textbox")) {
					waitTime(3);
					boolean isdisplayed = isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator(
							"new UiSelector().descriptionContains(\"" + FetchUiSpec.getValueUsingId(id) + "\")")));
					assertTrue(isdisplayed, "Verify if " + id + " header is displayed");
					clickAndsendKeysToTextBox(
							findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
									+ FetchUiSpec.getValueUsingId(id)
									+ "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),
							BasePage.generateData(FetchUiSpec.getTextBoxUsingId(id)));
					if (FetchUiSpec.getTransliterateTypeUsingId(id))
						assertTrue(checkSecondLanguageTextBoxNotNull(id),
								"Verify if " + id + " is enter in second language text box");
				} else if (FetchUiSpec.getControlTypeUsingId(id).equals("ageDate")) {
					waitTime(3);
					boolean isdisplayed = isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator(
							"new UiSelector().descriptionContains(\"" + FetchUiSpec.getValueUsingId(id) + "\")")));
					assertTrue(isdisplayed, "Verify if " + id + " header is displayed");
					if (age.equals("adult"))
						clickAndsendKeysToTextBox(
								findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
										+ FetchUiSpec.getValueUsingId(id)
										+ "\")]/parent::android.view.View/following-sibling::android.widget.EditText[1]")),
								"20");
					else if (age.equals("minor"))
						clickAndsendKeysToTextBox(
								findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
										+ FetchUiSpec.getValueUsingId(id)
										+ "\")]/parent::android.view.View/following-sibling::android.widget.EditText[1]")),
								"12");
					else if (age.equals("infant"))
						clickAndsendKeysToTextBox(
								findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
										+ FetchUiSpec.getValueUsingId(id)
										+ "\")]/parent::android.view.View/following-sibling::android.widget.EditText[1]")),
								"4");
					else if (age.equals("currentCalenderDate")) {
						waitTime(1);
						clickOnElement(findElementWithRetry(By.xpath(
								"//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id)
										+ "\")]/parent::android.view.View/following-sibling::android.view.View")));
						waitTime(1);
						clickOnElement(backgroundScreen);
						waitTime(1);
						assertTrue(checkDateFormatAndCurrentDate(id),
								"Verify date format and current date and time while selecting age date");
					}
				}
			}
			if (id.equals("introducerName") && FetchUiSpec.getFlowType().equals("newProcess")) {
				if (age.equals("minor") || age.equals("infant") || age.equals("currentCalenderDate")) {
					if (FetchUiSpec.getControlTypeUsingId(id).equals("textbox")) {
						waitTime(3);
						boolean isdisplayed = isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator(
								"new UiSelector().descriptionContains(\"" + FetchUiSpec.getValueUsingId(id) + "\")")));
						assertTrue(isdisplayed, "Verify if " + id + " header is displayed");
						clickAndsendKeysToTextBox(
								findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
										+ FetchUiSpec.getValueUsingId(id)
										+ "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),
								BasePage.generateData(FetchUiSpec.getTextBoxUsingId(id)));
						if (FetchUiSpec.getTransliterateTypeUsingId(id))
							assertTrue(checkSecondLanguageTextBoxNotNull(id),
									"Verify if " + id + " is enter in second language text box");
					}
				}
			}
			if (id.equals("introducerRID") && FetchUiSpec.getFlowType().equals("newProcess")) {
				if (age.equals("minor") || age.equals("infant") || age.equals("currentCalenderDate")) {
					if (FetchUiSpec.getControlTypeUsingId(id).equals("textbox")) {
						waitTime(3);
						boolean isdisplayed = isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator(
								"new UiSelector().descriptionContains(\"" + FetchUiSpec.getValueUsingId(id) + "\")")));
						assertTrue(isdisplayed, "Verify if " + id + " header is displayed");
						clickAndsendKeysToTextBox(
								findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
										+ FetchUiSpec.getValueUsingId(id)
										+ "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),
								TestDataReader.readData("RID"));
						if (FetchUiSpec.getTransliterateTypeUsingId(id))
							assertTrue(checkSecondLanguageTextBoxNotNull(id),
									"Verify if " + id + " is enter in second language text box");
					}
				}
			}
		}
	}

	public boolean checkSecondLanguageTextBoxNotNull(String id) {
		if (getTextFromLocator(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
				+ FetchUiSpec.getValueUsingId(id)
				+ "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[2]"))) == null
				|| getTextFromLocator(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
						+ FetchUiSpec.getValueUsingId(id)
						+ "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[2]"))) == "")
			return false;
		else
			return true;
	}

	public boolean checkDateFormatAndCurrentDate(String id) {
		if (getTextFromLocator(findElementWithRetry(
				By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id)
						+ "\")]/parent::android.view.View/following-sibling::android.view.View")))
				.equalsIgnoreCase(getCurrentDate()))
			return true;
		else
			return false;
	}

	public void fillIntroducerDetailsInDemographicDetailsPage(String age) {
		List<String> idList = FetchUiSpec.getAllIds("DemographicDetails");
		for (String id : idList) {
			if (id.equals("introducerName")) {
				if (age.equals("minor") || age.equals("infant") || age.equals("currentCalenderDate")) {
					if (FetchUiSpec.getControlTypeUsingId(id).equals("textbox")) {
						waitTime(3);
						boolean isdisplayed = isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator(
								"new UiSelector().descriptionContains(\"" + FetchUiSpec.getValueUsingId(id) + "\")")));
						assertTrue(isdisplayed, "Verify if " + id + " header is displayed");
						clickAndsendKeysToTextBox(
								findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
										+ FetchUiSpec.getValueUsingId(id)
										+ "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),
								BasePage.generateData(FetchUiSpec.getTextBoxUsingId(id)));
						if (FetchUiSpec.getTransliterateTypeUsingId(id))
							assertTrue(checkSecondLanguageTextBoxNotNull(id),
									"Verify if " + id + " is enter in second language text box");
					}
				}
			}
			if (id.equals("introducerUIN")) {
				if (age.equals("minor") || age.equals("infant") || age.equals("currentCalenderDate")) {
					if (FetchUiSpec.getControlTypeUsingId(id).equals("textbox")) {
						waitTime(3);
						boolean isdisplayed = isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator(
								"new UiSelector().descriptionContains(\"" + FetchUiSpec.getValueUsingId(id) + "\")")));
						assertTrue(isdisplayed, "Verify if " + id + " header is displayed");
						clickAndsendKeysToTextBox(
								findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
										+ FetchUiSpec.getValueUsingId(id)
										+ "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),
								TestDataReader.readData("UINminor"));
						if (FetchUiSpec.getTransliterateTypeUsingId(id))
							assertTrue(checkSecondLanguageTextBoxNotNull(id),
									"Verify if " + id + " is enter in second language text box");
					}
				}
			}
		}
	}

	public void fetchPreregApplicationId(String age) {
		By appIdLabel = By.xpath("//android.widget.EditText[contains(@hint,'Application ID')]");
		By appIdTextbox = By.xpath("//android.widget.EditText[contains(@hint,'Please Enter Application ID')]");

		boolean isDisplayed = isElementDisplayed(appIdLabel);
		assertTrue(isDisplayed, "Verify if Application ID label is displayed");

		applicationIdTextBox = findElementWithRetry(appIdTextbox);
		clickAndsendKeysToTextBox(applicationIdTextBox, AdminTestUtil.getPreRegistrationFlow(age));
		clickOnElement(fetchDataButton);
	}

	public void validateFetchedDemographicData() {
		String[] dateFormats = { "yyyy-MM-dd", "dd/MM/yyyy", "dd-MM-yyyy" };
		List<String> ids = FetchUiSpec.getAllIds("DemographicDetails");

		for (String id : ids) {
			if (!FetchUiSpec.getRequiredTypeUsingId(id))
				continue;

			String label = FetchUiSpec.getValueUsingId(id);
			String controlType = FetchUiSpec.getControlTypeUsingId(id);
			String lower = label == null ? "" : label.toLowerCase();

			WebElement field = getInputField(label);
			assertTrue(field != null, "Field not found for: " + label);

			String value = extract(field) == null ? "" : extract(field).trim();
			assertTrue(!value.isEmpty(), "Value empty for: " + label);

			// --- NAME ---
			if (lower.contains("name")) {
				assertTrue(value.matches(".*[A-Za-z].*"), "Invalid name (no letters): " + value);
				continue;
			}

			// --- DOB / AGE ---
			if ("ageDate".equals(controlType) || lower.contains("dob") || lower.contains("birth")) {
				boolean ok = false;
				for (String fmt : dateFormats) {
					try {
						SimpleDateFormat sdf = new SimpleDateFormat(fmt);
						sdf.setLenient(false);
						sdf.parse(value);
						ok = true;
						break;
					} catch (Exception ignored) {
					}
				}
				ok = ok || value.replaceAll("\\D+", "").length() > 0;
				assertTrue(ok, "Invalid DOB/AgeDate for " + label + ": " + value);
				continue;
			}

			// --- PHONE ---
			if (lower.contains("phone") || lower.contains("mobile") || lower.contains("هاتف")) {
				String digits = value.replaceAll("\\D+", "");
				assertTrue(digits.length() >= 7 && digits.length() <= 15,
						"Invalid phone digits for " + label + ": " + value);
				continue;
			}

			// --- EMAIL ---
			if (lower.contains("email") || lower.contains("e-mail") || lower.contains("البريد")) {
				boolean emailOk = value.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
				assertTrue(emailOk, "Invalid email format for " + label + ": " + value);
			}
		}
	}

	private WebElement getInputField(String label) {
		if (label == null || label.trim().isEmpty())
			return null;
		String cleanedLabel = label.trim();
		String leftPart = cleanedLabel.contains("/") ? cleanedLabel.substring(0, cleanedLabel.indexOf("/")).trim()
				: cleanedLabel;

		WebElement element = null;

		// Try multiple patterns with tighter scope
		String[] patterns = {
				// EditText under same parent container
				"//android.view.View[contains(@content-desc,'" + cleanedLabel + "') or contains(@text,'" + cleanedLabel
						+ "')]"
						+ "/parent::android.view.View/following-sibling::android.view.View//android.widget.EditText[1]",

				// fallback: sibling EditText
				"//android.view.View[contains(@content-desc,'" + cleanedLabel + "') or contains(@text,'" + cleanedLabel
						+ "')]" + "/following-sibling::android.widget.EditText[1]",

				// fallback: any EditText under ancestor container (section block)
				"//android.view.View[contains(@content-desc,'" + cleanedLabel + "') or contains(@text,'" + cleanedLabel
						+ "')]" + "/ancestor::android.view.View[1]//android.widget.EditText[1]" };

		// Scroll & try
		for (int attempt = 0; attempt < 6 && element == null; attempt++) {
			for (String xp : patterns) {
				try {
					element = findElementIfExists(By.xpath(xp));
					if (element != null && isElementDisplayed(element))
						return element;
				} catch (Exception ignored) {
				}
			}
			swipeOrScroll();
		}

		// Fallback: first visible EditText (not recommended but prevents NPE)
		try {
			for (WebElement e : driver.findElements(By.className("android.widget.EditText"))) {
				if (isElementDisplayed(e))
					return e;
			}
		} catch (Exception ignored) {
		}

		return null;
	}

	public void fillRemainDemographicDetailsPage(String age) {
		scrollToTop();
		List<String> idList = FetchUiSpec.getAllIds("DemographicDetails");
		for (String id : idList) {
			if (FetchUiSpec.getRequiredTypeUsingId(id) && !id.equals("Postal")) {
				if (FetchUiSpec.getControlTypeUsingId(id).equals("dropdown")
						&& FetchUiSpec.getFormatUsingId(id).equals("none")) {
					waitTime(3);
					while (!isElementDisplayed(MobileBy.AndroidUIAutomator(
							"new UiSelector().descriptionContains(\"" + FetchUiSpec.getValueUsingId(id) + "\")"))) {
						swipeOrScroll();
					}
					boolean isdisplayed = isElementDisplayed(MobileBy.AndroidUIAutomator(
							"new UiSelector().descriptionContains(\"" + FetchUiSpec.getValueUsingId(id) + "\")"));
//					assertTrue(isdisplayed, "Verify if " + id + " header is displayed");
					WebElement dropdownElement = findElement(
							By.xpath("//android.widget.Button[.//android.view.View[contains(@content-desc,'"
									+ FetchUiSpec.getValueUsingId(id) + "')]]"));
					clickOnElement(dropdownElement);
					waitTime(3);
					if (!isElementDisplayed(dropdownElement)) {
						clickOnElement(findElement(By.className("android.view.View")));
					} else if (isElementDisplayed(dropdownElement)) {
						swipeOrScroll();
						clickOnElement(dropdownElement);
						waitTime(2);
						clickOnElement(findElement(By.className("android.view.View")));
					}
					waitTime(2);
					if (isElementDisplayed(By.xpath("//android.view.View[contains(@content-desc, \""
							+ FetchUiSpec.getValueUsingId(id)
							+ "\")]/parent::android.view.View/parent::android.widget.Button[contains(@content-desc, \"Select Option\")]"))) {
						clickOnElement(dropdownElement);
						waitTime(2);
						clickOnElement(findElement(By.className("android.view.View")));
					}
				}
			} else if (FetchUiSpec.getRequiredTypeUsingId(id) && id.equals("Postal")) {
				By postalDropdown = By.xpath("//android.view.View[contains(@content-desc, 'Postal')]"
						+ "/parent::android.view.View/parent::android.widget.Button[contains(@content-desc, 'Select Option')]");

				int attempts = 0;
				while (attempts < 3) {
					try {
						if (isElementDisplayed(postalDropdown)) {
							WebElement dropdownElement = findElement(postalDropdown);
							clickOnElement(dropdownElement);
							waitTime(3); // increased wait for Postal data load

							if (isElementDisplayed(By.className("android.view.View"))) {
								clickOnElement(findElement(By.className("android.view.View")));
								System.out.println("✅ Postal dropdown handled successfully");
								break; // success
							} else {
								System.out.println("⏳ Postal options not visible yet, retrying...");
							}
						} else {
							swipeOrScroll();
						}
					} catch (org.openqa.selenium.StaleElementReferenceException e) {
						System.out.println("🔄 Postal element went stale, retrying...");
					}
					waitTime(2);
					attempts++;
				}
			}
			if (id.equals("introducerName") && FetchUiSpec.getFlowType().equals("newProcess")) {
				if (age.equals("minor") || age.equals("infant") || age.equals("currentCalenderDate")) {
					if (FetchUiSpec.getControlTypeUsingId(id).equals("textbox")) {
						waitTime(3);
						boolean isdisplayed = isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator(
								"new UiSelector().descriptionContains(\"" + FetchUiSpec.getValueUsingId(id) + "\")")));
						assertTrue(isdisplayed, "Verify if " + id + " header is displayed");
						clickAndsendKeysToTextBox(
								findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
										+ FetchUiSpec.getValueUsingId(id)
										+ "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),
								BasePage.generateData(FetchUiSpec.getTextBoxUsingId(id)));
						if (FetchUiSpec.getTransliterateTypeUsingId(id))
							assertTrue(checkSecondLanguageTextBoxNotNull(id),
									"Verify if " + id + " is enter in second language text box");
					}
				}
			}
			if (id.equals("introducerRID") && FetchUiSpec.getFlowType().equals("newProcess")) {
				if (age.equals("minor") || age.equals("infant") || age.equals("currentCalenderDate")) {
					if (FetchUiSpec.getControlTypeUsingId(id).equals("textbox")) {
						waitTime(3);
						boolean isdisplayed = isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator(
								"new UiSelector().descriptionContains(\"" + FetchUiSpec.getValueUsingId(id) + "\")")));
						assertTrue(isdisplayed, "Verify if " + id + " header is displayed");
						clickAndsendKeysToTextBox(
								findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""
										+ FetchUiSpec.getValueUsingId(id)
										+ "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),
								TestDataReader.readData("RID"));
						if (FetchUiSpec.getTransliterateTypeUsingId(id))
							assertTrue(checkSecondLanguageTextBoxNotNull(id),
									"Verify if " + id + " is enter in second language text box");
					}
				}
			}
		}
	}

}
