import 'package:flutter/material.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/global_provider.dart';


import '../../utils/app_config.dart';

class TabletNavbar extends StatelessWidget {
  const TabletNavbar({super.key});

  @override
  Widget build(BuildContext context) {
    double w = ScreenUtil().screenWidth;
    bool isLandscape =
        MediaQuery.of(context).orientation == Orientation.landscape;
    return Column(
      children: [
        SizedBox(
          height: 84.h,
          width: w,
          child: Row(
            children: [
              SizedBox(
                width: w < 512 ? 0 : 60,
              ),
              Image.asset(
                "assets/images/mosip_logo.png",
                height: 54.h,
              ),
              const Spacer(),
              context.watch<GlobalProvider>().currentIndex != 0
                  ? Icon(
                      Icons.menu,
                      color: const Color(0xff4E4E4E),
                      size: 26.h,
                    )
                  : const SizedBox.shrink(),
              SizedBox(
                width: (isLandscape) ? 30.w : 12.07.w,
              ),
              Icon(
                Icons.settings,
                color: const Color(0xff4E4E4E),
                size: 26.h,
              ),
              SizedBox(
                width: (isLandscape) ? 30.w : 12.07.w,
              ),
              Icon(
                Icons.notifications_outlined,
                color: const Color(0xff4E4E4E),
                size: 25.5.h,
              ),
              SizedBox(
                width: (isLandscape) ? 30.w : 12.07.w,
              ),
              // SizedBox(
              //   width: (isLandscape) ? 54.w : 13.5.w,
              // ),
              ClipRRect(
                borderRadius: BorderRadius.circular(100),
                child: Image.asset(
                  "assets/images/user_profile@2x.png",
                  height: 50.h,
                  width: 50.h,
                ),
              ),
              SizedBox(
                width: (isLandscape) ? 7.w : 1.75.w,
              ),
              Text(
                context.watch<GlobalProvider>().name[0].toUpperCase() +
                    context
                        .watch<GlobalProvider>()
                        .name
                        .substring(1)
                        .toLowerCase(),
                style: Theme.of(context).textTheme.titleMedium?.copyWith(
                    color: Colors.black, fontSize: 16, fontWeight: semiBold),
              ),
              SizedBox(
                width: (isLandscape) ? 5.w : 3.w,
              ),
              Icon(
                Icons.arrow_drop_down,
                color: const Color(0xff4D4C4C),
                size: 15.h,
              ),
              SizedBox(
                width: w < 512 ? 0 : 60,
              ),
            ],
          ),
        )
      ],
    );
  }
}
