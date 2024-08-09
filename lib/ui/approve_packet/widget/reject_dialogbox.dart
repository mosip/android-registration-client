import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/approve_packets_provider.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

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
              Text(
                AppLocalizations.of(context)!.reject_dialog_heading,
                style: const TextStyle(
                    fontSize: 28.0, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 16.0),
              SizedBox(
                width: width * 0.80,
                child: Text(
                  AppLocalizations.of(context)!.reject_dialog_subheading,
                  textAlign: TextAlign.center,
                  softWrap: true,
                  style: const TextStyle(fontSize: 18),
                ),
              ),
              const SizedBox(height: 40.0),
              Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.start,
                      children: [
                        Text(
                          "${AppLocalizations.of(context)!.reason_rejection} ",
                          style: const TextStyle(
                              fontSize: 18, fontWeight: FontWeight.w500),
                        ),
                        const Text(
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
                          hint: Text(AppLocalizations.of(context)!
                              .select_value_message),
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
                          AppLocalizations.of(context)!.no_reason_selected,
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
                      child: Text(
                        AppLocalizations.of(context)!.reject,
                        style: const TextStyle(fontSize: 18),
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
