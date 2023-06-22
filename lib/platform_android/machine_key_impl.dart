import 'package:flutter/material.dart';
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
      debugPrint('MachineApi call failed!');
    } catch (e) {
      debugPrint('Machine not fetched! ${e.toString()}');
    }

    return machine;
  }
  
  @override
  Future<String> getCenterName(String regCenterId) async {
    String regCenterName;
    try {
      regCenterName = await MachineApi().getCenterName(regCenterId);
    } on PlatformException catch (e) {
      debugPrint('Machine not found $e');
      regCenterName = "";
    }

    return regCenterName;
  }
}
