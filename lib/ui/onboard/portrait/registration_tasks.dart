import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/ui/process_ui/widgets/language_selector.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/app_style.dart';

class RegistrationTasks extends StatefulWidget {
  const RegistrationTasks({super.key});

  @override
  State<RegistrationTasks> createState() => _RegistrationTasksState();
}

class _RegistrationTasksState extends State<RegistrationTasks> {
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

  _newRegistrationClickedAudit() async {
    await context
        .read<GlobalProvider>()
        .getAudit("REG-HOME-002", "REG-MOD-102");
  }

  Widget getProcessUI(BuildContext context, Process process) {
    if (process.id == "NEW") {
      _newRegistrationClickedAudit();
      context.read<GlobalProvider>().clearMap();
      context.read<GlobalProvider>().clearScannedPages();
      context.read<GlobalProvider>().newProcessTabIndex = 0;
      context.read<GlobalProvider>().htmlBoxTabIndex = 0;
      context.read<GlobalProvider>().setRegId("");
      for (var screen in process.screens!) {
        for (var field in screen!.fields!) {
          if (field!.controlType == 'dropdown' &&
              field.fieldType == 'default') {
            context
                .read<GlobalProvider>()
                .initializeGroupedHierarchyMap(field.group!);
          }
        }
      }
      context.read<GlobalProvider>().createRegistrationLanguageMap();
      showDialog(
        context: context,
        builder: (BuildContext context) => LanguageSelector(
          newProcess: process,
        ),
      );
    }
    return Container();
  }

  _getSyncDataProvider() {
    return Container(
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
            'Synchronize Data',
            style: TextStyle(
              fontSize: 24,
              fontWeight: semiBold,
              color: AppStyle.appBlackShade1,
            ),
          ),
          const Expanded(
            child: SizedBox(),
          ),
          const Text(
            'Last Sync on Friday 24 Mar, 12:15 PM',
            style: TextStyle(
              fontSize: 18,
              color: AppStyle.appBlackShade2,
            ),
          )
        ],
      ),
    );
  }

  // _getRegistrationTasks() {
  //   debugPrint(
  //       "length ${context.watch<RegistrationTaskProvider>().listOfProcesses.length}");
  //   return SizedBox(
  //     height: 907.h,
  //     child: GridView.builder(
  //       itemCount:
  //           context.watch<RegistrationTaskProvider>().listOfProcesses.length,
  //       gridDelegate:
  //           const SliverGridDelegateWithFixedCrossAxisCount(crossAxisCount: 2),
  //       itemBuilder: (BuildContext context, int index) {
  //         return Container(
  //           decoration: BoxDecoration(
  //             border: Border.all(
  //               color: AppStyle.appWhite,
  //             ),
  //             borderRadius: const BorderRadius.all(
  //               Radius.circular(6),
  //             ),
  //             color: AppStyle.appWhite,
  //             boxShadow: const [
  //               BoxShadow(
  //                 color: AppStyle.greyBorderShade,
  //                 offset: Offset(3.0, 3.0),
  //                 blurRadius: 6.0,
  //                 spreadRadius: 0.0,
  //               ),
  //             ],
  //           ),
  //           child: Column(
  //             mainAxisAlignment: MainAxisAlignment.center,
  //             children: [
  //               SizedBox(
  //                 height: 49.08.h,
  //                 width: 67.73.w,
  //                 child: Image.asset(
  //                   "assets/images/${Process.fromJson(jsonDecode(context.watch<RegistrationTaskProvider>().listOfProcesses.elementAt(index).toString())).icon ?? ""}",
  //                   fit: BoxFit.fill,
  //                 ),
  //               ),
  //               SizedBox(
  //                 height: 38.17.h,
  //               ),
  //               Text(
  //                 Process.fromJson(jsonDecode(context
  //                         .watch<RegistrationTaskProvider>()
  //                         .listOfProcesses
  //                         .elementAt(index)
  //                         .toString()))
  //                     .label!["eng"]!,
  //                 style: const TextStyle(
  //                   fontSize: 27,
  //                   color: AppStyle.appBlackShade1,
  //                 ),
  //               ),
  //             ],
  //           ),
  //         );
  //       },
  //     ),
  //   );
  // }

  _getTasks() {
    return Wrap(
      spacing: 8.w,
      runSpacing: 8.h,
      children:
          context.watch<RegistrationTaskProvider>().listOfProcesses.map((e) {
        return InkWell(
          onTap: () {
            getProcessUI(
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
