/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:registration_client/pigeon/master_data_sync_pigeon.dart';

abstract class SyncResponseService {
  Future<SyncTime> getLastSyncTime();
  Future<Sync> getPolicyKeySync();
  Future<Sync> getGlobalParamsSync();
  Future<Sync> getUserDetailsSync();
  Future<Sync> getIDSchemaSync();
  Future<Sync> getMasterDataSync();
  Future<Sync> getCaCertsSync();
}