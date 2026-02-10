package regclient.pages.kannada;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.api.KeycloakUserManager;
import regclient.page.DashboardPage;

public class DashboardPageKannada extends DashboardPage {
	private static final Logger logger = Logger.getLogger(DashboardPageKannada.class);
	
	@AndroidFindBy(xpath = "//android.view.View[@content-desc=\"Dashboard\"]/following-sibling::android.view.View[1]")
	private WebElement packetCreatedNumber;

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
	
	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc,'User ID')]")
	private WebElement userTable;

	public DashboardPageKannada(AppiumDriver driver) {
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
	
	public String getPacketsCreatedCount() {
	    return getVisibleValue(packetCreatedNumber);
	}

	public String getPacketsSyncedCount() {
	    return getVisibleValue(packetSyncedNumber);
	}

	public String getPacketsUploadedCount() {
	    return getVisibleValue(packetUploadedNumber);
	}
	
	public void logPacketCounts() {
	    String created = getPacketsCreatedCount();
	    String synced = getPacketsSyncedCount();
	    String uploaded = getPacketsUploadedCount();
	    logger.info("No. of Packets Created  : " + created);
	    logger.info("No. of Packets Synced   : " + synced);
	    logger.info("No. of Packets Uploaded : " + uploaded);
	}
	
	public boolean isLoginUserActive() {
	    return isValuePresentInTable(userTable, KeycloakUserManager.moduleSpecificUser,2,"active");
	}

}
