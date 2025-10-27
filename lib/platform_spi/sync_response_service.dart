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
  Future<Sync> getPolicyKeySync(bool isManualSync, String jobId);
  Future<Sync> getGlobalParamsSync(bool isManualSync, String jobId);
  Future<Sync> getUserDetailsSync(bool isManualSync, String jobId);
  Future<Sync> getIDSchemaSync(bool isManualSync);
  Future<Sync> getMasterDataSync(bool isManualSync, String jobId);
  Future<Sync> getCaCertsSync(bool isManualSync, String jobId);
  Future<String> batchJob();
  Future<String> getPreRegIds(String jobId);

  Future<List<String?>> getReasonList(String langCode);

  Future<Sync> getKernelCertsSync(bool isManualSync, String jobId);
  Future<bool> getSyncAndUploadInProgressStatus();
  Future<bool> deleteAuditLogs(String jobId);
  Future<bool> deletePreRegRecords();

  Future<List<String?>> getActiveSyncJobs();
  Future<String> getLastSyncTimeByJobId(String jobId);
  Future<String> getNextSyncTimeByJobId(String jobId);

  factory SyncResponseService() => getSyncResponseServiceImpl();
}