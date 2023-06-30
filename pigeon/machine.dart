import 'package:pigeon/pigeon.dart';

class Machine {
  final Map<String?, String?> map;
  final String? errorCode;

  Machine({
    required this.map,
    this.errorCode,
  });
}

@HostApi()
abstract class MachineApi {
  @async
  Machine getMachineDetails();
  
  @async
  String getCenterName(String regCenterId, String langCode);

}
