/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/model/field.dart';

import 'package:registration_client/pigeon/demographics_data_pigeon.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/utils/app_config.dart';

class CheckboxControl extends StatefulWidget {
  const CheckboxControl({super.key, required this.field});
  final Field field;

  @override
  State<CheckboxControl> createState() => _CheckboxControlState();
}

class _CheckboxControlState extends State<CheckboxControl> {
  @override
  Widget build(BuildContext context) {
    bool isPortrait =
        MediaQuery.of(context).orientation == Orientation.portrait;
    return Card(
      color: pureWhite,
      surfaceTintColor: transparentColor,
      elevation: 5,
      margin: isPortrait
          ? EdgeInsets.fromLTRB(16.w, 8.h, 16.w, 8.h)
          : EdgeInsets.fromLTRB(0, 8.h, 0, 8.h),
      child: Padding(
        padding: const EdgeInsets.fromLTRB(12, 14, 12, 14),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            SizedBox(
                height: 20,
                width: 20,
                child: Checkbox(
                    activeColor: solidPrimary,
                    value: (context
                            .watch<GlobalProvider>()
                            .fieldInputValue
                            .containsKey(widget.field.id))
                        ? context
                            .watch<GlobalProvider>()
                            .fieldInputValue[widget.field.id]
                        : false,
                    onChanged: (value) async {
                      if (value == true) {
                        context.read<GlobalProvider>().setInputMapValue(
                            widget.field.id!,
                            value,
                            context.read<GlobalProvider>().fieldInputValue);
                      } else {
                        context
                            .read<GlobalProvider>()
                            .fieldInputValue
                            .remove(widget.field.id!);
                        setState(() {});
                      }
                      context
                          .read<RegistrationTaskProvider>()
                          .addConsentField(value != null && value ? 'Y' : 'N');
                      await DemographicsApi().addDemographicField(
                          widget.field.id!, value!.toString());
                    })),
            const SizedBox(
              width: 8,
            ),
            Flexible(
              child: (widget.field.inputRequired!)
                  ? RichText(
                      text: TextSpan(
                      text: context
                          .read<GlobalProvider>()
                          .chooseLanguage(widget.field.label!),
                      style: TextStyle(
                        color: blackShade1,
                        fontSize: isPortrait ? 18 : 14,
                      ),
                      children: const [
                        TextSpan(
                          text: " *",
                          style: TextStyle(color: Colors.red, fontSize: 14),
                        )
                      ],
                    ))
                  : Text(
                      context
                          .read<GlobalProvider>()
                          .chooseLanguage(widget.field.label!),
                    ),
            ),
          ],
        ),
      ),
    );
  }
}
