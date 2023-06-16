import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/global_provider.dart';

import '../onboard/onboard_landing_page.dart';
import '../onboard/home_page.dart';

import '../common/mobile_navbar.dart';

// TODO : Check whether it is a view then should be named as mobile_view
class DashBoardMobileView extends StatelessWidget {
  DashBoardMobileView({Key? key}) : super(key: key);

  final List<Widget> _pages = [
    const OnboardLandingPage(),
    const HomePage()
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
                      _pages[context.watch<GlobalProvider>().currentIndex],
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
