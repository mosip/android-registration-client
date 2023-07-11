import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/ui/common/tablet_footer.dart';
import 'package:registration_client/ui/common/tablet_header.dart';
import 'package:registration_client/ui/common/tablet_navbar.dart';
import 'package:registration_client/ui/process_ui/new_process.dart';

import '../onboard/onboard_landing_page.dart';
import '../onboard/home_page.dart';

class DashBoardTabletView extends StatelessWidget {
  DashBoardTabletView({Key? key}) : super(key: key);

  final List<Widget> _pages = [
    const OnboardLandingPage(),
    const HomePage(),
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
                      _pages[context.watch<GlobalProvider>().currentIndex],
                      const SizedBox(
                        height: 10,
                      ),
                      context.watch<GlobalProvider>().currentIndex != 0
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
