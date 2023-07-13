import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/pigeon/location_response_pigeon.dart';

import '../platform_spi/location.dart';

class LocationImpl implements Location {
  @override
  Future<LocationResponse?> getLocationDetails(
      String langCode, List<String> hierarchyName) async {
    LocationResponse? locationResponse;
    try {
      locationResponse = await LocationResponseApi()
          .fetchLocationList(langCode, hierarchyName);
    } on PlatformException {
      debugPrint('Platform Exception');
    } catch (e) {
      debugPrint('Not found Location');
    }

    return locationResponse;
  }
}

Location getLocationImpl() => LocationImpl();
