import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/pigeon/biometrics_pigeon.dart';
import 'package:registration_client/provider/biometric_capture_control_provider.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/ui/process_ui/widgets/biometric_capture_control_landscape.dart';
import 'package:registration_client/ui/process_ui/widgets_mobile/biometric_capture_control_portrait.dart';
import 'package:registration_client/utils/stateful_wrapper.dart';

class BiometricCaptureControl extends StatelessWidget {
  const BiometricCaptureControl({super.key, required this.e});
  final Field e;

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
        lazy: false,
        create: (_) => BiometricCaptureControlProvider(),
        child: BiometricCaptureControlInitialization(
          field: e,
        ));
  }
}

class BiometricCaptureControlInitialization extends StatelessWidget {
  const BiometricCaptureControlInitialization({super.key, required this.field});
  final Field field;

  @override
  Widget build(BuildContext context) {
    setInitialBioAttribute() async {
      if (field.conditionalBioAttributes!.first!.ageGroup!
              .compareTo(context.read<GlobalProvider>().ageGroup) ==
          0) {
        if (field.conditionalBioAttributes!.first!.bioAttributes!
                .contains("leftEye") &&
            field.conditionalBioAttributes!.first!.bioAttributes!
                .contains("rightEye")) {
          context.read<BiometricCaptureControlProvider>().biometricAttribute =
              "Iris";
        } else if (field.conditionalBioAttributes!.first!.bioAttributes!
                .contains("rightIndex") &&
            field.conditionalBioAttributes!.first!.bioAttributes!
                .contains("rightLittle") &&
            field.conditionalBioAttributes!.first!.bioAttributes!
                .contains("rightRing") &&
            field.conditionalBioAttributes!.first!.bioAttributes!
                .contains("rightMiddle")) {
          context.read<BiometricCaptureControlProvider>().biometricAttribute =
              "Right Hand";
        } else if (field.conditionalBioAttributes!.first!.bioAttributes!
                .contains("leftIndex") &&
            field.conditionalBioAttributes!.first!.bioAttributes!
                .contains("leftLittle") &&
            field.conditionalBioAttributes!.first!.bioAttributes!
                .contains("leftRing") &&
            field.conditionalBioAttributes!.first!.bioAttributes!
                .contains("leftMiddle")) {
          context.read<BiometricCaptureControlProvider>().biometricAttribute =
              "Left Hand";
        } else if (field.conditionalBioAttributes!.first!.bioAttributes!
                .contains("leftThumb") &&
            field.conditionalBioAttributes!.first!.bioAttributes!
                .contains("rightThumb")) {
          context.read<BiometricCaptureControlProvider>().biometricAttribute =
              "Thumbs";
        } else {
          context.read<BiometricCaptureControlProvider>().biometricAttribute =
              "Face";
        }
      } else {
        if (field.bioAttributes!.contains("leftEye") &&
            field.bioAttributes!.contains("rightEye")) {
          context.read<BiometricCaptureControlProvider>().biometricAttribute =
              "Iris";
        } else if (field.bioAttributes!.contains("rightIndex") &&
            field.bioAttributes!.contains("rightLittle") &&
            field.bioAttributes!.contains("rightRing") &&
            field.bioAttributes!.contains("rightMiddle")) {
          context.read<BiometricCaptureControlProvider>().biometricAttribute =
              "Right Hand";
        } else if (field.bioAttributes!.contains("leftIndex") &&
            field.bioAttributes!.contains("leftLittle") &&
            field.bioAttributes!.contains("leftRing") &&
            field.bioAttributes!.contains("leftMiddle")) {
          context.read<BiometricCaptureControlProvider>().biometricAttribute =
              "Left Hand";
        } else if (field.bioAttributes!.contains("leftThumb") &&
            field.bioAttributes!.contains("rightThumb")) {
          context.read<BiometricCaptureControlProvider>().biometricAttribute =
              "Thumbs";
        } else {
          context.read<BiometricCaptureControlProvider>().biometricAttribute =
              "Face";
        }
      }
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
    }

    bool isPortrait =
        MediaQuery.of(context).orientation == Orientation.portrait;

    context.read<BiometricCaptureControlProvider>().customSetterIris(
        context
            .read<GlobalProvider>()
            .thresholdValuesMap["mosip.registration.iris_threshold"]!,
        "thresholdPercentage");
    context.read<BiometricCaptureControlProvider>().customSetterLeftHand(
        context.read<GlobalProvider>().thresholdValuesMap[
            "mosip.registration.leftslap_fingerprint_threshold"]!,
        "thresholdPercentage");
    context.read<BiometricCaptureControlProvider>().customSetterRightHand(
        context.read<GlobalProvider>().thresholdValuesMap[
            "mosip.registration.rightslap_fingerprint_threshold"]!,
        "thresholdPercentage");
    context.read<BiometricCaptureControlProvider>().customSetterThumbs(
        context.read<GlobalProvider>().thresholdValuesMap[
            "mosip.registration.thumbs_fingerprint_threshold"]!,
        "thresholdPercentage");
    context.read<BiometricCaptureControlProvider>().customSetterFace(
        context
            .read<GlobalProvider>()
            .thresholdValuesMap["mosip.registration.face_threshold"]!,
        "thresholdPercentage");

    if (context
        .read<GlobalProvider>()
        .fieldInputValue
        .containsKey("${field.id}")) {
      if (context.read<GlobalProvider>().fieldInputValue[field.id].isNotEmpty) {
        if (context.read<BiometricCaptureControlProvider>().getElementPosition(
                context.read<GlobalProvider>().fieldInputValue[field.id],
                "Iris") !=
            -1) {
          context.read<BiometricCaptureControlProvider>().iris = context
              .read<GlobalProvider>()
              .fieldInputValue[field.id]
              .elementAt(context
                  .read<BiometricCaptureControlProvider>()
                  .getElementPosition(
                      context.read<GlobalProvider>().fieldInputValue[field.id],
                      "Iris"));
        }
        if (context.read<BiometricCaptureControlProvider>().getElementPosition(
                context.read<GlobalProvider>().fieldInputValue[field.id],
                "Right Hand") !=
            -1) {
          context.read<BiometricCaptureControlProvider>().rightHand = context
              .read<GlobalProvider>()
              .fieldInputValue[field.id]
              .elementAt(context
                  .read<BiometricCaptureControlProvider>()
                  .getElementPosition(
                      context.read<GlobalProvider>().fieldInputValue[field.id],
                      "Right Hand"));
        }
        if (context.read<BiometricCaptureControlProvider>().getElementPosition(
                context.read<GlobalProvider>().fieldInputValue[field.id],
                "Left Hand") !=
            -1) {
          context.read<BiometricCaptureControlProvider>().leftHand = context
              .read<GlobalProvider>()
              .fieldInputValue[field.id]
              .elementAt(context
                  .read<BiometricCaptureControlProvider>()
                  .getElementPosition(
                      context.read<GlobalProvider>().fieldInputValue[field.id],
                      "Left Hand"));
        }
        if (context.read<BiometricCaptureControlProvider>().getElementPosition(
                context.read<GlobalProvider>().fieldInputValue[field.id],
                "Thumbs") !=
            -1) {
          context.read<BiometricCaptureControlProvider>().thumbs = context
              .read<GlobalProvider>()
              .fieldInputValue[field.id]
              .elementAt(context
                  .read<BiometricCaptureControlProvider>()
                  .getElementPosition(
                      context.read<GlobalProvider>().fieldInputValue[field.id],
                      "Thumbs"));
        }
        if (context.read<BiometricCaptureControlProvider>().getElementPosition(
                context.read<GlobalProvider>().fieldInputValue[field.id],
                "Face") !=
            -1) {
          context.read<BiometricCaptureControlProvider>().face = context
              .read<GlobalProvider>()
              .fieldInputValue[field.id]
              .elementAt(context
                  .read<BiometricCaptureControlProvider>()
                  .getElementPosition(
                      context.read<GlobalProvider>().fieldInputValue[field.id],
                      "Face"));
        }
        if (context.read<BiometricCaptureControlProvider>().getElementPosition(
                context.read<GlobalProvider>().fieldInputValue[field.id],
                "Exception") !=
            -1) {
          context.read<BiometricCaptureControlProvider>().exception = context
              .read<GlobalProvider>()
              .fieldInputValue[field.id]
              .elementAt(context
                  .read<BiometricCaptureControlProvider>()
                  .getElementPosition(
                      context.read<GlobalProvider>().fieldInputValue[field.id],
                      "Exception"));
        }
      }
    }
    return StatefulWrapper(
        onInit: () {
          setInitialBioAttribute()
              .then((value) => debugPrint("State Initialized"));
        },
        child: (isPortrait)
            ? BiometricCaptureControlPortrait(field: field)
            : BiometricCaptureControlLandscape(field: field));
  }
}
