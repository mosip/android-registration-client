package regclient.utils;

import java.util.LinkedHashMap;
import java.util.Map;

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
	private static final int LOOKUP_ATTEMPTS = 3;
	private static final long LOOKUP_RETRY_DELAY_MILLIS = 5000;

	private UinRidGenerator() {
	}

	public static void ensureValidIntroducer(AppiumDriver driver) throws InterruptedException {
		String existingRid = TestDataReader.readData("AID");
		String uin = (existingRid == null || existingRid.isBlank()) ? null : lookupUin(existingRid);
		if (uin == null) {
			String rid = RidGenerator.generateNewRid(driver);
			fetchAndPersistUin(rid);
			return;
		}

		if (!uin.equals(TestDataReader.readData("UIN"))) {
			persistIntroducer(existingRid, uin);
			logger.info("Stored UIN did not match AID [" + mask(existingRid) + "], repaired");
		}
	}

	public static boolean isRidValidForEnvironment(String rid) {
		return lookupUin(rid) != null;
	}

	private static String lookupUin(String rid) {
		IllegalStateException lastFailure = null;
		for (int attempt = 1; attempt <= LOOKUP_ATTEMPTS; attempt++) {
			try {
				String token = new KernelAuthentication().getTokenByRole("idrepo");
				String uin = fetchUinByRid(rid, token);
				return (uin == null || uin.isEmpty()) ? null : uin;
			} catch (IllegalStateException e) {
				lastFailure = e;
				logger.warn("IDREPO lookup failed for RID [" + mask(rid) + "], attempt " + attempt + "/"
						+ LOOKUP_ATTEMPTS + ": " + e.getMessage());
				if (attempt < LOOKUP_ATTEMPTS) {
					try {
						Thread.sleep(LOOKUP_RETRY_DELAY_MILLIS);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
						break;
					}
				}
			}
		}
		throw new IllegalStateException("Could not verify introducer RID [" + mask(rid) + "] in IDREPO", lastFailure);
	}

	// AID and UIN are saved in one write so testdata.json never holds a mismatched pair
	private static void persistIntroducer(String rid, String uin) {
		Map<String, String> introducer = new LinkedHashMap<>();
		introducer.put("AID", rid);
		introducer.put("UIN", uin);
		TestDataReader.saveData(introducer);
	}

	public static String fetchAndPersistUin(String rid) {
		int maxRetries = Integer.parseInt(BaseTestCase.props.getProperty("uinGenMaxLoopCount", "20"));
		long delayMillis = Long.parseLong(BaseTestCase.props.getProperty("uinGenDelayTime", "10000"));

		String uin = null;
		for (int attempt = 1; attempt <= maxRetries && (uin == null || uin.isEmpty()); attempt++) {
			try {
				String token = new KernelAuthentication().getTokenByRole("idrepo");
				uin = fetchUinByRid(rid, token);
			} catch (IllegalStateException e) {
				// A failed lookup while polling just means "not available yet"
				logger.warn("IDREPO lookup failed for RID [" + mask(rid) + "]: " + e.getMessage());
				uin = null;
			}
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

		persistIntroducer(rid, uin);
		logger.info("Persisted fresh introducer for RID [" + mask(rid) + "] to testdata.json");

		return uin;
	}

	private static String fetchUinByRid(String rid, String token) {
		Response response;
		JSONObject responseJson;
		try {
			response = RestClient.getRequestWithCookie(
					BaseTestCase.ApplnURI + BaseTestCase.props.getProperty("retrieveIdByUin") + rid,
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, BaseTestCase.COOKIENAME, token);
			responseJson = new JSONObject(response.asString());
		} catch (Exception e) {
			throw new IllegalStateException("IDREPO request failed: " + e.getMessage(), e);
		}

		int status = response.getStatusCode();
		if (status == 401 || status == 403 || status >= 500) {
			throw new IllegalStateException("IDREPO returned HTTP " + status);
		}

		JSONArray errors = responseJson.optJSONArray("errors");
		if (errors != null && errors.length() > 0) {
			for (int i = 0; i < errors.length(); i++) {
				JSONObject error = errors.optJSONObject(i);
				String code = error == null ? "" : error.optString("errorCode", "");
				if (code.startsWith("KER-ATH")) {
					throw new IllegalStateException("IDREPO authentication failed: " + code);
				}
			}
			return null;
		}
		if (responseJson.isNull("response")) {
			return null;
		}

		JSONObject identity = responseJson.getJSONObject("response").optJSONObject("identity");
		return identity == null ? null : identity.optString("UIN", null);
	}

	// Show only the last 4 characters of a RID/UIN in logs
	private static String mask(String value) {
		if (value == null || value.length() <= 4) {
			return "****";
		}
		return "*".repeat(value.length() - 4) + value.substring(value.length() - 4);
	}
}
