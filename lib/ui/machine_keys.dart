/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/utils/app_config.dart';

import 'package:registration_client/utils/file_storage.dart';
import 'package:registration_client/utils/app_style.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

// ignore: must_be_immutable
class MachineKeys extends StatelessWidget {
  final VoidCallback onCloseComponent;

  MachineKeys({
    super.key,
    required this.onCloseComponent,
  });

  String machineDetails = '';
  bool isMobile = true;
  late BuildContext _context;

  _machineKeysLoadedAudit() async {
    await _context
        .read<GlobalProvider>()
        .getAudit("REG-LOAD-002", "REG-MOD-101");
  }

  @override
  Widget build(BuildContext context) {
    _context = context;
    Map<String?, String?> map = context.read<GlobalProvider>().machineDetails;

    if (map.isEmpty) {
      machineDetails = AppLocalizations.of(context)!.not_initialized;
    } else {
      machineDetails = jsonEncode(map).toString();
    }
    _machineKeysLoadedAudit();
    isMobile = MediaQuery.of(context).orientation == Orientation.portrait;

    return Container(
      width: isMobile
          ? isMobileSize
              ? 358.w
              : 566.w
          : 670.w,
      padding: EdgeInsets.only(left: 20.w, right: 19.w),
      decoration: const BoxDecoration(
        color: appWhite,
        borderRadius: BorderRadius.all(
          Radius.circular(12),
        ),
      ),
      child: SingleChildScrollView(
        child: Column(
          children: [
            Container(
              padding: EdgeInsets.symmetric(
                vertical: 20.h,
              ),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Expanded(
                    child: Text(
                      AppLocalizations.of(context)!.device_credentials,
                      style: TextStyle(
                        color: appBlack,
                        fontSize: isMobileSize ? 18 : 26,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                  IconButton(
                    onPressed: onCloseComponent,
                    icon: const Icon(
                      Icons.close,
                      color: appBlack,
                    ),
                  ),
                ],
              ),
            ),
            Divider(
              color: dividerColor,
              height: 1.h,
            ),
            SizedBox(
              height: 20.h,
            ),
            _copyButton(),
            SizedBox(
              height: 20.h,
            ),
            _downloadButton(
              title: AppLocalizations.of(_context)!.download_json,
              onTap: () {
                FileStorage.writeCounter(machineDetails, "machine_details.txt")
                    .then((value) {
                  showInSnackBar(
                      AppLocalizations.of(_context)!.download_message);
                });
              },
            ),
            SizedBox(
              height: 20.h,
            ),
            _downloadButton(
              title: "SHARE",
              onTap: () {},
            ),
            SizedBox(
              height: 30.h,
            ),
          ],
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

  Widget _copyButton() {
    return InkWell(
      onTap: () {
        Clipboard.setData(ClipboardData(text: machineDetails)).then((value) {
          showInSnackBar(AppLocalizations.of(_context)!.copy_message);
        });
      },
      child: Container(
        height: isMobile && !isMobileSize ? 94.h : 62.h,
        decoration: BoxDecoration(
          color: appSolidPrimary,
          border: Border.all(
            width: 1.w,
            color: appBlueShade1,
          ),
          borderRadius: const BorderRadius.all(
            Radius.circular(5),
          ),
        ),
        child: Center(
          child: Text(
            AppLocalizations.of(_context)!.copy_text,
            style: isMobileSize
                ? AppTextStyle.primaryButtonTextSmall
                : AppTextStyle.primaryButtonText,
          ),
        ),
      ),
    );
  }

  Widget _downloadButton({required String title, required VoidCallback onTap}) {
    return InkWell(
      onTap: onTap,
      child: Container(
        height: isMobile && !isMobileSize ? 94.h : 62.h,
        decoration: BoxDecoration(
          color: appWhite,
          border: Border.all(
            width: 1.w,
            color: appBlueShade1,
          ),
          borderRadius: const BorderRadius.all(
            Radius.circular(5),
          ),
        ),
        child: Center(
          child: Text(
            title,
            style: isMobileSize
                ? AppTextStyle.secondaryButtonTextSmall
                : AppTextStyle.secondaryButtonText,
          ),
        ),
      ),
    );
  }
}
