import 'dart:convert';
import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';

import '../../../model/field.dart';
import '../../../platform_spi/registration.dart';
import '../../../provider/global_provider.dart';
import 'custom_label.dart';

class TextBoxControl extends StatefulWidget {
  const TextBoxControl({super.key, required this.e, required this.validation});
  final Field e;
  final RegExp validation;

  @override
  State<TextBoxControl> createState() => _TextBoxControlState();
}

class _TextBoxControlState extends State<TextBoxControl> {
  bool isMvelValid = true;

  evaluateMVEL(String fieldData, String? engine, String? expression) async {
    final Registration registration = Registration();
    registration.evaluateMVEL(fieldData, expression!).then((value) {
      setState(() {
        isMvelValid = value;
      });
    });
  }

  @override
  void initState() {
    if (widget.e.required == false) {
      if (widget.e.requiredOn!.isNotEmpty) {
        evaluateMVEL(
          jsonEncode(widget.e.toJson()),
          widget.e.requiredOn?[0]?.engine,
          widget.e.requiredOn?[0]?.expr,
        );
      }
    }
    super.initState();
  }

  void saveData(value, lang) {
    if (widget.e.type == 'simpleType') {
      context
          .read<RegistrationTaskProvider>()
          .addSimpleTypeDemographicField(widget.e.id!, value!, lang);
    } else {
      context
          .read<RegistrationTaskProvider>()
          .addDemographicField(widget.e.id!, value!);
    }
  }

  void _saveDataToMap(value, lang) {
    if (widget.e.type == 'simpleType') {
      context.read<GlobalProvider>().setLanguageSpecificValue(
            widget.e.id!,
            value!,
            lang,
            context.read<GlobalProvider>().feildDemographicsValues,
          );
    } else {
      context.read<GlobalProvider>().setInputMapValue(
            widget.e.id!,
            value!,
            context.read<GlobalProvider>().feildDemographicsValues,
          );
    }
  }

  String _getDataFromMap(String lang) {
    String response = "";
    if (context
        .read<GlobalProvider>()
        .feildDemographicsValues
        .containsKey(widget.e.id)) {
      if (widget.e.type == 'simpleType') {
        if ((context.read<GlobalProvider>().feildDemographicsValues[widget.e.id]
                as Map<String, dynamic>)
            .containsKey(lang)) {
          response = context
              .read<GlobalProvider>()
              .feildDemographicsValues[widget.e.id][lang];
        }
      } else {
        response =
            context.read<GlobalProvider>().feildDemographicsValues[widget.e.id];
      }
    }
    return response;
  }

  @override
  Widget build(BuildContext context) {
    List<String> choosenLang = context.read<GlobalProvider>().chosenLang;

    List<String> singleTextBox = [
      "Phone",
      "Email",
      "introducerName",
      "RID",
      "UIN",
      "none"
    ];
    if (singleTextBox.contains(widget.e.subType)) {
      choosenLang = ["English"];
    }

    return isMvelValid
        ? Card(
            elevation: 0,
            margin: const EdgeInsets.symmetric(vertical: 1, horizontal: 12),
            child: Padding(
              padding: const EdgeInsets.symmetric(vertical: 20, horizontal: 12),
              child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    CustomLabel(feild: widget.e),
                    const SizedBox(
                      height: 10,
                    ),
                    Column(
                      children: choosenLang.map((code) {
                        String lang =
                            context.read<GlobalProvider>().langToCode(code);
                        return Container(
                          margin: const EdgeInsets.only(bottom: 8),
                          child: TextFormField(
                            autovalidateMode:
                                AutovalidateMode.onUserInteraction,
                            initialValue: _getDataFromMap(lang),
                            onChanged: (value) {
                              _saveDataToMap(value, lang);
                              saveData(value, lang);
                            },
                            validator: (value) {
                              if (value == null || value.isEmpty) {
                                return 'Please enter a value';
                              }
                              if (!widget.validation.hasMatch(value)) {
                                return 'Invalid input';
                              }
                              return null;
                            },
                            textAlign: (lang == 'ara')
                                ? TextAlign.right
                                : TextAlign.left,
                            decoration: InputDecoration(
                              border: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(8.0),
                                borderSide: const BorderSide(
                                    color: Color(0xff9B9B9F), width: 1),
                              ),
                              contentPadding: const EdgeInsets.symmetric(
                                  vertical: 14, horizontal: 16),
                              hintText: widget.e.label![lang],
                              hintStyle: const TextStyle(
                                  color: Color(0xff999999), fontSize: 14),
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
                      }).toList(),
                    ),
                  ]),
            ),
          )
        : const SizedBox.shrink();
  }
}
