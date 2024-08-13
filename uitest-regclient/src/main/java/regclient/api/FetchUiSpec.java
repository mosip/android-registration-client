package regclient.api;

import java.io.IOException;
import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.response.Response;
import regclient.utils.TestDataReader;

public class FetchUiSpec extends BaseTestCase{

	public static String UiSpec;

	public static void getUiSpec(String type) {
		if(type.equals("newProcess")) {
			String token = kernelAuthLib.getTokenByRole("globalAdmin");
			String url = ApplnURI + "/v1/masterdata/uispec/registration-client/latest";
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("version","1.100");
			map.put("type", type);
			map.put("idSchemaVersion", "0.2");
			Response response = RestClient.getRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, "Authorization", token);
			UiSpec= response.asString();
		}else if (type.equals("updateProcess")) {
			String token = kernelAuthLib.getTokenByRole("globalAdmin");
			String url = ApplnURI + "/v1/masterdata/uispec/registration-client/latest";
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("version","0.7");
			map.put("type", type);
			map.put("idSchemaVersion", "0.2");
			Response response = RestClient.getRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, "Authorization", token);
			UiSpec= response.asString();
		}
	}

	public static String getScreenTitle(String ScreenName) {
		String screenTitle=null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode rootNode = mapper.readTree(UiSpec);
			JsonNode responseNode = rootNode.path("response").get(0);
			JsonNode screensNode = responseNode.path("jsonSpec").get(0).path("spec").path("screens");

			for (JsonNode screenNode : screensNode) {
				if (ScreenName.equals(screenNode.path("name").asText())) {
					JsonNode labelNode = screenNode.path("label");
					screenTitle = labelNode.path(TestDataReader.readData("language")).asText();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return screenTitle;

	}

	public static String getValueUsingId(String Id ) { 
		String value=null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode rootNode = mapper.readTree(UiSpec);
			JsonNode responseNode = rootNode.path("response").get(0);
			JsonNode screensNode = responseNode.path("jsonSpec").get(0).path("spec").path("screens");

			for (JsonNode screenNode : screensNode) {
				JsonNode fieldsNode = screenNode.path("fields");

				for (JsonNode fieldNode : fieldsNode) {
					String id = fieldNode.path("id").asText();
					if (Id.equals(id)) {
						JsonNode labelNode = fieldNode.path("label");
						value = labelNode.path(TestDataReader.readData("language")).asText();
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;

	}
	
	public static String getGroupValueUsingId(String Id ) { 
		String value=null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode rootNode = mapper.readTree(UiSpec);
			JsonNode responseNode = rootNode.path("response").get(0);
			JsonNode screensNode = responseNode.path("jsonSpec").get(0).path("spec").path("screens");

			for (JsonNode screenNode : screensNode) {
				JsonNode fieldsNode = screenNode.path("fields");

				for (JsonNode fieldNode : fieldsNode) {
					String id = fieldNode.path("id").asText();
					if (Id.equals(id)) {
						JsonNode labelNode = fieldNode.path("groupLabel");
						value = labelNode.path(TestDataReader.readData("language")).asText();
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;

	}

	public static String getTitleUsingId(String Id ) { 
		JSONObject jsonObject = new JSONObject(UiSpec);
		JSONArray responseArray = jsonObject.getJSONArray("response");
		String value=null;
		for (int i = 0; i < responseArray.length(); i++) {
			JSONObject responseObject = responseArray.getJSONObject(i);
			JSONArray jsonSpecArray = responseObject.getJSONArray("jsonSpec");

			for (int j = 0; j < jsonSpecArray.length(); j++) {
				JSONObject specObject = jsonSpecArray.getJSONObject(j).getJSONObject("spec");
				String id = specObject.getString("id");
				value  = specObject.getJSONObject("label").getString(TestDataReader.readData("language"));

				if (id.equals(Id)) {
					return value;
				}
			}
		}
		return value;
	}
}
