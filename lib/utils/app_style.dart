import 'package:flutter/material.dart';
import 'package:registration_client/utils/app_config.dart';

abstract class AppStyle {
  static const Color appWhite = Color(0xFFffffff);
  static const Color appBlack = Color(0xFF000000);
  static const Color appBlackShade1 = Color(0xFF333333);
  static const Color appBlackShade2 = Color(0xFF6F6E6E);
  static const Color appBlackShade3 = Color(0xFF999999);
  static const Color appGreyShade = Color(0xFF9B9B9F);
  static const Color appSolidPrimary = Color(0xFF1C43A1);
  static const Color appBlueShade1 = Color(0xFF214FBF);
  static const Color appInfoText = Color(0xFFB1C8FF);
  static const Color appButtonBorderText = Color(0xFF1C43A2);
  static const Color appYellow = Color(0xFFFEC401);
  static const Color appOrange = Color(0xFFF97707);
  static const Color appBackButtonBorder = Color(0xFF2A4EA7);
  static const Color buttonDisabled = Color(0XFFCCCCCC);
  static const Color mandatoryField = Color(0XFFD32D2D);
  static const Color previewHeaderColor = Color(0XFF666666);
  static const Color previewHeaderComponentColor = Color(0XFFF5F5F5);
  static const Color authIconBackground = Color(0XFFF8FCFF);
  static const Color authIconBorder = Color(0XFFE1EDF5);
  static const Color dividerColor = Color(0XFFE5EBFA);
  static const Color languageSelectedColor = Color(0XFF1C429F);
  static const Color languageFreezedColor = Color(0XFFD9E3FF);
  static const Color appBlueShade = Color(0XFFFAFBFF);
  static const Color greyBorderShade = Color(0XFFEAEAEA);
  static const Color dropShadow = Color(0X0000000F);
  static const Color iconContainerColor = Color(0XFFF4F7FF);
  static const Color memoryCardColor = Color(0XFFF0F5FF);
  static const Color appRed = Color(0xFFBE1B1B);
  static const Color dropDownSelector = Color(0xFF1C429F);
  static const Color dropDownDividerColor = Color(0xFFBDCDF4);

