/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:registration_client/pigeon/machine_pigeon.dart';
import 'package:registration_client/platform_android/machine_key_service_impl.dart';

abstract class MachineKeyService {
  Future<Machine> getMachineKeys();

  Future<String> getCenterName(String regCenterId, String langCode);

  factory MachineKeyService() => getMachineKeyServiceImpl();
}
