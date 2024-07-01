import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/ui/export_packet/widgets/export_button.dart';
import 'package:registration_client/ui/export_packet/widgets/search_box.dart';
import 'package:registration_client/ui/export_packet/widgets/export_table.dart';
import 'package:registration_client/ui/export_packet/widgets/server_status_dropdown.dart';
import 'package:registration_client/ui/export_packet/widgets/upload_button.dart';
import 'package:registration_client/utils/app_config.dart';


import '../../provider/export_packet_provider.dart';
import 'widgets/clear_dropdown_filter.dart';
import 'widgets/client_status_dropdown.dart';

class ExportPacketsPage extends StatelessWidget {
  const ExportPacketsPage({super.key});

  @override
  Widget build(BuildContext context) {
    
    return ChangeNotifierProvider(
      create: (context) => ExportPacketsProvider(),
      builder:(context, _) => Scaffold(
        appBar: AppBar(
          elevation: 0,
          toolbarHeight: 75,
          leadingWidth: 80,
          leading: Container(
            margin: const EdgeInsets.all(14),
            child: ElevatedButton(
              style: ElevatedButton.styleFrom(backgroundColor: Colors.white.withOpacity(0.2), padding: const EdgeInsets.all(4)),
              child: const Icon(Icons.arrow_back, size: 32,),
              onPressed: (){
                Navigator.of(context).pop();
              },
            ),
          ),
          title: const Text('Manage Applications'),
        ),
        backgroundColor: backgroundColor,
        body: Padding(
          padding: const EdgeInsets.only(left: 20, right: 20, top: 16, bottom: 0),
          child: Column(
            children: [
              const SizedBox(height: 4,),
              const Row(
                children: [
                  Flexible(
                    flex: 2,
                    child: SearchBoxExport(),
                  ),
                  SizedBox(width: 10),
                  Flexible(
                    flex: 1,
                    child: UploadButton(),
                  ),
                  SizedBox(width: 10), // Add spacing between buttons
                  Flexible(
                    flex: 1,
                    child: ExportButton(),
                  ),
                ],
              ),
              const SizedBox(height: 24,),
              SizedBox(
                height: 100,
                child: Card(
                  margin: EdgeInsets.zero,
                  child: Padding(
                  padding: const EdgeInsets.symmetric(vertical: 16, horizontal: 24),
                  child:
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        context.watch<ExportPacketsProvider>().countSelected>0
                            ? Text("${context.watch<ExportPacketsProvider>().countSelected}/${context.watch<ExportPacketsProvider>().matchingPackets.length} Applications Selected", style: Theme.of(context).textTheme.titleLarge?.copyWith(fontSize: 20),)
                            : Text("Displaying ${context.watch<ExportPacketsProvider>().matchingPackets.length} Applications", style: Theme.of(context).textTheme.titleLarge?.copyWith(fontSize: 20),),
                        const Row(
                          children: [
                            ClientStatusDropdown(),
                            SizedBox(width: 16,),
                            ServerStatusDropdown(),
                            SizedBox(width: 10,),
                            ClearDropdownFilter(),
                          ],
                        ),
                      ],
                    ),
                  ),
                ),
              ),
              const SizedBox(height: 8,),
              const Expanded(
                child: Card(
                  margin: EdgeInsets.zero,
                  elevation: 2,
                  child: SingleChildScrollView(
                    scrollDirection: Axis.horizontal,
                    child: ExportTable(),
                  ),
                ),
              )
            ],
          ),
        ),
      ),
    );
  }
}