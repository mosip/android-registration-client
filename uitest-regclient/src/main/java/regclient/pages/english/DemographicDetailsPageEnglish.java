package regclient.pages.english;

import static org.testng.Assert.assertTrue;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import regclient.api.BaseTestCase;
import regclient.api.FetchUiSpec;
import regclient.page.BasePage;
import regclient.page.ConsentPage;
import regclient.page.DemographicDetailsPage;
import regclient.page.DocumentUploadPage;
import regclient.utils.TestDataReader;


public class DemographicDetailsPageEnglish extends DemographicDetailsPage {

	@AndroidFindBy(accessibility = "Male")
	private WebElement maleButton;

	@AndroidFindBy(accessibility = "Female")
	private WebElement femaleButton;

	@AndroidFindBy(accessibility = "CONTINUE")
	private WebElement continueButton;

	@AndroidFindBy(accessibility = "Invalid input")
	private WebElement errorMessageInvalidInputText;

	@AndroidFindBy(accessibility = "Scrim")
	private WebElement backgroundScreen;

	@AndroidFindBy(accessibility = "FETCH DATA")
	private WebElement fetchDataButton;


	public DemographicDetailsPageEnglish(AppiumDriver driver) {
		super(driver);
	}

	@SuppressWarnings("deprecation")
	public boolean isDemographicDetailsPageDisplayed() {
		WebElement  demographicDetailspage = findElementWithRetry(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"" + FetchUiSpec.getScreenTitle("DemographicDetails") + "\"))"));
		return isElementDisplayed(demographicDetailspage);
	}

	public boolean isErrorMessageInvalidInputTextDisplayed() {
		return isElementDisplayed(errorMessageInvalidInputText);
	}

	@SuppressWarnings("deprecation")
	public  ConsentPage clickOnConsentPageTitle() {
		WebElement  consentTitle = findElementWithRetry(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"" + FetchUiSpec.getScreenTitle("consentdet") + "\"))"));
		clickOnElement(consentTitle);
		return new ConsentPageEnglish(driver);
	}

	public  DocumentUploadPage clickOnContinueButton() {
		clickOnElement(continueButton);
		return new DocumentuploadPageEnglish(driver);

	}
	
	public  boolean isContinueButtonEnable() {
		return isElementEnabled(continueButton);

	}

	public boolean isPreRegFetchDataTextBoxDisplay() {
		return isElementDisplayed(fetchDataButton);
	}

	public void fillDemographicDetailsPage(String age) {
		List<String> idList=FetchUiSpec.getAllIds("DemographicDetails");
		for(String id : idList) {
			if(FetchUiSpec.getRequiredTypeUsingId(id)) {
				if(FetchUiSpec.getControlTypeUsingId(id).equals("textbox")) {
					waitTime(3);
					boolean isdisplayed =isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId(id)+"\")")));
					assertTrue(isdisplayed,"Verify if "+id+" header is displayed");
					clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id) + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),BasePage.generateData(FetchUiSpec.getTextBoxUsingId(id)));
					if(FetchUiSpec.getTransliterateTypeUsingId(id)) 
						assertTrue(checkSecondLanguageTextBoxNotNull(id),"Verify if "+id+" is enter in second language text box");
				}
				else if(FetchUiSpec.getControlTypeUsingId(id).equals("dropdown") &&  FetchUiSpec.getFormatUsingId(id).equals("none")){	
					waitTime(3);
					while(!isElementDisplayed(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId(id)+"\")"))) {
						swipeOrScroll();
					}
					boolean isdisplayed =isElementDisplayed(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId(id)+"\")"));
					assertTrue(isdisplayed,"Verify if "+id+" header is displayed");
					WebElement dropdownElement=findElement(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.widget.Button"));
					clickOnElement(dropdownElement);
					waitTime(3);
					if(!isElementDisplayed(dropdownElement)) {				
						clickOnElement(findElement(By.className("android.view.View")));
					}else if(isElementDisplayed(dropdownElement))  {
						swipeOrScroll();
						clickOnElement(dropdownElement);
						waitTime(2);
						clickOnElement(findElement(By.className("android.view.View")));
					}
					waitTime(2);
					if(isElementDisplayed(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.widget.Button[contains(@content-desc, \"Select Option\")]"))) {
						clickOnElement(dropdownElement);
						waitTime(2);
						clickOnElement(findElement(By.className("android.view.View")));
					}
				}else if(FetchUiSpec.getControlTypeUsingId(id).equals("dropdown") &&  FetchUiSpec.getFormatUsingId(id).equals("")){	
					if(!isElementDisplayed(maleButton)) {
						swipeOrScroll();
						clickOnElement(maleButton);		
					}else
						clickOnElement(maleButton);		

				}else if(FetchUiSpec.getControlTypeUsingId(id).equals("ageDate")){
					waitTime(3);
					boolean isdisplayed =isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId(id)+"\")")));
					assertTrue(isdisplayed,"Verify if "+id+" header is displayed");
					if(age.equals("adult"))
						clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+ FetchUiSpec.getValueUsingId(id) +"\")]/parent::android.view.View/following-sibling::android.widget.EditText[1]")),"20");
					else if(age.equals("minor"))
						clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+ FetchUiSpec.getValueUsingId(id) +"\")]/parent::android.view.View/following-sibling::android.widget.EditText[1]")),"12");
					else if(age.equals("infant"))
						clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+ FetchUiSpec.getValueUsingId(id) +"\")]/parent::android.view.View/following-sibling::android.widget.EditText[1]")),"4");
					else if(age.equals("currentCalenderDate")) {
						waitTime(1);
						clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+ FetchUiSpec.getValueUsingId(id) +"\")]/parent::android.view.View/following-sibling::android.view.View")));		
						waitTime(1);
						clickOnElement(backgroundScreen);
						waitTime(1);
						assertTrue(checkDateFormatAndCurrectDate(id),"Verify date format and current date and time while selecting age date");
					}
				}
			}
			else if(id.equals("residenceStatus")) {
				if(FetchUiSpec.getControlTypeUsingId(id).equals("dropdown") &&  FetchUiSpec.getFormatUsingId(id).equals("none")){	
					waitTime(2);
					boolean isdisplayed =isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId(id)+"\")")));
					assertTrue(isdisplayed,"Verify if "+id+" header is displayed");
					WebElement dropdownElement=findElement(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.widget.Button"));
					clickOnElement(dropdownElement);
					waitTime(2);
					if(!isElementDisplayed(dropdownElement)) {				
						clickOnElement(findElement(By.className("android.view.View")));
					}else if(isElementDisplayed(dropdownElement))  {
						swipeOrScroll();
						clickOnElement(dropdownElement);
						waitTime(2);
						clickOnElement(findElement(By.className("android.view.View")));
					}
					waitTime(2);
					if(isElementDisplayed(By.xpath("//android.view.View[contains(@content-desc, \""+FetchUiSpec.getValueUsingId(id)+"\")]/parent::android.view.View/parent::android.widget.Button[contains(@content-desc, \"Select Option\")]"))) {
						clickOnElement(dropdownElement);
						waitTime(2);
						clickOnElement(findElement(By.className("android.view.View")));
					}
				}
			}
			if(id.equals("introducerName") && FetchUiSpec.getFlowType().equals("newProcess")) {
				if(age.equals("minor") ||  age.equals("infant") ||  age.equals("currentCalenderDate")) {
					if(FetchUiSpec.getControlTypeUsingId(id).equals("textbox")) {
						waitTime(3);
						boolean isdisplayed =isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId(id)+"\")")));
						assertTrue(isdisplayed,"Verify if "+id+" header is displayed");
						clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id) + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),BasePage.generateData(FetchUiSpec.getTextBoxUsingId(id)));
						if(FetchUiSpec.getTransliterateTypeUsingId(id)) 
							assertTrue(checkSecondLanguageTextBoxNotNull(id),"Verify if "+id+" is enter in second language text box");
					}
				}
			}if(id.equals("introducerRID") && FetchUiSpec.getFlowType().equals("newProcess")) {
				if(age.equals("minor") ||  age.equals("infant") ||  age.equals("currentCalenderDate")) {
					if(FetchUiSpec.getControlTypeUsingId(id).equals("textbox")) {
						waitTime(3);
						boolean isdisplayed =isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId(id)+"\")")));
						assertTrue(isdisplayed,"Verify if "+id+" header is displayed");
						clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id) + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),TestDataReader.readData("RID"));
						if(FetchUiSpec.getTransliterateTypeUsingId(id)) 
							assertTrue(checkSecondLanguageTextBoxNotNull(id),"Verify if "+id+" is enter in second language text box");
					}
				}
			}
		}
	}

	public void editDemographicDetailsPage(String age) {
		List<String> idList=FetchUiSpec.getAllIds("DemographicDetails");
		for(String id : idList) {
			if(FetchUiSpec.getRequiredTypeUsingId(id)) {
				if(FetchUiSpec.getControlTypeUsingId(id).equals("textbox")) {
					waitTime(3);
					boolean isdisplayed =isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId(id)+"\")")));
					assertTrue(isdisplayed,"Verify if "+id+" header is displayed");
					clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id) + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),BasePage.generateData(FetchUiSpec.getTextBoxUsingId(id)));
					if(FetchUiSpec.getTransliterateTypeUsingId(id)) 
						assertTrue(checkSecondLanguageTextBoxNotNull(id),"Verify if "+id+" is enter in second language text box");
				}else if(FetchUiSpec.getControlTypeUsingId(id).equals("ageDate")){
					waitTime(3);
					boolean isdisplayed =isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId(id)+"\")")));
					assertTrue(isdisplayed,"Verify if "+id+" header is displayed");
					if(age.equals("adult"))
						clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+ FetchUiSpec.getValueUsingId(id) +"\")]/parent::android.view.View/following-sibling::android.widget.EditText[1]")),"20");
					else if(age.equals("minor"))
						clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+ FetchUiSpec.getValueUsingId(id) +"\")]/parent::android.view.View/following-sibling::android.widget.EditText[1]")),"12");
					else if(age.equals("infant"))
						clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+ FetchUiSpec.getValueUsingId(id) +"\")]/parent::android.view.View/following-sibling::android.widget.EditText[1]")),"4");
					else if(age.equals("currentCalenderDate")) {
						waitTime(1);
						clickOnElement(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+ FetchUiSpec.getValueUsingId(id) +"\")]/parent::android.view.View/following-sibling::android.view.View")));		
						waitTime(1);
						clickOnElement(backgroundScreen);
						waitTime(1);
						assertTrue(checkDateFormatAndCurrectDate(id),"Verify date format and current date and time while selecting age date");
					}
				}
			}
			if(id.equals("introducerName") && FetchUiSpec.getFlowType().equals("newProcess")) {
				if(age.equals("minor") ||  age.equals("infant") ||  age.equals("currentCalenderDate")) {
					if(FetchUiSpec.getControlTypeUsingId(id).equals("textbox")) {
						waitTime(3);
						boolean isdisplayed =isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId(id)+"\")")));
						assertTrue(isdisplayed,"Verify if "+id+" header is displayed");
						clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id) + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),BasePage.generateData(FetchUiSpec.getTextBoxUsingId(id)));
						if(FetchUiSpec.getTransliterateTypeUsingId(id)) 
							assertTrue(checkSecondLanguageTextBoxNotNull(id),"Verify if "+id+" is enter in second language text box");
					}
				}
			}if(id.equals("introducerRID") && FetchUiSpec.getFlowType().equals("newProcess")) {
				if(age.equals("minor") ||  age.equals("infant") ||  age.equals("currentCalenderDate")) {
					if(FetchUiSpec.getControlTypeUsingId(id).equals("textbox")) {
						waitTime(3);
						boolean isdisplayed =isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId(id)+"\")")));
						assertTrue(isdisplayed,"Verify if "+id+" header is displayed");
						clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id) + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),TestDataReader.readData("RID"));
						if(FetchUiSpec.getTransliterateTypeUsingId(id)) 
							assertTrue(checkSecondLanguageTextBoxNotNull(id),"Verify if "+id+" is enter in second language text box");
					}
				}
			}
		}
	}

	public boolean checkSecondLanguageTextBoxNotNull(String id) {
		if(getTextFromLocator(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id) + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[2]")))==null || getTextFromLocator(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id) + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[2]")))=="" )
			return	false;
		else
			return	true;
	}

	public  boolean checkDateFormatAndCurrectDate(String id) {
		if(getTextFromLocator(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \""+ FetchUiSpec.getValueUsingId(id) +"\")]/parent::android.view.View/following-sibling::android.view.View"))).equalsIgnoreCase(getCurrentDate())) 
			return	true;
		else
			return false;
	}
	
	public void fillIntroducerDetailsInDemographicDetailsPage(String age) {
		List<String> idList=FetchUiSpec.getAllIds("DemographicDetails");
		for(String id : idList) {
			if(id.equals("introducerName")) {
				if(age.equals("minor") ||  age.equals("infant") ||  age.equals("currentCalenderDate")) {
					if(FetchUiSpec.getControlTypeUsingId(id).equals("textbox")) {
						waitTime(3);
						boolean isdisplayed =isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId(id)+"\")")));
						assertTrue(isdisplayed,"Verify if "+id+" header is displayed");
						clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id) + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),BasePage.generateData(FetchUiSpec.getTextBoxUsingId(id)));
						if(FetchUiSpec.getTransliterateTypeUsingId(id)) 
							assertTrue(checkSecondLanguageTextBoxNotNull(id),"Verify if "+id+" is enter in second language text box");
					}
				}
			}if(id.equals("introducerUIN")) {
				if(age.equals("minor") ||  age.equals("infant") ||  age.equals("currentCalenderDate")) {
					if(FetchUiSpec.getControlTypeUsingId(id).equals("textbox")) {
						waitTime(3);
						boolean isdisplayed =isElementDisplayed(findElementWithRetry(MobileBy.AndroidUIAutomator("new UiSelector().descriptionContains(\""+FetchUiSpec.getValueUsingId(id)+"\")")));
						assertTrue(isdisplayed,"Verify if "+id+" header is displayed");
						clickAndsendKeysToTextBox(findElementWithRetry(By.xpath("//android.view.View[contains(@content-desc, \"" + FetchUiSpec.getValueUsingId(id) + "\")]/parent::android.view.View/following-sibling::android.view.View/descendant::android.widget.EditText[1]")),TestDataReader.readData("UINminor"));
						if(FetchUiSpec.getTransliterateTypeUsingId(id)) 
							assertTrue(checkSecondLanguageTextBoxNotNull(id),"Verify if "+id+" is enter in second language text box");
					}
				}
			}
		}
	}
}
