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
    this.isSyncing = false,
    this.syncProgress = 0,
    this.showLastSyncFallback = false,
  });

  final Widget icon;
  final String title;
  final int index;
  final String? subtitle;
  final void Function() ontap;
  final bool isSyncing;
  final int syncProgress;
  final bool showLastSyncFallback;

  @override
  State<HomePageCard> createState() => _HomePageCardState();
}

class _HomePageCardState extends State<HomePageCard> {
  @override
  Widget build(BuildContext context) {
    String? displaySubtitle = widget.subtitle;
    if (displaySubtitle == null && widget.showLastSyncFallback) {
      String syncTime = context.watch<SyncProvider>().lastSuccessfulSyncTime;
      if (syncTime.isNotEmpty) {
        final parsed = DateTime.tryParse(syncTime);
        if (parsed != null) {
          displaySubtitle = DateFormat("EEEE d MMMM, hh:mma")
              .format(parsed.toLocal())
              .toString();
        }
      }
    }

    return Card(
      clipBehavior: Clip.antiAlias,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          ListTile(
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
            subtitle: displaySubtitle == null
                ? null
                : Padding(
                    padding: const EdgeInsets.only(top: 4),
                    child: Text(
                      displaySubtitle,
                      style: TextStyle(
                        fontSize: 12,
                        color: widget.isSyncing ? appSolidPrimary : const Color(0xff6F6E6E),
                      ),
                    ),
                  ),
          ),
          if (widget.isSyncing)
            LinearProgressIndicator(
              value: widget.syncProgress > 0 ? widget.syncProgress / 100.0 : null,
              minHeight: 4,
              backgroundColor: greyBorderShade,
              valueColor: const AlwaysStoppedAnimation<Color>(appSolidPrimary),
            ),
        ],
      ),
    );
  }
}
