import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

class Test extends StatelessWidget {
  const Test({super.key});

  @override
  Widget build(BuildContext context) {
    // ScreenUtil.init(context);
    // ScreenUtil.init(context, designSize: const Size(390, 854));
    return Scaffold(
      body: Container(
        height: ScreenUtil().setHeight(50),
        width: ScreenUtil().setHeight(50),
        color: Colors.orangeAccent,
      ),
    );
  }
}