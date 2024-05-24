import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:provider/provider.dart';
import '../../provider/auth_provider.dart';
import '../../provider/global_provider.dart';
import '../../utils/app_config.dart';

class LogoutAlert extends StatefulWidget {
  const LogoutAlert({super.key});

  @override
  State<LogoutAlert> createState() => _LogoutAlertState();
}

class _LogoutAlertState extends State<LogoutAlert> {
  bool isMobile = true;
  late GlobalProvider globalProvider;
  late AppLocalizations appLocalizations = AppLocalizations.of(context)!;
  late AuthProvider authProvider;

  @override
  void initState() {
    authProvider = Provider.of<AuthProvider>(context, listen: false);
    super.initState();
  }

  void _showInSnackBar(String value) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(value),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    isMobile = MediaQuery.of(context).orientation == Orientation.portrait;
    return AlertDialog(
      surfaceTintColor: Colors.transparent,
      backgroundColor: appWhite,
      title: Container(
        height: 130,
        width: 130,
        decoration: const BoxDecoration(
          shape: BoxShape.circle,
          boxShadow: [BoxShadow(color: Color(0xFFF5F8FF), blurRadius: 50.0)],
        ),
        child: SvgPicture.asset('assets/svg/success_message_icon_pink.svg',
            height: 150,
            width: 150,
            alignment: Alignment.center,
            fit: BoxFit.scaleDown),
      ),
      contentPadding: EdgeInsets.symmetric(
          horizontal: isMobileSize ? 5.w : 20.w, vertical: 20.h),
      content: SizedBox(
        width: MediaQuery.of(context).size.width / 1.4,
        height: MediaQuery.of(context).size.height / 6,
        child: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              const SizedBox(
                height: 15,
              ),
              Text(appLocalizations.logout_action_required,
                  textAlign: TextAlign.center,
                  style: Theme.of(context)
                      .textTheme
                      .titleLarge
                      ?.copyWith(fontWeight: semiBold)),
              const SizedBox(height: 10),
              Padding(
                padding: const EdgeInsets.only(left: 20, right: 20),
                child: Text(appLocalizations.logout_warning_message,
                    textAlign: TextAlign.center,
                    style: Theme.of(context)
                        .textTheme
                        .titleSmall
                        ?.copyWith(fontWeight: regular)),
              ),
            ],
          ),
        ),
      ),
      actions: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Expanded(
              child: OutlinedButton(
                style: ElevatedButton.styleFrom(
                    backgroundColor: appWhite,
                    side: BorderSide(color: solidPrimary),
                    shape: const RoundedRectangleBorder(
                        borderRadius: BorderRadius.all(Radius.circular(5.0)))),
                onPressed: () {
                  Navigator.of(context).pop();
                },
                child: Padding(
                  padding: const EdgeInsets.only(top: 4, bottom: 4),
                  child: SizedBox(
                    height: isMobile && !isMobileSize ? 62.h : 37.h,
                    child: Center(
                      child: Text(
                        appLocalizations.cancel,
                        style: TextStyle(
                            fontSize: isMobile && !isMobileSize ? 18 : 14,
                            color: appSolidPrimary),
                      ),
                    ),
                  ),
                ),
              ),
            ),
            SizedBox(
              width: isMobile ? 20.w : 15.w,
            ),
            Expanded(
              child: ElevatedButton(
                style: ElevatedButton.styleFrom(
                    backgroundColor: logoutButtonColor,
                    shape: const RoundedRectangleBorder(
                        borderRadius: BorderRadius.all(Radius.circular(5.0)))),
                onPressed: () async {
                  String result = await authProvider.logoutUser();
                  if (result.contains("Logout Success")) {
                    _showInSnackBar("You have been successfully logged out!");
                    Navigator.pushNamedAndRemoveUntil(
                        context, '/login-page', (route) => false);
                  } else {
                    _showInSnackBar(
                        "Something went wrong, please try again after some time");
                    Navigator.of(context).pop();
                  }
                },
                child: Padding(
                  padding: const EdgeInsets.only(top: 4, bottom: 4),
                  child: SizedBox(
                    height: isMobile && !isMobileSize ? 62.h : 37.h,
                    child: Center(
                      child: Text(
                        appLocalizations.logout,
                        style: TextStyle(
                            fontSize: isMobile && !isMobileSize ? 18 : 14,
                            color: appWhite),
                      ),
                    ),
                  ),
                ),
              ),
            ),
          ],
        ),
      ],
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.all(
          Radius.circular(12),
        ),
      ),
    );
  }
}
