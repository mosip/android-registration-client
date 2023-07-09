import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

String appIcon = 'assets/images/MOSIP Logo@2x.png';
String appIconLogoOnly = 'assets/images/MOSIP Logo Only.png';
String buildingsX = 'assets/images/buildings.png';
String buildingsXX = 'assets/images/buildings@2x.png';
double appIconScale = 8;
Color solid_primary = Color(0xFF1C43A1);

Color backgroundColor = const Color(0xFFF8FCFF);
Color pure_white = const Color(0xFFFFFFFF);

Color black_shade_1 = const Color(0xFF333333);

List<Color> app_colors = [
  Color(0xFF014DAF),
  Color(0xFFF97707),
  Color(0xFF01A2FD),
  Color(0xFFFEC401)
];

List<Color> secondaryColors = [
  Color(0xFF214FBF),
  Color(0xFF6F6E6E),
  Color(0xFF999999),
  Color(0xFFFAFBFF),
  Color(0xFFF4F7FF),
  Color(0xFF7F7F7F),
  Color(0xFF1A9B42),
  Color(0xFF4E4E4E),
  Color(0xFF0000000),
  Color(0xFF0C0CAE3),
  Color(0xFFFAFBFF),
  Color(0xFF4B9D20),
];
FontWeight regular = FontWeight.w300;
FontWeight bold = FontWeight.bold;
FontWeight semiBold = FontWeight.w500;

// bool get isMobile => ScreenUtil().screenWidth < 750;
// bool get isTablet => ScreenUtil().screenWidth <= 1160;
// bool get isDesktop => ScreenUtil().screenWidth > 1160;

bool get isDesktop => ScreenUtil().screenWidth > 1160;
