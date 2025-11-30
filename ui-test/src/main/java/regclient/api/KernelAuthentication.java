package regclient.api;

import io.restassured.response.Response;
import org.json.simple.JSONObject;

import java.util.Map;

public class KernelAuthentication extends BaseTestCase {
	private final String authRequest = "/config/Authorization/request.json";
	private final String authInternalRequest = "/config/Authorization/internalAuthRequest.json";
	String cookie;
	public String zonemapCookie = null;
	static String dataKey = "response";
	CommonLibrary clib = new CommonLibrary();
	public final Map<String, String> props = clib.readProperty("Kernel");
	private final String admin_password = props.get("admin_password");
	private final String admin_userName = props.get("admin_userName");
	private final String authenticationInternalEndpoint = props.get("authenticationInternal");
	private final ApplicationLibrary appl = new ApplicationLibrary();

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

	@SuppressWarnings("unchecked")
	public String getAuthForIDREPO() {
		JSONObject actualrequest = getRequestJson(authRequest);

		JSONObject request = new JSONObject();
		request.put("appId", ArcConfigManager.getidRepoAppId());
		request.put("clientId", ArcConfigManager.getidRepoClientId());
		request.put("secretKey", ArcConfigManager.getIdRepoClientSecret());
		actualrequest.put("request", request);

		Response reponse = appl.postWithJson(props.get("authclientidsecretkeyURL"), actualrequest);
		cookie = reponse.getCookie("Authorization");
		return cookie;
	}

	@SuppressWarnings("unchecked")
	public String getAuthForAdmin() {

		JSONObject actualrequest = getRequestJson(authInternalRequest);

		JSONObject request = new JSONObject();
		request.put("appId", ArcConfigManager.getAdminAppId());
		request.put("password", admin_password);

		// if(BaseTestCase.currentModule==null) admin_userName=
		request.put("userName", BaseTestCase.currentModule + "-" + admin_userName);

		request.put("clientId", ArcConfigManager.getAdminClientId());
		request.put("clientSecret", ArcConfigManager.getAdminClientSecret());
		actualrequest.put("request", request);

		Response reponse = appl.postWithJson(authenticationInternalEndpoint, actualrequest);
		String responseBody = reponse.getBody().asString();
		String token = new org.json.JSONObject(responseBody).getJSONObject(dataKey).getString("token");
		return token;
	}

	@SuppressWarnings("unchecked")
	public String getAuthForzoneMap() {

		JSONObject actualrequest = getRequestJson(authInternalRequest);

		JSONObject request = new JSONObject();
		request.put("appId", ArcConfigManager.getAdminAppId());
		request.put("password", admin_password);
		request.put("userName", props.get("admin_zone_userName"));
		request.put("clientId", ArcConfigManager.getAdminClientId());
		request.put("clientSecret", ArcConfigManager.getAdminClientSecret());
		actualrequest.put("request", request);

		Response reponse = appl.postWithJson(authenticationInternalEndpoint, actualrequest);
		String responseBody = reponse.getBody().asString();
		String token = new org.json.JSONObject(responseBody).getJSONObject(dataKey).getString("token");
		return token;
	}
}
