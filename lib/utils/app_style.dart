import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

abstract class AppStyle {
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
  static const Color appBackButtonBorder = Color(0xFF2A4EA7);
  static const Color buttonDisabled = Color(0XFFCCCCCC);
  static const Color mandatoryField = Color(0XFFD32D2D);

  static TextStyle mobileHelpText = TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 14.spMin,
    color: appHelpText,
    letterSpacing: 0,
  );

  static TextStyle mobileHeaderText = TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 28.spMin,
    color: appBlackShade1,
    letterSpacing: 0,
  );

  static TextStyle mobileTextfieldHeader = TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 14.spMin,
    color: appBlackShade1,
    letterSpacing: 0,
  );

  static TextStyle mobileDropdownText = TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 14.spMin,
    color: appBlackShade1,
    letterSpacing: 0,
  );

  static TextStyle mobileTextfieldHintText = TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 14.spMin,
    color: appBlackShade3,
    letterSpacing: 0,
  );

  static TextStyle mobileButtonText = TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 14.spMin,
    color: appWhite,
    letterSpacing: 0,
  );

  static TextStyle mobileWelcomeText = TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 18.spMin,
    color: appWhite,
    letterSpacing: 0,
  );
  
  static TextStyle mobileCommunityRegClientText = TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 18.spMin,
    color: appWhite,
    letterSpacing: 0,
  );

  static TextStyle mobileInfoText = TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 14.spMin,
    color: appInfoText,
    letterSpacing: 0,
  );

  static TextStyle mobileForgotPasswordText = TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 14.spMin,
    color: appSolidPrimary,
    letterSpacing: 0,
  );

  static TextStyle mobileBackButtonText = TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 14.spMin,
    color: appHelpText,
    letterSpacing: 0,
  );

  static TextStyle tabletHelpText = TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 14.spMin,
    color: appHelpText,
    letterSpacing: 0,
  );

  static TextStyle tabletHeaderText = TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 28.spMin,
    color: appBlackShade1,
    letterSpacing: 0,
  );

  static TextStyle tabletTextfieldHeader = TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 14.spMin,
    color: appBlackShade1,
    letterSpacing: 0,
  );

  static TextStyle tabletDropdownText = TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 14.spMin,
    color: appBlackShade1,
    letterSpacing: 0,
  );

  static TextStyle tabletTextfieldHintText = TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 14.spMin,
    color: appBlackShade3,
    letterSpacing: 0,
  );

  static TextStyle tabletButtonText = TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 14.spMin,
    color: appWhite,
    letterSpacing: 0,
  );

  static TextStyle tabletWelcomeText = TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 26.spMin,
    color: appWhite,
    letterSpacing: 0,
  );

  static TextStyle tabletCommunityRegClientText = TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 26.spMin,
    color: appWhite,
    letterSpacing: 0,
  );

  static TextStyle tabletInfoText = TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 16.spMin,
    color: appInfoText,
    letterSpacing: 0,
  );

  static TextStyle tabletForgotPasswordText = TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 14.spMin,
    color: appSolidPrimary,
    letterSpacing: 0,
  );

  static TextStyle tabletBackButtonText = TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 14.spMin,
    color: appHelpText,
    letterSpacing: 0,
  );
}