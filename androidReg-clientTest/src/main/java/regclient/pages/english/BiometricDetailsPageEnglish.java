package regclient.pages.english;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.ApplicantBiometricsPage;
import regclient.page.BiometricDetailsPage;
import regclient.page.IntroducerBiometricPage;
import regclient.page.PreviewPage;


public class BiometricDetailsPageEnglish extends BiometricDetailsPage {

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Applicant Biometrics\"))")
	private WebElement applicantBiometricTitle;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Iris\"))")
	private WebElement irisScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Right\"))")
	private WebElement rightHandScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Left\"))")
	private WebElement leftHandScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Thumbs\"))")
	private WebElement thumbsScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Face\"))")
	private WebElement faceScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"Exception Scan\"))")
	private WebElement exceptionScanIcon;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc,\"Introducer Biometrics\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Iris Scan\"]")
	private WebElement introducerIrisScanIcon;
	
	@AndroidFindBy(accessibility = "CONTINUE")
	private WebElement continueButton;

	public BiometricDetailsPageEnglish(AppiumDriver driver) {
		super(driver);
	}

	public  boolean isBiometricDetailsPageDisplayed() {
		return isElementDisplayed(applicantBiometricTitle);
	}

	
	
	public ApplicantBiometricsPage clickOnIrisScan() {
		clickOnElement(irisScanIcon);
		return new ApplicantBiometricsPageEnglish(driver);
	}
	
	public IntroducerBiometricPage clickOnIntroducerIrisScan() {
		while(!isElementDisplayedOnScreen(introducerIrisScanIcon)) {
			swipeOrScroll();
		}
		clickOnElement(introducerIrisScanIcon);
		return new IntroducerBiometricPageEnglish(driver);
	}
	
	public ApplicantBiometricsPage clickOnRightHandScanIcon() {
		clickOnElement(rightHandScanIcon);
		return new ApplicantBiometricsPageEnglish(driver);
	}
	
	public ApplicantBiometricsPage clickOnLeftHandScanIcon() {
		clickOnElement(leftHandScanIcon);
		return new ApplicantBiometricsPageEnglish(driver);
	}
	
	public ApplicantBiometricsPage clickOnThumbsScanIcon() {
		clickOnElement(thumbsScanIcon);
		return new ApplicantBiometricsPageEnglish(driver);
	}
	
	public ApplicantBiometricsPage clickOnFaceScanIcon() {
		clickOnElement(faceScanIcon);
		return new ApplicantBiometricsPageEnglish(driver);
	}
	
	public ApplicantBiometricsPage clickOnExceptionScanIcon() {
		clickOnElement(exceptionScanIcon);
		return new ApplicantBiometricsPageEnglish(driver);
	}
	
	public  PreviewPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new PreviewPageEnglish(driver);
	}
}
