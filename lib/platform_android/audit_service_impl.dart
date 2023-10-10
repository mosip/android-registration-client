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
