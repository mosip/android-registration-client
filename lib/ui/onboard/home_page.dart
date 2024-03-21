/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

// ignore_for_file: deprecated_member_use

// import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

// import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/pigeon/biometrics_pigeon.dart';
import 'package:registration_client/provider/connectivity_provider.dart';

import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/sync_provider.dart';
// import 'package:registration_client/ui/common/tablet_footer.dart';
// import 'package:registration_client/ui/common/tablet_header.dart';
// import 'package:registration_client/ui/common/tablet_navbar.dart';
import 'package:registration_client/ui/onboard/portrait/mobile_home_page.dart';
import 'package:registration_client/ui/onboard/widgets/operator_onboarding_biometrics_capture_control.dart';
// import 'package:registration_client/ui/onboard/widgets/home_page_card.dart';

import 'package:registration_client/ui/process_ui/widgets/language_selector.dart';

import 'package:registration_client/provider/registration_task_provider.dart';

// import 'package:registration_client/utils/app_config.dart';
// import 'package:responsive_grid_list/responsive_grid_list.dart';
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
  bool isMobile = true;
  @override
  void initState() {
    _fetchProcessSpec();
    super.initState();
  }

  void _showInSnackBar(String value) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(value),
      ),
    );
  }

  _getIsConnected() {
    return context.read<ConnectivityProvider>().isConnected;
  }

  _showNetworkErrorMessage() {
    _showInSnackBar(AppLocalizations.of(context)!.network_error);
  }

  void syncData(BuildContext context) async {
    await context.read<ConnectivityProvider>().checkNetworkConnection();
    bool isConnected = _getIsConnected();
    if (!isConnected) {
      _showNetworkErrorMessage();
      return;
    }
    // ignore: use_build_context_synchronously
    await context.read<SyncProvider>().getLastSyncTime();
    await _masterDataSync();
    // ignore: use_build_context_synchronously
    await context.read<SyncProvider>().getLastSyncTime();
    await _getNewProcessSpecAction();
    await _getCenterNameAction();
    await _initializeLanguageDataList();
    await _initializeLocationHierarchy();
  }

  void _fetchProcessSpec() async {
    await _getNewProcessSpecAction();
    await _getFieldValues("preferredLang", "eng");
    await _getCenterNameAction();
    await _homePageLoadedAudit();
  }

  _initializeLanguageDataList() async {
    await context.read<GlobalProvider>().initializeLanguageDataList();
  }

  _initializeLocationHierarchy() async {
    await context.read<GlobalProvider>().initializeLocationHierarchyMap();
  }

  _getFieldValues(String fieldId, String langCode) async {
    List<String?> fieldValues = await context
        .read<RegistrationTaskProvider>()
        .getFieldValues(fieldId, langCode);
    _setNotificationLanguages(fieldValues);
  }

  _setNotificationLanguages(List<String?> fieldValues) {
    context.read<GlobalProvider>().setNotificationLanguages(fieldValues);
  }

  _homePageLoadedAudit() async {
    await context
        .read<GlobalProvider>()
        .getAudit("REG-LOAD-003", "REG-MOD-102");
  }

  Future<void> _masterDataSync() async {
    String result;
    try {
      result = await HomePage.platform.invokeMethod("masterDataSync");
      await HomePage.platform.invokeMethod("batchJob");
    } on PlatformException catch (e) {
      result = "Some Error Occurred: $e";
    }
    debugPrint(result);
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

  _getNewProcessSpecAction() async {
    await context.read<RegistrationTaskProvider>().getListOfProcesses();
  }

  _getCenterNameAction() async {
    String regCenterId = context.read<GlobalProvider>().centerId;

    String langCode = context.read<GlobalProvider>().selectedLanguage;
    await context
        .read<GlobalProvider>()
        .getRegCenterName(regCenterId, langCode);
  }

  @override
  Widget build(BuildContext context) {
    // double w = ScreenUtil().screenWidth;
    isMobile = MediaQuery.of(context).orientation == Orientation.portrait;
    List<Map<String, dynamic>> operationalTasks = [
      {
        "icon": SvgPicture.asset(
          "assets/svg/Synchronising Data.svg",
          width: 20,
          height: 20,
        ),
        "title": AppLocalizations.of(context)!.synchronize_data,
        "onTap": syncData,
        "subtitle": DateFormat("EEEE d MMMM, hh:mma")
            .format(DateTime.parse(
                    context.watch<SyncProvider>().lastSuccessfulSyncTime)
                .toLocal())
            .toString(),
      },
      {
        "icon": SvgPicture.asset(
          "assets/svg/Uploading Local - Registration Data.svg",
        ),
        "title": AppLocalizations.of(context)!.download_pre_registration_data,
        "onTap": () {},
        "subtitle": "Last downloaded on Friday 24 Mar, 12:15PM"
      },
      {
        "icon": SvgPicture.asset(
          "assets/svg/Updating Operator Biometrics.svg",
        ),
        "title": AppLocalizations.of(context)!.update_operator_biomterics,
        "onTap": (context) async {
          await BiometricsApi().startOperatorOnboarding();
          Navigator.push(
              context,
              MaterialPageRoute(
                  builder: (context) =>
                      OperatorOnboardingBiometricsCaptureControl()));
        },
        "subtitle": "Last updated on Wednesday 12 Apr, 11:20PM"
      },
      {
        "icon": SvgPicture.asset(
          "assets/svg/Uploading Local - Registration Data.svg",
        ),
        "title": AppLocalizations.of(context)!.appliction_upload,
        "onTap": () {},
        "subtitle": "3 application(s)"
      },
      {
        "icon": SvgPicture.asset(
          "assets/svg/Onboarding Yourself.svg",
        ),
        "title": AppLocalizations.of(context)!.check_updates,
        "onTap": () {},
        "subtitle": "Last updated on Wednesday 12 Apr, 11:20PM"
      },
      {
        "icon": SvgPicture.asset(
          "assets/svg/Uploading Local - Registration Data.svg",
        ),
        "title": AppLocalizations.of(context)!.center_remap_sync,
        "onTap": () {},
        "subtitle": "Last updated on Wednesday 12 Apr, 11:20PM"
      },
      {
        "icon": SvgPicture.asset(
          "assets/svg/Uploading Local - Registration Data.svg",
        ),
        "title": AppLocalizations.of(context)!.sync_activities,
        "onTap": () {},
        "subtitle": "Last updated on Wednesday 12 Apr, 11:20PM"
      },
    ];

    return MobileHomePage(
      operationalTasks: operationalTasks,
      getProcessUI: (BuildContext context, Process process) {
        getProcessUI(context, process);
      },
      syncData: (BuildContext context) {
        syncData(context);
      },
    );
  }
}

