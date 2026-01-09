package regclient.pages.french;

import static org.testng.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.api.AdminTestUtil;
import regclient.api.BaseTestCase;
import regclient.api.FetchUiSpec;
import regclient.page.BasePage;
import regclient.page.ConsentPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.DocumentUploadPage;
import regclient.pages.english.ConsentPageEnglish;
import regclient.pages.english.DocumentUploadPageEnglish;
import regclient.utils.TestDataReader;

public class DemographicDetailsPageFrench extends DemographicDetailsPage {

	@AndroidFindBy(accessibility = "Mâle")
	private WebElement maleButton;

	@AndroidFindBy(accessibility = "Femelle")
	private WebElement femaleButton;

	@AndroidFindBy(accessibility = "CONTINUER")
	private WebElement continueButton;

	@AndroidFindBy(accessibility = "Entrée invalide")
	private WebElement errorMessageInvalidInputText;

	@AndroidFindBy(accessibility = "Fond")
	private WebElement backgroundScreen;

	@AndroidFindBy(accessibility = "RÉCUPÉRER DES DONNÉES")
	private WebElement fetchDataButton;

	@AndroidFindBy(xpath = "//android.widget.ScrollView/android.widget.EditText[1]")
	private WebElement applicationIdTextBox;

	@AndroidFindBy(xpath = "//android.widget.Button[@content-desc='RÉCUPÉRER DES DONNÉES']/following-sibling::android.widget.Button")
	private WebElement scanButton;

	@AndroidFindBy(accessibility = "Postal/ بريدي")
	private WebElement postalHeader;

	public DemographicDetailsPageFrench(AppiumDriver driver) {
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
		return new ConsentPageFrench(driver);
	}

