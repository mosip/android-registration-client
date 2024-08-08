import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../provider/export_packet_provider.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class SearchBoxExport extends StatelessWidget {
  const SearchBoxExport({super.key});

  @override
  Widget build(BuildContext context) {
    return  Container(
      height: 60,
      color: Colors.white,
      child: TextField(
        onTapOutside: (pointer){FocusScope.of(context).unfocus();},
        decoration:   InputDecoration(
          labelText:  AppLocalizations.of(context)!.search_application,
          prefixIcon: const Icon(Icons.search),
          border: const OutlineInputBorder(),
        ),
        onChanged: (value) {
          context.read<ExportPacketsProvider>().setSearchList(value);
          context.read<ExportPacketsProvider>().searchedList();
        },
      ),
    );
  }
}
