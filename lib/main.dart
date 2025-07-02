/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter_config/flutter_config.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/app_router.dart';
import 'package:registration_client/platform_spi/sync_response_service.dart';
import 'package:registration_client/provider/approve_packets_provider.dart';
import 'package:registration_client/provider/auth_provider.dart';
import 'package:registration_client/provider/connectivity_provider.dart';
import 'package:provider/provider.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/provider/sync_provider.dart';
import 'package:registration_client/ui/login_page.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_driver/driver_extension.dart';
import 'package:registration_client/utils/inactivity_tracker.dart';

final GlobalKey<NavigatorState> rootNavigatorKey = GlobalKey<NavigatorState>();
final GlobalKey<ScaffoldMessengerState> rootScaffoldMessengerKey =
GlobalKey<ScaffoldMessengerState>();

/// Single instance to reuse inside logout handler
final SyncResponseService _syncResponseService = SyncResponseService();

void main() async {
  enableFlutterDriverExtension(enableTextEntryEmulation: false);
  WidgetsFlutterBinding.ensureInitialized();
  final GlobalProvider appLanguage = GlobalProvider();
  await FlutterConfig.loadEnvVariables();
  await appLanguage.fetchLocale();
  runApp(
    const RestartWidget(child: RegistrationClientApp()),
  );
}

Future<void> _handleAutoLogout() async {
  final ctx = rootNavigatorKey.currentContext;
  if (ctx == null) return; // Safety guard

  final syncProvider = ctx.read<SyncProvider>();
  final authProvider = ctx.read<AuthProvider>();
  final loc = AppLocalizations.of(ctx)!;

  final bool isAnySyncInProgress = syncProvider.isSyncInProgress ||
      syncProvider.isSyncAndUploadInProgress ||
      await _syncResponseService.getSyncAndUploadInProgressStatus();

  if (isAnySyncInProgress) return; // Skip logout during critical sync

  final String result = await authProvider.logoutUser();

  if (result.contains('Logout Success')) {
    rootScaffoldMessengerKey.currentState?.showSnackBar(
      SnackBar(content: Text(loc.logout_success)),
    );
    rootNavigatorKey.currentState
        ?.pushNamedAndRemoveUntil('/login-page', (_) => false);
  } else {
    rootScaffoldMessengerKey.currentState?.showSnackBar(
      SnackBar(content: Text(loc.logout_failure)),
    );
  }
}

class RegistrationClientApp extends StatelessWidget {
  const RegistrationClientApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(
          lazy: false,
          create: (_) => ConnectivityProvider(),
        ),
        ChangeNotifierProvider(
          lazy: false,
          create: (_) => GlobalProvider(),
        ),
        ChangeNotifierProvider(
          lazy: false,
          create: (_) => SyncProvider(),
        ),
        ChangeNotifierProvider(
          lazy: false,
          create: (_) => RegistrationTaskProvider(),
        ),
        ChangeNotifierProvider(
          lazy: false,
          create: (_) => AuthProvider(),
        ),
        ChangeNotifierProvider(
          lazy: false,
          create: (_) => ApprovePacketsProvider(),
        ),
      ],
      child: const BuildApp(),
    );
  }
}

class BuildApp extends StatefulWidget {
  const BuildApp({super.key});

  @override
  State<BuildApp> createState() => _BuildAppState();
}

class _BuildAppState extends State<BuildApp> {
  late AuthProvider authProvider;
  /// Default to 5 minutes until server value arrives
  Duration _idleDuration = const Duration(seconds: 900);
  Duration _graceDuration = const Duration(seconds: 600);

  @override
  void initState() {
    super.initState();
    _loadIdleTimeFromServer();
  }

  Future<void> _loadIdleTimeFromServer() async {
    try {
      final authProvider = Provider.of<AuthProvider>(context, listen: false);

      // Run both API calls in parallel
      await Future.wait<void>([
        authProvider.getIdleTime(),
        authProvider.getRefreshedLoginTime(),
      ]);

      final int idleSecs  = int.tryParse(authProvider.idleTime) ?? 0;
      final int graceSecs = int.tryParse(authProvider.refreshedLoginTime)  ?? 0;

      if (mounted) {
        setState(() {
          if (idleSecs  > 0) _idleDuration  = Duration(seconds: idleSecs);
          if (graceSecs > 0) _graceDuration = Duration(seconds: graceSecs);
        });
      }
    } catch (e) {
      debugPrint('Failed to load idle time / grace period: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Consumer<AuthProvider>(
      builder: (_, authProvider, __) {
        return MaterialApp(
          navigatorKey: rootNavigatorKey,
          scaffoldMessengerKey: rootScaffoldMessengerKey,
          title: 'Registration Client',
          routes: AppRouter.routes,
          debugShowCheckedModeBanner: false,
          localizationsDelegates: AppLocalizations.localizationsDelegates,
          supportedLocales: AppLocalizations.supportedLocales,
          locale: context.watch<GlobalProvider>().appLocal,
          theme: ThemeData(
            colorScheme: ColorScheme.light(primary: solidPrimary),
            primaryColor: solidPrimary,
            textTheme: const TextTheme(
              titleLarge: TextStyle(fontSize: 24),
              bodyLarge: TextStyle(fontSize: 18),
            ),
            elevatedButtonTheme:
            const ElevatedButtonThemeData(style: ButtonStyle()),
          ),
          builder: (context, child) {
            MediaQueryData mediaQueryData = MediaQuery.of(context);
            Orientation orientation = mediaQueryData.orientation;
            ScreenUtil.init(
              context,
              designSize: orientation == Orientation.portrait
                  ? mediaQueryData.size.width < 750
                  ? const Size(390, 844)
                  : const Size(800, 1280)
                  : const Size(1024, 768),
              minTextAdapt: true,
              splitScreenMode: true,
            );

            ///  Wrap entire app in the tracker with server-configured timeout
            return InactivityTracker(
              timeout: _idleDuration,
              gracePeriod: _graceDuration,// ← dynamic duration
              isUserLoggedIn: authProvider.isLoggedIn,
              onTimeout: _handleAutoLogout,            // global callback (already defined)
              child: child!,
            );
          },
          home: const LoginPage(),
        );
      },
    );
  }
}

class RestartWidget extends StatefulWidget {
  const RestartWidget({super.key, required this.child});

  final Widget child;

  static void restartApp(BuildContext context) {
    context.findAncestorStateOfType<_RestartWidgetState>()?.restartApp();
  }

  @override
  State<RestartWidget> createState() => _RestartWidgetState();
}

class _RestartWidgetState extends State<RestartWidget> {
  Key key = UniqueKey();

  void restartApp() {
    setState(() {
      key = UniqueKey();
    });
  }

  @override
  Widget build(BuildContext context) {
    return KeyedSubtree(
      key: key,
      child: widget.child,
    );
  }
}
