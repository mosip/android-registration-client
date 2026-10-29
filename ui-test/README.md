# Reg-Client Automation

Mobile Automation Framework for Android using **Appium & TestNG**.

---

## Introduction

Reg-Client Automation is a mobile automation framework for Android platforms.  
It automates **positive and negative scenarios** and supports both **emulators and real devices**.

---

## Build

1. **Build the JAR file**:
   ```bash
   mvn clean package -DskipTests=true
   ```

2. The generated JAR file (`uitest-regclient-1.2.0.jar`) will be located in the `target` directory.

3. **For running tests on a device**:  
   Use the shaded JAR with dependencies:

   ```text
   uitest-regclient-1.2.0.jar
   ```

---

## Prerequisites

Install the following before setup:

* Java JDK **11 or above**
* Maven
* Android Studio (for emulator)
* Node.js (for Appium)
* ADB (comes with Android SDK)
* Eclipse IDE or IntelliJ IDEA (optional)

---

## Appium Setup

1. Install Node.js:  
   https://nodejs.org (choose Windows 64-bit installer)  
   Ensure **Add to PATH** is selected.

2. Install Appium server globally:
   ```bash
   npm install -g appium
   appium -v
   ```

3. Start Appium server:
   ```bash
   appium
   ```

4. (Optional) Install **Appium Desktop (GUI)** and start the server from the app.

---

## Emulator Setup

1. Open **Android Studio → Tools → Device Manager → Create Device**
2. Select a device (e.g., Pixel 5) and API level (30/31)
3. Download system image and finish setup
4. Start emulator from Device Manager

---

## Mock MDS Setup

1. Place `mockmds.apk` in your platform-tools folder:
   ```
   C:\Users\<username>\AppData\Local\Android\Sdk\platform-tools
   ```

2. Verify emulator/device:
   ```bash
   adb devices
   ```

3. Install APK:
   ```bash
   adb install mockmds.apk
   ```

4. Verify installation:
   ```bash
   adb shell pm list packages | findstr mosip
   ```

---

## WireGuard Setup

1. Download WireGuard APK (official site or F-Droid)
2. Place it in `platform-tools`
3. Install:
   ```bash
   adb install com.wireguard.android-1.0.20250531.apk
   ```

4. Launch WireGuard and activate the tunnel if required

---

## Clone & Setup Automation Framework

Fork the repository (**branch: develop**) and clone locally:

```bash
cd ~/Desktop
mkdir arc && cd arc
git clone https://github.com/mosip/android-registration-client.git
```

Import the project into **Eclipse / IntelliJ** if needed.

---

## Run Automation

### From IDE

* Create a **Run Configuration**
* **Main Class**: `regclient.utils.TestRunner`
* Run the configuration

---

### From Packaged JAR

```bash
cd android-registration-client/ui-test/target
java -jar uitest-regclient-1.2.0.jar
```

Alternatively, run the provided batch script (if present):
```
run_regclient.bat
```

### Run Specific Scenarios

Set `regclientScenariosToExecute` in `resources/config/Kernel.properties` to one or more scenario names (comma-separated, no spaces, not case-sensitive). They run in the order listed:

```properties
regclientScenariosToExecute=ResetPassword
regclientScenariosToExecute=LoginTest,NewRegistrationAdult,UpdateMyUinUpdateDocuments
```

