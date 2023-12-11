import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/provider/biometric_capture_control_provider.dart';
import 'package:registration_client/ui/process_ui/widgets/biometric_capture_control_landscape.dart';
import 'package:registration_client/ui/process_ui/widgets_mobile/biometric_capture_control_portrait.dart';

class BiometricCaptureControl extends StatelessWidget {
  const BiometricCaptureControl({super.key, required this.e});
  final Field e;

  @override
  Widget build(BuildContext context) {
    bool isPortrait =
        MediaQuery.of(context).orientation == Orientation.portrait;
    return ChangeNotifierProvider(create: (_)=>BiometricCaptureControlProvider(),child: (isPortrait)
            ? BiometricCaptureControlPortrait(field: e)
            : BiometricCaptureControlLandscape(field: e),);
  }
}