class TelemetryConfig {
  static bool enabled = true;

  // later you can load this from app_config or backend
  static void update({required bool isEnabled}) {
    enabled = isEnabled;
  }
}