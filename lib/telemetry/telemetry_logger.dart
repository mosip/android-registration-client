class TelemetryLogger {
  static void log(Map<String, dynamic> event) {
    // later replace with API call
    // for now this is your "mock backend"
    // keep it clean for debugging
    print("[TELEMETRY] $event");
  }
}