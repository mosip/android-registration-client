import 'package:pigeon/pigeon.dart';

@HostApi()
abstract class TelemetryApi {
  void logMetric(String metricJson);
}