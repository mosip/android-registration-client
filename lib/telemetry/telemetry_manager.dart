import 'package:shared_preferences/shared_preferences.dart';

import 'telemetry_event.dart';

class TelemetryManager {
  static final List<TelemetryEvent> _queue = [];

  static bool _isEnabled = false;// This should be loaded from user consent (e.g., SharedPreferences) in initialize()

  /// ✅ Initialize telemetry (call in main)
  static Future<void> initialize() async {
    final prefs = await SharedPreferences.getInstance();
    _isEnabled = prefs.getBool('telemetry_enabled') ?? false;
  }

  /// ✅ Enable / Disable telemetry (for consent)
  static Future<void> setEnabled(bool value) async {
    _isEnabled = value;

    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('telemetry_enabled', value);
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

  /// ✅ CHANGED: Reverted to local queue
  static Future<void> addEvent(TelemetryEvent event) async {
    if (!_isEnabled) return;

    _queue.add(event);
    _flush();
  }

  static void _flush() {
    for (var event in _queue) {
      // TODO: Replace print with your custom API call or local database insert
      print("Flushing event: ${event.toJson()}");
    }
    _queue.clear();
  }
}
