import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/ui/export_packet/widgets/export_button.dart';
import 'package:registration_client/ui/export_packet/widgets/search_box.dart';
import 'package:registration_client/ui/export_packet/widgets/export_table.dart';
import 'package:registration_client/ui/export_packet/widgets/server_status_dropdown.dart';
import 'package:registration_client/ui/export_packet/widgets/upload_button.dart';
import 'package:registration_client/utils/app_config.dart';

import '../../provider/approve_packets_provider.dart';
import '../../provider/export_packet_provider.dart';
import '../../provider/registration_task_provider.dart';
import 'widgets/clear_dropdown_filter.dart';
import 'widgets/client_status_dropdown.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class ExportPacketsPage extends StatelessWidget {
  const ExportPacketsPage({super.key});

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (context) => ExportPacketsProvider(),
      builder: (context, _) => Scaffold(
        appBar: AppBar(
          elevation: 0,
          toolbarHeight: 75,
          leadingWidth: 80,
          leading: Container(
            margin: const EdgeInsets.all(14),
            child: ElevatedButton(
              style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.white.withOpacity(0.2),
                  padding: const EdgeInsets.all(4)),
              child: const Icon(
                Icons.arrow_back,
                size: 32,
              ),
              onPressed: () {
                context
                    .read<RegistrationTaskProvider>()
                    .getApplicationUploadNumber();
                context.read<ApprovePacketsProvider>().getTotalCreatedPackets();
                Navigator.of(context).pop();
              },
            ),
          ),
          title: Text(AppLocalizations.of(context)!.manage_applications),
        ),
        backgroundColor: backgroundColor,
        body: Padding(
          padding:
              const EdgeInsets.only(left: 20, right: 20, top: 16, bottom: 0),
          child: Column(
            children: [
              const SizedBox(
                height: 4,
              ),
              isMobileSize?Column(
                children: [
                  const Row(
                    children: [
                      Expanded(
                        child: SearchBoxExport(),
                      ),
                    ],
                  ),
                  const SizedBox(height: 10),
                  Row(
                    children: [
                      Expanded(
                        child: SizedBox(
                          height: 48,
                          child: UploadButton(),
                        ),
                      ),
                      const SizedBox(width: 10),
                      Expanded(
                        child: SizedBox(
                          height: 48,
                          child: ExportButton(),
                        ),
                      ),
                    ],
                  ),
                ],
              )
                  :const Row(
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

              const SizedBox(
                height: 24,
              ),
              SizedBox(
                height: 100,
                child: Card(
                  margin: EdgeInsets.zero,
                  child: Padding(
                    padding: const EdgeInsets.symmetric(
                        vertical: 16, horizontal: 24),
                    child: SingleChildScrollView(
                      scrollDirection: Axis.horizontal,
                      child: ConstrainedBox(
                        constraints: BoxConstraints(
                            minWidth: MediaQuery.of(context)!.size.width - 90),
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            context
                                .watch<ExportPacketsProvider>()
                                .countSelected >
                                0
                                ? Text(
                              AppLocalizations.of(context)!
                                  .total_selected_application(
                                  context
                                      .watch<ExportPacketsProvider>()
                                      .countSelected,
                                  context
                                      .watch<ExportPacketsProvider>()
                                      .matchingPackets
                                      .length),
                              style: Theme.of(context)
                                  .textTheme
                                  .titleLarge
                                  ?.copyWith(fontSize: 20),
                            )
                                : Text(
                              AppLocalizations.of(context)!
                                  .number_of_application(context
                                  .watch<ExportPacketsProvider>()
                                  .matchingPackets
                                  .length),
                              style: Theme.of(context)
                                  .textTheme
                                  .titleLarge
                                  ?.copyWith(fontSize: 20),
                            ),
                            const SizedBox(
                              width: 50,
                            ),
                            const Row(
                              children: [
                                ClientStatusDropdown(),
                                SizedBox(
                                  width: 16,
                                ),
                                ServerStatusDropdown(),
                                SizedBox(
                                  width: 8,
                                ),
                                ClearDropdownFilter(),
                              ],
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                ),
              ),
              const SizedBox(
                height: 8,
              ),
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
