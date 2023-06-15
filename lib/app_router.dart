import 'package:flutter/material.dart';

import 'package:registration_client/ui/process_ui/new_process.dart';

import 'package:registration_client/ui/login_page.dart';
import 'package:registration_client/ui/onboard/onboard_landing_page.dart';
import 'package:registration_client/ui/onboard/home_page.dart';

class AppRouter {
  AppRouter._();

  static Map<String, Widget Function(BuildContext)> routes = {
    LoginPage.route: (context) => const LoginPage(),
    NewProcess.routeName: (context) => const NewProcess(),
    OnboardLandingPage.route: (context) => const OnboardLandingPage(),
    HomePage.route: (context) => const HomePage(),
  };

  static Route<dynamic>? onUnknownRoute(RouteSettings settings) {
    return null;
  }
}
