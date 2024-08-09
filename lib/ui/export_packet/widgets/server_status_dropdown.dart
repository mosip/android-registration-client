import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../provider/export_packet_provider.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class ServerStatusDropdown extends StatelessWidget {
  const ServerStatusDropdown({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 56,
      decoration: BoxDecoration(
        border: Border.all(color: Colors.grey), // Add border
        borderRadius: BorderRadius.circular(8.0),
      ),

      child:DropdownButton<String>(
        value: context.watch<ExportPacketsProvider>().serverStatus,
        elevation: 1,
        style: Theme.of(context).textTheme.titleLarge?.copyWith(fontSize: 16),
        underline: const SizedBox.shrink(),
        dropdownColor: Colors.white,
        padding: const EdgeInsets.all(16),
        items:   [
          DropdownMenuItem(value: null, child: Text(AppLocalizations.of(context)!.server_status)),
          const DropdownMenuItem(value: "Packet has reached Packet Receiver", child: Text("Received")),
          const DropdownMenuItem(value: "Processing", child: Text("Processing")),
          const DropdownMenuItem(value: "Accepted", child: Text("Accepted")),
          const DropdownMenuItem(value: "Considered for deletion", child: Text("Deletion")),
        ],
        onChanged: (String? newValue) {
          context.read<ExportPacketsProvider>().changeServerStatus(newValue);
          context.read<ExportPacketsProvider>().filterSearchList();
        },
      ),
    );
  }
}
