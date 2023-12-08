import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/ui/onboard/portrait/tasks_page.dart';
import 'package:registration_client/ui/onboard/widgets/onboard_landing_page_navbar_icon.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/app_style.dart';

class MobileHomePage extends StatefulWidget {
  const MobileHomePage({
    super.key,
    required this.operationalTasks,
    required this.getProcessUI,
  });
  final List<Map<String, dynamic>> operationalTasks;
  final Function getProcessUI;

  @override
  State<MobileHomePage> createState() => _MobileHomePageState();
}

class _MobileHomePageState extends State<MobileHomePage> {
  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        bottomNavigationBar: _getBottomNavigationBar(),
        body: SingleChildScrollView(
          child: Container(
            height: ScreenUtil().screenHeight,
            width: ScreenUtil().screenWidth,
            padding: EdgeInsets.symmetric(
              // horizontal: 20.w,
              vertical: 14.h,
            ),
            margin: EdgeInsets.only(
              top: 14.h,
            ),
            color: AppStyle.appBlueShade,
            child: TasksPage(
              operationalTasks: widget.operationalTasks,
              getProcessUI: (BuildContext context, Process process) {
                widget.getProcessUI(context, process);
              },
            ),
          ),
        ),
      ),
    );
  }

  _getBottomNavigationBar() {
    return Container(
        height: 94.h,
        padding: EdgeInsets.symmetric(
          vertical: 15.h,
        ),
        decoration: const BoxDecoration(
          color: AppStyle.appWhite,
          boxShadow: [
            BoxShadow(
              color: AppStyle.greyBorderShade,
              offset: Offset(0.0, -3.0),
              blurRadius: 6.0,
              spreadRadius: 0.0,
            ),
          ],
        ),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Expanded(
              child: OnboardLandingPageNavbarIcon(
                imagePath: dashboardIcon,
                icon: Icon(
                  Icons.home,
                  color: solidPrimary,
                ),
                title: Text(
                  "Dashboard",
                  style: Theme.of(context)
                      .textTheme
                      .bodyMedium
                      ?.copyWith(color: solidPrimary),
                ),
                ontap: () {},
              ),
            ),
            Expanded(
              child: OnboardLandingPageNavbarIcon(
                imagePath: settingsIcon,
                icon: Icon(
                  Icons.settings,
                  color: secondaryColors.elementAt(5),
                ),
                title: Text(
                  "Settings",
                  style: Theme.of(context)
                      .textTheme
                      .bodyMedium
                      ?.copyWith(color: secondaryColors.elementAt(5)),
                ),
                ontap: () {},
              ),
            ),
            Expanded(
              child: InkWell(
                onTap: () {},
                child: SizedBox(
                  height: 54.h,
                  child: Center(
                    child: Image.asset(
                      appIconLogoOnly,
                      fit: BoxFit.fill,
                    ),
                  ),
                ),
              ),
            ),
            Expanded(
              child: OnboardLandingPageNavbarIcon(
                imagePath: notificationIcon,
                icon: Icon(
                  Icons.notifications,
                  color: secondaryColors.elementAt(5),
                ),
                title: Text(
                  "Notifications",
                  style: Theme.of(context)
                      .textTheme
                      .bodyMedium
                      ?.copyWith(color: secondaryColors.elementAt(5)),
                ),
                ontap: () {},
              ),
            ),
            Expanded(
              child: OnboardLandingPageNavbarIcon(
                imagePath: dashboardIcon,
                icon: Icon(
                  Icons.home,
                  color: secondaryColors.elementAt(5),
                ),
                title: Text(
                  "Account",
                  style: Theme.of(context)
                      .textTheme
                      .bodyMedium
                      ?.copyWith(color: secondaryColors.elementAt(5)),
                ),
                ontap: () {},
              ),
            )
          ],
        ));
  }
}
