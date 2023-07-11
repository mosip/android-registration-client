import 'package:flutter/material.dart';
import 'package:registration_client/utils/app_config.dart';

class TextBoxControl extends StatelessWidget {
  const TextBoxControl(
      {super.key,
      required this.onChanged,
      required this.label,
      required this.lang,
      required this.validation});

  final Function(String) onChanged;
  final String label;
  final String lang;
  final RegExp validation;

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.symmetric(vertical: 4),
      padding: const EdgeInsets.symmetric(horizontal: 12),
      decoration: BoxDecoration(
        border: Border.all(color: const Color(0xff9B9B9F), width: 1),
        borderRadius: BorderRadius.circular(8.0),
      ),
      child: TextFormField(
        validator: (value) {
          if (value == null || value.isEmpty) {
            return 'Please enter a value';
          }
          if (!validation.hasMatch(value)) {
            return 'Invalid input';
          }
          return null;
        },
        textAlign: (lang == 'ara') ? TextAlign.right : TextAlign.left,
        decoration: InputDecoration(
          border: InputBorder.none,
          hintText: label,
          hintStyle: const TextStyle(color: Color(0xff999999)),
          contentPadding:
              const EdgeInsets.symmetric(vertical: 16, horizontal: 0),
          prefixIcon: (lang == 'ara')
              ? const Icon(
                  Icons.keyboard_outlined,
                  size: 36,
                )
              : null,
          suffixIcon: (lang == 'ara')
              ? null
              : const Icon(
                  Icons.keyboard_outlined,
                  size: 36,
                ),
        ),
        onChanged: (value) => onChanged(value),
      ),
    );
  }
}
