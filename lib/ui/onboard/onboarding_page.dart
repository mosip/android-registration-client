import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/app_style.dart';

class OnboardingPage extends StatelessWidget {
  const OnboardingPage({super.key});

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        backgroundColor: AppStyle.appSolidPrimary,
        body: Column(
          children: [
            Container(
              alignment: Alignment.centerRight,
              padding: EdgeInsets.symmetric(horizontal: 20.w, vertical: 20.h),
              child: InkWell(
                onTap: () {},
                child: Container(
                  height: 62.h,
                  width: 129.w,
                  decoration: BoxDecoration(
                    color: Colors.transparent,
                    border: Border.all(
                      color: AppStyle.appWhite,
                    ),
                    borderRadius: const BorderRadius.all(
                      Radius.circular(5),
                    ),
                  ),
                  child: const Center(
                    child: Text(
                      'HELP',
                      style: TextStyle(
                        fontSize: 22,
                        fontWeight: FontWeight.bold,
                        color: AppStyle.appWhite,
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
                    const Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          'Hello ',
                          style: TextStyle(
                            fontSize: 36,
                            color: AppStyle.appWhite,
                          ),
                        ),
                        Text(
                          'Thomas Mendez,',
                          style: TextStyle(
                            fontSize: 36,
                            fontWeight: FontWeight.bold,
                            color: AppStyle.appWhite,
                          ),
                        )
                      ],
                    ),
                    SizedBox(
                      height: 7.h,
                    ),
                    Text(
                      'Please tap \'GET ONBOARD\' to get started with the onboarding process.',
                      style: TextStyle(
                        fontSize: 22,
                        color: AppStyle.appWhite.withOpacity(0.6),
                      ),
                    ),
                    SizedBox(
                      height: 65.h,
                    ),
                    _getButton(
                      title: 'GET ONBOARD',
                      onTap: () {},
                      color: AppStyle.appWhite,
                      fontColor: AppStyle.appButtonBorderText,
                    ),
                    SizedBox(
                      height: 40.h,
                    ),
                    _getButton(
                      title: 'SKIP TO HOME',
                      onTap: () {},
                      color: Colors.transparent,
                      fontColor: AppStyle.appWhite,
                    ),
                    SizedBox(
                      height: 40.h,
                    ),
                  ],
                ),
              ),
            ),
            _getBottomBar(),
          ],
        ),
      ),
    );
  }

  _getBottomBar() {
    return Container(
      height: 94.h,
      padding: EdgeInsets.symmetric(
        vertical: 15.h,
      ),
      color: AppStyle.appWhite,
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
      child: Container(
        height: 102.h,
        width: 540.w,
        decoration: BoxDecoration(
          color: color,
          border: Border.all(
            color: AppStyle.appWhite,
          ),
          borderRadius: const BorderRadius.all(
            Radius.circular(5),
          ),
        ),
        child: Center(
          child: Text(
            title,
            style: TextStyle(
              fontSize: 22,
              fontWeight: FontWeight.bold,
              color: fontColor,
            ),
          ),
        ),
      ),
    );
  }
}
