import 'package:firebase_analytics/firebase_analytics.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'telemetry_event.dart';

class TelemetryManager {
  static final FirebaseAnalytics _analytics = FirebaseAnalytics.instance;

  static bool _isEnabled = true;// This should be loaded from user consent (e.g., SharedPreferences) in initialize()

  /// ✅ Initialize telemetry (call in main)
  static Future<void> initialize() async {
    final prefs = await SharedPreferences.getInstance();
    _isEnabled = prefs.getBool('telemetry_enabled') ?? false;

    await _analytics.setAnalyticsCollectionEnabled(_isEnabled);
  }

  /// ✅ Enable / Disable telemetry (for consent)
  static Future<void> setEnabled(bool value) async {
    _isEnabled = value;

    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('telemetry_enabled', value);

    await _analytics.setAnalyticsCollectionEnabled(value);
  }

  /// ✅ FIXED: This replaces your old logEvent usage
  static Future<void> logEvent(
    String name, [
    Map<String, dynamic>? data,
  ]) async {
    if (!_isEnabled) return;

    final event = TelemetryEvent(
      name: name,
      timestamp: DateTime.now(),
      data: data ?? {},
    );

    await addEvent(event);
  }

  /// ✅ CHANGED: Now async + Firebase instead of queue
  static Future<void> addEvent(TelemetryEvent event) async {
    if (!_isEnabled) return;

    await _analytics.logEvent(
      name: event.name,
      parameters: event.data,
    );
  }
}
// import 'telemetry_event.dart';
// import 'telemetry_logger.dart';

// class TelemetryManager {
//   static final List<TelemetryEvent> _queue = [];

//   // ✅ NEW METHOD (THIS FIXES YOUR ERROR)
//   static void logEvent(String name, [Map<String, dynamic>? data]) {
//     final event = TelemetryEvent(
//       name: name,
//       timestamp: DateTime.now(),
//       data: data ?? {},
//     );

//     addEvent(event);
//   }

//   static void addEvent(TelemetryEvent event) {
//     _queue.add(event);

//     // For now: send immediately (simple)
//     _flush();
//   }

//   static void _flush() {
//     for (var event in _queue) {
//       TelemetryLogger.log(event.toJson());
//     }
//     _queue.clear();
//   }
// }