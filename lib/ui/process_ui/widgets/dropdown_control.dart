import 'dart:developer';

import 'package:flutter/material.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';

import '../../../provider/global_provider.dart';

class DropDownControl extends StatefulWidget {
  const DropDownControl({
    super.key,
    required this.id,
    required this.options,
  });

  final String id;
  final List<String?> options;

  @override
  State<DropDownControl> createState() => _CustomDropDownState();
}

class _CustomDropDownState extends State<DropDownControl> {
  String selected = "Select feild";

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16.0),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(8.0),
        border: Border.all(
          color: Colors.grey,
          width: 1.0,
        ),
      ),
      child: DropdownButtonFormField(
        icon: const Icon(null),
        onSaved: (value) {
          if (widget.id == 'postalCode') {
            context.read<GlobalProvider>().setInputMapValue(widget.id, value,
                context.read<GlobalProvider>().feildDemographicsValues);
            if(value != null) {
                  context.read<RegistrationTaskProvider>().addDemographicField(widget.id, value);
                }
          } else {
            context.read<GlobalProvider>().setLanguageSpecificValue(
                widget.id,
                value,
                "eng",
                context.read<GlobalProvider>().feildDemographicsValues);
                if(value != null) {
                  context.read<RegistrationTaskProvider>().addSimpleTypeDemographicField(widget.id, value, "eng");
                }
          }
        },
        decoration: InputDecoration(
          border: InputBorder.none,
          hintText: selected,
          hintStyle: const TextStyle(color: Color(0xff999999)),
        ),
        items: widget.options
            .map((option) => DropdownMenuItem(
                  value: option,
                  child: Text(option!),
                ))
            .toList(),
        onChanged: (value) {
          setState(() {
            selected = value!;
          });
        },
      ),
    );
  }
}
