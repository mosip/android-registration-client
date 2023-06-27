import 'package:flutter/material.dart';

class AgeDateControl extends StatefulWidget {
  const AgeDateControl({super.key, required this.validation, required this.onChanged});

  final RegExp validation;
  final Function(String) onChanged;

  @override
  State<AgeDateControl> createState() => _AgeDateControlState();
}

class _AgeDateControlState extends State<AgeDateControl> {
  final TextEditingController _dayController = TextEditingController();

  final TextEditingController _monthController = TextEditingController();

  final TextEditingController _yearController = TextEditingController();

  final TextEditingController _ageController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Flexible(
          child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 0),
            decoration: BoxDecoration(
              border: Border.all(color: const Color(0xff9B9B9F), width: 1),
              borderRadius: BorderRadius.circular(8.0),
            ),
            child: TextFormField(
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'Please enter a value';
                }
                if (!widget.validation.hasMatch(
                    "${_yearController.text}/${_monthController.text}/${_dayController.text}")) {
                  return 'DD';
                }
                return null;
              },
              onChanged: (value) => widget.onChanged(
                  "${_yearController.text}/${_monthController.text}/${_dayController.text}"),
              controller: _dayController,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(
                  hintText: 'DD', border: InputBorder.none),
            ),
          ),
        ),
        const SizedBox(width: 8.0),
        Flexible(
          child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 0),
            decoration: BoxDecoration(
              border: Border.all(color: const Color(0xff9B9B9F), width: 1),
              borderRadius: BorderRadius.circular(8.0),
            ),
            child: TextFormField(
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'Please enter a value';
                }
                if (!widget.validation.hasMatch(
                    "${_yearController.text}/${_monthController.text}/${_dayController.text}")) {
                  return 'MM';
                }
                return null;
              },
              onChanged: (value) => widget.onChanged(
                  "${_yearController.text}/${_monthController.text}/${_dayController.text}"),
              controller: _monthController,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(
                  hintText: 'MM', border: InputBorder.none),
            ),
          ),
        ),
        const SizedBox(width: 8.0),
        Flexible(
          child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 0),
            decoration: BoxDecoration(
              border: Border.all(color: const Color(0xff9B9B9F), width: 1),
              borderRadius: BorderRadius.circular(8.0),
            ),
            child: TextFormField(
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'Please enter a value';
                }
                if (!widget.validation.hasMatch(
                    "${_yearController.text}/${_monthController.text}/${_dayController.text}")) {
                  return 'YYYY';
                }
                return null;
              },
              onChanged: (value) => widget.onChanged(
                  "${_yearController.text}/${_monthController.text}/${_dayController.text}"),
              controller: _yearController,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(
                hintText: 'YYYY',
                border: InputBorder.none,
              ),
            ),
          ),
        ),
        const SizedBox(width: 12),
        const Text("OR"),
        const SizedBox(width: 12),
        Flexible(
          child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 0),
            decoration: BoxDecoration(
              border: Border.all(color: const Color(0xff9B9B9F), width: 1),
              borderRadius: BorderRadius.circular(8.0),
            ),
            child: TextFormField(
              controller: _ageController,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(
                  hintText: 'Age', border: InputBorder.none),
            ),
          ),
        ),
      ],
    );
  }
}
