import 'package:flutter/material.dart';
import 'package:flutter_inappwebview/flutter_inappwebview.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:webview_flutter_plus/webview_flutter_plus.dart';

import '../../provider/registration_task_provider.dart';

class PreviewPage extends StatefulWidget {
  const PreviewPage({super.key});

  @override
  _PreviewPageState createState() => _PreviewPageState();
}

class _PreviewPageState extends State<PreviewPage> {
  bool isLoading = true;
  WebViewPlusController? _controller;
  double _height = 1;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: MediaQuery.of(context).size.width,
      height: MediaQuery.of(context).size.height,
      child: ListView(
        children: [
          SizedBox(
            height: _height + 150.h,
            child:
                context.watch<RegistrationTaskProvider>().previewTemplate == ""
                    ? const Center(child: CircularProgressIndicator(),)
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
                              _height = height;
                            });
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
