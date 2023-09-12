import 'package:pigeon/pigeon.dart';

@HostApi()
abstract class AuditResponseApi {
  @async
  void audit();

  @async
  void loginLoadedAudit();
}
