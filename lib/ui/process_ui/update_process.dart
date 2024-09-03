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
import 'package:registration_client/pigeon/demographics_data_pigeon.dart';
import 'package:registration_client/pigeon/registration_data_pigeon.dart';

import 'package:registration_client/provider/auth_provider.dart';
import 'package:registration_client/provider/connectivity_provider.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';

import 'package:registration_client/ui/post_registration/acknowledgement_page.dart';

import 'package:registration_client/ui/post_registration/preview_page.dart';
import 'package:registration_client/ui/process_ui/widgets/update_field_selector.dart';

import 'package:registration_client/ui/process_ui/widgets/update_process_screen_content.dart';

import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/utils/app_style.dart';

import '../../utils/life_cycle_event_handler.dart';

class UpdateProcess extends StatefulWidget {
  const UpdateProcess({
    super.key,
  });

  static const routeName = '/update_process';

  @override
  State<UpdateProcess> createState() => _UpdateProcessState();
}

class _UpdateProcessState extends State<UpdateProcess>
    with WidgetsBindingObserver {
  late GlobalProvider globalProvider;
  late RegistrationTaskProvider registrationTaskProvider;
  late AuthProvider authProvider;
  late ConnectivityProvider connectivityProvider;
  late AppLocalizations appLocalizations = AppLocalizations.of(context)!;
  bool isPortrait = true;
  bool fieldSelectionCompleted = false;

  List<String> postRegistrationTabs = [
    'Preview',
    'Authentication',
    'Acknowledgement',
  ];

  Map<String, String>? templateTitleMap;

  String username = '';
  String password = '';

  @override
  void initState() {
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    registrationTaskProvider =
        Provider.of<RegistrationTaskProvider>(context, listen: false);
    authProvider = Provider.of<AuthProvider>(context, listen: false);
    connectivityProvider =
        Provider.of<ConnectivityProvider>(context, listen: false);
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
    _registrationScreenLoadedAudit();
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

  _authenticatePacket(BuildContext context) async {
    if (!_validateUsername(context)) {
      return false;
    }

    if (!_validatePassword(context)) {
      return false;
    }

    if (authProvider.currentUser.userId != username) {
      _showInSnackBar(appLocalizations.invalid_user);
      return false;
    }

    await authProvider.authenticatePacket(username, password);

    if (!authProvider.isPacketAuthenticated) {
      _showErrorInSnackbar();
      return false;
    }
    return true;
  }

  _showErrorInSnackbar() {
    String errorMsg = authProvider.packetError;
    String snackbarText = "";

    switch (errorMsg) {
      case "REG_TRY_AGAIN":
        snackbarText = appLocalizations.login_failed;
        break;

      case "REG_INVALID_REQUEST":
        snackbarText = appLocalizations.password_incorrect;
        break;

      case "REG_NETWORK_ERROR":
        snackbarText = appLocalizations.network_error;
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
      _showInSnackBar(appLocalizations.username_required);
      return false;
    }

    if (username.trim().length > 50) {
      _showInSnackBar(appLocalizations.username_exceed);
      return false;
    }

    return true;
  }

  bool _validatePassword(BuildContext context) {
    if (password.trim().isEmpty) {
      _showInSnackBar(appLocalizations.password_required);
      return false;
    }

    if (password.trim().length > 50) {
      _showInSnackBar(appLocalizations.password_exceed);
      return false;
    }

    return true;
  }

  _resetValuesOnRegistrationComplete() {
    Navigator.of(context).pop();
  }

  void _registrationScreenLoadedAudit() async {
    await globalProvider.getAudit("REG-EVT-002", "REG-MOD-103");
  }

  _nextButtonClickedAudit() async {
    await globalProvider.getAudit("REG-EVT-003", "REG-MOD-103");
  }

  bool continueButton = false;

  @override
  Widget build(BuildContext context) {
    postRegistrationTabs = [
      appLocalizations.preview_page,
      appLocalizations.packet_auth_page,
      appLocalizations.acknowledgement_page,
    ];
    isPortrait = MediaQuery.of(context).orientation == Orientation.portrait;
    bool isMobile = MediaQuery.of(context).size.width < 750;
    double w = ScreenUtil().screenWidth;
    Map<String, dynamic> arguments =
        ModalRoute.of(context)!.settings.arguments! as Map<String, dynamic>;
    final Process newProcess = arguments["process"];
    int size = newProcess.screens!.length;

    Future<bool> onWillPop() async {
      if (globalProvider.newProcessTabIndex > 0 &&
          globalProvider.newProcessTabIndex <
              newProcess.screens!.length + postRegistrationTabs.length - 1) {
        globalProvider.newProcessTabIndex =
            globalProvider.newProcessTabIndex - 1;
      } else if (globalProvider.newProcessTabIndex == 0 &&
          fieldSelectionCompleted) {
        setState(() {
          fieldSelectionCompleted = false;
        });
      } else {
        return true;
      }
      return false;
    }

    isExceptionPresent(String id) {
      bool isExceptionPresent = false;
      for (BiometricAttributeData x in globalProvider.fieldInputValue[id]) {
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

    biometricRequiredFieldValidation(Field field) async {
      if (field.controlType == "biometrics") {
        int count = returnBiometricListLength(field.bioAttributes, field.id!);
        if (globalProvider.completeException[field.id!] != null) {
          int length = globalProvider.completeException[field.id!].length;
          count = count - length;
        }

        if (globalProvider.fieldInputValue[field.id!].length < count) {
          return false;
        }
      }

      return true;
    }

    biometricConditionalFieldValidation(Field field) async {
      bool valid = await BiometricsApi().conditionalBioAttributeValidation(
          field.id!, field.conditionalBioAttributes!.first!.validationExpr!);
      if (field.exceptionPhotoRequired == true) {
        List<BiometricAttributeData> biometricAttributeDataList =
            globalProvider.fieldInputValue[field.id!];
        bool isExceptionPresent = false;
        bool isExceptionAttributePresent = false;
        for (var biometricAttributeData in biometricAttributeDataList) {
          if (globalProvider.exceptionAttributes
              .contains(biometricAttributeData.title)) {
            isExceptionPresent = true;
          }
          if (biometricAttributeData.title == "Exception") {
            isExceptionAttributePresent = true;
          }
        }

        if (isExceptionPresent == true &&
            isExceptionAttributePresent == false) {
          return false;
        }
      }
      if (!valid) {
        return false;
      }
      return true;
    }

    biometricValidation(Field field) async {
      if (field.conditionalBioAttributes != null &&
          field.conditionalBioAttributes!.isNotEmpty) {
        String response = await BiometricsApi().getAgeGroup();
        if (response
                .compareTo(field.conditionalBioAttributes!.first!.ageGroup!) !=
            0) {
          bool isValid = true;
          if (field.conditionalBioAttributes!.first!.ageGroup! == "ALL") {
            isValid = await biometricConditionalFieldValidation(field);
          } else {
            isValid = await biometricRequiredFieldValidation(field);
          }

          if (!isValid) {
            return false;
          }
        }

        if (response
                .compareTo(field.conditionalBioAttributes!.first!.ageGroup!) ==
            0) {
          bool isValid = await biometricConditionalFieldValidation(field);
          if (!isValid) {
            return false;
          }
        }
      }

      return true;
    }

    ageDateChangeValidation(int currentIndex) async {
      if (globalProvider.newProcessTabIndex < size) {
        Screen screen = newProcess.screens!.elementAt(currentIndex)!;
        for (int i = 0; i < screen.fields!.length; i++) {
          if (screen.fields!.elementAt(i)!.id == "dateOfBirth") {
            if (globalProvider.checkAgeGroupChange == "") {
              globalProvider.checkAgeGroupChange = globalProvider.ageGroup;
            } else {
              if (globalProvider.checkAgeGroupChange
                      .compareTo(globalProvider.ageGroup) ==
                  0) {
              } else {
                List<Screen?> screens = [];
                for (int i = 0;
                    i < registrationTaskProvider.listOfProcesses.length;
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
                  if (process.id == "NEW" || process.id == "UPDATE") {
                    screens = process.screens!;
                  }
                }
                for (Screen? screen in screens) {
                  if (screen!.name! == "Documents" ||
                      screen.name! == "BiometricDetails") {
                    for (Field? field in screen.fields!) {
                      globalProvider.fieldInputValue.remove(field!.id);
                    }
                  }
                }
                await BiometricsApi().clearBiometricAndDocumentHashmap();
                globalProvider.clearExceptions();
                globalProvider.checkAgeGroupChange = globalProvider.ageGroup;
              }
            }
          }
        }
      }
    }

    customValidation(int currentIndex) async {
      if (currentIndex == 0) {
        if (globalProvider.updateUINNumber.isEmpty) {
          return false;
        }
        return true;
      }
      if (globalProvider.newProcessTabIndex < size) {
        Screen screen = newProcess.screens!.elementAt(currentIndex)!;
        for (int i = 0; i < screen.fields!.length; i++) {
          Field field = screen.fields!.elementAt(i)!;
          String group = field.group!;
          String fieldId = field.id!;
          if (globalProvider.selectedUpdateFields[group] != null) {
            if (field.required! ||
                (globalProvider.mvelRequiredFields[fieldId] ?? false)) {
              if (!(globalProvider.fieldInputValue.containsKey(field.id)) &&
                  !(globalProvider.fieldInputValue
                      .containsKey(field.subType)) &&
                  !(globalProvider.fieldInputValue
                      .containsKey("${field.group}${field.subType}"))) {
                return false;
              }

              bool isValid = await biometricValidation(field);
              if (!isValid) {
                return false;
              }
            }
          } else if (newProcess.autoSelectedGroups!.contains(group)) {
            if (globalProvider.mvelRequiredFields[fieldId] ?? false) {
              if (!(globalProvider.fieldInputValue.containsKey(field.id)) &&
                  !(globalProvider.fieldInputValue
                      .containsKey(field.subType)) &&
                  !(globalProvider.fieldInputValue
                      .containsKey("${field.group}${field.subType}"))) {
                return false;
              }

              if (field.conditionalBioAttributes != null &&
                  field.conditionalBioAttributes!.isNotEmpty) {
                bool isValid = await biometricConditionalFieldValidation(field);
                if (!isValid) {
                  return false;
                }
              }
            }
          }
        }
      }
      return true;
    }

    continueButtonTap(int size, newProcess) async {
      if (globalProvider.newProcessTabIndex == 0 && !fieldSelectionCompleted) {
        if (globalProvider.selectedUpdateFields.isEmpty ||
            globalProvider.updateFieldKey.currentState == null ||
            !globalProvider.updateFieldKey.currentState!.validate()) {
          return;
        }
      }
      if (!fieldSelectionCompleted) {
        globalProvider.clearMap();
        globalProvider.clearScannedPages();
        globalProvider.clearExceptions();
        await registrationTaskProvider.changeUpdatableFieldGroups();
        globalProvider.ageGroup = "";
        await BiometricsApi().clearBiometricAndDocumentHashmap();
        setState(() {
          fieldSelectionCompleted = true;
        });
        return;
      }
      if (globalProvider.newProcessTabIndex < size) {
        ageDateChangeValidation(globalProvider.newProcessTabIndex);
        bool customValidator =
            await customValidation(globalProvider.newProcessTabIndex);
        if (customValidator) {
          if (globalProvider.formKey.currentState!.validate()) {
            if (globalProvider.newProcessTabIndex ==
                newProcess.screens!.length - 1) {
              templateTitleMap = {
                'demographicInfo': appLocalizations.demographic_information,
                'documents': appLocalizations.documents,
                'bioMetrics': appLocalizations.biometrics,
              };
              registrationTaskProvider.setPreviewTemplate("");
              registrationTaskProvider.setAcknowledgementTemplate("");
              await registrationTaskProvider.getPreviewTemplate(
                  true, templateTitleMap!);
              await registrationTaskProvider.getAcknowledgementTemplate(
                  false, templateTitleMap!);
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
              await registrationTaskProvider.submitRegistrationDto(username);
          if (registrationSubmitResponse.errorCode!.isNotEmpty) {
            _showInSnackBar(registrationSubmitResponse.errorCode!);
            return;
          }
          globalProvider.setRegId(registrationSubmitResponse.rId);

          // Updating key to packetId after success creation of packet
          registrationTaskProvider
              .updateTemplateStorageKey(registrationSubmitResponse.rId);
          registrationTaskProvider.deleteDefaultTemplateStored();

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
      if (globalProvider.newProcessTabIndex == 0) {
        if (!fieldSelectionCompleted) {
          continueButton = value &&
              globalProvider.updateFieldKey.currentState != null &&
              globalProvider.updateFieldKey.currentState!.validate();
        } else {
          continueButton = true;
        }
      } else {
        continueButton = value &&
            globalProvider.formKey.currentState != null &&
            globalProvider.formKey.currentState!.validate();
      }

      if (globalProvider.newProcessTabIndex >= size) {
        continueButton = true;
      }
    });

    return WillPopScope(
      onWillPop: onWillPop,
      child: SafeArea(
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
            child: context.watch<GlobalProvider>().newProcessTabIndex == 0
                ? Row(
                    children: [
                      Expanded(
                        child: fieldSelectionCompleted
                            ? OutlinedButton(
                                child: SizedBox(
                                  height:
                                      isPortrait && !isMobileSize ? 68.h : 52.h,
                                  child: Center(
                                    child: Text(
                                      appLocalizations.go_back,
                                      style: TextStyle(
                                        fontSize: isPortrait && !isMobileSize
                                            ? 22
                                            : 14,
                                      ),
                                    ),
                                  ),
                                ),
                                onPressed: () {
                                  if (!fieldSelectionCompleted) {
                                    Navigator.of(context).pop();
                                  } else {
                                    setState(() {
                                      fieldSelectionCompleted = false;
                                    });
                                  }
                                },
                              )
                            : const SizedBox(),
                      ),
                      SizedBox(
                        width: 10.w,
                      ),
                      Expanded(
                        child: ElevatedButton(
                          style: ButtonStyle(
                            maximumSize: MaterialStateProperty.all<Size>(
                                const Size(209, 52)),
                            minimumSize: MaterialStateProperty.all<Size>(
                                const Size(209, 52)),
                            backgroundColor: MaterialStateProperty.all<Color>(
                                !fieldSelectionCompleted
                                    ? (context
                                                .watch<GlobalProvider>()
                                                .selectedUpdateFields
                                                .isNotEmpty &&
                                            continueButton)
                                        ? solidPrimary
                                        : Colors.grey
                                    : solidPrimary),
                          ),
                          child: SizedBox(
                            height: isPortrait && !isMobileSize ? 68.h : 52.h,
                            child: Center(
                              child: Text(
                                fieldSelectionCompleted
                                    ? appLocalizations.informed
                                    : appLocalizations.continue_text,
                                style: TextStyle(
                                  fontSize:
                                      isPortrait && !isMobileSize ? 22 : 14,
                                ),
                              ),
                            ),
                          ),
                          onPressed: () async {
                            if (fieldSelectionCompleted) {
                              registrationTaskProvider.addConsentField("Y");
                              await DemographicsApi().addDemographicField(
                                  "UIN", globalProvider.updateUINNumber);
                              await DemographicsApi()
                                  .addDemographicField("consent", "true");
                            }
                            continueButtonTap(size, newProcess);
                          },
                        ),
                      ),
                    ],
                  )
                : Row(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      // globalProvider.newProcessTabIndex == size + 2
                      //     ? ElevatedButton(
                      //         onPressed: () async {
                      //           await connectivityProvider
                      //               .checkNetworkConnection();
                      //           bool isConnected =
                      //               connectivityProvider.isConnected;
                      //           if (!isConnected) {
                      //             _showInSnackBar(
                      //                 appLocalizations.network_error);
                      //             return;
                      //           }
                      //           globalProvider.syncPacket(globalProvider.regId);
                      //         },
                      //         child: Text(appLocalizations.sync_packet),
                      //       )
                      //     : const SizedBox.shrink(),
                      // SizedBox(
                      //   width: 10.w,
                      // ),
                      // globalProvider.newProcessTabIndex == size + 2
                      //     ? ElevatedButton(
                      //         onPressed: () async {
                      //           await connectivityProvider
                      //               .checkNetworkConnection();
                      //           bool isConnected =
                      //               connectivityProvider.isConnected;
                      //           if (!isConnected) {
                      //             _showInSnackBar(
                      //                 appLocalizations.network_error);
                      //             return;
                      //           }
                      //           globalProvider
                      //               .uploadPacket(globalProvider.regId);
                      //         },
                      //         child: Text(appLocalizations.upload_packet),
                      //       )
                      //     : const SizedBox.shrink(),
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
                        ),
                        onPressed: () {
                          continueButtonTap(size, newProcess);
                        },
                        child: Text(
                          context.read<GlobalProvider>().newProcessTabIndex <=
                                  size
                              ? appLocalizations.continue_text
                              : globalProvider.newProcessTabIndex == size + 1
                                  ? appLocalizations.authenticate
                                  : appLocalizations.go_to_home,
                          style: const TextStyle(color: appWhite),
                        ),
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
                  // isPortrait
                  //     ? const SizedBox()
                  //     : const Column(
                  //         children: [
                  //          // TabletHeader(),
                  //           TabletNavbar(),
                  //         ],
                  //       ),
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
                            newProcess.label![globalProvider.selectedLanguage]!,
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
                        fieldSelectionCompleted
                            ? Divider(
                                height: 12.h,
                                thickness: 1,
                                color: secondaryColors.elementAt(2),
                              )
                            : const SizedBox(),
                        fieldSelectionCompleted
                            ? Padding(
                                padding: isPortrait
                                    ? const EdgeInsets.all(0)
                                    : EdgeInsets.fromLTRB(60.w, 0, 60.w, 0),
                                child: Stack(
                                  alignment: FractionalOffset.centerRight,
                                  children: [
                                    Padding(
                                      padding: isPortrait
                                          ? EdgeInsets.fromLTRB(
                                              20.w, 10.h, 0, 0)
                                          : EdgeInsets.fromLTRB(0, 10.h, 0, 0),
                                      child: SizedBox(
                                        height: 36.h,
                                        child: ListView.builder(
                                            padding: const EdgeInsets.all(0),
                                            scrollDirection: Axis.horizontal,
                                            itemCount:
                                                newProcess.screens!.length + 3,
                                            itemBuilder: (BuildContext context,
                                                int index) {
                                              return GestureDetector(
                                                onTap: () {
                                                  if (globalProvider
                                                          .newProcessTabIndex ==
                                                      size + 2) {
                                                    return;
                                                  }

                                                  if (index <
                                                      globalProvider
                                                          .newProcessTabIndex) {
                                                    globalProvider
                                                            .newProcessTabIndex =
                                                        index;
                                                  }
                                                },
                                                child: Row(
                                                  children: [
                                                    Container(
                                                      padding:
                                                          EdgeInsets.fromLTRB(
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
                                                                  : Colors
                                                                      .transparent,
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
                                                                  Icons
                                                                      .check_circle,
                                                                  size: 17,
                                                                  color: secondaryColors
                                                                      .elementAt(
                                                                          11),
                                                                )
                                                              : (context
                                                                          .watch<
                                                                              GlobalProvider>()
                                                                          .newProcessTabIndex ==
                                                                      index)
                                                                  ? Icon(
                                                                      Icons
                                                                          .circle,
                                                                      color:
                                                                          pureWhite,
                                                                      size: 17,
                                                                    )
                                                                  : Icon(
                                                                      Icons
                                                                          .circle_outlined,
                                                                      size: 17,
                                                                      color: secondaryColors
                                                                          .elementAt(
                                                                              9),
                                                                    ),
                                                          SizedBox(
                                                            width: 6.w,
                                                          ),
                                                          Text(
                                                            index < size
                                                                ? newProcess
                                                                        .screens![
                                                                            index]!
                                                                        .label![
                                                                    context
                                                                        .read<
                                                                            GlobalProvider>()
                                                                        .selectedLanguage]!
                                                                : postRegistrationTabs[
                                                                    index -
                                                                        size],
                                                            style: Theme.of(
                                                                    context)
                                                                .textTheme
                                                                .titleSmall
                                                                ?.copyWith(
                                                                    color: (context.watch<GlobalProvider>().newProcessTabIndex ==
                                                                            index)
                                                                        ? pureWhite
                                                                        : secondaryColors
                                                                            .elementAt(
                                                                                9),
                                                                    fontWeight:
                                                                        semiBold,
                                                                    fontSize:
                                                                        14),
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
                                      padding:
                                          const EdgeInsets.fromLTRB(0, 0, 0, 0),
                                      color: solidPrimary,
                                      child: Icon(
                                        Icons.arrow_forward_ios_outlined,
                                        color: pureWhite,
                                        size: 17,
                                      ),
                                    ),
                                  ],
                                ),
                              )
                            : const SizedBox.shrink(),
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
                    child: !fieldSelectionCompleted
                        ? UpdateFieldSelector(process: newProcess)
                        : context.watch<GlobalProvider>().newProcessTabIndex <
                                size
                            ? UpdateProcessScreenContent(
                                context: context,
                                process: newProcess,
                                screen: newProcess.screens!.elementAt(context
                                    .watch<GlobalProvider>()
                                    .newProcessTabIndex)!)
                            : context
                                        .watch<GlobalProvider>()
                                        .newProcessTabIndex ==
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
                appLocalizations.authenticate_using_password,
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
                    appLocalizations.username,
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
                    appLocalizations.password,
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
          hintText: appLocalizations.enter_username,
          hintStyle: isPortrait && !isMobileSize
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
          hintText: appLocalizations.enter_password,
          hintStyle: isPortrait && !isMobileSize
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
