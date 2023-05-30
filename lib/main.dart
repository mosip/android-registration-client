/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/app_router.dart';
import 'package:registration_client/login_page.dart';

import 'package:registration_client/provider/app_language.dart';
import 'package:registration_client/provider/connectivity_provider.dart';
import 'package:registration_client/registration_client.dart';
import 'package:provider/provider.dart';
import 'package:flutter_localizations/flutter_localizations.dart';

import 'package:provider/provider.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/ui/dashboard/dashboard_view_model.dart';

import 'package:registration_client/utils/app_config.dart';

import 'ui/dashboard/dashboard_mobile/dashboard_mobile.dart';
import 'ui/dashboard/dashboard_tablet/dashboard_tablet_view.dart';
import 'utils/responsive.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  final AppLanguage appLanguage = AppLanguage();
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
          create: (_) => AppLanguage(),
        ),
        ChangeNotifierProvider(
          lazy: false,
          create: (_) => ConnectivityProvider(),
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
      locale: Provider.of<AppLanguage>(context).appLocal,
      theme: ThemeData(
          colorScheme: ColorScheme.light(primary: solid_primary),
          primaryColor: solid_primary,
          textTheme: const TextTheme(
            titleLarge: TextStyle(fontSize: 24),
            bodyLarge: TextStyle(fontSize: 18),
            bodyMedium: TextStyle(fontSize: 10),
          ),
          elevatedButtonTheme:
              const ElevatedButtonThemeData(style: ButtonStyle())),
      home: ChangeNotifierProvider<DashboardViewModel>(
        create: (context) => DashboardViewModel(),
        builder: (context, _) => MyHomePage(),
      ),
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
    return const LoginPage();
    //   Responsive(
    //   mobile: DashBoardMobileView(),
    //   desktop: DashBoardTabletView(),
    //   tablet: DashBoardTabletView(),
    // );
  }
}
