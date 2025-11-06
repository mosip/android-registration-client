# Device Settings

## Background
Upon successful login to the Android Registration Client (ARC), authorized users (Supervisors and Officers) should be able to view and monitor all devices/peripherals connected to the tablet. This feature provides real-time device status information, enabling administrators to verify device connections, troubleshoot connectivity issues, and ensure proper device configuration for registration operations. The feature supports manual device scanning to detect newly connected devices and displays a comprehensive view of all connected biometric devices including scanners, printers, and other peripheral devices.

## Target Users
* Registration Supervisor
* Registration Officer

## Key Requirements
1. The Android Registration Client should be installed and running.
2. User must be logged in using valid and active credentials.
3. User must have the required role (REGISTRATION_SUPERVISOR or REGISTRATION_OFFICER) to access Device Settings.
4. Devices must be SBI-compliant and properly connected to the tablet.
5. The Device Settings option must be configured in the settings-schema.

## Configuration

### Settings-Schema Configuration

The Device Settings feature must be configured in the `settings-schema` with the following JSON structure:

```json
{
  "name": "DeviceSettings",
  "label": {
    "eng": "Device Settings",
    "ar": "إعدادات الجهاز",
    "fr": "Paramètres de l'appareil",
    "hi": "डिवाइस सेटिंग्स"
  },
  "description": {
    "eng": "View and manage connected devices",
    "ar": "عرض وإدارة الأجهزة المتصلة",
    "fr": "Afficher et gérer les appareils connectés",
    "hi": "कनेक्टेड उपकरण देखें और प्रबंधित करें"
  },
  "fxml": "DeviceSettingsController",
  "access-control": [
    "REGISTRATION_SUPERVISOR",
    "REGISTRATION_OFFICER"
  ],
  "order": "3"
}
```

### Required Roles

The Device Settings feature requires users to have one of the following roles:

* **REGISTRATION_SUPERVISOR** - Registration Supervisor role
* **REGISTRATION_OFFICER** - Registration Officer role

Users without these roles will not see the Device Settings option in the Settings screen. The access control is enforced at the Settings screen level, where only tabs matching the user's roles are displayed.

## Non-Functional Requirements
* Access control on this page is controlled via the settings-schema.
* The system must retrieve and display real-time status of connected devices/peripherals.
* The layout must be responsive and usable on all supported devices and screen sizes (portrait and landscape).

## Solution
1. Configuration:
   * Device Settings option is configured in the settings-schema with controller name "DeviceSettingsController".
   * Access control is defined in the settings-schema with roles: REGISTRATION_SUPERVISOR and REGISTRATION_OFFICER.
   * Settings screen filters tabs based on user roles from the settings-schema.
2. Flow Controllers:
   * SettingsScreen displays available settings tabs based on user role.
   * DeviceSettingsTab displays the list of connected devices.
   * BiometricsService handles device discovery across modalities (Face, Iris, Thumbs).
3. Device Discovery:
   * System scans for devices using Secure Biometric Interface (SBI) protocol.
   * Scans across three biometric modalities: Face, Iris, and Thumbs.
   * Uses Android Intent-based communication with SBI-compliant device applications.
   * Device information (Device Name, Device ID, Connection Status) is displayed in a grid layout.
4. Manual Refresh:
   * "Scan Now" button allows users to manually trigger device discovery.
   * Device list refreshes automatically based on scan results.
5. Error Handling:
   * If no devices are detected, displays "No devices found" message.
   * If scan fails, displays "Error loading device details" message.

## Sequence Diagram
![DeviceSettingsUserFlow.png](../DeviceSettingsUserFlow.png)