import '../pigeon/location_response_pigeon.dart';
import '../platform_android/location_impl.dart';

abstract class Location {
  Future<LocationResponse?> getLocationDetails(
      String langCode, List<String> hierarchyName);

  factory Location() => getLocationImpl();
}
