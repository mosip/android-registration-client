// @dart=2.9

/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import 'package:registration_client/login_page.dart';
import 'package:registration_client/provider/app_language.dart';
import 'package:registration_client/registration_client.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/test.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/ui/onboarding_page_1/onboarding_page_1_view.dart';
import 'package:registration_client/utils/app_config.dart';

void main() async {
  await ScreenUtil.ensureScreenSize();
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
      ],
      child: BuildApp(),
    );
  }

  Future<void> _callAppComponent() async {
    await platform.invokeMethod("callComponent");
  }
}

class BuildApp extends StatelessWidget {
  const BuildApp({Key key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      debugShowCheckedModeBanner: false,
      localizationsDelegates: AppLocalizations.localizationsDelegates,
      supportedLocales: AppLocalizations.supportedLocales,
      locale: Provider.of<AppLanguage>(context).appLocal,
      theme: ThemeData(
          colorScheme: ColorScheme.light(primary: solid_primary),
          primaryColor: solid_primary,
          textTheme: TextTheme(
            titleLarge: TextStyle(fontSize: 24),
            bodyLarge: TextStyle(fontSize: 18),
            bodyMedium: TextStyle(fontSize: 10),
          ),
          elevatedButtonTheme: ElevatedButtonThemeData(style: ButtonStyle())),
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
    return OnboardingPage1View();
  }
}
