import 'package:registration_client/pigeons/machine_pigeon.dart';

abstract class MachineKey {
  Future<Machine> getMachineKeys();
}