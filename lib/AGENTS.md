# AGENTS.md — lib/

Parent guide: [`../AGENTS.md`](../AGENTS.md)

## Purpose

The Flutter/Dart application: UI, routing, state management, and the
Dart-side of the Pigeon platform bridge. This is the code that becomes
the actual app; native device logic lives in `../android/`'s manager
modules (see `../android/AGENTS.md`).

## Layout

```text
lib/
├── main.dart              # app entry point — see below
├── app_router.dart          # static named-route table
├── model/                     # 10 files — data models (see below)
├── platform_android/            # 16 files — concrete Android impls of platform_spi (see below)
├── platform_spi/                  # 16 files — abstract interfaces the app codes against (see below)
├── provider/                        # 8 files — ChangeNotifier state (see below)
├── ui/                                # screens, organized by feature (see below)
├── utils/                               # 14 files — design tokens, styles, misc helpers
└── pigeon/                                # Pigeon-generated output — gitignored, does NOT exist until `sh pigeon.sh` runs
```

**Correction to the root `AGENTS.md`**: it references `lib/l10n` for
localization source files. That directory does not exist — the actual
ARB files live in `assets/l10n/` (`app_ar.arb`, `app_en.arb`,
`app_fr.arb`, `app_hi.arb`, `app_kn.arb`, `app_ta.arb`), per the repo
root's `l10n.yaml` (`arb-dir: assets/l10n`). `flutter gen-l10n` still
generates `AppLocalizations` the same way — only the source-file
location differs from what the root file currently says.

## `main.dart` / `app_router.dart`

- **State management**: `provider: ^6.0.5`. `RegistrationClientApp` wraps
  the app in a `MultiProvider` with 6 eager `ChangeNotifierProvider`s:
  `ConnectivityProvider`, `GlobalProvider`, `SyncProvider`,
  `RegistrationTaskProvider`, `AuthProvider`, `ApprovePacketsProvider`.
- `main()` sets up a native→Dart `MethodChannel`
  (`io.mosip.registration_client/sync_restart`) for a sync-complete
  restart dialog, loads env via `flutter_config`, and wraps the app in
  `RestartWidget` (soft restart via new `UniqueKey()`; hard restart via
  the `restart_app` package).
- `BuildApp` builds `MaterialApp`: `routes: AppRouter.routes`,
  localization from generated `AppLocalizations`, `ScreenUtil.init` for
  responsive scaling, wrapped in `InactivityTracker` (auto-logout; idle
  time from `AuthProvider.getIdleTime()`/`getRefreshedLoginTime()`).
- **Routing**: `AppRouter` is a plain static
  `Map<String, Widget Function(BuildContext)>` — no router package. Only
  6 named routes exist (`LoginPage.route`, 4 process routes dispatching
  to `GenericProcess` via a `ProcessType` enum, `OnboardLandingPage.route`,
  `HomePage.route`); most navigation is direct
  `Navigator.push(MaterialPageRoute(...))` — don't assume every screen
  has an `app_router.dart` entry.

## `model/`

Mixed codegen strategy — check before assuming a file follows one
pattern:

- `@freezed` (+ `json_serializable`, both `part 'x.freezed.dart'` and
  `part 'x.g.dart'`): `biometrics_dto.dart`, `field.dart`, `process.dart`,
  `registration.dart`, `screen.dart`, `validator.dart`.
- `json_serializable` only: `actuator_info.dart`.
- Hand-written, mutable, no codegen: `biometric_attribute_data.dart`,
  `upload_document_data.dart`.

