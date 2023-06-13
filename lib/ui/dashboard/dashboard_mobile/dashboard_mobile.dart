import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../onboarding/get_onboard_landing_page.dart';
import '../../onboarding/home_page.dart';
import '../../../provider/dashboard_view_model.dart';
import 'mobile_navbar.dart';

class DashBoardMobileView extends StatelessWidget {
  DashBoardMobileView({Key? key}) : super(key: key);

  final List<Widget> _pages = [
    const GetOnboardLandingPageView(),
    const HomePageView()
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      bottomNavigationBar: const MobileNavbar(),
      body: Row(
        children: [
          Expanded(
            child: LayoutBuilder(
              builder: (context, constraint) {
                return SingleChildScrollView(
                  controller: ScrollController(),
                  child: Column(
                    children: [
                      _pages[context.watch<DashboardViewModel>().currentIndex],
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
