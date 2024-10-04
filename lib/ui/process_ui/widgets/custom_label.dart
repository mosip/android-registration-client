/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/utils/app_config.dart';

import '../../../model/field.dart';
import '../../../provider/global_provider.dart';

class CustomLabel extends StatelessWidget {
  final Field field;

  const CustomLabel({super.key, required this.field});

  @override
  Widget build(BuildContext context) {
    bool isPortrait =
        MediaQuery.of(context).orientation == Orientation.portrait;
    GlobalProvider globalProvider =
        Provider.of<GlobalProvider>(context, listen: false);
    return SingleChildScrollView(
      scrollDirection: Axis.horizontal,
      child: Row(
        children: [
          Text(
            globalProvider.chooseLanguage(field.label!),
            style: TextStyle(
                fontSize: isPortrait && !isMobileSize ? 20 : 18,
                fontWeight: semiBold),
          ),
          const SizedBox(
            width: 5,
          ),
          if (field.required! ||
              (field.requiredOn != null &&
                  field.requiredOn!.isNotEmpty &&
                  (globalProvider.mvelRequiredFields[field.id] ?? false)))
            Text(
              "*",
              style: TextStyle(
                  color: Colors.red,
                  fontSize: isPortrait && !isMobileSize ? 18 : 14),
            )
        ],
      ),
    );
  }
}
