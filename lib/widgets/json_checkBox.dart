/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:registration_client/data/models/field.dart';
import 'package:registration_client/demographic_details_view.dart';
import 'package:registration_client/provider/global_provider.dart';

import 'package:provider/provider.dart';

bool checkbox = false;

class JsonCheckBox extends StatefulWidget {
  const JsonCheckBox({super.key, required this.data});
  final Field data;

  @override
  State<JsonCheckBox> createState() => _JsonCheckBoxState();
}

class _JsonCheckBoxState extends State<JsonCheckBox> {
  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      mainAxisAlignment: MainAxisAlignment.end,
      children: [
        Container(
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Checkbox(
                  activeColor: primaryColor,
                  value: checkbox,
                  onChanged: (value) {
                    setState(() {
                      checkbox = !checkbox;
                    });
                  },
                ),
                Flexible(
                  child: Padding(
                    padding: EdgeInsets.fromLTRB(0,12,0,0),
                    child: Text(
                      "${context.read<GlobalProvider>().chooseLanguage(widget.data.label!)}",
                      style: Theme.of(context)
                          .textTheme
                          .titleLarge
                          ?.copyWith(fontWeight: FontWeight.w500),
                    ),
                  ),
                )
              ],
            ),
          ),
        ),
      ],
    );
  }
}
