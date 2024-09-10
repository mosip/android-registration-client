/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'dart:async';
import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:provider/provider.dart';

import 'package:registration_client/main.dart';

import 'package:registration_client/pigeon/user_pigeon.dart';

import 'package:registration_client/provider/auth_provider.dart';
import 'package:registration_client/provider/sync_provider.dart';
import 'package:registration_client/ui/dashboard/dashboard_tablet.dart';
import 'package:registration_client/ui/widgets/sync_alert_dialog.dart';
import 'package:registration_client/utils/app_style.dart';
import 'package:registration_client/ui/machine_keys.dart';
import 'package:registration_client/provider/connectivity_provider.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/responsive.dart';
import 'package:registration_client/ui/widgets/password_component.dart';
import 'package:registration_client/ui/widgets/username_component.dart';
import 'package:colorful_progress_indicators/colorful_progress_indicators.dart';

import '../utils/life_cycle_event_handler.dart';

class LoginPage extends StatefulWidget {
  static const route = "/login-page";
  const LoginPage({super.key});

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> with WidgetsBindingObserver {
  bool isMobile = true;
  bool isLoggingIn = false;
  String username = '';
  String password = '';
  bool isMachineKeysDialogOpen = false;

  _toggleMachineKeysDialog() {
    setState(() {
      isMachineKeysDialogOpen = false;
    });
  }

  late AuthProvider authProvider;
  late SyncProvider syncProvider;
  late GlobalProvider globalProvider;
  late ConnectivityProvider connectivityProvider;
  late AppLocalizations appLocalizations = AppLocalizations.of(context)!;

  TextEditingController usernameController = TextEditingController();
  TextEditingController passwordController = TextEditingController();

  @override
  void initState() {
    authProvider = Provider.of<AuthProvider>(context, listen: false);
    syncProvider = Provider.of<SyncProvider>(context, listen: false);
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    connectivityProvider =
        Provider.of<ConnectivityProvider>(context, listen: false);
    _initializeAppData();
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

  _initializeAppData() async {
    await globalProvider.setMachineDetails();
    await globalProvider.initializeLanguageDataList(false);
    await globalProvider.initializeLocationHierarchyMap();
    await globalProvider.setGitHeadAttributes();
    await globalProvider.getAudit("REG-LOAD-001", "REG-MOD-101");
  }

  _saveAllHeaders() async {
    for (var header in lang) {
      await globalProvider.saveScreenHeaderToGlobalParam(
          "newRegistrationProcess_$header",
          appLocalizations.newRegistrationProcess(header));
      await globalProvider.saveScreenHeaderToGlobalParam(
          "consentScreenName_$header",
          appLocalizations.consentScreenName(header));
      await globalProvider.saveScreenHeaderToGlobalParam(
          "demographicsScreenName_$header",
          appLocalizations.demographicsScreenName(header));
      await globalProvider.saveScreenHeaderToGlobalParam(
          "documentsScreenName_$header",
          appLocalizations.documentsScreenName(header));
      await globalProvider.saveScreenHeaderToGlobalParam(
          "biometricsScreenName_$header",
          appLocalizations.biometricsScreenName(header));
    }
  }

  @override
  Widget build(BuildContext context) {
    isMobile = MediaQuery.of(context).orientation == Orientation.portrait;
    double h = ScreenUtil().screenHeight;
    double w = ScreenUtil().screenWidth;
    appLocalizations = AppLocalizations.of(context)!;

    return SafeArea(
      child: Scaffold(
        backgroundColor: appSolidPrimary,
        bottomNavigationBar: _getBottomBar(),
        body: Stack(
          children: [
            Positioned(
              bottom: 0,
              //left: 16.w,
              child: _getBuildingsImage(),
            ),
            SizedBox(
              height: h,
              width: w,
              child: SingleChildScrollView(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    SizedBox(
                      height: 20.h,
                    ),
                    // Container(
                    //   alignment: Alignment.centerRight,
                    //   padding: EdgeInsets.symmetric(
                    //       horizontal: isMobile ? 20.w : 80.w),
                    //   child: InkWell(
                    //     onTap: () {},
                    //     child: Container(
                    //       height: isMobileSize ? 46.h : 62.h,
                    //       width: 129.w,
                    //       decoration: BoxDecoration(
                    //         color: Colors.transparent,
                    //         border: Border.all(
                    //           color: appWhite,
                    //         ),
                    //         borderRadius: const BorderRadius.all(
                    //           Radius.circular(5),
                    //         ),
                    //       ),
                    //       child: Center(
                    //         child: Text(appLocalizations.help,
                    //             style: isMobile && !isMobileSize
                    //                 ? AppTextStyle.tabletPortraitHelpText
                    //                 : AppTextStyle.mobileHelpText),
                    //       ),
                    //     ),
                    //   ),
                    // ),
                    SizedBox(
                      height: isMobileSize ? 78.h : 86.h,
                    ),
                    Flexible(
                      child: Column(
                        children: [
                          Container(
                            padding: EdgeInsets.symmetric(
                              horizontal: isMobile
                                  ? isMobileSize
                                      ? 20.w
                                      : 92.w
                                  : 80.w,
                            ),
                            child: isMobile ? _mobileView() : _tabletView(),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),
            isMachineKeysDialogOpen
                ? Container(
                    color: Colors.transparent.withOpacity(0.5),
                    child: Center(
                      child: MachineKeys(
                        onCloseComponent: () {
                          _toggleMachineKeysDialog();
                        },
                      ),
                    ),
                  )
                : const SizedBox(),
          ],
        ),
      ),
    );
  }

  _getUserValidation() async {
    FocusManager.instance.primaryFocus?.unfocus();
    if (username.isEmpty) {
      _showInSnackBar(appLocalizations.username_required);
      return;
    } else if (username.length > 50) {
      _showInSnackBar(appLocalizations.username_exceed);
      return;
    }

    String langCode = globalProvider.selectedLanguage;
    await authProvider.validateUser(username, langCode);

    bool isValid = authProvider.isValidUser;
    if (!isValid) {
      _showInSnackBar(appLocalizations.username_incorrect);
      return;
    }

    final User user = authProvider.currentUser;
    globalProvider.setCenterId(user.centerId!);
    globalProvider.setName(user.name!);
    globalProvider.setCenterName(user.centerName!);
    _showInSnackBar(appLocalizations.user_validated);
  }

  void _showInSnackBar(String value) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(value),
      ),
    );
  }

  _getLoginAction() async {
    ScaffoldMessenger.of(context).hideCurrentSnackBar();
    FocusManager.instance.primaryFocus?.unfocus();
    if (password.isEmpty) {
      _showInSnackBar(appLocalizations.password_required);
      return;
    }
    if (password.length > 50) {
      _showInSnackBar(appLocalizations.password_exceed);
      return;
    }

    setState(() {
      authProvider.setIsSyncing(true);
    });
    await connectivityProvider.checkNetworkConnection();

    await globalProvider.getVersionNoApp();
    String version = globalProvider.versionNoApp;
    await globalProvider.saveVersionToGlobalParam(
        "mosip.registration.server_version", version);
    if (version.startsWith("1.1.5")) {
      await _saveAllHeaders();
    }
    await authProvider.authenticateUser(
        username, password, connectivityProvider.isConnected);

    if (!authProvider.isLoggedIn) {
      authProvider.setIsSyncing(false);
      _showErrorInSnackbar();
      return;
    }

    await syncProvider.getLastSyncTime();
    debugPrint(syncProvider.lastSuccessfulSyncTime);

    await connectivityProvider.checkNetworkConnection();

    if (!connectivityProvider.isConnected) {
      if (!authProvider.isCenterActive) {
        authProvider.setLoginError("REG_CENTER_INACTIVE");
        authProvider.setIsLoggedIn(false);
        authProvider.setIsSyncing(false);
        _showErrorInSnackbar();
        return;
      }

      if (!authProvider.isMachineActive) {
        authProvider.setLoginError("REG_MACHINE_INACTIVE");
        authProvider.setIsLoggedIn(false);
        authProvider.setIsSyncing(false);
        _showErrorInSnackbar();
        return;
      }
    }

    if (!authProvider.isCenterActive || !authProvider.isMachineActive) {
      _showAlertDialog();
      authProvider.setIsSyncing(false);
      return;
    }

    log("machine active: ${authProvider.isMachineActive}");

    if (authProvider.isLoggedIn &&
        (syncProvider.lastSuccessfulSyncTime == "LastSyncTimeIsNull")) {
      log("sync time: ${syncProvider.lastSuccessfulSyncTime}");
      syncProvider.setIsGlobalSyncInProgress(true);
      await _autoSyncHandler();
    } else {
      authProvider.setIsSyncing(false);
      _navigateToHomePage();
    }
    setState(() {
      isLoggingIn = false;
    });
  }

  _showErrorInSnackbar() {
    String errorMsg = authProvider.loginError;
    if (errorMsg == "") {
      return;
    }
    String snackbarText = "";
    snackbarText = appLocalizations.errors(errorMsg);
    if (snackbarText == "Some error occurred!") {
      snackbarText = errorMsg;
    }
    _showInSnackBar(snackbarText);
  }

  _navigateToHomePage() {
    if (authProvider.isLoggedIn == true) {
      Navigator.popUntil(context, ModalRoute.withName('/login-page'));

      if (authProvider.isOnboarded || authProvider.isDefault) {
        globalProvider.setCurrentIndex(1);
      } else {
        globalProvider.setCurrentIndex(0);
      }

      Navigator.of(context).push(
        MaterialPageRoute(
          builder: (context) => Responsive(
            mobile: DashBoardTabletView(),
            desktop: DashBoardTabletView(),
            tablet: DashBoardTabletView(),
          ),
        ),
      );
    }
  }

  _getBottomBar() {
    return Container(
      height: isMobile && !isMobileSize ? 94.h : 62.h,
      padding: EdgeInsets.symmetric(
        vertical: 15.h,
      ),
      color: appWhite,
      child: Center(
        child: InkWell(
          onLongPress: () {
            globalProvider.getAudit("REG-AUTH-002", "REG-MOD-101");
            setState(() {
              isMachineKeysDialogOpen = true;
            });
          },
          child: Image.asset(
            appIcon,
            fit: BoxFit.fill,
          ),
        ),
      ),
    );
  }

  Widget _welcomeTextComponent() {
    return Column(
      crossAxisAlignment:
          isMobile ? CrossAxisAlignment.center : CrossAxisAlignment.start,
      children: [
        Text(
          appLocalizations.welcome,
          style: isMobile && !isMobileSize
              ? AppTextStyle.tabletPortraitWelcomeText
              : AppTextStyle.tabletWelcomeText,
          textAlign: isMobile ? TextAlign.center : TextAlign.start,
        ),
        Text(
          appLocalizations.community_reg_text,
          style: isMobile && !isMobileSize
              ? AppTextStyle.tabletPortraitCommunityRegClientText
              : AppTextStyle.tabletCommunityRegClientText,
          textAlign: isMobile ? TextAlign.center : TextAlign.start,
        )
      ],
    );
  }

  Widget _infoTextComponent() {
    return Text(
      appLocalizations.info_text,
      style: isMobile && !isMobileSize
          ? AppTextStyle.tabletPortraitInfoText
          : AppTextStyle.mobileInfoText,
      textAlign: isMobile ? TextAlign.center : TextAlign.start,
    );
  }

  Widget _appCombinedTextComponent() {
    return Column(
      crossAxisAlignment:
          isMobile ? CrossAxisAlignment.center : CrossAxisAlignment.start,
      children: [
        _welcomeTextComponent(),
        SizedBox(
          height: isMobile && !isMobileSize ? 18.h : 12.h,
        ),
        _infoTextComponent(),
      ],
    );
  }

  Widget _loginComponent() {
    return Container(
      width: isMobile
          ? isMobileSize
              ? 358.w
              : 616.w
          : 424.w,
      padding: EdgeInsets.symmetric(
        horizontal: isMobile && !isMobileSize ? 30.w : 20.w,
        vertical: isMobile && !isMobileSize ? 30.h : 20.h,
      ),
      decoration: BoxDecoration(
        color: appWhite,
        border: Border.all(
          width: 1.w,
        ),
        borderRadius: const BorderRadius.all(
          Radius.circular(12),
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          SizedBox(
            height: isMobile && !isMobileSize ? 16.h : 34.h,
          ),
          Text(
            appLocalizations.login_text,
            style: isMobile && !isMobileSize
                ? AppTextStyle.tabletPortraitHeaderText
                : AppTextStyle.mobileHeaderText,
          ),
          SizedBox(
            height: context.watch<AuthProvider>().isValidUser ? 42.h : 38.h,
          ),
          !context.watch<AuthProvider>().isValidUser
              ? UsernameComponent(
                  onTap: () {
                    _getUserValidation();
                  },
                  isDisabled:
                      username.trim().isEmpty || username.trim().length > 50,
                  languages: context.watch<GlobalProvider>().languages,
                  isMobile: isMobile,
                  mp: context.watch<GlobalProvider>().codeToLanguageMapper,
                  onChanged: (v) {
                    setState(() {
                      username = v;
                    });
                  },
                )
              : const SizedBox(),
          context.watch<AuthProvider>().isValidUser
              ? PasswordComponent(
                  isDisabled: password.isEmpty || password.length > 50,
                  onTapLogin: () async {
                    await _getLoginAction();
                    await globalProvider.getThresholdValues();
                  },
                  isMobile: isMobile,
                  onTapBack: () {
                    password = "";
                    FocusManager.instance.primaryFocus?.unfocus();
                    authProvider.setIsValidUser(false);
                    setState(() {
                      username = '';
                      authProvider.setIsSyncing(false);
                    });
                  },
                  onChanged: (v) {
                    setState(() {
                      password = v;
                    });
                  },
                  isLoggingIn: authProvider.isSyncing,
                )
              : const SizedBox(),
        ],
      ),
    );
  }

  Widget _mobileView() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        //_appCombinedTextComponent(),
        SizedBox(
          height: isMobileSize ? 100.h : 70.h,
        ),
        _loginComponent(),
      ],
    );
  }

  Widget _tabletView() {
    return SingleChildScrollView(
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          Column(
            children: [
              SizedBox(
                width: 399.w,
                //child: _appCombinedTextComponent(),
              ),
              SizedBox(
                height: 100.h,
              ),
            ],
          ),
          SizedBox(
            width: 41.w,
          ),
          _loginComponent(),
        ],
      ),
    );
  }

  Widget _getBuildingsImage() {
    return SizedBox(
      height: isMobile ? MediaQuery.of(context).size.height : MediaQuery.of(context).size.height,
      width: isMobile ? (478.28).w : MediaQuery.of(context).size.width,
      child: Image.asset(
        isMobile ? buildingsXX : buildingsXX,
        fit: BoxFit.fill,
      ),
    );
  }

  _autoSyncHandler() async {
    if (syncProvider.isGlobalSyncInProgress) {
      authProvider.setIsSyncing(false);
      showLoadingDialog(context);
      await syncProvider.autoSync(context).then((value) {
        // syncProvider.setIsGlobalSyncInProgress(false);
      });
      showSyncResultDialog();
    }

    await globalProvider.initializeLanguageDataList(false);
    Timer(const Duration(seconds: 5), () {
      if (syncProvider.isAllSyncSuccessful()) {
        RestartWidget.restartApp(context);
      } else {
        SystemChannels.platform.invokeMethod('SystemNavigator.pop');
      }
    });
  }

  showSyncResultDialog() {
    showGeneralDialog(
      context: context,
      barrierDismissible: false,
      pageBuilder: (_, __, ___) {
        return WillPopScope(
          onWillPop: () async => false,
          child: Align(
            alignment: Alignment.center,
            child: Container(
              height: isMobile && isMobileSize ? 210.h : 280.h,
              width: isMobile && isMobileSize ? 210.w : 280.w,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(15), color: pureWhite),
              child: Padding(
                padding: const EdgeInsets.all(25.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    SizedBox(
                      height: isMobile && isMobileSize ? 40.h : 100.h,
                      width: isMobile && isMobileSize ? 40.w : 100.w,
                      child: syncProvider.isAllSyncSuccessful()
                          ? SvgPicture.asset(
                              "assets/svg/Success Message Icon.svg")
                          : SvgPicture.asset(
                              "assets/svg/Failed Message Icon.svg"),
                    ),
                    SizedBox(
                      height: isMobile ? 2.h : 5.h,
                    ),
                    DefaultTextStyle(
                      style: TextStyle(
                          fontSize: isMobile && isMobileSize ? 10 : 18,
                          color: Colors.black87,
                          fontWeight: FontWeight.bold),
                      child: Text(
                        syncProvider.isAllSyncSuccessful()
                            ? appLocalizations.sync_completed_succesfully
                            : appLocalizations.sync_failed,
                        textAlign: TextAlign.center,
                      ),
                    ),
                    const SizedBox(),
                    DefaultTextStyle(
                      style: TextStyle(
                          fontSize: isMobile && isMobileSize ? 8 : 12,
                          color: const Color.fromARGB(221, 80, 79, 79),
                          fontWeight: FontWeight.w900),
                      child: Text(
                        syncProvider.isAllSyncSuccessful()
                            ? appLocalizations.sync_completed_message
                            : appLocalizations.sync_failed_message,
                        softWrap: true,
                        textAlign: TextAlign.center,
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
        );
      },
    );
  }

  showLoadingDialog(BuildContext context) {
    showGeneralDialog(
      context: context,
      barrierDismissible: false,
      pageBuilder: (_, __, ___) {
        return WillPopScope(
          onWillPop: () async => false,
          child: Align(
            alignment: Alignment.center,
            child: Container(
              height: isMobile && isMobileSize ? 210.h : 280.h,
              width: isMobile && isMobileSize ? 210.w : 280.w,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(15), color: pureWhite),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Stack(
                    alignment: Alignment.center,
                    children: [
                      Image.asset(
                        appIconLogoOnly,
                        fit: BoxFit.scaleDown,
                        height: isMobile && isMobileSize ? 35.h : 90.h,
                        width: isMobile && isMobileSize ? 35.w : 90.w,
                      ),
                      Transform.scale(
                        scale: isMobile ? 1.4 : 2.8,
                        child: Center(
                          child: ColorfulCircularProgressIndicator(
                            colors: appColors,
                            strokeWidth: 2.2,
                            duration: const Duration(milliseconds: 500),
                            initialColor: appColors[0],
                          ),
                        ),
                      ),
                    ],
                  ),
                  SizedBox(
                    height: isMobile ? 10.h : 20.h,
                  ),
                  DefaultTextStyle(
                      style: TextStyle(
                          fontSize: isMobile && isMobileSize ? 12 : 15,
                          color: const Color.fromARGB(221, 80, 79, 79),
                          fontWeight: FontWeight.w900),
                      child: Consumer<SyncProvider>(
                        builder: (context, syncP, child) {
                          return Text(
                              "Sync ${syncP.currentSyncProgress.toString()} of 7 ");
                        },
                      )),
                ],
              ),
            ),
          ),
        );
      },
    );
  }

  _showAlertDialog() {
    return showDialog(
      context: context,
      builder: (BuildContext context) => SyncAlertDialog(
        title: appLocalizations.sync_alert_title,
        content: appLocalizations.sync_alert_content,
        onPressed: () async {
          Navigator.of(context).pop();
          syncProvider.setIsGlobalSyncInProgress(true);
          await _autoSyncHandler();
        },
      ),
    );
  }
}
