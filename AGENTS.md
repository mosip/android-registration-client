# AGENTS.md

## Repository Overview

`android-registration-client` (repo short name: **ARC**) is the MOSIP
Registration Client packaged as a tablet/Android app — a portable,
Flutter-based version of the desktop
[`registration-client`](https://github.com/mosip/registration-client).

The app is a **Flutter application** (Dart UI in `lib/`) plus **native
Android library modules** under `android/` (`clientmanager`, `keymanager`,
`packetmanager`, `transliterationmanager`) for device-facing logic
(biometric SDK integration, key management, packet creation,
transliteration). Flutter talks to these modules through generated
[Pigeon](https://pub.dev/packages/pigeon) bridge code.

`ui-test/` is a separate Maven/Appium UI automation project with its own
`ui-test/README.md` — use that for automation setup, not this file.

## Technology Stack

- **App/UI**: Flutter/Dart, `pubspec.yaml` pins `sdk: ">=2.19.6 <3.0.0"`,
  Flutter SDK `3.10.4` per `README.md`. Flutter 3.10.x ships Dart 3.0.x,
  above that `<3.0.0` bound — a pre-existing repo inconsistency, not a
  typo here. Use a Dart-2-era Flutter SDK to satisfy the constraint, or
  expect to raise it if installing 3.10.4.
- **Native Android modules**: Kotlin/Java, Gradle (AGP `8.3.2`, Kotlin
  `1.9.24`, `compileSdkVersion`/`targetSdkVersion` 34, `minSdkVersion`
  28) — see `android/build.gradle` and `android/app/build.gradle`.
- **JDK**: Java 21 (`android/app/build.gradle`'s `compileOptions`; CI uses
  `zulu` 21 via `actions/setup-java`).
- **Code generation**: Pigeon (Flutter↔native bridge), `build_runner` /
  `freezed` / `json_serializable` for Dart models — all driven by
  `pigeon.sh`.
- **UI automation**: Java + Maven + Appium + TestNG, in `ui-test/`.
- **Quality tooling**: Jacoco + a SonarQube/SonarCloud Gradle plugin,
  both wired in `android/build.gradle`.

## Build & Test Commands

```bash
flutter pub get                    # install Dart/Flutter deps
sh pigeon.sh                       # regenerate Pigeon bridge + Dart models
                                    #   (required after cloning and whenever pigeon/ changes)
flutter gen-l10n                   # regenerate l10n after editing assets/l10n (per l10n.yaml, not lib/l10n)
flutter run                        # run on a connected device/emulator
flutter build apk --debug          # debug APK — can fail with D8 OutOfMemoryError
                                    #   during dex merge; android/gradle.properties'
                                    #   org.gradle.jvmargs=-Xmx1536M may be too low
flutter build apk --release        # release APK
(cd android && ./gradlew assembleDebug)   # native Android modules only
```

UI automation (Maven, from `ui-test/` — see `ui-test/README.md` for the
full Appium/emulator/Mock-MDS setup before running tests):

```bash
cd ui-test
mvn clean package -DskipTests=true      # produces target/uitest-regclient-1.0.1.jar (matches pom.xml <version>)
cd target && java -jar uitest-regclient-1.0.1.jar   # run tests
```

## Configuration

- **Backend base URL**: `android/build.gradle`'s `serverBaseURL`
  (`"https://api-internal.sandbox.xyz.net"`, plus
  `serverHealthCheckPath`/`serverActuatorInfoPath`) feeds `BuildConfig`
  fields in `android/app/build.gradle`. It's a sandbox placeholder —
  don't hardcode a different real environment here. `build-android.yml`
  and `push_trigger.yml` override it at build time via `sed`, driven by
  a `serverBaseURL`/`defaultServerBaseURL` workflow input — that's the
  supported override path.
- **Flutter SDK path**: `android/local.properties` (untracked — create
  locally, or let Android Studio's Flutter plugin generate it) must set
  `flutter.sdk=...`; `android/settings.gradle` asserts this.
- **Android app signing**: `android/app/build.gradle` reads
  `android/key.properties` (`keyAlias`, `keyPassword`, `storeFile`,
  `storePassword`) if present; if absent, the release build type
  **silently falls back to debug signing** instead of failing — always
  confirm `key.properties` + keystore exist before a release build.
  Neither is tracked. In CI (`build-android.yml`) both are produced at
  build time from the `JKS_PRIVATE_SECRET`/`KEY_PROPERTIES` secrets into
  `android/app/arc-local-keystore.jks` and `android/key.properties`.
  Locally, create your own dev keystore and never commit it.
- **Committed secrets (unresolved)**: `android/build.gradle`'s and
  `android/app/build.gradle`'s `sonarqube`/`sonar` blocks each hardcode
  the same real, live-looking `sonar.login` token; `android/build.gradle`
  also sets a real `debugPassword` (consumed by
  `android/clientmanager/build.gradle`'s `DEBUG_PASSWORD` field) and a
  contributor's personal Windows path in
  `sonar.coverage.jacoco.xmlReportPaths`. All are committed secrets in a
  public repo — treat as compromised, do not extend the pattern. CI's
  only token substitution (`build_client.yml` `sed`s `sqp_19c9702e…` in
  `*gradle.properties`) does **not** cover any of these — that pattern
  doesn't exist in the tree. See `android/AGENTS.md` — removing them
  requires a maintainer/deployment-owner decision (CI secret vs. local
  file), not a drive-by fix.

## Project Structure Notes

- `lib/` — Flutter/Dart app code; see [`lib/AGENTS.md`](lib/AGENTS.md).
- `pigeon/` / `pigeon.sh` — Pigeon defs + codegen script producing
  `lib/pigeon/*.dart` and Java classes under
  `android/app/src/main/java/io/mosip/registration_client/model/`.
  Generated output is git-ignored — always regenerate, never hand-edit.
- `android/` — Flutter Android embedding + native modules (`app`,
  `clientmanager`, `keymanager`, `packetmanager`,
  `transliterationmanager`, per `android/settings.gradle`) — see
  [`android/AGENTS.md`](android/AGENTS.md) and each module's own guide.
- `ios/`, `windows/`, `linux/`, `macos/`, `web/` — other Flutter targets
  present but out of scope here (Android is primary).
- `test/` — Dart unit/widget tests. `ui-test/` — separate Maven/Appium
  project, not part of the Flutter/Gradle build. `docs/` — flow-diagram
  PNGs. `assets/` — images/SVGs/localization referenced from
  `pubspec.yaml`.
- **Broken CI reference**: `.github/workflows/build_client.yml` and
  `push_trigger.yml` both `cd client && ./gradlew ...`, but no `client/`
  directory exists in this repo's tracked tree (verified via
  `git ls-tree -r HEAD`) — they don't match the current layout.
  `build-android.yml` is the workflow that actually matches (runs
  `flutter build apk` from repo root after `pigeon.sh`). Don't rely on
  the other two, and don't "fix" this by inventing a `client/` directory
  without maintainer confirmation.

## Development Workflow

- Active branch (per `README.md`): `develop`; `release-1.1.x` is also an
  active developer-release branch. Verify the default branch yourself —
  MOSIP repos vary.
- After cloning: `flutter pub get` → `sh pigeon.sh` → set
  `android/local.properties`'s `flutter.sdk`.
- After editing `pigeon/`: re-run `sh pigeon.sh`; commit only source
  changes — generated paths in `.gitignore` stay untracked.
- After editing `assets/l10n`: run `flutter gen-l10n` before testing.
- Styling/theme: `lib/utils/app_style.dart` /`app_config.dart`; app
  label/icon: `android/app/src/main/AndroidManifest.xml`.

## Pull Request Guidelines

- Sign off commits (`git commit -s`) — `push_trigger.yml`'s
  `dco-check` job enforces this and gates the rest of that workflow as
  a chain (`codeql` → `prebuild` → `build-apk`), so a missing sign-off
  blocks the whole build.
- Reference the tracking issue in the PR/commit body — this repo has no
  PR-linker workflow, so linking is by convention, not automation.
- Follow MOSIP's contribution guide:
  <https://docs.mosip.io/1.2.0/community/code-contributions>.
- Keep generated files (Pigeon output, Freezed/`*.g.dart`) out of PRs
  unless the PR is specifically about regenerating them.
- Never force-add `android/local.properties`, `android/key.properties`,
  or a keystore past `android/.gitignore`'s rules for them.

## Repository-Specific Considerations

- Hybrid Flutter + native-Android-modules codebase, not a pure native
  Android app — don't assume a top-level `app/` module; the Flutter
  embedding lives under `android/app` alongside sibling native modules.
- Mock MDS (Mock Device Service, for simulating biometric hardware in
  tests) is a separate project (`mosip/android-camera-mds`) — see
  `README.md`'s "Set up Mock MDS for Biometric Scan" section; it's not
  part of this repo's build.

## Agent rules

### Do

1. Run `flutter pub get` + `sh pigeon.sh` before any build; re-run
   `pigeon.sh` after editing `pigeon/`.
2. Keep `serverBaseURL`, `debugPassword`, and Sonar properties as
   placeholders — override only via documented CI inputs/secrets.
3. Verify CI-behavior claims against the actual workflow YAML and
   `git ls-tree` before relying on a workflow — `build_client.yml`/
   `push_trigger.yml` reference a path that doesn't exist here.
4. Point automation questions to `ui-test/README.md`, don't duplicate it.
5. Keep git-ignored generated output (Pigeon, Freezed/`*.g.dart`) out of
   commits and PRs.

### Do not

1. Commit `android/local.properties`, `android/key.properties`, any
   keystore, or a real Sonar/API token into a tracked file.
2. Invent a `client/` directory to make `build_client.yml`/
   `push_trigger.yml` "work" — confirm with a maintainer first.
3. Treat `api-internal.sandbox.xyz.net` as a stable real backend — it's
   a sandbox placeholder, overridden per environment at build time.
4. Skip DCO sign-off — CI enforces it.
