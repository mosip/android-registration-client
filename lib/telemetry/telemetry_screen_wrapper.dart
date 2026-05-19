import 'package:flutter/material.dart';
import 'telemetry_service.dart';

class TelemetryScreenWrapper extends StatefulWidget {
  final String screenName;
  final Widget child;

  const TelemetryScreenWrapper({
    super.key,
    required this.screenName,
    required this.child,
  });

  @override
  State<TelemetryScreenWrapper> createState() =>
      _TelemetryScreenWrapperState();
}

class _TelemetryScreenWrapperState extends State<TelemetryScreenWrapper> {
  @override
  void initState() {
    super.initState();

    TelemetryService.trackScreen(widget.screenName);
  }

  @override
  Widget build(BuildContext context) {
    return widget.child;
  }
}