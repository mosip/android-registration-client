
import 'package:registration_client/platform_android/location_service_impl.dart';

abstract class LocationService {
  Future<void> setMachineLocation(double latitude, double longitude);

  Future<String> getGpsEnableFlag();

  factory LocationService() => getLocationServiceImpl();
}