/*This piece of code is for the deprecated version of the home page*/
// isMobile
//         ? MobileHomePage(
//             operationalTasks: operationalTasks,
//             getProcessUI: (BuildContext context, Process process) {
//               getProcessUI(context, process);
//             },
//             syncData: (BuildContext context) {
//               syncData(context);
//             },
//           )
//         : Scaffold(
//             backgroundColor: Colors.white,
//             body: Row(
//               children: [
//                 Expanded(
//                   child: LayoutBuilder(
//                     builder: (context, constraint) {
//                       return SingleChildScrollView(
//                         controller: ScrollController(),
//                         child: Column(
//                           children: [
//                             isMobileSize ? const SizedBox() : const TabletHeader(),
//                             isMobileSize ? const SizedBox() : const TabletNavbar(),
//                             AnnotatedRegion<SystemUiOverlayStyle>(
//                               value: const SystemUiOverlayStyle(
//                                 statusBarColor: Colors.transparent,
//                               ),
//                               child: Column(
//                                 children: [
//                                   Container(
//                                     padding: const EdgeInsets.all(20),
//                                     decoration: const BoxDecoration(
//                                       gradient: LinearGradient(
//                                         begin: Alignment.topCenter,
//                                         end: Alignment.bottomCenter,
//                                         colors: [
//                                           Color(0xff214FBF),
//                                           Color(0xff1C43A1)
//                                         ],
//                                       ),
//                                     ),
//                                     child: Row(
//                                       children: [
//                                         SizedBox(
//                                           width: w < 512 ? 0 : 60,
//                                         ),
//                                         Expanded(
//                                           child: Column(
//                                             crossAxisAlignment:
//                                                 CrossAxisAlignment.start,
//                                             children: [
//                                               SizedBox(
//                                                 height: 30.h,
//                                               ),
//                                               Text(
//                                                 AppLocalizations.of(context)!.registration_tasks,
//                                                 style: Theme.of(context)
//                                                     .textTheme
//                                                     .bodyLarge
//                                                     ?.copyWith(
//                                                         color: Colors.white,
//                                                         fontWeight: semiBold,
//                                                         fontSize: 18),
//                                               ),
//                                               const SizedBox(
//                                                 height: 20,
//                                               ),
//                                               ResponsiveGridList(
//                                                 shrinkWrap: true,
//                                                 minItemWidth: 300,
//                                                 horizontalGridSpacing: 8,
//                                                 verticalGridSpacing: 8,
//                                                 children: List.generate(
//                                                     context
//                                                         .watch<
//                                                             RegistrationTaskProvider>()
//                                                         .listOfProcesses
//                                                         .length,
//                                                     (index) => HomePageCard(
//                                                           icon: Image.asset(
//                                                             "assets/images/${Process.fromJson(jsonDecode(context.watch<RegistrationTaskProvider>().listOfProcesses.elementAt(index).toString())).icon ?? ""}",
//                                                             width: 20,
//                                                             height: 20,
//                                                           ),
//                                                           index: index + 1,
//                                                           title: Process.fromJson(
//                                                                   jsonDecode(context
//                                                                       .watch<
//                                                                           RegistrationTaskProvider>()
//                                                                       .listOfProcesses
//                                                                       .elementAt(
//                                                                           index)
//                                                                       .toString()))
//                                                               .label!["eng"]!,
//                                                           ontap: () {
//                                                             getProcessUI(
//                                                               context,
//                                                               Process.fromJson(
//                                                                 jsonDecode(
//                                                                   context
//                                                                       .read<
//                                                                           RegistrationTaskProvider>()
//                                                                       .listOfProcesses
//                                                                       .elementAt(
//                                                                           index)
//                                                                       .toString(),
//                                                                 ),
//                                                               ),
//                                                             );
//                                                           },
//                                                         )),
//                                               ),
//                                               SizedBox(
//                                                 height: 30.h,
//                                               ),
//                                             ],
//                                           ),
//                                         ),
//                                         SizedBox(
//                                           width: w < 512 ? 0 : 60,
//                                         ),
//                                       ],
//                                     ),
//                                   ),
//                                   Padding(
//                                     padding: const EdgeInsets.all(20),
//                                     child: Row(
//                                       children: [
//                                         SizedBox(
//                                           width: w < 512 ? 0 : 60,
//                                         ),
//                                         Expanded(
//                                           child: Column(
//                                             crossAxisAlignment:
//                                                 CrossAxisAlignment.start,
//                                             children: [
//                                               Text(
//                                                 AppLocalizations.of(context)!.operation_tasks,
//                                                 style: Theme.of(context)
//                                                     .textTheme
//                                                     .bodyLarge
//                                                     ?.copyWith(
//                                                         fontWeight: semiBold),
//                                               ),
//                                               const SizedBox(
//                                                 height: 20,
//                                               ),
//                                               ResponsiveGridList(
//                                                 shrinkWrap: true,
//                                                 minItemWidth: 300,
//                                                 horizontalGridSpacing: 12,
//                                                 verticalGridSpacing: 12,
//                                                 children: List.generate(
//                                                   operationalTasks.length,
//                                                   (index) {
//                                                     return HomePageCard(
//                                                       index: index,
//                                                       icon: operationalTasks[
//                                                           index]["icon"],
//                                                       title: operationalTasks[
//                                                               index]["title"]
//                                                           as String,
//                                                       ontap: () =>
//                                                           operationalTasks[
//                                                                       index]
//                                                                   ["onTap"](
//                                                               context),
//                                                     );
//                                                   },
//                                                 ),
//                                               ),
//                                               SizedBox(
//                                                 height: 4.h,
//                                               ),
//                                             ],
//                                           ),
//                                         ),
//                                         SizedBox(
//                                           width: w < 512 ? 0 : 60,
//                                         ),
//                                       ],
//                                     ),
//                                   )
//                                 ],
//                               ),
//                             ),
//                             const SizedBox(
//                               height: 10,
//                             ),
//                             context.watch<GlobalProvider>().currentIndex != 0
//                                 ? const TabletFooter()
//                                 : const SizedBox.shrink(),
//                           ],
//                         ),
//                       );
//                     },
//                   ),
//                 )
//               ],
//             ),
//           );
  