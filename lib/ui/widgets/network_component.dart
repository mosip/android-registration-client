import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:registration_client/utils/app_style.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

import '../../utils/app_config.dart';

class NetworkComponent extends StatelessWidget {
  const NetworkComponent({super.key, required this.isMobile, required this.onTapRetry});
  final bool isMobile;
  final VoidCallback onTapRetry;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 10),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          SizedBox(
            height: 120,
            child: SvgPicture.asset(
                "assets/svg/No internet connection.svg"),
          ),
          SizedBox(
            height: 30.h,
          ),
          Text(AppLocalizations.of(context)!.no_internet_connection,
              style: const TextStyle(fontWeight: FontWeight.bold,color: Colors.black,fontSize: 26)),
          SizedBox(
            height: 5.h,
          ),
          Text(AppLocalizations.of(context)!.connect_and_retry,
              textAlign: TextAlign.center,
              style: const TextStyle(fontWeight: FontWeight.w300,color: Colors.grey,fontSize: 22)),
          SizedBox(
            height: 50.h,
          ),
          InkWell(
            onTap: onTapRetry,
            child: Container(
              height: isMobile && !isMobileSize ? 82.h : 52.h,
              decoration: BoxDecoration(
                color: appSolidPrimary,
                border: Border.all(
                  width: 1.w,
                  color: appBlueShade1,
                ),
                borderRadius: const BorderRadius.all(
                  Radius.circular(5),
                ),
              ),
              child: Center(
                child: Text(
                  AppLocalizations.of(context)!.retry,
                  style: isMobile
                      && !isMobileSize ? AppTextStyle.tabletPortraitButtonText
                      : AppTextStyle.mobileButtonText,
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
