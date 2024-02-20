import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/biometric_attribute_data.dart';
import 'package:registration_client/provider/biometric_capture_control_provider.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/stateful_wrapper.dart';
import 'package:responsive_grid_list/responsive_grid_list.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class OperatorOnboardingBiometricsCaptureControl extends StatelessWidget {
  const OperatorOnboardingBiometricsCaptureControl({super.key});

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
        lazy: true,
        create: (_) => BiometricCaptureControlProvider(),
        builder: (context, child) => StatefulWrapper(
            onInit: () {
              context.read<BiometricCaptureControlProvider>().iris.viewTitle =
                  AppLocalizations.of(context)!.iris;
              context
                  .read<BiometricCaptureControlProvider>()
                  .leftHand
                  .viewTitle = AppLocalizations.of(context)!.left_hand;
              context
                  .read<BiometricCaptureControlProvider>()
                  .rightHand
                  .viewTitle = AppLocalizations.of(context)!.right_hand;
              context.read<BiometricCaptureControlProvider>().thumbs.viewTitle =
                  AppLocalizations.of(context)!.thumbs;
              context.read<BiometricCaptureControlProvider>().face.viewTitle =
                  AppLocalizations.of(context)!.face;
              context
                  .read<BiometricCaptureControlProvider>()
                  .biometricAttribute = "Iris";
            },
            child: OperatorBiometricsCapture()));
  }
}

class OperatorBiometricsCapture extends StatefulWidget {
  const OperatorBiometricsCapture({super.key});

  @override
  State<OperatorBiometricsCapture> createState() =>
      _OperatorBiometricsCaptureState();
}

