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

  void _removeFocusFromAll() {
    dayFocus.unfocus();
    monthFocus.unfocus();
    yearFocus.unfocus();
  }

  @override
  Widget build(BuildContext context) {
    final DateFormat formatter = DateFormat(widget.format);

    return Row(
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Flexible(
          child: TextFormField(
            onTap: () => _removeFocusFromAll(),
            validator: (value) {
              try {
                String date = formatter.format(DateTime.parse(
                    "${_yearController.text}-${_monthController.text}-${_dayController.text}"));
                if (value == null || value.isEmpty) {
                  return 'Please enter a value';
                }
                if (!widget.validation.hasMatch(date)) {
                  return widget.format;
                }
                context
                    .read<GlobalProvider>()
                    .setInputMapValue(widget.id, value);
                return widget.format;
              } catch (e) {
                return null;
              }
            },
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
            onTap: () => _removeFocusFromAll(),
            validator: (value) {
              try {
                String date = formatter.format(DateTime.parse(
                    "${_yearController.text}-${_monthController.text}-${_dayController.text}"));
                if (value == null || value.isEmpty) {
                  return 'Please enter a value';
                }
                if (!widget.validation.hasMatch(date)) {
                  return widget.format;
                }
                context
                    .read<GlobalProvider>()
                    .setInputMapValue(widget.id, value);
                return widget.format;
              } catch (e) {
                return null;
              }
            },
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
            onTap: () => _removeFocusFromAll(),
            validator: (value) {
              try {
                String date = formatter.format(DateTime.parse(
                    "${_yearController.text}-${_monthController.text}-${_dayController.text}"));
                if (value == null || value.isEmpty) {
                  return 'Please enter a value';
                }
                if (!widget.validation.hasMatch(date)) {
                  return widget.format;
                }
                context
                    .read<GlobalProvider>()
                    .setInputMapValue(widget.id, value);
                return widget.format;
              } catch (e) {
                return null;
              }
            },
            onChanged: (value) {
              if (value.length >= 4) {
                yearFocus.unfocus();
              }
            },
            maxLength: 4,
            focusNode: yearFocus,
            controller: _yearController,
            keyboardType: TextInputType.number,
            decoration: InputDecoration(
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
    );
  }
}