	public DocumentUploadPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new DocumentUploadPageFrench(driver);

	}

	public boolean isContinueButtonEnable() {
		return isElementEnabled(continueButton);

	}

	public boolean isPreRegFetchDataTextBoxDisplay() {
		return isElementDisplayed(fetchDataButton);
	}

	public void fillDemographicDetailsPage(String age) {
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
							By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id)
									+ "\")]/parent::android.view.View/parent::android.widget.Button"));
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

		// Verify label is displayed
		boolean isDisplayed = isElementDisplayed(appIdLabel);
		assertTrue(isDisplayed, "Verify if Application ID label is displayed");

		// Enter prereg ID
		applicationIdTextBox = findElementWithRetry(appIdTextbox);
		clickAndsendKeysToTextBox(applicationIdTextBox, AdminTestUtil.getPreRegistrationFlow(age));
		clickOnElement(fetchDataButton);
	}

	public void validateFetchedDemographicData() {
		Pattern genderPattern = Pattern.compile("(?i)\\b(male|female|other|others|m|f|o)\\b");
		String[] dateFormats = { "yyyy-MM-dd", "dd/MM/yyyy", "dd-MM-yyyy" };

		List<String> idList = FetchUiSpec.getAllIds("DemographicDetails");
		for (String id : idList) {
			if (!FetchUiSpec.getRequiredTypeUsingId(id))
				continue;

			String label = FetchUiSpec.getValueUsingId(id);
			String controlType = FetchUiSpec.getControlTypeUsingId(id);
			String lowerLabel = label == null ? "" : label.toLowerCase();

			// common candidate element (immediate following control)
			WebElement el = null;
			try {
				el = findElementWithRetry(
						By.xpath("//android.view.View[contains(@content-desc,'" + label + "') or contains(@text,'"
								+ label + "')]" + "/parent::android.view.View/following-sibling::*[1]"));
			} catch (Exception ignored) {
			}

			// NAME
			if (lowerLabel.contains("name")) {
				// make visible like fill method
				try {
					int tries = 0;
					while (tries < 6 && !isElementDisplayed(findElementWithRetry(
							MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\"" + label + "\")")))) {
						swipeOrScroll();
						tries++;
					}
				} catch (Exception ignored) {
				}

				// prefer the same EditText locator used when filling
				if (el == null) {
					try {
						el = findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,'" + label
								+ "') or contains(@text,'" + label + "')]"
								+ "/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]"));
					} catch (Exception ignored) {
					}
				}
				assertTrue(el != null, "Name field element not found for: " + label);
				String name = extract(el).trim();
				assertTrue(!name.isEmpty(), "Verify name is not empty for: " + label);
				assertTrue(name.matches(".*[A-Za-z].*"), "Verify name contains letters: " + name);
				continue;
			}

			// DOB / ageDate
			if ("ageDate".equals(controlType) || lowerLabel.contains("dob") || lowerLabel.contains("date of birth")) {
				String raw = el == null ? "" : extract(el).trim();
				assertTrue(!raw.isEmpty(), "Verify DOB/AgeDate is not empty for: " + label);
				boolean ok = false;
				for (String fmt : dateFormats) {
					try {
						SimpleDateFormat sdf = new SimpleDateFormat(fmt);
						sdf.setLenient(false);
						if (sdf.parse(raw) != null) {
							ok = true;
							break;
						}
					} catch (ParseException ignored) {
					}
				}
				ok = ok || raw.replaceAll("\\D+", "").length() > 0;
				assertTrue(ok, "Verify DOB/AgeDate has valid date/digits for " + label + ": '" + raw + "'");
				continue;
			}

			// PHONE - simple, robust version
			if (lowerLabel.contains("phone") || lowerLabel.contains("mobile") || lowerLabel.contains("هاتف")) {
				// make label visible (same approach as fillDemographicDetailsPage)
				try {
					int tries = 0;
					while (tries < 6 && !isElementDisplayed(findElementWithRetry(
							MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\"" + label + "\")")))) {
						swipeOrScroll();
						tries++;
					}
				} catch (Exception ignored) {
				}

				// primary locator (same as fill)
				WebElement phoneEl = null;
				try {
					phoneEl = findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + label
							+ "\") or contains(@text, \"" + label + "\")]"
							+ "/parent::android.view.View/following-sibling::android.view.View//android.widget.EditText[1]"));
				} catch (Exception ignored) {
				}

				// fallback to ancestor search
				if (phoneEl == null) {
					try {
						phoneEl = findElementIfExists(By.xpath("//android.view.View[contains(@content-desc, \"" + label
								+ "\") or contains(@text, \"" + label + "\")]"
								+ "/ancestor::android.view.View[1]//android.widget.EditText[1]"));
					} catch (Exception ignored) {
					}
				}

				// if still null, provide short diagnostic and fail
				if (phoneEl == null) {
					StringBuilder diag = new StringBuilder();
					try {
						WebElement labelEl = findElementIfExists(
								By.xpath("//android.view.View[contains(@content-desc, \"" + label
										+ "\") or contains(@text, \"" + label + "\")]"));
						if (labelEl != null) {
							List<WebElement> sibs = labelEl
									.findElements(By.xpath("parent::android.view.View/following-sibling::*"));
							for (int i = 0; i < Math.min(sibs.size(), 6); i++) {
								WebElement s = sibs.get(i);
								String t = "";
								try {
									t = s.getText();
								} catch (Exception ignored) {
								}
								String cd = "";
								try {
									cd = s.getAttribute("content-desc");
								} catch (Exception ignored) {
								}
								diag.append("sib[").append(i).append("]={text=").append(t).append(",cd=").append(cd)
										.append("}; ");
							}
						}
					} catch (Exception ignored) {
					}
					assertTrue(false, "Phone field element not found for: " + label + ". Nearby: " + diag.toString());
				}

				// extract and validate
				String phone = extract(phoneEl);
				phone = phone == null ? "" : phone.trim();
				assertTrue(!phone.isEmpty(), "Verify phone is not empty for: " + label);
				String digits = phone.replaceAll("\\D+", "");
				assertTrue(digits.length() >= 7 && digits.length() <= 15,
						"Verify phone has 7-15 digits for " + label + ": " + phone);
			}

			// EMAIL
			if (lowerLabel.contains("email") || lowerLabel.contains("e-mail") || lowerLabel.contains("البريد")) {
				// make label visible (same approach as fill)
				try {
					int tries = 0;
					while (tries < 6 && !isElementDisplayed(findElementWithRetry(
							MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\"" + label + "\")")))) {
						swipeOrScroll();
						tries++;
					}
				} catch (Exception ignored) {
				}

				// primary locator (same style as fill)
				WebElement emailEl = null;
				try {
					emailEl = findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + label
							+ "\") or contains(@text, \"" + label + "\")]"
							+ "/parent::android.view.View/following-sibling::android.view.View//android.widget.EditText[1]"));
				} catch (Exception ignored) {
				}

				// fallback: any EditText under the same ancestor
				if (emailEl == null) {
					try {
						emailEl = findElementIfExists(By.xpath("//android.view.View[contains(@content-desc, \"" + label
								+ "\") or contains(@text, \"" + label + "\")]"
								+ "/ancestor::android.view.View[1]//android.widget.EditText[1]"));
					} catch (Exception ignored) {
					}
				}

				// if still null, show short diagnostic and fail
				if (emailEl == null) {
					StringBuilder diag = new StringBuilder();
					try {
						WebElement labelEl = findElementIfExists(
								By.xpath("//android.view.View[contains(@content-desc, \"" + label
										+ "\") or contains(@text, \"" + label + "\")]"));
						if (labelEl != null) {
							List<WebElement> sibs = labelEl
									.findElements(By.xpath("parent::android.view.View/following-sibling::*"));
							for (int i = 0; i < Math.min(sibs.size(), 6); i++) {
								WebElement s = sibs.get(i);
								String t = "";
								try {
									t = s.getText();
								} catch (Exception ignored) {
								}
								String cd = "";
								try {
									cd = s.getAttribute("content-desc");
								} catch (Exception ignored) {
								}
								diag.append("sib[").append(i).append("]={text=").append(t).append(",cd=").append(cd)
										.append("}; ");
							}
						}
					} catch (Exception ignored) {
					}
					assertTrue(false, "Email field element not found for: " + label + ". Nearby: " + diag.toString());
				}

				// extract and validate
				String email = extract(emailEl);
				email = email == null ? "" : email.trim();
				assertTrue(!email.isEmpty(), "Verify email is not empty for: " + label);

				// simple regex good for tests
				boolean emailOk = email.matches("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$");
				assertTrue(emailOk, "Verify email format is valid for " + label + ": " + email);
			}
		}
	}

	public void fillRemainDemographicDetailsPage(String age) {

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

	public boolean isApplicationIdTextBoxDisplay() {
		return isElementDisplayed(applicationIdTextBox);
	}

	public void clickOnScanButton() {
		clickOnElement(scanButton);
	}
}
