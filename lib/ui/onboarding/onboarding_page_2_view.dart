import 'dart:convert';
import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/data/models/process.dart';
import 'package:registration_client/provider/dashboard_view_model.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:responsive_grid_list/responsive_grid_list.dart';

import 'widgets/onboarding_page2_card.dart';

class OnboardingPage2View extends StatelessWidget {
  static const route = "/onboarding-page2-view";
  const OnboardingPage2View({super.key});
  static const platform =
      MethodChannel('com.flutter.dev/io.mosip.get-package-instance');

  void syncData(BuildContext context) async {
    await _masterDataSync();
    await _getNewProcessSpec(context);
    String value = await getCenterName(context);
    context.read<DashboardViewModel>().setCenterName(value);
  }

  Future<void> _masterDataSync() async {
    String result;
    try {
      result = await platform.invokeMethod("masterDataSync");
    } on PlatformException catch (e) {
      result = "Some Error Occurred: $e";
    }
    debugPrint(result);
  }

  Future<void> _getNewProcessSpec(BuildContext context) async {
    try {
      context.read<GlobalProvider>().listOfProcesses =
          await platform.invokeMethod("getNewProcessSpec");
      await Clipboard.setData(ClipboardData(
          text: context.read<GlobalProvider>().listOfProcesses.toString()));
    } on PlatformException catch (e) {
      debugPrint(e.message);
    }
  }

  Future<void> _getUISchema() async {
    String result;
    try {
      result = await platform.invokeMethod("getUISchema");
    } on PlatformException catch (e) {
      result = "Some Error Occurred: $e";
    }
    debugPrint(result);
  }

  Future<String> getCenterName(BuildContext context) async {
    String result;
    try {
      result = await platform.invokeMethod("getCenterName",
          {"centerId": context.read<DashboardViewModel>().centerId});
    } on PlatformException catch (e) {
      result = "Some Error Occurred: $e";
    }
    result = result.split("name=").last.split(",").first;
    log("${result}Master Data");
    return result;
  }

