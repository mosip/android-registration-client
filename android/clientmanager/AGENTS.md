# AGENTS.md — android/clientmanager/

Parent guide: [`../AGENTS.md`](../AGENTS.md)

## Purpose

Client-side registration business logic: starting/approving/submitting a
registration, biometrics capture orchestration, master-data sync,
job scheduling, audit, and packet upload/status tracking. This is the
largest and most central of the 4 native manager modules — it depends
on both `keymanager` (crypto) and `packetmanager` (packet
build/persist).

## Layout

```text
clientmanager/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/io/mosip/registration/clientmanager/
    │   │   ├── config/, constant/, dao/, entity/, exception/, interceptor/, jobs/, repository/, util/, util/advice/
    │   │   ├── dto/ (+ dto/http, dto/registration, dto/sbi, dto/uispec)
    │   │   ├── service/ (+ service/external, service/external/impl)
    │   │   └── spi/                          # the public contracts — see below
    │   └── assets/biosdk/matchsdk-debug.aar    # bundled biometric SDK — see the dexifyBiosdkAars task below
    ├── test/java/...                            # 65 unit test files (JUnit, Robolectric, Mockito, JUnit5, Spring-test)
    └── androidTest/assets/*.json                  # mock API response fixtures (no androidTest Java classes)
```

No module-level README.

## `spi/` — what this module exposes

- **`RegistrationService`** — the core registration lifecycle contract:
  `startRegistration(languages, flowType, process, latitude, longitude)`,
  `getRegistrationDto()`, `submitRegistrationDto(makerName)`,
  `approveRegistration(Registration)`, `rejectRegistration(Registration)`,
  `clearRegistration()`, `buildBIR(BiometricsDto)`.
- **`PacketService`** (impl: `service/PacketServiceImpl.java`) — packet
  sync/upload lifecycle: `syncRegistration`/`uploadRegistration` (each
  with an optional callback), `getAllRegistrations`/
  `getAllNotUploadedRegistrations`/`getRegistrationsByStatus`,
  `syncAllPacketStatus()`, `getPacketStatus(String)`,
  `isRegisteredPacketApprovalTimeBreached()`. This is where the
  cross-module dependency chain is concrete: it imports
  `keymanager`'s `CryptoUtil` directly, and `packetmanager`'s
  `IPacketCryptoService`/`DateUtils`/`HMACUtils2`/`JsonUtils`.
- Other `spi/` interfaces: `BiometricsService`, `MasterDataService`,
  `AuditManagerService`, `JobManagerService`, `SyncRestService`,
  `PreRegistrationDataSyncService`, `LocationValidationService`,
  `CenterRemapService`.

No Pigeon references in this module — Flutter-facing `HostApi` impls
that call these `spi` interfaces live in `android/app`'s
`api_services/` (see `../AGENTS.md`). New Flutter-callable capability:
`spi` interface + impl here, Pigeon glue in `android/app`.

## The `dexifyBiosdkAars` task

`build.gradle`'s custom `dexifyBiosdkAars` task scans
`src/main/assets/biosdk/*.aar`, extracts `classes.jar`, and runs R8
`D8` to convert it to DEX under `build/biosdk-dex/biosdk` so
`BioSDKLoader` can `DexClassLoader` it at runtime instead of a normal
compile-time dependency. Skips re-conversion if already DEX-format or
up to date. Wired to run before asset-merge **both** here and (via
`android/app/build.gradle`'s `afterEvaluate`) in `app` — a break here
surfaces as an `app`-module build failure (see `../AGENTS.md`). Don't
touch this task without understanding both wiring points.

## Build & Test Commands

```bash
cd android
./gradlew :clientmanager:assembleDebug
./gradlew :clientmanager:testDebugUnitTest
```

Applies the shared `../jacoco.gradle` coverage config (30% minimum line
coverage per variant) — see `../AGENTS.md`.

## Configuration

- Notable dependencies beyond the shared root-`ext` stack:
  `net.zetetic:android-database-sqlcipher` + `androidx.security:security-crypto`
  (encrypted local DB), `io.mosip.biometric.util:biometrics-util`,
  `io.mosip.kernel:kernel-biometrics-api` (`transitive=false`),
  `com.github.kenglxn.QRGen:android` (QR generation),
  `com.tom-roush:pdfbox-android`, `com.github.Tgo1014:JP2ForAndroid`
  (JPEG2000 support), Room, WorkManager, Retrofit/OkHttp,
  `com.auth0.android:jwtdecode`.
- Depends on `project(':packetmanager')` and `project(':keymanager')`.

## Agent rules

### Do

1. Route any new Flutter-callable capability through an `spi` interface
   here (or in another manager module, as appropriate) plus a
   corresponding Pigeon `HostApi` impl in `android/app` — don't
   implement Pigeon-facing logic directly in this module.
2. Keep the `dexifyBiosdkAars` → asset-merge wiring intact in both this
   module and `android/app` if you touch either's `build.gradle`.
3. Check both `keymanager`'s and `packetmanager`'s `spi` interfaces
   before adding a new crypto/packet operation here — this module should
   consume those modules' contracts, not reimplement their logic.

### Do not

1. Do not add Pigeon imports/interfaces to this module — the bridge
   layer belongs in `android/app`.
2. Do not assume `src/androidTest` has real instrumented test code — it
   only contains JSON fixtures, not test classes.
3. Do not bypass `dexifyBiosdkAars` by compiling the bundled biosdk AAR
   as a normal dependency — it's intentionally loaded via
   `DexClassLoader` at runtime.
