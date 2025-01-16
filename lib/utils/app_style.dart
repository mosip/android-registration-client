/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:registration_client/utils/app_config.dart';

abstract class AppTextStyle {

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
    fontWeight: FontWeight.bold,
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
    fontWeight: FontWeight.bold,
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