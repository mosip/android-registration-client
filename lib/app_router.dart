import 'package:flutter/material.dart';
import 'package:registration_client/login_page.dart';
import 'package:registration_client/ui/onboarding/get_onboard_landing_page.dart';
import 'package:registration_client/ui/onboarding/home_page.dart';

class AppRouter {
  AppRouter._();

  static Map<String, Widget Function(BuildContext)> routes = {
    LoginPage.route: (context) => const LoginPage(),
    GetOnboardLandingPageView.route: (context) => const GetOnboardLandingPageView(),
    HomePageView.route: (context) => const HomePageView(),
  };

  static Route<dynamic>? onUnknownRoute(RouteSettings settings) {
    return null;
  }
}
