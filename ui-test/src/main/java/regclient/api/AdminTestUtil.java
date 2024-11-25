package regclient.api;

import io.restassured.response.Response;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class AdminTestUtil extends BaseTestCase {

	private static final Logger logger = Logger.getLogger(AdminTestUtil.class);
	public static String token;
	public static final int OTP_CHECK_INTERVAL = 10000;
	public static String tokenRoleIdRepo = "idrepo";
	public static String tokenRoleAdmin = "admin";
	public static boolean initialized = false;
	private static String zoneMappingRequest = "/config/Authorization/zoneMappingRequest.json";

	public static String getmachinespecificationsID(String role){
		String machineDetails= RestClient.getRequestWithCookie(ApplnURI + "/v1/masterdata/machinespecifications/all", MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, COOKIENAME,
				new KernelAuthentication().getTokenByRole(role)).asString();

		JSONObject jsonObject = new JSONObject(machineDetails);

		JSONObject responseObject = jsonObject.getJSONObject("response");
		JSONArray dataArray = responseObject.getJSONArray("data");

		JSONObject firstItem = dataArray.getJSONObject(0);
		String idValue = firstItem.getString("id");

		return idValue;
	}



	public static String generateCurrentUTCTimeStamp() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(date);
	}

	
	public static String machinespecificationsID() {		
		return  AdminTestUtil.getmachinespecificationsID(tokenRoleAdmin);	
	}


	public static String creteaMachine(String signPublicKey, String publicKey ,String name) {
		String token = kernelAuthLib.getTokenByRole("globalAdmin");
		JSONObject requestJson = new JSONObject();		
		Response response = null;
		requestJson.put("id", "string");
		requestJson.put("metadata", new JSONObject()); 
		requestJson.put("requesttime", AdminTestUtil.generateCurrentUTCTimeStamp());
		requestJson.put("version", "string");
		requestJson.put("request", new HashMap<>());       
		requestJson.getJSONObject("request").put("id", "123");
		requestJson.getJSONObject("request").put("ipAddress", "192.168.0.424");
		requestJson.getJSONObject("request").put("isActive", true);
		requestJson.getJSONObject("request").put("langCode",  getLanguageList().get(0));
		requestJson.getJSONObject("request").put("macAddress", "11111111");
		requestJson.getJSONObject("request").put("machineSpecId", machinespecificationsID());
		requestJson.getJSONObject("request").put("name", name);
		requestJson.getJSONObject("request").put("serialNum", "FB5962911687");
		requestJson.getJSONObject("request").put("regCenterId", propsKernel.getProperty("regCenterId"));
		requestJson.getJSONObject("request").put("validityDateTime", "2021-12-24T05:52:46.758Z");
		requestJson.getJSONObject("request").put("publicKey", publicKey);
		requestJson.getJSONObject("request").put("zoneCode", propsKernel.getProperty("zone"));
		requestJson.getJSONObject("request").put("signPublicKey", signPublicKey);

		response = RestClient.postRequestWithCookie(BaseTestCase.ApplnURI + "/v1/masterdata/machines", requestJson.toString(), MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, BaseTestCase.COOKIENAME, token);
		JSONObject responseJson = new JSONObject(response.asString());
	        JSONObject responseobj = responseJson.getJSONObject("response");
	        return responseobj.getString("id");
	}

	public static void initialize() {
		if (!initialized) {
			ConfigManager.init();
			BaseTestCase.initialize();
			KeycloakUserManager.createUsers();
			mapUserToZone(BaseTestCase.currentModule +"-"+propsKernel.getProperty("iam-users-to-create"),propsKernel.getProperty("zone"));
			mapZone( BaseTestCase.currentModule +"-"+propsKernel.getProperty("iam-users-to-create"));
			mapUserToCenter(BaseTestCase.currentModule +"-"+propsKernel.getProperty("iam-users-to-create"),propsKernel.getProperty("regCenterId"));
			mapCenter( BaseTestCase.currentModule +"-"+propsKernel.getProperty("iam-users-to-create"));
			KeycloakUserManager.createUsersWithOutDefaultRole();
			mapUserToZone(KeycloakUserManager.onboardUser,propsKernel.getProperty("zone"));
			mapZone(KeycloakUserManager.onboardUser);
			mapUserToCenter(KeycloakUserManager.onboardUser,propsKernel.getProperty("regCenterId"));
			mapCenter(KeycloakUserManager.onboardUser);
			initialized = true;
		}
	}

	@SuppressWarnings("unchecked")
	public static void mapUserToZone(String user, String zone) {
		String token = kernelAuthLib.getTokenByRole("globalAdmin");
		String url = ApplnURI + propsKernel.getProperty("zoneMappingUrl");
		org.json.simple.JSONObject actualrequest = getRequestJson(zoneMappingRequest);
		JSONObject request = new JSONObject();
		request.put("zoneCode", zone);
		request.put("userId", user);
		request.put("langCode", getLanguageList().get(0));
		request.put("isActive","true");
		actualrequest.put("request", request);
		logger.info(actualrequest);
		Response response = RestClient.postReqestWithCookiesAndBody(url, actualrequest.toString(), token,"postrequest");
		logger.info(user + "Mapped to" + zone + "Zone");
		logger.info(response.getBody().asString());
	}
	
	public static List<String> getLanguageList() {
		logger.info("We have created a Config Manager. Beginning to read properties!");

		environment = ConfigManager.getiam_apienvuser();
		logger.info("Environemnt is  ==== :" + environment);
		ApplnURI = ConfigManager.getiam_apiinternalendpoint();
		logger.info("Application URI ======" + ApplnURI);

		logger.info("Configs from properties file are set.");
		if (!languageList.isEmpty()) {
			return languageList;
		}
		String url = ApplnURI + props.getProperty("preregLoginConfigUrl");
		Response response = RestClient.getRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
		org.json.JSONObject responseJson = new org.json.JSONObject(response.asString());
		org.json.JSONObject responseValue = (org.json.JSONObject) responseJson.get("response");
		String mandatoryLanguage = (String) responseValue.get("mosip.mandatory-languages");

		languageList.add(mandatoryLanguage);
		languageList.addAll(Arrays.asList(((String) responseValue.get("mosip.optional-languages")).split(",")));

		return languageList;
	}

	public static void mapZone(String user) {
		String token = kernelAuthLib.getTokenByRole("globalAdmin");
		String url = ApplnURI + propsKernel.getProperty("zoneMappingActivateUrl");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("isActive","true");
		map.put("userId", user);
		Response response = RestClient.patchRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, "Authorization", token);
		logger.info(response);
	}
	
	public static void mapUserToCenter(String user, String center) {
		String token = kernelAuthLib.getTokenByRole("globalAdmin");
		JSONObject requestJson = new JSONObject();		
		Response response = null;
		requestJson.put("id", "string");
		requestJson.put("metadata", new JSONObject()); 
		requestJson.put("requesttime", AdminTestUtil.generateCurrentUTCTimeStamp());
		requestJson.put("version", "string");
		requestJson.put("request", new HashMap<>());       
		requestJson.getJSONObject("request").put("id", user);
		requestJson.getJSONObject("request").put("name","automation");
		requestJson.getJSONObject("request").put("isActive", true);
		requestJson.getJSONObject("request").put("langCode",  getLanguageList().get(0));
		requestJson.getJSONObject("request").put("statusCode", "active");
		requestJson.getJSONObject("request").put("regCenterId",center);

		response = RestClient.postRequestWithCookie(BaseTestCase.ApplnURI + "/v1/masterdata/usercentermapping", requestJson.toString(), MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, BaseTestCase.COOKIENAME, token);
		JSONObject responseJson = new JSONObject(response.asString());
		System.out.println("responseJson = " + responseJson);
	}
	
	public static void mapCenter(String user) {
		String token = kernelAuthLib.getTokenByRole("globalAdmin");
		String url = ApplnURI + "/v1/masterdata/usercentermapping";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("isActive","true");
		map.put("id", user);
		Response response = RestClient.patchRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, "Authorization", token);
		logger.info(response);
	}
	
	public static String activateMachine(String id) {
		String token = kernelAuthLib.getTokenByRole("globalAdmin");
		String url = ApplnURI + "/v1/masterdata/machines";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("isActive","true");
		map.put("id", id);
		Response response = RestClient.patchRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, "Authorization", token);
		logger.info(response);
		JSONObject responseJson = new JSONObject(response.asString());
        JSONObject responseobj = responseJson.getJSONObject("response");
        return responseobj.getString("status");
	}

}