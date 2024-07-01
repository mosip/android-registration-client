
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../provider/connectivity_provider.dart';
import '../../../provider/export_packet_provider.dart';
import '../../../utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class ExportButton extends StatefulWidget {
  const ExportButton({super.key});

  @override
  State<ExportButton> createState() => _ExportButtonState();
}

class _ExportButtonState extends State<ExportButton> {
  late ConnectivityProvider connectivityProvider;
  late ExportPacketsProvider exportPacketsProvider;
  late AppLocalizations appLocalizations = AppLocalizations.of(context)!;

  @override
  void initState() {
    connectivityProvider = Provider.of<ConnectivityProvider>(context, listen: false);
    exportPacketsProvider = Provider.of<ExportPacketsProvider>(context, listen: false);
    super.initState();
  }

  void _showInSnackBar(String value) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(value),
      ),
    );
  }


  @override
  Widget build(BuildContext context) {
    return OutlinedButton(
      onPressed: () async {
        await connectivityProvider.checkNetworkConnection();
        if (!connectivityProvider.isConnected) {
          _showInSnackBar(appLocalizations.network_error);
        }else{
          await exportPacketsProvider.packetSyncAll();
        }
        await exportPacketsProvider.exportSelected();
        exportPacketsProvider.searchedList();
      },
      style: OutlinedButton.styleFrom(side: BorderSide(width: 1.5, color: solidPrimary),backgroundColor: Colors.white),
      child: SizedBox(
        height: 60,
        child:  Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.upload_sharp, size: 26,),
            const SizedBox(width: 4,),
            Text(AppLocalizations.of(context)!.export, style:Theme.of(context).textTheme.titleLarge?.copyWith(color: solidPrimary, fontSize: 17),),
          ],
        ),
      ),
    );
  }
}
