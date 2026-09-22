# AGENTS.md — android/

Parent guide: [`../AGENTS.md`](../AGENTS.md)

## Purpose

Flutter Android embedding (module `app`) plus four native library modules
Flutter calls into via Pigeon: `clientmanager`, `keymanager`,
`packetmanager`, `transliterationmanager`. See each module's own
`AGENTS.md` for its own responsibilities.

## Layout

```text
android/
├── build.gradle              # root Gradle config — version single source of truth (see below)
├── settings.gradle            # includes :app, :clientmanager, :keymanager, :packetmanager, :transliterationmanager
├── gradle.properties           # JVM args, AndroidX/Jetifier flags
├── jacoco.gradle                 # shared coverage config (4 manager modules; not app)
├── gradle/wrapper/gradle-wrapper.properties   # Gradle 8.5
├── app/                            # Flutter embedding — see below
├── clientmanager/ keymanager/ packetmanager/ transliterationmanager/   # each has its own AGENTS.md
```

`gradlew`/`gradlew.bat`/`.gradle/` are gitignored — a fresh clone has no
wrapper scripts checked in; don't assume `./gradlew` exists without
checking.

## Module dependency graph

From each module's `implementation project(...)` lines:

```text
app                     → clientmanager, keymanager, packetmanager, transliterationmanager
clientmanager           → packetmanager, keymanager
packetmanager           → keymanager
keymanager              → (no module deps; testImplementation project(':clientmanager') — test-only)
transliterationmanager  → (no module deps)
```

`keymanager`'s test-only dependency on `clientmanager` is the reverse of
the production direction (`clientmanager` depends on `keymanager` in
main code) — don't be confused by this when tracing what depends on
what; it only applies to `keymanager`'s own test sources.

## `android/build.gradle` (root Gradle config)

Centralizes almost every version number in one `ext {}` block — module
`build.gradle` files reference `rootProject.ext.*` rather than
hardcoding. Includes AGP `8.3.2`, Kotlin `1.9.24`, `compileSdk`/
`targetSdk` 34, `minSdk` 28, `serverBaseURL`/`serverHealthCheckPath`/
`serverActuatorInfoPath` (see root `AGENTS.md`), a committed-secret
`debugPassword` (see root `AGENTS.md`'s Configuration for the full
warning — do not re-add or extend this pattern), and ~50 dependency
version properties (Dagger, OkHttp/Retrofit, Jackson, BouncyCastle, jose4j,
SQLCipher, PDFBox, ICU4J, `biometrics-util`, `kernel-biometrics-api`,
etc.).

Also: auto-detects `android.namespace` for `com.android.library`
subprojects missing one (AGP-8 shim for vendored Flutter plugins);
disables unit tests for 6 hardcoded third-party plugin subprojects whose
Mockito tests break under JDK 17/21 (`flutter_plugin_android_lifecycle`,
`geolocator_android`, `image_picker_android`, `url_launcher_android`,
`shared_preferences_android`, `webview_flutter_android`); suppresses
lint for every subproject except this repo's own 5 modules;
`rootProject.buildDir = '../build'`.

## `android/settings.gradle`

Includes 5 modules: `:app`, `:clientmanager`, `:packetmanager`,
`:keymanager`, `:transliterationmanager`. Asserts `local.properties`
exists and reads `flutter.sdk` from it *before* Flutter's
`app_plugin_loader.gradle` runs — so a valid `flutter.sdk` path is
required just to **sync**, not only to build.

## `android/app/` (Flutter embedding)

- `namespace`/`applicationId`: `io.mosip.registration_client`.
  `minSdkVersion 28`, JDK 21.
- `buildConfigField`s `BASE_URL`/`HEALTH_CHECK_PATH`/`ACTUATOR_INFO_PATH`
  come from the root `ext` block (see root `AGENTS.md`'s Configuration).
- Signing: `signingConfigs.release` reads keys from `local.properties`
  (`key.properties`, if present, is merged into it first). If `storeFile`
  isn't set, release falls back to **debug** signing — don't rely on
  that fallback for a real release (see root `AGENTS.md`'s Configuration).
- `mergeDebugAssets`/`mergeReleaseAssets` `dependsOn`
  `:clientmanager:dexifyBiosdkAars` — app's asset merge is coupled to
  that custom task (see `clientmanager/AGENTS.md`); if it breaks, `app`'s
  build breaks too.
- Depends on all 4 manager modules plus its own stack (Dagger, Room,
  WorkManager, OkHttp/Retrofit, PDFBox, `biometrics-util`, BouncyCastle).
- Has its own inline `jacocoTestReport` task — does **not** use the
  shared `../jacoco.gradle`.
- Pigeon bridge implementations live under
  `android/app/src/main/java/io/mosip/registration_client/api_services/`
  — each implements a generated `<Feature>Pigeon.<Feature>Api` and
  delegates to an `spi` service in one of the 4 manager modules. **None
  of the 4 manager modules reference Pigeon directly.**
- `AndroidManifest.xml`: `android:allowBackup="false"`,
  `usesCleartextTraffic="false"`; scoped-storage + location + network
  permissions; `.MainActivity` (Flutter Embedding v2).

## Build & Test Commands

See the root `AGENTS.md` for the full Flutter-level commands
(`flutter pub get`, `sh pigeon.sh`, `flutter run`, `flutter build apk`).
From inside `android/`:

```bash
cd android
./gradlew assembleDebug
```

Per-module unit tests (only the 4 manager modules apply the shared
`jacoco.gradle` coverage config; `app` has its own):

```bash
./gradlew :clientmanager:testDebugUnitTest
./gradlew :keymanager:testDebugUnitTest
./gradlew :packetmanager:testDebugUnitTest
./gradlew :transliterationmanager:testDebugUnitTest
```

## Agent rules

### Do

1. Add new version numbers to `android/build.gradle`'s root `ext {}`
   block and reference them via `rootProject.ext.*` from the relevant
   module — don't hardcode a version directly in a module's own
   `build.gradle` unless the existing code already does so for that
   dependency.
2. Confirm which of the 5 modules a change belongs to before editing —
   check the dependency graph above so you don't introduce a reverse
   dependency (e.g. `keymanager` depending on `clientmanager` in main
   code, not just tests).
3. Remember the Pigeon `HostApi` implementations live in `android/app`'s
   `api_services/`, not in the manager modules — if you're wiring up a
   new Pigeon method, that's where the implementation goes.

### Do not

1. Do not assume `./gradlew` exists in a fresh checkout — it's gitignored.
2. Do not add Pigeon references inside `clientmanager`, `keymanager`,
   `packetmanager`, or `transliterationmanager` — none of them use
   Pigeon today, and the bridge layer belongs in `android/app`.
