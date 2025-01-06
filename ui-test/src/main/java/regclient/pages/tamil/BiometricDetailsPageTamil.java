package regclient.pages.tamil;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.api.FetchUiSpec;
import regclient.page.ApplicantBiometricsPage;
import regclient.page.BiometricDetailsPage;
import regclient.page.IntroducerBiometricPage;
import regclient.page.PreviewPage;
import regclient.pages.english.ApplicantBiometricsPageEnglish;
import regclient.pages.english.IntroducerBiometricPageEnglish;
import regclient.pages.english.PreviewPageEnglish;

public class BiometricDetailsPageTamil extends BiometricDetailsPage {

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"ஐரிஸ் ஊடுகதிர்\"))")
	private WebElement irisScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"வலது கை ஊடுகதிர்\"))")
	private WebElement rightHandScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"இடது கை ஊடுகதிர்\"))")
	private WebElement leftHandScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"முழுகுமதி ஊடுகதிர்\"))")
	private WebElement thumbsScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"முகம் ஊடுகதிர்\"))")
	private WebElement faceScanIcon;
	
	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)) .scrollIntoView(new UiSelector().descriptionContains(\"விதிவிலக்கு ஊடுகதிர்\"))")
	private WebElement exceptionScanIcon;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc,\"அறிமுகம் பயோமெட்ரிக்ஸ்\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"ஐரிஸ் ஊடுகதிர்\"]")
	private WebElement introducerIrisScanIcon;
	
	@AndroidFindBy(accessibility = "தொடர்க")
	private WebElement continueButton;


	public BiometricDetailsPageTamil(AppiumDriver driver) {
		super(driver);
	}

	@SuppressWarnings("deprecation")
	public  boolean isBiometricDetailsPageDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"" + FetchUiSpec.getValueUsingId("individualBiometrics") + "\"))")));
	}
		
	@SuppressWarnings("deprecation")
	public  boolean isAuthenticationBiometricTitleDisplayed() {
		return isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"" + FetchUiSpec.getValueUsingId("individualAuthBiometrics") + "\"))")));
	}
	
	public ApplicantBiometricsPage clickOnIrisScan() {
		clickOnElement(irisScanIcon);
		return new ApplicantBiometricsPageEnglish(driver);
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
	
	public IntroducerBiometricPage clickOnIntroducerIrisScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\"" + FetchUiSpec.getValueUsingId("introducerBiometrics") + "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Iris Scan\"]")));
		return new IntroducerBiometricPageEnglish(driver);

	}
	
	public IntroducerBiometricPage clickOnIntroducerRightHandScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\"" + FetchUiSpec.getValueUsingId("introducerBiometrics") + "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Right\"]")));
		return new IntroducerBiometricPageEnglish(driver);

	}
	
	public IntroducerBiometricPage clickOnIntroducerLeftHandScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\"" + FetchUiSpec.getValueUsingId("introducerBiometrics") + "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Left\"]")));
		return new IntroducerBiometricPageEnglish(driver);

	}
	
	public IntroducerBiometricPage clickOnIntroducerThumbScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\"" + FetchUiSpec.getValueUsingId("introducerBiometrics") + "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Thumbs\"]")));
		return new IntroducerBiometricPageEnglish(driver);

	}
	
	public IntroducerBiometricPage clickOnIntroducerFaceScan() {
		clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc,\"" + FetchUiSpec.getValueUsingId("introducerBiometrics") + "\")]/following-sibling::android.view.View/descendant::android.view.View/descendant::android.widget.ImageView[@content-desc=\"Face\"]")));
		return new IntroducerBiometricPageEnglish(driver);

	}
	
	public  PreviewPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new PreviewPageEnglish(driver);
	}
}
