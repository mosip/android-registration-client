# AGENTS.md

## Repository Overview

`android-registration-client` (repo short name: **ARC**) is the MOSIP Registration
Client packaged as a tablet/Android app. It is a portable, Flutter-based version
of the desktop [`registration-client`](https://github.com/mosip/registration-client),
built to run on Android devices in the field.

The app is a **Flutter application** (Dart UI code in `lib/`) with a set of
**native Android library modules** under `android/` (`clientmanager`,
`keymanager`, `packetmanager`, `transliterationmanager`) that implement the
device-facing logic (biometric SDK integration, key management, packet
creation, transliteration). Flutter talks to these native modules through
generated [Pigeon](https://pub.dev/packages/pigeon) bridge code.

A separate Maven/Appium UI automation project lives in `ui-test/` — it already
has its own `ui-test/README.md`; see that file for automation-specific setup
instead of duplicating it here.

## Technology Stack

- **App/UI**: Flutter/Dart, `pubspec.yaml` pins `sdk: ">=2.19.6 <3.0.0"`,
  Flutter SDK `3.10.4` per `README.md`.
- **Native Android modules**: Kotlin/Java, Gradle (Android Gradle Plugin
  `8.3.2`, Kotlin `1.9.24`, `compileSdkVersion`/`targetSdkVersion` 34,
  `minSdkVersion` 28) — see `android/build.gradle` and `android/app/build.gradle`.
- **JDK**: Java 21 (`compileOptions` in `android/app/build.gradle`; CI uses
  `zulu` 21 via `actions/setup-java`).
- **Code generation**: Pigeon (Flutter-to-native bridge), `build_runner` /
  `freezed` / `json_serializable` for Dart model codegen — all driven by
  `pigeon.sh`.
- **UI automation**: Java + Maven + Appium + TestNG, in `ui-test/` (see its
  own README).
- **Quality tooling referenced in the tree**: Jacoco (code coverage) and a
  SonarQube/SonarCloud Gradle plugin, both wired in `android/build.gradle`.

## Build & Test Commands

Run these from the repository root unless noted otherwise.

Install Dart/Flutter dependencies:

```bash
flutter pub get
```

Generate the Pigeon bridge code and Dart models (required after cloning, and
whenever files under `pigeon/` change):

```bash
sh pigeon.sh
```

Generate localization data after adding/editing files under `lib/l10n`:

```bash
flutter gen-l10n
```

Run the app on a connected device or emulator:

```bash
flutter run
```

Run Flutter (Dart) unit/widget tests:

```bash
flutter test
```

Build an APK:

```bash
# Debug APK
flutter build apk --debug

# Release APK
flutter build apk --release
```

Build only the native Android modules with Gradle (from `android/`):

```bash
cd android
./gradlew assembleDebug
```

UI automation tests (Maven, from `ui-test/`) — see `ui-test/README.md` for
the full Appium/emulator/Mock-MDS setup this needs before running:

```bash
cd ui-test
mvn clean package -DskipTests=true
```

## Configuration

- **Backend base URL**: `android/build.gradle` defines
  `serverBaseURL = "\"https://api-internal.sandbox.xyz.net\""` (plus
  `serverHealthCheckPath` and `serverActuatorInfoPath`), consumed as
  `BuildConfig` fields in `android/app/build.gradle`. This is a MOSIP sandbox
  placeholder value in the repo — do not treat it as a real, always-available
  environment, and do not hardcode a different real environment's URL into
  tracked files. The `build-android.yml` and `push_trigger.yml` workflows both
  substitute this value at build time via `sed`, driven by a
  `serverBaseURL`/`defaultServerBaseURL` workflow input — that is the
  supported way to point a build at a different backend, not editing
  `build.gradle` directly.
- **Flutter SDK path**: `android/local.properties` (not committed — create it
  locally, or let Android Studio's Flutter plugin generate it) must set
  `flutter.sdk=your-flutter-sdk-path`. `android/settings.gradle` asserts
  this file exists and that the property is set.
- **Android app signing**: `android/app/build.gradle` reads
  `key.properties` (via `android/key.properties`, if present) for
  `keyAlias`, `keyPassword`, `storeFile`, `storePassword`. If that file is
  absent, the release build type falls back to the debug signing config
  (`signingConfig localProperties['storeFile'] ? signingConfigs.release :
  signingConfigs.debug`). Neither `android/key.properties` nor a keystore
  `.jks` file is tracked in this repository. In CI (`build-android.yml`), both
  are produced at build time by base64-decoding the `JKS_PRIVATE_SECRET` and
  `KEY_PROPERTIES` GitHub Actions secrets into
  `android/app/arc-local-keystore.jks` and `android/key.properties`
  respectively — real key material never lives in the repo or in tracked
  config files. When working locally, create these files yourself with your
  own dev keystore and never commit them.
- **SonarQube properties**: `android/build.gradle`'s `sonarqube {}` block
  contains placeholder strings (`your-sonar-project-key`,
  `your-sonar-organization`, `your-sonar-token`) — these are examples, not
  real credentials. `push_trigger.yml` shows the actual mechanism: a
  `sed` replacement of a snapshot token pattern in `*gradle.properties` files,
  sourced from the `SONAR_TOKEN` GitHub Actions secret. Do not hand-edit a
  real Sonar token into `android/build.gradle`.
- **Debug password**: `android/build.gradle` sets a literal placeholder
  `debugPassword = "\"your-debug-password\""`. Treat this the same way — a
  placeholder to override via build config, not a value to replace with a
  real secret in a tracked file.

## Project Structure Notes

- `lib/` — Flutter/Dart application code (`main.dart`, `app_router.dart`,
  `model/`, `platform_android/`, `platform_spi/`, `provider/`, `ui/`,
  `utils/`).
- `pigeon/` and `pigeon.sh` — Pigeon message definitions and the codegen
  script that produces `lib/pigeon/*.dart` and the matching generated Java
  classes under
  `android/app/src/main/java/io/mosip/registration_client/model/`.
  Generated output is git-ignored (see `.gitignore`) — always regenerate
  locally rather than hand-editing generated files.
- `android/` — the Flutter Android embedding project, plus native library
  modules declared in `android/settings.gradle`: `app`, `clientmanager`,
  `keymanager`, `packetmanager`, `transliterationmanager`.
- `ios/`, `windows/`, `linux/`, `macos/`, `web/` — other Flutter platform
  targets present in the tree; this AGENTS.md focuses on the Android path
  since that is the repository's primary purpose.
- `test/` — Dart unit/widget tests (e.g. `login_test.dart`,
  `machine_details_test.dart`).
- `ui-test/` — separate Maven-based Appium/TestNG UI automation project with
  its own `README.md` and `pom.xml`; not part of the Flutter/Gradle build.
- `docs/` — flow diagrams (PNG) documenting app screens/flows, plus a
  `design/` subfolder.
- `assets/` — app images, SVGs, and localization bundles referenced from
  `pubspec.yaml`.
- **CI workflow note**: `.github/workflows/build_client.yml` and
  `.github/workflows/push_trigger.yml` both `cd client` and run
  `./gradlew ...` from a `client/` directory. No `client/` directory exists
  anywhere in this repository's tracked tree (confirmed via
  `git ls-tree -r HEAD`), so as written these two workflows do not match the
  current repo layout. `.github/workflows/build-android.yml` is the workflow
  that matches the actual tree — it runs `flutter build apk` from the repo
  root after `pigeon.sh` codegen. Do not assume `build_client.yml` or
  `push_trigger.yml` describe a working build path; verify against the
  current tree before relying on them, and do not "fix" this by inventing a
  `client/` directory unless you have confirmed with a maintainer that one
  should exist.

## Development Workflow

- Primary active branch (per `README.md`): `develop`. A `release-1.1.x`
  branch is also called out as an active developer-release branch. Verify the
  current default branch with your Git host before branching, since MOSIP
  repos vary.
- After cloning, before building: run `flutter pub get`, then `sh pigeon.sh`
  to generate the Flutter/native bridge code, then set
  `android/local.properties` with your `flutter.sdk` path.
- If you add or change files under `pigeon/`, re-run `sh pigeon.sh` and
  commit only the intended source changes — generated output paths listed in
  `.gitignore` (`/lib/pigeon/`, the Android Pigeon model package, iOS Pigeon
  files) should stay untracked.
- If you add localization strings under `lib/l10n`, run `flutter gen-l10n`
  before testing UI changes that use them.
- Styling/theme lives in `lib/utils/app_style.dart` and
  `lib/utils/app_config.dart`; app label/icon are set in
  `android/app/src/main/AndroidManifest.xml`.

## Pull Request Guidelines

- Sign off commits (Developer Certificate of Origin) — `use-pr-linker.yml`
  and the DCO check in `push_trigger.yml` both enforce this; use
  `git commit -s`.
- Reference the tracking issue in the PR description/commit body; MOSIP's
  PR-linker workflow (`use-pr-linker.yml`, calling
  `mosip/kattu/.github/workflows/link-pr-to-issue.yml`) links PRs to issues
  automatically when the issue is referenced.
- Follow the general MOSIP contribution guide linked from `README.md`:
  <https://docs.mosip.io/1.2.0/community/code-contributions>.
- Keep generated files (Pigeon output, Freezed/`*.g.dart` model files) out of
  PRs unless the PR is specifically about regenerating them — they are
  git-ignored for a reason.
- Do not commit `android/local.properties`, `android/key.properties`, or any
  `.jks` keystore file, even though `.gitignore` does not list
  `android/key.properties` or the keystore filename explicitly by path —
  these are populated only via CI secrets or a developer's own local, private
  files and must never enter version control.

## Repository-Specific Considerations

- This is a hybrid Flutter + native-Android-modules codebase, not a pure
  native Android app — commands and file layout differ from a typical
  single-module Gradle Android project. Don't assume a top-level `app/`
  module; the Flutter embedding lives under `android/app`, alongside sibling
  native library modules.
- The `serverBaseURL` and related `BuildConfig` values are sandbox
  placeholders meant to be overridden per environment at build time (via
  workflow inputs / secrets), not values to be permanently changed in
  `android/build.gradle`.
- `build_client.yml` and `push_trigger.yml` reference a `client/` directory
  that is not present in this repo's tracked tree (see Project Structure
  Notes above) — treat any instructions or automation based on those two
  workflows with caution until that mismatch is resolved upstream.
- The Mock MDS (Mock Device Service) app, used to simulate biometric
  hardware for testing, is a separate MOSIP project
  (`mosip/android-camera-mds`) — see `README.md`'s "Set up Mock MDS for
  Biometric Scan" section for setup steps; it is not part of this
  repository's build.

## Agent rules

### Do

1. Run `flutter pub get` and `sh pigeon.sh` before attempting any build, and
   re-run `sh pigeon.sh` after editing anything under `pigeon/`.
2. Keep `serverBaseURL`, `debugPassword`, and Sonar properties in
   `android/build.gradle` as placeholders; override them only through the
   documented CI inputs/secrets, never by hardcoding a real value into a
   tracked file.
3. Verify claims about CI behavior against the actual workflow YAML in
   `.github/workflows/` and the actual tracked file tree (`git ls-tree`)
   before describing or relying on a workflow — this repo has at least one
   workflow (`build_client.yml`/`push_trigger.yml`) that references a path
   not present in the tree.
4. Point automation-related questions to `ui-test/README.md` rather than
   duplicating or contradicting it.
5. Keep Pigeon-generated files, Freezed/`*.g.dart` model files, and other
   git-ignored generated output out of commits and PRs.

### Do not

1. Do not commit `android/local.properties`, `android/key.properties`, any
   `.jks`/keystore file, or a real Sonar/API token into any tracked file.
2. Do not invent or assume a `client/` directory to make `build_client.yml`
   or `push_trigger.yml` "work" — confirm with a maintainer first if that
   mismatch needs fixing.
3. Do not treat `https://api-internal.sandbox.xyz.net` in
   `android/build.gradle` as a stable, real backend to build integrations
   against — it is a MOSIP sandbox/placeholder host, overridden per
   environment at build time.
4. Do not skip DCO sign-off on commits — CI enforces it.
