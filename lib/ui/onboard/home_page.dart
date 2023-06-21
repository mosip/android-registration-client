import 'dart:convert';
import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/process.dart';

import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/ui/process_ui/widgets/new_process_language_selection.dart';

import 'package:registration_client/provider/registration_task_provider.dart';

import 'package:registration_client/utils/app_config.dart';
import 'package:responsive_grid_list/responsive_grid_list.dart';

import '../../provider/auth_provider.dart';
import '../../utils/app_style.dart';
import '../login_page.dart';
import 'widgets/home_page_card.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class HomePage extends StatefulWidget {
  static const route = "/home-page";
  const HomePage({super.key});

  static const platform =
      MethodChannel('com.flutter.dev/io.mosip.get-package-instance');

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  void syncData(BuildContext context) async {
    await _masterDataSync();
    await _getNewProcessSpec(context);
    String value = await getCenterName(context);
    context.read<GlobalProvider>().setCenterName(value);
  }

  Future<void> _masterDataSync() async {
    String result;
    try {
      result = await HomePage.platform.invokeMethod("masterDataSync");
    } on PlatformException catch (e) {
      result = "Some Error Occurred: $e";
    }
    debugPrint(result);
  }

  Widget getProcessUI(BuildContext context, Process process) {
    if (process.id == "NEW") {
      showDialog(
        context: context,
        builder: (BuildContext context) => NewProcessLanguageSelection(
          newProcess: process,
        ),
      );
    }
    return Container();
  }

  Future<void> _getNewProcessSpec(BuildContext context) async {
    try {
      context.read<RegistrationTaskProvider>().listOfProcesses =
          await HomePage.platform.invokeMethod("getNewProcessSpec");
      await Clipboard.setData(ClipboardData(
          text: context
              .read<RegistrationTaskProvider>()
              .listOfProcesses
              .toString()));
    } on PlatformException catch (e) {
      debugPrint(e.message);
    }
  }

  Future<void> _getUISchema() async {
    String result;
    try {
      result = await HomePage.platform.invokeMethod("getUISchema");
    } on PlatformException catch (e) {
      result = "Some Error Occurred: $e";
    }
    debugPrint(result);
  }

  Future<String> getCenterName(BuildContext context) async {
    String result;
    try {
      result = await HomePage.platform.invokeMethod("getCenterName",
          {"centerId": context.read<GlobalProvider>().centerId});
    } on PlatformException catch (e) {
      result = "Some Error Occurred: $e";
    }
    result = result.split("name=").last.split(",").first;
    log("${result}Master Data");
    return result;
  }

  late AuthProvider authProvider;

  @override
  Widget build(BuildContext context) {
    authProvider = Provider.of<AuthProvider>(context, listen: false);
    Future<void> logout() async {
      await context.read<AuthProvider>().logoutUser();
    }

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
                      InkWell(
                        //onTap: widget.onLogout,
                        onTap: () async {
                          print("logging out");
                          authProvider.clearUser();
                          await logout();
                          print("logged out");

                          Navigator.popUntil(
                              context, ModalRoute.withName(HomePage.route));
                          // Navigator.of(context).pushNamed(LoginPage.route);
                          // Navigator.pushNamed(context, LoginPage.route);
                          Navigator.of(context).push(
                            MaterialPageRoute(
                                builder: (context) => LoginPage()),
                          );
                          print("Navigated to Login Page");
                        },
                        child: Container(
                          // width: 129.w,
                          height: 46.h,
                          padding: EdgeInsets.only(
                            left: 46.w,
                            right: 47.w,
                          ),
                          decoration: BoxDecoration(
                            border: Border.all(
                              width: 1.h,
                              color: AppStyle.appHelpText,
                            ),
                            borderRadius: const BorderRadius.all(
                              Radius.circular(5),
                            ),
                          ),
                          child: Center(
                            child: Text(
                              AppLocalizations.of(context)!.logout,
                              style: AppStyle.mobileHelpText,
                            ),
                          ),
                        ),
                      ),
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
