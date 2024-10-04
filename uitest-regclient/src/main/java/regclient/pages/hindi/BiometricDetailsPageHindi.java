package regclient.pages.hindi;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.ApplicantBiometricsPage;
import regclient.page.BiometricDetailsPage;
import regclient.page.IntroducerBiometricPage;
import regclient.page.PreviewPage;

public class BiometricDetailsPageHindi extends BiometricDetailsPage{

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"आवेदक बायोमेट्रिक्स\"))")
	private WebElement applicantBiometricTitle;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"आईरिस स्कैन\"))")
	private WebElement irisScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"दाएं हाथ स्कैन\"))")
	private WebElement rightHandScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"बाएं हाथ स्कैन\"))")
	private WebElement leftHandScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"अंगूठे स्कैन\"))")
	private WebElement thumbsScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"चेहरा स्कैन\"))")
	private WebElement faceScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"अपवाद स्कैन\"))")
	private WebElement exceptionScanIcon;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc,\"परिचयकर्ता बायोमेट्रिक्स\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"आईरिस स्कैन\"]")
	private WebElement introducerIrisScanIcon;
	
	@AndroidFindBy(accessibility = "जारी रखें")
	private WebElement continueButton;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"प्रमाणीकरण बायोमेट्रिक्स\"))")
	private WebElement authenticationBiometricTitle;
	
	
	public BiometricDetailsPageHindi(AppiumDriver driver) {
		super(driver);
	}
	
	public  boolean isBiometricDetailsPageDisplayed() {
		return isElementDisplayed(applicantBiometricTitle);
	}

	
	
	public ApplicantBiometricsPage clickOnIrisScan() {
		clickOnElement(irisScanIcon);
		return new ApplicantBiometricsPageHindi(driver);
	}
	
	public IntroducerBiometricPage clickOnIntroducerIrisScan() {
		while(!isElementDisplayedOnScreen(introducerIrisScanIcon)) {
			swipeOrScroll();
		}
		clickOnElement(introducerIrisScanIcon);
		return new IntroducerBiometricPageHindi(driver);
	}
	
	public ApplicantBiometricsPage clickOnRightHandScanIcon() {
		clickOnElement(rightHandScanIcon);
		return new ApplicantBiometricsPageHindi(driver);
	}
	
	public ApplicantBiometricsPage clickOnLeftHandScanIcon() {
		clickOnElement(leftHandScanIcon);
		return new ApplicantBiometricsPageHindi(driver);
	}
	
	public ApplicantBiometricsPage clickOnThumbsScanIcon() {
		clickOnElement(thumbsScanIcon);
		return new ApplicantBiometricsPageHindi(driver);
	}
	
	public ApplicantBiometricsPage clickOnFaceScanIcon() {
		clickOnElement(faceScanIcon);
		return new ApplicantBiometricsPageHindi(driver);
	}
	
	public ApplicantBiometricsPage clickOnExceptionScanIcon() {
		clickOnElement(exceptionScanIcon);
		return new ApplicantBiometricsPageHindi(driver);
	}
	
	public  PreviewPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new PreviewPageHindi(driver);
	}

	public  boolean isAuthenticationBiometricTitleDisplayed() {
		return isElementDisplayed(authenticationBiometricTitle);
	}
}
