import 'package:flutter/material.dart';

import 'package:provider/provider.dart';

import '../../../provider/global_provider.dart';

class TextBoxControl extends StatelessWidget {
  const TextBoxControl(
      {super.key,
      required this.id,
      required this.label,
      required this.lang,
      required this.validation});

  final String id;

  final String label;
  final String lang;
  final RegExp validation;

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: 8),
      child: TextFormField(
        validator: (value) {
          if (value == null || value.isEmpty) {
            return 'Please enter a value';
          }
          if (!validation.hasMatch(value)) {
            return 'Invalid input';
          }
          context.read<GlobalProvider>().setLanguageSpecificValue(id, value,
              lang, context.read<GlobalProvider>().feildDemographicsValues);
          return null;
        },
        textAlign: (lang == 'ara') ? TextAlign.right : TextAlign.left,
        decoration: InputDecoration(
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(8.0),
            borderSide: const BorderSide(color: Color(0xff9B9B9F), width: 1),
          ),
          contentPadding:
              const EdgeInsets.symmetric(vertical: 14, horizontal: 16),
          hintText: label,
          hintStyle: const TextStyle(color: Color(0xff999999), fontSize: 14),
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
      ),
    );
  }
}
