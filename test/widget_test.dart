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
import 'package:registration_client/ui/machine_keys.dart';
import 'package:registration_client/ui/login_page.dart';
import 'package:registration_client/main.dart';
import 'package:registration_client/provider/app_language_provider.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/ui/widgets/password_component.dart';
import 'package:registration_client/ui/widgets/username_component.dart';
Widget testableWidget({required Widget child}) {
  return MultiProvider(
    providers: [
      ChangeNotifierProvider(
        lazy: false,
        create: (_) => AppLanguageProvider(),
      ),
    ],
    child: TestWidget(
      child: child,
    ),
  );
}
class TestWidget extends StatelessWidget {
  const TestWidget({ required this.child});
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
class TestWidget2 extends StatelessWidget {
  const TestWidget2({ required this.child});
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
  testWidgets("Username Component", (WidgetTester tester) async {
    await tester.pumpWidget(
      testableWidget(
        child: const TestWidget2(
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
    expect(find.byType(SafeArea), findsOneWidget);
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
  testWidgets("Password Component", (WidgetTester tester) async {
    await tester.pumpWidget(
      testableWidget(
        child: TestWidget2(
          child: Scaffold(
            body: PasswordComponent(
              onChanged: (v) {},
              onTapBack: () {},
              onTapLogin: () {},
              isDisabled: false,
              isLoggingIn: false,
            ),
          )
        ),
      ),
    );
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
}