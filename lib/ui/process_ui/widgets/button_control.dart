import 'package:flutter/material.dart';

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
    generateList(BuildContext context, int index) {
      List temp = List.generate(
          context.read<GlobalProvider>().fieldDisplayValues[field.id] == null
              ? 0
              : context
                  .read<GlobalProvider>()
                  .fieldDisplayValues[field.id]
                  .length,
          (index) => false);
      temp[index] = false;
      context.read<GlobalProvider>().setInputMapValue(
          field.id!, temp, context.read<GlobalProvider>().fieldInputValue);
      return false;
    }

    formList(List temp) {
      String str = "";
      for (int i = 0;
          i <
              context
                  .read<GlobalProvider>()
                  .fieldDisplayValues[field.id]
                  .length;
          i++) {
        if (temp[i] == true) {
          str = str +
              context.read<GlobalProvider>().fieldDisplayValues[field.id][i] +
              ",";
        }
      }
      return str.substring(0, str.length - 1);
    }

    return Card(
      color: pure_white,
      margin: EdgeInsets.fromLTRB(16, 8, 16, 8),
      child: Padding(
        padding: const EdgeInsets.fromLTRB(12, 14, 12, 14),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            (field.required!)
                ? RichText(
                    text: TextSpan(
                    text: context
                        .read<GlobalProvider>()
                        .chooseLanguage(field.label!),
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                        fontSize: 14,
                        color: black_shade_1,
                        fontWeight: semiBold),
                    children: [
                      TextSpan(
                        text: " *",
                        style: TextStyle(color: Colors.red, fontSize: 15),
                      )
                    ],
                  ))
                : Text(
                    context.read<GlobalProvider>().chooseLanguage(field.label!),
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                        fontSize: 14,
                        color: black_shade_1,
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
                          activeColor: solid_primary,
                          value: (context
                                  .watch<GlobalProvider>()
                                  .fieldInputValue
                                  .containsKey(field.id))
                              ? context
                                  .watch<GlobalProvider>()
                                  .fieldInputValue[field.id][i]
                              : generateList(context, i),
                          onChanged: (value) async {
                            List temp = context
                                .read<GlobalProvider>()
                                .fieldInputValue[field.id];
                            temp[i] = value;

                            context.read<GlobalProvider>().setInputMapValue(
                                field.id!,
                                temp,
                                context.read<GlobalProvider>().fieldInputValue);
                            await DemographicsApi()
                                .addDemographicField(field.id!, formList(temp));
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
