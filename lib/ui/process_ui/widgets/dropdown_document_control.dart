import 'dart:developer';

import 'package:flutter/material.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';

import '../../../model/field.dart';
import '../../../provider/global_provider.dart';
import 'custom_label.dart';

class DropDownDocumentControl extends StatefulWidget {
  const DropDownDocumentControl(
      {super.key, required this.field, required this.validation});

  final Field field;
  final RegExp validation;

  @override
  State<DropDownDocumentControl> createState() => _CustomDropDownState();
}

class _CustomDropDownState extends State<DropDownDocumentControl> {
  String? selected;

  @override
  void initState() {
    if (context
        .read<GlobalProvider>()
        .feildDemographicsValues
        .containsKey(widget.field.id ?? "")) {
      _getSelectedValueFromMap("eng");
    }
    super.initState();
  }

  void saveData(value) {
    if (value != null) {
      if (widget.field.type == 'simpleType') {
        context
            .read<RegistrationTaskProvider>()
            .addSimpleTypeDemographicField(widget.field.id ?? "", value, "eng");
      } else {
        context
            .read<RegistrationTaskProvider>()
            .addDemographicField(widget.field.id ?? "", value);
      }
    }
  }

  void _saveDataToMap(value) {
    if (value != null) {
      if (widget.field.type == 'simpleType') {
        context.read<GlobalProvider>().setLanguageSpecificValue(
              widget.field.id ?? "",
              value!,
              "eng",
              context.read<GlobalProvider>().feildDemographicsValues,
            );
      } else {
        context.read<GlobalProvider>().setInputMapValue(
              widget.field.id ?? "",
              value!,
              context.read<GlobalProvider>().feildDemographicsValues,
            );
      }
    }
  }

  void _getSelectedValueFromMap(String lang) {
    String response = "";
    if (widget.field.type == 'simpleType') {
      if ((context
                  .read<GlobalProvider>()
                  .feildDemographicsValues[widget.field.id ?? ""]
              as Map<String, dynamic>)
          .containsKey(lang)) {
        response = context
            .read<GlobalProvider>()
            .feildDemographicsValues[widget.field.id ?? ""][lang];
      }
    } else {
      response = context
          .read<GlobalProvider>()
          .feildDemographicsValues[widget.field.id ?? ""];
    }
    setState(() {
      selected = response;
    });
  }

  Future<List<String?>> _getLocationValues(
      String hierarchyLevelName, String langCode) async {
    return await context
        .read<RegistrationTaskProvider>()
        .getLocationValues(hierarchyLevelName, langCode);
  }

  Future<List<String?>> _getDocumentValues(
      String fieldName, String langCode, String? applicantType) async {
    //String fieldName, String langCode, String applicantType
    return await context
        .read<RegistrationTaskProvider>()
        .getDocumentValues(fieldName, langCode, applicantType);
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder(
        future: _getDocumentValues(widget.field.subType!, "eng",
            null), //TODO: drive the applicant type
        builder: (BuildContext context, AsyncSnapshot<List<String?>> snapshot) {
          return Card(
            elevation: 0,
            margin: const EdgeInsets.symmetric(vertical: 1, horizontal: 12),
            child: Padding(
              padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  CustomLabel(feild: widget.field),
                  const SizedBox(
                    height: 10,
                  ),
                  snapshot.hasData
                      ? DropdownButtonFormField(
                          icon: const Icon(null),
                          decoration: InputDecoration(
                            contentPadding:
                                const EdgeInsets.symmetric(horizontal: 16.0),
                            border: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(8.0),
                              borderSide: const BorderSide(
                                color: Colors.grey,
                                width: 1.0,
                              ),
                            ),
                            hintText: "Select Option",
                            hintStyle:
                                const TextStyle(color: Color(0xff999999)),
                          ),
                          items: snapshot.data!
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
                        )
                      : const SizedBox.shrink(),
                ],
              ),
            ),
          );
        });
  }
}
