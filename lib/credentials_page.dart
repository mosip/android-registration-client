/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/const/FileStorage.dart';
import 'package:registration_client/const/utils.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class CredentialsPage extends StatefulWidget {
  const CredentialsPage({super.key});

  @override
  State<CredentialsPage> createState() => _CredentialsPageState();
}

class _CredentialsPageState extends State<CredentialsPage> {
  static const platform =
      MethodChannel('com.flutter.dev/keymanager.test-machine');
  String machineDetails = '';
  bool isMobile = true;

  @override
  void initState() {
    super.initState();
    _getMachineDetails();
  }

  @override
  Widget build(BuildContext context) {
    // ScreenUtil.init(context);
    isMobile = MediaQuery.of(context).orientation == Orientation.portrait;
    return Directionality(
      textDirection: TextDirection.ltr,
      child: Scaffold(
        appBar: AppBar(
          title: Text(
            AppLocalizations.of(context)!.device_credentials,
          ),
          backgroundColor: Utils.appSolidPrimary,
        ),
        body: Container(
          height: ScreenUtil().screenHeight,
          width: ScreenUtil().screenWidth,
          padding: EdgeInsets.symmetric(
            horizontal: isMobile ? 16.w : 80.w,
          ),
          child: SingleChildScrollView(
            child: Column(
              children: [
                SizedBox(
                  height: 10.h,
                ),
                SelectableText(
                  machineDetails,
                  textDirection: TextDirection.ltr,
                  style: TextStyle(
                    fontWeight: FontWeight.w500,
                    fontSize: 16.sp,
                  ),
                ),
                SizedBox(
                  height: 10.h,
                ),
                isMobile ? _mobileView() : _tabletView(),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Future<void> _getMachineDetails() async {
    String resultText;
    Map<String, dynamic> machineMap;
    try {
      resultText = await platform.invokeMethod('getMachineDetails');
      machineMap = jsonDecode(resultText);
      debugPrint("Machine Map $machineMap");
    } on PlatformException catch (e) {
      resultText = "Failed to get platform version: '${e.message}'.";
      machineMap = {};
    }

    setState(() {
      machineDetails = resultText;
    });
  }

  void showInSnackBar(String value) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(value),
      ),
    );
  }

  Widget _mobileView() {
    return Column(
      children: [
        _copyButton(),
        SizedBox(
          height: 10.h,
        ),
        _downloadButton(),
      ],
    );
  }

  Widget _tabletView() {
    return Row(
      children: [
        Expanded(child: _copyButton()),
        SizedBox(
          width: 25.w,
        ),
        Expanded(child: _downloadButton()),
      ],
    );
  }

  Widget _copyButton() {
    return InkWell(
      onTap: () {
        Clipboard.setData(ClipboardData(text: machineDetails)).then((value) {
          showInSnackBar(AppLocalizations.of(context)!.copy_message);
        });
      },
      child: Container(
        height: 48.h,
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
            AppLocalizations.of(context)!.copy_text,
            style: Utils.mobileButtonText,
          ),
        ),
      ),
    );
  }

  Widget _downloadButton() {
    return InkWell(
      onTap: () {
        FileStorage.writeCounter(
            machineDetails, "machine_details.txt").then((value) {
              showInSnackBar(AppLocalizations.of(context)!.download_message);
        });
      },
      child: Container(
        height: 48.h,
        decoration: BoxDecoration(
          color: isMobile ? Utils.appWhite : Utils.appSolidPrimary,
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
            AppLocalizations.of(context)!.download_json,
            style:
                isMobile ? Utils.mobileBackButtonText : Utils.mobileButtonText,
          ),
        ),
      ),
    );
  }
}
