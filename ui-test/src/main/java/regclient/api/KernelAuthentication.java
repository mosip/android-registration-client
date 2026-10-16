package regclient.api;

import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class KernelAuthentication extends BaseTestCase {
	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(KernelAuthentication.class);
	private static final String CLIENT_ID = "client_id";
	private static final String CLIENT_SECRET = "client_secret";
	private static final String GRANT_TYPE_KEY = "grant_type";
	private static final String GRANT_TYPE = "client_credentials";
	private static final String GRANT_TYPE_PASSWORD = "password";
	private static final String USERNAME_KEY = "username";
	private static final String PASSWORD_KEY = "password";
	private static final String ACCESS_TOKEN = "access_token";

	public String zonemapCookie = null;
	CommonLibrary clib = new CommonLibrary();
	public final Map<String, String> props = clib.readProperty("Kernel");
	private final String admin_password = props.get("admin_password");
	private final String admin_userName = props.get("admin_userName");

	private static String getKeycloakTokenUrl() {
		return ArcConfigManager.getIAMUrl() + "/realms/" + ArcConfigManager.getIAMRealmId()
				+ "/protocol/openid-connect/token";
	}

	public String getTokenByRole(String role) {
		return getTokenByRole(role, null);
	}

	public String getTokenByRole(String role, String tokenType) {
		String insensitiveRole = null;
		if (role != null)
			insensitiveRole = role.toLowerCase();
		else
			return "";

		switch (insensitiveRole) {

		case "idrepo":
			if (!kernelCmnLib.isValidToken(idrepoCookie))
				idrepoCookie = kernelAuthLib.getAuthForIDREPO();
			return idrepoCookie;
		case "admin":
			if (!kernelCmnLib.isValidToken(adminCookie))
				adminCookie = kernelAuthLib.getAuthForAdmin();
			return adminCookie;
		case "globaladmin":
			if (!kernelCmnLib.isValidToken(zonemapCookie))
				zonemapCookie = kernelAuthLib.getAuthForzoneMap();
			return zonemapCookie;
		default:
			if (!kernelCmnLib.isValidToken(adminCookie))
				adminCookie = kernelAuthLib.getAuthForAdmin();
			return adminCookie;
		}

	}

	public String getAuthForIDREPO() {
		return getAuthTokenFromKeyCloak(ArcConfigManager.getidRepoClientId(),
				ArcConfigManager.getIdRepoClientSecret());
	}

	public String getAuthForRegproc() {
		return getAuthTokenFromKeyCloak(ArcConfigManager.getRegprocClientId(),
				ArcConfigManager.getRegprocClientSecret());
	}

	public String getAuthForAdmin() {
		return getAuthTokenFromKeyCloakPassword(ArcConfigManager.getAdminClientId(),
				ArcConfigManager.getAdminClientSecret(), BaseTestCase.currentModule + "-" + admin_userName,
				admin_password);
	}

	public String getAuthForzoneMap() {
		return getAuthTokenFromKeyCloakPassword(ArcConfigManager.getAdminClientId(),
				ArcConfigManager.getAdminClientSecret(), props.get("admin_zone_userName"), admin_password);
	}

	public static String getAuthTokenFromKeyCloak(String clientId, String clientSecret) {
		Map<String, String> params = new HashMap<>();
		params.put(CLIENT_ID, clientId);
		params.put(CLIENT_SECRET, clientSecret);
		params.put(GRANT_TYPE_KEY, GRANT_TYPE);
		return requestKeycloakToken(params);
	}

	public static String getAuthTokenFromKeyCloakPassword(String clientId, String clientSecret, String username,
			String password) {
		Map<String, String> params = new HashMap<>();
		params.put(CLIENT_ID, clientId);
		params.put(CLIENT_SECRET, clientSecret);
		params.put(GRANT_TYPE_KEY, GRANT_TYPE_PASSWORD);
		params.put(USERNAME_KEY, username);
		params.put(PASSWORD_KEY, password);
		return requestKeycloakToken(params);
	}

	private static String requestKeycloakToken(Map<String, String> params) {
		String tokenUrl = getKeycloakTokenUrl();
		Response response;
		try {
			response = given().relaxedHTTPSValidation().contentType(ContentType.URLENC).formParams(params).when()
					.post(tokenUrl);
		} catch (Exception e) {
			logger.error("Error sending POST request to Keycloak token URL: " + tokenUrl, e);
			return "";
		}

		if (response == null) {
			logger.error("Keycloak token request returned null response");
			return "";
		}
		int statusCode = response.getStatusCode();
		if (statusCode < 200 || statusCode >= 300) {
			logger.error("Keycloak token request failed with status code: " + statusCode);
			return "";
		}
		logger.info("Keycloak token request successful");

		return new org.json.JSONObject(response.getBody().asString()).optString(ACCESS_TOKEN, "");
	}
}
