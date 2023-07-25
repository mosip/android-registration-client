import 'package:flutter/material.dart';
import 'package:flutter_html/flutter_html.dart';
import 'package:flutter_inappwebview/flutter_inappwebview.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';

class PreviewPage extends StatelessWidget {
  const PreviewPage({super.key});



  @override
  Widget build(BuildContext context) {
    return Container(
        height: 400.h,
        child: InAppWebView(
          initialData: InAppWebViewInitialData(data: context.watch<RegistrationTaskProvider>().previewTemplate),
        ),
      );
  }
}
