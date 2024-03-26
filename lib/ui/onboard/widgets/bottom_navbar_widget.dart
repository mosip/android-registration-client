/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';

import 'package:flutter_svg/flutter_svg.dart';

import '../../../utils/app_config.dart';

class BottomNavBarWidget extends StatelessWidget {
  const BottomNavBarWidget({
    super.key,
    required this.title,
    required this.imagePath,
    required this.selectedImagePath,
    required this.index,
    required this.selectedIndex,
  });
  final String title;
  final String imagePath;
  final String selectedImagePath;
  final int index;
  final int selectedIndex;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: selectedIndex == index ? bottomBarSelectedColor : Colors.white,
        borderRadius: BorderRadius.circular(10),
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          selectedIndex == index ? SvgPicture.asset(selectedImagePath) : SvgPicture.asset(imagePath),
          const SizedBox(height: 5),
          Text(
            title,
            style: TextStyle(
              fontSize: isMobileSize ? 10 : 18,
              fontWeight: semiBold,
              color: selectedIndex == index ? solidPrimary : secondaryColors.elementAt(5),
            ),
          ),
        ],
      ),
    );
  }
}
