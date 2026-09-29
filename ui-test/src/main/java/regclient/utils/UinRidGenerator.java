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

/**
 * Checks whether the RID/UIN already in testdata.json is still valid for the current
 * environment, and (only when it isn't) fetches the UIN generated for a freshly created
 * RID and persists both to testdata.json - so they never need to be updated by hand when
 * tests are pointed at a new environment.
 */
public class UinRidGenerator {

	private static final Logger logger = Logger.getLogger(UinRidGenerator.class);

	private UinRidGenerator() {
	}

	/**
	 * Ensures testdata.json's RID/UIN are valid for the current environment - reusing them if
	 * so, or generating a fresh pair via RidGenerator's registration flow if not. Call this at
	 * the top of any test that needs a valid introducer RID.
	 */
	public static void ensureValidIntroducer(AppiumDriver driver) throws InterruptedException {
		String existingRid = TestDataReader.readData("AID");
		if (existingRid == null || existingRid.isBlank() || !isRidValidForEnvironment(existingRid)) {
			String rid = RidGenerator.generateNewRid(driver);
			fetchAndPersistUin(rid);
		}
	}

	/**
	 * @return true if this RID still resolves to a real identity in the current environment.
	 */
	public static boolean isRidValidForEnvironment(String rid) {
		try {
			String token = new KernelAuthentication().getTokenByRole("idrepo");
			Response response = RestClient.getRequestWithCookie(
					BaseTestCase.ApplnURI + BaseTestCase.props.getProperty("retrieveIdByUin") + rid,
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, BaseTestCase.COOKIENAME, token);

			JSONObject responseJson = new JSONObject(response.asString());
			JSONArray errors = responseJson.optJSONArray("errors");
			return (errors == null || errors.length() == 0) && responseJson.optJSONObject("response") != null;
		} catch (Exception e) {
			logger.warn("Could not verify existing RID [" + rid + "], treating it as invalid: " + e.getMessage());
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
			if (uin == null || uin.isEmpty()) {
				logger.warn("UIN not yet generated for RID [" + rid + "], attempt " + attempt + "/" + maxRetries);
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
					"UIN not generated for RID [" + rid + "] after " + maxRetries + " retries");
		}

		TestDataReader.saveData("AID", rid);
		TestDataReader.saveData("UIN", uin);
		logger.info("Persisted fresh UIN [" + uin + "] and RID [" + rid + "] to testdata.json");

		return uin;
	}

	private static String fetchUinByRid(String rid, String token) {
		try {
			Response response = RestClient.getRequestWithCookie(
					BaseTestCase.ApplnURI + BaseTestCase.props.getProperty("retrieveIdByUin") + rid,
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, BaseTestCase.COOKIENAME, token);

			JSONObject responseJson = new JSONObject(response.asString());
			if (responseJson.isNull("response")) {
				return null;
			}

			JSONObject identity = responseJson.getJSONObject("response").optJSONObject("identity");
			return identity == null ? null : identity.optString("UIN", null);
		} catch (Exception e) {
			logger.error("Error fetching UIN for RID [" + rid + "]: " + e.getMessage());
			return null;
		}
	}
}
