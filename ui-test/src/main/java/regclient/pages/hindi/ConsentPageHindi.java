package regclient.pages.hindi;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.api.FetchUiSpec;
import regclient.page.ConsentPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.RegistrationTasksPage;
import regclient.pages.english.DemographicDetailsPageEnglish;
import regclient.pages.english.RegistrationTasksPageEnglish;

public class ConsentPageHindi extends ConsentPage {

	@AndroidFindBy(accessibility = "सूचित")
	private WebElement informedButton;

	@AndroidFindBy(accessibility = "रद्द करना")
	private WebElement cancelButton;

	public ConsentPageHindi(AppiumDriver driver) {
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

	@SuppressWarnings("deprecation")
	public boolean isCheckBoxReadable() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator(
				"new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\""
						+ FetchUiSpec.getValueUsingId("consent") + "\"))")));
	}

	public boolean isInformedButtonEnabled() {
		return isElementEnabled(informedButton);
	}

	public DemographicDetailsPage clickOnInformedButton() {
		clickOnElement(informedButton);
		return new DemographicDetailsPageHindi(driver);
	}

	public RegistrationTasksPage clickOnCancelButton() {
		clickOnElement(cancelButton);
		return new RegistrationTasksPageHindi(driver);
	}

	@SuppressWarnings("deprecation")
	public boolean updateUINTitleDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator(
				"new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\""
						+ FetchUiSpec.getTitleUsingId("UPDATE") + "\"))")));
	}

}
