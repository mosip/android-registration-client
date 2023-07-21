import 'dart:developer';

import 'package:flutter/material.dart';

import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';

import '../../../model/field.dart';
import '../../../provider/global_provider.dart';
import 'custom_label.dart';

class AgeDateControl extends StatefulWidget {
  const AgeDateControl(
      {super.key, required this.validation, required this.field});

  final RegExp validation;
  final Field field;

  @override
  State<AgeDateControl> createState() => _AgeDateControlState();
}

class _AgeDateControlState extends State<AgeDateControl> {
  final TextEditingController _dayController = TextEditingController();

  final TextEditingController _monthController = TextEditingController();
  final TextEditingController _yearController = TextEditingController();
  final TextEditingController _ageController = TextEditingController();

  final dayFocus = FocusNode();
  final monthFocus = FocusNode();
  final yearFocus = FocusNode();

  @override
  void initState() {
    _getSavedDate();
    super.initState();
  }

  @override
  void dispose() {
    _dayController.dispose();
    _monthController.dispose();
    _yearController.dispose();

    dayFocus.dispose();
    monthFocus.dispose();
    yearFocus.dispose();

    super.dispose();
  }

  void focusNextField(FocusNode currentFocus, FocusNode nextFocus) {
    currentFocus.unfocus();
    FocusScope.of(context).requestFocus(nextFocus);
  }

  void _removeFocusFromAll(String currentTab) {
    switch (currentTab) {
      case "day":
        monthFocus.unfocus();
        yearFocus.unfocus();
        break;
      case "month":
        dayFocus.unfocus();
        yearFocus.unfocus();
        break;
      case "year":
        dayFocus.unfocus();
        monthFocus.unfocus();
        break;
      default:
    }
  }

  String? feildValidation(value, message) {
    try {
      String targetDateString = widget.field.format ??
          "yyyy/MM/dd"
              .replaceAll('dd', _dayController.text.padLeft(2, '0'))
              .replaceAll('MM', _monthController.text.padLeft(2, '0'))
              .replaceAll('yyyy', _yearController.text);

      if (value == "") {
        return 'Empty';
      }
      if (!widget.validation.hasMatch(targetDateString)) {
        return message;
      }
      return null;
    } catch (e) {
      log("error");
      return "";
    }
  }

  void saveData() {
    String targetDateString = widget.field.format ??
        "yyyy/MM/dd"
            .replaceAll('dd', _dayController.text.padLeft(2, '0'))
            .replaceAll('MM', _monthController.text.padLeft(2, '0'))
            .replaceAll('yyyy', _yearController.text);

    context.read<RegistrationTaskProvider>().setDateField(
          widget.field.id ?? "",
          widget.field.subType ?? "",
          _dayController.text.padLeft(2, '0'),
          _monthController.text.padLeft(2, '0'),
          _yearController.text,
        );
    context.read<GlobalProvider>().setInputMapValue(
          widget.field.id!,
          targetDateString,
          context.read<GlobalProvider>().feildDemographicsValues,
        );
  }
  
  void _getSavedDate() {
    if(context.read<GlobalProvider>().feildDemographicsValues.containsKey(widget.field.id)) {
      String targetDateFormat = widget.field.format ??
        "yyyy/MM/dd";
            
      String savedDate = context.read<GlobalProvider>().feildDemographicsValues[widget.field.id];
      DateTime parsedDate = DateFormat(targetDateFormat).parse(savedDate);
      _dayController.text = parsedDate.day.toString().padLeft(2, '0');
      _monthController.text = parsedDate.month.toString().padLeft(2, '0');
      _yearController.text = parsedDate.year.toString();
    }
  }

  @override
  Widget build(BuildContext context) {
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
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: [
                    Flexible(
                      child: TextFormField(
                        onTap: () => _removeFocusFromAll("day"),
                        autovalidateMode: AutovalidateMode.onUserInteraction,
                        validator: (value) {
                          return feildValidation(value, "dd");
                        },
                        onChanged: (value) {
                          if (value.length >= 2) {
                            focusNextField(dayFocus, monthFocus);
                          }
                          saveData();
                        },
                        maxLength: 2,
                        focusNode: dayFocus,
                        keyboardType: TextInputType.number,
                        controller: _dayController,
                        decoration: InputDecoration(
                          contentPadding: const EdgeInsets.symmetric(
                              vertical: 12, horizontal: 16),
                          hintStyle: const TextStyle(
                              color: Color(0xff999999), fontSize: 14),
                          counterText: "",
                          hintText: 'DD',
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(8.0),
                            borderSide: const BorderSide(
                                color: Color(0xff9B9B9F), width: 1),
                          ),
                        ),
                      ),
                    ),
                    const SizedBox(width: 8.0),
                    Flexible(
                      child: TextFormField(
                        onTap: () => _removeFocusFromAll("month"),
                        autovalidateMode: AutovalidateMode.onUserInteraction,
                        validator: (value) {
                          return feildValidation(value, "MM");
                        },
                        onChanged: (value) {
                          if (value.length >= 2) {
                            focusNextField(monthFocus, yearFocus);
                          }
                          saveData();
                        },
                        maxLength: 2,
                        focusNode: monthFocus,
                        keyboardType: TextInputType.number,
                        controller: _monthController,
                        decoration: InputDecoration(
                          contentPadding: const EdgeInsets.symmetric(
                              vertical: 12, horizontal: 16),
                          hintStyle: const TextStyle(
                              color: Color(0xff999999), fontSize: 14),
                          counterText: "",
                          hintText: 'MM',
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(8.0),
                            borderSide: const BorderSide(
                                color: Color(0xff9B9B9F), width: 1),
                          ),
                        ),
                      ),
                    ),
                    const SizedBox(width: 8.0),
                    Flexible(
                      child: TextFormField(
                        validator: (value) {
                          return feildValidation(value, "yyyy");
                        },
                        onChanged: (value) {
                          saveData();
                        },
                        onTap: () => _removeFocusFromAll("year"),
                        autovalidateMode: AutovalidateMode.onUserInteraction,
                        maxLength: 4,
                        focusNode: yearFocus,
                        controller: _yearController,
                        keyboardType: TextInputType.number,
                        decoration: InputDecoration(
                          contentPadding: const EdgeInsets.symmetric(
                              vertical: 12, horizontal: 16),
                          hintStyle: const TextStyle(
                              color: Color(0xff999999), fontSize: 14),
                          counterText: "",
                          hintText: 'YYYY',
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(8.0),
                            borderSide: const BorderSide(
                                color: Color(0xff9B9B9F), width: 1),
                          ),
                        ),
                      ),
                    ),
                    const SizedBox(width: 12),
                    const Text("OR"),
                    const SizedBox(width: 12),
                    Flexible(
                      child: TextFormField(
                        controller: _ageController,
                        keyboardType: TextInputType.number,
                        decoration: InputDecoration(
                          contentPadding: const EdgeInsets.symmetric(
                              vertical: 12, horizontal: 16),
                          hintStyle: const TextStyle(
                              color: Color(0xff999999), fontSize: 14),
                          hintText: 'Age',
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(8.0),
                            borderSide: const BorderSide(
                                color: Color(0xff9B9B9F), width: 1),
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(
                  height: 5,
                ),
              ],
            )
          ],
        ),
      ),
    );
  }
}
