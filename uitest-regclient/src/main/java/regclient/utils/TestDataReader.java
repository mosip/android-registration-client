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

	public static String getValueFromJson(String value) {
		JSONParser parser = new JSONParser();
		Object obj = null;
		try {
			obj = parser.parse(new FileReader(TestRunner.getResourcePath()+"/testdata.json"));

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
