/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:registration_client/app_config.dart';

import 'package:registration_client/credentials_page.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/registration_client.dart';

class LoginPage extends StatefulWidget {
  const LoginPage({super.key});

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  static const platform = MethodChannel("com.flutter.dev/keymanager.test-machine");
  bool isLoggedIn = false;
  bool isLoggingIn = false;
  String loginResponse = '';
  String username = '';
  String password = '';
  final _formKey = GlobalKey<FormState>();

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    double height = MediaQuery.of(context).size.height;
    double width = MediaQuery.of(context).size.width;
    return isLoggedIn ? const RegistrationClient() : SafeArea(
      child: Scaffold(
        appBar: AppBar(
          title: const Text('Registration Client'),
          backgroundColor: Colors.deepPurpleAccent,
        ),
        body: Container(
          height: height,
          width: width,
          padding: const EdgeInsets.symmetric(
            horizontal: 20.0,
          ),
          color: Colors.white,
          child: Form(
            key: _formKey,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                // const SizedBox(
                //   height: 100,
                // ),
                InkWell(
                  onLongPress: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => const CredentialsPage(),
                      ),
                    );
                  },
                  child: Image.asset(
                    appIcon,
                    scale: appIconScale,
            ),
                ),
                const SizedBox(
                  height: 25,
                ),
                TextFormField(
                  validator: (value) {
                    if (value == null || value.isEmpty) {
                      return 'Enter Username!';
                    }
                    return null;
                  },
                  onSaved: (v) {
                    setState(() {
                      username = v!;
                    });
                  },
                  decoration: const InputDecoration(
                    suffixIcon: Icon(Icons.person_2_outlined),
                    labelText: 'username',
                    hintText: 'Username',
                    border: UnderlineInputBorder(),
                  ),
                ),
                TextFormField(
                  obscureText: true,
                  validator: (value) {
                    if (value == null || value.isEmpty) {
                      return 'Enter Password!';
                    }
                    return null;
                  },
                  onSaved: (v) {
                    setState(() {
                      password = v!;
                    });
                  },
                  decoration: const InputDecoration(
                    suffixIcon: Icon(Icons.lock_outline),
                    labelText: 'password',
                    hintText: 'password',
                    border: UnderlineInputBorder(),
                  ),
                ),
                const SizedBox(
                  height: 20,
                ),
                ElevatedButton(
                  style: const ButtonStyle(
                    backgroundColor:
                        MaterialStatePropertyAll(Colors.deepPurpleAccent),
                  ),
                  onPressed: () {
                    setState(() {
                      isLoggingIn = true;
                    });
                    if (_formKey.currentState!.validate()) {
                      _formKey.currentState!.save();
                      _login(username, password).then((value) {
                        if(loginResponse.isNotEmpty && !isLoggedIn) {
                          showInSnackBar(loginResponse);
                        }
                        isLoggingIn = false;
                      });
                    }
                  },
                  child: isLoggingIn ? const CircularProgressIndicator() : const Text('Login'),
                ),
                ElevatedButton(
                  onPressed: () {
                    _testMachine();
                  },
                  child: const Text("Test"),
                )
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
      response = await platform.invokeMethod("login", {'username': username, 'password': password});
      mp = jsonDecode(response);
      debugPrint('Login: $mp');
    } on PlatformException catch(e) {
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

  Future<void> _testMachine() async {
    String result;
    try {
      result = await platform.invokeMethod("testMachine");
    } on PlatformException catch (e) {
      result = "Error: $e";
    }
    debugPrint("result: $result");
  } 

  void showInSnackBar(String value) {
    ScaffoldMessenger.of(context)
      .showSnackBar(
        SnackBar(
          content: Text(value), 
        ),
      );
  }
}
