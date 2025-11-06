# Global Config Settings

## Background
Upon successful login to the Android Registration Client (ARC), authorized users (Supervisors) should be able to view and manage global configurations in a single Global Config Settings screen. This feature allows administrators to review, verify, and update system-wide settings. The feature displays server values fetched from masterdata and allows supervisors to override these values locally on the device. Local configuration changes apply only to the current device and do not affect server-side configurations or other devices. The system validates changes before saving.

## Target Users
* Registration Supervisor

## Key Requirements
1. The Android Registration Client should be installed and running.
2. User must be logged in using valid and active credentials.
3. User must have the required role (REGISTRATION_SUPERVISOR) to access and edit Global Config Settings.
4. Server values must be fetched from masterdata and displayed as read-only.
5. The Global Config Settings option must be configured in the settings-schema.
6. Only configurations listed in the permitted configuration keys list can be edited.
7. Local configuration changes apply only to the current device.

## Configuration

### Settings-Schema Configuration

The Global Config Settings feature must be configured in the `settings-schema` with the following JSON structure:

```json
{
  "name": "GlobalConfigSettings",
  "label": {
    "eng": "Global Configuration Settings",
    "ar": "إعدادات التكوين العامة",
    "fr": "Paramètres de configuration globaux",
    "hi": "ग्लोबल कॉन्फ़िगरेशन सेटिंग्स"
  },
  "description": {
    "eng": "View and manage global configurations",
    "ar": "عرض وإدارة التكوينات العامة",
    "fr": "Afficher et gérer les configurations globales",
    "hi": "ग्लोबल कॉन्फ़िगरेशन देखें और प्रबंधित करें"
  },
  "fxml": "GlobalConfigSettingsController",
  "access-control": [
    "REGISTRATION_SUPERVISOR"
  ],
  "order": "2"
}
```

### Required Roles

The Global Config Settings feature requires users to have the following role:

* **REGISTRATION_SUPERVISOR** - Registration Supervisor role

Only users with the REGISTRATION_SUPERVISOR role can:
- Access the Global Config Settings screen
- Edit local configuration values
- Submit and save configuration changes

Users without this role will not see the Global Config Settings option in the Settings screen. The access control is enforced at the Settings screen level, where only tabs matching the user's roles are displayed.

## Non-Functional Requirements
* Access control on this page is controlled via the settings-schema.
* Server values must be fetched from masterdata and displayed as read-only.
* Local values are device-specific and do not affect server configurations or other devices.
* The Submit button must only be enabled when there are changes to local values.
* The system must validate input format and value where applicable.
* The layout must be responsive and usable on all supported devices and screen sizes (portrait and landscape).
* Configuration changes require app restart to take effect.

## Solution
1. Configuration:
   * Global Config Settings option is configured in the settings-schema with controller name "GlobalConfigSettingsController".
   * Access control is defined in the settings-schema with role: REGISTRATION_SUPERVISOR.
   * Settings screen filters tabs based on user roles from the settings-schema.
   * Permitted configuration keys list determines which configurations can be edited.
2. Flow Controllers:
   * SettingsScreen displays available settings tabs based on user role.
   * GlobalConfigSettingsTab displays the list of global configurations with server and local values.
   * GlobalConfigSettingsApi handles fetching server values, local configurations, and permitted configuration names.
3. Data Loading:
   * Server values are fetched from masterdata via getRegistrationParams().
   * Local configurations are loaded from local storage via getLocalConfigurations().
   * Permitted configuration names are loaded to determine which configurations are editable.
   * All three data sources are loaded in parallel for optimal performance.
4. Configuration Display:
   * Each configuration displays: Key (name), Server Value (read-only), and Local Value (editable for permitted configs).
   * Modified configurations are visually indicated.
5. Editing Local Values:
   * Only configurations in the permitted configuration keys list can be edited.
   * Server values are always read-only.
   * Local values can be updated and are tracked in the localValues map.
   * Changes are validated before saving.
6. Submit Changes:
   * Submit button is enabled only when there are changes to local values.
   * On submit, confirmation dialog is shown with count of configurations to be updated.
   * Changes are saved to local storage via modifyConfigurations().
   * Success message is displayed and app is automatically restarted to apply changes.
7. Error Handling:
   * If save fails, error message is displayed to the user.
   * If no changes are detected, appropriate message is shown.

## Sequence Diagram
![GlobalConfigSettingsUserFlow.png](../GlobalConfigSettingsUserFlow.png)

