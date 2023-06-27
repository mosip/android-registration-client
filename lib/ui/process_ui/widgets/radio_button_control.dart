import 'package:flutter/material.dart';
import 'package:registration_client/utils/app_config.dart';

class RadioButtonControl extends StatefulWidget {
  const RadioButtonControl(
      {super.key, required this.values, required this.onChanged});

  final List<String> values;
  final Function(String) onChanged;

  @override
  _RadioFormFieldState createState() => _RadioFormFieldState();
}

class _RadioFormFieldState extends State<RadioButtonControl> {
  String? selectedOption;

  void handleOptionChange(String? value) {
    setState(() {
      selectedOption = value;
    });
    widget.onChanged(value.toString());
  }

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.start,
      children: widget.values
          .map(
            (e) => Row(
              children: [
                Radio<String>(
                  activeColor: solid_primary,
                  value: e.toLowerCase(),
                  groupValue: selectedOption,
                  onChanged: (value) {
                    widget.onChanged(value!);
                    handleOptionChange(value);
                  },
                ),
                Text(e)
              ],
            ),
          )
          .toList(),
    );
  }
}
