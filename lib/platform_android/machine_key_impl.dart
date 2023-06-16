import 'dart:collection';

import 'package:flutter/services.dart';
import 'package:registration_client/pigeon/machine_pigeon.dart';
import 'package:registration_client/platform_spi/machine_key.dart';

class MachineKeyImpl extends MachineKey {

  @override
  Future<Machine> getMachineKeys() async {
    late Machine machine;
    try {
      machine = await MachineApi().getMachineDetails();
    } on PlatformException {
      print('MachineApi call failed!');
    } catch(e) {
      print('Machine not fetched! ${e.toString()}');
    } 
    
    return machine;
  }
}