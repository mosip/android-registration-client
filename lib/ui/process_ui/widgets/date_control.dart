import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/ui/process_ui/widgets/custom_label.dart';
import 'package:registration_client/utils/app_style.dart';


class DateControl extends StatefulWidget {
  const DateControl({super.key, required this.field, required this.validation});
  final Field  field;
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

  void _selectDate() async {
    pickedDate = await showDatePicker(
        context: context,
        initialDate: DateTime.now(),
        firstDate: DateTime(DateTime.now().year - 100) ,
        lastDate: DateTime.now());

    if (pickedDate != null) {
      DateFormat dateFormat = DateFormat("yyyy/MM/dd");
      dateController.text = dateFormat.format(pickedDate!).toString();
    }
    _saveData(dateController.text);
    _saveDataToMap(dateController.text);
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
      response = context.read<GlobalProvider>().fieldInputValue[widget.field.id];
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
                  if (!widget.field.required! && widget.field.requiredOn!.isEmpty) {
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
