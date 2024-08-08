import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/approve_packets_provider.dart';
import 'package:registration_client/utils/app_config.dart';

Widget dialogBox(callback, BuildContext context) {
  const double width = 600;

  return Dialog(
    shape: RoundedRectangleBorder(
      borderRadius: BorderRadius.circular(8),
    ),
    child: SingleChildScrollView(
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: SizedBox(
          width: width,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              SvgPicture.asset(
                'assets/images/success_message_icon.svg',
                height: 200.0,
                width: 200.0,
              ),
              const SizedBox(height: 24.0),
              const Text(
                "Reject Packet?",
                style: TextStyle(fontSize: 28.0, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 16.0),
              const SizedBox(
                width: width * 0.80,
                child: Text(
                  "You do not have enough memory to export selected packets, please clear memory on your device or de-select a few packets.",
                  textAlign: TextAlign.center,
                  softWrap: true,
                  style: TextStyle(fontSize: 18),
                ),
              ),
              const SizedBox(height: 40.0),
              Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  children: [
                    const Row(
                      mainAxisAlignment: MainAxisAlignment.start,
                      children: [
                        Text(
                          "Reason for rejection ",
                          style: TextStyle(
                              fontSize: 18, fontWeight: FontWeight.w500),
                        ),
                        Text(
                          "*",
                          style: TextStyle(color: Colors.red, fontSize: 18),
                        )
                      ],
                    ),
                    const SizedBox(height: 12.0),
                    Container(
                      width: double.infinity,
                      decoration: BoxDecoration(
                        border: Border.all(color: Colors.black26),
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: DropdownButtonHideUnderline(
                        child: DropdownButton<String>(
                          isExpanded: true,
                          icon: const Icon(
                            Icons.keyboard_arrow_down,
                            size: 32,
                          ),
                          value: context
                              .watch<ApprovePacketsProvider>()
                              .selectedReason,
                          padding: const EdgeInsets.symmetric(
                              horizontal: 18, vertical: 8),
                          items: context
                              .read<ApprovePacketsProvider>()
                              .reasonList
                              .map((String? value) {
                            return DropdownMenuItem<String>(
                              value: value,
                              child: Text(value ?? "No Value"),
                            );
                          }).toList(),
                          onChanged: (value) {
                            context
                                .read<ApprovePacketsProvider>()
                                .setSelectedReason(value);
                          },
                          hint: const Text('Select Reason'),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(
                height: 24,
              ),
              const Divider(
                height: 45,
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  context.watch<ApprovePacketsProvider>().rejectError
                      ? Text(
                          "Please select a reason...",
                          style: TextStyle(
                            fontSize: 16,
                            color: Colors.red[900],
                          ),
                        )
                      : const SizedBox(
                          width: 2,
                        ),
                  const SizedBox(
                    width: 25,
                  ),
                  ElevatedButton(
                      onPressed: () {
                        if (context
                                .read<ApprovePacketsProvider>()
                                .selectedReason ==
                            null) {
                          context
                              .read<ApprovePacketsProvider>()
                              .showRejectError();
                          return;
                        }
                        callback();
                      },
                      style: ElevatedButton.styleFrom(
                          backgroundColor: solidPrimary,
                          padding: const EdgeInsets.symmetric(
                              vertical: 16, horizontal: 32)),
                      child: const Text(
                        "REJECT",
                        style: TextStyle(fontSize: 18),
                      )),
                ],
              )
            ],
          ),
        ),
      ),
    ),
  );
}
