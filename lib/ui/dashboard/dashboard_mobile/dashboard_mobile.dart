import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';

import '../../../utils/app_config.dart';
import '../../onboarding/onboarding_page_1_view.dart';
import '../../onboarding/onboarding_page_2_view.dart';
import '../../onboarding/widgets/onboarding_page_1_navbar_icon.dart';
import '../dashboard_view_model.dart';

class DashBoardMobileView extends StatelessWidget {
  DashBoardMobileView({Key? key}) : super(key: key);

  final List<Widget> _pages = [
    const OnboardingPage1View(),
    const OnboardingPage2View()
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      bottomNavigationBar: Container(
        height: 80.h,
        color: pure_white,
        child: Padding(
          padding: EdgeInsets.fromLTRB(17.w, 12.h, 30.w, 17.h),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              OnboardingPage1NavbarIcon(
                  icon: Icon(
                    Icons.home,
                    color: solid_primary,
                  ),
                  title: Text(
                    "Home",
                    style: Theme.of(context)
                        .textTheme
                        .bodyMedium
                        ?.copyWith(color: solid_primary),
                  ),
                  ontap: () {}),
              OnboardingPage1NavbarIcon(
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
                  ontap: () {}),
              OnboardingPage1NavbarIcon(
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
                  ontap: () {}),
              OnboardingPage1NavbarIcon(
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
                  ontap: () {})
            ],
          ),
        ),
      ),
      body: Row(
        children: [
          Expanded(
            child: LayoutBuilder(
              builder: (context, constraint) {
                return SingleChildScrollView(
                  controller: ScrollController(),
                  child:
                      _pages[context.watch<DashboardViewModel>().currentIndex],
                );
              },
            ),
          )
        ],
      ),
    );
  }
}
