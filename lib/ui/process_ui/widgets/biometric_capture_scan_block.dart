/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';

import 'package:registration_client/utils/app_config.dart';

class BiometricCaptureScanBlock extends StatelessWidget {
  const BiometricCaptureScanBlock({
    super.key,
    required this.title,
    required this.middleBlock,
  });
  final String title;
  final Widget middleBlock;
  // final String id;
  // final List<String> images;
  // final int thresholdPercentage;
  // List<BiometricsDto> listOfBiomatricsDto=[];
  // List<Uint8List?> listOfUint8List=[];

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 547.h,
      width: 370.h,
      decoration: BoxDecoration(
          color: secondaryColors.elementAt(3),
          borderRadius: BorderRadius.circular(10),
          border: Border.all(color: secondaryColors.elementAt(13), width: 1)),
      child: Column(
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(0, 16, 0, 36),
            child: Text(
              title,
              style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                  fontSize: 18, fontWeight: semiBold, color: blackShade1),
            ),
          ),
          middleBlock,
        ],
      ),
    );
  }
}