* Leave it empty to run the full suite from `testng.xml`.
* Misspelled names are silently skipped; use the class names from `regclient.androidTestCases`.
* On a fresh install or a new environment, run `AddMachineDetails,InitialLaunch,LoginTest` first.
* Some scenarios need test data first; see [UIN / AID Generation Flow](#uin--aid-generation-flow).

---

## Important Configuration Files

Update these files before execution:

* `resources/config/Kernel.properties` — Environment details; also `UIN`, `AID`, `UINminor`, `UINinfant` and `generateUinAidPrerequisite` (true: auto-generate UIN/AID when empty; false: must be filled manually)
* `resources/testdata.json` — Test data (language, camera id)
* `resources/config.properties` — `nodePath`, `appiumServerExecutable`
* `resources/DesiredCapabilities.json` — `udid`, app path
* `camera.java` — Update camera & retake button coordinates [If camera id not available use camera button coordinates] 

📌 Any runtime properties (e.g. `bioValue.properties`) must be placed under:
```
src/main/resources/config
```
(Maven will copy them to `target/classes/config`)

---

## UIN / AID Generation Flow

Several scenarios need an existing identity: the **introducer** for Minor/Infant registrations and the **UIN** used by the Update My UIN scenarios. These are kept in `resources/config/Kernel.properties`:

| Key | Purpose | How it is set |
|---|---|---|
| `AID` | RID of an identity whose UIN is used as the introducer | Generated automatically, or added manually |
| `UIN` | UIN belonging to that `AID` | Generated automatically, or added manually |
| `UINminor`, `UINinfant` | UINs of existing minor/infant identities (UpdateMyUinMinor/Infant) | **Always added manually**; these scenarios are skipped if empty |
| `generateUinAidPrerequisite` | `true`: generate a new `AID`/`UIN` when missing or invalid. `false`: never generate; skip the scenario instead | Set manually |

**Environment dependent:** a UIN/AID exists only in the environment where it was created. Values from another environment (e.g. qa-base vs 131ga) will not work, so clear `UIN`/`AID` (or keep `generateUinAidPrerequisite=true`) when switching environments. `UINminor`/`UINinfant` must also come from the target environment.

### How it works

Before the introducer-dependent scenarios (NewRegistrationMinor, NewRegistrationMinorException, NewRegistrationInfant, NewRegistrationAdultUploadMultipleDoccuments, UpdateMyUinMinor, UpdateMyUinInfant, BiometricCorrection) run, the framework checks the introducer:

1. **`AID` is empty**
   - `generateUinAidPrerequisite=true` → a new adult registration is created through the app (see below) and its `AID`/`UIN` are saved to `Kernel.properties`.
   - `generateUinAidPrerequisite=false` → the scenario is **skipped**.
2. **`AID` is set** → its UIN is looked up in IDREPO of the current environment.
   - Found → used. If `UIN` in `Kernel.properties` does not match, it is corrected automatically.
   - Not found (e.g. AID from another environment) → regenerated if `generateUinAidPrerequisite=true`, otherwise **skipped**.

`NewRegistrationAdult` also saves its newly generated `AID`/`UIN` at the end, so later scenarios in the same run can reuse them.

> **Note:** UpdateMyUinUpdateBiometrics, UpdateMyUinUpdateDemographicDetails and UpdateMyUinUpdateDocuments read `UIN` directly without this check. Run them after a scenario that sets `UIN` (e.g. NewRegistrationAdult) or add a valid `UIN` for the target environment manually.

### Generating a new AID/UIN

When generation is needed, the framework:

1. Creates a new adult registration in the ARC app (demographics, documents, biometrics via Mock MDS).
2. Approves it as supervisor and uploads the packet.
3. Polls IDREPO (`/idrepository/v1/identity/idvid/<RID>`) until the UIN is issued.
4. Saves `AID` and `UIN` together to `Kernel.properties`.

Polling is controlled in `resources/config/application.properties`:

* `uinGenDelayTime` — wait between checks in ms (currently `10000` = 10 s)
* `uinGenMaxLoopCount` — maximum number of checks (currently `60`)

Polling stops as soon as the UIN is available. The maximum wait is `uinGenMaxLoopCount × uinGenDelayTime` (60 × 10 s = **10 minutes**); after that the scenario fails with `UIN not generated for RID ...`.

### Dependencies

Generation only works when the target environment can process a packet end to end:

* **Backend:** packet upload, registration processing and IDREPO must be working, otherwise no UIN is issued and polling times out.
* **Keycloak:** `keycloak-external-url` and the IDREPO client (`mosip_idrepo_client_id`, `mosip_idrepo_client_secret`) in `Kernel.properties`, used to query IDREPO.
* **Operator user:** the auto-created test user (`iam-users-to-create`, `iam-users-password`) logs in and approves the packet as supervisor.
* **Device:** Mock MDS running for biometric capture, and network access for the packet upload.

> `Kernel.properties` is updated at runtime with the generated values. Do not commit these local changes.

---

## Reports

After execution, reports are generated at:

```
test-output/emailableReports
```

Reports include:
* Test summary
* Pass / Fail ratio
* Detailed execution logs

---

## Troubleshooting (Quick)

* **JVM / native memory errors**  
  Increase Windows pagefile or reduce JVM `-Xmx`

* **SLF4J multiple bindings warning**  
  Ensure only **one SLF4J binding** exists  
  (recommended: `log4j-slf4j2-impl`)

* **Missing resource files**  
  Ensure all `.properties` files are under `src/main/resources`

* **Class name mismatch**  
  Java class names are **case-sensitive** — ensure TestNG XML matches compiled class names
