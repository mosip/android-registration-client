/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:registration_client/ui/process_ui/lost_process.dart';
import 'package:registration_client/ui/process_ui/correction_process.dart';

import 'package:registration_client/ui/process_ui/new_process.dart';

import 'package:registration_client/ui/login_page.dart';
import 'package:registration_client/ui/onboard/onboard_landing_page.dart';
import 'package:registration_client/ui/onboard/home_page.dart';
import 'package:registration_client/ui/process_ui/update_process.dart';

class AppRouter {
  AppRouter._();

  static Map<String, Widget Function(BuildContext)> routes = {
    LoginPage.route: (context) => const LoginPage(),
    NewProcess.routeName: (context) => const NewProcess(),
    UpdateProcess.routeName: (context) => const UpdateProcess(),
    OnboardLandingPage.route: (context) => const OnboardLandingPage(),
    HomePage.route: (context) => const HomePage(),
    LostProcess.routeName: (context) => const LostProcess(),
    CorrectionProcess.routeName: (context) => const CorrectionProcess(),
  };

  static Route<dynamic>? onUnknownRoute(RouteSettings settings) {
    return null;
  }
}
