/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/provider/global_provider.dart';

import 'package:registration_client/utils/file_storage.dart';
import 'package:registration_client/utils/app_style.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

// ignore: must_be_immutable
class MachineKeys extends StatelessWidget {
  MachineKeys({super.key});

  String machineDetails = '';
  bool isMobile = true;
  late BuildContext _context;

  @override
  Widget build(BuildContext context) {
    _context = context;
    Map<String?, String?> map = context.read<GlobalProvider>().machineDetails;

    if (map.isEmpty) {
      machineDetails = AppLocalizations.of(context)!.not_initialized;
    } else {
      machineDetails = jsonEncode(map).toString();
    }

    isMobile = MediaQuery.of(context).orientation == Orientation.portrait;
    return Scaffold(
      appBar: AppBar(
        title: Text(
          AppLocalizations.of(context)!.device_credentials,
        ),
        actions: [
          IconButton(
            onPressed: () {
              Navigator.pop(context);
            },
            icon: const Icon(
              Icons.close_rounded,
              color: AppStyle.appWhite,
            ),
          )
        ],
        backgroundColor: AppStyle.appSolidPrimary,
        automaticallyImplyLeading: false,
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
    );
  }

  void showInSnackBar(String value) {
    ScaffoldMessenger.of(_context).showSnackBar(
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
          showInSnackBar(AppLocalizations.of(_context)!.copy_message);
        });
      },
      child: Container(
        height: 48.h,
        decoration: BoxDecoration(
          color: AppStyle.appSolidPrimary,
          border: Border.all(
            width: 1.w,
            color: AppStyle.appBlueShade1,
          ),
          borderRadius: const BorderRadius.all(
            Radius.circular(5),
          ),
        ),
        child: Center(
          child: Text(
            AppLocalizations.of(_context)!.copy_text,
            style: AppStyle.mobileButtonText,
          ),
        ),
      ),
    );
  }

  Widget _downloadButton() {
    return InkWell(
      onTap: () {
        FileStorage.writeCounter(machineDetails, "machine_details.txt")
            .then((value) {
          showInSnackBar(AppLocalizations.of(_context)!.download_message);
        });
      },
      child: Container(
        height: 48.h,
        decoration: BoxDecoration(
          color: isMobile ? AppStyle.appWhite : AppStyle.appSolidPrimary,
          border: Border.all(
            width: 1.w,
            color: AppStyle.appBlueShade1,
          ),
          borderRadius: const BorderRadius.all(
            Radius.circular(5),
          ),
        ),
        child: Center(
          child: Text(
            AppLocalizations.of(_context)!.download_json,
            style: isMobile
                ? AppStyle.mobileBackButtonText
                : AppStyle.mobileButtonText,
          ),
        ),
      ),
    );
  }
}
