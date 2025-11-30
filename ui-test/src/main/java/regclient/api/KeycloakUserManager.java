package regclient.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.ws.rs.core.Response;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import regclient.utils.TestRunner;

public class KeycloakUserManager {

	public static String moduleSpecificUser = null;
	public static String onboardUser = getDateTime();
	public static String onlyOperatorRoleUser = null;
	public static String onboardingUser = null;

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(KeycloakUserManager.class);

	public static Properties propsKernel = getproperty(TestRunner.getResourcePath() + "/config/Kernel.properties");

	private static Keycloak getKeycloakInstance() {
		Keycloak key = null;
		try {

			key = KeycloakBuilder.builder().serverUrl(ArcConfigManager.getIAMUrl())
					.realm(ArcConfigManager.getIAMRealmId()).grantType(OAuth2Constants.CLIENT_CREDENTIALS)
					.clientId(ArcConfigManager.getAutomationClientId())
					.clientSecret(ArcConfigManager.getAutomationClientSecret()).build();
			logger.debug("Connecting to IAM at {}", ArcConfigManager.getIAMUrl());
		} catch (Exception e) {
			throw e;

		}
		return key;
	}

	public static Properties getproperty(String path) {
		Properties prop = new Properties();
		try {
			File file = new File(path);
			prop.load(new FileInputStream(file));
		} catch (IOException e) {
			logger.error("Exception " + e.getMessage());
		}
		return prop;
	}

	public static void createUsers() {
		List<String> needsToBeCreatedUsers = List.of(ArcConfigManager.getIAMUsersToCreate().split(","));
		Keycloak keycloakInstance = getKeycloakInstance();
		for (String needsToBeCreatedUser : needsToBeCreatedUsers) {
			UserRepresentation user = new UserRepresentation();

			if (needsToBeCreatedUser.equals("globaladmin")) {
				moduleSpecificUser = needsToBeCreatedUser;
			} else if (needsToBeCreatedUser.equals("masterdata-220005")) {
				moduleSpecificUser = needsToBeCreatedUser;
			} else {
				moduleSpecificUser = BaseTestCase.currentModule + "-" + needsToBeCreatedUser;
			}
			logger.info(moduleSpecificUser);
			user.setEnabled(true);
			user.setUsername(moduleSpecificUser);
			user.setFirstName(moduleSpecificUser);
			user.setLastName(moduleSpecificUser);
			user.setEmail("automation" + moduleSpecificUser + "@automationlabs.com");
			// Get realm
			RealmResource realmResource = keycloakInstance.realm(ArcConfigManager.getIAMRealmId());
			UsersResource usersRessource = realmResource.users();
			// Create user (requires manage-users role)
			Response response = null;
			response = usersRessource.create(user);
			logger.info("Response: {} {}", response.getStatus(), response.getStatusInfo());
			if (response.getStatus() == 409) {
				continue;
			}
			String userId = CreatedResponseUtil.getCreatedId(response);
			logger.info("User created with userId: %s%n" + userId);

			// Define password credential
			CredentialRepresentation passwordCred = new CredentialRepresentation();

			passwordCred.setTemporary(false);
			passwordCred.setType(CredentialRepresentation.PASSWORD);

			// passwordCred.setValue(userPassword.get(passwordIndex));
			passwordCred.setValue(ArcConfigManager.getIAMUsersPassword());

			UserResource userResource = usersRessource.get(userId);

			// Set password credential
			userResource.resetPassword(passwordCred);

			// Getting all the roles
			List<RoleRepresentation> allRoles = realmResource.roles().list();
			List<RoleRepresentation> availableRoles = new ArrayList<>();
			List<String> toBeAssignedRoles = List.of(ArcConfigManager.getRolesForUser().split(","));
			for (String role : toBeAssignedRoles) {
				if (allRoles.stream().anyMatch(r -> r.getName().equalsIgnoreCase(role))) {
					availableRoles
							.add(allRoles.stream().filter(r -> r.getName().equalsIgnoreCase(role)).findFirst().get());
				}
			}
			// Assign realm role tester to user
			userResource.roles().realmLevel() //
					.add((availableRoles.isEmpty() ? allRoles : availableRoles));

			// passwordIndex ++;
		}
	}

