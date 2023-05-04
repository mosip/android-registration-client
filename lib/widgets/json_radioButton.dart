/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:registration_client/data/models/field.dart';
import 'package:registration_client/demographic_details_view.dart';


bool radio = true;

class JsonRadioButton extends StatefulWidget {
  const JsonRadioButton({super.key, required this.data});
  final Field data;
  

  @override
  State<JsonRadioButton> createState() => _JsonRadioButtonState();
}

class _JsonRadioButtonState extends State<JsonRadioButton> {
  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Flexible(
          child: ListTile(
            title: const Text('Value 1'),
            leading: Radio(
              activeColor: secondaryColor,
              value: true,
              groupValue: radio,
              onChanged: (value) {
                setState(() {
                  radio = true;
                });
              },
            ),
          ),
        ),
        Flexible(
          child: ListTile(
            title: const Text('Value 2'),
            leading: Radio(
              activeColor: secondaryColor,
              value: false,
              groupValue: radio,
              onChanged: (value) {
                setState(() {
                  radio = false;
                });
              },
            ),
          ),
        ),
      ],
    );
  }
}
