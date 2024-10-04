import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../provider/export_packet_provider.dart';
import '../../../utils/constants.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class ClientStatusDropdown extends StatelessWidget {
  const ClientStatusDropdown({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
        height: 56,
        decoration: BoxDecoration(
          border: Border.all(color: Colors.grey), // Add border
          borderRadius: BorderRadius.circular(8.0), // Rounded corners
        ),
        child: DropdownButton<String>(
          value: context.watch<ExportPacketsProvider>().clientStatus,
          elevation: 1,
          style: Theme.of(context).textTheme.titleLarge?.copyWith(fontSize: 16),
          underline: const SizedBox.shrink(),
          dropdownColor: Colors.white,
          padding: const EdgeInsets.all(16),
          items:  [
             DropdownMenuItem(value: null, child: Text(AppLocalizations.of(context)!.client_status)),
            DropdownMenuItem(value: ClientStatus.CREATED.name, child: const Text("Created")),
            DropdownMenuItem(value: ClientStatus.APPROVED.name, child: const Text("Approved")),
            DropdownMenuItem(value: ClientStatus.REJECTED.name, child: const Text("Rejected")),
            DropdownMenuItem(value: ClientStatus.SYNCED.name, child: const Text("Synced")),
            DropdownMenuItem(value: ClientStatus.EXPORTED.name, child: const Text("Exported")),
            // Add more items as needed
          ],
          onChanged: (String? newValue) {
            context.read<ExportPacketsProvider>().changeClientStatus(newValue);
            context.read<ExportPacketsProvider>().filterSearchList();
          },
        )
    );
  }
}