	public static void createUsersWithOutDefaultRole() {
		Keycloak keycloakInstance = getKeycloakInstance();
		UserRepresentation user = new UserRepresentation();
		logger.info(onboardUser);
		user.setEnabled(true);
		user.setUsername(onboardUser);
		user.setFirstName(onboardUser);
		user.setLastName(onboardUser);
		user.setEmail("automation" + onboardUser + "@automationlabs.com");
		RealmResource realmResource = keycloakInstance.realm(ArcConfigManager.getIAMRealmId());
		UsersResource usersRessource = realmResource.users();
		Response response = null;
		response = usersRessource.create(user);
		logger.info("Response: %s %s%n" + response.getStatus() + response.getStatusInfo());

		String userId = CreatedResponseUtil.getCreatedId(response);
		logger.info("User created with userId: %s%n" + userId);

		CredentialRepresentation passwordCred = new CredentialRepresentation();

		passwordCred.setTemporary(false);
		passwordCred.setType(CredentialRepresentation.PASSWORD);

		passwordCred.setValue(ArcConfigManager.getIAMUsersPassword());

		UserResource userResource = usersRessource.get(userId);

		userResource.resetPassword(passwordCred);

		List<RoleRepresentation> allRoles = realmResource.roles().list();
		List<RoleRepresentation> availableRoles = new ArrayList<>();
		List<String> toBeAssignedRoles = List.of(ArcConfigManager.getRolesForUser().split(","));
		for (String role : toBeAssignedRoles) {
			if (!role.equalsIgnoreCase("Default")) {
				if (allRoles.stream().anyMatch((r -> r.getName().equalsIgnoreCase(role)))) {
					availableRoles.add(allRoles.stream().filter(r -> r.getName().equals(role)).findFirst().get());
				} else {
					logger.info("Role not found in keycloak: %s%n" + role);
				}
			}
			userResource.roles().realmLevel() //
					.add((availableRoles.isEmpty() ? allRoles : availableRoles));

		}
	}

	public static void createOnboardingUser() {
		List<String> needsToBeCreatedUsers = List.of(ArcConfigManager.getIAMUsersToCreateOnboarder().split(","));
		Keycloak keycloakInstance = getKeycloakInstance();
		for (String needsToBeCreatedUser : needsToBeCreatedUsers) {
			UserRepresentation user = new UserRepresentation();

			if (needsToBeCreatedUser.equals("globaladmin")) {
				onboardingUser = needsToBeCreatedUser;
			} else if (needsToBeCreatedUser.equals("masterdata-220005")) {
				onboardingUser = needsToBeCreatedUser;
			} else {
				onboardingUser = BaseTestCase.currentModule + "-" + needsToBeCreatedUser;
			}
			logger.info(onboardingUser);
			user.setEnabled(true);
			user.setUsername(onboardingUser);
			user.setFirstName(onboardingUser);
			user.setLastName(onboardingUser);
			user.setEmail("automation" + onboardingUser + "@automationlabs.com");
			// Get realm
			RealmResource realmResource = keycloakInstance.realm(ArcConfigManager.getIAMRealmId());
			UsersResource usersRessource = realmResource.users();
			// Create user (requires manage-users role)
			Response response = null;
			response = usersRessource.create(user);
			logger.info("Response: {} {}", response.getStatus(), response.getStatusInfo());
			if (response.getStatus() == 409) {
				continue;
			}
			String userId = CreatedResponseUtil.getCreatedId(response);
			logger.info("User created with userId: %s%n" + userId);

			// Define password credential
			CredentialRepresentation passwordCred = new CredentialRepresentation();

			passwordCred.setTemporary(false);
			passwordCred.setType(CredentialRepresentation.PASSWORD);

			// passwordCred.setValue(userPassword.get(passwordIndex));
			passwordCred.setValue(ArcConfigManager.getIAMUsersPassword());

			UserResource userResource = usersRessource.get(userId);

			// Set password credential
			userResource.resetPassword(passwordCred);

			// Getting all the roles
			List<RoleRepresentation> allRoles = realmResource.roles().list();
			List<RoleRepresentation> availableRoles = new ArrayList<>();
			List<String> toBeAssignedRoles = List.of(ArcConfigManager.getRolesForOnboardUser().split(","));
			for (String role : toBeAssignedRoles) {
				if (!role.equalsIgnoreCase("Default")) {
				if (allRoles.stream().anyMatch(r -> r.getName().equalsIgnoreCase(role))) {
					availableRoles
							.add(allRoles.stream().filter(r -> r.getName().equalsIgnoreCase(role)).findFirst().get());
				}
				}
			}
			// Assign realm role tester to user
			userResource.roles().realmLevel() //
					.add((availableRoles.isEmpty() ? allRoles : availableRoles));

			// passwordIndex ++;
		}
	}

