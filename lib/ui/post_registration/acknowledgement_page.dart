/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/app_style.dart';
import 'package:webview_flutter_plus/webview_flutter_plus.dart';
import 'package:printing/printing.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';


class AcknowledgementPage extends StatefulWidget {
  const AcknowledgementPage({super.key});

  @override
  State<AcknowledgementPage> createState() => _AcknowledgementPageState();
}

class _AcknowledgementPageState extends State<AcknowledgementPage> {
  WebViewPlusController? _controller;
  double _height = ScreenUtil().screenHeight;
  bool isMobile = true;

  @override
  void initState() {
    super.initState();
  }

  _printHtmlToPdf() async {
    String htmlContent =
        context.read<RegistrationTaskProvider>().acknowledgementTemplate;

    await Printing.layoutPdf(
        onLayout: (format) async => await Printing.convertHtml(
              format: format,
              html: htmlContent,
            ));
  }

  _registrationAcknowledgementPageLoadedAudit() async {
    await context.read<GlobalProvider>().getAudit("REG-EVT-011", "REG-MOD-103");
  }

  _printAcknowledgementAudit() async {
    await context
        .read<GlobalProvider>()
        .getAudit("REG-EVT-012", "REG-MOD-103");
  }

  @override
  Widget build(BuildContext context) {
    isMobile = MediaQuery.of(context).orientation == Orientation.portrait;
    return SizedBox(
      height: _height,
      width: ScreenUtil().screenWidth,
      child: Column(
        children: [
          SizedBox(
            height: 23.h,
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                AppLocalizations.of(context)!.registration_acknowledgement,
                style: const TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.w500,
                  color: appBlackShade1,
                ),
              ),
              InkWell(
                onTap: () {
                  _printAcknowledgementAudit();
                  _printHtmlToPdf();
                },
                child: Container(
                  height: 42.h,
                  width: 170.w,
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
                      AppLocalizations.of(context)!.print_text,
                      style: isMobile
                          ? AppTextStyle.tabletPortraitButtonText
                          : AppTextStyle.mobileButtonText,
                    ),
                  ),
                ),
              ),
            ],
          ),
          SizedBox(
            height: 33.h,
          ),
          Expanded(
            child: ListView(
              physics: const NeverScrollableScrollPhysics(),
              children: [
                SizedBox(
                  height: _height,
                  child: WebViewPlus(
                    onWebViewCreated: (controller) {
                      _controller = controller;
                      controller.loadString(context
                          .read<RegistrationTaskProvider>()
                          .acknowledgementTemplate);
                    },
                    onPageFinished: (url) {
                      _controller!.getHeight().then((double height) {
                        setState(() {
                          _height = height + 250.h;
                        });
                        _registrationAcknowledgementPageLoadedAudit();
                      });
                    },
                    javascriptMode: JavascriptMode.unrestricted,
                  ),
                ),
                SizedBox(
                  height: 20.h,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
