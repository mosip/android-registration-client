import 'package:flutter/material.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/utils/app_config.dart';

class CheckboxControl extends StatelessWidget {
  const CheckboxControl({super.key, required this.field});
  final Field field;

  @override
  Widget build(BuildContext context) {
    return Card(
      color: pure_white,
      margin: EdgeInsets.fromLTRB(16, 8, 16, 8),
      child: Padding(
        padding: const EdgeInsets.fromLTRB(12, 14, 12, 14),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            SizedBox(
                height: 20,
                width: 20,
                child: Checkbox(
                    activeColor: solid_primary,
                    value: (context
                            .watch<GlobalProvider>()
                            .feildConsentValues
                            .containsKey(field.id))
                        ? context
                            .watch<GlobalProvider>()
                            .feildConsentValues[field.id]
                        : false,
                    onChanged: (value) {
                      context.read<GlobalProvider>().setInputMapValue(
                          field.id!,
                          value,
                          context.read<GlobalProvider>().feildConsentValues);
                      context
                          .read<RegistrationTaskProvider>()
                          .addConsentField(value != null && value ? 'Y' : 'N');
                    })),
            SizedBox(
              width: 8,
            ),
            Flexible(
              // width: 300.w,
              child: Text(
                context.read<GlobalProvider>().chooseLanguage(field.label!),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
