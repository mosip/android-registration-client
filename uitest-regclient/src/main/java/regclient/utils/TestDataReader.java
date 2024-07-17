package regclient.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TestDataReader {
	public static String readData(String key) {
		return getValueFromJson(key);
	}

	//ToDo - Need to remove this once path issue is fixed on device farm
	private static String getValueForKey(String key) {
		switch (key) {
		case "password":
			return "admin123";
		case "username":
			return "9343";
		case "language":
			return "eng";
		case "defaultlanguage":
			return "ara";
		case "notificationLanguage":
			return "eng";
		case "fullname":
			return "abcd";
		case "adultage":
			return "20";
		case "infantAge":
			return "4";
		case "minorAge":
			return "12";
		case "editData":
			return "fghjk";
		case "gender":
			return "female";
		case "address":
			return "abcd";
		case "residenceStatus":
			return "Non-Foreigner";
		case "region":
			return "Rabat Sale Kenitra";
		case "province":
			return "Kenitra";
		case "city":
			return "Kenitra";
		case "mobileNumber":
			return "9876543210";
		case "emailId":
			return "abc@gmail.com";
		case "RID":
			return "10001105671003120240215071549";
		case "nonRegisteredUsername":
			return "123456789";
		case "InvalidPassword":
			return "123456789";
		default:
			return "Key not found";
		}
	}

	public static String getValueFromJson(String value) {
		JSONParser parser = new JSONParser();
		Object obj = null;
		try {
			obj = parser.parse(new FileReader(System.getProperty("user.dir")+"//src//main//resources//testdata.json"));
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		JSONObject jsonObject = (JSONObject) obj;
		return (String) jsonObject.get(value);

	}
}
