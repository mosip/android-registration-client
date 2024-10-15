package regclient.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.response.Response;
import regclient.utils.TestDataReader;

public class FetchUiSpec extends BaseTestCase{

	public static String UiSpec;
	public static String eye = "no";
	public static String rightHand = "no";
	public static String leftHand = "no";
	public static String thumb = "no";
	public static String face = "no";

	public static void getUiSpec(String type) {
		if(type.equals("newProcess")) {
			String token = kernelAuthLib.getTokenByRole("globalAdmin");
			String url = ApplnURI + "/v1/masterdata/uispec/registration-client/latest";
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("type", type);
			Response response = RestClient.getRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, "Authorization", token);
			UiSpec= response.asString();
		}else if (type.equals("updateProcess")) {
			String token = kernelAuthLib.getTokenByRole("globalAdmin");
			String url = ApplnURI + "/v1/masterdata/uispec/registration-client/latest";
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("type", type);
			Response response = RestClient.getRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, "Authorization", token);
			UiSpec= response.asString();
		}
	}

	public static List<String> getAllScreenOrder() {
		  List<String> screenNames = new ArrayList<>();

	        JSONObject jsonObject = new JSONObject(UiSpec);
	        JSONArray responseArray = jsonObject.getJSONArray("response");

	        JSONArray jsonSpecArray = responseArray.getJSONObject(0).getJSONArray("jsonSpec");
	        JSONObject specObject = jsonSpecArray.getJSONObject(0).getJSONObject("spec");
	        JSONArray screensArray = specObject.getJSONArray("screens");

	        for (int i = 0; i < screensArray.length(); i++) {
	            JSONObject screen = screensArray.getJSONObject(i);
	            String name = screen.getString("name");
	            screenNames.add(name);
	        }

	        return screenNames;
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

	public static String getControlTypeUsingId(String Id) { 
		String controlType=null;
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
						controlType = fieldNode.path("controlType").asText();
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return controlType;

	}

	public static boolean getRequiredTypeUsingId(String Id) { 
		boolean controlType = false;
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
						controlType = fieldNode.path("required").asBoolean();
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return controlType;

	}
	
	public static String getRequiredGroupName(String Id) { 
		String group = null;
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
						group = fieldNode.path("group").asText();
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return group;

	}

	public static boolean getTransliterateTypeUsingId(String Id) { 
		boolean controlType = false;
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
						controlType = fieldNode.path("transliterate").asBoolean();
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return controlType;

	}

	public static String getFormatUsingId(String Id) { 
		String controlType=null;
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
						controlType = fieldNode.path("format").asText();
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return controlType;

	}

	public static String getTextBoxUsingId(String Id ) { 
		String validator=null;
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
						JsonNode labelNode = fieldNode.path("validators");
						if (labelNode.isArray()) {
							for (JsonNode validatorNode : labelNode) {
								validator = validatorNode.path("validator").asText();
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return validator;

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
	
	public static String getFlowType() { 
		 String typeValue=null;
		 ObjectMapper mapper = new ObjectMapper();
	        try {
	            JsonNode rootNode = mapper.readTree(UiSpec);
	            JsonNode responseNode = rootNode.path("response");
	            if (responseNode.isArray()) {
	                for (JsonNode node : responseNode) {
	                    JsonNode jsonSpecNode = node.path("jsonSpec");
	                    if (jsonSpecNode.isArray()) {
	                        for (JsonNode specNode : jsonSpecNode) {
	                             typeValue = specNode.path("type").asText();
	                        }
	                    }
	                }
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        };
			return typeValue;
	}

	public static List<String> getAllIds(String page) {
		List<String> idList = new ArrayList<>();

		// Parse the UiSpec JSON string
		JSONObject jsonObject = new JSONObject(UiSpec);
		JSONArray responseArray = jsonObject.getJSONArray("response");

		// Loop through the JSON array to extract all IDs
		for (int i = 0; i < responseArray.length(); i++) {
			JSONObject responseObject = responseArray.getJSONObject(i);
			JSONArray jsonSpecArray = responseObject.getJSONArray("jsonSpec");

			for (int j = 0; j < jsonSpecArray.length(); j++) {
				JSONObject specObject = jsonSpecArray.getJSONObject(j).getJSONObject("spec");
				JSONArray screensArray = specObject.getJSONArray("screens");

				for (int k = 0; k < screensArray.length(); k++) {
					JSONObject screenObject = screensArray.getJSONObject(k);
					String name = screenObject.getString("name");

					// Check if the screen name is "DemographicDetails"
					if (page.equals(name)) {
						JSONArray fieldsArray = screenObject.getJSONArray("fields");

						for (int l = 0; l < fieldsArray.length(); l++) {
							JSONObject fieldObject = fieldsArray.getJSONObject(l);
							String id = fieldObject.getString("id");
							idList.add(id);
						}
					}
				}
			}
		}

		// Return the list of IDs
		return idList;
	}

	public static List<String> getAllGroupLabelUsingId(String page){
		List<String> idList = new ArrayList<>();
		List<String> groupLabelList = new ArrayList<>();
		idList=getAllIds(page);
		for(String id:idList ) {
			if(FetchUiSpec.getRequiredTypeUsingId(id)) {
				String groupLabel =getGroupValueUsingId(id );
				if (groupLabel.equals(null)||groupLabel.equals("")) {
					groupLabel =getRequiredGroupName(id);
				}
				groupLabelList.add(groupLabel);
			}else if(id.equals("residenceStatus")) {
				String groupLabel =getGroupValueUsingId(id );
				groupLabelList.add(groupLabel);
			}
		}
		Set<String> set = new LinkedHashSet<>(groupLabelList);

		groupLabelList.clear();
		groupLabelList.addAll(set);
		return groupLabelList;
	}

	public static void getBiometricDetails(String biometricId) {
        List<String> bioAttributes = new ArrayList<>();

        JSONObject rootObject = new JSONObject(UiSpec);
        JSONArray responseArray = rootObject.getJSONArray("response");

        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject responseObject = responseArray.getJSONObject(i);
            JSONArray jsonSpecArray = responseObject.getJSONArray("jsonSpec");

            for (int j = 0; j < jsonSpecArray.length(); j++) {
                JSONObject specObject = jsonSpecArray.getJSONObject(j);
                if (specObject.getString("type").equals("newProcess")) {
                    JSONObject specDetails = specObject.getJSONObject("spec");
                    JSONArray screensArray = specDetails.getJSONArray("screens");

                    for (int k = 0; k < screensArray.length(); k++) {
                        JSONObject screenObject = screensArray.getJSONObject(k);
                        JSONArray fieldsArray = screenObject.getJSONArray("fields");

                        for (int l = 0; l < fieldsArray.length(); l++) {
                            JSONObject fieldObject = fieldsArray.getJSONObject(l);

                            if (fieldObject.getString("id").equals(biometricId)) {
                                JSONArray bioArray = fieldObject.getJSONArray("bioAttributes");

                                for (int m = 0; m < bioArray.length(); m++) {
                                    bioAttributes.add(bioArray.getString(m));
                                }
                            }
                        }
                    }
                }
            }
        }
        for (String attribute : bioAttributes) {
            switch (attribute) {
                case "leftEye":
                case "rightEye":
                    eye = "yes";
                    break;
                case "rightIndex":
                case "rightLittle":
                case "rightRing":
                case "rightMiddle":
                    rightHand = "yes";
                    break;
                case "leftIndex":
                case "leftLittle":
                case "leftRing":
                case "leftMiddle":
                    leftHand = "yes";
                    break;
                case "leftThumb":
                case "rightThumb":
                    thumb = "yes";
                    break;
                case "face":
                    face = "yes";
                    break;
                default:
                    break;
            }
        }
    }
}
