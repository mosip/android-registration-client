import 'dart:developer';

import 'package:flutter/material.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/pigeon/dynamic_response_pigeon.dart';
import 'package:registration_client/provider/registration_task_provider.dart';

import '../../../model/field.dart';
import '../../../provider/global_provider.dart';
import 'custom_label.dart';

class DropDownControl extends StatefulWidget {
  const DropDownControl({
    super.key,
    required this.field,
    required this.validation,
  });

  final Field field;
  final RegExp validation;

  @override
  State<DropDownControl> createState() => _CustomDropDownState();
}

class _CustomDropDownState extends State<DropDownControl> {
  GenericData? selected;

  List<String> hierarchyReverse = [
    "region",
    "province",
    "city",
    "zone",
    "postalCode"
  ];
  int? index;
  List<GenericData?> list = [];

  @override
  void initState() {
    setState(() {
      index = hierarchyReverse.indexOf(widget.field.id!);
    });
    _getOptionsList();
    super.initState();
  }

  void saveData(value) {
    for (int i = index! + 1; i < 5; i++) {
      context
          .read<RegistrationTaskProvider>()
          .removeDemographicField(hierarchyReverse[i]);
    }
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

  void _saveDataToMap(GenericData? value) {
    for (int i = index! + 1; i < 5; i++) {
      context.read<GlobalProvider>().removeFieldFromMap(
            hierarchyReverse[i],
            context.read<GlobalProvider>().fieldInputValue,
          );
    }
    if (value != null) {
      if (widget.field.type == 'simpleType') {
        context.read<GlobalProvider>().setLanguageSpecificValue(
              widget.field.id ?? "",
              value,
              "eng",
              context.read<GlobalProvider>().fieldInputValue,
            );
      } else {
        context.read<GlobalProvider>().setInputMapValue(
              widget.field.id ?? "",
              value,
              context.read<GlobalProvider>().fieldInputValue,
            );
      }
    }
  }

  void _getSelectedValueFromMap(String lang, List<GenericData?> list) {
    GenericData? response;
    if (widget.field.type == 'simpleType') {
      if ((context.read<GlobalProvider>().fieldInputValue[widget.field.id ?? ""]
              as Map<String, dynamic>)
          .containsKey(lang)) {
        response = context
            .read<GlobalProvider>()
            .fieldInputValue[widget.field.id ?? ""][lang] as GenericData;
      }
    } else {
      response = context
          .read<GlobalProvider>()
          .fieldInputValue[widget.field.id ?? ""] as GenericData;
    }
    setState(() {
      list.forEach((element) {
        if (element!.name == response!.name) {
          selected = element;
        }
      });
    });
  }

  Future<List<GenericData?>> _getLocationValues(
      String hierarchyLevelName, String langCode) async {
    return await context
        .read<RegistrationTaskProvider>()
        .getLocationValues(hierarchyLevelName, langCode);
  }

  Future<List<GenericData?>> _getLocationValuesBasedOnParent(
      String? parentCode, String hierarchyLevelName, String langCode) async {
    return await context
        .read<RegistrationTaskProvider>()
        .getLocationValuesBasedOnParent(
            parentCode, hierarchyLevelName, langCode);
  }

  _getOptionsList() async {
    log("message");
    List<GenericData?> temp;
    if (index == 0) {
      temp = await _getLocationValues(widget.field.subType!, "eng");
    } else {
      var parentCode =
          context.watch<GlobalProvider>().locationHierarchy[index! - 1];
      temp = await _getLocationValuesBasedOnParent(
          parentCode, widget.field.subType!, "eng");
    }
    setState(() {
      selected = null;
    });
    setState(() {
      list = temp;
    });
    if (context
        .read<GlobalProvider>()
        .fieldInputValue
        .containsKey(widget.field.id ?? "")) {
      _getSelectedValueFromMap("eng", list);
    }
  }

  @override
  Widget build(BuildContext context) {
    _getOptionsList();
    return Column(children: [
      Card(
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
              DropdownButtonFormField<GenericData>(
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
                items: list
                    .map((option) => DropdownMenuItem(
                          value: option,
                          child: Text(option!.name),
                        ))
                    .toList(),
                autovalidateMode: AutovalidateMode.onUserInteraction,
                value: selected,
                validator: (value) {
                  if (!widget.field.required! && widget.field.requiredOn!.isEmpty) {
                      return null;
                    }
                  if (value == null) {
                    return 'Please enter a value';
                  }
                  if (!widget.validation.hasMatch(value.name)) {
                    return 'Invalid input';
                  }
                  return null;
                },
                onChanged: (value) {
                  if (value != selected) {
                    saveData(value!.name);
                    _saveDataToMap(value);
                    context
                        .read<GlobalProvider>()
                        .setLocationHierarchy(value.code, index!);
                    _getSelectedValueFromMap("eng", list);
                  }
                  log(context
                      .read<GlobalProvider>()
                      .locationHierarchy
                      .toString());

                  // log("commited on feature flutter error");
                },
              ),
            ],
          ),
        ),
      ),
    ]);
  }
}
