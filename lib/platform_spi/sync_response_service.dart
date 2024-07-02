/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:registration_client/pigeon/master_data_sync_pigeon.dart';
import 'package:registration_client/platform_android/sync_response_service_impl.dart';

abstract class SyncResponseService {
  Future<SyncTime> getLastSyncTime();
  Future<Sync> getPolicyKeySync(bool isManualSync);
  Future<Sync> getGlobalParamsSync(bool isManualSync);
  Future<Sync> getUserDetailsSync(bool isManualSync);
  Future<Sync> getIDSchemaSync(bool isManualSync);
  Future<Sync> getMasterDataSync(bool isManualSync);
  Future<Sync> getCaCertsSync(bool isManualSync);
  Future<String> batchJob();
  Future<String> getPreRegIds();
  Future<Sync> getKernelCertsSync(bool isManualSync);
  Future<bool> getSyncAndUploadInProgressStatus();

  factory SyncResponseService() => getSyncResponseServiceImpl();
}