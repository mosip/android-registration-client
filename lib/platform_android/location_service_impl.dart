import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/pigeon/location_pigeon.dart';
import 'package:registration_client/platform_spi/location_service.dart';

class LocationServiceImpl implements LocationService {

  @override
  Future<void> setMachineLocation(double latitude, double longitude) async {
    try {
      await LocationApi().setMachineLocation(latitude, longitude);
    } on PlatformException {
      debugPrint('location call failed');
    } catch (e) {
      debugPrint('location call failed $e');
    }
  }

  @override
  Future<String> getGpsEnableFlag() async {
    String gpsEnableFlag = "";
    try {
      gpsEnableFlag = await LocationApi().getGpsEnableFlag();
    } on PlatformException {
      debugPrint("Location Api failed!");
    }  catch (e) {
      debugPrint("Location fetch error: $e");
    }
    return gpsEnableFlag;
  }
}

LocationService getLocationServiceImpl() => LocationServiceImpl();