import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/ui/onboarding_page_1/widgets/onboarding_page_1_card.dart';
import 'package:registration_client/ui/onboarding_page_1/widgets/onboarding_page_1_navbar_icon.dart';
import 'package:registration_client/utils/app_config.dart';

class OnboardingPage1View extends StatelessWidget {
  const OnboardingPage1View({super.key});

  @override
  Widget build(BuildContext context) {
    double h = ScreenUtil().screenHeight;
    double w = ScreenUtil().screenWidth;
    return AnnotatedRegion<SystemUiOverlayStyle>(
      value: SystemUiOverlayStyle(
        statusBarColor: Colors.transparent,
      ),
      child: Scaffold(
        backgroundColor: backgroundColor,
        bottomNavigationBar: Container(
          height: 80.h,
          color: pure_white,
          child: Padding(
            padding: EdgeInsets.fromLTRB(17.w, 12.h, 30.w, 17.h),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                OnboardingPage1NavbarIcon(
                    icon: Icon(Icons.home,color: solid_primary,),
                    title: Text(
                      "Home",
                      style: Theme.of(context)
                          .textTheme
                          .bodyMedium
                          ?.copyWith(color: solid_primary),
                    ),
                    ontap: () {}),
                OnboardingPage1NavbarIcon(
                    icon: Icon(Icons.settings,color: secondaryColors.elementAt(5),),
                    title: Text(
                      "Settings",
                      style: Theme.of(context)
                          .textTheme
                          .bodyMedium
                          ?.copyWith(color: secondaryColors.elementAt(5)),
                    ),
                    ontap: () {}),
                OnboardingPage1NavbarIcon(
                    icon: Icon(Icons.notifications,color: secondaryColors.elementAt(5),),
                    title: Text(
                      "Notifications",
                      style: Theme.of(context)
                          .textTheme
                          .bodyMedium
                          ?.copyWith(color: secondaryColors.elementAt(5)),
                    ),
                    ontap: () {}),
                OnboardingPage1NavbarIcon(
                    icon: Icon(Icons.home,color: secondaryColors.elementAt(5),),
                    title: Text(
                      "Account",
                      style: Theme.of(context)
                          .textTheme
                          .bodyMedium
                          ?.copyWith(color: secondaryColors.elementAt(5)),
                    ),
                    ontap: () {})
              ],
            ),
          ),
        ),
        body: SingleChildScrollView(
          child: Container(
            height: h,
            width: w,
            child: Column(
              children: [
                Container(
                  height: 292.h,
                  width: w,
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
                                    ?.copyWith(color: pure_white,fontWeight: regular),
                                children: [
                              TextSpan(
                                text: "Thomas!",
                                style: Theme.of(context)
                                    .textTheme
                                    .titleLarge
                                    ?.copyWith(
                                        color: pure_white,
                                        fontWeight: bold),
                              )
                            ])),
                      ),
                      Padding(
                        padding: EdgeInsets.fromLTRB(16.w, 8.h, 0, 0),
                        child: Text(
                          "Please tap the \"Get Onboard\" button to onboard yourself into the portal.",
                          style: Theme.of(context)
                              .textTheme
                              .bodyLarge
                              ?.copyWith(color: secondaryColors.elementAt(2)),
                        ),
                      ),
                      Padding(
                        padding: EdgeInsets.fromLTRB(16.w, 31.h, 0, 0),
                        child: ElevatedButton(
                          onPressed: () {},
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
                                  ?.copyWith(
                                      color: secondaryColors.elementAt(0)),
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
                              icon: "assets/images/Uploading Local - Registration Data.png",
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
          ),
        ),
      ),
    );
  }
}
