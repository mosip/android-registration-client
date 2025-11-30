package regclient.api;

import regclient.utils.TestDataReader;
import regclient.utils.TestRunner;

import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.simple.JSONObject;

import io.restassured.response.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

public class BaseTestCase {
	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(BaseTestCase.class);
	public static String environment;
	public static List<String> languageList = new ArrayList<>();
	public static String ApplnURI;
	public static String ApplnURIForKeyCloak;
	public static String testLevel;
	public static Properties props = getproperty(TestRunner.getResourcePath() + "/config/application.properties");
	public static Properties propsKernel = getproperty(TestRunner.getResourcePath() + "/config/Kernel.properties");

	public static Properties propsMap = getproperty(TestRunner.getResourcePath() + "/config/valueMapping.properties");
	public static Properties propsBio = getproperty(TestRunner.getResourcePath() + "/config/bioValue.properties");
	public static String SEPRATOR = "";
	public static String currentModule = "androidregclient";
	public final static String COOKIENAME = "Authorization";
	public static CommonLibrary kernelCmnLib = null;
	public static KernelAuthentication kernelAuthLib = null;
	public String adminCookie = null;
	public String idrepoCookie = null;
	public static String uinEmail;
	public static String uinPhone;

	public static String uin = "";
	public static String perpetualVid = "";
	public static String onetimeuseVid = "";
	public static String temporaryVid = "";

	public static String getOSType() {
		String type = System.getProperty("os.name");
		if (type.toLowerCase().contains("windows")) {
			SEPRATOR = "\\\\";
			return "WINDOWS";
		} else if (type.toLowerCase().contains("linux") || type.toLowerCase().contains("unix")) {
			SEPRATOR = "/";
			return "OTHERS";
		}
		return null;
	}

	public static Properties getproperty(String path) {
		Properties prop = new Properties();

		try {
			File file = new File(path);
			prop.load(new FileInputStream(file));
		} catch (IOException e) {
			logger.error("Exception " + e.getMessage());
		}
		return prop;
	}

	public static void initialize() {
		PropertyConfigurator.configure(getLoggerPropertyConfig());
		kernelAuthLib = new KernelAuthentication();
		kernelCmnLib = new CommonLibrary();
		/**
		 * Make sure test-output is there
		 */

		getOSType();
		logger.info("We have created a Config Manager. Beginning to read properties!");

		environment = ArcConfigManager.getiam_apienvuser();
		logger.info("Environemnt is  ==== :" + environment);
		ApplnURI = ArcConfigManager.getiam_apiinternalendpoint();
		logger.info("Application URI ======" + ApplnURI);
		ApplnURIForKeyCloak = ArcConfigManager.getIAMUrl();
		logger.info("Application URI ======" + ApplnURIForKeyCloak);
		testLevel = System.getProperty("env.testLevel");
		logger.info("Test Level ======" + testLevel);
		logger.info("Test Level ======" + languageList);

		logger.info("Configs from properties file are set.");

	}

	private static Properties getLoggerPropertyConfig() {
		Properties logProp = new Properties();
		logProp.setProperty("log4j.rootLogger", "INFO, Appender1,Appender2");
		logProp.setProperty("log4j.appender.Appender1", "org.apache.log4j.ConsoleAppender");
		logProp.setProperty("log4j.appender.Appender1.layout", "org.apache.log4j.PatternLayout");
		logProp.setProperty("log4j.appender.Appender1.layout.ConversionPattern", "%-7p %d [%t] %c %x - %m%n");
		logProp.setProperty("log4j.appender.Appender2", "org.apache.log4j.FileAppender");
		logProp.setProperty("log4j.appender.Appender2.File", "src/logs/mosip-api-test.log");
		logProp.setProperty("log4j.appender.Appender2.layout", "org.apache.log4j.PatternLayout");
		logProp.setProperty("log4j.appender.Appender2.layout.ConversionPattern", "%-7p %d [%t] %c %x - %m%n");
		return logProp;
	}

	public static JSONObject getRequestJson(String filepath) {
		return kernelCmnLib.readJsonData(filepath, true);

	}

	public static String GethierarchyName(int locationHierarchyLevels) {
		kernelAuthLib = new KernelAuthentication();
		String token = kernelAuthLib.getTokenByRole("admin");
		String url = ApplnURI + props.getProperty("locationhierarchy");
		Response response = RestClient.getRequestWithCookie(url + GethierarchyLevelName(locationHierarchyLevels),
				MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, "Authorization", token);
		org.json.JSONObject responseJson = new org.json.JSONObject(response.asString());
		org.json.JSONObject responseObj = responseJson.getJSONObject("response");
		JSONArray responseArray = responseObj.getJSONArray("locations");

		for (int i = 0, size = responseArray.length(); i < size; i++) {
			org.json.JSONObject idItem = responseArray.getJSONObject(i);
			String lang = idItem.getString("langCode");
			String hierarchyName = idItem.getString("name");
			if (lang.equals(TestDataReader.readData("language"))) {
				return hierarchyName;
			}

		}
		return null;

	}

	public static String GethierarchyLevelName(int locationHierarchyLevels) {
		kernelAuthLib = new KernelAuthentication();
		String token = kernelAuthLib.getTokenByRole("admin");
		String url = ApplnURI + props.getProperty("locationHierarchyLevels");
		Response response = RestClient.getRequestWithCookie(
				url + locationHierarchyLevels + "/" + TestDataReader.readData("language"), MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, "Authorization", token);
		org.json.JSONObject responseJson = new org.json.JSONObject(response.asString());
		org.json.JSONObject responseObj = responseJson.getJSONObject("response");
		JSONArray responseArray = responseObj.getJSONArray("locationHierarchyLevels");
		org.json.JSONObject idItem = responseArray.getJSONObject(0);
		String hierarchyLevelName = idItem.getString("hierarchyLevelName");
		return hierarchyLevelName;

	}

}