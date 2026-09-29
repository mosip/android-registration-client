package regclient.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TestDataReader {

	public static String readData(String key) {
		return getValueFromJson(key);
	}

	public static String readData(String key, String defaultValue) {
		try {
			String val = getValueFromJson(key);
			return (val == null || val.trim().isEmpty()) ? defaultValue : val;
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static String getValueFromJson(String key) {

		JSONParser parser = new JSONParser();

		try (FileReader reader = new FileReader(getTestDataPath())) {

			Object obj = parser.parse(reader);
			JSONObject jsonObject = (JSONObject) obj;

			return (String) jsonObject.get(key);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void saveData(String key, String value) {
		saveData(Collections.singletonMap(key, value));
	}

	// Saves all keys in one write. The JSON goes to a temp file that then replaces
	// testdata.json, so the file is never left half-written or with only some keys updated.
	@SuppressWarnings("unchecked")
	public static void saveData(Map<String, String> values) {

		JSONParser parser = new JSONParser();
		String filePath = getTestDataPath();

		try {

			JSONObject jsonObject;

			try (FileReader reader = new FileReader(filePath)) {

				Object obj = parser.parse(reader);
				jsonObject = (JSONObject) obj;
			}

			jsonObject.putAll(values);

			Path target = Paths.get(filePath);
			Path temp = Paths.get(filePath + ".tmp");
			try (FileWriter fw = new FileWriter(temp.toFile())) {
				fw.write(jsonObject.toJSONString());
			}
			try {
				Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
			} catch (AtomicMoveNotSupportedException e) {
				Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
			}

		} catch (IOException | ParseException e) {
			throw new IllegalStateException("Failed to persist keys " + values.keySet() + " to file: " + filePath, e);
		}
	}

	private static String getTestDataPath() {
		return TestRunner.getResourcePath() + "/testdata.json";
	}
}