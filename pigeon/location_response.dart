// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'package:pigeon/pigeon.dart';

class LocationResponse {
  final List<String?> countryList;
  final List<String?> regionList;
  final List<String?> provinceList;
  final List<String?> cityList;
  final List<String?> zoneList;
  final List<String?> postalCodeList;

  LocationResponse({
    required this.countryList,
    required this.regionList,
    required this.provinceList,
    required this.cityList,
    required this.zoneList,
    required this.postalCodeList,
  });
}

@HostApi()
abstract class LocationResponseApi {
  @async
  LocationResponse fetchLocationList(
      String langCode, List<String> hierarchyName);
}
