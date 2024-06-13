import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../provider/export_packet_provider.dart';
import '../../../utils/app_config.dart';

class UploadButton extends StatelessWidget {
  const UploadButton({super.key});

  @override
  Widget build(BuildContext context) {
    return OutlinedButton(
      onPressed:(context.watch<ExportPacketsProvider>().countSelected==0)?null: () {
        context.read<ExportPacketsProvider>().uploadSelected();
      },
      style: OutlinedButton.styleFrom(side:(context.watch<ExportPacketsProvider>().countSelected==0)?const BorderSide(width: 1.5, color: Colors.grey): BorderSide(width: 1.5, color: solidPrimary), backgroundColor: Colors.white),
      child: SizedBox(
        height: 60,
        child:  Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.cloud_upload_outlined, size: 28,),
            const SizedBox(width: 6,),
            Text('UPLOAD', style:Theme.of(context).textTheme.titleLarge?.copyWith(color: (context.watch<ExportPacketsProvider>().countSelected==0)? Colors.grey :  solidPrimary, fontSize: 17),),
          ],
        ),
      ),
    );
  }
}
