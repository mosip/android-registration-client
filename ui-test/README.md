# Reg-Client Automation

Mobile Automation Framework for Android using Appium & TestNG.

---

## Introduction

Reg-Client Automation is a mobile automation framework for Android platforms. It automates positive and negative scenarios and supports both emulators and real devices.

## Prerequisites

Install the following before setup:

* Java JDK 11 or above
* Maven
* Android Studio (for emulator)
* Node.js (for Appium)
* ADB (comes with Android SDK)
* Eclipse IDE or IntelliJ IDEA (optional)

---

## Appium Setup

1. Install Node.js: [https://nodejs.org](https://nodejs.org) (choose Windows 64-bit installer). Ensure **Add to PATH** is selected.
2. Install Appium server globally:

```bash
npm install -g appium
appium -v    # verify
```

3. Start Appium server:

```bash
appium
```

4. (Optional) Install Appium Desktop (GUI) and start server from the app.

---

## Emulator Setup

1. Open Android Studio → **Tools > Device Manager > Create Device**.
2. Select a device (e.g., Pixel 5) and an API level (30/31).
3. Download the system image and finish setup.
4. Start emulator from Device Manager.

---

## Mock MDS Setup

1. Place `mockmds.apk` in your platform-tools folder, e.g.:

```
C:\Users\<username>\AppData\Local\Android\Sdk\platform-tools
```

2. Verify emulator is running:

```bash
adb devices
```

3. Install the APK:

```bash
adb install mockmds.apk
```

4. Verify installation:

```bash
adb shell pm list packages | findstr mosip
```

---

## WireGuard Setup

1. Download WireGuard APK (official site or F‑Droid) and place it in platform-tools.
2. Install:

```bash
adb install com.wireguard.android-1.0.20250531.apk
```

3. Launch WireGuard and activate the tunnel if required.

---

## Clone & Setup Automation Framework

Fork the repository (branch: `develop`) and clone locally:

```bash
cd ~/Desktop
mkdir arc && cd arc
git clone https://github.com/mosip/android-registration-client.git
```

Import the project into your IDE (Eclipse/IntelliJ) if needed.

---

## Run Automation

### From IDE

* Create a Run Configuration with **Main Class**: `regclient.utils.TestRunner` and run.

### From packaged JAR

Build and run the JAR:

```bash
cd android-registration-client/ui-test/target
java -jar uitest-regclient-0.0.1.jar
```

Alternatively run the provided batch script: `run_regclient.bat` (if present).

---

## Important Configuration Files

Update these files before running tests:

* `resources/Config/kernal.properties` — Environment details
* `resources/testdata.json` — Test data (uin, language, rid, camera id)
* `resources/config.properties` — `nodePath`, `appiumServerExecutable`
* `resources/DesiredCapabilies.json` — `udid`, app path
* camara.java- update camera, retake button cordinates

Place any runtime properties such as `bioValue.properties` under `src/main/resources/config` (or ensure your build copies them to `target/classes/config`).

---

## Reports

After execution, reports are generated at:

```
test-output/emailableReports
```

Reports include:

* Test summary
* Pass/fail ratio
* Detailed logs

---

## Troubleshooting (quick)

* **Native memory / JVM errors**: Increase Windows pagefile or reduce JVM `-Xmx` used by Eclipse/installer.
* **SLF4J multiple bindings**: Ensure only one SLF4J binding (preferably Log4j2 `log4j-slf4j2-impl`) is on classpath and exclude others.
* **Missing resource files**: Put required `.properties` under `src/main/resources` so Maven copies them to `target/classes`.
* **Class name mismatch**: Java class names and filenames are case-sensitive; ensure TestNG XML references match compiled class names.
