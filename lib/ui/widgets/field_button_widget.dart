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
import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class FieldButtonWidget extends StatefulWidget {
  const FieldButtonWidget({
    super.key,
    required this.isSelected,
    required this.onTap,
    required this.isMobile,
    required this.fieldTitle,
  });
  final bool isSelected;
  final VoidCallback onTap;
  final bool isMobile;
  final String fieldTitle;

  @override
  State<FieldButtonWidget> createState() => _FieldButtonWidgetState();
}

class _FieldButtonWidgetState extends State<FieldButtonWidget> {
  late GlobalProvider globalProvider;
  late AppLocalizations appLocalizations = AppLocalizations.of(context)!;

  @override
  void initState() {
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: () {
        widget.onTap();
      },
      child: Container(
        padding: EdgeInsets.only(
          left: 25.w,
          right: 25.w,
          top: widget.isMobile && !isMobileSize ? 15.h : 9.h,
          bottom: widget.isMobile && !isMobileSize ? 15.h : 9.h,
        ),
        decoration: BoxDecoration(
          color: widget.isSelected ? appButtonBorderText : Colors.transparent,
          border: Border.all(
            width: 1,
            color:
                widget.isSelected ? appButtonBorderText : languageSelectedColor,
          ),
          borderRadius: const BorderRadius.all(
            Radius.circular(5),
          ),
        ),
        child: Text(
          widget.fieldTitle,
          style: TextStyle(
            fontSize: widget.isMobile && !isMobileSize ? 24 : 16,
            color: widget.isSelected ? appWhite : appBlackShade1,
          ),
        ),
      ),
    );
  }
}
