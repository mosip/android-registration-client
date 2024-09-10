/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

String appIcon = 'assets/images/zambia Logo.png';
String appIconLogoOnly = 'assets/images/zambia Logo.png';
String buildingsX = 'assets/images/buildings.png';
String buildingsXX = 'assets/images/zambia-background-image.jpg';
double appIconScale = 8;
String dashboardIcon = 'assets/svg/Dashboard.svg';
String notificationIcon = 'assets/svg/Notifications.svg';
String settingsIcon = 'assets/svg/Settings.svg';
String mosipLogo = 'assets/svg/MOSIP Logo.svg';
String syncDataIcon = 'assets/svg/Synchronising Data.svg';
String dashboardSelectedIcon = 'assets/svg/DashboardSelected.svg';
String settingsSelectedIcon = 'assets/svg/SettingsSelected.svg';
String profileIcon = 'assets/svg/ProfileIcon.svg';
String profileSelectedIcon = 'assets/svg/ProfileSelected.svg';
Color solidPrimary = const Color(0xFF1C43A1);

Color backgroundColor = const Color(0xFFF8FCFF);
Color pureWhite = const Color(0xFFFFFFFF);

Color blackShade1 = const Color(0xFF333333);

List<Color> appColors = [
  const Color(0xFF014DAF),
  const Color(0xFFF97707),
  const Color(0xFF01A2FD),
  const Color(0xFFFEC401)
];

const Color appWhite = Color(0xFFffffff);
const Color appBlack = Color(0xFF000000);
const Color appBlackShade1 = Color(0xFF333333);
const Color appBlackShade2 = Color(0xFF6F6E6E);
const Color appBlackShade3 = Color(0xFF999999);
const Color appGreyShade = Color(0xFF9B9B9F);
const Color appSolidPrimary = Color(0xFF1C43A1);
const Color appBlueShade1 = Color(0xFF214FBF);
const Color appInfoText = Color(0xFFB1C8FF);
const Color appButtonBorderText = Color(0xFF1C43A2);
const Color appYellow = Color(0xFFFEC401);
const Color appOrange = Color(0xFFF97707);
const Color appBackButtonBorder = Color(0xFF2A4EA7);
const Color buttonDisabled = Color(0XFFCCCCCC);
const Color mandatoryField = Color(0XFFD32D2D);
const Color previewHeaderColor = Color(0XFF666666);
const Color previewHeaderComponentColor = Color(0XFFF5F5F5);
const Color authIconBackground = Color(0XFFF8FCFF);
const Color authIconBorder = Color(0XFFE1EDF5);
const Color dividerColor = Color(0XFFE5EBFA);
const Color languageSelectedColor = Color(0XFF1C429F);
const Color languageFreezedColor = Color(0XFFD9E3FF);
const Color appBlueShade = Color(0XFFFAFBFF);
const Color greyBorderShade = Color(0XFFEAEAEA);
const Color dropShadow = Color(0X0000000F);
const Color iconContainerColor = Color(0XFFF4F7FF);
const Color memoryCardColor = Color(0XFFF0F5FF);
const Color appRed = Color(0xFFBE1B1B);
const Color dropDownSelector = Color(0xFF1C429F);
const Color dropDownDividerColor = Color(0xFFBDCDF4);
const Color dashBoardPacketUploadColor = Color(0xFF4B9B21);
const Color dashBoardPacketUploadPendingColor = Color(0xFFE5961A);
const Color dashBoardPacketUploadExceptionColor = Color(0xFFB71D1D);
const Color logoutButtonColor = Color(0xFFC70000);
const Color bottomBarSelectedColor = Color(0xFFEFF4FF);
const Color bulletPointColor = Color(0xFF656565);

List<Color> secondaryColors = [
  const Color(0xFF214FBF),
  const Color(0xFF6F6E6E),
  const Color(0xFF999999),
  const Color(0xFFFAFBFF),
  const Color(0xFFF4F7FF),
  const Color(0xFF7F7F7F),
  const Color(0xFF1A9B42),
  const Color(0xFF4E4E4E),
  const Color(0xFF000000),
  const Color(0xFFC0CAE3),
  const Color(0xFFFAFBFF),
  const Color(0xFF4B9D20),
  const Color(0xFF1C429F),
  const Color(0xFFE1EDF5),
  const Color(0xFFE5EBFA),
  const Color(0xFFFF0000),
  const Color(0xFFF29D1C),
  const Color(0xFFE0E0E0),
  const Color(0xFFF8F8F8),
  const Color(0xFF848484),
  const Color(0xFFC11717),
  const Color(0xFFF7F7F7),
  const Color(0xFFCCCCCC),
  const Color(0xFFFFF7EB),
  const Color(0xFFA8781E),
  const Color(0xFFFF0202),
  const Color(0xFFC4102B),
];
FontWeight regular = FontWeight.w300;
FontWeight bold = FontWeight.bold;
FontWeight semiBold = FontWeight.w500;

/* For 1.1.5 configuration, the screen headers will be saved in the 
global params for all the below languages. Other languages can also 
be added here. */
List<String> lang = [
  "eng",
  "ara",
  "fra",
];

// bool get isMobile => ScreenUtil().screenWidth < 750;
// bool get isTablet => ScreenUtil().screenWidth <= 1160;
// bool get isDesktop => ScreenUtil().screenWidth > 1160;

bool get isDesktop => ScreenUtil().screenWidth > 1160;
bool get isMobileSize => ScreenUtil().screenWidth < 750;
