import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;
import 'telemetry_event.dart';

class TelemetryManager {
  static final List<TelemetryEvent> _queue = [];

  static bool _isEnabled = true; // This should be loaded from user consent (e.g., SharedPreferences) in initialize()

  /// ✅ Initialize telemetry (call in main)
  static Future<void> initialize() async {
    final prefs = await SharedPreferences.getInstance();
     _isEnabled = true;// prefs.getBool('telemetry_enabled') ?? false;
  }

  /// ✅ Enable / Disable telemetry (for consent)
  static Future<void> setEnabled(bool value) async {
    _isEnabled = value;

    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('telemetry_enabled', value);
  }

  /// ✅ FIXED: This replaces your old logEvent usage
  static Future<void> logEvent(String name, [
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

//   static void _flush() {
//     // for (var event in _queue) {
//     //   // TODO: Replace print with your custom API call or local database insert
//     //   print("Flushing event: ${event.toJson()}");
//     // }
//     _queue.clear();
//   }
// }
  static void _flush() async {
    if (_queue.isEmpty) return;

    // 1. Create a snapshot of the queue and clear the original immediately
    // This prevents the "Concurrent modification during iteration" error
    final List<TelemetryEvent> eventsToSend = List.from(_queue);
    _queue.clear();

    for (var event in eventsToSend) {
      print("DEBUG: Attempting to send telemetry: ${event.name}");

      try {
        // Ensure the URL matches your current active ngrok session
        final url = Uri.parse(
            "https://moshe-postmesenteric-elvina.ngrok-free.app");

        print("DEBUG: Hitting URL: $url");

        // 2. Use http.post with TUS headers to force file creation in Docker
        final response = await http.post(
          url,
          headers: {
            "Tus-Resumable": "1.0.0",
            "Upload-Length": "100", // Required by TUSD to initialize a file
            "User-Agent": "Flutter-Client",
            "Content-Type": "application/offset+octet-stream",
          },
        );

        print("DEBUG: Response Status: ${response.statusCode}");

        if (response.statusCode == 201) {
          print("DEBUG: SUCCESS! File created in Docker data folder.");
        }
      } catch (e) {
        print("DEBUG: Failed to reach backend: $e");
      }
    }
  }
}