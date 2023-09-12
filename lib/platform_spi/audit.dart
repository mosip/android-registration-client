

import 'package:registration_client/platform_android/audit_impl.dart';

abstract class Audit {
  Future<void> performAudit();

  factory Audit() => getAuditImpl();
}
