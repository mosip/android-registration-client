/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'dart:developer';

import 'package:flutter/material.dart';

import 'package:flutter_svg/flutter_svg.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/pigeon/biometrics_pigeon.dart';
import 'package:registration_client/pigeon/dynamic_response_pigeon.dart';
import 'package:registration_client/provider/approve_packets_provider.dart';
import 'package:registration_client/provider/auth_provider.dart';
import 'package:registration_client/provider/connectivity_provider.dart';

import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/sync_provider.dart';
import 'package:registration_client/ui/approve_packet/approve_packet_ui.dart';
import 'package:registration_client/ui/export_packet/export_packet_ui.dart';
import 'package:registration_client/ui/onboard/portrait/mobile_home_page.dart';
import 'package:registration_client/ui/onboard/widgets/operator_onboarding_biometrics_capture_control.dart';
// import 'package:registration_client/ui/onboard/widgets/home_page_card.dart';

import 'package:registration_client/ui/process_ui/widgets/language_selector.dart';

import 'package:registration_client/provider/registration_task_provider.dart';

import 'package:flutter_gen/gen_l10n/app_localizations.dart';

import '../../model/screen.dart';

class HomePage extends StatefulWidget {
  static const route = "/home-page";

  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  bool isMobile = true;
  late SyncProvider syncProvider;
  late GlobalProvider globalProvider;
  late RegistrationTaskProvider registrationTaskProvider;
  late ConnectivityProvider connectivityProvider;
  late AppLocalizations appLocalizations = AppLocalizations.of(context)!;
  String lastOperatorUpdateBiometricTime = "";

