import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter_html/flutter_html.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/approve_packets_provider.dart';
import 'package:registration_client/ui/approve_packet/widget/template_bottom_sheet.dart';
import 'package:registration_client/utils/constants.dart';
import '../../../model/registration.dart';
import '../../../utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class ApproveTable extends StatefulWidget {
  const ApproveTable({super.key});

  @override
  State<ApproveTable> createState() => _ApproveTableState();
}

class _ApproveTableState extends State<ApproveTable> {
  void showTemplate() {
    showModalBottomSheet(
        context: context,
        isScrollControlled: true,
        backgroundColor: Colors.white,
        elevation: 4,
        shape: const RoundedRectangleBorder(
          borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
        ),
        builder: (_) {
          return TemplateBottomSheet().bottomSheet(context);
        });
  }

  @override
  Widget build(BuildContext context) {
    double tableWidth = MediaQuery.of(context).size.width + 75;
    TextStyle? textTheme = Theme.of(context)
        .textTheme
        .bodySmall
        ?.copyWith(fontSize: 16, fontWeight: FontWeight.w900);
    Map<String, Color> reviewColor = {
      ReviewStatus.APPROVED.name: Colors.green,
      ReviewStatus.REJECTED.name: Colors.red,
      ReviewStatus.NOACTIONTAKEN.name: Colors.black45
    };
    return SizedBox(
      width: tableWidth,
      child: ListView.separated(
        itemCount:
            context.watch<ApprovePacketsProvider>().matchingPackets.length + 1,
        itemBuilder: (BuildContext context, int index) {
          if (index == 0) {
            return Column(
              children: [
                Container(
                  color: Colors.white,
                  child: ListTile(
                    horizontalTitleGap: 0,
                    leading: Checkbox(
                      activeColor: solidPrimary,
                      tristate: true,
                      side: BorderSide(color: solidPrimary, width: 1.5),
                      value: context
                                  .watch<ApprovePacketsProvider>()
                                  .countSelected ==
                              0
                          ? false
                          : (context
                                      .watch<ApprovePacketsProvider>()
                                      .countSelected ==
                                  context
                                      .watch<ApprovePacketsProvider>()
                                      .matchingPackets
                                      .length
                              ? true
                              : null),
                      onChanged: (bool? value) {
                        // select all for which action is taken
                        context
                            .read<ApprovePacketsProvider>()
                            .setSelectedAll(value ?? false);
                      },
                    ),
                    contentPadding:
                        const EdgeInsets.symmetric(horizontal: 16, vertical: 0),
                    title: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      children: [
                        SizedBox(
                            width: tableWidth / 20,
                            child: Text(
                                AppLocalizations.of(context)!.serial_number,
                                textAlign: TextAlign.center,
                                style: textTheme)),
                        SizedBox(
                            width: tableWidth / 4.5,
                            child: Text(
                                AppLocalizations.of(context)!.application_id,
                                textAlign: TextAlign.center,
                                style: textTheme)),
                        SizedBox(
                            width: tableWidth / 8,
                            child: Text(AppLocalizations.of(context)!.reg_date,
                                textAlign: TextAlign.center, style: textTheme)),
                        SizedBox(
                            width: tableWidth / 9,
                            child: Text(
                                AppLocalizations.of(context)!.client_status,
                                textAlign: TextAlign.center,
                                style: textTheme)),
                        SizedBox(
                            width: tableWidth / 8,
                            child: Text(
                                AppLocalizations.of(context)!.review_status,
                                textAlign: TextAlign.center,
                                style: textTheme)),
                        SizedBox(
                            width: tableWidth / 12,
                            child: Text(
                                AppLocalizations.of(context)!.operator_id,
                                textAlign: TextAlign.center,
                                style: textTheme)),
                      ],
                    ),
                    onTap: () {
                      // Handle tile selection here
                      // You can access myClass.myList[index]
                    },
                  ),
                ),
                Container(height: 1.25, color: Colors.grey.shade200),
              ],
            );
          }
          Registration regCurrent = context
              .watch<ApprovePacketsProvider>()
              .matchingPackets[index - 1]['packet'] as Registration;
          String reviewStatus = context
              .watch<ApprovePacketsProvider>()
              .matchingPackets[index - 1]['review_status'] as String;

          var dateTime = DateTime.fromMillisecondsSinceEpoch(
              regCurrent.crDtime ?? DateTime.now().millisecond);
          var formattedDate =
              DateFormat('dd/MM/yyyy, hh:mm a').format(dateTime);
          return ListTile(
            horizontalTitleGap: 0,
            leading: Checkbox(
              activeColor: solidPrimary,
              value: context
                  .watch<ApprovePacketsProvider>()
                  .matchingSelected[index - 1],
              onChanged: (bool? value) {
                context
                    .read<ApprovePacketsProvider>()
                    .setSelected(index - 1, value);
              },
              side: BorderSide(color: solidPrimary, width: 1.5),
            ),
            contentPadding:
                const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
            tileColor: context
                    .watch<ApprovePacketsProvider>()
                    .matchingSelected[index - 1]
                ? const Color(0XFFF0F5FF)
                : Colors.white,
            title: Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                SizedBox(
                    width: tableWidth / 25,
                    child: Text(
                      "$index.",
                      textAlign: TextAlign.center,
                    )),
                SizedBox(
                    width: tableWidth / 4.5,
                    child: TextButton(
                      onPressed: () {
                        context
                            .read<ApprovePacketsProvider>()
                            .setCurrentInd(index);
                        showTemplate();
                        log("show");
                      },
                      child: Text(regCurrent.packetId,
                          textAlign: TextAlign.center,
                          overflow: TextOverflow.ellipsis,
                          maxLines: 2),
                    )),
                SizedBox(
                    width: tableWidth / 8,
                    child: Text(
                      formattedDate,
                      textAlign: TextAlign.center,
                      overflow: TextOverflow.ellipsis,
                      maxLines: 2,
                    )),
                SizedBox(
                    width: tableWidth / 9,
                    child: Text(
                      regCurrent.clientStatus.toString(),
                      textAlign: TextAlign.center,
                      overflow: TextOverflow.ellipsis,
                      maxLines: 2,
                    )),
                SizedBox(
                  width: tableWidth / 8,
                  child: Text(
                    reviewStatus == "NOACTIONTAKEN"
                        ? "Pending"
                        : "${reviewStatus[0].toUpperCase()}${reviewStatus.substring(1).toLowerCase()}",
                    textAlign: TextAlign.center,
                    overflow: TextOverflow.ellipsis,
                    maxLines: 2,
                    style: TextStyle(
                        color: reviewColor[reviewStatus],
                        fontWeight: FontWeight.bold),
                  ),
                ),
                SizedBox(
                    width: tableWidth / 12,
                    child: Text(
                      regCurrent.crBy.toString(),
                      textAlign: TextAlign.center,
                      overflow: TextOverflow.ellipsis,
                      maxLines: 2,
                    )),
              ],
            ),
            onTap: () {
              // Handle tile selection here
            },
          );
        },
        separatorBuilder: (BuildContext context, int index) {
          return Container(
            height: 1,
            color:
                context.watch<ApprovePacketsProvider>().matchingSelected[index]
                    ? Colors.white
                    : const Color(0XFFF0F5FF),
          );
        },
      ),
    );
  }
}
