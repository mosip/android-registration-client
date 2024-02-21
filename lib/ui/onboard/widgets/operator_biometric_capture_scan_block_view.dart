import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/biometric_capture_control_provider.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class OperatorBiometricCaptureScanBlockView extends StatefulWidget {
  const OperatorBiometricCaptureScanBlockView({super.key});

  @override
  State<OperatorBiometricCaptureScanBlockView> createState() => _OperatorBiometricCaptureScanBlockViewState();
}

class _OperatorBiometricCaptureScanBlockViewState extends State<OperatorBiometricCaptureScanBlockView> {

  
  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        bottomNavigationBar: Container(
          color: pureWhite,
          padding: EdgeInsets.symmetric(
            horizontal: (isMobileSize) ? 30.w : 80.w,
            vertical: 16.h,
          ),
          height: (isMobileSize) ? 60.w : 100.h,
          child: Row(
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              const Spacer(),
              ElevatedButton(
                style: ButtonStyle(
                  maximumSize:
                      MaterialStateProperty.all<Size>(const Size(215, 68)),
                  minimumSize:
                      MaterialStateProperty.all<Size>(const Size(215, 68)),
                ),
                onPressed: () {
                  // List<String> bioAttributes = (widget
                  //             .field.conditionalBioAttributes!.first!.ageGroup!
                  //             .compareTo(
                  //                 context.read<GlobalProvider>().ageGroup) ==
                  //         0)
                  //     ? _returnBiometricList(
                  //         widget.field.conditionalBioAttributes!.first!
                  //             .bioAttributes!,
                  //         widget.field.id!)
                  //     : _returnBiometricList(
                  //         widget.field.bioAttributes!, widget.field.id!);

                  // var nextElement = _getNextElement(
                  //     bioAttributes,
                  //     context
                  //         .read<BiometricCaptureControlProvider>()
                  //         .biometricAttribute);
                  // if (nextElement != null) {
                  //   context
                  //       .read<BiometricCaptureControlProvider>()
                  //       .biometricAttribute = nextElement;
                  // } else {
                  //   Navigator.pop(context);
                  // }
                },
                child: Text(AppLocalizations.of(context)!.next_button,
                    style: TextStyle(fontSize: 24, fontWeight: bold)),
              ),
            ],
          ),
        ),
        appBar: PreferredSize(
          preferredSize: Size.fromHeight((isMobileSize) ? 60 : 70),
          child: AppBar(
            automaticallyImplyLeading: false,
            flexibleSpace: SizedBox(
              height: (isMobileSize) ? 60 : 70,
              child: Card(
                margin: const EdgeInsets.all(0),
                child: Padding(
                  padding: (isMobileSize)
                      ? const EdgeInsets.fromLTRB(20, 9, 0, 9)
                      : const EdgeInsets.fromLTRB(20, 18, 0, 18),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                       Text(
                              "Supervisor's Biometric Verification",
                              style: Theme.of(context)
                                  .textTheme
                                  .titleLarge
                                  ?.copyWith(
                                      fontSize: (isMobileSize) ? 14.h : 24.h,
                                      color: blackShade1,
                                      fontWeight: semiBold,
                                      overflow: TextOverflow.ellipsis),
                            ),
                      Padding(
                        padding: const EdgeInsets.only(right: 30),
                        child: InkWell(
                          onTap: () {
                            Navigator.pop(context);
                          },
                          child: Image.asset(
                            "assets/images/Group 57951.png",
                            height: (isMobileSize) ? 30.h : 52.h,
                          ),
                        ),
                      )
                    ],
                  ),
                ),
              ),
            ),
          ),
        ),
        body: SingleChildScrollView(
          child: Padding(
            padding: const EdgeInsets.fromLTRB(20, 40, 20, 0),
            child: Column(
              children: [
                const SizedBox(
                  width: double.infinity,
                ),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    Expanded(
                      child: InkWell(
                        onTap: () {
                          context
                              .read<BiometricCaptureControlProvider>()
                              .biometricCaptureScanBlockTabIndex = 1;
                        },
                        child: Container(
                          decoration: BoxDecoration(
                            color: (context
                                        .read<BiometricCaptureControlProvider>()
                                        .biometricCaptureScanBlockTabIndex ==
                                    1)
                                ? solidPrimary
                                : pureWhite,
                            border: (context
                                        .read<BiometricCaptureControlProvider>()
                                        .biometricCaptureScanBlockTabIndex ==
                                    1)
                                ? const Border()
                                : Border(
                                    bottom: BorderSide(
                                        color: solidPrimary, width: 3),
                                  ),
                          ),
                          height: 84,
                          child: Center(
                            child: Text(
                              " ${AppLocalizations.of(context)!.scan}",
                              style: TextStyle(
                                  fontSize: (isMobileSize) ? 18 : 24,
                                  fontWeight: semiBold,
                                  color: (context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .biometricCaptureScanBlockTabIndex ==
                                          1)
                                      ? pureWhite
                                      : blackShade1),
                            ),
                          ),
                        ),
                      ),
                    ),
                    Expanded(
                      child: InkWell(
                        onTap: () {
                          context
                              .read<BiometricCaptureControlProvider>()
                              .biometricCaptureScanBlockTabIndex = 2;
                        },
                        child: Container(
                          decoration: BoxDecoration(
                            color: (context
                                        .read<BiometricCaptureControlProvider>()
                                        .biometricCaptureScanBlockTabIndex ==
                                    2)
                                ? solidPrimary
                                : pureWhite,
                            border: (context
                                        .read<BiometricCaptureControlProvider>()
                                        .biometricCaptureScanBlockTabIndex ==
                                    2)
                                ? const Border()
                                : Border(
                                    bottom: BorderSide(
                                        color: solidPrimary, width: 3),
                                  ),
                          ),
                          height: 84,
                          child: Center(
                            child: Text(
                              AppLocalizations.of(context)!.mark_exception,
                              style: TextStyle(
                                  fontSize: (isMobileSize) ? 18 : 24,
                                  fontWeight: semiBold,
                                  color: (context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .biometricCaptureScanBlockTabIndex ==
                                          2)
                                      ? pureWhite
                                      : blackShade1),
                            ),
                          ),
                        ),
                      ),
                    )
                  ],
                ),
                const SizedBox(
                  height: 40,
                ),
                // (context
                //             .read<BiometricCaptureControlProvider>()
                //             .biometricCaptureScanBlockTabIndex ==
                //         1)
                //     ? _scanBlock()
                //     : _exceptionBlock()
              ],
            ),
          ),
        ),
      ),
    );
  
  }
}