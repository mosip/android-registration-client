import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';


class SyncAlertDialog extends StatelessWidget {
  const SyncAlertDialog({super.key, required this.title, required this.content, required this.onPressed,});

  final String title;
  final String content;
  final VoidCallback onPressed;

  @override
  Widget build(BuildContext context) {
    bool isMobile = MediaQuery.of(context).orientation == Orientation.portrait;
    return AlertDialog(
        title: Text(
          title,
          style: TextStyle(
            fontSize: isMobile && !isMobileSize ? 26 : 20,
            fontWeight: FontWeight.bold,
          ),
        ),
        contentPadding: EdgeInsets.symmetric(
            horizontal: isMobileSize ? 5.w : 20.w, vertical: 20.h),
        content: Text(
          content,
          style: TextStyle(
            fontSize: isMobile && !isMobileSize ? 18 : 14,
            fontWeight: regular,
          ),
        ),
        actions: [
          const Divider(),
          SizedBox(
            height: 10.h,
          ),
          Row(
            children: [
              Expanded(
                child: OutlinedButton(
                  onPressed: () {
                    Navigator.of(context).pop();
                  },
                  child: SizedBox(
                    height: isMobile && !isMobileSize ? 62.h : 37.h,
                    child: Center(
                      child: Text(
                        AppLocalizations.of(context)!.cancel,
                        style: TextStyle(
                          fontSize: isMobile && !isMobileSize ? 18 : 14,
                        ),
                      ),
                    ),
                  ),
                ),
              ),
              const SizedBox(
                width: 10,
              ),
              Expanded(
                child: ElevatedButton(
                  onPressed: onPressed,
                  style: ButtonStyle(
                    backgroundColor:
                        MaterialStateProperty.all<Color>(solidPrimary),
                  ),
                  child: SizedBox(
                    height: isMobile && !isMobileSize ? 62.h : 37.h,
                    child: Center(
                      child: Text(
                        AppLocalizations.of(context)!.sync_alert_text,
                        style: TextStyle(
                          fontSize: isMobile && !isMobileSize ? 18 : 14,
                        ),
                      ),
                    ),
                  ),
                ),
              ),
            ],
          ),
        ],
      );
  }
}