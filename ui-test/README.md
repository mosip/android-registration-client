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
   ```
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

---

## Important Configuration Files

Update these files before execution:

* `resources/config/kernal.properties` — Environment details
* `resources/testdata.json` — Test data (uin, language, aid, camera id). The `AID` and `UIN` values are managed automatically — see [Introducer AID Handling](#introducer-aid-handling) below; you don't need to maintain them by hand.
* `resources/config.properties` — `nodePath`, `appiumServerExecutable`
* `resources/DesiredCapabilies.json` — `udid`, app path
* `camera.java` — Update camera & retake button coordinates

📌 Any runtime properties (e.g. `bioValue.properties`) must be placed under:
```
src/main/resources/config
```
(Maven will copy them to `target/classes/config`)

---

## Introducer AID Handling

Infant/minor registration (and biometric correction, UIN update) flows need an existing, real identity with a confirmed UIN to enter as the **introducer**. This is managed automatically — no manual RID/AID lookup required.

**How it works:**

1. At the start of these flows, `UinRidGenerator.ensureValidIntroducer(driver)` runs:
   * `regclient.androidTestCases.NewRegistrationInfant`
   * `regclient.androidTestCases.NewRegistrationMinor`
   * `regclient.androidTestCases.NewRegistrationMinorException`
   * `regclient.androidTestCases.NewRegistrationAdultUploadMultipleDoccuments`
   * `regclient.androidTestCases.BiometricCorrection`
   * `regclient.androidTestCases.UpdateMyUinInfant`
   * `regclient.androidTestCases.UpdateMyUinMinor`

2. It reads the `AID` value already saved in `testdata.json` and checks whether it's still valid **for the current environment** (`UinRidGenerator.isRidValidForEnvironment`, which queries idrepo).
   * **Valid** → reused as-is. No new registration is performed.
   * **Missing / blank / invalid** (e.g. first run, or `testdata.json` pointed at a different environment) → `RidGenerator.generateNewRid(driver)` runs a full new-adult-registration flow end to end (login → demographic details → biometrics → submit → supervisor approval → upload) to create a fresh identity.

3. Once a fresh identity is created, `UinRidGenerator.fetchAndPersistUin(rid)` polls idrepo until the UIN is actually generated, then persists **both** `AID` and `UIN` back to `testdata.json`. This is also the same method `NewRegistrationAdult` calls after every successful adult registration, so whichever adult registration most recently succeeded in a run becomes the introducer candidate for later infant/minor tests.

4. The six language-specific `DemographicDetailsPage*.java` classes (`English`, `Hindi`, `Arabic`, `French`, `Tamil`, `Kannada`) read `testdata.json`'s `AID` value and type it into the introducer-RID field during infant/minor registration.

**Config knobs** (`resources/config/application.properties`):
* `uinGenMaxLoopCount` / `uinGenDelayTime` — how many times, and how far apart, to poll idrepo before giving up on UIN generation. Increase these if your environment's registration-processor pause/resume rules (e.g. non-resident applicant auto-pause) add extra latency beyond steady-state processing time.

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

