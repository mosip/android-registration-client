import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

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
                      Container(
                        height: 50,
                        width: double.infinity,
                        color: Colors.blue,
                        child: Text("Header"),
                      ),
                      Container(
                        height: 100,
                        width: double.infinity,
                        color: Colors.white,
                        child: Text("Navbar"),
                      ),
                      _pages[context.watch<DashboardViewModel>().currentIndex],
                      SizedBox(
                        height: 10,
                      ),
                      Container(
                        height: 50,
                        width: double.infinity,
                        color: Colors.blue,
                        child: Text("Footer"),
                      ),
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
