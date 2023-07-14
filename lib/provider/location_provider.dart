import 'dart:developer';

import 'package:flutter/material.dart';

import '../pigeon/location_response_pigeon.dart';
import 'package:registration_client/platform_spi/location.dart';

class LocationProvider with ChangeNotifier {
  LocationResponse? _locationResponse;

  get locationResponse => _locationResponse;

  final Location location = Location();

  setLocationResponse(
    String langCode,
  ) async {
    List<String> hierarchyName = [
      "Country",
      "Region",
      "Province",
      "City",
      "Zone",
      "Postal Code"
    ];

    _locationResponse =
        await location.getLocationDetails(langCode, hierarchyName);
    if (_locationResponse == null) {
      log("Not Found Lopcation");
      return;
    }
    notifyListeners();
  }
}
