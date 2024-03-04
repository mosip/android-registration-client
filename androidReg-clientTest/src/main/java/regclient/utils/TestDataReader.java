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
			return "1234";
		case "language":
			return "eng";
		case "firstname":
			return "abcd";
		case "lastname":
			return "abcd";
		case "adultage":
			return "20";
		case "gender":
			return "female";
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