  @override
  void initState() {
    syncProvider = Provider.of<SyncProvider>(context, listen: false);
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    registrationTaskProvider =
        Provider.of<RegistrationTaskProvider>(context, listen: false);
    connectivityProvider =
        Provider.of<ConnectivityProvider>(context, listen: false);
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

  void syncData(BuildContext context) async {
    await connectivityProvider.checkNetworkConnection();
    if (!connectivityProvider.isConnected) {
      _showInSnackBar(appLocalizations.network_error);
      return;
    }
    await syncProvider.manualSync();
    log("Manual Sync Completed!");
    syncProvider.isSyncAndUploadInProgress = true;
    await syncProvider.batchJob();
    syncProvider.isSyncAndUploadInProgress = false;
    //await syncProvider.getPreRegistrationIds();
    await registrationTaskProvider.getListOfProcesses();
    await globalProvider.getRegCenterName(
        globalProvider.centerId, globalProvider.selectedLanguage);
    await globalProvider.initializeLanguageDataList(true);
    await globalProvider.initializeLocationHierarchyMap();
  }

  void _fetchProcessSpec() async {
    await registrationTaskProvider.getLastUpdatedTime();
    await registrationTaskProvider.getListOfProcesses();
    await _getFieldValues("preferredLang", globalProvider.selectedLanguage);
    await globalProvider.getRegCenterName(
        globalProvider.centerId, globalProvider.selectedLanguage);
    await globalProvider.getAudit("REG-LOAD-003", "REG-MOD-102");
  }

  _getFieldValues(String fieldId, String langCode) async {
    List<DynamicFieldData?> fieldValues = await registrationTaskProvider
        .getFieldValues(fieldId, langCode, globalProvider.chosenLang);
    globalProvider.setNotificationLanguages(fieldValues);
  }

  Widget getProcessUI(BuildContext context, Process process) {
    List<Screen?> sortedScreens;
    sortedScreens = process.screens!.toList()..sort((e1, e2) => e1!.order!.compareTo(e2!.order!));
    if (process.id == "NEW" || process.id == "UPDATE") {
      globalProvider.clearRegistrationProcessData();
      globalProvider.setPreRegistrationId("");
      for (var screen in sortedScreens) {
        for (var field in screen!.fields!) {
          if (field!.controlType == 'dropdown' &&
              field.fieldType == 'default') {
            globalProvider.initializeGroupedHierarchyMap(field.group!);
          }
        }
      }
      globalProvider.createRegistrationLanguageMap();
      globalProvider.getAudit("REG-HOME-002", "REG-MOD-102");
      showDialog(
        context: context,
        builder: (BuildContext context) => LanguageSelector(
          newProcess: Process(label: process.label, autoSelectedGroups: process.autoSelectedGroups,
              caption: process.caption, flow: process.flow, icon: process.icon, id: process.id,
              isActive: process.isActive, order: process.order, screens: sortedScreens
          ),
        ),
      );
    }
    return Container();
  }

  @override
  Widget build(BuildContext context) {
    isMobile = MediaQuery.of(context).orientation == Orientation.portrait;
    appLocalizations = AppLocalizations.of(context)!;

    try {
      String dateString =
          context.watch<RegistrationTaskProvider>().lastSuccessfulUpdatedTime;
      DateTime dateTime =
          DateTime.fromMillisecondsSinceEpoch(int.parse(dateString));
      lastOperatorUpdateBiometricTime =
          DateFormat("EEEE d MMMM, hh:mma").format(dateTime);
    } catch (e) {
      lastOperatorUpdateBiometricTime = "";
    }

    List<Map<String, dynamic>> operationalTasks = [
      {
        "icon": SvgPicture.asset(
          "assets/svg/Synchronising Data.svg",
          width: 20,
          height: 20,
        ),
        "title": appLocalizations.synchronize_data,
        "onTap": syncData,
        "subtitle": context.watch<SyncProvider>().lastSuccessfulSyncTime != ""
            ? DateFormat("EEEE d MMMM, hh:mma")
                .format(DateTime.parse(
                        context.watch<SyncProvider>().lastSuccessfulSyncTime)
                    .toLocal())
                .toString()
            : "Last Sync time not found",
      },
      // {
      //   "icon": SvgPicture.asset(
      //     "assets/svg/Uploading Local - Registration Data.svg",
      //   ),
      //   "title": appLocalizations.download_pre_registration_data,
      //   "onTap": () {},
      //   "subtitle": "Last downloaded on Friday 24 Mar, 12:15PM"
      // },
      {
        "icon": SvgPicture.asset(
          "assets/svg/Updating Operator Biometrics.svg",
        ),
        "title": AppLocalizations.of(context)!.update_operator_biomterics,
        "onTap": (context) async {
          await BiometricsApi().startOperatorOnboarding();
          globalProvider.onboardingProcessName = "Updation";
          Navigator.push(
              context,
              MaterialPageRoute(
                  builder: (context) =>
                      // Don't add const for loading the widget in a stateless class
                      OperatorOnboardingBiometricsCaptureControl()));
        },
        "subtitle": lastOperatorUpdateBiometricTime.toString() == ""
            ? "Not updated yet"
            : "Last Updated on ${lastOperatorUpdateBiometricTime.toString()}",
      },
      {
        "icon": SvgPicture.asset(
          "assets/svg/Uploading Local - Registration Data.svg",
        ),
        "title": appLocalizations.appliction_upload,
        "onTap": (context) {
          Navigator.push(
              context,
              MaterialPageRoute(
                  builder: (context) => const ExportPacketsPage()));
        },
        "subtitle":
            "${context.watch<RegistrationTaskProvider>().numberOfPackets} application(s)"
      },
      // {
      //   "icon": SvgPicture.asset(
      //     "assets/svg/Onboarding Yourself.svg",
      //   ),
      //   "title": appLocalizations.check_updates,
      //   "onTap": () {},
      //   "subtitle": "Last updated on Wednesday 12 Apr, 11:20PM"
      // },
      // {
      //   "icon": SvgPicture.asset(
      //     "assets/svg/Uploading Local - Registration Data.svg",
      //   ),
      //   "title": appLocalizations.center_remap_sync,
      //   "onTap": () {},
      //   "subtitle": "Last updated on Wednesday 12 Apr, 11:20PM"
      // },
      // {
      //   "icon": SvgPicture.asset(
      //     "assets/svg/Uploading Local - Registration Data.svg",
      //   ),
      //   "title": appLocalizations.sync_activities,
      //   "onTap": () {},
      //   "subtitle": "Last updated on Wednesday 12 Apr, 11:20PM"
      // },
      if (Provider.of<AuthProvider>(context, listen: false).isSupervisor)
        {
          "icon": SvgPicture.asset(
            "assets/svg/Uploading Local - Registration Data.svg",
          ),
          "title": appLocalizations.pending_approval,
          "onTap": (context) {
            Navigator.push(
                context,
                MaterialPageRoute(
                    builder: (context) => const ApprovePacketsPage()));
          },
          "subtitle":
              "${context.watch<ApprovePacketsProvider>().totalCreatedPackets} application(s)"
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
