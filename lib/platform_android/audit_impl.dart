import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/pigeon/audit_response_pigeon.dart';
import 'package:registration_client/platform_spi/audit.dart';

class AuditImpl implements Audit {
  @override
  Future<void> performAudit() async {
    try {
      await AuditResponseApi().audit();
    } on PlatformException {
      debugPrint('AuditResponseApi call failed');
    } catch (e) {
      debugPrint('Audit Failed: $e');
    }
  }
  
}

Audit getAuditImpl() => AuditImpl();
