import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/ui/dashboard/dashboard_view_model.dart';
import 'package:registration_client/ui/onboarding/widgets/onboarding_page_1_card.dart';
import 'package:registration_client/utils/app_config.dart';

class OnboardingPage1View extends StatelessWidget {
  const OnboardingPage1View({super.key});

  @override
  Widget build(BuildContext context) {
    return AnnotatedRegion<SystemUiOverlayStyle>(
      value: const SystemUiOverlayStyle(
        statusBarColor: Colors.transparent,
      ),
      child: Column(
        children: [
          Container(
            height: 292.h,
            width: double.infinity,
            decoration: BoxDecoration(
              gradient: LinearGradient(
                colors: [secondaryColors.elementAt(0), solid_primary],
              ),
            ),
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
                          text: "Thomas!",
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
                        color: Color(0xffFFFFFF).withOpacity(0.6),
                        fontSize: 18,
                        fontWeight: regular),
                  ),
                ),
                Padding(
                  padding: EdgeInsets.fromLTRB(16.w, 31.h, 0, 0),
                  child: ElevatedButton(
                    onPressed: () {
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
          Padding(
            padding: EdgeInsets.fromLTRB(17.w, 30.h, 16.w, 0),
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
                Container(
                  height: 413.h,
                  child: ListView(
                    padding: EdgeInsets.all(0),
                    scrollDirection: Axis.vertical,
                    children: [
                      Onboarding_Page_1_Card(
                        icon: "assets/images/Onboarding Yourself.png",
                        title: "Onboarding Yourself",
                        ontap: () {},
                      ),
                      Onboarding_Page_1_Card(
                        icon: "assets/images/Registering an Individual.png",
                        title: "Registering an Individual",
                        ontap: () {},
                      ),
                      Onboarding_Page_1_Card(
                        icon: "assets/images/Synchronising Data.png",
                        title: "Synchronising Data",
                        ontap: () {},
                      ),
                      Onboarding_Page_1_Card(
                        icon: "assets/images/fingerprint_icon.png",
                        title: "Mapping Devices to Machine",
                        ontap: () {},
                      ),
                      Onboarding_Page_1_Card(
                        icon:
                            "assets/images/Uploading Local - Registration Data.png",
                        title: "Uploading Local/Registration Data",
                        ontap: () {},
                      ),
                      Onboarding_Page_1_Card(
                        icon: "assets/images/fingerprint_icon.png",
                        title: "Updating Operator Biometrics",
                        ontap: () {},
                      ),
                    ],
                  ),
                )
              ],
            ),
          )
        ],
      ),
    );
  }
}
