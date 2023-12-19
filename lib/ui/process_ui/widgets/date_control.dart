import 'package:flutter/material.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/ui/process_ui/widgets/custom_label.dart';
import 'package:registration_client/utils/app_style.dart';

import '../../../utils/app_config.dart';
import 'custom_cupertino_picker.dart';

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

  @override
  void initState() {
    _getDataFromMap();
    super.initState();
  }

  void showBottomPopup(BuildContext context) {
    showModalBottomSheet(
        backgroundColor: Colors.white,
        context: context,
        enableDrag: true,
        elevation: 5,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        builder: (context) {
          return SingleChildScrollView(
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
                  maxDate: DateTime.now(),
                  minDate: DateTime(1920),
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
          );
        });
  }

  void _selectDate() async {
    showBottomPopup(context);
  }

  _saveData(value) {
    context
        .read<RegistrationTaskProvider>()
        .addDemographicField(widget.field.id!, value!);
  }

  _saveDataToMap(value) {
    context.read<GlobalProvider>().setInputMapValue(
          widget.field.id!,
          value!,
          context.read<GlobalProvider>().fieldInputValue,
        );
  }

  _getDataFromMap() {
    String response = "";
    if (context
        .read<GlobalProvider>()
        .fieldInputValue
        .containsKey(widget.field.id)) {
      response =
          context.read<GlobalProvider>().fieldInputValue[widget.field.id];
    }
    setState(() {
      dateController.text = response;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 0,
      margin: const EdgeInsets.symmetric(vertical: 1, horizontal: 12),
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 20, horizontal: 12),
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
                    return 'Please enter a value';
                  }
                  if (!widget.validation.hasMatch(value)) {
                    return 'Invalid input';
                  }
                  return null;
                },
                decoration: InputDecoration(
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8.0),
                    borderSide: const BorderSide(
                        color: AppStyle.appGreyShade, width: 1),
                  ),
                  contentPadding: const EdgeInsets.symmetric(
                    vertical: 14,
                    horizontal: 16,
                  ),
                  hintText: "dd/mm/yyyy",
                  hintStyle: const TextStyle(
                    color: AppStyle.appBlackShade3,
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