  static TextStyle mobileHelpText = const TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 14,
    color: appWhite,
    letterSpacing: 0.5,
  );

  static TextStyle mobileHeaderText = const TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 28,
    color: appBlackShade1,
    letterSpacing: 0.5,
  );

  static TextStyle mobileTextfieldHeader = const TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 14,
    color: appBlackShade1,
    letterSpacing: 0.5,
  );

  static TextStyle mobileDropdownText = const TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 14,
    color: appBlackShade1,
    letterSpacing: 0.5,
  );

  static TextStyle mobileTextfieldHintText = const TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 14,
    color: appBlackShade3,
    letterSpacing: 0.5,
  );

  static TextStyle mobileDropdownHintText = const TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 15,
    color: appBlackShade3,
    letterSpacing: 0.5,
  );

  static TextStyle mobileButtonText = const TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 14,
    color: appWhite,
    letterSpacing: 0.5,
  );

  static TextStyle mobileWelcomeText = const TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 18,
    color: appWhite,
    letterSpacing: 0.5,
  );
  
  static TextStyle mobileCommunityRegClientText = const TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 18,
    color: appWhite,
    letterSpacing: 0.5,
  );

  static TextStyle mobileInfoText = const TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 14,
    color: appInfoText,
    letterSpacing: 0.5,
  );

  static TextStyle mobileForgotPasswordText = const TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 14,
    color: appSolidPrimary,
    letterSpacing: 0.5,
  );

  static TextStyle mobileBackButtonText = const TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 14,
    color: appButtonBorderText,
    letterSpacing: 0.5,
  );

  static TextStyle tabletHelpText = const TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 14,
    color: appButtonBorderText,
    letterSpacing: 0,
  );

  static TextStyle tabletHeaderText = const TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 28,
    color: appBlackShade1,
    letterSpacing: 0,
  );

  static TextStyle tabletTextfieldHeader = const TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 14,
    color: appBlackShade1,
    letterSpacing: 0,
  );

  static TextStyle tabletDropdownText = const TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 14,
    color: appBlackShade1,
    letterSpacing: 0,
  );

  static TextStyle tabletTextfieldHintText = const TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 14,
    color: appBlackShade3,
    letterSpacing: 0,
  );

  static TextStyle tabletButtonText = const TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 14,
    color: appWhite,
    letterSpacing: 0,
  );

  static TextStyle tabletWelcomeText = const TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 26,
    color: appWhite,
    letterSpacing: 0,
  );

  static TextStyle tabletCommunityRegClientText = const TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 26,
    color: appWhite,
    letterSpacing: 0,
  );

  static TextStyle tabletInfoText = const TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 16,
    color: appInfoText,
    letterSpacing: 0,
  );

  static TextStyle tabletForgotPasswordText = const TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 14,
    color: appSolidPrimary,
    letterSpacing: 0,
  );

  static TextStyle tabletBackButtonText = const TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 14,
    color: appButtonBorderText,
    letterSpacing: 0,
  );

  static TextStyle previewHeaderText = TextStyle(
    fontWeight: regular,
    fontSize: 14,
    color: previewHeaderColor,
  );

  static TextStyle previewHeaderResponseText = TextStyle(
    fontWeight: semiBold,
    fontSize: 16,
    color: appBlackShade1,
  );

  static TextStyle previewComponentHeaderText = TextStyle(
    fontWeight: semiBold,
    fontSize: 20,
    color: appBlack
  );

  static TextStyle primaryButtonText = TextStyle(
    fontWeight: bold,
    fontSize: 21,
    color: appWhite
  );

  static TextStyle secondaryButtonText = TextStyle(
    fontWeight: bold,
    fontSize: 21,
    color: appButtonBorderText
  );

  static TextStyle primaryButtonTextSmall = TextStyle(
    fontWeight: bold,
    fontSize: 16,
    color: appWhite
  );

  static TextStyle secondaryButtonTextSmall = TextStyle(
    fontWeight: bold,
    fontSize: 16,
    color: appButtonBorderText
  );

  static TextStyle tabletPortraitHelpText = const TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 22,
    color: appWhite,
    letterSpacing: 0.5,
  );

  static TextStyle tabletPortraitHeaderText = const TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 36,
    color: appBlackShade1,
    letterSpacing: 0.5,
  );

  static TextStyle tabletPortraitTextfieldHeader = const TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 22,
    color: appBlackShade1,
    letterSpacing: 0.5,
  );

  static TextStyle tabletPortraitDropdownText = const TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 22,
    color: appBlackShade1,
    letterSpacing: 0.5,
  );

  static TextStyle tabletPortraitTextfieldHintText = const TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 22,
    color: appBlackShade3,
    letterSpacing: 0.5,
  );

  static TextStyle tabletPortraitDropdownHintText = const TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 22,
    color: appBlackShade3,
    letterSpacing: 0.5,
  );

  static TextStyle tabletPortraitButtonText = const TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 22,
    color: appWhite,
    letterSpacing: 0.5,
  );

  static TextStyle tabletPortraitWelcomeText = const TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 36,
    color: appWhite,
    letterSpacing: 0.5,
  );
  
  static TextStyle tabletPortraitCommunityRegClientText = const TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 36,
    color: appWhite,
    letterSpacing: 0.5,
  );

  static TextStyle tabletPortraitInfoText = const TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 26,
    color: appInfoText,
    letterSpacing: 0.5,
  );

  static TextStyle tabletPortraitForgotPasswordText = const TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 22,
    color: appSolidPrimary,
    letterSpacing: 0.5,
  );

  static TextStyle tabletPortraitBackButtonText = const TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 22,
    color: appButtonBorderText,
    letterSpacing: 0.5,
  );
}