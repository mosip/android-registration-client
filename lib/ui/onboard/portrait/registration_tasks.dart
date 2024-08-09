/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/provider/sync_provider.dart';
import 'package:registration_client/ui/onboard/widgets/home_page_card.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class RegistrationTasks extends StatefulWidget {
  const RegistrationTasks({
    super.key,
    required this.getProcessUI,
    required this.syncData,
  });

  final Function getProcessUI;
  final Function syncData;

  @override
  State<RegistrationTasks> createState() => _RegistrationTasksState();
}

class _RegistrationTasksState extends State<RegistrationTasks> {
  bool isPortrait = true;

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    isPortrait = MediaQuery.of(context).orientation == Orientation.portrait;
    return Column(
      children: [
        SizedBox(
          height: 26.h,
        ),
        isMobileSize
            ? Padding(
                padding: EdgeInsets.symmetric(horizontal: 16.w),
                child: HomePageCard(
                  index: 0,
                  icon: SvgPicture.asset(
                    syncDataIcon,
                  ),
                  title: AppLocalizations.of(context)!.synchronize_data,
                  ontap: () => widget.syncData(context),
                ),
              )
            : _getSyncDataProvider(),
        SizedBox(
          height: 16.h,
        ),
        _getTasks(),
      ],
    );
  }

  _getSyncDataProvider() {
    return InkWell(
      onTap: () {
        widget.syncData(context);
      },
      child: Container(
        height: 111.h,
        padding: EdgeInsets.symmetric(
          horizontal: 15.w,
          vertical: 16.h,
        ),
        margin: EdgeInsets.symmetric(
          horizontal: 20.w,
        ),
        decoration: BoxDecoration(
          border: Border.all(
            color: appWhite,
          ),
          borderRadius: const BorderRadius.all(
            Radius.circular(6),
          ),
          color: appWhite,
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
          children: [
            Container(
              height: 78.h,
              width: 78.h,
              decoration: BoxDecoration(
                border: Border.all(
                  color: appWhite,
                ),
                borderRadius: const BorderRadius.all(
                  Radius.circular(10),
                ),
                color: iconContainerColor,
              ),
              child: Transform.scale(
                scale: 0.5,
                child: SvgPicture.asset(
                  syncDataIcon,
                ),
              ),
            ),
            SizedBox(
              width: 24.w,
            ),
            Text(
              AppLocalizations.of(context)!.synchronize_data,
              style: TextStyle(
                fontSize: 24,
                fontWeight: semiBold,
                color: appBlackShade1,
              ),
            ),
            const Expanded(
              child: SizedBox(),
            ),
            Text(
              context.watch<SyncProvider>().lastSuccessfulSyncTime != ""
                  ? DateFormat("EEEE d MMMM, hh:mma")
                      .format(DateTime.parse(context
                              .watch<SyncProvider>()
                              .lastSuccessfulSyncTime)
                          .toLocal())
                      .toString()
                  : "Last Sync time not found",
              style: const TextStyle(
                fontSize: 18,
                color: appBlackShade2,
              ),
            )
          ],
        ),
      ),
    );
  }

  _getTasks() {
    return Padding(
      padding: EdgeInsets.symmetric(horizontal: 20.w),
      child: GridView.builder(
          itemCount:
              context.watch<RegistrationTaskProvider>().listOfProcesses.length,
          shrinkWrap: true,
          gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: (isPortrait) ? 2 : 4,
            mainAxisSpacing: (isPortrait) ? 8.h : 1.h,
            crossAxisSpacing: (isPortrait) ? 8.w : 1.w,
          ),
          itemBuilder: (BuildContext context, int index) {
            Process process = Process.fromJson(jsonDecode(context
                .watch<RegistrationTaskProvider>()
                .listOfProcesses
                .elementAt(index)
                .toString()));

            return InkWell(
              onTap: () {
                widget.getProcessUI(
                  context,
                  Process.fromJson(
                    jsonDecode(
                      context
                          .read<RegistrationTaskProvider>()
                          .listOfProcesses
                          .elementAt(index)
                          .toString(),
                    ),
                  ),
                );
              },
              child: Container(
                decoration: BoxDecoration(
                  border: Border.all(
                    color: appWhite,
                  ),
                  borderRadius: const BorderRadius.all(
                    Radius.circular(6),
                  ),
                  color: appWhite,
                  boxShadow: const [
                    BoxShadow(
                      color: greyBorderShade,
                      offset: Offset(3.0, 3.0),
                      blurRadius: 6.0,
                      spreadRadius: 0.0,
                    ),
                  ],
                ),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    SizedBox(
                      // height: 49.08.h,
                      // width: 67.73.w,
                      child: Image.asset(
                        "assets/images/${Process.fromJson(jsonDecode(context.watch<RegistrationTaskProvider>().listOfProcesses.elementAt(index).toString())).icon ?? ""}",
                        fit: BoxFit.fill,
                      ),
                    ),
                    SizedBox(
                      height: 38.17.h,
                    ),
                    Text(
                      process.label![context
                              .read<GlobalProvider>()
                              .selectedLanguage] ??
                          process.label!["eng"] ??
                          process.label!.values.first,
                      style: TextStyle(
                        fontSize: isMobileSize ? 18 : 27,
                        fontWeight: semiBold,
                        color: appBlackShade1,
                      ),
                    ),
                  ],
                ),
              ),
            );
          }),
    );
  }
}
