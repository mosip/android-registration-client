/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'dart:async';
import 'dart:convert';
import 'dart:developer';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/login_response.dart';
import 'package:registration_client/pigeon/machine_pigeon.dart';
import 'package:registration_client/pigeon/user_pigeon.dart';
import 'package:registration_client/platform_android/auth_impl.dart';
import 'package:registration_client/platform_android/machine_key_impl.dart';
import 'package:registration_client/platform_spi/machine_key.dart';
import 'package:registration_client/provider/auth_provider.dart';
import 'package:registration_client/utils/app_style.dart';

import 'package:registration_client/ui/machine_keys.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/provider/connectivity_provider.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/ui/dashboard/dashboard_mobile.dart';
import 'package:registration_client/ui/dashboard/dashboard_tablet.dart';

import 'package:registration_client/ui/onboard/onboard_landing_page.dart';
import 'package:registration_client/ui/onboard/home_page.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/responsive.dart';
import 'package:registration_client/ui/widgets/password_component.dart';
import 'package:registration_client/ui/widgets/username_component.dart';

class LoginPage extends StatefulWidget {
  static const route = "/login-page";
  const LoginPage({super.key});

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  bool isMobile = true;
  static const platform =
      MethodChannel("com.flutter.dev/io.mosip.get-package-instance");
  // bool isLoggedIn = false;
  bool isLoggingIn = false;
  String loginResponse = '';
  String errorCode = '';
  String username = '';
  String password = '';
  bool isUserValidated = false;
  // String isOnboardedValue = "";
  List<String> _languages = ['eng', 'ara', 'fre'];
  Map<String, String> mp = {};
  late AuthProvider authProvider;

  TextEditingController usernameController = TextEditingController();
  TextEditingController passwordController = TextEditingController();
  late LoginResponse loginResp;

  @override
  void initState() {
    super.initState();
    mp['eng'] = "English";
    mp['ara'] = "العربية";
    mp['fre'] = "Français";
  }

  @override
  Widget build(BuildContext context) {
    isMobile = MediaQuery.of(context).orientation == Orientation.portrait;
    double h = ScreenUtil().screenHeight;
    double w = ScreenUtil().screenWidth;
    authProvider = Provider.of<AuthProvider>(context, listen: false);
    return authProvider.isLoggedIn
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
                ],
              ),
            ),
          );
  }

  Future<void> _login(String username, String password) async {
    final connectivityProvider =
        Provider.of<ConnectivityProvider>(context, listen: false);
    String response;
    List<dynamic> temp = List.empty(growable: true);
    Map<String, dynamic> mp;
    try {
      response = await platform.invokeMethod("login", {
        'username': username,
        'password': password,
        'isConnected': connectivityProvider.isConnected,
      });
      mp = jsonDecode(response);
      loginResp = LoginResponse.fromJson(mp);

      temp = loginResp.roles;
    } on PlatformException {
      mp = {};
    }

    authProvider.setIsLoggedIn(loginResp.isLoggedIn);
    authProvider.setIsSupervisor(temp.contains("REGISTRATION_SUPERVISOR"));
    authProvider.setIsOfficer(temp.contains("REGISTRATION_OPERATOR"));
    setState(() {
      errorCode = loginResp.error_code;
      if (authProvider.isLoggedIn) {
        loginResponse = loginResp.login_response;
      } else if (errorCode == '500') {
        loginResponse = AppLocalizations.of(context)!.login_failed;
      } else if (errorCode == '501') {
        loginResponse = AppLocalizations.of(context)!.network_error;
      } else if (errorCode == '401') {
        loginResponse = AppLocalizations.of(context)!.password_incorrect;
      } else if (errorCode == 'MACHINE_NOT_FOUND') {
        loginResponse = AppLocalizations.of(context)!.machine_not_found;
      } else if (errorCode == 'OFFLINE') {
        loginResponse = AppLocalizations.of(context)!.cred_expired;
      } else {
        loginResponse = loginResp.login_response;
      }
    });
    if (authProvider.isLoggedIn == true) {
      Navigator.popUntil(context, ModalRoute.withName('/login-page'));
      if (authProvider.isOnboarded ||
          (authProvider.isSupervisor && authProvider.isOfficer)) {
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
    if(!isValid) {
      _showInSnackBar(AppLocalizations.of(context)!.username_incorrect);
      return;
    }

    final user = context.read<AuthProvider>().currentUser;
    context.read<GlobalProvider>().setCenterId(user.centerId!);
    context.read<GlobalProvider>().setName(user.name!);
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

  void _onTapLogin() {
    FocusManager.instance.primaryFocus?.unfocus();
    if (password.isEmpty) {
      _showInSnackBar(AppLocalizations.of(context)!.password_required);
      return;
    } else if (password.length > 50) {
      _showInSnackBar(AppLocalizations.of(context)!.password_exceed);
      return;
    }
    setState(() {
      isLoggingIn = true;
    });
    _login(username, password).then((value) {
      if (loginResponse.isNotEmpty && !authProvider.isLoggedIn) {
        _showInSnackBar(loginResponse);
      }
      isLoggingIn = false;
    });
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
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => MachineKeys(),
                ),
              );
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

  _getMachineName() {
    String machineName = context.read<GlobalProvider>().machineName;
    return machineName;
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
            height: isUserValidated ? 41.h : 38.h,
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
                  onTapLogin: _onTapLogin,
                  onTapBack: () {
                    FocusManager.instance.primaryFocus?.unfocus();
                    context.read<AuthProvider>().setIsValidUser(false);
                    setState(() {
                      username = '';
                      isUserValidated = false;
                      isLoggingIn = false;
                    });
                  },
                  onChanged: (v) {
                    setState(() {
                      password = v;
                    });
                  },
                  isLoggingIn: isLoggingIn,
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
}
