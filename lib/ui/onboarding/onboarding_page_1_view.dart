import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/dashboard_view_model.dart';
import 'package:registration_client/ui/onboarding/widgets/onboarding_page_1_card.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:responsive_grid_list/responsive_grid_list.dart';
import 'package:url_launcher/url_launcher.dart';

import '../../utils/responsive.dart';

class OnboardingPage1View extends StatelessWidget {
  static const route = "/onboarding-page1-view";
  const OnboardingPage1View({super.key});
  goToUrl(String url) async {
    if (await canLaunchUrl(Uri.parse(url))) {
      await launchUrl(Uri.parse(url));
    } else {
      throw 'Could not launch $url';
    }
  }

  @override
  Widget build(BuildContext context) {
    bool isLandscape =
        MediaQuery.of(context).orientation == Orientation.landscape;
    List<Map<String, dynamic>> helpTopics = [
      {
        "icon": "assets/images/Onboarding Yourself.png",
        "title": "Onboarding Yourself",
        "onTap": () async {
          goToUrl("https://docs.mosip.io/1.2.0/modules/registration-client/operator-onboarding");
        },
      },
      {
        "icon": "assets/images/Onboarding Yourself.png",
        "title": "Registering an Individual",
        "onTap": () async {
          goToUrl("https://docs.mosip.io/1.2.0/modules/registration-client/registration-client-home-page");
        },
      },
      {
        "icon": "assets/images/Synchronising Data.png",
        "title": "Synchronising Data",
        "onTap": () async {
          goToUrl("https://docs.mosip.io/1.2.0/modules/registration-client/registration-client-home-page#synchronize-data");
        },
      },
      {
        "icon": "assets/images/fingerprint_icon.png",
        "title": "Mapping Devices to Machine",
        "onTap": () async {
          goToUrl("https://docs.mosip.io/1.2.0/modules/registration-client/registration-client-home-page#application-upload");
        },
      },
      {
        "icon": "assets/images/Uploading Local - Registration Data.png",
        "title": "Uploading Local/Registration Data",
        "onTap": () async {
          goToUrl("https://docs.mosip.io/1.2.0/modules/registration-client/registration-client-home-page#application-upload");
        },
      },
      {
        "icon": "assets/images/fingerprint_icon.png",
        "title": "Updating Operator Biometrics",
        "onTap": () async {
          goToUrl("https://docs.mosip.io/1.2.0/modules/registration-client/registration-client-home-page#update-operator-biometrics");
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
                                    color: pure_white, fontWeight: regular),
                            children: [
                          TextSpan(
                            text: context
                                    .watch<DashboardViewModel>()
                                    .name[0]
                                    .toUpperCase() +
                                context
                                    .watch<DashboardViewModel>()
                                    .name
                                    .substring(1)
                                    .toLowerCase(),
                            style: Theme.of(context)
                                .textTheme
                                .titleLarge
                                ?.copyWith(color: pure_white, fontWeight: bold),
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
                        print(MediaQuery.of(context).size.width);
                        context.read<DashboardViewModel>().setCurrentIndex(1);
                      },
                      style: ButtonStyle(
                          backgroundColor:
                              MaterialStateProperty.all<Color>(pure_white)),
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
                          (index) => Onboarding_Page_1_Card(
                            icon: helpTopics[index]["icon"] as String,
                            title: helpTopics[index]["title"] as String,
                            ontap: helpTopics[index]["onTap"],
                          ),
                        ),
                      ),

                      // Onboarding_Page_1_Card(
                      //   icon: "assets/images/Onboarding Yourself.png",
                      //   title: "Onboarding Yourself",
                      //   ontap: () {},
                      // ),
                      // Onboarding_Page_1_Card(
                      //   icon: "assets/images/Registering an Individual.png",
                      //   title: "Registering an Individual",
                      //   ontap: () {},
                      // ),
                      // Onboarding_Page_1_Card(
                      //   icon: "assets/images/Synchronising Data.png",
                      //   title: "Synchronising Data",
                      //   ontap: () {},
                      // ),
                      // Onboarding_Page_1_Card(
                      //   icon: "assets/images/fingerprint_icon.png",
                      //   title: "Mapping Devices to Machine",
                      //   ontap: () {},
                      // ),
                      // Onboarding_Page_1_Card(
                      //   icon:
                      //       "assets/images/Uploading Local - Registration Data.png",
                      //   title: "Uploading Local/Registration Data",
                      //   ontap: () {},
                      // ),
                      // Onboarding_Page_1_Card(
                      //   icon: "assets/images/fingerprint_icon.png",
                      //   title: "Updating Operator Biometrics",
                      //   ontap: () {},
                      // ),
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
