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
  //bool isChecked = false;

  @override
  Widget build(BuildContext context) {
    GlobalProvider globalProvider =
        Provider.of<GlobalProvider>(context, listen: false);
    RegistrationTaskProvider registrationTaskProvider =
        Provider.of<RegistrationTaskProvider>(context, listen: false);
    bool isPortrait =
        MediaQuery.of(context).orientation == Orientation.portrait;
    return Card(
      color: pureWhite,
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
                     // isChecked = value!;
                      if (value == true) {
                        globalProvider.setInputMapValue(widget.field.id!, value,
                            globalProvider.fieldInputValue);
                      } else {
                        globalProvider.fieldInputValue.remove(widget.field.id!);
                        setState(() {});
                      }
                      registrationTaskProvider
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
                      text: globalProvider
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
                      globalProvider
                          .chooseLanguage(widget.field.label!),
                    ),
            ),
          ],
        ),
      ),
    );
  }
}
