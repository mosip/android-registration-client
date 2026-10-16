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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import regclient.api.BaseTestCase;

public class TestDataReader {

	private static final Object SAVE_LOCK = new Object();

	// UINs/AID are environment specific, so they live in Kernel.properties instead of testdata.json
	private static final Set<String> KERNEL_KEYS = Set.of("UIN", "AID", "UINminor", "UINinfant");

	public static String readData(String key) {
		if (KERNEL_KEYS.contains(key)) {
			String val = BaseTestCase.propsKernel.getProperty(key);
			return (val == null || val.trim().isEmpty()) ? null : val.trim();
		}
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

		Map<String, String> kernelValues = new HashMap<>();
		Map<String, String> jsonValues = new HashMap<>();
		values.forEach((k, v) -> (KERNEL_KEYS.contains(k) ? kernelValues : jsonValues).put(k, v));

		if (!kernelValues.isEmpty()) {
			saveKernelData(kernelValues);
		}
		if (!jsonValues.isEmpty()) {
			saveJsonData(jsonValues);
		}
	}

	private static void saveJsonData(Map<String, String> values) {

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

	// Updates the key lines in place so the rest of Kernel.properties (comments, order) is untouched
	private static void saveKernelData(Map<String, String> values) {

		Path target = Paths.get(getKernelPropertiesPath());

		synchronized (SAVE_LOCK) {
			try {
				String content = new String(Files.readAllBytes(target), StandardCharsets.UTF_8);
				String lineSeparator = content.contains("\r\n") ? "\r\n" : "\n";
				List<String> lines = new ArrayList<>(Arrays.asList(content.split("\\r?\\n", -1)));

				for (Map.Entry<String, String> entry : values.entrySet()) {
					String newLine = entry.getKey() + "=" + entry.getValue();
					boolean replaced = false;
					for (int i = 0; i < lines.size(); i++) {
						if (lines.get(i).matches("\\s*" + Pattern.quote(entry.getKey()) + "\\s*[=:].*")) {
							lines.set(i, newLine);
							replaced = true;
							break;
						}
					}
					if (!replaced) {
						lines.add(newLine);
					}
					BaseTestCase.propsKernel.setProperty(entry.getKey(), entry.getValue());
				}

				Files.write(target, String.join(lineSeparator, lines).getBytes(StandardCharsets.UTF_8));
			} catch (IOException e) {
				throw new IllegalStateException("Failed to persist keys " + values.keySet() + " to file: " + target, e);
			}
		}
	}

	private static String getKernelPropertiesPath() {
		return TestRunner.getResourcePath() + "/config/Kernel.properties";
	}

	private static String getTestDataPath() {
		return TestRunner.getResourcePath() + "/testdata.json";
	}
}