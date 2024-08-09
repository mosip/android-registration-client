import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:provider/provider.dart';

import '../../../provider/approve_packets_provider.dart';

class SearchBoxApprove extends StatelessWidget {
  const SearchBoxApprove({super.key});

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
          context.read<ApprovePacketsProvider>().setSearchList(value);
          context.read<ApprovePacketsProvider>().searchedList();
        },
      ),
    );
  }
}
