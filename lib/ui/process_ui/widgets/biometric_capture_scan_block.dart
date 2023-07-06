import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:percent_indicator/percent_indicator.dart';
import 'package:registration_client/utils/app_config.dart';

class BiometricCaptureScanBlock extends StatelessWidget {
  const BiometricCaptureScanBlock(
      {super.key,
      required this.title,
      required this.images,
      required this.thresholdPercentage});
  final String title;
  final List<String> images;
  final int thresholdPercentage;

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 547.h,
      width: 370.h,
      decoration: BoxDecoration(
          color: secondaryColors.elementAt(3),
          borderRadius: BorderRadius.circular(10),
          border: Border.all(color: secondaryColors.elementAt(13), width: 1)),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          Padding(
            padding: EdgeInsets.fromLTRB(0, 16, 0, 36),
            child: Text(
              title,
              style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                  fontSize: 18, fontWeight: semiBold, color: black_shade_1),
            ),
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              ...images.map(
                (e) => Container(
                    height: 164.h,
                    width: 164.h,
                    decoration: BoxDecoration(
                      color: pure_white,
                      border: Border.all(
                        color: secondaryColors.elementAt(14),
                      ),
                    ),
                    child: Padding(
                      padding: EdgeInsets.symmetric(
                          vertical: 48.h, horizontal: 27.h),
                      child: Image.asset(
                        e,
                      ),
                    )),
              ),
            ],
          ),
          OutlinedButton.icon(
            onPressed: () {},
            icon: Icon(
              Icons.crop_free,
              color: solid_primary,
              size: 14,
            ),
            label: Text(
              "SCAN",
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  fontSize: 14, fontWeight: bold, color: solid_primary),
            ),
            style: OutlinedButton.styleFrom(
              side: BorderSide(color: solid_primary, width: 1),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(5),
              ),
            ),
          ),
          Container(
            height: 157.h,
            width: 338.h,
            decoration: BoxDecoration(
              color: pure_white,
              border: Border.all(
                color: secondaryColors.elementAt(14),
              ),
            ),
            child: Column(
              children: [
                SizedBox(
                  height: 20.h,
                ),
                Text(
                  "Quality",
                  style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                      fontSize: 14, fontWeight: semiBold, color: black_shade_1),
                ),
                SizedBox(
                  height: 42.h,
                ),
                Text(
                  "Threshold $thresholdPercentage%",
                  style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                        fontSize: 14,
                        fontWeight: regular,
                        color: secondaryColors.elementAt(1),
                      ),
                ),
                SizedBox(
                  height: 16.h,
                ),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    LinearPercentIndicator(
                      width: 260.h,
                      lineHeight: 8,
                      percent: 0.0,
                      backgroundColor: Colors.grey,
                      progressColor: secondaryColors.elementAt(11),
                    ),
                    SizedBox(
                      width: 16.h,
                    ),
                    Text(
                      "0%",
                      style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                            fontSize: 14,
                            fontWeight: regular,
                            color: black_shade_1,
                          ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
