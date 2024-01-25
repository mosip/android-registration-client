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
import 'package:registration_client/utils/app_config.dart';

class ButtonControl extends StatelessWidget {
  const ButtonControl({super.key, required this.field});
  final Field field;

  @override
  Widget build(BuildContext context) {
    bool isPortrait =
        MediaQuery.of(context).orientation == Orientation.portrait;
    return Card(
      elevation: 5,
      color: pureWhite,
      margin: EdgeInsets.symmetric(vertical: 1.h, horizontal: isPortrait ? 16.w : 0),
      child: Padding(
        padding: EdgeInsets.symmetric(vertical: 24.h, horizontal: 16.w),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            (field.inputRequired!)
                ? RichText(
                    text: TextSpan(
                    text: context
                        .read<GlobalProvider>()
                        .chooseLanguage(field.label!),
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                        fontSize: 14,
                        color: blackShade1,
                        fontWeight: semiBold),
                    children: const [
                      TextSpan(
                        text: " *",
                        style: TextStyle(color: Colors.red, fontSize: 14),
                      )
                    ],
                  ))
                : Text(
                    context.read<GlobalProvider>().chooseLanguage(field.label!),
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                        fontSize: 14,
                        color: blackShade1,
                        fontWeight: semiBold),
                  ),
            const SizedBox(
              height: 15,
            ),
            Row(
              children: [
                for (int i = 0;
                    i <
                        context
                            .watch<GlobalProvider>()
                            .fieldDisplayValues[field.id]
                            .length;
                    i++)
                  Row(
                    children: [
                      SizedBox(
                        height: 20,
                        width: 20,
                        child: Checkbox(
                          activeColor: solidPrimary,
                          value: (context
                                  .watch<GlobalProvider>()
                                  .fieldInputValue
                                  .containsKey(field.id))
                              ? (context
                                          .watch<GlobalProvider>()
                                          .fieldInputValue[field.id] ==
                                      context
                                          .read<GlobalProvider>()
                                          .fieldDisplayValues[field.id][i])
                                  ? true
                                  : false
                              : false,
                          onChanged: (value) async {
                            context.read<GlobalProvider>().setInputMapValue(
                                field.id!,
                                context
                                    .read<GlobalProvider>()
                                    .fieldDisplayValues[field.id][i],
                                context.read<GlobalProvider>().fieldInputValue);

                            await DemographicsApi().addDemographicField(
                                field.id!,
                                context
                                    .read<GlobalProvider>()
                                    .fieldDisplayValues[field.id][i]);
                          },
                        ),
                      ),
                      const SizedBox(
                        width: 5,
                      ),
                      Text(context
                          .read<GlobalProvider>()
                          .fieldDisplayValues[field.id][i]),
                      const SizedBox(
                        width: 37,
                      ),
                    ],
                  ),
              ],
            )
          ],
        ),
      ),
    );
  }
}
