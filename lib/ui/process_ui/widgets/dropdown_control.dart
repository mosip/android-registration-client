import 'dart:developer';

import 'package:flutter/material.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';

import '../../../provider/global_provider.dart';

class DropDownControl extends StatefulWidget {
  const DropDownControl(
      {super.key,
      required this.id,
      required this.options,
      required this.type,
      required this.validation});

  final String id;
  final List<String?> options;
  final String type;
  final RegExp validation;

  @override
  State<DropDownControl> createState() => _CustomDropDownState();
}

class _CustomDropDownState extends State<DropDownControl> {
  String selected = "Select feild";

  void saveData(value) {
    if (widget.type == 'simpleType') {
      if (value != null) {
        context
            .read<RegistrationTaskProvider>()
            .addSimpleTypeDemographicField(widget.id, value, "eng");
      }
    } else {
      if (value != null) {
        context
            .read<RegistrationTaskProvider>()
            .addDemographicField(widget.id, value);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return DropdownButtonFormField(
      icon: const Icon(null),
      decoration: InputDecoration(
        contentPadding: const EdgeInsets.symmetric(horizontal: 16.0),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(8.0),
          borderSide: const BorderSide(
            color: Colors.grey,
            width: 1.0,
          ),
        ),
        hintText: selected,
        hintStyle: const TextStyle(color: Color(0xff999999)),
      ),
      items: widget.options
          .map((option) => DropdownMenuItem(
                value: option,
                child: Text(option!),
              ))
          .toList(),
      autovalidateMode: AutovalidateMode.onUserInteraction,
      validator: (value) {
        if (value == null || value.isEmpty) {
          return 'Please enter a value';
        }
        if (!widget.validation.hasMatch(value)) {
          return 'Invalid input';
        }
        return null;
      },
      onChanged: (value) {
        saveData(value);
        setState(() {
          selected = value!;
        });
      },
    );
  }
}
