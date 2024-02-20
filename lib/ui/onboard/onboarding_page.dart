/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/ui/onboard/widgets/operator_onboarding_biometrics_capture_control.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class OnboardingPage extends StatelessWidget {
  const OnboardingPage({super.key});

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        bottomNavigationBar: _getBottomBar(),
        backgroundColor: appSolidPrimary,
        body: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Container(
              alignment: Alignment.centerRight,
              padding: EdgeInsets.symmetric(horizontal: 20.w, vertical: 20.h),
              child: InkWell(
                onTap: () {},
                child: Container(
                  height: isMobileSize ? 46.h : 62.h,
                  width: 129.w,
                  decoration: BoxDecoration(
                    color: Colors.transparent,
                    border: Border.all(
                      color: appWhite,
                    ),
                    borderRadius: const BorderRadius.all(
                      Radius.circular(5),
                    ),
                  ),
                  child: Center(
                    child: Text(
                      AppLocalizations.of(context)!.help,
                      style: TextStyle(
                        fontSize: isMobileSize ? 16 : 22,
                        fontWeight: FontWeight.bold,
                        color: appWhite,
                      ),
                    ),
                  ),
                ),
              ),
            ),
            Expanded(
              child: Padding(
                padding: EdgeInsets.symmetric(horizontal: 20.w, vertical: 20.h),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          "${AppLocalizations.of(context)!.hello}, ",
                          style: TextStyle(
                            fontSize: isMobileSize ? 28.h : 36.h,
                            color: appWhite,
                          ),
                        ),
                        Text(
                          context.read<GlobalProvider>().name,
                          style: TextStyle(
                            fontSize: isMobileSize ? 28 : 36,
                            fontWeight: FontWeight.bold,
                            color: appWhite,
                          ),
                        )
                      ],
                    ),
                    SizedBox(
                      height: 7.h,
                    ),
                    Text(
                      AppLocalizations.of(context)!.onboard_process_help,
                      style: TextStyle(
                        fontSize: isMobileSize ? 16 : 22,
                        color: appWhite.withOpacity(0.6),
                      ),
                      textAlign: TextAlign.center,
                    ),
                    SizedBox(
                      height: 65.h,
                    ),
                    _getButton(
                      title: AppLocalizations.of(context)!.get_onboard,
                      onTap: () {
                        Navigator.push(
                            context,
                            MaterialPageRoute(
                                builder: (context) =>
                                    OperatorOnboardingBiometricsCaptureControl()));
                      },
                      color: appWhite,
                      fontColor: appSolidPrimary,
                    ),
                    SizedBox(
                      height: 40.h,
                    ),
                    _getButton(
                      title: AppLocalizations.of(context)!.skip_to_home,
                      onTap: () {
                        context.read<GlobalProvider>().setCurrentIndex(1);
                      },
                      color: Colors.transparent,
                      fontColor: appWhite,
                    ),
                    SizedBox(
                      height: 40.h,
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  _getBottomBar() {
    return Container(
      height: !isMobileSize ? 94.h : 62.h,
      padding: EdgeInsets.symmetric(
        vertical: 15.h,
      ),
      color: appWhite,
      child: Center(
        child: Image.asset(
          appIcon,
          fit: BoxFit.fill,
        ),
      ),
    );
  }

  _getButton({
    required String title,
    required VoidCallback onTap,
    required Color color,
    required Color fontColor,
  }) {
    return InkWell(
      onTap: onTap,
      child: Container(
        height: isMobileSize ? 62.h : 102.h,
        width: isMobileSize ? 350.w : 540.w,
        decoration: BoxDecoration(
          color: color,
          border: Border.all(
            color: appWhite,
          ),
          borderRadius: const BorderRadius.all(
            Radius.circular(5),
          ),
        ),
        child: Center(
          child: Text(
            title,
            style: TextStyle(
              fontSize: isMobileSize ? 16 : 22,
              fontWeight: FontWeight.bold,
              color: fontColor,
            ),
          ),
        ),
      ),
    );
  }
}
