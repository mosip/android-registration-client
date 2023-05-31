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

import 'package:registration_client/const/utils.dart';

import 'package:registration_client/credentials_page.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/data/models/login_response.dart';
import 'package:registration_client/provider/connectivity_provider.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/registration_client.dart';
import 'package:registration_client/ui/dashboard/dashboard_mobile/dashboard_mobile.dart';
import 'package:registration_client/ui/dashboard/dashboard_tablet/dashboard_tablet_view.dart';
import 'package:registration_client/ui/dashboard/dashboard_view_model.dart';
import 'package:registration_client/ui/onboarding/onboarding_page_1_view.dart';
import 'package:registration_client/ui/onboarding/onboarding_page_2_view.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/responsive.dart';
import 'package:registration_client/widgets/password_component.dart';
import 'package:registration_client/widgets/username_component.dart';

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
  bool isLoggedIn = false;
  bool isLoggingIn = false;
  String loginResponse = '';
  String errorCode = '';
  String username = '';
  String password = '';
  bool isUserValidated = false;
  String isOnboardedValue = "";
  List<String> _languages = ['eng', 'ara', 'fre'];
  Map<String, String> mp = {};

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
    return isLoggedIn
        ?
        //   Responsive(
        //   mobile: DashBoardMobileView(),
        //   desktop: DashBoardTabletView(),
        //   tablet: DashBoardTabletView(),
        // )
        RegistrationClient(
            // onLogout: () {
            //   setState(() {
            //     username = '';
            //     isUserValidated = false;
            //     isLoggedIn = false;
            //   });
            // },
            )
        : SafeArea(
            child: Scaffold(
              backgroundColor: Utils.appSolidPrimary,
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
    setState(() {
      isLoggedIn = loginResp.isLoggedIn;
      errorCode = loginResp.error_code;
      if (isLoggedIn) {
        loginResponse = loginResp.login_response;
      } else if (errorCode == '500') {
        loginResponse = AppLocalizations.of(context)!.login_failed;
      } else if (errorCode == '501') {
        loginResponse = AppLocalizations.of(context)!.network_error;
      } else if (errorCode == '401') {
        loginResponse = AppLocalizations.of(context)!.password_incorrect;
      } else if (errorCode == 'OFFLINE') {
        loginResponse = AppLocalizations.of(context)!.cred_expired;
      } else {
        loginResponse = AppLocalizations.of(context)!.machine_not_found;
      }
    });
    if (isLoggedIn == true) {
      Navigator.popUntil(context, ModalRoute.withName('/login-page'));
      if (isOnboardedValue == "true" && temp.contains("default-roles-mosip")) {
        context.read<DashboardViewModel>().setCurrentIndex(1);
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

  Future<void> _validateUsername() async {
    String response;
    Map<String, dynamic> mp;

    try {
      response = await platform
          .invokeMethod("validateUsername", {'username': username});
      mp = jsonDecode(response);
      if (mp["user_details"] != "") {
        isOnboardedValue = mp["user_details"]
            .toString()
            .split("isOnboarded=")
            .last
            .split(",")
            .first;
      } else {
        isOnboardedValue = "";
      }
    } on PlatformException {
      mp = {};
    }

    setState(() {
      setState(() {
        isUserValidated = mp['isUserPresent'];
        if (isUserValidated) {
          loginResponse = AppLocalizations.of(context)!.user_validated;
        } else {
          loginResponse = AppLocalizations.of(context)!.username_incorrect;
        }
      });
    });
  }

  void _showInSnackBar(String value) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(value),
      ),
    );
  }

  void _onTapNext() {
    FocusManager.instance.primaryFocus?.unfocus();
    if (username.isEmpty) {
      _showInSnackBar(AppLocalizations.of(context)!.username_required);
    } else if (username.length > 50) {
      _showInSnackBar(AppLocalizations.of(context)!.username_exceed);
    } else if (!isUserValidated) {
      _validateUsername().then((value) {
        _showInSnackBar(loginResponse);
      });
    }
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
      if (loginResponse.isNotEmpty && !isLoggedIn) {
        _showInSnackBar(loginResponse);
      }
      isLoggingIn = false;
    });
  }

  Widget _appBarComponent() {
    return Container(
      height: 90.h,
      color: Utils.appWhite,
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
                  builder: (context) => const CredentialsPage(),
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
                  color: Utils.appHelpText,
                ),
                borderRadius: const BorderRadius.all(
                  Radius.circular(5),
                ),
              ),
              child: Center(
                child: Text(
                  AppLocalizations.of(context)!.help,
                  style: Utils.mobileHelpText,
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
            style: Utils.mobileWelcomeText,
            textAlign: isMobile ? TextAlign.center : TextAlign.start,
          ),
          Text(
            AppLocalizations.of(context)!.community_reg_text,
            style: Utils.mobileCommunityRegClientText,
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
        style: Utils.mobileInfoText,
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
        color: Utils.appWhite,
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
              style: Utils.mobileHeaderText,
            ),
          ),
          SizedBox(
            height: isUserValidated ? 41.h : 38.h,
          ),
          !isUserValidated
              ? UsernameComponent(
                  onTap: _onTapNext,
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
          isUserValidated
              ? PasswordComponent(
                  isDisabled: password.isEmpty || password.length > 50,
                  onTapLogin: _onTapLogin,
                  onTapBack: () {
                    FocusManager.instance.primaryFocus?.unfocus();
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
