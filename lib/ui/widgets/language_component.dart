import 'package:flutter/material.dart';
import 'package:registration_client/utils/app_style.dart';

class LanguageComponent extends StatefulWidget {
  const LanguageComponent({
    super.key,
    required this.title,
    required this.isSelected,
    required this.onTap,
  });
  final String title;
  final bool isSelected;
  final VoidCallback onTap;

  @override
  State<LanguageComponent> createState() => _LanguageComponentState();
}

class _LanguageComponentState extends State<LanguageComponent> {
  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: widget.onTap,
      child: Container(
        padding: const EdgeInsets.only(
          left: 25,
          right: 25,
          top: 15,
          bottom: 15,
        ),
        decoration: BoxDecoration(
          color: widget.isSelected ? AppStyle.appHelpText : Colors.transparent,
          border: Border.all(
            width: 1,
            color: widget.isSelected
                ? AppStyle.appHelpText
                : AppStyle.languageSelectedColor,
          ),
          borderRadius: const BorderRadius.all(
            Radius.circular(36),
          ),
        ),
        child: Text(
          widget.title,
          style: TextStyle(
            fontSize: 24,
            color:
                widget.isSelected ? AppStyle.appWhite : AppStyle.appBlackShade1,
          ),
        ),
      ),
    );
  }
}
