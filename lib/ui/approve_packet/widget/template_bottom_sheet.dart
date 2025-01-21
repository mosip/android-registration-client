import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/constants.dart';
import 'package:webview_flutter_plus/webview_flutter_plus.dart';

import '../../../model/registration.dart';
import '../../../provider/approve_packets_provider.dart';
import 'reject_dialogbox.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class TemplateBottomSheet {
  void loadHtmlData(WebViewPlusController? controller, String packetId) async {
    log(packetId);
    const storage = FlutterSecureStorage(
      aOptions: AndroidOptions(
        encryptedSharedPreferences: true,
      ),
    );
    String? data = await storage.read(key: packetId);
    if (controller != null) {
      controller.webViewController.loadHtmlString(data ?? "No Template...");
    }
  }

  Widget bottomSheet(BuildContext context) {

    return ChangeNotifierProvider<ApprovePacketsProvider>.value(
      value: context.watch<ApprovePacketsProvider>(),
      builder: (context, _) {
        int currentInd = context.watch<ApprovePacketsProvider>().currentInd;
        Registration regCurrent = context
            .read<ApprovePacketsProvider>()
            .matchingPackets[currentInd - 1]['packet'] as Registration;
        String reviewStatus = context
            .watch<ApprovePacketsProvider>()
            .matchingPackets[currentInd - 1]['review_status'] as String;

        return Container(
          height: MediaQuery.of(context).size.height * 0.85,
          decoration: const BoxDecoration(
            borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
          ),
          child: Column(
            children: [
              Container(
                height: 4,
                width: 75,
                margin: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(28),
                  color: Colors.black26,
                ),
              ),
              Expanded(
                child: SingleChildScrollView(
                  controller: ScrollController(),
                  child: SizedBox(
                    height: isMobileSize ? 1500 : 2700,
                    child: WebViewPlus(
                      zoomEnabled: true,
                      onWebViewCreated: (controller) async {
                        context
                            .read<ApprovePacketsProvider>()
                            .setWebViewPlusController(controller);
                        loadHtmlData(controller, regCurrent.packetId);
                      },
                      javascriptMode: JavascriptMode.unrestricted,
                    ),
                  ),
                ),
              ),
              const Divider(
                height: 0,
              ),
              const SizedBox(
                height: 18,
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Card(
                    margin: const EdgeInsets.only(left: 24),
                    elevation: 0,
                    shape: CircleBorder(
                        side: BorderSide(
                      color: (currentInd > 1) ? solidPrimary : Colors.grey,
                    )),
                    child: IconButton(
                      icon: Icon(
                        Icons.arrow_back,
                        color: (currentInd > 1) ? solidPrimary : Colors.grey,
                      ),
                      onPressed: (currentInd > 1)
                          ? () {
                              context
                                  .read<ApprovePacketsProvider>()
                                  .setCurrentInd(currentInd - 1);
                              Registration reg = context
                                      .read<ApprovePacketsProvider>()
                                      .matchingPackets[(currentInd - 1) - 1]
                                  ['packet'] as Registration;
                              log(reg.packetId);
                              loadHtmlData(
                                  context
                                      .read<ApprovePacketsProvider>()
                                      .webViewPlusController,
                                  reg.packetId);
                            }
                          : () {
                              log("Out of range");
                            },
                    ),
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      ElevatedButton.icon(
                        style: ElevatedButton.styleFrom(
                          disabledForegroundColor: Colors.white,
                          disabledBackgroundColor: Colors.grey.withOpacity(0.5),
                          padding: const EdgeInsets.symmetric(
                              vertical: 12, horizontal: 18),
                        ),
                        icon: const Icon(
                          Icons.check_outlined,
                          color: Colors.white,
                        ),
                        onPressed: reviewStatus == ReviewStatus.APPROVED.name
                            ? null
                            : () {
                                context
                                    .read<ApprovePacketsProvider>()
                                    .approvePacket(regCurrent.packetId);
                                log(currentInd.toString());
                                if (currentInd <
                                    context
                                        .read<ApprovePacketsProvider>()
                                        .matchingPackets
                                        .length) {
                                  context
                                      .read<ApprovePacketsProvider>()
                                      .setCurrentInd(currentInd + 1);
                                  Registration reg = context
                                          .read<ApprovePacketsProvider>()
                                          .matchingPackets[(currentInd + 1) - 1]
                                      ['packet'] as Registration;
                                  log(reg.packetId);
                                  loadHtmlData(
                                      context
                                          .read<ApprovePacketsProvider>()
                                          .webViewPlusController,
                                      reg.packetId);
                                }
                              },
                        label: Text(AppLocalizations.of(context)!.approve),
                      ),
                      const SizedBox(
                        width: 16,
                      ),
                      OutlinedButton.icon(
                        icon: const Icon(Icons.close),
                        onPressed: reviewStatus == ReviewStatus.REJECTED.name
                            ? null
                            : () {
                                showDialog(
                                    context: context,
                                    builder: (BuildContext context) {
                                      return dialogBox(() {
                                        context
                                            .read<ApprovePacketsProvider>()
                                            .rejectPacket(regCurrent.packetId);
                                        if (currentInd <
                                            context
                                                .read<ApprovePacketsProvider>()
                                                .matchingPackets
                                                .length) {
                                          context
                                              .read<ApprovePacketsProvider>()
                                              .setCurrentInd(currentInd + 1);

                                          Registration reg = context
                                              .read<ApprovePacketsProvider>()
                                              .matchingPackets[(currentInd +
                                                  1) -
                                              1]['packet'] as Registration;
                                          log(reg.packetId);
                                          loadHtmlData(
                                              context
                                                  .read<
                                                      ApprovePacketsProvider>()
                                                  .webViewPlusController,
                                              reg.packetId);
                                        }
                                        Navigator.of(context).pop();
                                      }, context);
                                    });
                              },
                        style: OutlinedButton.styleFrom(
                          disabledForegroundColor: Colors.white,
                          disabledBackgroundColor: Colors.grey.withOpacity(0.5),
                          foregroundColor: Colors.red,
                          padding: const EdgeInsets.symmetric(
                              vertical: 12, horizontal: 18),
                          side: BorderSide(
                              color: reviewStatus == ReviewStatus.REJECTED.name
                                  ? Colors.transparent
                                  : Colors.red,
                              width: 2),
                        ),
                        label: Text(
                          AppLocalizations.of(context)!.reject,
                          style: const TextStyle(fontWeight: FontWeight.bold),
                        ),
                      ),
                      const SizedBox(
                        width: 16,
                      ),
                      Card(
                        margin: const EdgeInsets.only(right: 24),
                        elevation: 0,
                        shape: CircleBorder(
                            side: BorderSide(
                          color: reviewStatus == ReviewStatus.NOACTIONTAKEN.name
                              ? Colors.grey
                              : solidPrimary,
                        )),
                        child: IconButton(
                          onPressed:
                              reviewStatus == ReviewStatus.NOACTIONTAKEN.name
                                  ? null
                                  : () {
                                      context
                                          .read<ApprovePacketsProvider>()
                                          .clearReview(regCurrent.packetId);
                                    },
                          icon: Icon(
                            Icons.refresh_outlined,
                            color:
                                reviewStatus == ReviewStatus.NOACTIONTAKEN.name
                                    ? Colors.grey
                                    : solidPrimary,
                            size: 28,
                          ),
                        ),
                      )
                    ],
                  ),
                  Card(
                    margin: const EdgeInsets.only(right: 24),
                    elevation: 0,
                    shape: CircleBorder(
                        side: BorderSide(
                      color: (currentInd <
                              context
                                  .read<ApprovePacketsProvider>()
                                  .matchingPackets
                                  .length)
                          ? solidPrimary
                          : Colors.grey,
                    )),
                    child: IconButton(
                      icon: Icon(
                        Icons.arrow_forward,
                        color: (currentInd <
                                context
                                    .read<ApprovePacketsProvider>()
                                    .matchingPackets
                                    .length)
                            ? solidPrimary
                            : Colors.grey,
                        size: 24,
                      ),
                      onPressed: currentInd <
                              context
                                  .read<ApprovePacketsProvider>()
                                  .matchingPackets
                                  .length
                          ? () {
                              context
                                  .read<ApprovePacketsProvider>()
                                  .setCurrentInd(currentInd + 1);
                              Registration reg = context
                                      .read<ApprovePacketsProvider>()
                                      .matchingPackets[(currentInd + 1) - 1]
                                  ['packet'] as Registration;
                              log(reg.packetId);
                              loadHtmlData(
                                  context
                                      .read<ApprovePacketsProvider>()
                                      .webViewPlusController,
                                  reg.packetId);
                            }
                          : () {
                              log("Out of range");
                            },
                    ),
                  ),
                ],
              ),
              const SizedBox(
                height: 18,
              ),
            ],
          ),
        );
      },
    );
  }
}
