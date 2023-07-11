import 'package:registration_client/pigeon/machine_pigeon.dart';
import 'package:registration_client/platform_android/machine_key_impl.dart';

abstract class MachineKey {
  Future<Machine> getMachineKeys();

  Future<String> getCenterName(String regCenterId, String langCode);

  factory MachineKey() => getMachineKeyImpl();
}
