import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:provider/provider.dart';
import '../../provider/auth_provider.dart';
import '../../provider/global_provider.dart';
import '../../utils/app_config.dart';

class ValidatorAlert extends StatefulWidget {
  const ValidatorAlert({super.key,required this.errorMessage,this.subError});
  final String errorMessage;
  final String? subError;

  @override
  State<ValidatorAlert> createState() => _ValidatorAlertState();
}

class _ValidatorAlertState extends State<ValidatorAlert> {
  bool isMobile = true;
  late GlobalProvider globalProvider;
  late AppLocalizations appLocalizations = AppLocalizations.of(context)!;
  late AuthProvider authProvider;

  @override
  void initState() {
    authProvider = Provider.of<AuthProvider>(context, listen: false);
    super.initState();
  }



  @override
  Widget build(BuildContext context) {
    isMobile = MediaQuery.of(context).orientation == Orientation.portrait;
    return AlertDialog(
      surfaceTintColor: Colors.transparent,
      backgroundColor: appWhite,
      actionsPadding: const EdgeInsets.symmetric(horizontal: 10,vertical: 20),
      title: Container(
        height: 130,
        width: 130,
        decoration: const BoxDecoration(
          shape: BoxShape.circle,
          boxShadow: [BoxShadow(color: Color(0xFFF5F8FF), blurRadius: 50.0)],
        ),
        child: SvgPicture.asset('assets/svg/success_message_icon_orange.svg',
            height: 150,
            width: 150,
            alignment: Alignment.center,
            fit: BoxFit.scaleDown),
      ),
      contentPadding: EdgeInsets.symmetric(
          horizontal: isMobileSize ? 5.w : 20.w, vertical: 20.h),
      content: SizedBox(
        width: MediaQuery.of(context).size.width / 1.4,
        height: MediaQuery.of(context).size.height / 8,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const SizedBox(
              height: 15,
            ),
            Text(widget.errorMessage,
                textAlign: TextAlign.center,
                style: Theme.of(context)
                    .textTheme
                    .titleLarge
                    ?.copyWith(fontWeight: semiBold)),
            if(widget.subError!=null)...[
            const SizedBox(height: 10),
            Padding(
              padding: const EdgeInsets.only(left: 20, right: 20),
              child: Text(widget.subError.toString(),
                  textAlign: TextAlign.center,
                  style: Theme.of(context)
                      .textTheme
                      .titleSmall
                      ?.copyWith(fontWeight: regular)),
            ),
    ]
          ],
        ),
      ),
      actions: [
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            OutlinedButton(
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
                  height: isMobile && !isMobileSize ? 60.h : 50.h,
                  width: 150.h,
                  child: Center(
                    child: Text(
                      AppLocalizations.of(context)!.okay,
                      style: TextStyle(
                          fontSize: isMobile && !isMobileSize ? 18 : 14,
                          color: appSolidPrimary),
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