class _OperatorBiometricsCaptureState extends State<OperatorBiometricsCapture>
    with WidgetsBindingObserver {
  Widget _getBiometricCaptureSelectionBlockMobile(
      BiometricAttributeData biometricAttributeData) {
    return InkWell(
        onTap: () {
          setState(() {
            context.read<BiometricCaptureControlProvider>().biometricAttribute =
                biometricAttributeData.title;
          });

          // final providerCopy = Provider.of<BiometricCaptureControlProvider>(
          //     context,
          //     listen: false);
          // Navigator.push(
          //     context,
          //     MaterialPageRoute(
          //         builder: (context) => ChangeNotifierProvider.value(
          //               value: providerCopy,
          //               child: BiometricCaptureScanBlockPortrait(
          //                   field: widget.field),
          //             )));
        },
        child: Center(
          child: Stack(
            children: [
              Container(
                height: 335.h,
                width: 372.h,
                decoration: BoxDecoration(
                    color: pureWhite,
                    border: Border.all(
                        color: (biometricAttributeData.isScanned == true)
                            ? (biometricAttributeData.exceptions.contains(true))
                                ? secondaryColors.elementAt(16)
                                : secondaryColors.elementAt(11)
                            : (context
                                        .watch<
                                            BiometricCaptureControlProvider>()
                                        .biometricAttribute ==
                                    biometricAttributeData.title)
                                ? secondaryColors.elementAt(12)
                                : secondaryColors.elementAt(14)),
                    borderRadius: BorderRadius.circular(10)),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: SvgPicture.asset(
                        "assets/svg/${biometricAttributeData.title}.svg",
                        height: 200.h,
                        width: 200.h,
                      ),
                    ),
                    SizedBox(
                      height: 10.h,
                    ),
                    Text(
                      "${biometricAttributeData.viewTitle} ${AppLocalizations.of(context)!.scan}",
                      style: TextStyle(
                        fontSize: 28,
                        fontWeight: semiBold,
                        color: blackShade1,
                      ),
                    )
                  ],
                ),
              ),
              if (biometricAttributeData.isScanned == true)
                Positioned(
                    top: 15,
                    right: 15,
                    child: (biometricAttributeData.exceptions.contains(true))
                        ? Image.asset(
                            "assets/images/Group 57548@2x.png",
                          )
                        : Image.asset(
                            "assets/images/Group 57745@2x.png",
                          )),
              if (biometricAttributeData.isScanned == true)
                Positioned(
                    top: 20,
                    left: 20,
                    child: Container(
                      padding: const EdgeInsets.symmetric(
                          horizontal: 10, vertical: 7),
                      decoration: BoxDecoration(
                          color: (biometricAttributeData.qualityPercentage
                                      .toInt() <
                                  int.parse(biometricAttributeData
                                      .thresholdPercentage))
                              ? secondaryColors.elementAt(26)
                              : secondaryColors.elementAt(11),
                          borderRadius: BorderRadius.circular(50)),
                      height: 40,
                      child: Text(
                          "${biometricAttributeData.qualityPercentage.toInt()}%",
                          style: TextStyle(
                              fontSize: 20,
                              color: pureWhite,
                              fontWeight: semiBold)),
                    )),
            ],
          ),
        ));
  }

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    WidgetsBinding.instance.addObserver(this);
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeMetrics() {
    final orientation = WidgetsBinding.instance.window.physicalSize
    .aspectRatio > 1 ? Orientation.landscape : Orientation.portrait;
    print('$WidgetsBindingObserver metrics changed ${DateTime.now()}: '
        '${WidgetsBinding.instance.window.physicalSize.aspectRatio > 1 ? Orientation.landscape : Orientation.portrait}');
  }

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        backgroundColor: secondaryColors.elementAt(10),
        appBar: AppBar(
          backgroundColor: pureWhite,

          title: Text(
            "Supervisor's Biometric Verification",
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
                fontSize: (isMobileSize) ? 16.w : 24.w,
                color: blackShade1,
                fontWeight: semiBold,
                overflow: TextOverflow.ellipsis),
          ),
          // flexibleSpace: Card(
          //   margin: const EdgeInsets.all(0),
          //   child: Padding(
          //     padding: (isMobileSize)
          //         ? const EdgeInsets.fromLTRB(20, 9, 0, 9)
          //         : const EdgeInsets.fromLTRB(20, 18, 0, 18),
          //     child: Row(
          //       mainAxisAlignment: MainAxisAlignment.spaceBetween,
          //       children: [
          //         Text(
          //           "Supervisor's Biometric Verification",
          //           style: Theme.of(context).textTheme.titleLarge?.copyWith(
          //               fontSize: (isMobileSize) ? 16.w : 24.w,
          //               color: blackShade1,
          //               fontWeight: semiBold,
          //               overflow: TextOverflow.ellipsis),
          //         ),
          //         // SizedBox(
          //         //   height: (isMobileSize)?20.h:52.h,
          //         // ),
          //       ],
          //     ),
          //   ),
          // ),
        ),
        body: SingleChildScrollView(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(
                width: double.infinity,
              ),
              // SizedBox(
              //   width: double.infinity,
              //   child:
              // ),
              SizedBox(
                height: 15.h,
              ),
              Column(
                children: [
                  Container(
                    margin: const EdgeInsets.fromLTRB(20, 0, 20, 20),
                    height: (isMobileSize)
                        ? ((((5.toDouble())).ceil() * 335.h) + 409.h)
                        : ((((5.toDouble()) / 2).ceil() * 335.h) + 70.h),
                    width: double.infinity,
                    child: ResponsiveGridList(
                        listViewBuilderOptions: ListViewBuilderOptions(
                            physics: const NeverScrollableScrollPhysics()),
                        minItemWidth: 372.h,
                        verticalGridSpacing: 17,
                        horizontalGridMargin: 30,
                        minItemsPerRow: (isMobileSize) ? 1 : 2,
                        maxItemsPerRow: (isMobileSize) ? 1 : 2,
                        children: [
                          _getBiometricCaptureSelectionBlockMobile(context
                              .read<BiometricCaptureControlProvider>()
                              .iris),
                          _getBiometricCaptureSelectionBlockMobile(context
                              .read<BiometricCaptureControlProvider>()
                              .rightHand),
                          _getBiometricCaptureSelectionBlockMobile(context
                              .read<BiometricCaptureControlProvider>()
                              .leftHand),
                          _getBiometricCaptureSelectionBlockMobile(context
                              .read<BiometricCaptureControlProvider>()
                              .thumbs),
                          _getBiometricCaptureSelectionBlockMobile(context
                              .read<BiometricCaptureControlProvider>()
                              .face),
                        ]),
                  )
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
