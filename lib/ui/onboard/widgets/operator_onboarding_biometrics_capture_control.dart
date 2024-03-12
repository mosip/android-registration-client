import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/biometric_attribute_data.dart';
import 'package:registration_client/pigeon/biometrics_pigeon.dart';
import 'package:registration_client/provider/biometric_capture_control_provider.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/ui/onboard/widgets/operator_biometrics_capture_view.dart';
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
        builder: (context, child) =>
            OperatorOnboardingBiometricsCaptureControlInitialization());
  }
}

class OperatorOnboardingBiometricsCaptureControlInitialization
    extends StatelessWidget {
  const OperatorOnboardingBiometricsCaptureControlInitialization({super.key});

  @override
  Widget build(BuildContext context) {
    _getOperatorOnboardingAttributes() async {
      await BiometricsApi()
          .getMapValue("mosip.registration.operator.onboarding.bioattributes")
          .then((value) {
        context.read<GlobalProvider>().operatorOnboardingAttributes = value;
      });
    }

    setInitialBioAttribute() async {
      context.read<BiometricCaptureControlProvider>().biometricAttribute =
          "Iris";

      context.read<BiometricCaptureControlProvider>().iris.viewTitle =
          AppLocalizations.of(context)!.iris;
      context.read<BiometricCaptureControlProvider>().leftHand.viewTitle =
          AppLocalizations.of(context)!.left_hand;
      context.read<BiometricCaptureControlProvider>().rightHand.viewTitle =
          AppLocalizations.of(context)!.right_hand;
      context.read<BiometricCaptureControlProvider>().thumbs.viewTitle =
          AppLocalizations.of(context)!.thumbs;
      context.read<BiometricCaptureControlProvider>().face.viewTitle =
          AppLocalizations.of(context)!.face;
      context.read<BiometricCaptureControlProvider>().biometricAttribute =
          "Iris";
      context.read<BiometricCaptureControlProvider>().customSetterFace(
          int.parse(await BiometricsApi()
              .getMapValue("mosip.registration.num_of_face_retries")),
          "noOfCapturesAllowed");
      context.read<BiometricCaptureControlProvider>().customSetterThumbs(
          int.parse(await BiometricsApi()
              .getMapValue("mosip.registration.num_of_fingerprint_retries")),
          "noOfCapturesAllowed");
      context.read<BiometricCaptureControlProvider>().customSetterIris(
          int.parse(await BiometricsApi()
              .getMapValue("mosip.registration.num_of_iris_retries")),
          "noOfCapturesAllowed");
      context.read<BiometricCaptureControlProvider>().customSetterLeftHand(
          int.parse(await BiometricsApi()
              .getMapValue("mosip.registration.num_of_fingerprint_retries")),
          "noOfCapturesAllowed");
      context.read<BiometricCaptureControlProvider>().customSetterRightHand(
          int.parse(await BiometricsApi()
              .getMapValue("mosip.registration.num_of_fingerprint_retries")),
          "noOfCapturesAllowed");
      await _getOperatorOnboardingAttributes();
    }

    // context.read<BiometricCaptureControlProvider>().customSetterIris(
    //     context
    //         .read<GlobalProvider>()
    //         .thresholdValuesMap["mosip.registration.iris_threshold"]!,
    //     "thresholdPercentage");
    // context.read<BiometricCaptureControlProvider>().customSetterLeftHand(
    //     context.read<GlobalProvider>().thresholdValuesMap[
    //         "mosip.registration.leftslap_fingerprint_threshold"]!,
    //     "thresholdPercentage");
    // context.read<BiometricCaptureControlProvider>().customSetterRightHand(
    //     context.read<GlobalProvider>().thresholdValuesMap[
    //         "mosip.registration.rightslap_fingerprint_threshold"]!,
    //     "thresholdPercentage");
    // context.read<BiometricCaptureControlProvider>().customSetterThumbs(
    //     context.read<GlobalProvider>().thresholdValuesMap[
    //         "mosip.registration.thumbs_fingerprint_threshold"]!,
    //     "thresholdPercentage");
    // context.read<BiometricCaptureControlProvider>().customSetterFace(
    //     context
    //         .read<GlobalProvider>()
    //         .thresholdValuesMap["mosip.registration.face_threshold"]!,
    //     "thresholdPercentage");
    bool isPortrait =
        MediaQuery.of(context).orientation == Orientation.portrait;
    return StatefulWrapper(
        onInit: () {
          setInitialBioAttribute()
              .then((value) => debugPrint("State Initialized"));
        },
        child: (isPortrait) //Condition to rerender on changing of orientation
            ? OperatorBiometricsCaptureView()
            : OperatorBiometricsCaptureView());
  }
}
