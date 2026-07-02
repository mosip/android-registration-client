import 'package:pigeon/pigeon.dart';

@ConfigurePigeon(PigeonOptions(
  dartOut: 'lib/pigeon/telemetry_pigeon.dart',
  javaOut: 'android/app/src/main/java/io/mosip/registration_client/model/TelemetryPigeon.java',
  javaOptions: JavaOptions(package: 'io.mosip.registration_client.model'),
))

@HostApi()
abstract class TelemetryApi {
  void logMetric(String metricJson);
}