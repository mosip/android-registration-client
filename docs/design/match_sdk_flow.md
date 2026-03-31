# Design – Local Deduplication using Match SDK

## Background

During applicant registration in the Android Registration Client (ARC), there is a possibility that an Operator may provide their own biometrics instead of the applicant’s.

To prevent this, a **local deduplication mechanism** is implemented using a Match SDK. The system compares applicant biometrics with the Operator’s biometrics and blocks the flow if a match is detected.

The Match SDK is dynamically loaded using a **Dex-based loader**.

---

## Target Users

* Operator
* Registration Supervisor

---

## Key Requirements

1. ARC must be installed and accessible.
2. Operator must be logged in with valid credentials.
3. Biometric capture must be performed for the applicant.
4. Local deduplication must:

    * Compare applicant biometrics with Operator biometrics.
    * Be configurable (enable/disable).
5. SDK loading must:

    * Work without code changes.
    * Support vendor replacement via configuration.
6. Feature must work in:

    * Offline mode
    * Online mode

---

## Non-Functional Requirements

1. Match SDK must be **pluggable and vendor-independent**.
2. SDK must be loaded dynamically using **DexClassLoader**.
3. Matching should be fast and not block UI performance.
4. Configuration-driven behavior must be supported.

---

## Solution

### 1. Build-Time Setup

* Vendor/reference SDK artifacts are placed in:

  ```text
  clientmanager/src/main/assets/biosdk/
  ```

* A Gradle task (`dexifyBiosdkAars`) converts:

    * `.aar` → `.jar` with `classes.dex` (using D8)

* If a pre-built DEX JAR is already available, it is used directly.

* Final output ensures:

    * Assets contain **DEX-compatible SDK files** for runtime loading.

---

### 2. Runtime SDK Loading

* At runtime, `BioSdkLoader`:

    1. Copies SDK files (`.dex/.jar/.apk/.zip`) from assets
       → `filesDir/biosdk/`
    2. Loads SDK using `DexClassLoader`
    3. Instantiates the configured class

* Only classes implementing **IBioApiV2** are considered valid.

---

### 3. Configuration-Based Loading (No Code Change)

* SDK provider details are read from global configuration.

* Parsed via:

    * `GlobalParamRepository.getBiometricProviderConfig()`

* Initialized via:

    * `BioSdkProviderFactory.initialize()`

* Initialization happens after:

    * Global parameter sync (`MasterDataSyncApi.getGlobalParamsSync`)

* Key pattern (modalities: `finger`, `iris`, `face`):

  ```text
  mosip.biometric.sdk.providers.<modality>.<vendorId>.<parameter>=<value>
  ```

* Typical parameters per vendor: `classname`, `version`, `args`, `threshold` (passed into `IBioApiV2.init(...)`).

> **Note:** Local device preferences may override synced global parameters where the app supports it (merged in `GlobalParamRepository`).

---

### 4. Local Deduplication Flow

* Controlled using configuration flag:

  ```properties
  mosip.registration.mds.deduplication.enable.flag=Y
  ```

* The flag value is synced from the server and stored in **SharedPreferences** (alongside other registration settings) so the running app reads a stable on-device value.

* **Runtime rule:** deduplication is treated as **ON** only when this stored value equals **`Y`** (case-insensitive). Any other value (including empty) means **OFF**.

* Flow:

    1. Applicant biometrics are captured.
    2. System retrieves all relevant operator biometrics from local storage for comparison.
    3. In `Biometrics095Service.handleRCaptureResponse`:

        * Match SDK is invoked using `IBioApiV2.match`
        * Matching handled via `MatchUtil`
    4. If dedup is ON but **no** Match SDK is available for that modality:

        * Exception: `SBIError.SBI_DEDUPE_SDK_UNAVAILABLE` (deduplication cannot run).
    5. If match is detected:

        * Exception is thrown (`SBIError.SBI_DEDUPE_MATCH`)
        * Error message shown:
          **"Biometrics matched with operator; please try again"**
        * Navigation is blocked
        * Biometric data is not saved

* **Applicant registration:** comparison uses **all** onboarded operators (including the logged-in operator).

* **Operator onboarding / update:** comparison uses **other** operators only—the **current** user is excluded so they are not blocked by their own stored biometrics.

---

### 5. Match SDK Enabled Flow

If deduplication is enabled, the system follows the Local Deduplication Flow described above.

---

### 6. Match SDK Disabled Flow

If deduplication is disabled, biometric comparison is skipped and registration continues normally.

---

## Sequence Diagram

Registration capture path (applicant biometrics). **Operator onboarding** uses the same services but `MatchUtil` excludes the current user from the gallery instead of all operators.

![MatchSDK_Flow.png](../MatchSDK_Flow.png)

---

## Scenarios

### Scenario 1: Operator Uses Own Biometrics

* Operator provides their own biometrics
* Match detected
* Registration is blocked

---

### Scenario 2: Another Operator Biometrics

* Different operator biometrics used
* Match detected
* Registration is blocked

---

### Scenario 3: Valid Applicant (Happy Flow)

* Applicant biometrics captured
* No match found
* Registration continues

---

## Configuration Steps

1. Enable/Disable deduplication:

   ```properties
   mosip.registration.mds.deduplication.enable.flag=Y
   ```

   Use any value other than **`Y`** to disable (or leave unset after sync, depending on server defaults).

   **Note:** The client enables this flag only when the value is **`Y`** (case-insensitive). Values like `true` / `false` are **not** treated as on unless your sync layer maps them to **`Y`**.

2. Configure SDK provider (example — repeat per modality as needed):

   ```properties
   mosip.biometric.sdk.providers.finger.mockvendor.classname=io.mosip.mock.sdk.impl.SampleSDK
   mosip.biometric.sdk.providers.finger.mockvendor.version=0.9
   mosip.biometric.sdk.providers.finger.mockvendor.args=
   mosip.biometric.sdk.providers.finger.mockvendor.threshold=60

   mosip.biometric.sdk.providers.iris.mockvendor.classname=io.mosip.mock.sdk.impl.SampleSDK
   mosip.biometric.sdk.providers.iris.mockvendor.version=0.9
   mosip.biometric.sdk.providers.iris.mockvendor.args=
   mosip.biometric.sdk.providers.iris.mockvendor.threshold=60

   mosip.biometric.sdk.providers.face.mockvendor.classname=io.mosip.mock.sdk.impl.SampleSDK
   mosip.biometric.sdk.providers.face.mockvendor.version=0.9
   mosip.biometric.sdk.providers.face.mockvendor.args=
   mosip.biometric.sdk.providers.face.mockvendor.threshold=60
   ```

   Replace `mockvendor` with your vendor id if different.

3. Place SDK files in:

   ```text
   assets/biosdk/
   ```

   (Under the `clientmanager` module path above; the build merges these into the APK.)

---

## Important Note (No APK Rebuild)

* Changing:

    * Class name
    * Threshold
      → Can be done via configuration (no rebuild)

* However:

    * Adding/replacing SDK binary
      → Requires new APK

* Using a **fat DEX JAR with multiple vendors** allows switching via config without rebuild.

---