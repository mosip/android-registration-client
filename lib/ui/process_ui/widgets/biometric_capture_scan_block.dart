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
  BiometricCaptureScanBlock(
      {super.key,
      required this.title,
      required this.images,
      required this.thresholdPercentage,
      required this.id});
  final String title;
  final String id;
  final List<String> images;
  final int thresholdPercentage;
  List<BiometricsDto> listOfBiomatricsDto=[];
  List<Uint8List?> listOfUint8List=[];
  

  @override
  State<BiometricCaptureScanBlock> createState() =>
      _BiometricCaptureScanBlockState();
}

class _BiometricCaptureScanBlockState extends State<BiometricCaptureScanBlock> {
  
  @override
  Widget build(BuildContext context) {
    
    listOfResultImages(List<Uint8List?> list) {
      List<Widget> temp = [];
      for (var e in list) {
        temp.add(
          Flexible(
            child: Padding(
              padding: EdgeInsets.symmetric(vertical: 48.h, horizontal: 27.h),
              child: Image.memory(
                e!,
                height: 100,
              ),
            ),
          ),
        );
      }
      return temp;
    }

    listOfImages(List<String> images) {
      List<Widget> temp = [];
      for (var e in images) {
        temp.add(
          Container(
            height: 164.h,
            width: 164.h,
            decoration: BoxDecoration(
              color: pure_white,
              border: Border.all(
                color: secondaryColors.elementAt(14),
              ),
            ),
            child: Padding(
              padding: EdgeInsets.symmetric(vertical: 48.h, horizontal: 27.h),
              child: Image.asset(
                e,
              ),
            ),
          ),
        );
      }
      return temp;
    }

    avgScore(List<BiometricsDto> list) {
      double avg = 0;
      int i;
      for (i = 0; i < list.length; i++) {
        avg = avg + list[i].qualityScore!;
      }
      avg = avg / i;
      return avg;
    }
    return Container(
      height: 547.h,
      width: 370.h,
      decoration: BoxDecoration(
          color: secondaryColors.elementAt(3),
          borderRadius: BorderRadius.circular(10),
          border: Border.all(color: secondaryColors.elementAt(13), width: 1)),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          Padding(
            padding: EdgeInsets.fromLTRB(0, 16, 0, 36),
            child: Text(
              widget.title,
              style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                  fontSize: 18, fontWeight: semiBold, color: black_shade_1),
            ),
          ),
          (widget.listOfBiomatricsDto.isEmpty)
              ? Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    ...listOfImages(widget.images).map(
                      (e) => e,
                    ),
                  ],
                )
              : Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    ...listOfResultImages(widget.listOfUint8List).map(
                      (e) => e,
                    ),
                  ],
                ),
          OutlinedButton.icon(
            onPressed: () async {
              await BiometricsApi().invokeDiscoverSbi(
                  widget.id, widget.title.split("Scan").first);
              await BiometricsApi()
                  .getBestBiometrics(
                      widget.id, widget.title.split("Scan").first)
                  .then((value) async {
                for (var e in value) {
                  widget.listOfBiomatricsDto
                      .add(BiometricsDto.fromJson(json.decode(e!)));
                }
               
              });
               await BiometricsApi().extractImageValues().then((value) {
                  widget.listOfUint8List = value;
                });
                setState(() {});
            },
            icon: Icon(
              Icons.crop_free,
              color: solid_primary,
              size: 14,
            ),
            label: Text(
              "SCAN",
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  fontSize: 14, fontWeight: bold, color: solid_primary),
            ),
            style: OutlinedButton.styleFrom(
              side: BorderSide(color: solid_primary, width: 1),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(5),
              ),
            ),
          ),
          Container(
            height: 157.h,
            width: 338.h,
            decoration: BoxDecoration(
              color: pure_white,
              border: Border.all(
                color: secondaryColors.elementAt(14),
              ),
            ),
            child: Column(
              children: [
                SizedBox(
                  height: 20.h,
                ),
                Text(
                  "Quality",
                  style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                      fontSize: 14, fontWeight: semiBold, color: black_shade_1),
                ),
                SizedBox(
                  height: 42.h,
                ),
                Text(
                  "Threshold ${widget.thresholdPercentage}%",
                  style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                        fontSize: 14,
                        fontWeight: regular,
                        color: secondaryColors.elementAt(1),
                      ),
                ),
                SizedBox(
                  height: 16.h,
                ),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    LinearPercentIndicator(
                      width: 260.h,
                      lineHeight: 8,
                      percent: (widget.listOfBiomatricsDto.isEmpty)
                          ? 0.0
                          : (avgScore(widget.listOfBiomatricsDto) / 100),
                      backgroundColor: Colors.grey,
                      progressColor: secondaryColors.elementAt(11),
                    ),
                    SizedBox(
                      width: 16.h,
                    ),
                    Text(
                      (widget.listOfBiomatricsDto.isEmpty)
                          ? "0%"
                          : "${avgScore(widget.listOfBiomatricsDto)}%",
                      style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                            fontSize: 14,
                            fontWeight: regular,
                            color: black_shade_1,
                          ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
