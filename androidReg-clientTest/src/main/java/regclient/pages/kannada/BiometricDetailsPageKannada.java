package regclient.pages.kannada;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.ApplicantBiometricsPage;
import regclient.page.BiometricDetailsPage;
import regclient.page.IntroducerBiometricPage;
import regclient.page.PreviewPage;

public class BiometricDetailsPageKannada extends BiometricDetailsPage {
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"ಅರ್ಜಿದಾರ ಬಯೋಮೆಟ್ರಿಕ್ಸ್\"))")
	private WebElement applicantBiometricTitle;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"ಐರಿಸ್ ಸ್ಕ್ಯಾನ್\"))")
	private WebElement irisScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"ಬಲ ಕೈ ಸ್ಕ್ಯಾನ್\"))")
	private WebElement rightHandScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"ಎಡ ಕೈ ಸ್ಕ್ಯಾನ್\"))")
	private WebElement leftHandScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"ಬೆರಳುಗಳು ಸ್ಕ್ಯಾನ್\"))")
	private WebElement thumbsScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"ಮುಖ ಸ್ಕ್ಯಾನ್\"))")
	private WebElement faceScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"ವಿನಾಯಿತಿ ಸ್ಕ್ಯಾನ್\"))")
	private WebElement exceptionScanIcon;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc,\"ಪರಿಚಯಕಾರ ಬಯೋಮೆಟ್ರಿಕ್ಸ್\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"ಐರಿಸ್ ಸ್ಕ್ಯಾನ್\"]")
	private WebElement introducerIrisScanIcon;
	
	@AndroidFindBy(accessibility = "ಮುಂದುವರಿಸಿ")
	private WebElement continueButton;

	public BiometricDetailsPageKannada(AppiumDriver driver) {
		super(driver);
	}

	public  boolean isBiometricDetailsPageDisplayed() {
		return isElementDisplayed(applicantBiometricTitle);
	}

	
	
	public ApplicantBiometricsPage clickOnIrisScan() {
		clickOnElement(irisScanIcon);
		return new ApplicantBiometricsPageKannada(driver);
	}
	
	public IntroducerBiometricPage clickOnIntroducerIrisScan() {
		while(!isElementDisplayedOnScreen(introducerIrisScanIcon)) {
			swipeOrScroll();
		}
		clickOnElement(introducerIrisScanIcon);
		return new IntroducerBiometricPageKannada(driver);
	}
	
	public ApplicantBiometricsPage clickOnRightHandScanIcon() {
		clickOnElement(rightHandScanIcon);
		return new ApplicantBiometricsPageKannada(driver);
	}
	
	public ApplicantBiometricsPage clickOnLeftHandScanIcon() {
		clickOnElement(leftHandScanIcon);
		return new ApplicantBiometricsPageKannada(driver);
	}
	
	public ApplicantBiometricsPage clickOnThumbsScanIcon() {
		clickOnElement(thumbsScanIcon);
		return new ApplicantBiometricsPageKannada(driver);
	}
	
	public ApplicantBiometricsPage clickOnFaceScanIcon() {
		clickOnElement(faceScanIcon);
		return new ApplicantBiometricsPageKannada(driver);
	}
	
	public ApplicantBiometricsPage clickOnExceptionScanIcon() {
		clickOnElement(exceptionScanIcon);
		return new ApplicantBiometricsPageKannada(driver);
	}
	
	public  PreviewPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new PreviewPageKannada(driver);
	}

}
