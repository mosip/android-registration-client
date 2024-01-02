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
      width: isMobile ? 330.w : 670.w,
      padding: EdgeInsets.only(left: 20.w, right: 19.w),
      decoration: const BoxDecoration(
        color: AppStyle.appWhite,
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
                  Text(
                    AppLocalizations.of(context)!.device_credentials,
                    style: const TextStyle(
                      color: AppStyle.appBlack,
                      fontSize: 26,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  IconButton(
                    onPressed: onCloseComponent,
                    icon: const Icon(
                      Icons.close,
                      color: AppStyle.appBlack,
                    ),
                  ),
                ],
              ),
            ),
            Divider(
              color: AppStyle.dividerColor,
              height: 1.h,
            ),
            // Container(
            //   padding: EdgeInsets.symmetric(
            //     vertical: 20.h,
            //   ),
            //   child: SelectableText(
            //     machineDetails,
            //     textDirection: TextDirection.ltr,
            //     style: const TextStyle(
            //       fontWeight: FontWeight.w500,
            //       fontSize: 16,
            //     ),
            //   ),
            // ),
            // Divider(
            //   color: AppStyle.dividerColor,
            //   height: 1.h,
            // ),
            SizedBox(
              height: 20.h,
            ),
            // isMobile ? _mobileView() : _tabletView(),
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

  // Widget _buttonRow() {
  //   return Row(
  //     children: [
  //       // Expanded(child: _downloadButton()),
  //       SizedBox(
  //         width: 10.w,
  //       ),
  //       Expanded(child: _copyButton()),
  //     ],
  //   );
  // }

  // Widget _mobileView() {
  //   return Column(
  //     crossAxisAlignment: CrossAxisAlignment.center,
  //     children: [
  //       _buttonRow(),
  //       SizedBox(
  //         height: 29.h,
  //       ),
  //       _shareButton(),
  //     ],
  //   );
  // }

  // Widget _tabletView() {
  //   return Row(
  //     mainAxisAlignment: MainAxisAlignment.spaceBetween,
  //     children: [
  //       Expanded(
  //         child: _shareButton(),
  //       ),
  //       Expanded(
  //         child: _buttonRow(),
  //       ),
  //     ],
  //   );
  // }

  Widget _copyButton() {
    return InkWell(
      onTap: () {
        Clipboard.setData(ClipboardData(text: machineDetails)).then((value) {
          showInSnackBar(AppLocalizations.of(_context)!.copy_message);
        });
      },
      child: Container(
        height: 62.h,
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
            style: AppStyle.primaryButtonText,
          ),
        ),
      ),
    );
  }

  Widget _downloadButton({required String title, required VoidCallback onTap}) {
    return InkWell(
      onTap: onTap,
      child: Container(
        height: 62.h,
        decoration: BoxDecoration(
          color: AppStyle.appWhite,
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
            title,
            style: AppStyle.secondaryButtonText,
          ),
        ),
      ),
    );
  }

  // _shareButton() {
  //   return InkWell(
  //     child: Row(
  //       children: [
  //         const Icon(
  //           Icons.share,
  //           color: AppStyle.appSolidPrimary,
  //         ),
  //         SizedBox(
  //           width: 5.w,
  //         ),
  //         const Text(
  //           'SHARE',
  //           style: TextStyle(
  //             color: AppStyle.appSolidPrimary,
  //             fontSize: 14,
  //             fontWeight: FontWeight.bold,
  //           ),
  //         ),
  //       ],
  //     ),
  //   );
  // }
}
