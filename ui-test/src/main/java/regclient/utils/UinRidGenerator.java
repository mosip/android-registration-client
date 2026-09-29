package regclient.utils;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import io.appium.java_client.AppiumDriver;
import io.restassured.response.Response;
import regclient.androidTestCases.RidGenerator;
import regclient.api.BaseTestCase;
import regclient.api.KernelAuthentication;
import regclient.api.RestClient;

public class UinRidGenerator {

	private static final Logger logger = Logger.getLogger(UinRidGenerator.class);

	private UinRidGenerator() {
	}

	public static void ensureValidIntroducer(AppiumDriver driver) throws InterruptedException {
		String existingRid = TestDataReader.readData("AID");
		if (existingRid == null || existingRid.isBlank() || !isRidValidForEnvironment(existingRid)) {
			String rid = RidGenerator.generateNewRid(driver);
			fetchAndPersistUin(rid);
		}
	}

	// Valid only if the RID resolves to a UIN: introducer flows need a confirmed UIN
	public static boolean isRidValidForEnvironment(String rid) {
		try {
			String token = new KernelAuthentication().getTokenByRole("idrepo");
			String uin = fetchUinByRid(rid, token);
			return uin != null && !uin.isEmpty();
		} catch (Exception e) {
			logger.warn("Could not verify existing RID [" + mask(rid) + "], treating it as invalid: " + e.getMessage());
			return false;
		}
	}

	public static String fetchAndPersistUin(String rid) {
		int maxRetries = Integer.parseInt(BaseTestCase.props.getProperty("uinGenMaxLoopCount", "20"));
		long delayMillis = Long.parseLong(BaseTestCase.props.getProperty("uinGenDelayTime", "10000"));

		String uin = null;
		for (int attempt = 1; attempt <= maxRetries && (uin == null || uin.isEmpty()); attempt++) {
			String token = new KernelAuthentication().getTokenByRole("idrepo");
			uin = fetchUinByRid(rid, token);
			if ((uin == null || uin.isEmpty()) && attempt < maxRetries) {
				logger.warn("UIN not yet generated for RID [" + mask(rid) + "], attempt " + attempt + "/" + maxRetries);
				try {
					Thread.sleep(delayMillis);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}

		if (uin == null || uin.isEmpty()) {
			throw new IllegalStateException(
					"UIN not generated for RID [" + mask(rid) + "] after " + maxRetries + " retries");
		}

		TestDataReader.saveData("AID", rid);
		TestDataReader.saveData("UIN", uin);
		logger.info("Persisted fresh UIN [" + mask(uin) + "] and RID [" + mask(rid) + "] to testdata.json");

		return uin;
	}

	private static String fetchUinByRid(String rid, String token) {
		try {
			Response response = RestClient.getRequestWithCookie(
					BaseTestCase.ApplnURI + BaseTestCase.props.getProperty("retrieveIdByUin") + rid,
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, BaseTestCase.COOKIENAME, token);

			JSONObject responseJson = new JSONObject(response.asString());
			JSONArray errors = responseJson.optJSONArray("errors");
			if ((errors != null && errors.length() > 0) || responseJson.isNull("response")) {
				return null;
			}

			JSONObject identity = responseJson.getJSONObject("response").optJSONObject("identity");
			return identity == null ? null : identity.optString("UIN", null);
		} catch (Exception e) {
			logger.error("Error fetching UIN for RID [" + mask(rid) + "]: " + e.getMessage());
			return null;
		}
	}

	// Show only the last 4 characters of a RID/UIN in logs
	private static String mask(String value) {
		if (value == null || value.length() <= 4) {
			return "****";
		}
		return "*".repeat(value.length() - 4) + value.substring(value.length() - 4);
	}
}
