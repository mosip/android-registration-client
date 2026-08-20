import 'dart:convert';
import '../pigeon/telemetry_pigeon.dart';

/// Flutter-side telemetry service.
/// Mirrors desktop LoggingJsonMeterRegistry event structure.
/// Sends events via Pigeon → AndroidMetricCollector → metrics.log
class TelemetryService {
  TelemetryService._internal();
  static final TelemetryService instance = TelemetryService._internal();

  final TelemetryApi _api = TelemetryApi();

  // ─── Core event logger ────────────────────────────────────────────
  void logEvent(String name, Map<String, dynamic> properties) {
    try {
      final payload = {
        'name': name,
        'type': 'gauge',
        ...properties,
      };
      _api.logMetric(jsonEncode(payload));
    } catch (e) {
      // Never crash the app due to telemetry failure
    }
  }

  // ─── Registration lifecycle events ────────────────────────────────
  void onLoginSuccess(String userId) {
    logEvent('registration.login.success', {
      'value': 1,
      'unit': 'count',
      'user': userId,
    });
  }

  void onLoginFailure(String reason) {
    logEvent('registration.login.failure', {
      'value': 1,
      'unit': 'count',
      'reason': reason,
    });
  }

  void onRegistrationStarted(String processName) {
    logEvent('registration.started', {
      'value': 1,
      'unit': 'count',
      'process': processName,
    });
  }

  void onBiometricCaptured(String modality, bool success) {
    logEvent('registration.biometric.captured', {
      'value': success ? 1 : 0,
      'unit': 'count',
      'modality': modality,
    });
  }

  void onPacketCreated(bool success) {
    logEvent('registration.packet.created', {
      'value': success ? 1 : 0,
      'unit': 'count',
    });
  }

  void onPacketUploaded(bool success) {
    logEvent('registration.packet.uploaded', {
      'value': success ? 1 : 0,
      'unit': 'count',
    });
  }

  void onRegistrationCompleted(String status) {
    logEvent('registration.completed', {
      'value': 1,
      'unit': 'count',
      'status': status,
    });
  }

  void onSyncCompleted(bool success) {
    logEvent('registration.sync.completed', {
      'value': success ? 1 : 0,
      'unit': 'count',
    });
  }

/// Logs screen navigation events for telemetry (AC2)
void onScreenView(String screenName, {String? previousScreen}) {
logEvent('registration.screen.navigated', {
'value': 1,
'unit': 'count',
'screen_name': screenName,
if (previousScreen != null) 'previous_screen': previousScreen,
});
}

/// Logs button click / action events
  void onButtonClick(String buttonName, {String? screenName}) {
    logEvent('registration.button.clicked', {
      'value': 1,
      'unit': 'count',
      'button_name': buttonName,
      if (screenName != null) 'screen_name': screenName,
    });
  }
/// Logs operational task selection events
  void onTaskSelected(String taskName, {String? category}) {
    logEvent('registration.task.selected', {
      'value': 1,
      'unit': 'count',
      'task_name': taskName,
      if (category != null) 'category': category,
    });
  }

}