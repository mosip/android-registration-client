import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/provider/app_language_provider.dart';

import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/ui/process_ui/widgets/language_selector.dart';

import 'package:registration_client/provider/registration_task_provider.dart';

import 'package:registration_client/utils/app_config.dart';
import 'package:responsive_grid_list/responsive_grid_list.dart';

import 'widgets/home_page_card.dart';

class HomePage extends StatelessWidget {
  static const route = "/home-page";
  const HomePage({super.key});

  static const platform =
      MethodChannel('com.flutter.dev/io.mosip.get-package-instance');

  void syncData(BuildContext context) async {
    await _masterDataSync();
    await _getNewProcessSpecAction(context);
    await _getCenterNameAction(context);
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

  Widget getProcessUI(BuildContext context, Process process) {
    if (process.id == "NEW") {
      showDialog(
        context: context,
        builder: (BuildContext context) => LanguageSelector(
          newProcess: process,
        ),
      );
    }
    return Container();
  }

  _getNewProcessSpecAction(BuildContext context) async {
    await context.read<RegistrationTaskProvider>().getListOfProcesses();
  }

  _getUiSchemaAction(BuildContext context) async {
    await context.read<RegistrationTaskProvider>().getUISchema();
  }

  _getCenterNameAction(BuildContext context) async {
    String regCenterId = context.read<GlobalProvider>().centerId;

    String langCode = context.read<AppLanguageProvider>().selectedLanguage;
    await context
        .read<GlobalProvider>()
        .getRegCenterName(regCenterId, langCode);
  }

  @override
  Widget build(BuildContext context) {
    double w = ScreenUtil().screenWidth;
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
                                .watch<RegistrationTaskProvider>()
                                .listOfProcesses
                                .length,
                            (index) => HomePageCard(
                                  icon: Image.asset(
                                    "assets/images/${Process.fromJson(jsonDecode(context.watch<RegistrationTaskProvider>().listOfProcesses.elementAt(index).toString())).icon!}",
                                    width: 20,
                                    height: 20,
                                  ),
                                  title: Process.fromJson(jsonDecode(context
                                          .watch<RegistrationTaskProvider>()
                                          .listOfProcesses
                                          .elementAt(index)
                                          .toString()))
                                      .label!["eng"]!,
                                  ontap: () {
                                    getProcessUI(
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
                      ResponsiveGridList(
                        shrinkWrap: true,
                        minItemWidth: 300,
                        horizontalGridSpacing: 12,
                        verticalGridSpacing: 12,
                        children: List.generate(
                          operationalTasks.length,
                          (index) => HomePageCard(
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
