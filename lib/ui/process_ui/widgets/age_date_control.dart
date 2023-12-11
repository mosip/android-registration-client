import 'dart:developer';

import 'package:flutter/material.dart';

import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/pigeon/biometrics_pigeon.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/app_style.dart';

import '../../../model/field.dart';
import '../../../provider/global_provider.dart';
import 'custom_cupertino_picker.dart';
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
  TextEditingController dateController = TextEditingController();
  TextEditingController ageController = TextEditingController();

  @override
  void initState() {
    _getSavedDate();
    super.initState();
  }

  int calculateYearDifference(DateTime date1, DateTime date2) {
    int yearDifference = date2.year - date1.year;
    if (date1.month > date2.month ||
        (date1.month == date2.month && date1.day > date2.day)) {
      yearDifference--;
    }
    return yearDifference;
  }

  _calculateAgeFromDOB() {
    String dateString = dateController.text;
    DateTime date = DateTime.parse(dateString.replaceAll("/", "-"));
    DateTime currentDate = DateTime.now();
    if (date.compareTo(currentDate) < 0) {
      ageController.text =
          calculateYearDifference(date, currentDate).abs().toString();
    } else {
      ageController.text = "";
    }
  }

  void saveData() {
    if (dateController.text == "") {
      return;
    }
    List<String> date = dateController.text.split("/");
    print(date.toString());
    String targetDateString = widget.field.format ??
        "yyyy/MM/dd"
            .replaceAll('dd', date[0])
            .replaceAll('MM', date[1])
            .replaceAll('yyyy', date[2]);

    context.read<RegistrationTaskProvider>().setDateField(
          widget.field.id ?? "",
          widget.field.subType ?? "",
          date[0],
          date[1],
          date[2],
        );
    context.read<GlobalProvider>().setInputMapValue(
          widget.field.id!,
          targetDateString,
          context.read<GlobalProvider>().fieldInputValue,
        );
    BiometricsApi().getAgeGroup().then((value) {
      context.read<GlobalProvider>().ageGroup = value;
    });
  }

  void _getSavedDate() {
    if (context
        .read<GlobalProvider>()
        .fieldInputValue
        .containsKey(widget.field.id)) {
      String targetDateFormat = widget.field.format ?? "yyyy/MM/dd";

      String savedDate =
          context.read<GlobalProvider>().fieldInputValue[widget.field.id];
      DateTime parsedDate = DateFormat(targetDateFormat).parse(savedDate);
      setState(() {
        dateController.text = savedDate;
        ageController.text = calculateYearDifference(parsedDate, DateTime.now())
            .abs()
            .toString();
      });
    }
  }

  void _getDateFromAge(String value) {
    int age = int.parse(value);
    DateTime currentDate = DateTime.now();
    DateTime calculatedDate = DateTime(currentDate.year - age, 1, 1);
    setState(() {
      dateController.text = DateFormat(widget.field.format ?? "yyyy/MM/dd")
          .format(calculatedDate);
    });
  }

  void showBottomPopup(BuildContext context) {
    print("button pressed");
    showBottomSheet(
        backgroundColor: Colors.grey.shade100,
        context: context,
        enableDrag: true,
        elevation: 5,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
        builder: (context) {
          return Padding(
            padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 0),
            child: SizedBox(
              height: MediaQuery.of(context).size.height / 2,
              child: CustomCupertinoDatePicker(
                maxDate: DateTime.now(),
                minDate: DateTime(1920),
                itemExtent: 75,
                diameterRatio: 10,
                selectionOverlay: Container(
                  width: double.infinity,
                  height: 50,
                  decoration: const BoxDecoration(
                    border: Border.symmetric(
                      horizontal: BorderSide(color: Colors.grey, width: 0.25),
                    ),
                  ),
                ),
                selectedStyle: const TextStyle(
                  color: Colors.black54,
                  fontWeight: FontWeight.w600,
                  fontSize: 24,
                ),
                unselectedStyle: TextStyle(
                  color: Colors.grey[800],
                  fontSize: 18,
                ),
                disabledStyle: TextStyle(
                  color: Colors.grey[400],
                  fontSize: 18,
                ),
                onSelectedItemChanged: (selectedDate) {
                  String targetDateString = widget.field.format ??
                      "yyyy/MM/dd"
                          .replaceAll(
                              'dd', selectedDate.day.toString().padLeft(2, "0"))
                          .replaceAll('MM',
                              selectedDate.month.toString().padLeft(2, "0"))
                          .replaceAll('yyyy', selectedDate.year.toString());
                  setState(() {
                    dateController.text = targetDateString;
                  });
                  _calculateAgeFromDOB();
                  saveData();
                },
              ),
            ),
          );
        });
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
            CustomLabel(field: widget.field),
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
                      flex: 3,
                      child: TextFormField(
                        readOnly: true,
                        controller: dateController,
                        onTap: (() {
                          showBottomPopup(context);
                        }),
                        textAlign: TextAlign.center,
                        decoration: InputDecoration(
                          contentPadding: const EdgeInsets.symmetric(
                              vertical: 12, horizontal: 16),
                          hintStyle: const TextStyle(
                            color: AppStyle.appBlackShade3,
                            fontSize: 14,
                          ),
                          hintText: widget.field.format ?? "yyyy/MM/dd",
                          prefixIcon: Icon(
                            Icons.calendar_month,
                            color: solidPrimary,
                          ),
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
                      flex: 1,
                      child: TextFormField(
                        controller: ageController,
                        keyboardType: TextInputType.number,
                        onChanged: (value) {
                          if (value != "") {
                            _getDateFromAge(value);
                            saveData();
                          } else {
                            dateController.text = "";
                          }
                        },
                        decoration: InputDecoration(
                          contentPadding: const EdgeInsets.symmetric(
                              vertical: 12, horizontal: 16),
                          hintStyle: const TextStyle(
                              color: AppStyle.appBlackShade3, fontSize: 14),
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
