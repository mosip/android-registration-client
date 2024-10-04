/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/ui/onboard/onboarding_page.dart';

import '../onboard/home_page.dart';

class DashBoardTabletView extends StatelessWidget {
  DashBoardTabletView({Key? key}) : super(key: key);

  final List<Widget> _pages = [
    const OnboardingPage(),
    const HomePage(),
  ];

  @override
  Widget build(BuildContext context) {
    return _pages[context.watch<GlobalProvider>().currentIndex];
  }
}
