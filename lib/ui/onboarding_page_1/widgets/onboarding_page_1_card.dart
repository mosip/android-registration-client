import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/utils/app_config.dart';

class Onboarding_Page_1_Card extends StatelessWidget {
  const Onboarding_Page_1_Card(
      {super.key,
      required this.icon,
      required this.title,
      required this.ontap});
  final String icon;
  final String title;
  final void Function() ontap;

  @override
  Widget build(BuildContext context) {
    double h = ScreenUtil().screenHeight;
    double w = ScreenUtil().screenWidth;
    return Padding(
      padding: EdgeInsets.fromLTRB(0, 16.h, 0, 0),
      child: InkWell(
        onTap: ontap,
        child: Card(
          child: Padding(
            padding: EdgeInsets.fromLTRB(25.w, 25.h, 0, 25.h),
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
                        .bodyLarge
                        ?.copyWith(fontWeight: semiBold),
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
