package regclient.pages.french;


import org.openqa.selenium.WebElement;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.api.KeycloakUserManager;
import regclient.page.DashboardPage;
import regclient.pages.english.DashboardPageEnglish;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DashboardPageFrench extends DashboardPage {
	
	
	private static final Logger logger = LoggerFactory.getLogger(DashboardPageFrench.class);

	
	@AndroidFindBy(xpath = "//android.view.View[@content-desc=\"Tableau de bord\"]/following-sibling::android.view.View[3]")
	private WebElement packetUploadedNumber;

	@AndroidFindBy(xpath = "//android.view.View[@content-desc=\"Tableau de bord\"]/following-sibling::android.view.View[2]")
	private WebElement packetSyncedNumber;
	
	@AndroidFindBy(xpath = "//android.view.View[@content-desc=\"Tableau de bord\"]/following-sibling::android.view.View[1]")
	private WebElement packetCreatedNumber;

	@AndroidFindBy(accessibility = "Tableau de bord")
	private WebElement dashboardPageTitle;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Identifiant utilisateur\")]")
	private WebElement userIDTitle;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Nom d’utilisateur\")]")
	private WebElement userNameTitle;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, \"Statut\")]")
	private WebElement statusTitle;

	@AndroidFindBy(xpath = "//android.view.View[contains(@content-desc,'Utilisatrices')]")
	private WebElement userTable;
	
	public DashboardPageFrench(AppiumDriver driver) {
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
