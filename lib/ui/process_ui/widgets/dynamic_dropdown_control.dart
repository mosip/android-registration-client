import 'package:flutter/material.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/utils/app_style.dart';

import '../../../model/field.dart';
import '../../../provider/global_provider.dart';
import 'custom_label.dart';

class DynamicDropDownControl extends StatefulWidget {
  const DynamicDropDownControl(
      {super.key, required this.field, required this.validation});

  final Field field;
  final RegExp validation;

  @override
  State<DynamicDropDownControl> createState() => _CustomDynamicDropDownState();
}

class _CustomDynamicDropDownState extends State<DynamicDropDownControl> {
  String? selected;

  @override
  void initState() {
    if (context
        .read<GlobalProvider>()
        .fieldInputValue
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
              context.read<GlobalProvider>().fieldInputValue,
            );
      } else {
        context.read<GlobalProvider>().setInputMapValue(
              widget.field.id ?? "",
              value!,
              context.read<GlobalProvider>().fieldInputValue,
            );
      }
    }
  }

  void _getSelectedValueFromMap(String lang) {
    String response = "";
    if (widget.field.type == 'simpleType') {
      if ((context.read<GlobalProvider>().fieldInputValue[widget.field.id ?? ""]
              as Map<String, dynamic>)
          .containsKey(lang)) {
        response = context
            .read<GlobalProvider>()
            .fieldInputValue[widget.field.id ?? ""][lang];
      }
    } else {
      response =
          context.read<GlobalProvider>().fieldInputValue[widget.field.id ?? ""];
    }
    setState(() {
      selected = response;
    });
  }

  Future<List<String?>> _getFieldValues(String fieldId, String langCode) async {
    return await context
        .read<RegistrationTaskProvider>()
        .getFieldValues(fieldId, langCode);
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder(
        future: _getFieldValues(widget.field.subType!, "eng"),
        builder: (BuildContext context, AsyncSnapshot<List<String?>> snapshot) {
          return Card(
            elevation: 0,
            margin: const EdgeInsets.symmetric(vertical: 1, horizontal: 12),
            child: Padding(
              padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  CustomLabel(field: widget.field),
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
                            hintStyle: const TextStyle(
                              color: AppStyle.appBlackShade3,
                            ),
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
                            if (!widget.field.required! &&
                                widget.field.requiredOn!.isEmpty) {
                              return null;
                            }
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
