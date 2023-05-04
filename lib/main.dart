// @dart=2.9

/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/app_config.dart';
import 'package:registration_client/login_page.dart';
import 'package:registration_client/registration_client.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:provider/provider.dart';


void main() {
 
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp();

  // This widget is the root of your application.
  static const platform = MethodChannel('com.flutter.dev/keymanager.test-machine');
  
  @override
  Widget build(BuildContext context) {
    WidgetsBinding.instance.addPostFrameCallback((timeStamp) {
      _callAppComponent();
    });
    return MultiProvider(
      providers: [
          ChangeNotifierProvider(
              lazy: false, create: (context) => GlobalProvider())
        ],
      child: MaterialApp(
        title: 'Flutter Demo',
        theme: ThemeData(
          colorScheme: ColorScheme.light(primary: primarySolidColor1),
            primaryColor: primarySolidColor1,
        ),
        home: const LoginPage(),
      ),
    );
  }
  Future<void> _callAppComponent() async {
    await platform.invokeMethod("callComponent");
  }
}