import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/provider/approve_packets_provider.dart';
import 'package:registration_client/provider/global_provider.dart';

import '../../provider/connectivity_provider.dart';
import '../../provider/registration_task_provider.dart';
import '../../utils/app_config.dart';
import 'widget/approve_table.dart';
import 'widget/authenticate_dialogbox.dart';
import 'widget/search_box.dart';

class ApprovePacketsPage extends StatefulWidget {
  const ApprovePacketsPage({super.key});

  @override
  State<ApprovePacketsPage> createState() => _ApprovePacketsPageState();
}

class _ApprovePacketsPageState extends State<ApprovePacketsPage> {
  late ConnectivityProvider connectivityProvider;
  late AppLocalizations appLocalizations = AppLocalizations.of(context)!;

  @override
  void initState() {
    connectivityProvider =
        Provider.of<ConnectivityProvider>(context, listen: false);
    context.read<ApprovePacketsProvider>().getPackets();
    GlobalProvider globalProvider =
        Provider.of<GlobalProvider>(context, listen: false);
    context
        .read<ApprovePacketsProvider>()
        .getAllReasonList(globalProvider.selectedLanguage);
    super.initState();
  }

  void _showInSnackBar(String value) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(value),
      ),
    );
  }

  Widget submitButton() {
    return ElevatedButton(
      onPressed: (context.read<ApprovePacketsProvider>().countSelected == 0)
          ? null
          : () async {
              await connectivityProvider.checkNetworkConnection();
              if (!connectivityProvider.isConnected) {
                _showInSnackBar(appLocalizations.network_error);
                return;
              }
              showDialog(
                  context: context,
                  builder: (BuildContext context) {
                    return AuthenticateDialogBox();
                  });
            },
      style: ElevatedButton.styleFrom(
        backgroundColor: solidPrimary,
        minimumSize: const Size(double.infinity, 60),
        padding: const EdgeInsets.symmetric(horizontal: 4),
      ),
      child: Center(
        child: FittedBox(
          fit: BoxFit.scaleDown,
          child: Text(
            appLocalizations.authenticate,
            textAlign: TextAlign.center,
            style: Theme.of(context)
                .textTheme
                .titleLarge
                ?.copyWith(color: Colors.white, fontSize: 17),
          ),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
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
        title: Text(AppLocalizations.of(context)!.pending_approval),
      ),
      backgroundColor: backgroundColor,
      body: Padding(
        padding:
            const EdgeInsets.only(left: 20, right: 20, top: 16, bottom: 12),
        child: Column(
          children: [
            const SizedBox(
              height: 4,
            ),
            Row(
              children: [
                Flexible(
                  flex: isMobileSize ? 2 : 3,
                  child: const SearchBoxApprove(),
                ),
                const SizedBox(width: 10),
                Flexible(
                  flex: 1,
                  child: submitButton(),
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
                  padding:
                      const EdgeInsets.symmetric(vertical: 16, horizontal: 24),
                  child: SingleChildScrollView(
                    scrollDirection: Axis.horizontal,
                    child: ConstrainedBox(
                      constraints: BoxConstraints(
                          minWidth: MediaQuery.of(context)!.size.width - 90),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          context
                                      .watch<ApprovePacketsProvider>()
                                      .countSelected >
                                  0
                              ? Text(
                                  AppLocalizations.of(context)!
                                      .total_selected_application(
                                          context
                                              .watch<ApprovePacketsProvider>()
                                              .countSelected,
                                          context
                                              .watch<ApprovePacketsProvider>()
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
                                          .watch<ApprovePacketsProvider>()
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
            Expanded(
              child: Card(
                margin: EdgeInsets.zero,
                elevation: 2,
                child: SingleChildScrollView(
                  scrollDirection: Axis.horizontal,
                  child: ApproveTable(
                    matchingPackets:
                        context.watch<ApprovePacketsProvider>().matchingPackets,
                  ),
                ),
              ),
            )
          ],
        ),
      ),
    );
  }
}
