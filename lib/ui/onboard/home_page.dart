/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

// ignore_for_file: deprecated_member_use

import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:flutter_svg/flutter_svg.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/provider/connectivity_provider.dart';

import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/sync_provider.dart';
import 'package:registration_client/ui/onboard/portrait/mobile_home_page.dart';

import 'package:registration_client/ui/process_ui/widgets/language_selector.dart';

import 'package:registration_client/provider/registration_task_provider.dart';

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
  late SyncProvider syncProvider;
  late GlobalProvider globalProvider;
  late RegistrationTaskProvider registrationTaskProvider;
  late ConnectivityProvider connectivityProvider;

  @override
  void initState() {
    syncProvider = Provider.of<SyncProvider>(context, listen: false);
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    registrationTaskProvider = Provider.of<RegistrationTaskProvider>(context, listen: false);
    connectivityProvider = Provider.of<ConnectivityProvider>(context, listen: false);
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
    return connectivityProvider.isConnected;
  }

  _showNetworkErrorMessage() {
    _showInSnackBar(AppLocalizations.of(context)!.network_error);
  }

  void syncData(BuildContext context) async {
    await connectivityProvider.checkNetworkConnection();
    bool isConnected = _getIsConnected();
    if (!isConnected) {
      _showNetworkErrorMessage();
      return;
    }
    await syncProvider.getLastSyncTime();
    await _masterDataSync();
    await syncProvider.getLastSyncTime();
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
    await globalProvider.initializeLanguageDataList();
  }

  _initializeLocationHierarchy() async {
    await globalProvider.initializeLocationHierarchyMap();
  }

  _getFieldValues(String fieldId, String langCode) async {
    List<String?> fieldValues = await registrationTaskProvider
        .getFieldValues(fieldId, langCode);
    _setNotificationLanguages(fieldValues);
  }

  _setNotificationLanguages(List<String?> fieldValues) {
    globalProvider.setNotificationLanguages(fieldValues);
  }

  _homePageLoadedAudit() async {
    await globalProvider
        .getAudit("REG-LOAD-003", "REG-MOD-102");
  }

  Future<void> _masterDataSync() async {
    // try {
    //   result = await HomePage.platform.invokeMethod("masterDataSync");
    //   await HomePage.platform.invokeMethod("batchJob");
    // } on PlatformException catch (e) {
    //   result = "Some Error Occurred: $e";
    // }
    await syncProvider.manualSync();
    log("sync complete!");
    await syncProvider.batchJob();
    log("batch job completed!");
  }

  _newRegistrationClickedAudit() async {
    await globalProvider
        .getAudit("REG-HOME-002", "REG-MOD-102");
  }

  Widget getProcessUI(BuildContext context, Process process) {
    if (process.id == "NEW") {
      _newRegistrationClickedAudit();
      globalProvider.clearMap();
      globalProvider.clearScannedPages();
      globalProvider.newProcessTabIndex = 0;
      globalProvider.htmlBoxTabIndex = 0;
      globalProvider.setRegId("");
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
      globalProvider.createRegistrationLanguageMap();
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
    await registrationTaskProvider.getListOfProcesses();
  }

  _getCenterNameAction() async {
    String regCenterId = globalProvider.centerId;

    String langCode = globalProvider.selectedLanguage;
    await globalProvider
        .getRegCenterName(regCenterId, langCode);
  }

  @override
  Widget build(BuildContext context) {
    // double w = ScreenUtil().screenWidth;
    isMobile = MediaQuery.of(context).orientation == Orientation.portrait;
    syncProvider = Provider.of<SyncProvider>(context, listen: false);
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    registrationTaskProvider = Provider.of<RegistrationTaskProvider>(context, listen: false);
    connectivityProvider = Provider.of<ConnectivityProvider>(context, listen: false);

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
        "onTap": () {},
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
          );}
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
  