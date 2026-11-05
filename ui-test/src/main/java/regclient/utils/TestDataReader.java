package regclient.utils;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.testng.SkipException;

import regclient.api.BaseTestCase;

public class TestDataReader {

	private static final Object SAVE_LOCK = new Object();

	public static String readData(String key) {
		String val = BaseTestCase.propsKernel.getProperty(key);
		return (val == null || val.trim().isEmpty()) ? null : val.trim();
	}

	// For values that must be filled in manually (e.g. UINminor/UINinfant): skip the test with a clear message if empty
	public static String requireData(String key) {
		String val = readData(key);
		if (val == null || val.trim().isEmpty()) {
			throw new SkipException(key + " is not set in Kernel.properties; add it manually to run this test");
		}
		return val;
	}

	public static String readData(String key, String defaultValue) {
		try {
			String val = readData(key);
			return (val == null || val.trim().isEmpty()) ? defaultValue : val;
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static void saveData(String key, String value) {
		saveData(Collections.singletonMap(key, value));
	}

	public static void saveData(Map<String, String> values) {
		saveKernelData(values);
	}

	// Updates the key lines in place so the rest of Kernel.properties (comments, order) is untouched
	private static void saveKernelData(Map<String, String> values) {

		Path target = Paths.get(getKernelPropertiesPath());

		synchronized (SAVE_LOCK) {
			try {
				String content = new String(Files.readAllBytes(target), StandardCharsets.ISO_8859_1);
				String lineSeparator = content.contains("\r\n") ? "\r\n" : "\n";
				List<String> lines = new ArrayList<>(Arrays.asList(content.split("\\r?\\n", -1)));

				for (Map.Entry<String, String> entry : values.entrySet()) {
					String newLine = entry.getKey() + "=" + escapeNonLatin1(entry.getValue());
					boolean replaced = false;
					for (int i = 0; i < lines.size(); i++) {
						if (lines.get(i).matches("\\s*" + Pattern.quote(entry.getKey()) + "\\s*[=:].*")) {
							lines.set(i, newLine);
							replaced = true;
						}
					}
					if (!replaced) {
						lines.add(newLine);
					}
				}

				// Write to a temp file and swap it in, so a failed write never leaves a partial Kernel.properties
				Path temp = Files.createTempFile(target.toAbsolutePath().getParent(), "Kernel", ".tmp");
				try {
					Files.write(temp, String.join(lineSeparator, lines).getBytes(StandardCharsets.ISO_8859_1));
					try {
						Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
					} catch (AtomicMoveNotSupportedException e) {
						Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
					}
				} finally {
					Files.deleteIfExists(temp);
				}
				// Only update memory once the file is safely written
				values.forEach(BaseTestCase.propsKernel::setProperty);
			} catch (IOException e) {
				throw new IllegalStateException("Failed to persist keys " + values.keySet() + " to file: " + target, e);
			}
		}
	}

	private static String escapeNonLatin1(String value) {
		StringBuilder escaped = new StringBuilder(value.length());
		for (char c : value.toCharArray()) {
			if (c > 0xFF) {
				escaped.append(String.format("\\u%04x", (int) c));
			} else {
				escaped.append(c);
			}
		}
		return escaped.toString();
	}

	private static String getKernelPropertiesPath() {
		return TestRunner.getResourcePath() + "/config/Kernel.properties";
	}

}