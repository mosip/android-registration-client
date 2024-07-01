import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../provider/connectivity_provider.dart';
import '../../../provider/export_packet_provider.dart';
import '../../../utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class UploadButton extends StatefulWidget {
  const UploadButton({super.key});

  @override
  State<UploadButton> createState() => _UploadButtonState();
}

class _UploadButtonState extends State<UploadButton> {
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
      onPressed:(context.watch<ExportPacketsProvider>().countSelected==0)?null: () async {
        await connectivityProvider.checkNetworkConnection();
        if (!connectivityProvider.isConnected) {
          _showInSnackBar(appLocalizations.network_error);
          return;
        }
        await exportPacketsProvider.packetSyncAll();
        exportPacketsProvider.uploadSelected();
      },
      style: OutlinedButton.styleFrom(side:(context.watch<ExportPacketsProvider>().countSelected==0)?const BorderSide(width: 1.5, color: Colors.grey): BorderSide(width: 1.5, color: solidPrimary), backgroundColor: Colors.white),
      child: SizedBox(
        height: 60,
        child:  Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.cloud_upload_outlined, size: 28,),
            const SizedBox(width: 6,),
            Text(AppLocalizations.of(context)!.upload, style:Theme.of(context).textTheme.titleLarge?.copyWith(color: (context.watch<ExportPacketsProvider>().countSelected==0)? Colors.grey :  solidPrimary, fontSize: 17),),
          ],
        ),
      ),
    );
  }
}
