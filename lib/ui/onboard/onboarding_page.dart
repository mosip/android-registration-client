import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/app_style.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

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
                  child: Center(
                    child: Text(
                      AppLocalizations.of(context)!.help,
                      style: const TextStyle(
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
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          AppLocalizations.of(context)!.hello,
                          style: const TextStyle(
                            fontSize: 36,
                            color: AppStyle.appWhite,
                          ),
                        ),
                        Text(
                          context.read<GlobalProvider>().name,
                          style: const TextStyle(
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
                      AppLocalizations.of(context)!.onboard_process_help,
                      style: TextStyle(
                        fontSize: 22,
                        color: AppStyle.appWhite.withOpacity(0.6),
                      ),
                    ),
                    SizedBox(
                      height: 65.h,
                    ),
                    _getButton(
                      title: AppLocalizations.of(context)!.get_onboard,
                      onTap: () {},
                      color: AppStyle.appWhite,
                      fontColor: AppStyle.appHelpText,
                    ),
                    SizedBox(
                      height: 40.h,
                    ),
                    _getButton(
                      title: AppLocalizations.of(context)!.skip_to_home,
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
