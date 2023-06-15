import 'package:pigeon/pigeon.dart';

class Machine {
  final Map<String?, String?> map;
  final String? error;

  Machine({
    required this.map,
    this.error,
  });
}

@HostApi()
abstract class MachineApi {
  Machine getMachineDetails();
}
