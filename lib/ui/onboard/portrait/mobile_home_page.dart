import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/ui/onboard/portrait/registration_tasks.dart';
import 'package:registration_client/ui/onboard/widgets/onboard_landing_page_navbar_icon.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/app_style.dart';

class MobileHomePage extends StatefulWidget {
  const MobileHomePage({super.key});

  @override
  State<MobileHomePage> createState() => _MobileHomePageState();
}

class _MobileHomePageState extends State<MobileHomePage> {
  int currentIndex = 0;
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
              horizontal: 20.w,
              vertical: 14.h,
            ),
            margin: EdgeInsets.only(
              top: 14.h,
            ),
            color: AppStyle.appBlueShade,
            child: Column(
              children: [
                Row(
                  children: [
                    Expanded(
                      child: InkWell(
                        onTap: () {
                          setState(() {
                            currentIndex = 0;
                          });
                        },
                        child: Container(
                          height: 84.h,
                          decoration: BoxDecoration(
                            border: Border.all(
                              color: currentIndex == 0
                                  ? AppStyle.appSolidPrimary
                                  : AppStyle.greyBorderShade,
                            ),
                            borderRadius: const BorderRadius.only(
                              topLeft: Radius.circular(6),
                              topRight: Radius.circular(6),
                            ),
                            color: currentIndex == 0
                                ? AppStyle.appSolidPrimary
                                : AppStyle.appWhite,
                          ),
                          child: Center(
                            child: Text(
                              'Registration Tasks',
                              style: TextStyle(
                                fontSize: 24,
                                fontWeight: semiBold,
                                color: currentIndex == 0
                                    ? AppStyle.appWhite
                                    : AppStyle.appBlack,
                              ),
                            ),
                          ),
                        ),
                      ),
                    ),
                    Expanded(
                      child: InkWell(
                        onTap: () {
                          setState(() {
                            currentIndex = 1;
                          });
                        },
                        child: Container(
                          height: 84.h,
                          decoration: BoxDecoration(
                            border: Border.all(
                              color: currentIndex == 1
                                  ? AppStyle.appSolidPrimary
                                  : AppStyle.greyBorderShade,
                            ),
                            borderRadius: const BorderRadius.only(
                              topLeft: Radius.circular(6),
                              topRight: Radius.circular(6),
                            ),
                            color: currentIndex == 1
                                ? AppStyle.appSolidPrimary
                                : AppStyle.appWhite,
                          ),
                          child: Center(
                            child: Text(
                              'Operational Tasks',
                              style: TextStyle(
                                fontSize: 24,
                                fontWeight: semiBold,
                                color: currentIndex == 1
                                    ? AppStyle.appWhite
                                    : AppStyle.appBlack,
                              ),
                            ),
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
                Container(
                  color: AppStyle.appSolidPrimary,
                  height: 2.5.h,
                ),
                SizedBox(
                  height: 26.h,
                ),
                const RegistrationTasks(),
              ],
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
