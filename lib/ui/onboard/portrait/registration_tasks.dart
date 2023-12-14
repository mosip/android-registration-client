import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/provider/sync_provider.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/app_style.dart';
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
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        SizedBox(
          height: 26.h,
        ),
        _getSyncDataProvider(),
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
            color: AppStyle.appWhite,
          ),
          borderRadius: const BorderRadius.all(
            Radius.circular(6),
          ),
          color: AppStyle.appWhite,
          boxShadow: const [
            BoxShadow(
              color: AppStyle.greyBorderShade,
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
                  color: AppStyle.appWhite,
                ),
                borderRadius: const BorderRadius.all(
                  Radius.circular(10),
                ),
                color: AppStyle.iconContainerColor,
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
                color: AppStyle.appBlackShade1,
              ),
            ),
            const Expanded(
              child: SizedBox(),
            ),
            Text(
              DateFormat("EEEE d MMMM, hh:mma")
                  .format(DateTime.parse(
                          context.watch<SyncProvider>().lastSuccessfulSyncTime)
                      .toLocal())
                  .toString(),
              style: const TextStyle(
                fontSize: 18,
                color: AppStyle.appBlackShade2,
              ),
            )
          ],
        ),
      ),
    );
  }

  _getTasks() {
    return Wrap(
      spacing: 8.w,
      runSpacing: 8.h,
      children:
          context.watch<RegistrationTaskProvider>().listOfProcesses.map((e) {
        return InkWell(
          onTap: () {
            widget.getProcessUI(
              context,
              Process.fromJson(
                jsonDecode(
                  e.toString(),
                ),
              ),
            );
          },
          child: Container(
            height: 435.h,
            width: 372.w,
            decoration: BoxDecoration(
              border: Border.all(
                color: AppStyle.appWhite,
              ),
              borderRadius: const BorderRadius.all(
                Radius.circular(6),
              ),
              color: AppStyle.appWhite,
              boxShadow: const [
                BoxShadow(
                  color: AppStyle.greyBorderShade,
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
                  height: 49.08.h,
                  width: 67.73.w,
                  child: Image.asset(
                    "assets/images/${Process.fromJson(jsonDecode(e.toString())).icon ?? ""}",
                    fit: BoxFit.fill,
                  ),
                ),
                SizedBox(
                  height: 38.17.h,
                ),
                Text(
                  Process.fromJson(jsonDecode(e.toString())).label!["eng"]!,
                  style: const TextStyle(
                    fontSize: 27,
                    color: AppStyle.appBlackShade1,
                  ),
                ),
              ],
            ),
          ),
        );
      }).toList(),
    );
  }
}
