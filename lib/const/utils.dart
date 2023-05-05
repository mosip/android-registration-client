import 'package:flutter/material.dart';

abstract class Utils {
  static const Color appWhite = Color(0xFFffffff);
  static const Color appBlack = Color(0xFF000000);
  static const Color appBlackShade1 = Color(0xFF333333);
  static const Color appBlackShade3 = Color(0xFF999999);
  static const Color appGreyShade = Color(0xFF9B9B9F);
  static const Color appSolidPrimary = Color(0xFF1C43A1);
  static const Color appBlueShade1 = Color(0xFF214FBF);
  static const Color appInfoText = Color(0xFFB1C8FF);
  static const Color appHelpText = Color(0xFF1C43A2);
  static const Color appYellow = Color(0xFFFEC401);
  static const Color appOrange = Color(0xFFF97707);
  static const appBackButtonBorder = Color(0xFF2A4EA7);

  static const TextStyle helpText = TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 14,
    color: appHelpText,
    letterSpacing: 0,
  );

  static const TextStyle headerText = TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 28,
    color: appBlackShade1,
    letterSpacing: 0,
  );

  static const TextStyle textfieldHeader = TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 14,
    color: appBlackShade1,
    letterSpacing: 0,
  );

  static const TextStyle dropdownText = TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 14 ,
    color: appBlackShade1,
    letterSpacing: 0,
  );

  static const TextStyle textfieldHintText = TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 14,
    color: appBlackShade3,
    letterSpacing: 0,
  );

  static const TextStyle buttonText = TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 14,
    color: appWhite,
    letterSpacing: 0,
  );

  static const TextStyle welcomeText = TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 18,
    color: appWhite,
    letterSpacing: 0,
  );
  
  static const TextStyle communityRegClientText = TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 18,
    color: appWhite,
    letterSpacing: 0,
  );

  static const TextStyle infoText = TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 14,
    color: appInfoText,
    letterSpacing: 0,
  );

  static const TextStyle forgotPasswordText = TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 14,
    color: appSolidPrimary,
    letterSpacing: 0,
  );

  static const TextStyle backButtonText = TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 14,
    color: appHelpText,
    letterSpacing: 0,
  );
}