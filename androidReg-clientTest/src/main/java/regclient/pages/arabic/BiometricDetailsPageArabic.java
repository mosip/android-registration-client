package regclient.pages.arabic;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.ApplicantBiometricsPage;
import regclient.page.BiometricDetailsPage;
import regclient.page.IntroducerBiometricPage;
import regclient.page.PreviewPage;

public class BiometricDetailsPageArabic extends BiometricDetailsPage {
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"المقاييس الحيوية لمقدم الطلب \"))")
	private WebElement applicantBiometricTitle;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"القزحية مسح\"))")
	private WebElement irisScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"اليد اليمنى مسح\"))")
	private WebElement rightHandScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"اليد اليسرى مسح\"))")
	private WebElement leftHandScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"الأبهام مسح\"))")
	private WebElement thumbsScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"الوجه مسح\"))")
	private WebElement faceScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"استثناء مسح\"))")
	private WebElement exceptionScanIcon;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc,\"المقاييس الحيوية المقدّمة\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"القزحية مسح\"]")
	private WebElement introducerIrisScanIcon;
	
	@AndroidFindBy(accessibility = "يكمل")
	private WebElement continueButton;

	public BiometricDetailsPageArabic(AppiumDriver driver) {
		super(driver);
	}

	public  boolean isBiometricDetailsPageDisplayed() {
		return isElementDisplayed(applicantBiometricTitle);
	}

	
	
	public ApplicantBiometricsPage clickOnIrisScan() {
		clickOnElement(irisScanIcon);
		return new ApplicantBiometricsPageArabic(driver);
	}
	
	public IntroducerBiometricPage clickOnIntroducerIrisScan() {
		while(!isElementDisplayedOnScreen(introducerIrisScanIcon)) {
			swipeOrScroll();
		}
		clickOnElement(introducerIrisScanIcon);
		return new IntroducerBiometricPageArabic(driver);
	}
	
	public ApplicantBiometricsPage clickOnRightHandScanIcon() {
		clickOnElement(rightHandScanIcon);
		return new ApplicantBiometricsPageArabic(driver);
	}
	
	public ApplicantBiometricsPage clickOnLeftHandScanIcon() {
		clickOnElement(leftHandScanIcon);
		return new ApplicantBiometricsPageArabic(driver);
	}
	
	public ApplicantBiometricsPage clickOnThumbsScanIcon() {
		clickOnElement(thumbsScanIcon);
		return new ApplicantBiometricsPageArabic(driver);
	}
	
	public ApplicantBiometricsPage clickOnFaceScanIcon() {
		clickOnElement(faceScanIcon);
		return new ApplicantBiometricsPageArabic(driver);
	}
	
	public ApplicantBiometricsPage clickOnExceptionScanIcon() {
		clickOnElement(exceptionScanIcon);
		return new ApplicantBiometricsPageArabic(driver);
	}
	
	public  PreviewPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new PreviewPageArabic(driver);
	}

}
