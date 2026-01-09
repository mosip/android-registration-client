package regclient.pages.arabic;

import static org.testng.Assert.assertTrue;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.PendingApproval;

public class PendingApprovalArabic extends PendingApproval {

	@AndroidFindBy(accessibility = "ما زال يحتاج بتصدير")
	private WebElement pendingApprovalTitle;

	@AndroidFindBy(accessibility = "يعتمد")
	private WebElement approveButton;

	@AndroidFindBy(accessibility = "تمويه")
	private WebElement backGroundScreen;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.CheckBox\").instance(0)")
	private WebElement searchCheckBoxButton;

	@AndroidFindBy(accessibility = "يُقدِّم")
	private WebElement submitButton;

	@AndroidFindBy(accessibility = "اسم المستخدم غير صالح!")
	private WebElement invalidUsernameMessage;

	@AndroidFindBy(accessibility = "إرسال")
	private WebElement invalidUsernameMessageForempty;

	@AndroidFindBy(accessibility = "مصادقة المشرف")
	private WebElement supervisorAuthenticationTitle;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(0)")
	private WebElement userNameTextBox;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.EditText\").instance(1)")
	private WebElement passwordTextBox;

	@AndroidFindBy(xpath = "//*[contains(@content-desc,\"ما زال يحتاج بتصدير\")]//preceding-sibling::android.widget.Button")
	private WebElement backButton;

	@AndroidFindBy(accessibility = "رفض")
	private WebElement rejectButton;

	@AndroidFindBy(accessibility = "الرقم التسلسلي معرف التطبيق تاريخ التسجيل حالة العميل حالة المراجعة معرف المشغل")
	private WebElement pageAttributes;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"تم الإنشاء\")")
	private WebElement clientStatus;

	@AndroidFindBy(uiAutomator = "new UiSelector().descriptionContains(\"قيد الانتظار\").instance(1)")
	private WebElement reviewStatus;

	@AndroidFindBy(xpath = "//android.widget.EditText")
	private WebElement applicationIdTextbox;

	@AndroidFindBy(accessibility = "رفض الحزمة؟")
	private WebElement rejectPacketTitle;

	@AndroidFindBy(accessibility = "يرجى تحديد قيمة")
	private WebElement rejectReasonDropdown;

	@AndroidFindBy(accessibility = "عرض 1 من الطلبات")
	private WebElement displayApplication;

	@AndroidFindBy(accessibility = "يرجى تحديد سبب لرفض الحزمة. يمكنك تغيير المراجعة إلى الموافقة أو إعادة تعيين حالة المراجعة لاحقًا.")
	private WebElement rejectPacketInfoMessage;

	@AndroidFindBy(uiAutomator = "new UiSelector().text(\"البيانات البيومترية\")")
	private WebElement pendingApprovalBiometricsInformation;

	@AndroidFindBy(xpath = "(//android.widget.Button[@content-desc])[1]")
	private WebElement latestAid;

	@AndroidFindBy(uiAutomator = "UiSelector().className(\"android.widget.CheckBox\").instance(1)")
	private WebElement latestAIdCheckBox;

	@AndroidFindBy(accessibility = "لم يتم العثور على شبكة!")
	private WebElement noNetworkFound;

	@AndroidFindBy(accessibility = "المصادقة")
	private WebElement authenticateButton;

	public PendingApprovalArabic(AppiumDriver driver) {
		super(driver);
	}

	public boolean isPendingApprovalTitleDisplayed() {
		return isElementDisplayed(pendingApprovalTitle);
	}

	@SuppressWarnings("deprecation")
	public void clickOnAID(String AID) {
		clickOnElement(findElementWithRetry(MobileBy.AccessibilityId(AID)));
	}

	public void clickOnApproveButton() {
		clickOnElement(approveButton);
	}

	public void clickOnClosePopUpButton() {
		clickOnElement(backGroundScreen);
	}

	public void clickOnCheckBox() {
		clickOnElement(searchCheckBoxButton);
	}

	public void clickOnSubmitButton() {
		clickOnElement(submitButton);
	}

	public boolean isSupervisorAuthenticationTitleDisplayed() {
		return isElementDisplayed(supervisorAuthenticationTitle);
	}

	public void enterUserName(String username) {
		sendKeysToTextBox(userNameTextBox, username);
	}

	public void enterPassword(String password) {
		sendKeysToTextBox(passwordTextBox, password);
	}

	public void clickOnBackButton() {
		clickOnElement(backButton);
	}

	public boolean isApprovalButtonDisplayed() {
		return isElementDisplayed(approveButton);
	}

	public boolean isRejectButtonDisplayed() {
		return isElementDisplayed(rejectButton);
	}

	public boolean isInvalidUsernameMessageDisplayed() {
		return isElementDisplayed(invalidUsernameMessage);
	}

	public boolean isPageAttributesDisplayed() {
		return isElementDisplayed(pageAttributes);
	}

	public boolean isClientStatusDisplayed() {
		return isElementDisplayed(clientStatus);
	}

	public boolean isReviewStatusDisplayed() {
		return isElementDisplayed(reviewStatus);
	}

	public void enterAID(String AID) {
		clickAndsendKeysToTextBox(applicationIdTextbox, AID);
	}

	public void clickOnRejectButton() {
		clickOnElement(rejectButton);
	}

	public boolean isRejectPacketTitleDisplayed() {
		return isElementDisplayed(rejectPacketTitle);
	}

	public void selectRejectionReasonDropdown() {
		boolean isdisplayed = isElementDisplayed(rejectReasonDropdown);
		assertTrue(isdisplayed, "Verify if " + rejectReasonDropdown + " header is displayed");
		clickOnElement(rejectReasonDropdown);
		waitTime(2);
		if (!isElementDisplayed(rejectReasonDropdown)) {
			clickOnElement(findElement(By.className("android.view.View")));
		} else {
			clickOnElement(rejectReasonDropdown);
			waitTime(2);
			clickOnElement(findElement(By.className("android.view.View")));
		}
	}

	public boolean isSubmitButtonEnabled() {
		return isElementEnabled(submitButton);
	}

	public boolean isSubmitButtonEnabledWithEmptyUsername() {
		return isElementEnabled(invalidUsernameMessageForempty);
	}

	public boolean isNumberOfApplicationDisplayed() {
		waitTime(2);
		return isElementDisplayed(displayApplication);
	}

	public boolean isRejectPacketInfoMessageDisplayed() {
		return isElementDisplayed(rejectPacketInfoMessage);
	}

	public boolean isPendingApprovalBiometricsInformationDisplayed() {
		swipeOrScroll();
		return isElementDisplayed(pendingApprovalBiometricsInformation);
	}

	public void clickOnLatestAid() {
		List<WebElement> allButtons = driver.findElements(MobileBy.className("android.widget.Button"));
		allButtons.get(2).click();
	}

	public void selectLatestAIdCheckBox() {
		clickOnElement(latestAIdCheckBox);
	}

	public boolean isNoNetworkFoundDisplayed() {
		return isElementEnabled(noNetworkFound);
	}

	public void clickOnPendingApprovalSubmitButton(int maxRetries) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

		for (int i = 1; i <= maxRetries; i++) {
			clickOnSubmitButton();
			try {
				boolean popupShown = wait.until(d -> isNoNetworkFoundDisplayed());
				if (popupShown) {
				}
			} catch (TimeoutException e) {
				break;
			}
		}
		System.out.println("Still No Network Found Displayed");
	}

	public void clickOnAuthenticateButton() {
		clickOnElement(authenticateButton);
	}

	public boolean isAuthenticateButtonEnabled() {
		return isElementEnabled(authenticateButton);
	}


}