  @override
  Widget build(BuildContext context) {
    double w = ScreenUtil().screenWidth;

    // List<Map<String, dynamic>> registrationTask = [
    //   {
    //     "icon": "assets/svg/Onboarding Yourself.svg",
    //     "title": "New Registration",
    //     "onTap": () {},
    //   },
    //   {
    //     "icon": "assets/svg/Onboarding Yourself.svg",
    //     "title": "Lost UIN",
    //     "onTap": () {},
    //   },
    //   {
    //     "icon": "assets/svg/Onboarding Yourself.svg",
    //     "title": "Update UIN",
    //     "onTap": () {},
    //   },
    //   {
    //     "icon": "assets/svg/Onboarding Yourself.svg",
    //     "title": "Biometrics Correction",
    //     "onTap": () {},
    //   },
    // ];

    List<Map<String, dynamic>> operationalTasks = [
      {
        "icon": SvgPicture.asset(
          "assets/svg/Synchronising Data.svg",
          width: 20,
          height: 20,
        ),
        "title": "Sync Data",
        "onTap": syncData,
      },
      {
        "icon": SvgPicture.asset(
          "assets/svg/Uploading Local - Registration Data.svg",
          width: 20,
          height: 20,
        ),
        "title": "Download Pre-Registration Data",
        "onTap": () {},
      },
      {
        "icon": SvgPicture.asset(
          "assets/svg/Updating Operator Biometrics.svg",
          width: 20,
          height: 20,
        ),
        "title": "Update Operator Biometrics",
        "onTap": () {},
      },
      {
        "icon": SvgPicture.asset(
          "assets/svg/Uploading Local - Registration Data.svg",
          width: 20,
          height: 20,
        ),
        "title": "Application Upload",
        "onTap": () {},
      },
      {
        "icon": SvgPicture.asset(
          "assets/svg/Onboarding Yourself.svg",
          width: 20,
          height: 20,
        ),
        "title": "Pending Approval",
        "onTap": () {},
      },
      {
        "icon": SvgPicture.asset(
          "assets/svg/Uploading Local - Registration Data.svg",
          width: 20,
          height: 20,
        ),
        "title": "Check Update",
        "onTap": () {},
      },
      {
        "icon": SvgPicture.asset(
          "assets/svg/Uploading Local - Registration Data.svg",
          width: 20,
          height: 20,
        ),
        "title": "Center Remap Sync.",
        "onTap": () {},
      },
    ];

    return AnnotatedRegion<SystemUiOverlayStyle>(
      value: const SystemUiOverlayStyle(
        statusBarColor: Colors.transparent,
      ),
      child: Column(
        children: [
          Container(
            padding: const EdgeInsets.all(20),
            decoration: const BoxDecoration(
              gradient: LinearGradient(
                begin: Alignment.topCenter,
                end: Alignment.bottomCenter,
                colors: [Color(0xff214FBF), Color(0xff1C43A1)],
              ),
            ),
            child: Row(
              children: [
                SizedBox(
                  width: w < 512 ? 0 : 60,
                ),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      SizedBox(
                        height: 30.h,
                      ),
                      Text(
                        "Registration Tasks",
                        style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                            color: Colors.white,
                            fontWeight: semiBold,
                            fontSize: 18),
                      ),
                      const SizedBox(
                        height: 20,
                      ),
                      ResponsiveGridList(
                        shrinkWrap: true,
                        minItemWidth: 300,
                        horizontalGridSpacing: 8,
                        verticalGridSpacing: 8,
                        children: List.generate(
                            context
                                .watch<GlobalProvider>()
                                .listOfProcesses
                                .length,
                            (index) => Onboarding_Page2_Card(
                                  icon: Image.asset(
                                    "assets/images/${Process.fromJson(jsonDecode(context.watch<GlobalProvider>().listOfProcesses.elementAt(index).toString())).icon!}",
                                    width: 20,
                                    height: 20,
                                  ),
                                  title: Process.fromJson(jsonDecode(context
                                          .watch<GlobalProvider>()
                                          .listOfProcesses
                                          .elementAt(index)
                                          .toString()))
                                      .label!["eng"]!,
                                  ontap: () {},
                                )),
                      ),
                      SizedBox(
                        height: 30.h,
                      ),
                    ],
                  ),
                ),
                SizedBox(
                  width: w < 512 ? 0 : 60,
                ),
              ],
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(20),
            child: Row(
              children: [
                SizedBox(
                  width: w < 512 ? 0 : 60,
                ),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        "Operational Tasks",
                        style: Theme.of(context)
                            .textTheme
                            .bodyLarge
                            ?.copyWith(fontWeight: semiBold),
                      ),
                      const SizedBox(
                        height: 20,
                      ),
                      // Column(
                      //   children: [
                      //     Onboarding_Page2_Card(
                      //       icon: "assets/svg/Synchronising Data.svg",
                      //       title: "Synchronize Data",
                      //       ontap: () {},
                      //     ),
                      //     Onboarding_Page2_Card(
                      //       icon:
                      //           "assets/svg/Uploading Local - Registration Data.svg",
                      //       title: "Download Pre-Registration Data",
                      //       ontap: () {},
                      //     ),
                      //     Onboarding_Page2_Card(
                      //       icon: "assets/svg/Updating Operator Biometrics.svg",
                      //       title: "Update Operator Biometrics",
                      //       ontap: () {},
                      //     ),
                      //     Onboarding_Page2_Card(
                      //       icon:
                      //           "assets/svg/Uploading Local - Registration Data.svg",
                      //       title: "Application Upload",
                      //       ontap: () {},
                      //     ),
                      //     Onboarding_Page2_Card(
                      //       icon: "assets/svg/Onboarding Yourself.svg",
                      //       title: "Pending Approval",
                      //       ontap: () {},
                      //     ),
                      //     Onboarding_Page2_Card(
                      //       icon:
                      //           "assets/svg/Uploading Local - Registration Data.svg",
                      //       title: "Check Updates",
                      //       ontap: () {},
                      //     ),
                      //     Onboarding_Page2_Card(
                      //       icon:
                      //           "assets/svg/Uploading Local - Registration Data.svg",
                      //       title: "Center Remap Sync.",
                      //       ontap: () {},
                      //     ),
                      //   ],
                      // ),

                      ResponsiveGridList(
                        shrinkWrap: true,
                        minItemWidth: 300,
                        horizontalGridSpacing: 12,
                        verticalGridSpacing: 12,
                        children: List.generate(
                          operationalTasks.length,
                          (index) => Onboarding_Page2_Card(
                            icon: operationalTasks[index]["icon"],
                            title: operationalTasks[index]["title"] as String,
                            ontap: () =>
                                operationalTasks[index]["onTap"](context),
                          ),
                        ),
                      ),

                      SizedBox(
                        height: 4.h,
                      ),
                    ],
                  ),
                ),
                SizedBox(
                  width: w < 512 ? 0 : 60,
                ),
              ],
            ),
          )
        ],
      ),
    );
  }
}
