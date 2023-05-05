/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/app_config.dart';
import 'package:registration_client/const/utils.dart';

import 'package:registration_client/credentials_page.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/registration_client.dart';

class LoginPage extends StatefulWidget {
  const LoginPage({super.key});

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  static const platform =
      MethodChannel("com.flutter.dev/keymanager.test-machine");
  bool isLoggedIn = false;
  bool isLoggingIn = false;
  String loginResponse = '';
  String username = '';
  String password = '';
  bool isUserValidated = false;
  List<String> _languages = ['English', 'Arabic', 'French'];
  String _selectedLanguage = 'English';
  final _formKey = GlobalKey<FormState>();
  TextEditingController usernameController = TextEditingController();
  TextEditingController passwordController = TextEditingController();

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return isLoggedIn
        ? const RegistrationClient()
        : SafeArea(
            child: Scaffold(
              backgroundColor: Utils.appSolidPrimary,
              body: Container(
                height: ScreenUtil().screenHeight,
                width: ScreenUtil().screenWidth,
                child: SingleChildScrollView(
                  child: Form(
                    key: _formKey,
                    child: Column(
                      children: [
                        _appBarComponent(),
                        SizedBox(
                          height: 50.h,
                        ),
                        _welcomeTextComponent(),
                        SizedBox(
                          height: 12.h,
                        ),
                        _infoTextComponent(),
                        SizedBox(
                          height: 40.h,
                        ),
                        _loginComponent(),

                        // ElevatedButton(
                        //   style: const ButtonStyle(
                        //     backgroundColor:
                        //         MaterialStatePropertyAll(Colors.deepPurpleAccent),
                        //   ),
                        //   onPressed: () {
                        //     setState(() {
                        //       isLoggingIn = true;
                        //     });
                        //     if (_formKey.currentState!.validate()) {
                        //       _formKey.currentState!.save();
                        //       _login(username, password).then((value) {
                        //         if (loginResponse.isNotEmpty && !isLoggedIn) {
                        //           showInSnackBar(loginResponse);
                        //         }
                        //         isLoggingIn = false;
                        //       });
                        //     }
                        //   },
                        //   child: isLoggingIn
                        //       ? const CircularProgressIndicator()
                        //       : const Text('Login'),
                        // ),
                      ],
                    ),
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
        'login_response': 'Failed!',
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
      response = await platform.invokeMethod("validateUsername", {'username': username});
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

  void showInSnackBar(String value) {
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
        horizontal: 16.w,
      ),
      child: Row(
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
              height: 46.h,
              width: 115.39.w,
              child: Image.asset(
                appIcon,
                scale: appIconScale,
              ),
            ),
          ),
          SizedBox(
            width: (113.61).w,
          ),
          InkWell(
            child: Container(
              width: 129.w,
              height: 46.h,
              decoration: BoxDecoration(
                border: Border.all(
                  width: 1.h,
                  color: Utils.appHelpText,
                ),
                borderRadius: const BorderRadius.all(
                  Radius.circular(5),
                ),
              ),
              child: const Center(
                child: Text(
                  'HELP',
                  style: Utils.helpText,
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
      height: 47.w,
      padding: EdgeInsets.symmetric(
        horizontal: 57.w,
      ),
      child: Column(
        children: const [
          Text(
            'Welcome to',
            style: Utils.welcomeText,
          ),
          Text(
            'Community Registration Client!',
            style: Utils.communityRegClientText,
          )
        ],
      ),
    );
  }

  Widget _infoTextComponent() {
    return Container(
      height: 17.h,
      padding: EdgeInsets.symmetric(
        horizontal: 68.w,
      ),
      child: const Text(
        'Please login to access all the features.',
        style: Utils.infoText,
      ),
    );
  }

  Widget _loginComponent() {
    return Container(
      height: 414.h,
      width: 358.w,
      padding: EdgeInsets.symmetric(
        horizontal: 20.w,
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
        children: [
          SizedBox(
            height: 54.h,
          ),
          Container(
            height: 34.h,
            child: const Text(
              'Login',
              style: Utils.headerText,
            ),
          ),
          SizedBox(
            height: isUserValidated ? 41.h : 38.h,
          ),
          !isUserValidated
              ? Container(
                  height: 17.h,
                  child: const Text(
                    'Language',
                    style: Utils.textfieldHeader,
                  ),
                )
              : const SizedBox(),
          SizedBox(
            height: !isUserValidated ? 8.h : 0,
          ),
          !isUserValidated
              ? Container(
                  height: 48.h,
                  width: 318.w,
                  padding: EdgeInsets.only(left: 17.w, right: (14.42).w),
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
                    children: [
                      Align(
                        alignment: Alignment.centerLeft,
                        child: DropdownButton(
                          value: _selectedLanguage,
                          underline: const SizedBox.shrink(),
                          icon: const SizedBox.shrink(),
                          onChanged: (newValue) {
                            setState(() {
                              _selectedLanguage = newValue!;
                            });
                          },
                          items: _languages.map((lang) {
                            return DropdownMenuItem(
                              value: lang,
                              child: Container(
                                height: 17.h,
                                width: 47.w,
                                child: Text(
                                  lang,
                                  style: Utils.dropdownText,
                                ),
                              ),
                            );
                          }).toList(),
                        ),
                      ),
                      const Expanded(
                        child: SizedBox(),
                      ),
                      Container(
                        // height: (8.36).h,
                        width: (13.16).w,
                        alignment: Alignment.centerRight,
                        child: const Icon(
                          Icons.keyboard_arrow_down_outlined,
                          color: Utils.appGreyShade,
                        ),
                      )
                    ],
                  ),
                )
              : const SizedBox(),
          SizedBox(
            height: !isUserValidated ? 30.h : 0,
          ),
          !isUserValidated
              ? Container(
                  height: 17.h,
                  child: const Text(
                    'Username',
                    style: Utils.textfieldHeader,
                  ),
                )
              : const SizedBox(),
          SizedBox(
            height: !isUserValidated ? 11.h : 0,
          ),
          !isUserValidated
              ? Container(
                  height: 52.h,
                  width: 318.w,
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
                  child: Align(
                    alignment: Alignment.centerLeft,
                    child: TextField(
                      controller: usernameController,
                      decoration: const InputDecoration(
                        hintText: 'Enter Username',
                        hintStyle: Utils.textfieldHintText,
                        border: InputBorder.none,
                      ),
                    ),
                  ),
                )
              : const SizedBox(),
          isUserValidated
              ? Container(
                  height: 17.h,
                  child: const Text(
                    'Password',
                    style: Utils.textfieldHeader,
                  ),
                )
              : const SizedBox(),
          SizedBox(
            height: isUserValidated ? 11.h : 0,
          ),
          isUserValidated
              ? Container(
                  height: 52.h,
                  width: 318.w,
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
                  child: Align(
                    alignment: Alignment.centerLeft,
                    child: TextField(
                      obscureText: true,
                      controller: passwordController,
                      onChanged: (v) {
                        setState(() {
                          password = v;
                        });
                      },
                      decoration: const InputDecoration(
                        hintText: 'Enter Password',
                        hintStyle: Utils.textfieldHintText,
                        border: InputBorder.none,
                      ),
                    ),
                  ),
                )
              : const SizedBox(),
          SizedBox(
            height: isUserValidated ? 15.h : 0,
          ),
          isUserValidated
              ? InkWell(
                  onTap: () {},
                  child: Container(
                    height: 17.h,
                    alignment: Alignment.centerRight,
                    child: const Text(
                      'Forgot Password?',
                      style: Utils.forgotPasswordText,
                    ),
                  ),
                )
              : const SizedBox(),
          SizedBox(
            height: 30.h,
          ),
          !isUserValidated
              ? InkWell(
                  onTap: () {
                    setState(() {
                      username = usernameController.text;
                    });
                    if(username.isEmpty) {
                      showInSnackBar("Please enter a valid username!");
                    } else if(!isUserValidated) {
                      _validateUsername().then((value) {
                        showInSnackBar(loginResponse);
                      });
                    }
                  },
                  child: Container(
                    height: 52.h,
                    width: 318.w,
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
                    child: const Center(
                      child: Text(
                        'NEXT',
                        style: Utils.buttonText,
                      ),
                    ),
                  ),
                )
              : const SizedBox(),
          isUserValidated
              ? InkWell(
                  onTap: () {
                    debugPrint('Username: $username and Password: $password');
                    setState(() {
                      isLoggingIn = true;
                    });
                    _login(username, password).then((value) {
                      if (loginResponse.isNotEmpty && !isLoggedIn) {
                        showInSnackBar(loginResponse);
                      }
                      isLoggingIn = false;
                    });
                  },
                  child: Container(
                    height: 52.h,
                    width: 318.w,
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
                      child: isLoggingIn ? const CircularProgressIndicator(
                        color: Utils.appWhite,
                      ) : const Text(
                        'LOGIN',
                        style: Utils.buttonText,
                      ),
                    ),
                  ),
                )
              : const SizedBox(),
          SizedBox(
            height: isUserValidated ? 10.h : 0,
          ),
          isUserValidated
              ? InkWell(
                  onTap: () {
                    setState(() {
                      isUserValidated = false;
                    });
                  },
                  child: Container(
                    height: 52.h,
                    width: 318.w,
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
                    child: const Center(
                      child: Text(
                        'BACK',
                        style: Utils.backButtonText,
                      ),
                    ),
                  ),
                )
              : const SizedBox(),
        ],
      ),
    );
  }
}
