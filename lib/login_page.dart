/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/const/app_config.dart';
import 'package:registration_client/const/utils.dart';

import 'package:registration_client/credentials_page.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/data/models/login_response.dart';
import 'package:registration_client/provider/app_language.dart';
import 'package:registration_client/registration_client.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/widgets/password_component.dart';
import 'package:registration_client/widgets/username_component.dart';

class LoginPage extends StatefulWidget {
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
  List<String> _languages = ['eng', 'ara', 'fre'];
  Map<String, String> mp = {};
  final _formKey = GlobalKey<FormState>();
  TextEditingController usernameController = TextEditingController();
  TextEditingController passwordController = TextEditingController();
  late LoginResponse loginResp;
  bool _isConnected = false;
  ConnectivityResult _connectionStatus = ConnectivityResult.none;
  final Connectivity _connectivity = Connectivity();
  late StreamSubscription<ConnectivityResult> _connectivitySubscription;

  @override
  void initState() {
    super.initState();
    initConnectivity();
    _connectivitySubscription =
        _connectivity.onConnectivityChanged.listen(_updateConnectionStatus);
    mp['eng'] = "English";
    mp['ara'] = "العربية";
    mp['fre'] = "Français";
  }

  @override
  void dispose() {
    _connectivitySubscription.cancel();
    super.dispose();
  }

  Future<void> initConnectivity() async {
    late ConnectivityResult result;
    try {
      result = await _connectivity.checkConnectivity();
    } on PlatformException catch (e) {
      debugPrint('Couldn\'t check connectivity status');
      return;
    }

    if (!mounted) {
      return Future.value(null);
    }

    return _updateConnectionStatus(result);
  }

  Future<void> _updateConnectionStatus(ConnectivityResult result) async {
    setState(() {
      _connectionStatus = result;
      if(result == ConnectivityResult.none) {
        _isConnected = false;
      } else {
        _isConnected = true;
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    isMobile = MediaQuery.of(context).orientation == Orientation.portrait;
    double h = ScreenUtil().screenHeight;
    double w = ScreenUtil().screenWidth;
    return isLoggedIn
        ? RegistrationClient(
            onLogout: () {
              setState(() {
                username = '';
                isUserValidated = false;
                isLoggedIn = false;
              });
            },
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
    String response;
    Map<String, dynamic> mp;
    try {
      response = await platform
          .invokeMethod("login", {'username': username, 'password': password, 'isConnected': _isConnected,});
      mp = jsonDecode(response);
      loginResp = LoginResponse.fromJson(mp);
    } on PlatformException catch (e) {
      mp = {};
    }
    setState(() {
      isLoggedIn = loginResp.isLoggedIn;
      errorCode = loginResp.error_code;
      if(isLoggedIn) {
        loginResponse = loginResp.login_response;
      } else if(errorCode == '500') {
        loginResponse = AppLocalizations.of(context)!.login_failed;
      } else if(errorCode == '501') {
        loginResponse = AppLocalizations.of(context)!.network_error;
      } else if(errorCode == '401') {
        loginResponse = AppLocalizations.of(context)!.password_incorrect;
      } else if(errorCode == 'OFFLINE') {
        loginResponse = loginResp.login_response;
      } else {
        loginResponse = AppLocalizations.of(context)!.machine_not_found;
      }
    });
  }

  Future<void> _validateUsername() async {
    String response;
    Map<String, dynamic> mp;

    try {
      response = await platform
          .invokeMethod("validateUsername", {'username': username});
      mp = jsonDecode(response);
    } on PlatformException catch (e) {
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
              width: isMobile ? 115.39.w : 135.46.w,
              child: Image.asset(
                appIcon,
                scale: appIconScale,
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
      // height: isMobile ? 47.h : 62.h,
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
          ),
          Text(
            AppLocalizations.of(context)!.community_reg_text,
            style: Utils.mobileCommunityRegClientText,
          )
        ],
      ),
    );
  }

  Widget _infoTextComponent() {
    return Container(
      // height: isMobile ? 17.h : 20.h,
      padding: EdgeInsets.symmetric(
        horizontal: isMobile ? 52.w : 0,
      ),
      child: Text(
        AppLocalizations.of(context)!.info_text,
        style: Utils.mobileInfoText,
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
      // height: 414.h,
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
      children: [
        _appCombinedTextComponent(),
        SizedBox(
          height: 40.h,
        ),
        _loginComponent(),
        SizedBox(
          height: 174.h,
        ),
      ],
    );
  }

  Widget _tabletView() {
    return SingleChildScrollView(
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          _appCombinedTextComponent(),
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
      // height: 168.48.h,
      // width: 222.28.w,
      child: Image.asset(
        isMobile ? buildingsX : buildingsXX,
        fit: BoxFit.fill,
      ),
    );
  }
}
