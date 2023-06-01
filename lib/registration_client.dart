/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'dart:core';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import 'package:registration_client/const/utils.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/utils/app_config.dart';

class RegistrationClient extends StatefulWidget {
  static const route = "/registration-client";
  RegistrationClient({super.key});

  // final VoidCallback onLogout;

  @override
  State<RegistrationClient> createState() => _RegistrationClientState();
}

class _RegistrationClientState extends State<RegistrationClient> {
  // This widget is the root of your application.
  static const platform =
      MethodChannel('com.flutter.dev/io.mosip.get-package-instance');
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
                    onTap: () {},
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
                  InkWell(
                    onTap: () {
                      // _testingSpec();
                      _adityaTestingSpec();
                    },
                    child: Container(
                      padding: const EdgeInsets.all(8),
                      color: Utils.appWhite,
                      child: Center(
                          child: Text(
                        'Packet Bundling',
                        style: Utils.mobileHelpText,
                      )),
                    ),
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
              // scale: appIconScale,
            ),
          ),
          InkWell(
            onTap: () {},
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

  Future<void> _testingSpec() async {
    String result;
    try {
      await platform.invokeMethod("testingSpec");
    } on PlatformException catch (e) {
      result = "Some Error Occurred: $e";
    }
    // setState(() {
    //   // syncDataResponse = result;
    // });
    debugPrint(syncDataResponse);
  }
  Future<void> _adityaTestingSpec() async {
    String result;
    try {
      await platform.invokeMethod("adityaTestingSpec",{"centerId":"10006"});
    } on PlatformException catch (e) {
      result = "Some Error Occurred: $e";
    }
    // setState(() {
    //   // syncDataResponse = result;
    // });
    debugPrint(syncDataResponse);
  }
}
