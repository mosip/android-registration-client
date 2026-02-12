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
  /// Short-circuits when permission is already granted (always/whileInUse) to
  /// avoid redundant requestPermission() calls that may behave unexpectedly on
  /// some Android versions. "Only this time" still works: after app restart,
  /// permission is revoked, so we request again and show the dialog.
  /// Returns true if permission is granted, false otherwise.
  Future<bool> checkLocationPermissionForSession() async {
    final prefs = await SharedPreferences.getInstance();
    bool askedInThisSession = prefs.getBool(_locationAskedKey) ?? false;

    LocationPermission permission = await Geolocator.checkPermission();

    if (!askedInThisSession) {
      // Only request when not already granted - avoids redundant system dialog
      // for "Always allow" users; "Only this time" users get re-prompted after
      if (permission == LocationPermission.denied ||
          permission == LocationPermission.unableToDetermine) {
        permission = await Geolocator.requestPermission();
        
        // If user just denied permanently (e.g., clicked "Don't ask again"),
        // user can manually go to settings to change permission
        if (permission == LocationPermission.deniedForever) {
          await prefs.setBool(_locationAskedKey, true);
          return false;
        }
      }
      await prefs.setBool(_locationAskedKey, true);
    }

    // If permanently denied (from previous session), return false without
    // opening settings automatically - let the UI handle it if needed
    if (permission == LocationPermission.deniedForever) {
      return false;
    }

    if (permission == LocationPermission.denied) {
      return false;
    }

    // Only return true for explicitly granted permissions
    if (permission == LocationPermission.always ||
        permission == LocationPermission.whileInUse) {
      return true;
    }

    // Covers unableToDetermine or any unexpected value
    return false;
  }
}
