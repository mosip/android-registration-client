package regclient.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
        try {
            Object obj = parser.parse(new FileReader(getTestDataPath()));
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
        JSONParser parser = new JSONParser();
        String filePath = getTestDataPath();
        try {
            Object obj = parser.parse(new FileReader(filePath));
            JSONObject jsonObject = (JSONObject) obj;
            jsonObject.put(key, value);
            try (FileWriter fw = new FileWriter(filePath)) {
                fw.write(jsonObject.toJSONString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static String getTestDataPath() {
        return TestRunner.getResourcePath() + "/testdata.json";
    }
}