	public static void createUsersWithOutSupervisorRole() {
		List<String> needsToBeCreatedUsers = List.of(ArcConfigManager.getIAMUsersToCreateOperator().split(","));
		Keycloak keycloakInstance = getKeycloakInstance();
		for (String needsToBeCreatedUser : needsToBeCreatedUsers) {
			UserRepresentation user = new UserRepresentation();

			if (needsToBeCreatedUser.equals("globaladmin")) {
				onlyOperatorRoleUser = needsToBeCreatedUser;
			} else if (needsToBeCreatedUser.equals("masterdata-220005")) {
				onlyOperatorRoleUser = needsToBeCreatedUser;
			} else {
				onlyOperatorRoleUser = BaseTestCase.currentModule + "-" + needsToBeCreatedUser;
			}
			logger.info(onlyOperatorRoleUser);
			user.setEnabled(true);
			user.setUsername(onlyOperatorRoleUser);
			user.setFirstName(onlyOperatorRoleUser);
			user.setLastName(onlyOperatorRoleUser);
			user.setEmail("automation" + onlyOperatorRoleUser + "@automationlabs.com");
			// Get realm
			RealmResource realmResource = keycloakInstance.realm(ArcConfigManager.getIAMRealmId());
			UsersResource usersRessource = realmResource.users();
			// Create user (requires manage-users role)
			Response response = null;
			response = usersRessource.create(user);
			logger.info("Response: {} {}", response.getStatus(), response.getStatusInfo());
			if (response.getStatus() == 409) {
				continue;
			}
			String userId = CreatedResponseUtil.getCreatedId(response);
			logger.info("User created with userId: %s%n" + userId);

			// Define password credential
			CredentialRepresentation passwordCred = new CredentialRepresentation();

			passwordCred.setTemporary(false);
			passwordCred.setType(CredentialRepresentation.PASSWORD);

			// passwordCred.setValue(userPassword.get(passwordIndex));
			passwordCred.setValue(ArcConfigManager.getIAMUsersPassword());

			UserResource userResource = usersRessource.get(userId);

			// Set password credential
			userResource.resetPassword(passwordCred);

			// Getting all the roles
			List<RoleRepresentation> allRoles = realmResource.roles().list();
			List<RoleRepresentation> availableRoles = new ArrayList<>();
			List<String> toBeAssignedRoles = List.of(ArcConfigManager.getRolesForOperatorUser().split(","));
			for (String role : toBeAssignedRoles) {
				if (allRoles.stream().anyMatch(r -> r.getName().equalsIgnoreCase(role))) {
					availableRoles
							.add(allRoles.stream().filter(r -> r.getName().equalsIgnoreCase(role)).findFirst().get());
				}
			}
			userResource.roles().realmLevel() //
					.add((availableRoles.isEmpty() ? allRoles : availableRoles));

		}
	}

	public static String getDateTime() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String formattedDateTime = currentDateTime.format(formatter);
		return formattedDateTime;
	}

	public static String invalidUsername() {
		int randomNum = new Random().nextInt(900) + 100; // 100–999
		String base = (moduleSpecificUser == null || moduleSpecificUser.isBlank()) ? "invalid-user"
				: moduleSpecificUser;
		return base + "-" + randomNum;
	}

}
