import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/provider/global_provider.dart';
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
                            .fieldInputValues
                            .containsKey(field.id))
                        ? context
                            .watch<GlobalProvider>()
                            .fieldInputValues[field.id]
                        : false,
                    onChanged: (value) {
                      context
                          .read<GlobalProvider>()
                          .setInputMapValue(field.id!, value);
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
