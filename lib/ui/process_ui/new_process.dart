/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/biometric_attribute_data.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/model/screen.dart';
import 'package:registration_client/pigeon/biometrics_pigeon.dart';
import 'package:registration_client/pigeon/registration_data_pigeon.dart';
import 'package:registration_client/platform_spi/registration_service.dart';

import 'package:registration_client/provider/auth_provider.dart';
import 'package:registration_client/provider/connectivity_provider.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';

import 'package:registration_client/ui/common/tablet_header.dart';
import 'package:registration_client/ui/common/tablet_navbar.dart';
import 'package:registration_client/ui/post_registration/acknowledgement_page.dart';

import 'package:registration_client/ui/post_registration/preview_page.dart';
import 'package:registration_client/ui/process_ui/widgets/language_selector.dart';

import 'package:registration_client/ui/process_ui/widgets/new_process_screen_content.dart';

import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/utils/app_style.dart';

import '../../utils/life_cycle_event_handler.dart';

class NewProcess extends StatefulWidget {
  const NewProcess({super.key});

  static const routeName = '/new_process';

  @override
  State<NewProcess> createState() => _NewProcessState();
}

class _NewProcessState extends State<NewProcess> with WidgetsBindingObserver {
  late GlobalProvider globalProvider;
  late RegistrationTaskProvider registrationTaskProvider;
  bool isPortrait = true;

  List<String> postRegistrationTabs = [
    'Preview',
    'Authentication',
    'Acknowledgement',
  ];

  String username = '';
  String password = '';

