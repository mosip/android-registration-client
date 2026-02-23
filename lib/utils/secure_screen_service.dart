/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'dart:developer';

import 'package:flutter/foundation.dart';
import 'package:registration_client/pigeon/secure_screen_pigeon.dart';

class SecureScreenService {
  SecureScreenService._();

  static final SecureScreenApi _api = SecureScreenApi();

  static int _refCount = 0;
  static Future<void> _operationQueue = Future.value();

  static Future<void> acquire() async {
    if (defaultTargetPlatform != TargetPlatform.android) return;
    _operationQueue = _operationQueue
        .then<void>((_) {}, onError: (_) {}) // recover from any prior failure
        .then((_) async {
      _refCount++;
      if (_refCount == 1) {
        try {
          await _api.addFlagSecure();
        } catch (e) {
          _refCount--;
          log('SecureScreenService: failed to add FLAG_SECURE – $e');
          rethrow;
        }
      }
    });
    await _operationQueue;
  }

  static Future<void> release() async {
    if (defaultTargetPlatform != TargetPlatform.android) return;
    _operationQueue = _operationQueue
        .then<void>((_) {}, onError: (_) {}) // recover from any prior failure
        .then((_) async {
      if (_refCount <= 0) {
        log('SecureScreenService: release() called with refCount=$_refCount; ignoring.');
        return;
      }
      _refCount--;
      if (_refCount == 0) {
        try {
          await _api.clearFlagSecure();
        } catch (e) {
          _refCount++;
          log('SecureScreenService: failed to clear FLAG_SECURE – $e');
          rethrow;
        }
      }
    });
    await _operationQueue;
  }
}
