/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/utils/app_config.dart';

class TaskCard extends StatefulWidget {
  const TaskCard({
    super.key,
    required this.icon,
    required this.title,
    required this.index,
    required this.ontap,
    required this.subtitle,
  });

  final Widget icon;
  final String title;
  final int index;
  final Function ontap;
  final String subtitle;

  @override
  State<TaskCard> createState() => _TaskCardState();
}

class _TaskCardState extends State<TaskCard> {
  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 10.0,
      color: appWhite,
      margin: EdgeInsets.symmetric(
        horizontal: 20.w,
        vertical: 10.h,
      ),
      child: SizedBox(
        height: 110.h,
        child: ListTile(
          onTap: (){widget.ontap();},
          contentPadding: EdgeInsets.only(
            top: 15.h,
            bottom: 17.h,
            left: 15.w,
            right: 15.w,
          ),
          leading: Container(
            padding: EdgeInsets.all(10.w),
            height: 78.h,
            width: 78.h,
            decoration: BoxDecoration(
                color: const Color(0xffF4F7FF),
                borderRadius: BorderRadius.circular(8)),
            child: widget.icon,
            // child: const Text("Y"),
          ),
          title: Text(
            widget.title,
            style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  fontWeight: semiBold,
                  fontSize: 24,
                  color: appBlackShade1,
                ),
          ),
          subtitle: Padding(
            padding: const EdgeInsets.only(top: 4),
            child: Text(
              widget.subtitle,
              style: const TextStyle(
                fontSize: 18,
                color: appBlackShade2,
              ),
            ),
          ),
        ),
      ),
    );
  }
}
