package regclient.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import io.mosip.testrig.apirig.utils.ConfigManager;

public class ArcConfigManager extends io.mosip.testrig.apirig.utils.ConfigManager {

	private static final Logger LOGGER = Logger.getLogger(ArcConfigManager.class);

	public static void init() {
		Logger configManagerLogger = Logger.getLogger(ConfigManager.class);
		configManagerLogger.setLevel(Level.WARN);

		Map<String, Object> moduleSpecificPropertiesMap = new HashMap<>();
		// Load scope specific properties
		try {
			Properties configProps = new Properties();
			try (InputStream inputStream = ArcConfigManager.class.getClassLoader()
					.getResourceAsStream("config.properties")) {
				if (inputStream == null) {
					LOGGER.error("config.properties resource not found in classpath");
					throw new FileNotFoundException("config.properties not found");
				}
				configProps.load(inputStream);
				LOGGER.info("Config properties loaded successfully.");
			} catch (IOException e) {
				LOGGER.error("Failed to load config.properties", e);
				throw new RuntimeException("Failed to load config.properties file", e);
			}

			// Convert Properties to Map and add to moduleSpecificPropertiesMap
			for (String key : configProps.stringPropertyNames()) {
				moduleSpecificPropertiesMap.put(key, configProps.getProperty(key));
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		// Add module specific properties as well.
		init(moduleSpecificPropertiesMap);
	}

	public static String getDbUrl() {
		return getProperty("db-server-es", "");
	}

	public static String getDbUser() {
		return getProperty("db-su-user", "");
	}

	public static String getDbPassword() {
		return getProperty("postgres-password", "");
	}

	public static String getDbSchema() {
		return getProperty("es_db_schema", "");
	}
	
	public static String getIAMUsersToCreateOperator() {
		return getproperty("iam-users-to-create-operator");
	}
	
	public static String getIAMUsersToCreateOnboarder() {
		return getproperty("iam-users-to-create-onboarder");
	}
	
	public static String getiam_apienvuser() {
		return getProperty("apiEnvUser", "");
	}

	public static String getiam_apiinternalendpoint() {
		return getProperty("apiInternalEndPoint", "");
	}

	public static String getProperty(String key, String defaultValue) {
		String value = propertiesMap.get(key) == null ? "" : propertiesMap.get(key).toString();
		return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
	}

	public static int getTimeout() {
		try {
			return Integer.parseInt(getProperty("explicitWaitTimeout", "10"));
		} catch (NumberFormatException e) {
			LOGGER.error("Invalid explicitWaitTimeout value in config.properties. Using default 10 seconds.");
			return 10;
		}
	}

	public static String getIAMUrl() {
		return getProperty("keycloak-external-url", "") + "/auth";
	}

	public static String getSignupPortalUrl() {
		return getProperty("signup.portal.url", "");
	}

	public static String getIAMUsersPassword() {
		return getProperty("iam-users-password", "");
	}

	public static String getEnv() {
		return getProperty("db-server", "");
	}

	public static String getSmtpUrl() {
		return getProperty("smtp.url", "");
	}

	public static String getHealthPortalUrl() {
		return getProperty("baseurl", "");
	}

	public static String gettestcases() {
		return getProperty("regclientScenariosToExecute", "");
	}
	
	public static String getRolesForOperatorUser() { 
		return getproperty("rolesForOperator");
	}
	
	public static String getRolesForOnboardUser() { 
		return getproperty("rolesForOnboarder");
	}
}