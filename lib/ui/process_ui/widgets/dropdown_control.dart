import 'package:flutter/material.dart';

class DropDownControl extends StatefulWidget {
  const DropDownControl({
    super.key,
    required this.onChanged,
  });

  final Function(String) onChanged;

  @override
  State<DropDownControl> createState() => _CustomDropDownState();
}

class _CustomDropDownState extends State<DropDownControl> {
  String selected = "Select feild";

  @override
  Widget build(BuildContext context) {
    final options = [""];

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
          hintStyle: const TextStyle(color: Color(0xff999999)),
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
