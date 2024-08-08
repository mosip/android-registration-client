/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/ui/dashboard/user_dashboard.dart';
import 'package:registration_client/ui/onboard/portrait/tasks_page.dart';
import 'package:registration_client/ui/onboard/widgets/bottom_navbar_widget.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

import '../../profile/profile.dart';


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
  int selectedTab = 1;

  changeTab(int index) {
    if (index == 0 || index == 1 || index == 2) {
      setState(() {
        selectedTab = index;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    List bottomNavPages = [
      const UserDashBoard(),
      // const Center(
      //   child: Text("Settings"),
      // ),
      Container(
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
      // const Center(
      //   child: Text("Notifications"),
      // ),
      const ProfilePage(),
    ];

    return SafeArea(
      child: Scaffold(
        bottomNavigationBar: _getBottomNavigationBar(),
        body: bottomNavPages[selectedTab],
      ),
    );
  }

  _getBottomNavigationBar() {
    return BottomNavigationBar(
      type: BottomNavigationBarType.fixed,
      currentIndex: selectedTab,
      onTap: (index) => changeTab(index),
      backgroundColor: Colors.white,
      items: [
        BottomNavigationBarItem(
          label: "",
          icon: BottomNavBarWidget(
            index: 0,
            selectedIndex: selectedTab,
            imagePath: dashboardIcon,
            selectedImagePath: dashboardSelectedIcon,
            title: AppLocalizations.of(context)!.dashboard,
          ),
        ),
        // BottomNavigationBarItem(
        //   label: "",
        //   icon: BottomNavBarWidget(
        //     index: 1,
        //     selectedIndex: selectedTab,
        //     imagePath: settingsIcon,
        //     selectedImagePath: settingsSelectedIcon,
        //     title: AppLocalizations.of(context)!.settings,
        //   ),
        // ),
        BottomNavigationBarItem(
          label: "",
          icon: SizedBox(
            height: 54.h,
            child: Center(
              child: Image.asset(
                appIconLogoOnly,
                fit: BoxFit.fill,
              ),
            ),
          ),
        ),
        // BottomNavigationBarItem(
        //   label: "",
        //   icon: BottomNavBarWidget(
        //     index: 3,
        //     selectedIndex: selectedTab,
        //     imagePath: notificationIcon,
        //     selectedImagePath: notificationIcon,
        //     title: AppLocalizations.of(context)!.notifications,
        //   ),
        // ),
        BottomNavigationBarItem(
          label: "",
          icon: BottomNavBarWidget(
            index: 2,
            selectedIndex: selectedTab,
            imagePath: profileIcon,
            selectedImagePath: profileSelectedIcon,
            title: AppLocalizations.of(context)!.profile,
          ),
        )
      ],

    );
  }
}
