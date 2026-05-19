import 'package:flutter/material.dart';
import 'telemetry_manager.dart'; // ✅ use manager, not service directly
import 'telemetry_event.dart';

class TelemetryNavigationObserver extends NavigatorObserver {

  @override
  void didPush(Route route, Route? previousRoute) {
    _trackScreen(route);
  }

  @override
  void didPop(Route route, Route? previousRoute) {
    if (previousRoute != null) {
      _trackScreen(previousRoute);
    }
  }

  void _trackScreen(Route route) {
    final screenName =
        route.settings.name ?? route.runtimeType.toString(); // ✅ FIX

    TelemetryManager.addEvent(
  TelemetryEvent(
    name: "screen_view",
    timestamp: DateTime.now(), 
    data: {"screen_name": screenName},
  ),
);
  }
}