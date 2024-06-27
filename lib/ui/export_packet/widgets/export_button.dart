import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../provider/export_packet_provider.dart';
import '../../../utils/app_config.dart';

class ExportButton extends StatelessWidget {
  const ExportButton({super.key});

  @override
  Widget build(BuildContext context) {
    return OutlinedButton(
      onPressed: () {
        context.read<ExportPacketsProvider>().exportSelected();
        log(context.read<ExportPacketsProvider>().countSelected.toString());
      },
      style: OutlinedButton.styleFrom(side: BorderSide(width: 1.5, color: solidPrimary),backgroundColor: Colors.white),
      child: SizedBox(
        height: 60,
        child:  Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.upload_sharp, size: 26,),
            const SizedBox(width: 4,),
            Text('EXPORT', style:Theme.of(context).textTheme.titleLarge?.copyWith(color: solidPrimary, fontSize: 17),),
          ],
        ),
      ),
    );
  }
}
