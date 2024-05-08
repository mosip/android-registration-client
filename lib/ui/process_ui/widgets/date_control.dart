/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:intl/intl.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/ui/process_ui/widgets/custom_label.dart';
import 'package:registration_client/utils/app_config.dart';

import 'custom_cupertino_picker.dart';

import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class DateControl extends StatefulWidget {
  const DateControl({super.key, required this.field, required this.validation});
  final Field field;
  final RegExp validation;

  @override
  State<DateControl> createState() => _DateControlState();
}

class _DateControlState extends State<DateControl> {
  bool isMvelValid = true;
  TextEditingController dateController = TextEditingController();
  DateTime? pickedDate;
  late GlobalProvider globalProvider;
  late RegistrationTaskProvider registrationTaskProvider;

  @override
  void initState() {
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    registrationTaskProvider =
        Provider.of<RegistrationTaskProvider>(context, listen: false);
    _getDataFromMap();
    super.initState();
  }

  void showBottomPopup(BuildContext context) {
    String dateString = dateController.text;
    showModalBottomSheet(
        backgroundColor: Colors.white,
        context: context,
        enableDrag: true,
        elevation: 5,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(30.0)),
        builder: (context) {
          return SingleChildScrollView(
            child: Container(
              decoration: const BoxDecoration(
                  color: appWhite,
                  borderRadius: BorderRadius.only(
                    topLeft: Radius.circular(30.0),
                    topRight: Radius.circular(30.0),
                  )
              ),
              child: Column(
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
                    backgroundColor: appWhite,
                    maxDate: DateTime.now(),
                    minDate: DateTime(DateTime.now().year - 125),
                    selectedDate: dateString != ""
                        ? DateFormat(widget.field.format ?? "yyyy/MM/dd")
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
                      String targetDateString = widget.field.format ??
                          "yyyy/MM/dd"
                              .replaceAll('dd',
                                  selectedDate.day.toString().padLeft(2, "0"))
                              .replaceAll('MM',
                                  selectedDate.month.toString().padLeft(2, "0"))
                              .replaceAll('yyyy', selectedDate.year.toString());
                      setState(() {
                        dateController.text = targetDateString;
                      });
                      _saveData(dateController.text);
                      _saveDataToMap(dateController.text);
                    },
                  ),
                ],
              ),
            ),
          );
        });
  }

  void _selectDate() async {
    showBottomPopup(context);
  }

  _saveData(value) {
    registrationTaskProvider
        .addDemographicField(widget.field.id!, value!);
  }

  _saveDataToMap(value) {
    globalProvider.setInputMapValue(
      widget.field.id!,
      value!,
      globalProvider.fieldInputValue,
    );
  }

  _getDataFromMap() {
    String response = "";
    if (globalProvider
        .fieldInputValue
        .containsKey(widget.field.id)) {
      response =
          globalProvider.fieldInputValue[widget.field.id];
    }
    setState(() {
      dateController.text = response;
    });
  }

  @override
  Widget build(BuildContext context) {
    bool isPortrait =
        MediaQuery.of(context).orientation == Orientation.portrait;
    return Card(
      elevation: 5,
      surfaceTintColor: transparentColor,
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
            Container(
              margin: const EdgeInsets.only(bottom: 8),
              child: TextFormField(
                autovalidateMode: AutovalidateMode.onUserInteraction,
                controller: dateController,
                readOnly: true,
                onTap: _selectDate,
                textCapitalization: TextCapitalization.words,
                textAlign: TextAlign.left,
                validator: (value) {
                  if (!widget.field.required! &&
                      widget.field.requiredOn!.isEmpty) {
                    return null;
                  }
                  if (value == null || value.isEmpty) {
                    return AppLocalizations.of(context)!
                        .select_value_message;
                  }
                  if (!widget.validation.hasMatch(value)) {
                    return AppLocalizations.of(context)!
                        .invalid_input;
                  }
                  return null;
                },
                decoration: InputDecoration(
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8.0),
                    borderSide: const BorderSide(color: appGreyShade, width: 1),
                  ),
                  contentPadding: const EdgeInsets.symmetric(
                    vertical: 14,
                    horizontal: 16,
                  ),
                  hintText: "yyyy/MM/dd",
                  hintStyle: const TextStyle(
                    color: appBlackShade3,
                    fontSize: 14,
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
