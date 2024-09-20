import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/biometric_attribute_data.dart';
import 'package:registration_client/pigeon/biometrics_pigeon.dart';
import 'package:registration_client/provider/biometric_capture_control_provider.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
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
  late GlobalProvider globalProvider;
  bool isSavingBiometrics = false;
  late BiometricCaptureControlProvider biometricCaptureControlProvider;
  late AppLocalizations appLocalizations = AppLocalizations.of(context)!;

  @override
  void initState() {
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    biometricCaptureControlProvider =
        Provider.of<BiometricCaptureControlProvider>(context, listen: false);
    super.initState();
  }

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
              if (!biometricAttributeData.exceptions.contains(false))
                Positioned(
                    top: 15,
                    right: 15,
                    child: Image.asset(
                      "assets/images/Group 57548@2x.png",
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
                if (
                ((globalProvider.operatorOnboardingAttributes
                    .contains("leftEye") &&
                    globalProvider.operatorOnboardingAttributes
                        .contains("rightEye")) ? ((biometricCaptureControlProvider.iris.isScanned || !biometricCaptureControlProvider.iris.exceptions.contains(false)) &&
                    (biometricCaptureControlProvider.iris.qualityPercentage >=
                        int.parse(biometricCaptureControlProvider
                            .iris.thresholdPercentage))) : true) &&
                    ((globalProvider.operatorOnboardingAttributes
                        .contains("rightLittle") &&
                        globalProvider.operatorOnboardingAttributes
                            .contains("rightRing")&&
                        globalProvider.operatorOnboardingAttributes
                            .contains("rightMiddle")&&
                        globalProvider.operatorOnboardingAttributes
                            .contains("rightIndex"))
                    ? ((biometricCaptureControlProvider.rightHand.isScanned ||
                        !biometricCaptureControlProvider.rightHand.exceptions
                            .contains(false)) && (biometricCaptureControlProvider.rightHand.qualityPercentage >=
                        int.parse(biometricCaptureControlProvider
                            .rightHand.thresholdPercentage) ||
                        !biometricCaptureControlProvider.rightHand.exceptions
                            .contains(false)) ) : true) &&
                    ((globalProvider.operatorOnboardingAttributes
                        .contains("leftLittle") &&
                        globalProvider.operatorOnboardingAttributes
                            .contains("leftRing")&&
                        globalProvider.operatorOnboardingAttributes
                            .contains("leftMiddle")&&
                        globalProvider.operatorOnboardingAttributes
                            .contains("leftIndex"))
                    ? ((biometricCaptureControlProvider.leftHand.isScanned ||
                        !biometricCaptureControlProvider.leftHand.exceptions
                            .contains(false)) &&
                    (biometricCaptureControlProvider.leftHand.qualityPercentage >= int.parse(biometricCaptureControlProvider.leftHand.thresholdPercentage) ||
                        !biometricCaptureControlProvider.leftHand.exceptions
                            .contains(false))) : true) &&
                    ((globalProvider.operatorOnboardingAttributes
                        .contains("leftThumb") &&
                        globalProvider.operatorOnboardingAttributes
                            .contains("rightThumb"))
                    ? ((biometricCaptureControlProvider.thumbs.isScanned ||
                        !biometricCaptureControlProvider.thumbs.exceptions
                            .contains(false)) &&
                    (biometricCaptureControlProvider.thumbs.qualityPercentage >=
                            int.parse(biometricCaptureControlProvider.thumbs.thresholdPercentage) ||
                        !biometricCaptureControlProvider.thumbs.exceptions.contains(false))) : true) &&
                    ((globalProvider.operatorOnboardingAttributes
                        .contains("face"))
                    ? ((biometricCaptureControlProvider.face.qualityPercentage >= int.parse(biometricCaptureControlProvider.face.thresholdPercentage)) &&
                    biometricCaptureControlProvider.face.isScanned) : true)) {
                  setState(() {
                    isSavingBiometrics = true;
                  });

                  String isOperatorBiometricSaved = "";
                  await BiometricsApi().saveOperatorBiometrics().timeout(
                    Duration(seconds: 10),
                    onTimeout: () {
                      setState(() {
                        isSavingBiometrics = false;
                      });
                      return "";
                    },
                  ).then((value) {
                    isOperatorBiometricSaved = value;
                    setState(() {});
                  });

                  setState(() {
                    isSavingBiometrics = false;
                  });
                  if (isOperatorBiometricSaved != "") {
                    await context.read<RegistrationTaskProvider>().getLastUpdatedTime();
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
                                (context
                                            .read<GlobalProvider>()
                                            .onboardingProcessName ==
                                        "Onboarding")
                                    ? appLocalizations.onboarded_successfully
                                    : appLocalizations
                                        .operator_biometric_updated_successfully,
                                textAlign: TextAlign.center,
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
                                  appLocalizations.home,
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
                      globalProvider.setCurrentIndex(1);
                    });
                  }
                }
              },
              child: isSavingBiometrics
                  ? CircularProgressIndicator(
                      color: appWhite,
                    )
                  : Text(
                      appLocalizations.verify_and_save,
                      style: Theme.of(context)
                          .textTheme
                          .bodyLarge
                          ?.copyWith(fontSize: 26.h, color: pureWhite),
                    ),
              style: OutlinedButton.styleFrom(
                  backgroundColor: (
                      ((globalProvider.operatorOnboardingAttributes
                          .contains("leftEye") &&
                          globalProvider.operatorOnboardingAttributes
                              .contains("rightEye")) ? ((biometricCaptureControlProvider.iris.isScanned || !biometricCaptureControlProvider.iris.exceptions.contains(false)) &&
                          (biometricCaptureControlProvider.iris.qualityPercentage >=
                              int.parse(biometricCaptureControlProvider
                                  .iris.thresholdPercentage))) : true) &&
                          ((globalProvider.operatorOnboardingAttributes
                              .contains("rightLittle") &&
                              globalProvider.operatorOnboardingAttributes
                                  .contains("rightRing")&&
                              globalProvider.operatorOnboardingAttributes
                                  .contains("rightMiddle")&&
                              globalProvider.operatorOnboardingAttributes
                                  .contains("rightIndex"))
                              ? ((biometricCaptureControlProvider.rightHand.isScanned ||
                              !biometricCaptureControlProvider.rightHand.exceptions
                                  .contains(false)) && (biometricCaptureControlProvider.rightHand.qualityPercentage >=
                              int.parse(biometricCaptureControlProvider
                                  .rightHand.thresholdPercentage) ||
                              !biometricCaptureControlProvider.rightHand.exceptions
                                  .contains(false)) ) : true) &&
                          ((globalProvider.operatorOnboardingAttributes
                              .contains("leftLittle") &&
                              globalProvider.operatorOnboardingAttributes
                                  .contains("leftRing")&&
                              globalProvider.operatorOnboardingAttributes
                                  .contains("leftMiddle")&&
                              globalProvider.operatorOnboardingAttributes
                                  .contains("leftIndex"))
                              ? ((biometricCaptureControlProvider.leftHand.isScanned ||
                              !biometricCaptureControlProvider.leftHand.exceptions
                                  .contains(false)) &&
                              (biometricCaptureControlProvider.leftHand.qualityPercentage >= int.parse(biometricCaptureControlProvider.leftHand.thresholdPercentage) ||
                                  !biometricCaptureControlProvider.leftHand.exceptions
                                      .contains(false))) : true) &&
                          ((globalProvider.operatorOnboardingAttributes
                              .contains("leftThumb") &&
                              globalProvider.operatorOnboardingAttributes
                                  .contains("rightThumb"))
                              ? ((biometricCaptureControlProvider.thumbs.isScanned ||
                              !biometricCaptureControlProvider.thumbs.exceptions
                                  .contains(false)) &&
                              (biometricCaptureControlProvider.thumbs.qualityPercentage >=
                                  int.parse(biometricCaptureControlProvider.thumbs.thresholdPercentage) ||
                                  !biometricCaptureControlProvider.thumbs.exceptions.contains(false))) : true) &&
                          ((globalProvider.operatorOnboardingAttributes
                              .contains("face"))
                              ? ((biometricCaptureControlProvider.face.qualityPercentage >= int.parse(biometricCaptureControlProvider.face.thresholdPercentage)) &&
                              biometricCaptureControlProvider.face.isScanned) : true))
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
                      globalProvider.onboardingProcessName == "Onboarding"
                          ? appLocalizations.supervisors_biometric_onboard
                          : appLocalizations.supervisors_biometric_update,
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
                          if (globalProvider.operatorOnboardingAttributes
                                  .contains("leftEye") &&
                              globalProvider.operatorOnboardingAttributes
                                  .contains("rightEye"))
                            _getBiometricCaptureSelectionBlockMobile(
                                biometricCaptureControlProvider.iris),
                          if (globalProvider.operatorOnboardingAttributes
                                  .contains("rightLittle") &&
                              globalProvider.operatorOnboardingAttributes
                                  .contains("rightRing") &&
                              globalProvider.operatorOnboardingAttributes
                                  .contains("rightMiddle") &&
                              globalProvider.operatorOnboardingAttributes
                                  .contains("rightIndex"))
                            _getBiometricCaptureSelectionBlockMobile(
                                biometricCaptureControlProvider.rightHand),
                          if (globalProvider.operatorOnboardingAttributes
                                  .contains("leftLittle") &&
                              globalProvider.operatorOnboardingAttributes
                                  .contains("leftRing") &&
                              globalProvider.operatorOnboardingAttributes
                                  .contains("leftMiddle") &&
                              globalProvider.operatorOnboardingAttributes
                                  .contains("leftIndex"))
                            _getBiometricCaptureSelectionBlockMobile(
                                biometricCaptureControlProvider.leftHand),
                          if (globalProvider.operatorOnboardingAttributes
                                  .contains("rightThumb") &&
                              globalProvider.operatorOnboardingAttributes
                                  .contains("leftThumb"))
                            _getBiometricCaptureSelectionBlockMobile(
                                biometricCaptureControlProvider.thumbs),
                          if (globalProvider.operatorOnboardingAttributes
                              .contains("face"))
                            _getBiometricCaptureSelectionBlockMobile(
                                biometricCaptureControlProvider.face),
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
