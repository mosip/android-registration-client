import 'package:flutter/material.dart';
import 'package:registration_client/login_page.dart';
import 'package:registration_client/registration_client.dart';
import 'package:registration_client/ui/onboarding/onboarding_page_1_view.dart';
import 'package:registration_client/ui/onboarding/onboarding_page_2_view.dart';

class AppRouter {
  AppRouter._();

  static Map<String, Widget Function(BuildContext)> routes = {
    LoginPage.route: (context) => const LoginPage(),
    RegistrationClient.route: (context) => RegistrationClient(),
    OnboardingPage1View.route: (context) => const OnboardingPage1View(),
    OnboardingPage2View.route: (context) => const OnboardingPage2View(),
  };

  static Route<dynamic>? onUnknownRoute(RouteSettings settings) {
    return null;
  }
}
