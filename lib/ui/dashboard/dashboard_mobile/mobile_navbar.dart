import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import '../../../utils/app_config.dart';
import '../../onboarding/widgets/onboarding_page_1_navbar_icon.dart';

class MobileNavbar extends StatelessWidget {
  const MobileNavbar({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
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
    );
  }
}
