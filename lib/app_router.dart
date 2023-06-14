import 'package:flutter/material.dart';
import 'package:registration_client/ui/login_page/login_page.dart';
import 'package:registration_client/ui/onboarding/get_onboard_landing_page.dart';
import 'package:registration_client/ui/onboarding/home_page.dart';
import 'package:registration_client/ui/process_ui/new_process.dart';

class AppRouter {
  AppRouter._();

  static Map<String, Widget Function(BuildContext)> routes = {
    LoginPage.route: (context) => const LoginPage(),
    GetOnboardLandingPageView.route: (context) => const GetOnboardLandingPageView(),
    HomePageView.route: (context) => const HomePageView(),
    NewProcess.routeName: (context) => const NewProcess(),
  };

  static Route<dynamic>? onUnknownRoute(RouteSettings settings) {
    return null;
  }
}
