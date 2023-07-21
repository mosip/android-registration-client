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
  String? selected;

  @override
  void initState() {
    if (context
        .read<GlobalProvider>()
        .feildDemographicsValues
        .containsKey(widget.id)) {
      _getSelectedValueFromMap("eng");
    }
    super.initState();
  }

  void saveData(value) {
    if (value != null) {
      if (widget.type == 'simpleType') {
        context
            .read<RegistrationTaskProvider>()
            .addSimpleTypeDemographicField(widget.id, value, "eng");
      } else {
        context
            .read<RegistrationTaskProvider>()
            .addDemographicField(widget.id, value);
      }
    }
  }

  void _saveDataToMap(value) {
    if (value != null) {
      if (widget.type == 'simpleType') {
        context.read<GlobalProvider>().setLanguageSpecificValue(
              widget.id,
              value!,
              "eng",
              context.read<GlobalProvider>().feildDemographicsValues,
            );
      } else {
        context.read<GlobalProvider>().setInputMapValue(
              widget.id,
              value!,
              context.read<GlobalProvider>().feildDemographicsValues,
            );
      }
    }
  }

  void _getSelectedValueFromMap(String lang) {
    String response = "";
    if (widget.type == 'simpleType') {
      if ((context.read<GlobalProvider>().feildDemographicsValues[widget.id]
              as Map<String, dynamic>)
          .containsKey(lang)) {
        response = context
            .read<GlobalProvider>()
            .feildDemographicsValues[widget.id][lang];
      }
    } else {
      response =
          context.read<GlobalProvider>().feildDemographicsValues[widget.id];
    }
    setState(() {
      selected = response;
    });
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
        hintText: "Select Option",
        hintStyle: const TextStyle(color: Color(0xff999999)),
      ),
      items: widget.options
          .map((option) => DropdownMenuItem(
                value: option,
                child: Text(option!),
              ))
          .toList(),
      autovalidateMode: AutovalidateMode.onUserInteraction,
      value: selected,
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
        _saveDataToMap(value);
        setState(() {
          selected = value!;
        });
      },
    );
  }
}
