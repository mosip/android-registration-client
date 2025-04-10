/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';

import 'package:registration_client/utils/app_config.dart';

import '../../../provider/sync_provider.dart';

class HomePageCard extends StatefulWidget {
  const HomePageCard({
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
  final String? subtitle;
  final void Function() ontap;

  @override
  State<HomePageCard> createState() => _HomePageCardState();
}

class _HomePageCardState extends State<HomePageCard> {
  String? subtitle;

  void updateSubTitle(int index) {
    switch (index) {
      case 0:
        String syncTime = context.watch<SyncProvider>().lastSuccessfulSyncTime;
        setState(() {
          subtitle = syncTime == ""
              ? null
              : DateFormat("EEEE d MMMM, hh:mma")
                  .format(DateTime.parse(syncTime).toLocal())
                  .toString();
        });
        break;
      default:
    }
  }

  @override
  Widget build(BuildContext context) {
    updateSubTitle(widget.index);

    return Card(
      child: ListTile(
        contentPadding: EdgeInsets.symmetric(vertical: 4.w, horizontal: 12.h),
        onTap: widget.ontap,
        leading: Container(
          padding: const EdgeInsets.all(10),
          decoration: BoxDecoration(
              color: const Color(0xffF4F7FF),
              borderRadius: BorderRadius.circular(8)),
          child: widget.icon,
        ),
        title: Text(
          widget.title,
          style: Theme.of(context).textTheme.bodySmall?.copyWith(
              fontWeight: semiBold,
              fontSize: 15,
              color: const Color(0xff333333)),
        ),
        subtitle: widget.subtitle == null
            ? (subtitle == null
                ? null
                : Padding(
                    padding: const EdgeInsets.only(top: 4),
                    child: Text(
                      subtitle ?? "",
                      style: const TextStyle(
                          fontSize: 12, color: Color(0xff6F6E6E)),
                    ),
                  ))
            : Padding(
                padding: const EdgeInsets.only(top: 4),
                child: Text(
                  widget.subtitle ?? "",
                  style:
                      const TextStyle(fontSize: 12, color: Color(0xff6F6E6E)),
                ),
              ),
      ),
    );
  }
}
