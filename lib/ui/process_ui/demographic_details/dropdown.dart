import 'package:flutter/material.dart';

class CustomDropDown extends StatefulWidget {
  const CustomDropDown({
    super.key,
    required this.onChanged,
  });

  final Function(String) onChanged;

  @override
  State<CustomDropDown> createState() => _CustomDropDownState();
}

class _CustomDropDownState extends State<CustomDropDown> {
  String selected = "";

  @override
  Widget build(BuildContext context) {
    final options = ["Data1", "Data2", "Data3"];

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16.0),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(8.0),
        border: Border.all(
          color: Colors.grey,
          width: 1.0,
        ),
      ),
      child: DropdownButtonFormField(
        icon: Icon(null),
        decoration: InputDecoration(
          border: InputBorder.none,
          hintText: selected,
        ),
        items: options
            .map((option) => DropdownMenuItem(
                  value: option,
                  child: Text(option),
                ))
            .toList(),
        onChanged: (value) {
          widget.onChanged(value.toString());
          setState(() {
            selected = value!;
          });
        },
      ),
    );
  }
}
