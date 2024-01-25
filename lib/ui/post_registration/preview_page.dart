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
import 'package:webview_flutter_plus/webview_flutter_plus.dart';

import '../../provider/registration_task_provider.dart';

class PreviewPage extends StatefulWidget {
  const PreviewPage({super.key});

  @override
  State<PreviewPage> createState() => _PreviewPageState();
}

class _PreviewPageState extends State<PreviewPage> {
  bool isLoading = true;
  WebViewPlusController? _controller;
  double _height = ScreenUtil().screenHeight;

  _registrationPreviewPageLoadedAudit() async {
    await context.read<GlobalProvider>().getAudit("REG-EVT-008", "REG-MOD-103");
  }

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: MediaQuery.of(context).size.width,
      height: _height,
      child: ListView(
        physics: const NeverScrollableScrollPhysics(),
        children: [
          SizedBox(
            height: _height,
            child:
                context.watch<RegistrationTaskProvider>().previewTemplate == ""
                    ? const Center(
                        child: CircularProgressIndicator(),
                      )
                    : WebViewPlus(
                        onWebViewCreated: (controller) {
                          _controller = controller;
                          controller.loadString(context
                              .read<RegistrationTaskProvider>()
                              .previewTemplate);
                        },
                        onPageFinished: (url) {
                          _controller!.getHeight().then((double height) {
                            setState(() {
                              _height = height + 250.h;
                            });
                            _registrationPreviewPageLoadedAudit();
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
    );
  }
}
