import 'package:flutter/material.dart';

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
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          SvgPicture.asset(imagePath),
          title,
        ],
      ),
    );
  }
}
