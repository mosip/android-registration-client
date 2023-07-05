import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/utils/app_config.dart';

class PreferredLangButtonControl extends StatelessWidget {
  const PreferredLangButtonControl({super.key, required this.field});
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
      context.read<GlobalProvider>().setInputMapValue(field.id!, temp);
      return false;
    }

    return Card(
      color: pure_white,
      margin: EdgeInsets.fromLTRB(16, 8, 16, 8),
      child: Padding(
        padding: const EdgeInsets.fromLTRB(12, 14, 12, 14),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              context.read<GlobalProvider>().chooseLanguage(field.label!),
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                  fontSize: 14, color: black_shade_1, fontWeight: semiBold),
            ),
            SizedBox(
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
                                  .fieldInputValues
                                  .containsKey(field.id))
                              ? context
                                  .read<GlobalProvider>()
                                  .fieldInputValues[field.id][i]
                              : generateList(context, i),
                          onChanged: (value) {
                            List temp = context
                                .read<GlobalProvider>()
                                .fieldInputValues[field.id];
                            temp[i] = value;
                            context
                                .read<GlobalProvider>()
                                .setInputMapValue(field.id!, temp);
                          },
                        ),
                      ),
                      SizedBox(
                        width: 5,
                      ),
                      Text(context
                          .read<GlobalProvider>()
                          .fieldDisplayValues[field.id][i]),
                      SizedBox(
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
