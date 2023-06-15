import 'package:flutter/material.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/utils/app_config.dart';

class OnboardLandingPageCard extends StatelessWidget {
  const OnboardLandingPageCard(
      {super.key,
      required this.icon,
      required this.title,
      required this.ontap});
  final String icon;
  final String title;
  final void Function() ontap;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.fromLTRB(0, 12.h, 0, 0),
      child: InkWell(
        onTap: ontap,
        child: Card(
          child: Padding(
            padding: EdgeInsets.fromLTRB(18.w, 18.h, 0, 18.h),
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Container(
                  height: 50.h,
                  width: 50.w,
                  color: secondaryColors.elementAt(4),
                  child: Image.asset(icon),
                ),
                SizedBox(
                  width: 10.w,
                ),
                Flexible(
                  child: Text(
                    "${title}",
                    style: Theme.of(context)
                        .textTheme
                        .bodySmall
                        ?.copyWith(fontWeight: FontWeight.w900, fontSize: 14),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
