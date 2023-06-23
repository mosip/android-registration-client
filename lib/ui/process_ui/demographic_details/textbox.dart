import 'package:flutter/material.dart';
import 'package:registration_client/utils/app_config.dart';

class TextBox extends StatelessWidget {
  const TextBox(
      {super.key,
      required this.onChanged,
      required this.label,
      required this.validation});

  final Function(String) onChanged;
  final String label;
  final RegExp validation;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.only(left: 12),
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
        decoration: InputDecoration(
          border: InputBorder.none,
          hintText: label,
          hintStyle: const TextStyle(color: Color(0xff999999)),
          contentPadding:
              const EdgeInsets.symmetric(vertical: 16, horizontal: 0),
          suffixIconColor: solid_primary,
          suffixIcon: const Icon(
            Icons.keyboard_outlined,
            size: 36,
          ),
        ),
        onChanged: (value) => onChanged(value),
      ),
    );
  }
}
