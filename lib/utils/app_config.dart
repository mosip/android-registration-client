import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

String appIcon = 'assets/images/MOSIP Logo@2x.png';
String appIconLogoOnly = 'assets/images/MOSIP Logo Only.png';
String buildingsX = 'assets/images/buildings.png';
String buildingsXX = 'assets/images/buildings@2x.png';
double appIconScale = 8;
String dashboardIcon = 'assets/svg/Dashboard.svg';
String notificationIcon = 'assets/svg/Notifications.svg';
String settingsIcon = 'assets/svg/Settings.svg';
String mosipLogo = 'assets/svg/MOSIP Logo.svg';
String syncDataIcon = 'assets/svg/Synchronising Data.svg';
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
