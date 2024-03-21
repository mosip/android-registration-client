import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/biometric_attribute_data.dart';
import 'package:registration_client/pigeon/biometrics_pigeon.dart';
import 'package:registration_client/provider/biometric_capture_control_provider.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/ui/onboard/widgets/operator_biometric_capture_scan_block_view.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:responsive_grid_list/responsive_grid_list.dart';

class OperatorBiometricsCaptureView extends StatefulWidget {
  const OperatorBiometricsCaptureView({super.key});

  @override
  State<OperatorBiometricsCaptureView> createState() =>
      _OperatorBiometricsCaptureState();
}

class _OperatorBiometricsCaptureState
    extends State<OperatorBiometricsCaptureView> {
  Widget _getBiometricCaptureSelectionBlockMobile(
      BiometricAttributeData biometricAttributeData) {
    return InkWell(
        onTap: () {
          setState(() {
            context.read<BiometricCaptureControlProvider>().biometricAttribute =
                biometricAttributeData.title;
          });

          final providerCopy = Provider.of<BiometricCaptureControlProvider>(
              context,
              listen: false);
          Navigator.push(
              context,
              MaterialPageRoute(
                  builder: (context) => ChangeNotifierProvider.value(
                        value: providerCopy,
                        child: OperatorBiometricCaptureScanBlockView(),
                      ))).then((value) {
            setState(() {});
          });
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
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        backgroundColor: secondaryColors.elementAt(10),
        bottomNavigationBar: Container(
          height: 100.h,
          color: pureWhite,
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
            child: ElevatedButton(
              onPressed: () async {
                if (context
                        .read<BiometricCaptureControlProvider>()
                        .iris
                        .isScanned &&
                    context
                        .read<BiometricCaptureControlProvider>()
                        .rightHand
                        .isScanned &&
                    context
                        .read<BiometricCaptureControlProvider>()
                        .leftHand
                        .isScanned &&
                    context
                        .read<BiometricCaptureControlProvider>()
                        .thumbs
                        .isScanned &&
                    context
                        .read<BiometricCaptureControlProvider>()
                        .face
                        .isScanned) {
                  await BiometricsApi().saveOperatorBiometrics();
                  Navigator.pop(context);
                  showDialog<String>(
                    context: context,
                    builder: (BuildContext context) => AlertDialog(
                      content: Container(
                        height: 474.h,
                        width: 574.w,
                        child: Column(
                          children: [
                            SizedBox(
                              height: 70.h,
                              width: double.infinity,
                            ),
                            SvgPicture.asset(
                                "assets/svg/success_message_icon.svg"),
                            Text(
                              "You have onboarded successfully.",
                              style: TextStyle(
                                  fontSize: 28,
                                  fontWeight: semiBold,
                                  color: Color(0xFF000000)),
                            ),
                            SizedBox(
                              height: 62.h,
                            ),
                            ElevatedButton(
                              onPressed: () {
                                Navigator.pop(context);
                              },
                              child: Text(
                                "HOME",
                                style: TextStyle(
                                    fontSize: 19,
                                    fontWeight: bold,
                                    color: pureWhite),
                              ),
                              style: ButtonStyle(
                                  fixedSize: MaterialStateProperty.all<Size>(
                                      Size(564, 70))),
                            )
                          ],
                        ),
                      ),
                    ),
                  );

                  setState(() {
                    context.read<GlobalProvider>().setCurrentIndex(1);
                  });
                }
              },
              child: Text(
                "VERIFY & SAVE",
                style: Theme.of(context)
                    .textTheme
                    .bodyLarge
                    ?.copyWith(fontSize: 26.h, color: pureWhite),
              ),
              style: OutlinedButton.styleFrom(
                  backgroundColor: (context
                              .read<BiometricCaptureControlProvider>()
                              .iris
                              .isScanned &&
                          context
                              .read<BiometricCaptureControlProvider>()
                              .rightHand
                              .isScanned &&
                          context
                              .read<BiometricCaptureControlProvider>()
                              .leftHand
                              .isScanned &&
                          context
                              .read<BiometricCaptureControlProvider>()
                              .thumbs
                              .isScanned &&
                          context
                              .read<BiometricCaptureControlProvider>()
                              .face
                              .isScanned)
                      ? solidPrimary
                      : secondaryColors.elementAt(22)),
            ),
          ),
        ),
        appBar: PreferredSize(
          preferredSize: Size.fromHeight(70.h),
          child: AppBar(
            backgroundColor: pureWhite,
            automaticallyImplyLeading: false,
            flexibleSpace: Card(
              elevation: 0,
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
                      style: Theme.of(context).textTheme.titleLarge?.copyWith(
                          fontSize: (isMobileSize) ? 16.w : 24.w,
                          color: blackShade1,
                          fontWeight: semiBold,
                          overflow: TextOverflow.ellipsis),
                    ),
                    // SizedBox(
                    //   height: (isMobileSize)?20.h:52.h,
                    // ),
                  ],
                ),
              ),
            ),
          ),
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
                          if (context
                                  .read<GlobalProvider>()
                                  .operatorOnboardingAttributes
                                  .contains("leftEye") &&
                              context
                                  .read<GlobalProvider>()
                                  .operatorOnboardingAttributes
                                  .contains("rightEye"))
                            _getBiometricCaptureSelectionBlockMobile(context
                                .read<BiometricCaptureControlProvider>()
                                .iris),
                          if (context
                                  .read<GlobalProvider>()
                                  .operatorOnboardingAttributes
                                  .contains("rightLittle") &&
                              context
                                  .read<GlobalProvider>()
                                  .operatorOnboardingAttributes
                                  .contains("rightRing") &&
                              context
                                  .read<GlobalProvider>()
                                  .operatorOnboardingAttributes
                                  .contains("rightMiddle") &&
                              context
                                  .read<GlobalProvider>()
                                  .operatorOnboardingAttributes
                                  .contains("rightIndex"))
                            _getBiometricCaptureSelectionBlockMobile(context
                                .read<BiometricCaptureControlProvider>()
                                .rightHand),
                          if (context
                                  .read<GlobalProvider>()
                                  .operatorOnboardingAttributes
                                  .contains("leftLittle") &&
                              context
                                  .read<GlobalProvider>()
                                  .operatorOnboardingAttributes
                                  .contains("leftRing") &&
                              context
                                  .read<GlobalProvider>()
                                  .operatorOnboardingAttributes
                                  .contains("leftMiddle") &&
                              context
                                  .read<GlobalProvider>()
                                  .operatorOnboardingAttributes
                                  .contains("leftIndex"))
                            _getBiometricCaptureSelectionBlockMobile(context
                                .read<BiometricCaptureControlProvider>()
                                .leftHand),
                          if (context
                                  .read<GlobalProvider>()
                                  .operatorOnboardingAttributes
                                  .contains("rightThumb") &&
                              context
                                  .read<GlobalProvider>()
                                  .operatorOnboardingAttributes
                                  .contains("leftThumb"))
                            _getBiometricCaptureSelectionBlockMobile(context
                                .read<BiometricCaptureControlProvider>()
                                .thumbs),
                          if (context
                              .read<GlobalProvider>()
                              .operatorOnboardingAttributes
                              .contains("face"))
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
