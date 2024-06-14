import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../provider/export_packet_provider.dart';

class SearchBoxExport extends StatelessWidget {
  const SearchBoxExport({super.key});

  @override
  Widget build(BuildContext context) {
    return  Container(
      height: 60,
      color: Colors.white,
      child: TextField(
        onTapOutside: (pointer){FocusScope.of(context).unfocus();},
        decoration:  const InputDecoration(
          labelText:  "Search by Application ID",
          prefixIcon: Icon(Icons.search),
          border: OutlineInputBorder(),
        ),
        onChanged: (value) {
          context.read<ExportPacketsProvider>().setSearchList(value);
          context.read<ExportPacketsProvider>().searchedList();
        },
      ),
    );
  }
}
