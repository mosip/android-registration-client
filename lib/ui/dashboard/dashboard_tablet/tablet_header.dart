import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import '../../../utils/app_config.dart';

class TabletHeader extends StatelessWidget {
  const TabletHeader({super.key});

  @override
  Widget build(BuildContext context) {
    double w = ScreenUtil().screenWidth;
    bool isLandscape =
        MediaQuery.of(context).orientation == Orientation.landscape;

    return Container(
      decoration: const BoxDecoration(
        border: Border(
          bottom: BorderSide(
            color: Color(0xffE5EBFA),
          ),
        ),
        color: Color(0xffFAFBFF),
      ),
      width: w,
      height: 50.h,
      child: Padding(
        padding: EdgeInsets.fromLTRB(0, 11.94.h, 80.w, 13.06.h),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.end,
          crossAxisAlignment: CrossAxisAlignment.end,
          children: [
            Icon(
              Icons.location_pin,
              color: solid_primary,
              size: 16,
            ),
            SizedBox(
              width: (isLandscape) ? 7.85.w : 1.96.w,
            ),
            Text(
              "Center Souissi",
              style: Theme.of(context).textTheme.titleSmall?.copyWith(
                  color: const Color(0xff333333), fontWeight: semiBold),
            ),
            SizedBox(
              width: (isLandscape) ? 44.w : 11.w,
            ),
            const Icon(
              Icons.circle,
              color: Color(0xff1A9B42),
              size: 12,
            ),
            SizedBox(
              width: (isLandscape) ? 10.w : 2.5.w,
            ),
            Icon(
              Icons.desktop_mac_outlined,
              color: solid_primary,
              size: 16,
            ),
            SizedBox(
              width: (isLandscape) ? 7.w : 1.75.w,
            ),
            Text(
              "M1HSNDS590",
              style: Theme.of(context).textTheme.titleSmall?.copyWith(
                  color: const Color(0xff333333), fontWeight: semiBold),
            ),
          ],
        ),
      ),
    );
  }
}
