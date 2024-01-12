import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/utils/app_config.dart';

class LanguageComponent extends StatefulWidget {
  const LanguageComponent({
    super.key,
    required this.title,
    required this.isSelected,
    required this.onTap,
    required this.isMobile,
    required this.isFreezed,
    required this.isDisabled,
  });
  final String title;
  final bool isSelected;
  final VoidCallback onTap;
  final bool isMobile;
  final bool isFreezed;
  final bool isDisabled;

  @override
  State<LanguageComponent> createState() => _LanguageComponentState();
}

class _LanguageComponentState extends State<LanguageComponent> {
  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: (){
        if(!widget.isFreezed && !widget.isDisabled){
          widget.onTap();
        }
      },
      child: Container(
        padding: EdgeInsets.only(
          left: 25.w,
          right: 25.w,
          top: widget.isMobile && !isMobileSize ? 15.h : 9.h,
          bottom: widget.isMobile && !isMobileSize ? 15.h : 9.h,
        ),
        decoration: BoxDecoration(
          color: widget.isFreezed
              ? languageFreezedColor
              : widget.isSelected
                  ? appButtonBorderText
                  : Colors.transparent,
          border: Border.all(
            width: 1,
            color: widget.isDisabled ? appBlackShade3 :
            widget.isSelected
                ? appButtonBorderText
                : languageSelectedColor,
          ),
          borderRadius: const BorderRadius.all(
            Radius.circular(36),
          ),
        ),
        child: Text(
          widget.title,
          style: TextStyle(
            fontSize: widget.isMobile && !isMobileSize ? 24 : 16,
            color: widget.isDisabled ? appBlackShade3 :
            widget.isFreezed
                ? appBlack
                : widget.isSelected
                    ? appWhite
                    : appBlackShade1,
          ),
        ),
      ),
    );
  }
}
