/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

// ignore_for_file: use_build_context_synchronously, deprecated_member_use

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';

import 'package:registration_client/ui/onboard/widgets/onboard_landing_page_card.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:responsive_grid_list/responsive_grid_list.dart';
import 'package:url_launcher/url_launcher.dart';

class OnboardLandingPage extends StatelessWidget {
  static const route = "/onboard-landing-page";
  static const platform =
      MethodChannel('com.flutter.dev/io.mosip.get-package-instance');
  const OnboardLandingPage({super.key});

  goToUrl(String url) async {
    if (await canLaunchUrl(Uri.parse(url))) {
      await launchUrl(Uri.parse(url));
    } else {
      throw 'Could not launch $url';
    }
  }

  _getStringValueGlobalParamAction(BuildContext context, String key) async {
    await context
        .read<RegistrationTaskProvider>()
        .getStringValueGlobalParam(key);
    String res =
        context.read<RegistrationTaskProvider>().stringValueGlobalParam;
    await goToUrl(res);
  }

  @override
  Widget build(BuildContext context) {
    bool isLandscape =
        MediaQuery.of(context).orientation == Orientation.landscape;
    List<Map<String, dynamic>> helpTopics = [
      {
        "icon": "assets/images/Onboarding Yourself.png",
        "title": "Onboarding Yourself",
        "onTap": () {
          _getStringValueGlobalParamAction(
              context, "mosip.registration.onboard_yourself_url");
        },
      },
      {
        "icon": "assets/images/Onboarding Yourself.png",
        "title": "Registering an Individual",
        "onTap": () async {
          _getStringValueGlobalParamAction(
              context, "mosip.registration.registering_individual_url");
        },
      },
      {
        "icon": "assets/images/Synchronising Data.png",
        "title": "Synchronising Data",
        "onTap": () async {
          _getStringValueGlobalParamAction(
              context, "mosip.registration.sync_data_url");
        },
      },
      {
        "icon": "assets/images/fingerprint_icon.png",
        "title": "Mapping Devices to Machine",
        "onTap": () async {
          _getStringValueGlobalParamAction(
              context, "mosip.registration.mapping_devices_url");
        },
      },
      {
        "icon": "assets/images/Uploading Local - Registration Data.png",
        "title": "Uploading Local/Registration Data",
        "onTap": () async {
          _getStringValueGlobalParamAction(
              context, "mosip.registration.uploading_data_url");
        },
      },
      {
        "icon": "assets/images/fingerprint_icon.png",
        "title": "Updating Operator Biometrics",
        "onTap": () async {
          _getStringValueGlobalParamAction(
              context, "mosip.registration.updating_biometrics_url");
        },
      },
    ];

    double w = ScreenUtil().screenWidth;
    return AnnotatedRegion<SystemUiOverlayStyle>(
      value: const SystemUiOverlayStyle(
        statusBarColor: Colors.transparent,
      ),
      child: Column(
        children: [
          Container(
            height: 292.h,
            width: MediaQuery.of(context).size.width,
            decoration: const BoxDecoration(
              gradient: LinearGradient(
                begin: Alignment.topCenter,
                end: Alignment.bottomCenter,
                colors: [Color(0xff214FBF), Color(0xff1C43A1)],
              ),
            ),
            child: Padding(
              padding: (isLandscape)
                  ? EdgeInsets.fromLTRB(30.w, 0, 0, 0)
                  : EdgeInsets.fromLTRB(10.w, 0, 0, 0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Padding(
                    padding: EdgeInsets.fromLTRB(16.w, 80.h, 0, 0),
                    child: RichText(
                        text: TextSpan(
                            text: "Hello ",
                            style: Theme.of(context)
                                .textTheme
                                .titleLarge
                                ?.copyWith(
                                    color: pureWhite, fontWeight: regular),
                            children: [
                          TextSpan(
                            text: context
                                    .watch<GlobalProvider>()
                                    .name[0]
                                    .toUpperCase() +
                                context
                                    .watch<GlobalProvider>()
                                    .name
                                    .substring(1)
                                    .toLowerCase(),
                            style: Theme.of(context)
                                .textTheme
                                .titleLarge
                                ?.copyWith(color: pureWhite, fontWeight: bold),
                          )
                        ])),
                  ),
                  Padding(
                    padding: EdgeInsets.fromLTRB(16.w, 8.h, 0, 0),
                    child: Text(
                      "Please tap the \"Get Onboard\" button to onboard yourself into the portal.",
                      style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                          color: const Color(0xffFFFFFF).withOpacity(0.6),
                          fontSize: 18,
                          fontWeight: regular),
                    ),
                  ),
                  Padding(
                    padding: EdgeInsets.fromLTRB(16.w, 31.h, 0, 0),
                    child: ElevatedButton(
                      onPressed: () {
                        debugPrint(MediaQuery.of(context).size.width.toString());
                        context.read<GlobalProvider>().setCurrentIndex(1);
                      },
                      style: ButtonStyle(
                          backgroundColor:
                              MaterialStateProperty.all<Color>(pureWhite)),
                      child: Padding(
                        padding: EdgeInsets.symmetric(
                            vertical: 18.h, horizontal: 44.w),
                        child: Text(
                          "Get Onboard",
                          style: Theme.of(context)
                              .textTheme
                              .bodyLarge
                              ?.copyWith(color: secondaryColors.elementAt(0)),
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
          Padding(
            padding: EdgeInsets.fromLTRB(17.w, 30.h, 16.w, 0),
            child: Row(
              children: [
                SizedBox(
                  width: w < 512 ? 0 : 60,
                ),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        "Help topics",
                        style: Theme.of(context)
                            .textTheme
                            .bodyLarge
                            ?.copyWith(fontWeight: semiBold),
                      ),
                      SizedBox(
                        height: 4.h,
                      ),
                      ResponsiveGridList(
                        shrinkWrap: true,
                        minItemWidth: 300,
                        minItemsPerRow: 1,
                        maxItemsPerRow: 3,
                        horizontalGridSpacing: 8,
                        verticalGridSpacing: 8,
                        children: List.generate(
                          helpTopics.length,
                          (index) => OnboardLandingPageCard(
                            icon: helpTopics[index]["icon"] as String,
                            title: helpTopics[index]["title"] as String,
                            ontap: helpTopics[index]["onTap"],
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
                SizedBox(
                  width: w < 512 ? 0 : 60,
                ),
              ],
            ),
          )
        ],
      ),
    );
  }
}
