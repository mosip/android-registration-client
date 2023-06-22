import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/connectivity_provider.dart';

import 'package:registration_client/provider/global_provider.dart';

import '../../utils/app_config.dart';

class TabletHeader extends StatelessWidget {
  const TabletHeader({super.key});

  @override
  Widget build(BuildContext context) {
    double w = ScreenUtil().screenWidth;
    bool isLandscape =
        MediaQuery.of(context).orientation == Orientation.landscape;

    return Container(
      decoration: const BoxDecoration(
        border: Border(
          bottom: BorderSide(
            color: Color(0xffE5EBFA),
          ),
        ),
        color: Color(0xffFAFBFF),
      ),
      width: w,
      height: 50.h,
      child: Padding(
        padding: EdgeInsets.fromLTRB(17.w, 0, 16.w, 0),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.end,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            SizedBox(
              width: w < 512 ? 0 : 60,
            ),
            Icon(
              Icons.location_pin,
              color: solid_primary,
              size: 16,
            ),
            SizedBox(
              width: (isLandscape) ? 7.85.w : 1.96.w,
            ),
            Text(
              context.watch<GlobalProvider>().centerName,
              style: Theme.of(context).textTheme.titleSmall?.copyWith(
                  color: const Color(0xff333333), fontWeight: semiBold),
            ),
            SizedBox(
              width: (isLandscape) ? 20.w : 8.w,
            ),
            Icon(
              Icons.circle,
              color: context.watch<ConnectivityProvider>().isConnected
                  ? const Color(0xff1A9B42)
                  : Colors.grey,
              size: 12,
            ),
            SizedBox(
              width: (isLandscape) ? 10.w : 2.5.w,
            ),
            Icon(
              Icons.desktop_mac_outlined,
              color: solid_primary,
              size: 16,
            ),
            SizedBox(
              width: (isLandscape) ? 7.w : 1.75.w,
            ),
            Text(
              context.watch<GlobalProvider>().machineName.toUpperCase(),
              style: Theme.of(context).textTheme.titleSmall?.copyWith(
                  color: const Color(0xff333333), fontWeight: semiBold),
            ),
            SizedBox(
              width: w < 512 ? 0 : 60,
            ),
          ],
        ),
      ),
    );
  }
}