  @override
  void initState() {
    _registrationScreenLoadedAudit();
    super.initState();
    WidgetsBinding.instance.addObserver(LifecycleEventHandler(
      resumeCallBack: () async {
        if (mounted) {
          setState(() {
            closeKeyboard();
          });
        }
      },
      suspendingCallBack: () async {
        if (mounted) {
          setState(() {
            closeKeyboard();
          });
        }
      },
    ));
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  void closeKeyboard() {
    FocusScope.of(context).unfocus();
  }

  void _showInSnackBar(String value) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(value),
      ),
    );
  }

  _showNetworkError() {
    _showInSnackBar(AppLocalizations.of(context)!.network_error);
  }

  _submitRegistration() async {
    RegistrationSubmitResponse registrationSubmitResponse =
        await registrationTaskProvider.submitRegistrationDto(username);

    return registrationSubmitResponse;
  }

  _getIsPacketAuthenticated() {
    return context.read<AuthProvider>().isPacketAuthenticated;
  }

  _authenticatePacket(BuildContext context) async {
    if (!_validateUsername(context)) {
      return false;
    }

    if (!_validatePassword(context)) {
      return false;
    }

    if (!_isUserLoggedInUser(context)) {
      return false;
    }

    await context.read<AuthProvider>().authenticatePacket(username, password);
    bool isPacketAuthenticated = _getIsPacketAuthenticated();

    if (!isPacketAuthenticated) {
      _showErrorInSnackbar();
      return false;
    }
    return true;
  }

  _showErrorInSnackbar() {
    String errorMsg = context.read<AuthProvider>().packetError;
    String snackbarText = "";

    switch (errorMsg) {
      case "REG_TRY_AGAIN":
        snackbarText = AppLocalizations.of(context)!.login_failed;
        break;

      case "REG_INVALID_REQUEST":
        snackbarText = AppLocalizations.of(context)!.password_incorrect;
        break;

      case "REG_NETWORK_ERROR":
        snackbarText = AppLocalizations.of(context)!.network_error;
        break;

      case "":
        return;

      default:
        snackbarText = errorMsg;
        break;
    }

    _showInSnackBar(snackbarText);
  }

  bool _validateUsername(BuildContext context) {
    if (username.trim().isEmpty) {
      _showInSnackBar(AppLocalizations.of(context)!.username_required);
      return false;
    }

    if (username.trim().length > 50) {
      _showInSnackBar(AppLocalizations.of(context)!.username_exceed);
      return false;
    }

    return true;
  }

  bool _validatePassword(BuildContext context) {
    if (password.trim().isEmpty) {
      _showInSnackBar(AppLocalizations.of(context)!.password_required);
      return false;
    }

    if (password.trim().length > 50) {
      _showInSnackBar(AppLocalizations.of(context)!.password_exceed);
      return false;
    }

    return true;
  }

  bool _isUserLoggedInUser(BuildContext context) {
    final user = context.read<AuthProvider>().currentUser;
    if (user.userId != username) {
      _showInSnackBar(AppLocalizations.of(context)!.invalid_user);
      return false;
    }
    return true;
  }

  // _onTabBackNavigate(int index, BuildContext context) {
  //   if (index < globalProvider.newProcessTabIndex) {
  //     globalProvider.newProcessTabIndex = index;
  //   }
  // }

  _resetValuesOnRegistrationComplete() {
    Navigator.of(context).pop();
    globalProvider.newProcessTabIndex = 0;
    globalProvider.htmlBoxTabIndex = 0;
    globalProvider.setRegId("");
    for (int i = 0;
        i < context.read<RegistrationTaskProvider>().listOfProcesses.length;
        i++) {
      Process process = Process.fromJson(
        jsonDecode(
          context
              .read<RegistrationTaskProvider>()
              .listOfProcesses
              .elementAt(i)
              .toString(),
        ),
      );
      if (process.id == "NEW") {
        getProcessUI(context, process);
      }
    }
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

  _newRegistrationClickedAudit() async {
    await context
        .read<GlobalProvider>()
        .getAudit("REG-HOME-002", "REG-MOD-102");
  }

  void _registrationScreenLoadedAudit() async {
    await context.read<GlobalProvider>().getAudit("REG-EVT-002", "REG-MOD-103");
  }

  _nextButtonClickedAudit() async {
    await context.read<GlobalProvider>().getAudit("REG-EVT-003", "REG-MOD-103");
  }

  _getIsConnected() {
    return context.read<ConnectivityProvider>().isConnected;
  }

  bool continueButton = false;
  @override
  Widget build(BuildContext context) {
    postRegistrationTabs = [
      AppLocalizations.of(context)!.preview_page,
      AppLocalizations.of(context)!.packet_auth_page,
      AppLocalizations.of(context)!.acknowledgement_page,
    ];
    isPortrait = MediaQuery.of(context).orientation == Orientation.portrait;
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    registrationTaskProvider =
        Provider.of<RegistrationTaskProvider>(context, listen: false);
    bool isMobile = MediaQuery.of(context).size.width < 750;
    double w = ScreenUtil().screenWidth;
    Map<String, dynamic> arguments =
        ModalRoute.of(context)!.settings.arguments! as Map<String, dynamic>;
    final Process newProcess = arguments["process"];
    int size = newProcess.screens!.length;
    evaluateMVEL(
        String fieldData, String? engine, String? expression, Field e) async {
      final RegistrationService registrationService = RegistrationService();
      bool required =
          await registrationService.evaluateMVEL(fieldData, expression!);
      return required;
    }

    isExceptionPresent(String id) {
      bool isExceptionPresent = false;
      for (BiometricAttributeData x
          in context.read<GlobalProvider>().fieldInputValue[id]) {
        if (x.exceptions.contains(true) || x.title == "Exception") {
          isExceptionPresent = true;
          break;
        }
      }
      return isExceptionPresent;
    }

    returnBiometricListLength(List<String?>? list, String id) {
      int i = 0;
      if (list!.contains("leftEye") && list.contains("rightEye")) {
        i++;
      }
      if (list.contains("rightIndex") &&
          list.contains("rightLittle") &&
          list.contains("rightRing") &&
          list.contains("rightMiddle")) {
        i++;
      }
      if (list.contains("leftIndex") &&
          list.contains("leftLittle") &&
          list.contains("leftRing") &&
          list.contains("leftMiddle")) {
        i++;
      }
      if (list.contains("rightThumb") && list.contains("rightThumb")) {
        i++;
      }
      if (list.contains("face")) {
        i++;
      }
      if (isExceptionPresent(id) == true) {
        i++;
      }
      return i;
    }

    customValidation(int currentIndex) async {
      bool isValid = true;
      Screen screen = newProcess.screens!.elementAt(currentIndex)!;
      for (int i = 0; i < screen.fields!.length; i++) {
        if (screen.fields!.elementAt(i)!.inputRequired! &&
            screen.fields!.elementAt(i)!.required!) {
          if (!(context
                  .read<GlobalProvider>()
                  .fieldInputValue
                  .containsKey(screen.fields!.elementAt(i)!.id)) &&
              !(context
                  .read<GlobalProvider>()
                  .fieldInputValue
                  .containsKey(screen.fields!.elementAt(i)!.subType)) &&
              !(context.read<GlobalProvider>().fieldInputValue.containsKey(
                  "${screen.fields!.elementAt(i)!.group}${screen.fields!.elementAt(i)!.subType}"))) {
            // log("field: ${screen.fields!.elementAt(i)!.group}${screen.fields!.elementAt(i)!.subType}");

            // if (screen.fields!.elementAt(i)!.controlType == "fileupload") {
            //   _showInSnackBar(AppLocalizations.of(context)!.upload_document);
            // }
            isValid = false;

            break;
          }
          if (screen.fields!.elementAt(i)!.conditionalBioAttributes != null &&
              screen.fields!
                  .elementAt(i)!
                  .conditionalBioAttributes!
                  .isNotEmpty) {
            String response = await BiometricsApi().getAgeGroup();
            if (!(response.compareTo(screen.fields!
                    .elementAt(i)!
                    .conditionalBioAttributes!
                    .first!
                    .ageGroup!) ==
                0)) {
              if (screen.fields!.elementAt(i)!.controlType == "biometrics") {
                int count = returnBiometricListLength(
                    screen.fields!.elementAt(i)!.bioAttributes,
                    screen.fields!.elementAt(i)!.id!);
                if (globalProvider
                        .completeException[screen.fields!.elementAt(i)!.id!] !=
                    null) {
                  int length = globalProvider
                      .completeException[screen.fields!.elementAt(i)!.id!]
                      .length;
                  count = count - length;
                }

                if (globalProvider
                        .fieldInputValue[screen.fields!.elementAt(i)!.id!]
                        .length <
                    count) {
                  isValid = false;

                  break;
                }
              }
            }
          }
        }
        if (screen.fields!.elementAt(i)!.requiredOn!.isNotEmpty) {
          bool required = await evaluateMVEL(
              jsonEncode(screen.fields!.elementAt(i)!.toJson()),
              screen.fields!.elementAt(i)!.requiredOn?[0]?.engine,
              screen.fields!.elementAt(i)!.requiredOn?[0]?.expr,
              screen.fields!.elementAt(i)!);
          if (required) {
            if (screen.fields!.elementAt(i)!.inputRequired!) {
              if (!(globalProvider.fieldInputValue
                      .containsKey(screen.fields!.elementAt(i)!.id)) &&
                  !(globalProvider.fieldInputValue
                      .containsKey(screen.fields!.elementAt(i)!.subType)) &&
                  !(globalProvider.fieldInputValue.containsKey(
                      "${screen.fields!.elementAt(i)!.group}${screen.fields!.elementAt(i)!.subType}"))) {
                isValid = false;

                break;
              }
            }
          }
        }
        if (screen.fields!.elementAt(i)!.conditionalBioAttributes != null &&
            screen.fields!.elementAt(i)!.conditionalBioAttributes!.isNotEmpty) {
          String response = await BiometricsApi().getAgeGroup();
          if (response.compareTo(screen.fields!
                  .elementAt(i)!
                  .conditionalBioAttributes!
                  .first!
                  .ageGroup!) ==
              0) {
            bool valid = await BiometricsApi()
                .conditionalBioAttributeValidation(
                    screen.fields!.elementAt(i)!.id!,
                    screen.fields!
                        .elementAt(i)!
                        .conditionalBioAttributes!
                        .first!
                        .validationExpr!);
            if (!valid) {
              isValid = false;
              break;
            }
          }
        }
      }
      return isValid;
    }

    continueButtonTap(BuildContext context, int size, newProcess) async {
      if (globalProvider.newProcessTabIndex < size) {
        bool customValidator =
            await customValidation(globalProvider.newProcessTabIndex);
        if (customValidator) {
          if (globalProvider.formKey.currentState!.validate()) {
            if (globalProvider.newProcessTabIndex ==
                newProcess.screens!.length - 1) {
              registrationTaskProvider.setPreviewTemplate("");
              registrationTaskProvider.setAcknowledgementTemplate("");
              await registrationTaskProvider.getPreviewTemplate(true);
              await registrationTaskProvider.getAcknowledgementTemplate(false);
            }

            globalProvider.newProcessTabIndex =
                globalProvider.newProcessTabIndex + 1;
          }
        }

        _nextButtonClickedAudit();
      } else {
        if (globalProvider.newProcessTabIndex == size + 1) {
          bool isPacketAuthenticated = await _authenticatePacket(context);
          if (!isPacketAuthenticated) {
            return;
          }
          RegistrationSubmitResponse registrationSubmitResponse =
              await _submitRegistration();
          if (registrationSubmitResponse.errorCode!.isNotEmpty) {
            _showInSnackBar(registrationSubmitResponse.errorCode!);
            return;
          }
          globalProvider.setRegId(registrationSubmitResponse.rId);
          setState(() {
            username = '';
            password = '';
          });
        }
        if (globalProvider.newProcessTabIndex == size + 2) {
          _resetValuesOnRegistrationComplete();
          return;
        }
        globalProvider.newProcessTabIndex =
            globalProvider.newProcessTabIndex + 1;
      }
    }

    customValidation(globalProvider.newProcessTabIndex).then((value) {
      continueButton = value;
    });

    return SafeArea(
      child: Scaffold(
        backgroundColor: secondaryColors.elementAt(10),
        bottomNavigationBar: Container(
          decoration: BoxDecoration(
            border: const Border(
              top: BorderSide(
                color: dividerColor,
                width: 1,
              ),
            ),
            color: pureWhite,
          ),
          padding: EdgeInsets.symmetric(
            horizontal: isPortrait ? 20.w : 60.w,
            vertical: 16.h,
          ),
          // height: isPortrait ? 94.h : 84.h,
          child: globalProvider.newProcessTabIndex == 0
              ? Row(
                  children: [
                    Expanded(
                      child: OutlinedButton(
                        style: ElevatedButton.styleFrom(
                            backgroundColor: appWhite,
                            side: BorderSide(color: solidPrimary),
                            shape: const RoundedRectangleBorder(borderRadius: BorderRadius.all(Radius.circular(5.0)))),
                        child: SizedBox(
                          height: isPortrait && !isMobileSize ? 68.h : 52.h,
                          child: Center(
                            child: Text(
                              AppLocalizations.of(context)!.go_back,
                              style: TextStyle(
                                fontSize: isPortrait && !isMobileSize ? 22 : 14,
                              ),
                            ),
                          ),
                        ),
                        onPressed: () {
                          Navigator.of(context).pop();
                        },
                      ),
                    ),
                    SizedBox(
                      width: 10.w,
                    ),
                    Expanded(
                      child: ElevatedButton(
                        style: ElevatedButton.styleFrom(backgroundColor: solidPrimary,shape: const RoundedRectangleBorder(borderRadius: BorderRadius.all(Radius.circular(5.0)))),
                        child: SizedBox(
                          height: isPortrait && !isMobileSize ? 68.h : 52.h,
                          child: Center(
                            child: Text(
                              AppLocalizations.of(context)!.informed,
                              style: TextStyle(
                                fontSize: isPortrait && !isMobileSize ? 22 : 14,
                                  color: appWhite
                              ),
                            ),
                          ),
                        ),
                        onPressed: () {
                          continueButtonTap(context, size, newProcess);
                        },
                      ),
                    ),
                  ],
                )
              : Row(
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    !isPortrait && globalProvider.newProcessTabIndex == size + 2
                        ? ElevatedButton(
                      style: ElevatedButton.styleFrom(
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(5.0)),
                          backgroundColor: solidPrimary
                      ),
                            onPressed: () async {
                              await context
                                  .read<ConnectivityProvider>()
                                  .checkNetworkConnection();
                              bool isConnected = _getIsConnected();
                              if (!isConnected) {
                                _showNetworkError();
                                return;
                              }
                              globalProvider.syncPacket(globalProvider.regId);
                            },
                            child:
                                Text(AppLocalizations.of(context)!.sync_packet,style: const TextStyle(color: appWhite)),
                          )
                        : const SizedBox.shrink(),
                    SizedBox(
                      width: 10.w,
                    ),
                    !isPortrait && globalProvider.newProcessTabIndex == size + 2
                        ? ElevatedButton(
                      style: ElevatedButton.styleFrom(
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(5.0)),
                          backgroundColor: solidPrimary
                      ),
                            onPressed: () async {
                              await context
                                  .read<ConnectivityProvider>()
                                  .checkNetworkConnection();
                              bool isConnected = _getIsConnected();
                              if (!isConnected) {
                                _showNetworkError();
                                return;
                              }
                              globalProvider.uploadPacket(globalProvider.regId);
                            },
                            child: Text(
                                AppLocalizations.of(context)!.upload_packet,style: const TextStyle(color: appWhite)),
                          )
                        : const SizedBox.shrink(),
                    const Expanded(
                      child: SizedBox(),
                    ),
                    ElevatedButton(
                      style: ButtonStyle(
                        maximumSize: MaterialStateProperty.all<Size>(
                            const Size(209, 52)),
                        minimumSize: MaterialStateProperty.all<Size>(
                            const Size(209, 52)),
                        backgroundColor: MaterialStateProperty.all<Color>(
                            continueButton ? solidPrimary : Colors.grey),
                          shape: MaterialStatePropertyAll(RoundedRectangleBorder(borderRadius: BorderRadius.circular(5.0)))
                      ),
                      onPressed: () {
                        continueButtonTap(context, size, newProcess);
                      },
                      child: Text(context
                                  .read<GlobalProvider>()
                                  .newProcessTabIndex <=
                              size
                          ? AppLocalizations.of(context)!.continue_text
                          : globalProvider.newProcessTabIndex == size + 1
                              ? AppLocalizations.of(context)!.authenticate
                              : AppLocalizations.of(context)!.new_registration,style: isMobile
                          ? AppTextStyle.tabletPortraitButtonText
                          : AppTextStyle.mobileButtonText),
                    ),
                  ],
                ),
        ),
        body: SingleChildScrollView(
          child: AnnotatedRegion<SystemUiOverlayStyle>(
            value: const SystemUiOverlayStyle(
              statusBarColor: Colors.transparent,
            ),
            child: Column(
              children: [
                isPortrait
                    ? const SizedBox()
                    : const Column(
                        children: [
                          TabletHeader(),
                          TabletNavbar(),
                        ],
                      ),
                Container(
                  padding: isMobile && !isMobileSize
                      ? const EdgeInsets.fromLTRB(0, 46, 0, 0)
                      : const EdgeInsets.fromLTRB(0, 0, 0, 0),
                  decoration: const BoxDecoration(
                    gradient: LinearGradient(
                      begin: Alignment.topCenter,
                      end: Alignment.bottomCenter,
                      colors: [Color(0xff214FBF), Color(0xff1C43A1)],
                    ),
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      SizedBox(
                        width: w,
                        height: isPortrait ? 21.w : 30.w,
                      ),
                      Padding(
                        padding: isPortrait
                            ? EdgeInsets.fromLTRB(20.w, 0, 0, 0)
                            : EdgeInsets.fromLTRB(60.w, 0, 60.w, 0),
                        child: Text(
                          newProcess.label![
                              context.read<GlobalProvider>().selectedLanguage]!,
                          style: Theme.of(context)
                              .textTheme
                              .titleMedium
                              ?.copyWith(
                                  color: pureWhite,
                                  fontWeight: semiBold,
                                  fontSize: isPortrait ? 24 : 21),
                        ),
                      ),
                      SizedBox(
                        height: 30.h,
                      ),
                      Divider(
                        height: 12.h,
                        thickness: 1,
                        color: secondaryColors.elementAt(2),
                      ),
                      Padding(
                        padding: isPortrait
                            ? const EdgeInsets.all(0)
                            : EdgeInsets.fromLTRB(60.w, 0, 60.w, 0),
                        child: Stack(
                          alignment: FractionalOffset.centerRight,
                          children: [
                            Padding(
                              padding: isPortrait
                                  ? EdgeInsets.fromLTRB(20.w, 10.h, 0, 0)
                                  : EdgeInsets.fromLTRB(0, 10.h, 0, 0),
                              child: SizedBox(
                                height: 36.h,
                                child: ListView.builder(
                                    padding: const EdgeInsets.all(0),
                                    scrollDirection: Axis.horizontal,
                                    itemCount: newProcess.screens!.length + 3,
                                    itemBuilder:
                                        (BuildContext context, int index) {
                                      return GestureDetector(
                                        onTap: () {
                                          if (context
                                                  .read<GlobalProvider>()
                                                  .newProcessTabIndex ==
                                              size + 2) {
                                            return;
                                          }

                                          if (index <
                                              context
                                                  .read<GlobalProvider>()
                                                  .newProcessTabIndex) {
                                            context
                                                .read<GlobalProvider>()
                                                .newProcessTabIndex = index;
                                          }
                                        },
                                        child: Row(
                                          children: [
                                            Container(
                                              padding: EdgeInsets.fromLTRB(
                                                  0, 0, 0, 8.h),
                                              decoration: BoxDecoration(
                                                border: Border(
                                                  bottom: BorderSide(
                                                      color: (context
                                                                  .watch<
                                                                      GlobalProvider>()
                                                                  .newProcessTabIndex ==
                                                              index)
                                                          ? pureWhite
                                                          : Colors.transparent,
                                                      width: 3),
                                                ),
                                              ),
                                              child: Row(
                                                children: [
                                                  (index <
                                                          context
                                                              .watch<
                                                                  GlobalProvider>()
                                                              .newProcessTabIndex)
                                                      ? Icon(
                                                          Icons.check_circle,
                                                          size: 17,
                                                          color: secondaryColors
                                                              .elementAt(11),
                                                        )
                                                      : (context
                                                                  .watch<
                                                                      GlobalProvider>()
                                                                  .newProcessTabIndex ==
                                                              index)
                                                          ? Icon(
                                                              Icons.circle,
                                                              color: pureWhite,
                                                              size: 17,
                                                            )
                                                          : Icon(
                                                              Icons
                                                                  .circle_outlined,
                                                              size: 17,
                                                              color:
                                                                  secondaryColors
                                                                      .elementAt(
                                                                          9),
                                                            ),
                                                  SizedBox(
                                                    width: 6.w,
                                                  ),
                                                  Text(
                                                    index < size
                                                        ? newProcess
                                                                .screens![index]!
                                                                .label![
                                                            context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .selectedLanguage]!
                                                        : postRegistrationTabs[
                                                            index - size],
                                                    style: Theme.of(context)
                                                        .textTheme
                                                        .titleSmall
                                                        ?.copyWith(
                                                            color: (context
                                                                        .watch<
                                                                            GlobalProvider>()
                                                                        .newProcessTabIndex ==
                                                                    index)
                                                                ? pureWhite
                                                                : secondaryColors
                                                                    .elementAt(
                                                                        9),
                                                            fontWeight:
                                                                semiBold,
                                                            fontSize: 14),
                                                  ),
                                                ],
                                              ),
                                            ),
                                            SizedBox(
                                              width: 35.w,
                                            ),
                                          ],
                                        ),
                                      );
                                    }),
                              ),
                            ),
                            Container(
                              height: 36.h,
                              width: 25.w,
                              padding: const EdgeInsets.fromLTRB(0, 0, 0, 0),
                              color: solidPrimary,
                              child: Icon(
                                Icons.arrow_forward_ios_outlined,
                                color: pureWhite,
                                size: 17,
                              ),
                            ),
                          ],
                        ),
                      ),
                      const SizedBox(
                        height: 5,
                      ),
                    ],
                  ),
                ),
                Padding(
                  padding: isPortrait
                      ? const EdgeInsets.all(0)
                      : EdgeInsets.fromLTRB(60.w, 0, 60.w, 0),
                  child: context.watch<GlobalProvider>().newProcessTabIndex <
                          size
                      ? NewProcessScreenContent(
                          context: context,
                          screen: newProcess.screens!.elementAt(context
                              .watch<GlobalProvider>()
                              .newProcessTabIndex)!)
                      : context.watch<GlobalProvider>().newProcessTabIndex ==
                              size
                          ? const PreviewPage()
                          : context
                                      .watch<GlobalProvider>()
                                      .newProcessTabIndex ==
                                  size + 1
                              ? _getPacketAuthComponent()
                              : const AcknowledgementPage(),
                ),
                SizedBox(
                  height: 20.h,
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _getPacketAuthComponent() {
    return Column(
      children: [
        SizedBox(
          height: 30.h,
        ),
        Container(
          width: isPortrait && !isMobileSize ? 566.w : 376.w,
          padding: EdgeInsets.only(
            top: 24.h,
            bottom: 28.h,
            left: 20.w,
            right: 20.w,
          ),
          decoration: BoxDecoration(
            borderRadius: const BorderRadius.all(
              Radius.circular(6),
            ),
            color: pureWhite,
          ),
          child: Column(
            children: [
              _getAuthIcon(),
              SizedBox(
                height: 26.h,
              ),
              Text(
                AppLocalizations.of(context)!.authenticate_using_password,
                style: TextStyle(
                    fontSize: isPortrait && !isMobileSize ? 24 : 18,
                    fontWeight: semiBold,
                    color: appBlack),
              ),
              SizedBox(
                height: 35.h,
              ),
              Row(
                children: [
                  Text(
                    AppLocalizations.of(context)!.username,
                    style: isPortrait
                        ? AppTextStyle.tabletPortraitTextfieldHeader
                        : AppTextStyle.mobileTextfieldHeader,
                  ),
                  const Text(
                    ' *',
                    style: TextStyle(
                      color: mandatoryField,
                    ),
                  ),
                ],
              ),
              SizedBox(
                height: 11.h,
              ),
              _getUsernameTextField(),
              SizedBox(
                height: 35.h,
              ),
              Row(
                children: [
                  Text(
                    AppLocalizations.of(context)!.password,
                    style: isPortrait
                        ? AppTextStyle.tabletPortraitTextfieldHeader
                        : AppTextStyle.mobileTextfieldHeader,
                  ),
                  const Text(
                    ' *',
                    style: TextStyle(color: mandatoryField),
                  ),
                ],
              ),
              SizedBox(
                height: 11.h,
              ),
              _getPasswordTextField(),
            ],
          ),
        ),
      ],
    );
  }

  _getAuthIcon() {
    return Container(
      height: 80.w,
      width: 80.w,
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        border: Border.all(
          color: authIconBorder,
          width: 2,
        ),
        color: authIconBackground,
      ),
      child: Center(
        child: Image.asset('assets/images/Registering an Individual@2x.png'),
      ),
    );
  }

  _getUsernameTextField() {
    return Container(
      height: isPortrait && !isMobileSize ? 82.h : 52.h,
      alignment: Alignment.centerLeft,
      padding: EdgeInsets.symmetric(
        horizontal: 12.w,
      ),
      decoration: BoxDecoration(
        border: Border.all(
          width: 1.h,
          color: appGreyShade,
        ),
        borderRadius: const BorderRadius.all(
          Radius.circular(6),
        ),
      ),
      child: TextField(
        decoration: InputDecoration(
          hintText: AppLocalizations.of(context)!.enter_username,
          hintStyle: isPortrait
              ? AppTextStyle.tabletPortraitTextfieldHintText
              : AppTextStyle.mobileTextfieldHintText,
          border: InputBorder.none,
        ),
        style: TextStyle(
          fontSize: isPortrait && !isMobileSize ? 22 : 14,
          color: appBlack,
        ),
        onChanged: (v) {
          setState(() {
            username = v;
          });
        },
      ),
    );
  }

  _getPasswordTextField() {
    return Container(
      height: isPortrait && !isMobileSize ? 82.h : 52.h,
      alignment: Alignment.centerLeft,
      padding: EdgeInsets.symmetric(
        horizontal: 12.w,
      ),
      decoration: BoxDecoration(
        border: Border.all(
          width: 1.h,
          color: appGreyShade,
        ),
        borderRadius: const BorderRadius.all(
          Radius.circular(6),
        ),
      ),
      child: TextField(
        obscureText: true,
        decoration: InputDecoration(
          hintText: AppLocalizations.of(context)!.enter_password,
          hintStyle: isPortrait
              ? AppTextStyle.tabletPortraitTextfieldHintText
              : AppTextStyle.mobileTextfieldHintText,
          border: InputBorder.none,
        ),
        style: TextStyle(
          fontSize: isPortrait && !isMobileSize ? 22 : 14,
          color: appBlack,
        ),
        onChanged: (v) {
          setState(() {
            password = v;
          });
        },
      ),
    );
  }
}
