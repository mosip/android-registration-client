/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'dart:developer';

import 'package:flutter_windowmanager/flutter_windowmanager.dart';

class SecureScreenService {
  SecureScreenService._();

  static int _refCount = 0;

  static Future<void> acquire() async {
    _refCount++;
    if (_refCount == 1) {
      try {
        await FlutterWindowManager.addFlags(FlutterWindowManager.FLAG_SECURE);
      } catch (e) {
        _refCount--;
        log('SecureScreenService: failed to add FLAG_SECURE – $e');
        rethrow;
      }
    }
  }

  static Future<void> release() async {
    if (_refCount <= 0) {
      log('SecureScreenService: release() called with refCount=$_refCount; ignoring.');
      return;
    }
    _refCount--;
    if (_refCount == 0) {
      try {
        await FlutterWindowManager.clearFlags(FlutterWindowManager.FLAG_SECURE);
      } catch (e) {
        _refCount++;
        log('SecureScreenService: failed to clear FLAG_SECURE – $e');
        rethrow;
      }
    }
  }
}
