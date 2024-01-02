import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import '../../utils/app_config.dart';
import '../onboard/widgets/onboard_landing_page_navbar_icon.dart';

class MobileNavbar extends StatelessWidget {
  const MobileNavbar({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 80.h,
      color: pureWhite,
      child: Padding(
        padding: EdgeInsets.fromLTRB(17.w, 12.h, 30.w, 17.h),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            OnboardLandingPageNavbarIcon(
              imagePath: "",
                icon: Icon(
                  Icons.home,
                  color: solidPrimary,
                ),
                title: Text(
                  "Home",
                  style: Theme.of(context)
                      .textTheme
                      .bodyMedium
                      ?.copyWith(color: solidPrimary),
                ),
                ontap: () {}),
            OnboardLandingPageNavbarIcon(
              imagePath: "",
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
            OnboardLandingPageNavbarIcon(
              imagePath: "",
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
            OnboardLandingPageNavbarIcon(
              imagePath: "",
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
