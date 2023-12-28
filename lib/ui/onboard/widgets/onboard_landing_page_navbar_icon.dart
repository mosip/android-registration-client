import 'package:flutter/material.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';

class OnboardLandingPageNavbarIcon extends StatelessWidget {
  const OnboardLandingPageNavbarIcon({
    super.key,
    required this.icon,
    required this.title,
    required this.ontap,
    required this.imagePath,
  });
  final Widget icon;
  final Widget title;
  final void Function() ontap;
  final String imagePath;

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: ontap,
      child: SizedBox(
        height: 56.h,
        child: Column(
          children: [
            SvgPicture.asset(imagePath),
            SizedBox(
              height: 5.h,
            ),
            title,
          ],
        ),
      ),
    );
  }
}
