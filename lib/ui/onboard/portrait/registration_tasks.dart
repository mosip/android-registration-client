import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
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
        _getSyncDataProvider(),
        SizedBox(
          height: 16.h,
        ),
        // _getRegistrationTasks(),
        _getTasks(),
      ],
    );
  }

  _getSyncDataProvider() {
    return Container(
      height: 111.h,
      padding: EdgeInsets.symmetric(
        horizontal: 15.w,
        vertical: 16.h,
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
        return Container(
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
        );
      }).toList(),
    );
  }
}
