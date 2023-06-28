// This is a basic Flutter widget test.
//
// To perform an interaction with a widget in your test, use the WidgetTester
// utility in the flutter_test package. For example, you can send tap and scroll
// gestures. You can also use WidgetTester to find child widgets in the widget
// tree, read text, and verify that the values of widget properties are correct.
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/app_router.dart';
import 'package:registration_client/provider/auth_provider.dart';
import 'package:registration_client/provider/connectivity_provider.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/ui/login_page.dart';
import 'package:registration_client/provider/app_language_provider.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/ui/machine_keys.dart';
import 'package:registration_client/ui/widgets/password_component.dart';
import 'package:registration_client/ui/widgets/username_component.dart';

Widget testableWidget({required Widget child}) {
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
    child: ParentWidget(
      child: child,
    ),
  );
}

class ParentWidget extends StatelessWidget {
  const ParentWidget({super.key, required this.child});
  final Widget child;
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      routes: AppRouter.routes,
      debugShowCheckedModeBanner: false,
      localizationsDelegates: AppLocalizations.localizationsDelegates,
      supportedLocales: AppLocalizations.supportedLocales,
      locale: Provider.of<AppLanguageProvider>(context).appLocal,
      home: Builder(builder: (BuildContext context) {
        return child;
      }),
    );
  }
}

class SizedWidget extends StatelessWidget {
  const SizedWidget({super.key, required this.child});
  final Widget child;
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
    return child;
  }
}

void main() {
  testWidgets("Login Page", (WidgetTester tester) async {
    await tester.pumpWidget(
      testableWidget(
        child: const SizedWidget(
          child: LoginPage(),
        ),
      ),
    );
    expect(find.byType(SafeArea), findsOneWidget);
    expect(find.widgetWithText(InkWell, 'HELP'), findsOneWidget);
    expect(find.text('Welcome to'), findsOneWidget);
    expect(find.text('Community Registration Client!'), findsOneWidget);
    expect(find.text('Please login to access the features.'), findsOneWidget);
    expect(find.widgetWithText(TextField, "Enter Username"), findsOneWidget);
    expect(find.widgetWithText(InkWell, 'NEXT'), findsOneWidget);
    expect(find.byType(Scaffold), findsOneWidget);
    expect(find.byType(SizedBox), findsNWidgets(19));
    expect(find.byType(Container), findsNWidgets(23));
    expect(find.byType(Text), findsNWidgets(13));
    expect(find.byType(InkWell), findsNWidgets(4));
    expect(find.text('Login'), findsOneWidget);
    expect(find.text('Username'), findsOneWidget);
    expect(find.widgetWithText(TextField, "Enter Username"), findsOneWidget);
    expect(find.widgetWithText(InkWell, 'NEXT'), findsOneWidget);
    expect(find.byType(TextField), findsOneWidget);
    expect(find.text('NEXT'), findsOneWidget);
  });

  testWidgets("Username Component", (WidgetTester tester) async {
    await tester.pumpWidget(
      testableWidget(
        child: SizedWidget(
          child: SafeArea(
            child: Scaffold(
              body: UsernameComponent(
                onTap: () {},
                languages: const [
                  'eng',
                  'ara',
                  'fre',
                ],
                mp: const {
                  "eng": "",
                  "ara": "",
                  "fre": "",
                },
                onChanged: (v) {},
                isDisabled: false,
                isMobile: true,
              ),
            ),
          ),
        ),
      ),
    );
    expect(find.byType(SafeArea), findsOneWidget);
    expect(find.widgetWithText(InkWell, 'NEXT'), findsOneWidget);
    expect(find.byType(Scaffold), findsOneWidget);
    expect(find.byType(SizedBox), findsNWidgets(13));
    expect(find.byType(Container), findsNWidgets(12));
    expect(find.byType(Text), findsNWidgets(8));
    expect(find.byType(InkWell), findsNWidgets(2));
    expect(find.text('Language'), findsOneWidget);
    expect(find.text('Username'), findsOneWidget);
    expect(find.widgetWithText(TextField, "Enter Username"), findsOneWidget);
    expect(find.widgetWithText(InkWell, 'NEXT'), findsOneWidget);
    expect(find.byType(TextField), findsOneWidget);
    expect(find.text('NEXT'), findsOneWidget);
  });

  testWidgets("Password Component", (WidgetTester tester) async {
    await tester.pumpWidget(
      testableWidget(
        child: SizedWidget(
            child: SafeArea(
          child: Scaffold(
            body: PasswordComponent(
              onChanged: (v) {},
              onTapBack: () {},
              onTapLogin: () {},
              isDisabled: false,
              isLoggingIn: false,
            ),
          ),
        )),
      ),
    );
    expect(find.byType(SafeArea), findsOneWidget);
    expect(find.byType(Scaffold), findsOneWidget);
    expect(find.text('Password'), findsOneWidget);
    expect(find.byType(Container), findsNWidgets(5));
    expect(find.byType(InkWell), findsNWidgets(3));
    expect(find.byType(Text), findsNWidgets(6));
    expect(find.byType(SizedBox), findsNWidgets(5));
    expect(find.byType(Row), findsNWidgets(1));
    expect(find.byType(Column), findsNWidgets(1));
    expect(find.byType(Center), findsNWidgets(2));
    expect(find.byType(TextField), findsOneWidget);
    expect(find.widgetWithText(TextField, 'Enter Password'), findsOneWidget);
    expect(find.widgetWithText(InkWell, 'LOGIN'), findsOneWidget);
    expect(find.widgetWithText(InkWell, 'BACK'), findsOneWidget);
    expect(find.widgetWithText(InkWell, 'Forgot Password?'), findsOneWidget);
    expect(find.byType(CircularProgressIndicator), findsNothing);
  });

  testWidgets("Machine Keys", (WidgetTester tester) async {
    await tester.pumpWidget(
      testableWidget(
        child: SizedWidget(
          child: MachineKeys(),
        ),
      ),
    );

    expect(find.byType(Scaffold), findsOneWidget);
    expect(find.byType(IconButton), findsOneWidget);
    expect(find.byType(Icon), findsOneWidget);
    expect(find.byType(Container), findsNWidgets(3));
    expect(find.byType(Center), findsNWidgets(3));
    expect(find.byType(Text), findsNWidgets(3));
    expect(find.byType(InkWell), findsNWidgets(2));
    expect(find.text('Copy Text'), findsOneWidget);
    expect(find.text('Download JSON'), findsOneWidget);
    expect(find.text('Device Credentials'), findsOneWidget);
    expect(find.byType(SizedBox), findsNWidgets(5));
    expect(find.byType(SingleChildScrollView), findsOneWidget);
    expect(find.byType(Column), findsOneWidget);
    expect(find.byType(SelectableText), findsOneWidget);
    expect(find.byType(AppBar), findsOneWidget);
  });
}
