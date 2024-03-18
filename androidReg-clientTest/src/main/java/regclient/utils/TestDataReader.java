package regclient.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.File;
import java.io.IOException;

public class TestDataReader {
	public static String readData(String key) {
		return getValueForKey(key);
	}

	//ToDo - Need to remove this once path issue is fixed on device farm
	private static String getValueForKey(String key) {
		switch (key) {
		case "password":
			return "admin123";
		case "username":
			return "9343";
		case "language":
			return "hin";
		case "secondLanguage":
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
		case "nonRegisteredUsername":
			return "123456789";
		case "InvalidPassword":
			return "123456789";
		default:
			return "Key not found";
		}
	}
}
