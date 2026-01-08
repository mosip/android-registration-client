package regclient.api;

import io.mosip.testrig.apirig.testrunner.OTPListener;
import io.restassured.response.Response;
import regclient.utils.TestDataReader;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.javafaker.Faker;

import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
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

	public static String getmachinespecificationsID(String role) {
		String machineDetails = RestClient
				.getRequestWithCookie(ApplnURI + "/v1/masterdata/machinespecifications/all", MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, COOKIENAME, new KernelAuthentication().getTokenByRole(role))
				.asString();

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
	
	public static String generateFutureUTCTimeStamp(int daysToAdd) {
	    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	    calendar.add(Calendar.DAY_OF_YEAR, daysToAdd);

	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

	    return dateFormat.format(calendar.getTime());
	}


	public static String machinespecificationsID() {
		return AdminTestUtil.getmachinespecificationsID(tokenRoleAdmin);
	}

	public static String creteaMachine(String signPublicKey, String publicKey, String name) {
		String token = kernelAuthLib.getTokenByRole("globalAdmin");
		JSONObject requestJson = new JSONObject();
		Response response = null;
		requestJson.put("id", "string");
		requestJson.put("metadata", new JSONObject());
		requestJson.put("requesttime", AdminTestUtil.generateCurrentUTCTimeStamp());
		requestJson.put("version", "string");
		JSONObject request = new JSONObject();
		request.put("id", "123");
		request.put("ipAddress", "192.168.0.424");
		request.put("isActive", true);
		request.put("langCode", getLanguageList().get(0));
		request.put("macAddress", "11111111");
		request.put("machineSpecId", machinespecificationsID());
		request.put("name", name);
		requestJson.put("request", request);
		requestJson.getJSONObject("request").put("serialNum", "FB5962911687");
		requestJson.getJSONObject("request").put("regCenterId", propsKernel.getProperty("regCenterId"));
		requestJson.getJSONObject("request").put("validityDateTime", AdminTestUtil.generateFutureUTCTimeStamp(30));
		requestJson.getJSONObject("request").put("publicKey", publicKey);
		requestJson.getJSONObject("request").put("zoneCode", propsKernel.getProperty("zone"));
		requestJson.getJSONObject("request").put("signPublicKey", signPublicKey);

		response = RestClient.postRequestWithCookie(BaseTestCase.ApplnURI + "/v1/masterdata/machines",
				requestJson.toString(), MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, BaseTestCase.COOKIENAME,
				token);
		JSONObject responseJson = new JSONObject(response.asString());
		JSONObject responseobj = responseJson.getJSONObject("response");
		return responseobj.getString("id");
	}

	public static void initialize() {
		if (!initialized) {
			ArcConfigManager.init();
			BaseTestCase.initialize();
			//user zone and center mapping
			KeycloakUserManager.createUsers();
			mapUserToZone(BaseTestCase.currentModule + "-" + propsKernel.getProperty("iam-users-to-create"),
					propsKernel.getProperty("zone"));
			mapZone(BaseTestCase.currentModule + "-" + propsKernel.getProperty("iam-users-to-create"));
			mapUserToCenter(BaseTestCase.currentModule + "-" + propsKernel.getProperty("iam-users-to-create"),
					propsKernel.getProperty("regCenterId"));
			mapCenter(BaseTestCase.currentModule + "-" + propsKernel.getProperty("iam-users-to-create"));
			//user zone and center mapping
			KeycloakUserManager.createUsersWithOutDefaultRole();
			mapUserToZone(KeycloakUserManager.onboardUser, propsKernel.getProperty("zone"));
			mapZone(KeycloakUserManager.onboardUser);
			mapUserToCenter(KeycloakUserManager.onboardUser, propsKernel.getProperty("regCenterId"));
			mapCenter(KeycloakUserManager.onboardUser);
			//user zone and center mapping
			KeycloakUserManager.createUsersWithOutSupervisorRole();
			mapUserToZone(BaseTestCase.currentModule + "-" + propsKernel.getProperty("iam-users-to-create-operator"),
					propsKernel.getProperty("zone"));
			mapZone(BaseTestCase.currentModule + "-" + propsKernel.getProperty("iam-users-to-create-operator"));
			mapUserToCenter(BaseTestCase.currentModule + "-" + propsKernel.getProperty("iam-users-to-create-operator"),
					propsKernel.getProperty("regCenterId"));
			mapCenter(BaseTestCase.currentModule + "-" + propsKernel.getProperty("iam-users-to-create-operator"));
			KeycloakUserManager.createOnboardingUser();
			mapUserToZone(BaseTestCase.currentModule + "-" + propsKernel.getProperty("iam-users-to-create-onboarder"),
					propsKernel.getProperty("zone"));
			mapZone(BaseTestCase.currentModule + "-" + propsKernel.getProperty("iam-users-to-create-onboarder"));
			mapUserToCenter(BaseTestCase.currentModule + "-" + propsKernel.getProperty("iam-users-to-create-onboarder"),
					propsKernel.getProperty("regCenterId"));
			mapCenter(BaseTestCase.currentModule + "-" + propsKernel.getProperty("iam-users-to-create-onboarder"));

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
		request.put("isActive", "true");
		actualrequest.put("request", request);
		logger.info(actualrequest);
		Response response = RestClient.postReqestWithCookiesAndBody(url, actualrequest.toString(), token,
				"postrequest");
		logger.info(user + "Mapped to" + zone + "Zone");
		logger.info(response.getBody().asString());
	}

	public static List<String> getLanguageList() {
		logger.info("We have created a Config Manager. Beginning to read properties!");

		environment = ArcConfigManager.getiam_apienvuser();
		logger.info("Environemnt is  ==== :" + environment);
		ApplnURI = ArcConfigManager.getiam_apiinternalendpoint();
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
		map.put("isActive", "true");
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
		JSONObject request = new JSONObject();
		request.put("id", user);
		request.put("name", "automation");
		request.put("isActive", true);
		request.put("langCode", getLanguageList().get(0));
		request.put("statusCode", "active");
		request.put("regCenterId", center);
		requestJson.put("request", request);

		response = RestClient.postRequestWithCookie(BaseTestCase.ApplnURI + "/v1/masterdata/usercentermapping",
				requestJson.toString(), MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, BaseTestCase.COOKIENAME,
				token);
		JSONObject responseJson = new JSONObject(response.asString());
		System.out.println("responseJson = " + responseJson);
	}

	public static void sendOtp(String userId, String langCode) {
		String token = kernelAuthLib.getTokenByRole("globalAdmin");
		JSONObject requestJson = new JSONObject();
		requestJson.put("id", "mosip.pre-registration.login.sendotp");
		requestJson.put("version", "1.0");
		requestJson.put("requesttime", AdminTestUtil.generateCurrentUTCTimeStamp());
		JSONObject innerRequest = new JSONObject();
		innerRequest.put("userId", userId);
		innerRequest.put("langCode", langCode);
		requestJson.put("request", innerRequest);
		Response response = RestClient.postRequestWithCookie(
				BaseTestCase.ApplnURI + "/preregistration/v1/login/sendOtp/langcode", requestJson.toString(),
				MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, BaseTestCase.COOKIENAME, token);
		JSONObject responseJson = new JSONObject(response.asString());
		System.out.println("Response JSON = " + responseJson);
		if (responseJson.has("response") && responseJson.getJSONObject("response").has("status")
				&& "success".equalsIgnoreCase(responseJson.getJSONObject("response").getString("status"))) {
			System.out.println("OTP request sent successfully.");
		} else {
			throw new RuntimeException("OTP request failed. Response: " + responseJson.toString());
		}
	}

	public static Response validateOtp(String userId, String otp) {
		String token = kernelAuthLib.getTokenByRole("globalAdmin");
		JSONObject requestJson = new JSONObject();
		requestJson.put("id", "mosip.pre-registration.login.useridotp");
		requestJson.put("version", "1.0");
		requestJson.put("requesttime", AdminTestUtil.generateCurrentUTCTimeStamp());

		JSONObject innerRequest = new JSONObject();
		innerRequest.put("userId", userId);
		innerRequest.put("otp", otp);
		requestJson.put("request", innerRequest);
		Response response = RestClient.postRequestWithCookie(
				BaseTestCase.ApplnURI + "/preregistration/v1/login/validateOtp", requestJson.toString(),
				MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, BaseTestCase.COOKIENAME, token);

		if (response == null) {
			throw new RuntimeException("No response from validateOtp API");
		}

		int statusCode = response.getStatusCode();
		if (statusCode < 200 || statusCode >= 300) {
			throw new RuntimeException(
					"HTTP error from validateOtp API. Status: " + statusCode + " Body: " + response.asString());
		}

		JSONObject responseJson;
		try {
			responseJson = new JSONObject(response.asString());
		} catch (Exception e) {
			throw new RuntimeException("Invalid JSON returned from validateOtp API: " + response.asString(), e);
		}
		if (responseJson.has("response") && responseJson.getJSONObject("response").has("status")
				&& "success".equalsIgnoreCase(responseJson.getJSONObject("response").getString("status"))) {
			System.out.println("OTP validation successful.");
			return response;
		} else {
			throw new RuntimeException("OTP validation failed. Response: " + responseJson.toString());
		}
	}

	public static String createPreRegistration(String userId, String age) {
		String token = kernelAuthLib.getTokenByRole("globalAdmin");

		// Generate base prereg JSON (as String)
		String baseJson = io.mosip.testrig.apirig.utils.AdminTestUtil.generateHbsForPrereg(false);

		// Parse into JSONObject
		JSONObject requestJson = new JSONObject(baseJson);

		// Faker for dynamic values
		Faker faker = new Faker();
		String randomName = faker.name().fullName();
		String address1 = faker.address().streetAddress();
		String address2 = faker.address().secondaryAddress();
		String address3 = faker.address().buildingNumber();
		String phone = faker.number().digits(10);
		String email = faker.internet().emailAddress();
		int ageInt;
		String category;
		try {
			ageInt = Integer.parseInt(age);
			category = (ageInt < 6) ? "infant" : (ageInt < 18) ? "minor" : "adult";
		} catch (NumberFormatException e) {
			category = age.toLowerCase();
			switch (category) {
			case "infant":
				ageInt = 2;
				break;
			case "minor":
				ageInt = 10;
				break;
			default:
				ageInt = 25;
				category = "adult";
				break;
			}
		}

		String dob = LocalDate.now().minusYears(ageInt).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

		// Navigate to identity node
		JSONObject request = requestJson.getJSONObject("request");
		JSONObject identity = request.getJSONObject("demographicDetails").getJSONObject("identity");

		// Fill only if empty or placeholder
		if (identity.optJSONArray("fullName") != null) {
			for (int i = 0; i < identity.getJSONArray("fullName").length(); i++) {
				identity.getJSONArray("fullName").getJSONObject(i).put("value", randomName);
			}
		}

		if (!identity.has("dateOfBirth") || identity.get("dateOfBirth").toString().contains("{{")) {
			identity.put("dateOfBirth", dob);
		}

		if (!identity.has("phone") || identity.get("phone").toString().contains("{{")) {
			identity.put("phone", phone);
		}

		if (!identity.has("email") || identity.get("email").toString().contains("{{")) {
			identity.put("email", email);
		}

		if (identity.optJSONArray("addressLine1") != null) {
			for (int i = 0; i < identity.getJSONArray("addressLine1").length(); i++) {
				identity.getJSONArray("addressLine1").getJSONObject(i).put("value", address1);
			}
		}

		if (identity.optJSONArray("addressLine2") != null) {
			for (int i = 0; i < identity.getJSONArray("addressLine2").length(); i++) {
				identity.getJSONArray("addressLine2").getJSONObject(i).put("value", address2);
			}
		}

		if (identity.optJSONArray("addressLine3") != null) {
			for (int i = 0; i < identity.getJSONArray("addressLine3").length(); i++) {
				identity.getJSONArray("addressLine3").getJSONObject(i).put("value", address3);
			}
		}

		if (!identity.has("postalCode") || identity.get("postalCode").toString().contains("{{")) {
			identity.put("postalCode", propsKernel.getProperty("regCenterId"));
		}

		// Replace placeholders for langCode, id, and version
		if (request.has("langCode") && request.getString("langCode").contains("{{")) {
			request.put("langCode", "eng");
		}
		if (requestJson.has("id") && requestJson.getString("id").contains("{{")) {
			requestJson.put("id", "mosip.pre-registration.demographic.create");
		}
		if (requestJson.has("version") && requestJson.getString("version").contains("{{")) {
			requestJson.put("version", "1.0");
		}

		// update requesttime
		requestJson.put("requesttime", AdminTestUtil.generateCurrentUTCTimeStamp());

		// Hit API
		Response response = RestClient.postRequestWithCookie(
				BaseTestCase.ApplnURI + "/preregistration/v1/applications/prereg", requestJson.toString(),
				MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, BaseTestCase.COOKIENAME, token);

		String preRegId = null;
		JSONObject responseJson = new JSONObject(response.asString());
		if (responseJson.has("response") && responseJson.getJSONObject("response").has("preRegistrationId")) {
			preRegId = responseJson.getJSONObject("response").getString("preRegistrationId");
			System.out.println("preRegistrationId = " + preRegId);
		} else {
			throw new RuntimeException("preRegistrationId not found in response: " + responseJson.toString());
		}
		return preRegId;
	}

	public static String getPreRegistrationFlow(String age) {
		Faker faker = new Faker();
		String userId = faker.internet().emailAddress();
		sendOtp(userId, TestDataReader.readData("language"));
		validateOtp(userId, OTPListener.getOtp(userId));
		String preRegId = createPreRegistration(userId, age);
		return preRegId;
	}

	public static void mapCenter(String user) {
		String token = kernelAuthLib.getTokenByRole("globalAdmin");
		String url = ApplnURI + "/v1/masterdata/usercentermapping";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("isActive", "true");
		map.put("id", user);
		Response response = RestClient.patchRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, "Authorization", token);
		logger.info(response);
	}

	public static String activateMachine(String id) {
		String token = kernelAuthLib.getTokenByRole("globalAdmin");
		String url = ApplnURI + "/v1/masterdata/machines";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("isActive", "true");
		map.put("id", id);
		Response response = RestClient.patchRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, "Authorization", token);
		logger.info(response);
		JSONObject responseJson = new JSONObject(response.asString());
		JSONObject responseobj = responseJson.getJSONObject("response");
		return responseobj.getString("status");
	}

}