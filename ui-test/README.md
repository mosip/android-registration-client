
# Reg-Client Automation - Mobile Automation Framework using Appium

## Overview
Reg-Client Automation is a mobile automation framework designed for Android platforms. It automates both positive and negative scenarios to ensure comprehensive testing of mobile applications.

## Installation
To set up Appium for use with this framework, please follow the installation instructions provided in the [Appium documentation](https://appium.io/docs/en/about-appium/intro/).

## Build
1. **Build the JAR file**:
   ```bash
   mvn clean package -DskipTests=true
   ```
2. The generated JAR file (`uitest-regclient-0.0.1.jar`) will be located in the `target` directory.
3. **For running tests on a device**: Use the JAR file with dependencies (`uitest-regclient-0.0.1.jar`).

## Configurations

### General Configurations (for both JAR and IDE runs)
1. **Environment Settings**:
   - Update `resources/Config/kernal.properties` to modify environment-specific settings.

2. **Test Data**:
   - Update the following keys in `resources/testdata.json`:
     - `uin`
     - `language`
     - `rid`

3. **General Configurations**:
   - Update `resources/config.properties` with the following values:
     - `nodePath`: Path to the Node.js executable.
     - `appiumServerExecutable`: Path to the Appium server executable.

4. **Desired Capabilities**:
   - Update `resources/DesiredCapabilies.json` with the following keys:
     - `appium:udid`: Unique Device Identifier for the target device.
     - `appium:app`: Path to the application APK.

## Execution

### Running Tests with JAR
1. **Run the JAR file**:
   - Execute the `run_regclient.bat` batch file, which will trigger the test execution.

### Running Tests in IDE
1. **Run Configuration**:
   - Set `regclient.utils.TestRunner` as the main class in your IDE run configuration.
   
2. **Resource File Locations**:
   - `kernal.properties` and `testdata.json` are located under `src/main/resources` for IDE runs, as opposed to `resources` in the JAR run.

## Reports
- After test execution, test reports will be available in the `test-output/emailableReports` directory.
