/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/pigeon/biometrics_pigeon.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/utils/app_config.dart';

import '../../../model/field.dart';
import '../../../provider/global_provider.dart';
import 'custom_cupertino_picker.dart';
import 'custom_label.dart';

import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class AgeDateControl extends StatefulWidget {
  const AgeDateControl(
      {super.key, required this.validation, required this.field});

  final RegExp validation;
  final Field field;

  @override
  State<AgeDateControl> createState() => _AgeDateControlState();
}

class _AgeDateControlState extends State<AgeDateControl> {
  late GlobalProvider globalProvider;
  late RegistrationTaskProvider registrationTaskProvider;
  TextEditingController dateController = TextEditingController();
  TextEditingController ageController = TextEditingController();
  int maxAge = 150;

  @override
  void initState() {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      globalProvider = Provider.of<GlobalProvider>(context, listen: false);
      registrationTaskProvider =
          Provider.of<RegistrationTaskProvider>(context, listen: false);
      _getDOBMaxAge();
    });
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
    if (dateController.text == "") {
      return;
    }
    String dateString = dateController.text;
    DateTime date = DateFormat(widget.field.format == null ||
                widget.field.format!.toLowerCase() == "none"
            ? "yyyy/MM/dd"
            : widget.field.format)
        .parse(dateString);

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
      registrationTaskProvider.removeDemographicField(widget.field.id!);
      globalProvider.removeInputMapValue(
          widget.field.id!, globalProvider.fieldInputValue);
      return;
    }
    String dateString = dateController.text;
    DateTime date = DateFormat(widget.field.format == null ||
                widget.field.format!.toLowerCase() == "none"
            ? "yyyy/MM/dd"
            : widget.field.format)
        .parse(dateString);
    registrationTaskProvider.setDateField(
      widget.field.id ?? "",
      widget.field.subType ?? "",
      date.day.toString().padLeft(2, '0'),
      date.month.toString().padLeft(2, '0'),
      date.year.toString(),
    );
    globalProvider.setInputMapValue(
      widget.field.id!,
      dateController.text,
      globalProvider.fieldInputValue,
    );
    BiometricsApi().getAgeGroup().then((value) {
      globalProvider.ageGroup = value;
    });
  }

  _getDOBMaxAge() async {
    String maxAgeStr =
        await registrationTaskProvider.demographics.getDOBMaxAge();
    if (maxAgeStr.isNotEmpty) {
      if (!mounted) return;
      final parsedAge = int.tryParse(maxAgeStr);
      if (parsedAge == null) return;
      setState(() {
        maxAge = parsedAge;
      });
    }
  }

  void _getSavedDate() {
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    if (globalProvider.fieldInputValue.containsKey(widget.field.id)) {
      String savedDate = globalProvider.fieldInputValue[widget.field.id];
      DateTime parsedDate = DateFormat(widget.field.format == null ||
                  widget.field.format!.toLowerCase() == "none"
              ? "yyyy/MM/dd"
              : widget.field.format)
          .parse(savedDate);
      WidgetsBinding.instance.addPostFrameCallback((_) {
        setState(() {
          dateController.text = savedDate;
          ageController.text =
              calculateYearDifference(parsedDate, DateTime.now())
                  .abs()
                  .toString();
        });
      });
    }
  }

  void _getDateFromAge(String value) {
    int age = int.parse(value);
    DateTime currentDate = DateTime.now();
    DateTime calculatedDate = DateTime(currentDate.year - age, 1, 1);
    setState(() {
      dateController.text = DateFormat(widget.field.format == null ||
                  widget.field.format!.toLowerCase() == "none"
              ? "yyyy/MM/dd"
              : widget.field.format)
          .format(calculatedDate);
    });
  }

  void showBottomPopup(BuildContext context) {
    String dateString = dateController.text;
    final localMaxAge = maxAge;

    showModalBottomSheet(
        backgroundColor: Colors.white,
        context: context,
        enableDrag: true,
        elevation: 5,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        builder: (context) {
          return ListView(
            primary: false,
            physics: const NeverScrollableScrollPhysics(),
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const SizedBox(
                    width: 50,
                  ),
                  Text(
                    widget.field.label!['eng'] ?? "",
                    style: Theme.of(context)
                        .textTheme
                        .bodyMedium
                        ?.copyWith(fontWeight: FontWeight.bold),
                  ),
                  IconButton(
                    icon: const Icon(
                      Icons.close,
                    ),
                    onPressed: () => Navigator.pop(context),
                  )
                ],
              ),
              Container(
                height: 2.5,
                width: MediaQuery.of(context).size.width,
                color: solidPrimary.withOpacity(0.075),
              ),
              const SizedBox(
                height: 16,
              ),
              CustomCupertinoDatePicker(
                maxDate: DateTime.now(),
                minDate: DateTime(DateTime.now().year - localMaxAge),
                selectedDate: dateString != ""
                    ? DateFormat(widget.field.format == null ||
                                widget.field.format!.toLowerCase() == "none"
                            ? "yyyy/MM/dd"
                            : widget.field.format)
                        .parse(dateString)
                    : null,
                squeeze: 1,
                itemExtent: 50,
                diameterRatio: 10,
                selectionOverlay: Container(
                  width: double.infinity,
                  decoration: BoxDecoration(
                    color: solidPrimary.withOpacity(0.075),
                  ),
                ),
                selectedStyle: TextStyle(
                  color: solidPrimary,
                  fontWeight: FontWeight.w600,
                  fontSize: 16,
                ),
                unselectedStyle: TextStyle(
                  color: Colors.grey[800],
                  fontSize: 15,
                ),
                disabledStyle: TextStyle(
                  color: Colors.grey[400],
                  fontSize: 15,
                ),
                onSelectedItemChanged: (selectedDate) {
                  String targetDateString = (widget.field.format == null ||
                              widget.field.format!.toLowerCase() == "none"
                          ? "yyyy/MM/dd"
                          : widget.field.format!)
                      .replaceAll(
                          'dd', selectedDate.day.toString().padLeft(2, "0"))
                      .replaceAll(
                          'MM', selectedDate.month.toString().padLeft(2, "0"))
                      .replaceAll('yyyy', selectedDate.year.toString());
                  setState(() {
                    dateController.text = targetDateString;
                  });
                  _calculateAgeFromDOB();
                  saveData();
                },
              ),
            ],
          );
        });
  }

  @override
  Widget build(BuildContext context) {
    bool isPortrait =
        MediaQuery.of(context).orientation == Orientation.portrait;
    return Card(
      elevation: 5,
      color: pureWhite,
      margin: EdgeInsets.symmetric(
          vertical: 1.h, horizontal: isPortrait ? 16.w : 0),
      child: Padding(
        padding: EdgeInsets.symmetric(vertical: 24.h, horizontal: 16.w),
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
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Flexible(
                      flex: 3,
                      child: TextFormField(
                        autovalidateMode: AutovalidateMode.onUserInteraction,
                        readOnly: true,
                        controller: dateController,
                        validator: (value) {
                          if (widget.field.required == true &&
                              (value == null || value.isEmpty)) {
                            return AppLocalizations.of(context)!
                                .select_value_message;
                          }
                          if (value != null && value.isNotEmpty) {
                            try {
                              DateTime date = DateFormat(
                                      widget.field.format == null ||
                                              widget.field.format!
                                                      .toLowerCase() ==
                                                  "none"
                                          ? "yyyy/MM/dd"
                                          : widget.field.format)
                                  .parse(value);
                              int age = calculateYearDifference(
                                      date, DateTime.now())
                                  .abs();
                              if (age > maxAge) {
                                return AppLocalizations.of(context)!.age_should_not_be_greater(maxAge);
                              }
                            } catch (e) {
                              print(e);
                            }
                          }
                          return null;
                        },
                        onTap: (() {
                          showBottomPopup(context);
                        }),
                        textAlign: TextAlign.center,
                        decoration: InputDecoration(
                          contentPadding: const EdgeInsets.symmetric(
                              vertical: 14, horizontal: 16),
                          hintStyle: const TextStyle(
                            color: appBlackShade3,
                            fontSize: 14,
                          ),
                          hintText: widget.field.format == null ||
                                  widget.field.format!.toLowerCase() == "none"
                              ? ("yyyy/MM/dd").toUpperCase()
                              : widget.field.format,
                          prefixIcon: Icon(
                            Icons.calendar_month_outlined,
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
                    Padding(
                      padding: const EdgeInsets.only(top: 15),
                      child: const Text("OR"),
                    ),
                    const SizedBox(width: 12),
                    Flexible(
                      flex: 1,
                      child: TextFormField(
                        autovalidateMode: AutovalidateMode.onUserInteraction,
                        controller: ageController,
                        keyboardType: TextInputType.number,
                        validator: (value) {
                          if (widget.field.required == true && (value == null || value.isEmpty)) {
                            return AppLocalizations.of(context)!
                                .select_value_message;
                          }
                          return null;
                        },
                        onChanged: (value) async {
                          if (value != "") {
                            _getDateFromAge(value);
                          } else {
                            dateController.text = "";
                          }
                          saveData();
                        },
                        inputFormatters: [FilteringTextInputFormatter.digitsOnly],
                        decoration: InputDecoration(
                          contentPadding: const EdgeInsets.symmetric(
                              vertical: 14, horizontal: 16),
                          hintStyle: const TextStyle(
                              color: appBlackShade3, fontSize: 14),
                          hintText: '0',
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
