import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/app_router.dart';

import 'package:registration_client/provider/auth_provider.dart';
import 'package:registration_client/provider/connectivity_provider.dart';

import 'package:provider/provider.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/provider/sync_provider.dart';
import 'package:registration_client/ui/login_page.dart';
import 'package:registration_client/utils/app_config.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  final GlobalProvider appLanguage = GlobalProvider();
  await appLanguage.fetchLocale();
  runApp(
    const RestartWidget(child: RegistrationClientApp()),
  );
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
      ],
      child: const BuildApp(),
    );
  }
}

class BuildApp extends StatelessWidget {
  const BuildApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      routes: AppRouter.routes,
      debugShowCheckedModeBanner: false,
      localizationsDelegates: AppLocalizations.localizationsDelegates,
      supportedLocales: AppLocalizations.supportedLocales,
      locale: Provider.of<GlobalProvider>(context).appLocal,
      theme: ThemeData(
          colorScheme: ColorScheme.light(primary: solidPrimary),
          primaryColor: solidPrimary,
          textTheme: const TextTheme(
            titleLarge: TextStyle(fontSize: 24),
            bodyLarge: TextStyle(fontSize: 18),
          ),
          elevatedButtonTheme:
              const ElevatedButtonThemeData(style: ButtonStyle())),
      builder: (context, child) {
        MediaQueryData mediaQueryData = MediaQuery.of(context);
        Orientation orientation = mediaQueryData.orientation;
        ScreenUtil.init(
          context,
          designSize: orientation == Orientation.portrait
              ? const Size(390, 844)
              : const Size(1024, 768),
          minTextAdapt: true,
          splitScreenMode: true,
        );
        context.read<GlobalProvider>().setMachineDetails();
        return child!;
      },
      home: const LoginPage(),
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
