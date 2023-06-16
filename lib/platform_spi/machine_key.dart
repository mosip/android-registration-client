import 'package:registration_client/pigeon/machine_pigeon.dart';

abstract class MachineKey {
  Future<Machine> getMachineKeys();
}