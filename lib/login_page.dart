/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/app_config.dart';
import 'package:registration_client/const/utils.dart';

import 'package:registration_client/credentials_page.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/provider/app_language.dart';
import 'package:registration_client/registration_client.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class LoginPage extends StatefulWidget {
  const LoginPage({super.key});

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  bool isMobile = true;
  static const platform =
      MethodChannel("com.flutter.dev/keymanager.test-machine");
  bool isLoggedIn = false;
  bool isLoggingIn = false;
  String loginResponse = '';
  String username = '';
  String password = '';
  bool isUserValidated = false;
  List<String> _languages = ['eng', 'ara', 'fre'];
  Map<String, String> mp = {};
  final _formKey = GlobalKey<FormState>();
  TextEditingController usernameController = TextEditingController();
  TextEditingController passwordController = TextEditingController();

  @override
  void initState() {
    super.initState();
    mp['eng'] = "English";
    mp['ara'] = "Arabic";
    mp['fre'] = "French";
  }

  @override
  Widget build(BuildContext context) {
    isMobile = MediaQuery.of(context).orientation == Orientation.portrait;
    double h = ScreenUtil().screenHeight;
    double w = ScreenUtil().screenWidth;
    return isLoggedIn
        ? const RegistrationClient()
        : SafeArea(
            child: Scaffold(
              backgroundColor: Utils.appSolidPrimary,
              body: Container(
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
                        child: _mobileView(),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          );
  }

  Future<void> _login(String username, String password) async {
    String response;
    Map<String, dynamic> mp;
    try {
      response = await platform
          .invokeMethod("login", {'username': username, 'password': password});
      mp = jsonDecode(response);
      debugPrint('Login: $mp');
    } on PlatformException catch (e) {
      mp = {
        'login_response': 'Login Failed..Try Again!',
        'isLoggedIn': 'false',
      };
    }
    setState(() {
      loginResponse = mp['login_response'];
      isLoggedIn = mp['isLoggedIn'] == 'true' ? true : false;
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
      mp = {
        'user_response': 'Failed!',
        'isUserPresent': 'false',
      };
    }

    setState(() {
      setState(() {
        loginResponse = mp['user_response'];
        isUserValidated = mp['isUserPresent'] == 'true' ? true : false;
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
            height: 34.h,
            child: Text(
              AppLocalizations.of(context)!.login_text,
              style: Utils.mobileHeaderText,
            ),
          ),
          SizedBox(
            height: isUserValidated ? 41.h : 38.h,
          ),
          !isUserValidated ? _usernameComponent() : const SizedBox(),
          isUserValidated ? _passwordComponent() : const SizedBox(),
        ],
      ),
    );
  }

  Widget _usernameComponent() {
    final appLanguage = Provider.of<AppLanguage>(context, listen: false);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          height: 17.h,
          child: Text(
            AppLocalizations.of(context)!.language,
            style: Utils.mobileTextfieldHeader,
          ),
        ),
        SizedBox(
          height: 8.h,
        ),
        Container(
          height: 48.h,
          // width: 318.w,
          padding: EdgeInsets.only(
            left: 17.w,
            right: (14.42).w,
          ),
          decoration: BoxDecoration(
            border: Border.all(
              width: 1.h,
              color: Utils.appGreyShade,
            ),
            borderRadius: const BorderRadius.all(
              Radius.circular(6),
            ),
          ),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              DropdownButton(
                value: context.watch<AppLanguage>().selectedLanguage,
                underline: const SizedBox.shrink(),
                icon: const SizedBox.shrink(),
                onChanged: (newValue) {
                  context.read<AppLanguage>().selectedLanguage = newValue!;
                  appLanguage.changeLanguage(Locale(newValue));
                },
                items: _languages.map((lang) {
                  return DropdownMenuItem(
                    value: lang,
                    child: Container(
                      height: 17.h,
                      // width: 47.w,
                      child: Text(
                        mp[lang]!,
                        style: Utils.mobileDropdownText,
                      ),
                    ),
                  );
                }).toList(),
              ),
              Container(
                child: const Icon(
                  Icons.keyboard_arrow_down_outlined,
                  color: Utils.appGreyShade,
                ),
              )
            ],
          ),
        ),
        SizedBox(
          height: 30.h,
        ),
        Container(
          height: 17.h,
          child: Text(
            AppLocalizations.of(context)!.username,
            style: Utils.mobileTextfieldHeader,
          ),
        ),
        SizedBox(
          height: 11.h,
        ),
        Container(
          height: 52.h,
          alignment: Alignment.centerLeft,
          padding: EdgeInsets.symmetric(
            horizontal: 17.w,
          ),
          decoration: BoxDecoration(
            border: Border.all(
              width: 1.h,
              color: Utils.appGreyShade,
            ),
            borderRadius: const BorderRadius.all(
              Radius.circular(6),
            ),
          ),
          child: TextField(
            controller: usernameController,
            decoration: InputDecoration(
              hintText: AppLocalizations.of(context)!.enter_username,
              hintStyle: Utils.mobileTextfieldHintText,
              border: InputBorder.none,
            ),
          ),
        ),
        SizedBox(
          height: 30.h,
        ),
        InkWell(
          onTap: () {
            setState(() {
              username = usernameController.text;
            });
            if (username.isEmpty) {
              _showInSnackBar(AppLocalizations.of(context)!.username_required);
            } else if (username.length > 50) {
              _showInSnackBar(AppLocalizations.of(context)!.username_exceed);
            } else if (!isUserValidated) {
              _validateUsername().then((value) {
                _showInSnackBar(loginResponse);
              });
            }
          },
          child: Container(
            height: 52.h,
            decoration: BoxDecoration(
              color: Utils.appSolidPrimary,
              border: Border.all(
                width: 1.w,
                color: Utils.appBlueShade1,
              ),
              borderRadius: const BorderRadius.all(
                Radius.circular(5),
              ),
            ),
            child: Center(
              child: Text(
                AppLocalizations.of(context)!.next_button,
                style: Utils.mobileButtonText,
              ),
            ),
          ),
        ),
      ],
    );
  }

  Widget _passwordComponent() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          height: 17.h,
          child: Text(
            AppLocalizations.of(context)!.password,
            style: Utils.mobileTextfieldHeader,
          ),
        ),
        SizedBox(
          height: 11.h,
        ),
        Container(
          height: 52.h,
          alignment: Alignment.centerLeft,
          padding: EdgeInsets.only(
            left: 17.w,
          ),
          decoration: BoxDecoration(
            border: Border.all(
              width: 1.h,
              color: Utils.appGreyShade,
            ),
            borderRadius: const BorderRadius.all(
              Radius.circular(6),
            ),
          ),
          child: TextField(
            obscureText: true,
            controller: passwordController,
            onChanged: (v) {
              setState(() {
                password = v;
              });
            },
            decoration: InputDecoration(
              hintText: AppLocalizations.of(context)!.enter_password,
              hintStyle: Utils.mobileTextfieldHintText,
              border: InputBorder.none,
            ),
          ),
        ),
        SizedBox(
          height: 15.h,
        ),
        InkWell(
          onTap: () {},
          child: Container(
            height: 17.h,
            alignment: Alignment.centerRight,
            child: Text(
              AppLocalizations.of(context)!.forgot_password,
              style: Utils.mobileForgotPasswordText,
            ),
          ),
        ),
        SizedBox(
          height: 30.h,
        ),
        InkWell(
          onTap: () {
            debugPrint('Username: $username and Password: $password');
            if (password.isEmpty) {
              _showInSnackBar(AppLocalizations.of(context)!.password_required);
              return;
            } else if (password.length > 50) {
              _showInSnackBar(AppLocalizations.of(context)!.password_exceed);
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
          },
          child: Container(
            height: 52.h,
            decoration: BoxDecoration(
              color: Utils.appSolidPrimary,
              border: Border.all(
                width: 1.w,
                color: Utils.appBlueShade1,
              ),
              borderRadius: const BorderRadius.all(
                Radius.circular(5),
              ),
            ),
            child: Center(
              child: isLoggingIn
                  ? const CircularProgressIndicator(
                      color: Utils.appWhite,
                    )
                  : Text(
                      AppLocalizations.of(context)!.login_text,
                      style: Utils.mobileButtonText,
                    ),
            ),
          ),
        ),
        SizedBox(
          height: 10.h,
        ),
        InkWell(
          onTap: () {
            setState(() {
              isUserValidated = false;
            });
          },
          child: Container(
            height: 52.h,
            // width: 318.w,
            decoration: BoxDecoration(
              color: Utils.appWhite,
              border: Border.all(
                width: 1.w,
                color: Utils.appBackButtonBorder,
              ),
              borderRadius: const BorderRadius.all(
                Radius.circular(5),
              ),
            ),
            child: Center(
              child: Text(
                AppLocalizations.of(context)!.back_button,
                style: Utils.mobileBackButtonText,
              ),
            ),
          ),
        ),
      ],
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
      ],
    );
  }

// Widget _tabletView() {
//   return SingleChildScrollView(
//     child: Row(
//       mainAxisAlignment: MainAxisAlignment.spaceBetween,
//       children: [
//         _appCombinedTextComponent(),
//         // SizedBox(
//         //   width: 41.w,
//         // ),
//         _loginComponent(),
//       ],
//     ),
//   );
// }
}
