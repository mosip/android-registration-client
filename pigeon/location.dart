import 'package:pigeon/pigeon.dart';

@HostApi()
abstract class LocationApi {
  @async
  void setMachineLocation(double latitude, double longitude);
  @async
  String getGpsEnableFlag();
}