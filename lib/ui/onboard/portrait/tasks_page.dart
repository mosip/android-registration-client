/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/ui/onboard/portrait/operational_tasks.dart';
import 'package:registration_client/ui/onboard/portrait/registration_tasks.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class TasksPage extends StatefulWidget {
  const TasksPage({
    super.key,
    required this.operationalTasks,
    required this.getProcessUI,
    required this.syncData,
  });
  final List<Map<String, dynamic>> operationalTasks;
  final Function getProcessUI;
  final Function syncData;

  @override
  State<TasksPage> createState() => _TasksPageState();
}

class _TasksPageState extends State<TasksPage> {
  int currentIndex = 0;

  @override
  void initState() {
    super.initState();
    Provider.of<GlobalProvider>(context, listen: false)
        .getAudit("NAV_DASHBOARD", "REG-MOD-102");
  }

  void _showRemapSpotlight() {
    final remapTask = widget.operationalTasks.firstWhere(
      (t) => t["isRemapHighlight"] == true,
      orElse: () => {},
    );
    if (remapTask.isEmpty) return;

    showGeneralDialog(
      context: context,
      barrierDismissible: false,
      barrierColor: Colors.black54,
      transitionDuration: Duration.zero,
      pageBuilder: (ctx, _, __) {
        return Material(
          color: Colors.transparent,
          child: SafeArea(
            child: Stack(
              children: [
                // X button — top right corner
                Positioned(
                  top: 40.h,
                  right: 28.w,
                  child: GestureDetector(
                    onTap: () => Navigator.of(ctx).pop(),
                    child: Container(
                      width: isMobileSize ? 28 : 36,
                      height: isMobileSize ? 28 : 36,
                      decoration: const BoxDecoration(
                        color: appWhite,
                        shape: BoxShape.circle,
                      ),
                      child: Icon(
                        Icons.close,
                        color: appSolidPrimary,
                        size: isMobileSize ? 16 : 22,
                      ),
                    ),
                  ),
                ),

                // Center Remap Sync card — centred vertically
                Center(
                  child: Padding(
                    padding: EdgeInsets.symmetric(horizontal: 16.w),
                    child: Container(
                      decoration: BoxDecoration(
                        color: appWhite,
                        borderRadius: BorderRadius.circular(8),
                        border: Border.all(color: greyBorderShade, width: 1.5),
                        boxShadow: const [
                          BoxShadow(
                            color: Colors.black26,
                            blurRadius: 12,
                            offset: Offset(0, 4),
                          ),
                        ],
                      ),
                      child: ListTile(
                        leading: remapTask["icon"] as Widget,
                        title: Text(
                          remapTask["title"] as String,
                          style: TextStyle(
                            fontSize: isMobileSize ? 14 : 20,
                            fontWeight: semiBold,
                            color: appBlackShade1,
                          ),
                        ),
                        subtitle: Text(
                          remapTask["subtitle"] as String,
                          style: TextStyle(
                            fontSize: isMobileSize ? 12 : 16,
                            color: appBlackShade2,
                          ),
                        ),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      child: Column(
        children: [
          Container(
            padding: EdgeInsets.symmetric(horizontal: 20.w),
            child: Row(
              children: [
                Expanded(
                  child: InkWell(
                    onTap: () {
                      setState(() => currentIndex = 0);
                    },
                    child: Container(
                      padding: EdgeInsets.symmetric(vertical: 28.h),
                      decoration: BoxDecoration(
                        border: Border.all(
                          color: currentIndex == 0
                              ? appSolidPrimary
                              : greyBorderShade,
                        ),
                        borderRadius: const BorderRadius.only(
                          topLeft: Radius.circular(6),
                          topRight: Radius.circular(6),
                        ),
                        color: currentIndex == 0 ? appSolidPrimary : appWhite,
                      ),
                      child: Center(
                        child: Text(
                          AppLocalizations.of(context)!.registration_tasks,
                          style: TextStyle(
                            fontSize: isMobileSize ? 14 : 24,
                            fontWeight: semiBold,
                            color: currentIndex == 0 ? appWhite : appBlack,
                          ),
                        ),
                      ),
                    ),
                  ),
                ),
                Expanded(
                  child: InkWell(
                    onTap: () {
                      Provider.of<GlobalProvider>(context, listen: false)
                          .getAudit("NAV_OPERATIONAL_TASKS", "REG-MOD-102");
                      setState(() => currentIndex = 1);
                    },
                    child: Container(
                      padding: EdgeInsets.symmetric(vertical: 28.h),
                      decoration: BoxDecoration(
                        border: Border.all(
                          color: currentIndex == 1
                              ? appSolidPrimary
                              : greyBorderShade,
                        ),
                        borderRadius: const BorderRadius.only(
                          topLeft: Radius.circular(6),
                          topRight: Radius.circular(6),
                        ),
                        color: currentIndex == 1 ? appSolidPrimary : appWhite,
                      ),
                      child: Center(
                        child: Text(
                          AppLocalizations.of(context)!.operation_tasks,
                          style: TextStyle(
                            fontSize: isMobileSize ? 14 : 24,
                            fontWeight: semiBold,
                            color: currentIndex == 1 ? appWhite : appBlack,
                          ),
                        ),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
          Container(
            color: appSolidPrimary,
            height: 2.5.h,
            margin: EdgeInsets.symmetric(horizontal: 20.w),
          ),
          currentIndex == 0
              ? RegistrationTasks(
                  getProcessUI: (BuildContext context, Process process) {
                    widget.getProcessUI(context, process);
                  },
                  syncData: (BuildContext context) {
                    widget.syncData(context);
                  },
                  onRemapBannerTap: () {
                    Provider.of<GlobalProvider>(context, listen: false)
                        .getAudit("NAV_OPERATIONAL_TASKS", "REG-MOD-102");
                    setState(() => currentIndex = 1);
                    _showRemapSpotlight();
                  },
                )
              : OperationalTasks(
                  operationalTasks: widget.operationalTasks,
                ),

          SizedBox(height: 125.h),
        ],
      ),
    );
  }
}
