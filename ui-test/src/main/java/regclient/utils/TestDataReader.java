package regclient.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TestDataReader {

	private static final Object SAVE_LOCK = new Object();

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

	@SuppressWarnings("unchecked")
	public static void saveData(String key, String value) {
		saveData(Collections.singletonMap(key, value));
	}

	public static void saveData(Map<String, String> values) {

		String filePath = getTestDataPath();
		Path target = Paths.get(filePath);
		Path lockFile = Paths.get(filePath + ".lock");

		synchronized (SAVE_LOCK) {
			try (FileChannel channel = FileChannel.open(lockFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
					FileLock lock = channel.lock()) {
				mergeAndReplace(target, values);
			} catch (IOException | ParseException e) {
				throw new IllegalStateException("Failed to persist keys " + values.keySet() + " to file: " + filePath,
						e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void mergeAndReplace(Path target, Map<String, String> values) throws IOException, ParseException {

			JSONObject jsonObject;

			try (FileReader reader = new FileReader(target.toFile())) {
			jsonObject = (JSONObject) new JSONParser().parse(reader);
		}

		jsonObject.putAll(values);

		Path temp = Files.createTempFile(target.toAbsolutePath().getParent(), "testdata", ".tmp");
		try {
			try (FileWriter fw = new FileWriter(temp.toFile())) {
				fw.write(jsonObject.toJSONString());
			}
			try {
				Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
			} catch (AtomicMoveNotSupportedException e) {
				Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
			}
		} finally {
			Files.deleteIfExists(temp);
		}
	}

	private static String getTestDataPath() {
		return TestRunner.getResourcePath() + "/testdata.json";
	}
}