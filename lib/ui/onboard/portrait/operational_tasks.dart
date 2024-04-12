/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:percent_indicator/linear_percent_indicator.dart';
import 'package:registration_client/ui/onboard/portrait/task_card.dart';
import 'package:registration_client/ui/onboard/widgets/home_page_card.dart';
import 'package:registration_client/utils/app_config.dart';

class OperationalTasks extends StatefulWidget {
  const OperationalTasks({
    super.key,
    required this.operationalTasks,
  });
  final List<Map<String, dynamic>> operationalTasks;

  @override
  State<OperationalTasks> createState() => _OperationalTasksState();
}

class _OperationalTasksState extends State<OperationalTasks> {
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        SizedBox(
          height: 12.h,
        ),
        _getMemoryProvider(),
        _getTasks(),
      ],
    );
  }

  _getMemoryProvider() {
    return Container(
      // height: 186.h,
      color: appWhite,
      width: ScreenUtil().screenWidth,
      padding: EdgeInsets.only(
        left: 20.w,
        right: 20.w,
        top: 18.h,
        bottom: 30.h,
      ),
      child: Container(
        height: 138.h,
        padding: EdgeInsets.symmetric(
          horizontal: 15.w,
          vertical: 16.h,
        ),
        decoration: BoxDecoration(
          border: Border.all(
            color: appWhite,
          ),
          borderRadius: const BorderRadius.all(
            Radius.circular(6),
          ),
          color: memoryCardColor,
          boxShadow: const [
            BoxShadow(
              color: greyBorderShade,
              offset: Offset(-3.0, -3.0),
              blurRadius: 6.0,
              spreadRadius: 0.0,
            ),
          ],
        ),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            Container(
              height: 83.h,
              width: 83.h,
              decoration: const BoxDecoration(
                color: appWhite,
                shape: BoxShape.circle,
              ),
              child: Transform.scale(
                scale: 0.5,
                child: SvgPicture.asset(
                  syncDataIcon,
                ),
              ),
            ),
            SizedBox(
              width: 18.w,
            ),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'System Storage Usage',
                    style: TextStyle(
                      fontSize: isMobileSize ? 16 : 24,
                      fontWeight: semiBold,
                      color: appBlackShade1,
                    ),
                  ),
                  SizedBox(
                    height: 10.h,
                  ),
                  LinearPercentIndicator(
                    lineHeight: 10.h,
                    percent: 0.72,
                    backgroundColor: appWhite,
                    progressColor: appSolidPrimary,
                    barRadius: const Radius.circular(10),
                  ),
                  SizedBox(
                    height: 10.h,
                  ),
                  Row(
                    children: [
                      Text(
                        'Used: 286 GB (72% used)',
                        style: TextStyle(
                          fontSize: isMobileSize ? 14 : 20,
                          color: const Color(0XFF4E4E4E),
                        ),
                      ),
                      const Expanded(
                        child: SizedBox(),
                      ),
                      !isMobileSize
                          ? Text(
                              'Available: 192 GB',
                              style: TextStyle(
                                fontSize: isMobileSize ? 14 : 20,
                                color: const Color(0XFF4E4E4E),
                              ),
                            )
                          : const SizedBox.shrink()
                    ],
                  ),
                  isMobileSize
                      ? Text(
                          'Available: 192 GB',
                          style: TextStyle(
                            fontSize: isMobileSize ? 14 : 20,
                            color: const Color(0XFF4E4E4E),
                          ),
                        )
                      : const SizedBox.shrink(),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  _getTasks() {
    return ListView(
      shrinkWrap: true,
      // scrollDirection: Axis.vertical,
      physics: const NeverScrollableScrollPhysics(),
      children: List.generate(
        widget.operationalTasks.length,
        (index) {
          return isMobileSize
              ? Padding(
                  padding: EdgeInsets.symmetric(
                    horizontal: 16.w,
                  ),
                  child: HomePageCard(
                    index: index,
                    icon: widget.operationalTasks[index]["icon"],
                    title: widget.operationalTasks[index]["title"] as String,
                    ontap: () async {
                      return widget.operationalTasks[index]["onTap"](context);
                    },
                  ),
                )
              : TaskCard(
                  index: index,
                  icon: widget.operationalTasks[index]["icon"],
                  title: widget.operationalTasks[index]["title"] as String,
                  ontap: () {
                    return widget.operationalTasks[index]["onTap"](context);
                  },
                  subtitle: widget.operationalTasks[index]["subtitle"],
                );
        },
      ),
    );
  }
}
