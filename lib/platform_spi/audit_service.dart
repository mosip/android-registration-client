import 'package:registration_client/platform_android/audit_service_impl.dart';

abstract class Audit {
  Future<void> performAudit(String id, String componentId);

  factory Audit() => getAuditImpl();
}
