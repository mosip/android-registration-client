import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../provider/export_packet_provider.dart';
import '../../../utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class ClearDropdownFilter extends StatelessWidget {
  const ClearDropdownFilter({super.key});

  @override
  Widget build(BuildContext context) {
    return TextButton(
      onPressed: (){
        context.read<ExportPacketsProvider>().changeClientStatus(null);
        context.read<ExportPacketsProvider>().changeServerStatus(null);
        context.read<ExportPacketsProvider>().filterSearchList();
      },
      child:  Text(AppLocalizations.of(context)!.clear_filter ,style: Theme.of(context).textTheme.titleMedium?.copyWith(fontSize: 16, fontWeight: FontWeight.w500, color: (context.watch<ExportPacketsProvider>().serverStatus==null && context.watch<ExportPacketsProvider>().clientStatus==null)? Colors.black54: solidPrimary),),);
  }
}
