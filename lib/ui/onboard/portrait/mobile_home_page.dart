import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/ui/onboard/portrait/tasks_page.dart';
import 'package:registration_client/ui/onboard/widgets/onboard_landing_page_navbar_icon.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class MobileHomePage extends StatefulWidget {
  const MobileHomePage({
    super.key,
    required this.operationalTasks,
    required this.getProcessUI,
    required this.syncData,
  });
  final List<Map<String, dynamic>> operationalTasks;
  final Function getProcessUI;
  final Function syncData;

  @override
  State<MobileHomePage> createState() => _MobileHomePageState();
}

class _MobileHomePageState extends State<MobileHomePage> {
  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        bottomNavigationBar: _getBottomNavigationBar(),
        body: Container(
          height: ScreenUtil().screenHeight,
          width: ScreenUtil().screenWidth,
          padding: EdgeInsets.symmetric(
            // horizontal: 20.w,
            vertical: 14.h,
          ),
          margin: EdgeInsets.only(
            top: 14.h,
          ),
          color: appBlueShade,
          child: TasksPage(
            operationalTasks: widget.operationalTasks,
            getProcessUI: (BuildContext context, Process process) {
              widget.getProcessUI(context, process);
            },
            syncData: (BuildContext context) {
              widget.syncData(context);
            },
          ),
        ),
      ),
    );
  }

  _getBottomNavigationBar() {
    return Container(
        height: isMobileSize ? 84.h : 94.h,
        padding: EdgeInsets.symmetric(
          vertical: 17.h,
          horizontal: 20.w
        ),
        decoration: const BoxDecoration(
          color: appWhite,
          boxShadow: [
            BoxShadow(
              color: greyBorderShade,
              offset: Offset(0.0, -3.0),
              blurRadius: 6.0,
              spreadRadius: 0.0,
            ),
          ],
        ),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            OnboardLandingPageNavbarIcon(
              imagePath: dashboardIcon,
              icon: Icon(
                Icons.home,
                color: solidPrimary,
              ),
              title: Text(
                AppLocalizations.of(context)!.dashboard,
                style: TextStyle(
                  fontSize: isMobileSize ? 10 : 14,
                  fontWeight: semiBold,
                  color: secondaryColors.elementAt(5),
                ),
              ),
              ontap: () {},
            ),
            OnboardLandingPageNavbarIcon(
              imagePath: settingsIcon,
              icon: Icon(
                Icons.settings,
                color: secondaryColors.elementAt(5),
              ),
              title: Text(
                AppLocalizations.of(context)!.settings,
                style: TextStyle(
                  fontSize: isMobileSize ? 10 : 14,
                  fontWeight: semiBold,
                  color: secondaryColors.elementAt(5),
                ),
              ),
              ontap: () {},
            ),
            InkWell(
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
            OnboardLandingPageNavbarIcon(
              imagePath: notificationIcon,
              icon: Icon(
                Icons.notifications,
                color: secondaryColors.elementAt(5),
              ),
              title: Text(
                AppLocalizations.of(context)!.notifications,
                style: TextStyle(
                  fontSize: isMobileSize ? 10 : 14,
                  fontWeight: semiBold,
                  color: secondaryColors.elementAt(5),
                ),
              ),
              ontap: () {},
            ),
            OnboardLandingPageNavbarIcon(
              imagePath: dashboardIcon,
              icon: Icon(
                Icons.home,
                color: secondaryColors.elementAt(5),
              ),
              title: Text(
                AppLocalizations.of(context)!.profile,
                style: TextStyle(
                  fontSize: isMobileSize ? 10 : 14,
                  fontWeight: semiBold,
                  color: secondaryColors.elementAt(5),
                ),
              ),
              ontap: () {},
            )
          ],
        ));
  }
}
