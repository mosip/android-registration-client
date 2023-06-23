import 'package:flutter/material.dart';
import 'package:registration_client/utils/app_config.dart';

class RadioFormField extends StatefulWidget {
  const RadioFormField(
      {super.key, required this.values, required this.onChanged});

  final List<String> values;
  final Function(String) onChanged;

  @override
  _RadioFormFieldState createState() => _RadioFormFieldState();
}

class _RadioFormFieldState extends State<RadioFormField> {
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
                  onChanged: handleOptionChange,
                ),
                Text(e)
              ],
            ),
          )
          .toList(),

      // [

      //   const SizedBox(
      //     width: 10,
      //   ),
      //   Row(
      //     children: [
      //       Radio<String>(
      //         activeColor: solid_primary,
      //         value: 'male',
      //         groupValue: selectedOption,
      //         onChanged: handleOptionChange,
      //       ),
      //       const Text('Male')
      //     ],
      //   ),
      //   const SizedBox(
      //     width: 10,
      //   ),
      //   Row(
      //     children: [
      //       Radio<String>(
      //         activeColor: solid_primary,
      //         value: 'other',
      //         groupValue: selectedOption,
      //         onChanged: handleOptionChange,
      //       ),
      //       const Text('Others')
      //     ],
      //   ),
      // ],
    );
  }
}
