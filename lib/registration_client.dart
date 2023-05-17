/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'dart:convert';
import 'dart:io';

import 'package:document_scanner/document_scanner.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/app_config.dart';
import 'package:registration_client/const/utils.dart';
import 'package:registration_client/demographic_details_view.dart';
import 'package:provider/provider.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

import 'provider/global_provider.dart';

class RegistrationClient extends StatefulWidget {
  RegistrationClient({super.key, required this.onLogout});

  final VoidCallback onLogout;

  @override
  State<RegistrationClient> createState() => _RegistrationClientState();
}

class _RegistrationClientState extends State<RegistrationClient> {
  // This widget is the root of your application.
  static const platform =
      MethodChannel('com.flutter.dev/keymanager.test-machine');
  String syncDataResponse = '';
  bool isMobile = true;

  @override
  Widget build(BuildContext context) {
    isMobile = MediaQuery.of(context).orientation == Orientation.portrait;
    double h = ScreenUtil().screenHeight;
    double w = ScreenUtil().screenWidth;
    return SafeArea(
      child: Scaffold(
        backgroundColor: Utils.appSolidPrimary,
        body: Container(
          height: h,
          width: w,
          child: Column(
            children: [
              _appBarComponent(),
              SizedBox(
                height: 30.h,
              ),
              GridView.count(
                primary: false,
                scrollDirection: Axis.vertical,
                shrinkWrap: true,
                padding: const EdgeInsets.all(20),
                crossAxisSpacing: 10,
                mainAxisSpacing: 10,
                crossAxisCount: 2,
                children: [
                  InkWell(
                    onTap: () {
                      Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) =>
                                const MyHomePage(title: "Mosip New Registration"),
                          ));
                    },
                    child: Container(
                      padding: const EdgeInsets.all(8),
                      color: Utils.appWhite,
                      child: Center(
                        child: Text(
                          "New registration",
                          style: Utils.mobileHelpText,
                        ),
                      ),
                    ),
                  ),
                  InkWell(
                    onTap: () {
                      _masterDataSync();
                    },
                    child: Container(
                      padding: const EdgeInsets.all(8),
                      color: Utils.appWhite,
                      child: Center(
                          child: Text(
                        'Master Data Sync',
                        style: Utils.mobileHelpText,
                      )),
                    ),
                  ),
                  Container(
                    padding: const EdgeInsets.all(8),
                    color: Utils.appWhite,
                    child: Center(
                        child: Text(
                      'Packet Bundling',
                      style: Utils.mobileHelpText,
                    )),
                  ),
                  Container(
                    padding: const EdgeInsets.all(8),
                    color: Utils.appWhite,
                    child: Center(
                        child: Text(
                      'Biometrics Enabled',
                      style: Utils.mobileHelpText,
                    )),
                  ),
                ],
              ),
            ],
          ),
        ),
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
          Container(
            height: isMobile ? 46.h : 54.h,
            width: isMobile ? 115.39.w : 135.46.w,
            child: Image.asset(
              appIcon,
              scale: appIconScale,
            ),
          ),
          InkWell(
            onTap: widget.onLogout,
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
                  AppLocalizations.of(context)!.logout,
                  style: Utils.mobileHelpText,
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Future<void> _masterDataSync() async {
    String result;
    try {
      result = await platform.invokeMethod("masterDataSync");
    } on PlatformException catch (e) {
      result = "Some Error Occurred: $e";
    }
    setState(() {
      syncDataResponse = result;
    });
    debugPrint(syncDataResponse);
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = 0;

  void _incrementCounter() {
    setState(() {
      _counter++;
    });
  }

  @override
  Widget build(BuildContext context) {
    File scannedDocument;
    var height = MediaQuery.of(context).size.height;
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          children: <Widget>[
            SizedBox(
              height: 25,
            ),
            Image.asset(
              appIcon,
              scale: appIconScale,
            ),
            SizedBox(
              height: height * .2,
            ),
            // SvgPicture.asset("assets/images/svg-1.svg",
            //     semanticsLabel: "SVG Icon"),
            // OutlinedButton(
            //   onPressed: () {

            //   },
            //   child: Text("Scanner"),
            // ),

            Text(
              "Choose your language",
              style: Theme.of(context).textTheme.titleLarge,
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                Row(
                  children: [
                    Radio(
                        value: 1,
                        groupValue:
                            context.watch<GlobalProvider>().selectedLangCode,
                        onChanged: (value) {
                          context.read<GlobalProvider>().selectedLangCode =
                              value;
                        }),
                    Text("English"),
                  ],
                ),
                Row(
                  children: [
                    Radio(
                        value: 2,
                        groupValue:
                            context.watch<GlobalProvider>().selectedLangCode,
                        onChanged: (value) {
                          context.read<GlobalProvider>().selectedLangCode =
                              value;
                        }),
                    Text("Arabic"),
                  ],
                ),
                Row(
                  children: [
                    Radio(
                        value: 3,
                        groupValue:
                            context.watch<GlobalProvider>().selectedLangCode,
                        onChanged: (value) {
                          context.read<GlobalProvider>().selectedLangCode =
                              value;
                        }),
                    Text("French"),
                  ],
                ),
              ],
            ),
            SizedBox(
              height: height * .2,
            ),
            ElevatedButton(
                onPressed: () {
                  Navigator.push(
                      context,
                      MaterialPageRoute(
                          builder: (context) => DemographicDetailsView()));
                },
                child: Text("Start New Registration")),
            // const Text(
            //   'You have pushed the button this many times:',
            // ),
            // Text(
            //   '$_counter',
            //   style: Theme.of(context).textTheme.headlineMedium,
            // ),
          ],
        ),
      ),
      // floatingActionButton: FloatingActionButton(
      //   onPressed: _incrementCounter,
      //   tooltip: 'Increment',
      //   child: const Icon(Icons.add),
      // ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}
