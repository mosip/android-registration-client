import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/ui/dashboard/dashboard_tablet/tablet_footer.dart';
import 'package:registration_client/ui/dashboard/dashboard_tablet/tablet_header.dart';
import 'package:registration_client/ui/dashboard/dashboard_tablet/tablet_navbar.dart';

import '../../onboarding/onboarding_page_1_view.dart';
import '../../onboarding/onboarding_page_2_view.dart';
import '../dashboard_view_model.dart';

class DashBoardTabletView extends StatelessWidget {
  DashBoardTabletView({Key? key}) : super(key: key);

  final List<Widget> _pages = [
    const OnboardingPage1View(),
    const OnboardingPage2View()
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: Row(
        children: [
          Expanded(
            child: LayoutBuilder(
              builder: (context, constraint) {
                return SingleChildScrollView(
                  controller: ScrollController(),
                  child: Column(
                    children: [
                      const TabletHeader(),
                      const TabletNavbar(),
                      _pages[context.watch<DashboardViewModel>().currentIndex],
                      const SizedBox(
                        height: 10,
                      ),
                      context.watch<DashboardViewModel>().currentIndex != 0
                          ? const TabletFooter()
                          : const SizedBox.shrink(),
                    ],
                  ),
                );
              },
            ),
          )
        ],
      ),
    );
  }
}
