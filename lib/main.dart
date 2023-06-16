/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/app_router.dart';

import 'package:registration_client/provider/app_language_provider.dart';
import 'package:registration_client/provider/auth_provider.dart';
import 'package:registration_client/provider/connectivity_provider.dart';

import 'package:provider/provider.dart';
import 'package:flutter_localizations/flutter_localizations.dart';

import 'package:provider/provider.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/ui/login_page.dart';

import 'package:registration_client/utils/app_config.dart';

import 'ui/dashboard/dashboard_mobile.dart';
import 'utils/responsive.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  final AppLanguageProvider appLanguage = AppLanguageProvider();
  await appLanguage.fetchLocale();
  runApp(
    const MyApp(),
  );
}

class MyApp extends StatelessWidget {
  const MyApp();

  // This widget is the root of your application.
  static const platform =
      MethodChannel('com.flutter.dev/io.mosip.get-package-instance');

  @override
  Widget build(BuildContext context) {
    WidgetsBinding.instance.addPostFrameCallback((timeStamp) {
      _callAppComponent();
    });
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(
          lazy: false,
          create: (_) => AppLanguageProvider(),
        ),
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
          create: (_) => RegistrationTaskProvider(),
        ),
        ChangeNotifierProvider(
          lazy: false,
          create: (_) => AuthProvider(),
        ),
      ],
      child: BuildApp(),
    );
  }

  Future<void> _callAppComponent() async {
    await platform.invokeMethod("callComponent");
  }
}

class BuildApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      routes: AppRouter.routes,
      debugShowCheckedModeBanner: false,
      localizationsDelegates: AppLocalizations.localizationsDelegates,
      supportedLocales: AppLocalizations.supportedLocales,
      locale: Provider.of<AppLanguageProvider>(context).appLocal,
      theme: ThemeData(
          colorScheme: ColorScheme.light(primary: solid_primary),
          primaryColor: solid_primary,
          textTheme: const TextTheme(
            titleLarge: TextStyle(fontSize: 24),
            bodyLarge: TextStyle(fontSize: 18),
          ),
          elevatedButtonTheme:
              const ElevatedButtonThemeData(style: ButtonStyle())),
      home: MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  @override
  Widget build(BuildContext context) {
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
    return const LoginPage();
  }
}
