// @dart=2.9

// This is a basic Flutter widget test.
//
// To perform an interaction with a widget in your test, use the WidgetTester
// utility in the flutter_test package. For example, you can send tap and scroll
// gestures. You can also use WidgetTester to find child widgets in the widget
// tree, read text, and verify that the values of widget properties are correct.

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:registration_client/main.dart';
import 'package:registration_client/test.dart';

void main() {
  testWidgets('Find Help Button', (WidgetTester tester) async {
    // Build our app and trigger a frame.
    await tester.pumpWidget(MyApp());

    var helpButton = find.widgetWithText(InkWell, "HELP");
    expect(helpButton, findsOneWidget);
  });

  testWidgets('Find Welcome Text', (WidgetTester tester) async {
    await tester.pumpWidget(MyApp());

    var welcomeText = find.text('Welcome to');
    expect(welcomeText, findsOneWidget);
  });

  testWidgets('Find Community Text', (WidgetTester tester) async {
    await tester.pumpWidget(MyApp());

    var communityText = find.text('Community Registration Client!');
    expect(communityText, findsOneWidget);
  });

  testWidgets('Find Info Text', (WidgetTester tester) async {
    await tester.pumpWidget(MyApp());

    var infoText = find.text('Please login to access the features.');
    expect(infoText, findsOneWidget);
  });

  testWidgets('Login Component', (WidgetTester tester) async {
    await tester.pumpWidget(MyApp());

    var loginText = find.text('Login');
    expect(loginText, findsOneWidget);

    var usernameText = find.text('Username');
    expect(usernameText, findsOneWidget);

    var usernameTF = find.widgetWithText(TextField, "Enter Username");
    expect(usernameTF, findsOneWidget);

    var nextButton = find.widgetWithText(InkWell, "NEXT");
    expect(nextButton, findsOneWidget);

    await tester.tap(find.text("NEXT"));
    await tester.pumpAndSettle();

    // expect(usernameText, findsNothing);
  });



}
