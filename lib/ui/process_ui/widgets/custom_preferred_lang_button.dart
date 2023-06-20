import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/utils/app_config.dart';

class CustomPreferredLangButton extends StatelessWidget {
  const CustomPreferredLangButton({super.key, required this.feild});
  final Field feild;

  @override
  Widget build(BuildContext context) {
    return Card(
      color: pure_white,
      margin: EdgeInsets.fromLTRB(16, 8, 16, 8),
      child: Padding(
        padding: const EdgeInsets.fromLTRB(12, 14, 12, 14),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              context.read<GlobalProvider>().chooseLanguage(feild.label!),
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                  fontSize: 14, color: black_shade_1, fontWeight: semiBold),
            ),
            SizedBox(
              height: 15,
            ),
            Row(
              children: [
                ...feild.label!.keys.map(
                  (e) => Row(
                    children: [
                      SizedBox(
                          height: 20,
                          width: 20,
                          child: Checkbox(
                            value: false,
                            onChanged: (value) {},
                          )),
                      SizedBox(
                        width: 5,
                      ),
                      Text(e),
                      SizedBox(
                        width: 37,
                      ),
                    ],
                  ),
                ),
              ],
            )
          ],
        ),
      ),
    );
  }
}