`.g.dart`/`.freezed.dart` companion files are `build_runner`-generated
and gitignored — they will not exist in a fresh checkout until codegen
runs (see root `AGENTS.md`'s build commands).

## `platform_spi/` vs `platform_android/`

`platform_spi/` is an **abstract interface layer** (16 abstract
classes), and `platform_android/` is the **concrete Android
implementation** of each (16 `*Impl` classes) that call into the
Pigeon-generated `*Api` classes. Pairing is 1:1 by name (e.g.
`auth_service.dart` ↔ `auth_service_impl.dart`, covering: audit, auth,
biometrics, dash_board, demographic, document, document_category,
dynamic_response, global_config, machine_key, network, packet,
process_spec, registration, sync_response, transliteration).

Each `platform_spi/*.dart` abstract class exposes a `factory`
constructor that resolves **directly** (not via `Platform.isAndroid` or
a conditional import) to the Android impl:

```dart
// lib/platform_spi/auth_service.dart
abstract class AuthService {
  Future<User> validateUser(String username, String langCode);
  factory AuthService() => getAuthServiceImpl();
}
```

```dart
// lib/platform_android/auth_service_impl.dart
class AuthServiceImpl implements AuthService {
  @override
  Future<User> validateUser(String username, String langCode) async {
    user = await UserApi().validateUser(username, langCode); // Pigeon-generated API
  }
}
AuthService getAuthServiceImpl() => AuthServiceImpl();
```

Consumers instantiate the interface type (`AuthService()`) and the
factory hardwires to the Android impl. **No other platform
implementation exists** (no `platform_ios/`) despite `ios/`, `windows/`,
`linux/`, `macos/`, `web/` folders existing at the repo root — don't
assume cross-platform support at the Dart layer.

## `provider/`

Uses the `provider` package. Files: `approve_packets_provider.dart`,
`auth_provider.dart`, `biometric_capture_control_provider.dart`,
`connectivity_provider.dart`, `export_packet_provider.dart`,
`global_provider.dart`, `registration_task_provider.dart`,
`sync_provider.dart`.

Consistent pattern across all of them: a `ChangeNotifier` subclass
holding private state + public getters + explicit setter methods that
call `notifyListeners()`, delegating actual work to a `platform_spi`
service instance held as a field (e.g. `AuthProvider` holds
`final AuthService auth = AuthService();`). Follow this pattern for any
new provider rather than introducing a different state-management style.

## `ui/`

Organized **by screen/feature**, with a shared `ui/widgets/` for
cross-cutting widgets and `ui/common/` for shared chrome/navbars:

```text
ui/
├── login_page.dart, machine_keys.dart          # top-level screens
├── approve_packet/      + widget/ (5)
├── common/                                      # mobile/tablet navbar, tablet footer/header
├── dashboard/            dashboard_mobile.dart, dashboard_tablet.dart, user_dashboard.dart
├── export_packet/          + widgets/ (7)
├── onboard/                home_page.dart, onboard_landing_page.dart, onboarding_page.dart
│                             + portrait/ (5), widgets/ (7)
├── post_registration/        acknowledgement_page.dart, authentication_page.dart, preview_page.dart
├── process_ui/                 generic_process.dart, process_type.dart
│                                 + widgets/ (26 — one per dynamic form-field control type)
│                                 + widgets_mobile/ (2 — portrait variants)
├── profile/                       logout_alert.dart, profile.dart
├── scanner/                          custom_scanner.dart, preview_screen.dart, qr_code_scanner.dart, scanner.dart
├── settings/                           settings_screen.dart + widgets/ (2)
└── widgets/                              9 shared widgets (sync dialog, language/network/password/username, remap dialogs)
```

Recurring pattern: mobile-vs-tablet split (`dashboard_mobile.dart`/
`dashboard_tablet.dart`, `widgets/` vs `widgets_mobile/` under
`process_ui/`, `portrait/` under `onboard/`), tied to `ScreenUtil`/
`utils/responsive.dart` — check whether a new screen needs a
mobile/tablet or portrait/landscape variant.

## `utils/`

- **`app_config.dart`** — design tokens: asset path strings, the color
  palette as top-level `Color` constants (`solidPrimary`, `appWhite`,
  status colors, ~35 named colors total), font weights, the supported
  language list, and responsive breakpoint getters (`isDesktop`,
  `isMobileSize`, based on `ScreenUtil().screenWidth`).
- **`app_style.dart`** — a large `abstract class AppTextStyle` of static
  `TextStyle` presets (e.g. `mobileHelpText`, `mobileHeaderText`),
  composed from `app_config.dart`'s tokens. Add new reusable text styles
  here rather than inlining `TextStyle(...)` in a widget.
- Other files: `biometrics_utils.dart`, `constants.dart`,
  `file_storage.dart`, `inactivity_tracker.dart`,
  `life_cycle_event_handler.dart`, `location_service.dart`,
  `responsive.dart`, `secure_screen_service.dart`, `secure_storage.dart`,
  `stateful_wrapper.dart`, `static_resource_id_constants.dart`,
  `sync_job_def.dart`.

## `pigeon/` (generated output)

Gitignored (`/lib/pigeon/`) — does not exist until `sh pigeon.sh` runs
(see root `AGENTS.md`). Source defs live in `../pigeon/` (17 files).
Every `platform_spi`/`platform_android`/`provider` file that talks to
native code imports `package:registration_client/pigeon/*.dart` — the
app won't compile/analyze until codegen has run once.

## Configuration

Key `pubspec.yaml` dependencies grounding the patterns above:
`provider: ^6.0.5` (state management), `freezed`/`json_serializable`/
`build_runner` (model codegen), `pigeon: ^10.0.1`, `http: ^0.13.6`,
`flutter_secure_storage`/`shared_preferences`/`path_provider` (local
storage), `flutter_screenutil` (responsive UI), `flutter_config` (env
config), `connectivity_plus`.

## Agent rules

### Do

1. Add new native-bridged capabilities as an `platform_spi` interface +
   `platform_android` impl pair, following the existing factory-resolves-
   to-impl pattern — don't call Pigeon APIs directly from `ui/` or
   `provider/`.
2. Follow the existing `ChangeNotifier` + getter/setter pattern in
   `provider/` for new state.
3. Run `sh pigeon.sh` (see root `AGENTS.md`) before assuming any file
   importing from `pigeon/` will compile.
4. Check whether a new screen needs a mobile/tablet or portrait/landscape
   variant, matching the pattern used by nearby existing screens.

### Do not

1. Do not assume `lib/l10n` exists — localization ARB source files are
   under `assets/l10n/`.
2. Do not assume iOS/other platform support exists at the Dart layer —
   every `platform_spi` factory resolves to the Android impl only, even
   though other Flutter platform folders exist at the repo root.
3. Do not hand-edit anything under `lib/pigeon/` — it's fully generated
   and gitignored; edit the source definitions in `../pigeon/` instead.
4. Do not assume every screen has a named route in `app_router.dart` —
   most navigation is direct `Navigator.push`.
