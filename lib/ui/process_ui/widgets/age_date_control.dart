import 'dart:developer';

import 'package:flutter/material.dart';

import 'package:intl/intl.dart';
import 'package:provider/provider.dart';

import '../../../provider/global_provider.dart';

class AgeDateControl extends StatefulWidget {
  const AgeDateControl(
      {super.key,
      required this.validation,
      required this.id,
      required this.format});

  final RegExp validation;
  final String format;
  final String id;

  @override
  State<AgeDateControl> createState() => _AgeDateControlState();
}

class _AgeDateControlState extends State<AgeDateControl> {
  bool showError = false;
  final TextEditingController _dayController = TextEditingController();

  final TextEditingController _monthController = TextEditingController();
  final TextEditingController _yearController = TextEditingController();
  final TextEditingController _ageController = TextEditingController();

  final dayFocus = FocusNode();
  final monthFocus = FocusNode();
  final yearFocus = FocusNode();

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
    setState(() {
      showError = false;
    });
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

  String? feildValidation(value) {
    try {
      String targetDateString = widget.format
          .replaceAll('dd', _dayController.text.padLeft(2, '0'))
          .replaceAll('MM', _monthController.text.padLeft(2, '0'))
          .replaceAll('yyyy', _yearController.text);

      if (value == null || value.isEmpty) {
        return 'Please enter a value';
      }
      if (!widget.validation.hasMatch(targetDateString)) {
        setState(() {
          showError = true;
        });
        return "";
      }

      setState(() {
        showError = false;
      });
      return null;
    } catch (e) {
      log("error");
      setState(() {
        showError = true;
      });
      return "";
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Flexible(
              child: TextFormField(
                onTap: () => _removeFocusFromAll("day"),
                validator: feildValidation,
                onChanged: (value) {
                  if (value.length >= 2) {
                    focusNextField(dayFocus, monthFocus);
                  }
                },
                maxLength: 2,
                focusNode: dayFocus,
                keyboardType: TextInputType.number,
                controller: _dayController,
                decoration: InputDecoration(
                  errorBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8.0),
                    borderSide:
                        const BorderSide(color: Color(0xff9B9B9F), width: 1),
                  ),
                  errorStyle: const TextStyle(fontSize: 0),
                  contentPadding:
                      const EdgeInsets.symmetric(vertical: 12, horizontal: 16),
                  hintStyle:
                      const TextStyle(color: Color(0xff999999), fontSize: 14),
                  counterText: "",
                  hintText: 'DD',
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8.0),
                    borderSide:
                        const BorderSide(color: Color(0xff9B9B9F), width: 1),
                  ),
                ),
              ),
            ),
            const SizedBox(width: 8.0),
            Flexible(
              child: TextFormField(
                onTap: () => _removeFocusFromAll("month"),
                validator: feildValidation,
                onChanged: (value) {
                  if (value.length >= 2) {
                    focusNextField(monthFocus, yearFocus);
                  }
                },
                maxLength: 2,
                focusNode: monthFocus,
                keyboardType: TextInputType.number,
                controller: _monthController,
                decoration: InputDecoration(
                  errorBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8.0),
                    borderSide:
                        const BorderSide(color: Color(0xff9B9B9F), width: 1),
                  ),
                  errorStyle: const TextStyle(fontSize: 0),
                  contentPadding:
                      const EdgeInsets.symmetric(vertical: 12, horizontal: 16),
                  hintStyle:
                      const TextStyle(color: Color(0xff999999), fontSize: 14),
                  counterText: "",
                  hintText: 'MM',
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8.0),
                    borderSide:
                        const BorderSide(color: Color(0xff9B9B9F), width: 1),
                  ),
                ),
              ),
            ),
            const SizedBox(width: 8.0),
            Flexible(
              child: TextFormField(
                onSaved: (value) {
                  String targetDateString = widget.format
                      .replaceAll('dd', _dayController.text.padLeft(2, '0'))
                      .replaceAll('MM', _monthController.text.padLeft(2, '0'))
                      .replaceAll('yyyy', _yearController.text);
                  context.read<GlobalProvider>().setInputMapValue(
                      widget.id,
                      targetDateString,
                      context.read<GlobalProvider>().feildDemographicsValues);
                },
                onTap: () => _removeFocusFromAll("year"),
                validator: feildValidation,
                maxLength: 4,
                focusNode: yearFocus,
                controller: _yearController,
                keyboardType: TextInputType.number,
                decoration: InputDecoration(
                  errorBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8.0),
                    borderSide:
                        const BorderSide(color: Color(0xff9B9B9F), width: 1),
                  ),
                  errorStyle: const TextStyle(fontSize: 0),
                  contentPadding:
                      const EdgeInsets.symmetric(vertical: 12, horizontal: 16),
                  hintStyle:
                      const TextStyle(color: Color(0xff999999), fontSize: 14),
                  counterText: "",
                  hintText: 'YYYY',
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8.0),
                    borderSide:
                        const BorderSide(color: Color(0xff9B9B9F), width: 1),
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
                  contentPadding:
                      const EdgeInsets.symmetric(vertical: 12, horizontal: 16),
                  hintStyle:
                      const TextStyle(color: Color(0xff999999), fontSize: 14),
                  hintText: 'Age',
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8.0),
                    borderSide:
                        const BorderSide(color: Color(0xff9B9B9F), width: 1),
                  ),
                ),
              ),
            ),
          ],
        ),
        const SizedBox(
          height: 5,
        ),
        showError
            ? Text(
                " Date Format: ${widget.format}",
                style: const TextStyle(
                    color: Color.fromARGB(255, 183, 21, 9), fontSize: 12),
              )
            : const SizedBox.shrink()
      ],
    );
  }
}
