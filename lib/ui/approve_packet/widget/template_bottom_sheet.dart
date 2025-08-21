import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
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
                    height: isMobileSize ? 1600 : 2700,
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
              Column(
                children: [
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 8),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        // Back Arrow
                        Card(
                          elevation: 0,
                          shape: CircleBorder(
                            side: BorderSide(
                              color: (currentInd > 1) ? solidPrimary : Colors.grey,
                            ),
                          ),
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
                                  .read<ApprovePacketsProvider>().matchingPackets[(currentInd - 1) - 1]['packet'] as Registration;
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
                  // Buttons Column
                  Column(
                      children: [
                        // APPROVE BUTTON
                        SizedBox(
                          width: 190.sp,
                          child: ElevatedButton.icon(
                            style: ElevatedButton.styleFrom(
                              backgroundColor: solidPrimary,
                              padding: const EdgeInsets.symmetric(vertical: 14, horizontal: 18),
                            ),
                            icon: const Icon(Icons.check_outlined, color: Colors.white),
                            label: Text(
                              AppLocalizations.of(context)!.approve,
                              style: const TextStyle(fontWeight: FontWeight.bold),
                            ),
                            onPressed: reviewStatus == ReviewStatus.APPROVED.name
                                ? null
                                : () {
                              context
                                  .read<ApprovePacketsProvider>()
                                  .approvePacket(regCurrent.packetId);
                              if (currentInd <
                                  context.read<ApprovePacketsProvider>().matchingPackets.length) {
                                context
                                    .read<ApprovePacketsProvider>()
                                    .setCurrentInd(currentInd + 1);
                                Registration reg = context
                                    .read<ApprovePacketsProvider>()
                                    .matchingPackets[(currentInd + 1) - 1]['packet'] as Registration;
                                loadHtmlData(
                                    context.read<ApprovePacketsProvider>().webViewPlusController,
                                    reg.packetId);
                              }
                            },
                          ),
                        ),
                        const SizedBox(height: 12),

                        // REJECT BUTTON
                        SizedBox(
                          width: 190.sp,
                          child: OutlinedButton.icon(
                            icon: const Icon(Icons.close),
                            label: Text(
                              AppLocalizations.of(context)!.reject,
                              style: const TextStyle(fontWeight: FontWeight.bold),
                            ),
                            style: OutlinedButton.styleFrom(
                              foregroundColor: Colors.red,
                              padding: const EdgeInsets.symmetric(vertical: 14, horizontal: 18),
                              side: const BorderSide(color: Colors.red, width: 2),
                            ),
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
                                        context.read<ApprovePacketsProvider>().matchingPackets.length) {
                                      context
                                          .read<ApprovePacketsProvider>()
                                          .setCurrentInd(currentInd + 1);
                                      Registration reg = context
                                          .read<ApprovePacketsProvider>()
                                          .matchingPackets[(currentInd + 1) - 1]['packet']
                                      as Registration;
                                      loadHtmlData(
                                          context
                                              .read<ApprovePacketsProvider>()
                                              .webViewPlusController,
                                          reg.packetId);
                                    }
                                    Navigator.of(context).pop();
                                  }, context);
                                },
                              );
                            },
                          ),
                        ),
                        const SizedBox(height: 12),
                        // RESET BUTTON
                        MouseRegion(
                          cursor: SystemMouseCursors.click,
                          child: SizedBox(
                            width: 190.sp,
                            child: OutlinedButton(
                              style: OutlinedButton.styleFrom(
                                backgroundColor: Colors.white,
                                padding: const EdgeInsets.symmetric(vertical: 14, horizontal: 18),
                                side: BorderSide(color: Colors.transparent, width: 2),
                              ).copyWith(
                                side: MaterialStateProperty.resolveWith((states) {
                                  if (states.contains(MaterialState.hovered)) {
                                    return BorderSide(color: solidPrimary, width: 2);
                                  }
                                  return BorderSide(color: Colors.transparent, width: 2);
                                }),
                              ),
                              onPressed: reviewStatus == ReviewStatus.NOACTIONTAKEN.name
                                  ? null
                                  : () {
                                context.read<ApprovePacketsProvider>().clearReview(regCurrent.packetId);
                              },
                              child: Text(
                                'RESET',
                                style: TextStyle(
                                  fontWeight: FontWeight.bold,
                                  color: solidPrimary,
                                ),
                              ),
                            ),
                          ),
                        ),

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
          ),
              const SizedBox(
                height: 18),
              ],
        ),
          ],
        ),
        );
      },
    );
  }
}
