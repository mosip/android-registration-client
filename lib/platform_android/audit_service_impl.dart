/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/pigeon/audit_response_pigeon.dart';
import 'package:registration_client/platform_spi/audit_service.dart';

class AuditImpl implements Audit {
  @override
  Future<void> performAudit(String id, String componentId) async {
    try {
      await AuditResponseApi().audit(id, componentId);
    } on PlatformException {
      debugPrint('AuditResponseApi call failed');
    } catch (e) {
      debugPrint('Audit Failed: $e');
    }
  }
  
}

Audit getAuditImpl() => AuditImpl();
