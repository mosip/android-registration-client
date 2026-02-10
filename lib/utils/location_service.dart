/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:geolocator/geolocator.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// Session-based location permission service.
/// Tracks if permission was requested in the current session so we can
/// re-prompt on next login when user had chosen "Only this time" previously.
class LocationService {
  static final LocationService instance = LocationService._();
  LocationService._();

  static const String _locationAskedKey = 'location_asked_this_session';

  /// Call on login success. Clears session flag so permission will be
  /// requested again if user had chosen "Only this time" previously.
  Future<void> startNewSession() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_locationAskedKey);
  }

  /// Call on logout. Clears session flag for next login.
  Future<void> endSession() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_locationAskedKey);
  }

  /// Checks and optionally requests location permission.
  /// Only requests permission if not already asked in this session.
  /// Returns true if permission is granted, false otherwise.
  Future<bool> checkLocationPermissionForSession() async {
    final prefs = await SharedPreferences.getInstance();
    bool askedInThisSession = prefs.getBool(_locationAskedKey) ?? false;

    LocationPermission permission = await Geolocator.checkPermission();

    if (!askedInThisSession) {
      permission = await Geolocator.requestPermission();
      await prefs.setBool(_locationAskedKey, true);
    }

    if (permission == LocationPermission.denied ||
        permission == LocationPermission.deniedForever) {
      return false;
    }

    return true;
  }
}
