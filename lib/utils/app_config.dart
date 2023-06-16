import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

String appIcon='assets/images/MOSIP Logo@2x.png';
String buildingsX = 'assets/images/buildings.png';
String buildingsXX = 'assets/images/buildings@2x.png';
double appIconScale=8;
Color solid_primary = Color(0xFF1C43A1);

Color backgroundColor=Color(0xFFF8FCFF);
Color pure_white=Color(0xFFFFFFFF);

Color black_shade_1 = Color(0xFF333333);
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
  
];
FontWeight regular=FontWeight.w300;
FontWeight bold=FontWeight.bold;
FontWeight semiBold=FontWeight.w500;

bool get isMobile => ScreenUtil().screenWidth < 750;
bool get isTablet => ScreenUtil().screenWidth <= 1160;

bool get isDesktop => ScreenUtil().screenWidth > 1160;