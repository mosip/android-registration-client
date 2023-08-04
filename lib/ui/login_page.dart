/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/pigeon/biometrics_pigeon.dart';
import 'package:registration_client/main.dart';
import 'package:registration_client/pigeon/machine_pigeon.dart';
import 'package:registration_client/pigeon/master_data_sync_pigeon.dart';
import 'package:registration_client/pigeon/user_pigeon.dart';
import 'package:registration_client/platform_android/auth_impl.dart';
import 'package:registration_client/platform_android/machine_key_impl.dart';
import 'package:registration_client/platform_android/sync_response_impl.dart';
import 'package:registration_client/platform_spi/machine_key.dart';
import 'package:registration_client/provider/auth_provider.dart';
import 'package:registration_client/provider/sync_provider.dart';
import 'package:registration_client/utils/app_style.dart';
import 'package:registration_client/ui/machine_keys.dart';
import 'package:registration_client/provider/connectivity_provider.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/ui/dashboard/dashboard_mobile.dart';
import 'package:registration_client/ui/dashboard/dashboard_tablet.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/responsive.dart';
import 'package:registration_client/ui/widgets/password_component.dart';
import 'package:registration_client/ui/widgets/username_component.dart';
import 'package:colorful_progress_indicators/colorful_progress_indicators.dart';

class LoginPage extends StatefulWidget {
  static const route = "/login-page";
  const LoginPage({super.key});

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
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

  final List<String> _languages = ['eng', 'ara', 'fra'];

  Map<String, String> mp = {};
  late AuthProvider authProvider;
  late SyncProvider syncProvider;

  TextEditingController usernameController = TextEditingController();
  TextEditingController passwordController = TextEditingController();

  @override
  void initState() {
    super.initState();
    mp['eng'] = "English";
    mp['ara'] = "العربية";
    mp['fra'] = "Français";
  }

