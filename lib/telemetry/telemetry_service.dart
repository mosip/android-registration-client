import 'dart:io';
import 'package:package_info_plus/package_info_plus.dart';
import 'package:device_info_plus/device_info_plus.dart';

import 'telemetry_config.dart';
import 'telemetry_event.dart';
import 'telemetry_manager.dart';

class TelemetryService {
  static Map<String, dynamic> _deviceInfo = {};

  static Future<void> init() async {
    await _collectDeviceInfo();
    _trackAppStart();
  }

  static Future<void> _collectDeviceInfo() async {
    final deviceInfoPlugin = DeviceInfoPlugin();
    final packageInfo = await PackageInfo.fromPlatform();

    if (Platform.isAndroid) {
      final androidInfo = await deviceInfoPlugin.androidInfo;
      _deviceInfo = {
        "device_model": androidInfo.model,
        "os_version": androidInfo.version.release,
        "app_version": packageInfo.version,
      };
    }
  }

  static void _trackAppStart() {
    trackPerformance("app_start_time", DateTime.now().millisecondsSinceEpoch);
  }

  static void trackEvent(String name, {Map<String, dynamic>? data}) {
    if (!TelemetryConfig.enabled) return;

    final event = TelemetryEvent(
      name: name,
      timestamp: DateTime.now(),
      data: {
        ..._deviceInfo,
        ...?data,
      },
    );

    TelemetryManager.addEvent(event);
  }

  static void trackScreen(String screenName) {
    trackEvent("screen_view", data: {"screen": screenName});
  }

  static void logError(Object error) {
    trackEvent("error", data: {"message": error.toString()});
  }

  static void trackPerformance(String metric, int value) {
    trackEvent("performance", data: {
      "metric": metric,
      "value": value,
    });
  }
}