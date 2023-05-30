import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:responsive_grid_list/responsive_grid_list.dart';

import 'widgets/onboarding_page2_card.dart';

class OnboardingPage2View extends StatelessWidget {
  static const route="/onboarding-page2-view";
  const OnboardingPage2View({super.key});

  @override
  Widget build(BuildContext context) {
    double w = ScreenUtil().screenWidth;

    List<Map<String, dynamic>> registrationTask = [
      {
        "icon": "assets/svg/Onboarding Yourself.svg",
        "title": "New Registration",
        "onTap": () {},
      },
      {
        "icon": "assets/svg/Onboarding Yourself.svg",
        "title": "Lost UIN",
        "onTap": () {},
      },
      {
        "icon": "assets/svg/Onboarding Yourself.svg",
        "title": "Update UIN",
        "onTap": () {},
      },
      {
        "icon": "assets/svg/Onboarding Yourself.svg",
        "title": "Biometrics Correction",
        "onTap": () {},
      },
    ];

    List<Map<String, dynamic>> operationalTasks = [
      {
        "icon": "assets/svg/Synchronising Data.svg",
        "title": "Sync Data",
        "onTap": () {},
      },
      {
        "icon": "assets/svg/Uploading Local - Registration Data.svg",
        "title": "Download Pre-Registration Data",
        "onTap": () {},
      },
      {
        "icon": "assets/svg/Updating Operator Biometrics.svg",
        "title": "Update Operator Biometrics",
        "onTap": () {},
      },
      {
        "icon": "assets/svg/Uploading Local - Registration Data.svg",
        "title": "Application Upload",
        "onTap": () {},
      },
      {
        "icon": "assets/svg/Onboarding Yourself.svg",
        "title": "Pending Approval",
        "onTap": () {},
      },
      {
        "icon": "assets/svg/Uploading Local - Registration Data.svg",
        "title": "Check Update",
        "onTap": () {},
      },
      {
        "icon": "assets/svg/Uploading Local - Registration Data.svg",
        "title": "Center Remap Sync.",
        "onTap": () {},
      },
    ];

    return AnnotatedRegion<SystemUiOverlayStyle>(
      value: const SystemUiOverlayStyle(
        statusBarColor: Colors.transparent,
      ),
      child: Column(
        children: [
          Container(
            padding: const EdgeInsets.all(20),
            decoration: const BoxDecoration(
              gradient: LinearGradient(
                begin: Alignment.topCenter,
                end: Alignment.bottomCenter,
                colors: [Color(0xff214FBF), Color(0xff1C43A1)],
              ),
            ),
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
                        "Registration Tasks",
                        style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                            color: Colors.white,
                            fontWeight: semiBold,
                            fontSize: 18),
                      ),
                      const SizedBox(
                        height: 20,
                      ),
                      ResponsiveGridList(
                        shrinkWrap: true,
                        minItemWidth: 300,
                        horizontalGridSpacing: 8,
                        verticalGridSpacing: 8,
                        children: List.generate(
                          registrationTask.length,
                          (index) => Onboarding_Page2_Card(
                            icon: registrationTask[index]["icon"] as String,
                            title: registrationTask[index]["title"] as String,
                            ontap: registrationTask[index]["onTap"]
                                as VoidCallback,
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
          ),
          Padding(
            padding: const EdgeInsets.all(20),
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
                        "Operational Tasks",
                        style: Theme.of(context)
                            .textTheme
                            .bodyLarge
                            ?.copyWith(fontWeight: semiBold),
                      ),
                      const SizedBox(
                        height: 20,
                      ),
                      // Column(
                      //   children: [
                      //     Onboarding_Page2_Card(
                      //       icon: "assets/svg/Synchronising Data.svg",
                      //       title: "Synchronize Data",
                      //       ontap: () {},
                      //     ),
                      //     Onboarding_Page2_Card(
                      //       icon:
                      //           "assets/svg/Uploading Local - Registration Data.svg",
                      //       title: "Download Pre-Registration Data",
                      //       ontap: () {},
                      //     ),
                      //     Onboarding_Page2_Card(
                      //       icon: "assets/svg/Updating Operator Biometrics.svg",
                      //       title: "Update Operator Biometrics",
                      //       ontap: () {},
                      //     ),
                      //     Onboarding_Page2_Card(
                      //       icon:
                      //           "assets/svg/Uploading Local - Registration Data.svg",
                      //       title: "Application Upload",
                      //       ontap: () {},
                      //     ),
                      //     Onboarding_Page2_Card(
                      //       icon: "assets/svg/Onboarding Yourself.svg",
                      //       title: "Pending Approval",
                      //       ontap: () {},
                      //     ),
                      //     Onboarding_Page2_Card(
                      //       icon:
                      //           "assets/svg/Uploading Local - Registration Data.svg",
                      //       title: "Check Updates",
                      //       ontap: () {},
                      //     ),
                      //     Onboarding_Page2_Card(
                      //       icon:
                      //           "assets/svg/Uploading Local - Registration Data.svg",
                      //       title: "Center Remap Sync.",
                      //       ontap: () {},
                      //     ),
                      //   ],
                      // ),

                      ResponsiveGridList(
                        shrinkWrap: true,
                        minItemWidth: 300,
                        horizontalGridSpacing: 12,
                        verticalGridSpacing: 12,
                        children: List.generate(
                          operationalTasks.length,
                          (index) => Onboarding_Page2_Card(
                            icon: operationalTasks[index]["icon"] as String,
                            title: operationalTasks[index]["title"] as String,
                            ontap: operationalTasks[index]["onTap"]
                                as VoidCallback,
                          ),
                        ),
                      ),

                      SizedBox(
                        height: 4.h,
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