  @override
  Widget build(BuildContext context) {
    isMobile = MediaQuery.of(context).orientation == Orientation.portrait;
    double h = ScreenUtil().screenHeight;
    double w = ScreenUtil().screenWidth;
    authProvider = Provider.of<AuthProvider>(context, listen: false);
    syncProvider = Provider.of<SyncProvider>(context, listen: false);

    return authProvider.isLoggedIn && !syncProvider.isGlobalSyncInProgress
        ? Responsive(
            mobile: DashBoardMobileView(),
            desktop: DashBoardTabletView(),
            tablet: DashBoardTabletView(),
          )
        : SafeArea(
            child: Scaffold(
              backgroundColor: AppStyle.appSolidPrimary,
              body: Stack(
                children: [
                  Positioned(
                    bottom: 0,
                    left: 16.w,
                    child: _getBuildingsImage(),
                  ),
                  Container(
                    height: h,
                    width: w,
                    child: SingleChildScrollView(
                      child: Column(
                        crossAxisAlignment: isMobile
                            ? CrossAxisAlignment.center
                            : CrossAxisAlignment.start,
                        children: [
                          _appBarComponent(),
                          SizedBox(
                            height: isMobile ? 50.h : 132.h,
                          ),
                          Container(
                            padding: EdgeInsets.symmetric(
                              horizontal: isMobile ? 16.w : 80.w,
                            ),
                            child: isMobile ? _mobileView() : _tabletView(),
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
      _showInSnackBar(AppLocalizations.of(context)!.username_required);
      return;
    } else if (username.length > 50) {
      _showInSnackBar(AppLocalizations.of(context)!.username_exceed);
      return;
    }

    await context.read<AuthProvider>().validateUser(username);
    bool isValid = context.read<AuthProvider>().isValidUser;
    if (!isValid) {
      _showInSnackBar(AppLocalizations.of(context)!.username_incorrect);
      return;
    }

    final user = context.read<AuthProvider>().currentUser;
    context.read<GlobalProvider>().setCenterId(user.centerId!);
    context.read<GlobalProvider>().setName(user.name!);
    context.read<GlobalProvider>().setCenterName(user.centerName!);
    _showInSnackBar(AppLocalizations.of(context)!.user_validated);
  }

  void _onNextButtonPressed() async {
    await _getUserValidation();
  }

  void _showInSnackBar(String value) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(value),
      ),
    );
  }

  _onLoginButtonPressed() async {
    await _getLoginAction();
  }

  _getLoginAction() async {
    FocusManager.instance.primaryFocus?.unfocus();
    if (password.isEmpty) {
      _showInSnackBar(AppLocalizations.of(context)!.password_required);
      return;
    }
    if (password.length > 50) {
      _showInSnackBar(AppLocalizations.of(context)!.password_exceed);
      return;
    }

    setState(() {
      authProvider.setIsSyncing(true);
    });
    bool isConnected = context.read<ConnectivityProvider>().isConnected;
    await context
        .read<AuthProvider>()
        .authenticateUser(username, password, isConnected);

    bool isTrue = context.read<AuthProvider>().isLoggedIn;
    if (!isTrue) {
      authProvider.setIsSyncing(false);
      _showErrorInSnackbar();
    } else {
      authProvider.setIsSyncing(false);
      syncProvider.setIsGlobalSyncInProgress(true);
      if (syncProvider.isGlobalSyncInProgress) {
        showLoadingDialog(context);
        await syncProvider.autoSync(context).then((value) {
          // syncProvider.setIsGlobalSyncInProgress(false);
        });
      }
      showSyncResultDialog(context);
      Timer(const Duration(seconds: 5), () {
        if (syncProvider.isAllSyncSuccessful()) {
          // RestartWidget.restartApp(context);
          _navigateToHomePage();
        } else {
          SystemChannels.platform.invokeMethod('SystemNavigator.pop');
        }
      });
    }

    setState(() {
      isLoggingIn = false;
    });
  }

  _showErrorInSnackbar() {
    String errorMsg = context.read<AuthProvider>().loginError;
    String snackbarText = "";

    switch (errorMsg) {
      case "REG_TRY_AGAIN":
        snackbarText = AppLocalizations.of(context)!.login_failed;
        break;

      case "REG_INVALID_REQUEST":
        snackbarText = AppLocalizations.of(context)!.password_incorrect;
        break;

      case "REG_MACHINE_NOT_FOUND":
        snackbarText = AppLocalizations.of(context)!.machine_not_found;
        break;

      case "REG_NETWORK_ERROR":
        snackbarText = AppLocalizations.of(context)!.network_error;
        break;

      case "REG_CRED_EXPIRED":
        snackbarText = AppLocalizations.of(context)!.cred_expired;
        break;

      case "":
        return;

      default:
        snackbarText = errorMsg;
        break;
    }

    _showInSnackBar(snackbarText);
  }

  _navigateToHomePage() {
    if (context.read<AuthProvider>().isLoggedIn == true) {
      Navigator.popUntil(context, ModalRoute.withName('/login-page'));
      if (context.read<AuthProvider>().isOnboarded || 
          context.read<AuthProvider>().isDefault ||
          (context.read<AuthProvider>().isSupervisor &&
              context.read<AuthProvider>().isOfficer)) {
        context.read<GlobalProvider>().setCurrentIndex(1);
      }

      Navigator.of(context).push(
        MaterialPageRoute(
          builder: (context) => Responsive(
            mobile: DashBoardMobileView(),
            desktop: DashBoardTabletView(),
            tablet: DashBoardTabletView(),
          ),
        ),
      );
    }
  }

  Widget _appBarComponent() {
    return Container(
      height: 90.h,
      color: AppStyle.appWhite,
      padding: EdgeInsets.symmetric(
        vertical: 22.h,
        horizontal: isMobile ? 16.w : 80.w,
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          InkWell(
            onLongPress: () {
              setState(() {
                isMachineKeysDialogOpen = true;
              });
            },
            child: Container(
              height: isMobile ? 46.h : 54.h,
              // width: isMobile ? 115.39.w : 135.46.w,
              child: Image.asset(
                appIcon,
                fit: BoxFit.fill,
              ),
            ),
          ),
          InkWell(
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
                  AppLocalizations.of(context)!.help,
                  style: AppStyle.mobileHelpText,
                ),
              ),
            ),
            onTap: () {},
          ),
        ],
      ),
    );
  }

  Widget _welcomeTextComponent() {
    return Container(
      padding: EdgeInsets.symmetric(
        horizontal: isMobile ? 41.w : 0,
      ),
      child: Column(
        crossAxisAlignment:
            isMobile ? CrossAxisAlignment.center : CrossAxisAlignment.start,
        children: [
          Text(
            AppLocalizations.of(context)!.welcome,
            style: AppStyle.mobileWelcomeText,
            textAlign: isMobile ? TextAlign.center : TextAlign.start,
          ),
          Text(
            AppLocalizations.of(context)!.community_reg_text,
            style: AppStyle.mobileCommunityRegClientText,
            textAlign: isMobile ? TextAlign.center : TextAlign.start,
          )
        ],
      ),
    );
  }

  Widget _infoTextComponent() {
    return Container(
      padding: EdgeInsets.symmetric(
        horizontal: isMobile ? 52.w : 0,
      ),
      child: Text(
        AppLocalizations.of(context)!.info_text,
        style: AppStyle.mobileInfoText,
        textAlign: isMobile ? TextAlign.center : TextAlign.start,
      ),
    );
  }

  Widget _appCombinedTextComponent() {
    return Column(
      crossAxisAlignment:
          isMobile ? CrossAxisAlignment.center : CrossAxisAlignment.start,
      children: [
        _welcomeTextComponent(),
        SizedBox(
          height: isMobile ? 12.h : 16.h,
        ),
        _infoTextComponent(),
      ],
    );
  }

  Widget _loginComponent() {
    return Container(
      width: isMobile ? 358.w : 424.w,
      padding: EdgeInsets.symmetric(
        horizontal: 20.w,
        vertical: 20.h,
      ),
      decoration: BoxDecoration(
        color: AppStyle.appWhite,
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
            height: 34.h,
          ),
          Container(
            // height: 34.h,
            child: Text(
              AppLocalizations.of(context)!.login_text,
              style: AppStyle.mobileHeaderText,
            ),
          ),
          SizedBox(
            height: context.watch<AuthProvider>().isValidUser ? 41.h : 38.h,
          ),
          !context.watch<AuthProvider>().isValidUser
              ? UsernameComponent(
                  onTap: _onNextButtonPressed,
                  isDisabled: username.isEmpty || username.length > 50,
                  languages: _languages,
                  isMobile: isMobile,
                  mp: mp,
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
                  onTapLogin: _onLoginButtonPressed,
                  onTapBack: () {
                    FocusManager.instance.primaryFocus?.unfocus();
                    context.read<AuthProvider>().setIsValidUser(false);
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
        _appCombinedTextComponent(),
        SizedBox(
          height: 40.h,
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
          Container(
            width: 399.w,
            child: _appCombinedTextComponent(),
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
    return Container(
      height: isMobile ? (162.48).h : (293.48).h,
      width: isMobile ? (222.28).w : (400.28).w,
      child: Image.asset(
        isMobile ? buildingsX : buildingsXX,
        fit: BoxFit.fill,
      ),
    );
  }

  showSyncResultDialog(BuildContext context) {
    showGeneralDialog(
      context: context,
      barrierDismissible: false,
      pageBuilder: (_, __, ___) {
        return WillPopScope(
          onWillPop: () async => false,
          child: Align(
            alignment: Alignment.center,
            child: Container(
              height: isMobile ? 125.h : 220.h,
              width: isMobile ? 125.w : 220.w,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(15), color: pure_white),
              child: Padding(
                padding: const EdgeInsets.all(25.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    SizedBox(
                      height: isMobile ? 40.h : 100.h,
                      width: isMobile ? 40.w : 100.w,
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
                          fontSize: isMobile ? 12 : 18,
                          color: Colors.black87,
                          fontWeight: FontWeight.bold),
                      child: Text(
                        syncProvider.isAllSyncSuccessful()
                            ? AppLocalizations.of(context)!
                                .sync_completed_succesfully
                            : AppLocalizations.of(context)!.sync_failed,
                        textAlign: TextAlign.center,
                      ),
                    ),
                    const SizedBox(),
                    DefaultTextStyle(
                      style: TextStyle(
                          fontSize: isMobile ? 7 : 12,
                          color: const Color.fromARGB(221, 80, 79, 79),
                          fontWeight: FontWeight.w900),
                      child: Text(
                        syncProvider.isAllSyncSuccessful()
                            ? AppLocalizations.of(context)!
                                .sync_completed_message
                            : AppLocalizations.of(context)!.sync_failed_message,
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
              height: isMobile ? 125.h : 220.h,
              width: isMobile ? 125.w : 220.w,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(15), color: pure_white),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Stack(
                    alignment: Alignment.center,
                    children: [
                      Image.asset(
                        appIconLogoOnly,
                        fit: BoxFit.scaleDown,
                        height: isMobile ? 35.h : 90.h,
                        width: isMobile ? 35.w : 90.w,
                      ),
                      Transform.scale(
                        scale: isMobile? 1.4 : 2.8,
                        child: Center(
                          child: ColorfulCircularProgressIndicator(
                            colors: app_colors,
                            strokeWidth: 2.2,
                            duration: const Duration(milliseconds: 500),
                            initialColor: app_colors[0],
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
                          fontSize: isMobile ? 9 : 15,
                          color: const Color.fromARGB(221, 80, 79, 79),
                          fontWeight: FontWeight.w900),
                      child: Consumer<SyncProvider>(
                        builder: (context, syncP, child) {
                          return Text(
                              "Sync ${syncP.currentSyncProgress.toString()} of 5 ");
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
}
