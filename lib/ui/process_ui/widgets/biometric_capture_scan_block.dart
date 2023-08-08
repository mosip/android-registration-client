import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter/src/widgets/framework.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:percent_indicator/percent_indicator.dart';
import 'package:registration_client/model/biometrics_dto.dart';
import 'package:registration_client/pigeon/biometrics_pigeon.dart';
import 'package:registration_client/utils/app_config.dart';

class BiometricCaptureScanBlock extends StatefulWidget {
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
  State<BiometricCaptureScanBlock> createState() =>
      _BiometricCaptureScanBlockState();
}

class _BiometricCaptureScanBlockState extends State<BiometricCaptureScanBlock> {
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
            padding: EdgeInsets.fromLTRB(0, 16, 0, 36),
            child: Text(
              widget.title,
              style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                  fontSize: 18, fontWeight: semiBold, color: black_shade_1),
            ),
          ),
          
          widget.middleBlock,
        ],
      ),
    );
  }
}
