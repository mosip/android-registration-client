package regclient.pages.english;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.page.DashboardPage;

public class DashboardPageEnglish extends DashboardPage{

	@AndroidFindBy(xpath = "//android.view.View[@content-desc=\"Dashboard\"]/following-sibling::android.view.View[3]")
	private WebElement packetUploadedNumber;
	
	@AndroidFindBy(xpath = "//android.view.View[@content-desc=\"Dashboard\"]/following-sibling::android.view.View[2]")
	private WebElement packetSyncedNumber;
	
	@AndroidFindBy(accessibility = "Dashboard")
	private WebElement dashboardPageTitle;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"User ID\")]")
	private WebElement userIDTitle;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"User Name\")]")
	private WebElement userNameTitle;
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Status\")]")
	private WebElement statusTitle;
	
	public DashboardPageEnglish(AppiumDriver driver) {
		super(driver);
	}
	
	public boolean isDashboardTitleDisplayed() {
		return isElementDisplayed(dashboardPageTitle);
	}

	public boolean isPacketsUploadedValueDisplayed() {
		return isElementDisplayed(packetUploadedNumber);
	}
	
	public boolean isPacketsSyncedValueDisplayed() {
		return isElementDisplayed(packetSyncedNumber);
	}
	
	public boolean isUserIDDisplayed() {
		return isElementDisplayed(userIDTitle);
	}
	
	public boolean isUserNameDisplayed() {
		return isElementDisplayed(userNameTitle);
	}
	
	public boolean isStatusTitleDisplayed() {
		return isElementDisplayed(statusTitle);
	}
}
