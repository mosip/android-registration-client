/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/pigeon/master_data_sync_pigeon.dart';
import 'package:registration_client/platform_spi/sync_response_service.dart';
import 'package:flutter/services.dart' show MethodChannel;

class SyncResponseServiceImpl implements SyncResponseService {
  @override
  Future<Sync> getPolicyKeySync(bool isManualSync, String jobId) async {
    late Sync syncResponse;
    try {
      syncResponse = await SyncApi().getPolicyKeySync(isManualSync, jobId);
    } on PlatformException {
      debugPrint('PolicyKeySync Api call failed, PlatformException');
    } catch (e) {
      debugPrint('PolicyKeySync has failed! ${e.toString()}');
    }
    return syncResponse;
  }

  @override
  Future<Sync> getGlobalParamsSync(bool isManualSync, String jobId) async {
    late Sync syncResponse;
    try {
      syncResponse = await SyncApi().getGlobalParamsSync(isManualSync, jobId);
    } on PlatformException {
      debugPrint('GlobalParamsSync Api call failed, PlatformException');
    } catch (e) {
      debugPrint('GlobalParamsSync has failed! ${e.toString()}');
    }
    return syncResponse;
  }

  @override
  Future<Sync> getUserDetailsSync(bool isManualSync, String jobId) async {
    late Sync syncResponse;
    try {
      syncResponse = await SyncApi().getUserDetailsSync(isManualSync, jobId);
    } on PlatformException {
      debugPrint('UserDetailsSync Api call failed, PlatformException');
    } catch (e) {
      debugPrint('UserDetailsSync has failed! ${e.toString()}');
    }
    return syncResponse;
  }

  @override
  Future<Sync> getIDSchemaSync(bool isManualSync) async {
    late Sync syncResponse;
    try {
      syncResponse = await SyncApi().getIDSchemaSync(isManualSync);
    } on PlatformException {
      debugPrint('IDSchemaSync Api call failed, PlatformException');
    } catch (e) {
      debugPrint('IDSchemaSync has failed! ${e.toString()}');
    }
    return syncResponse;
  }

  @override
  Future<Sync> getMasterDataSync(bool isManualSync, String jobId) async {
    late Sync syncResponse;
    try {
      syncResponse = await SyncApi().getMasterDataSync(isManualSync, jobId);
    } on PlatformException {
      debugPrint('MasterDataSync Api call failed, PlatformException');
    } catch (e) {
      debugPrint('MasterDataSync has failed! ${e.toString()}');
    }
    return syncResponse;
  }

  // Removed per request: use getMasterDataSync(bool) only

  @override
  Future<SyncTime> getLastSyncTime() async {
    late SyncTime syncTime;
    try {
      syncTime = await SyncApi().getLastSyncTime();
    } on PlatformException {
      debugPrint('Unable to get last sync time!');
    } catch (e) {
      debugPrint('Unable to get last sync time! ${e.toString()}');
    }
    return syncTime;
  }

  @override
  Future<Sync> getCaCertsSync(bool isManualSync, String jobId) async {
    late Sync syncResponse;
    try {
      syncResponse = await SyncApi().getCaCertsSync(isManualSync, jobId);
    } on PlatformException {
      debugPrint('CaCerts Api call failed, PlatformException');
    } catch (e) {
      debugPrint('CaCertsSync has failed! ${e.toString()}');
    }
    return syncResponse;
  }

  @override
  Future<String> batchJob() async {
    String batchJobResponse = "";
    try {
      batchJobResponse = await SyncApi().batchJob();
    } on PlatformException {
      debugPrint('Batch Job Api call failed, PlatformException');
    } catch (e) {
      debugPrint('Batch Job has failed! ${e.toString()}');
    }
    return batchJobResponse;
  }

  @override
  Future<String> getPreRegIds(String jobId) async {
    String preRegIdResponse = "";
    try {
      preRegIdResponse = await SyncApi().getPreRegIds(jobId);
    } on PlatformException {
      debugPrint('Application Id Api call failed, PlatformException');
    } catch (e) {
      debugPrint('Application Id call has failed! ${e.toString()}');
    }
    return preRegIdResponse;
  }

  @override
  Future<Sync> getKernelCertsSync(bool isManualSync, String jobId) async {
    late Sync syncResponse;
    try {
      syncResponse = await SyncApi().getKernelCertsSync(isManualSync, jobId);
    } on PlatformException {
      debugPrint('KernelCerts Api call failed, PlatformException');
    } catch (e) {
      debugPrint('KernelCertsSync has failed! ${e.toString()}');
    }
    return syncResponse;
  }

  @override
  Future<List<String?>> getReasonList(String langCode) async {
    late List<String?> reasonList;
    try {
      reasonList = await SyncApi().getReasonList(langCode);
      if (reasonList.isEmpty) {
        reasonList = await SyncApi().getReasonList("eng");
      }
    } on PlatformException {
      debugPrint('KernelCerts Api call failed, PlatformException');
    } catch (e) {
      debugPrint('KernelCertsSync has failed! ${e.toString()}');
    }
    return reasonList;
  }

  @override
  Future<bool> getSyncAndUploadInProgressStatus() async {
    bool syncAndUploadResponse = false;
    try {
      syncAndUploadResponse = await SyncApi().getSyncAndUploadInProgressStatus();
    } on PlatformException {
      debugPrint('getSyncAndUploadInProgressStatus call failed, PlatformException');
    } catch (e) {
      debugPrint('getSyncAndUploadInProgressStatus has failed! ${e.toString()}');
    }
    return syncAndUploadResponse;
  }

  @override
  Future<List<String?>> getActiveSyncJobs() async {
    try {
      final result = await SyncApi().getActiveSyncJobs();
      final list = result;
      return list;
    } on PlatformException catch (e) {
      debugPrint('getActiveSyncJobs PlatformException: ${e.message}');
      return const [];
    } catch (e) {
      debugPrint('getActiveSyncJobs failed: $e');
      return const [];
    }
  }

  @override
  Future<bool> deleteAuditLogs(String jobId) async {
    try {
      final deleteResponse = await SyncApi().deleteAuditLogs(jobId);
      return deleteResponse;
    } on PlatformException catch (e) {
      debugPrint('deleteAuditLogs PlatformException: ${e.message}');
      return false;
    } catch (e) {
      debugPrint('deleteAuditLogs failed: $e');
      return false;
    }
  }

  @override
  Future<bool> deletePreRegRecords(String jobId) async {
    try {
      final deleteResponse = await SyncApi().deletePreRegRecords(jobId);
      return deleteResponse;
    } on PlatformException catch (e) {
      debugPrint('deleteAuditLogs PlatformException: ${e.message}');
      return false;
    } catch (e) {
      debugPrint('deleteAuditLogs failed: $e');
      return false;
    }
  }

  @override
  Future<bool> deleteRegistrationPackets(String jobId) async {
    try {
      final deleteResponse = await SyncApi().deleteRegistrationPackets(jobId);
      return deleteResponse;
    } on PlatformException catch (e) {
      debugPrint('deleteRegistrationPackets PlatformException: ${e.message}');
      return false;
    } catch (e) {
      debugPrint('deleteRegistrationPackets failed: $e');
      return false;
    }
  }

  @override
  Future<bool> syncPacketStatus(String jobId) async {
    try {
      final syncResponse = await SyncApi().syncPacketStatus(jobId);
      return syncResponse;
    } on PlatformException catch (e) {
      debugPrint('syncPacketStatus PlatformException: ${e.message}');
      return false;
    } catch (e) {
      debugPrint('syncPacketStatus failed: $e');
      return false;
    }
  }

  @override
  Future<String> getLastSyncTimeByJobId(String jobId) async{
    try {
      final lastSyncTime = await SyncApi().getLastSyncTimeByJobId(jobId);
      return lastSyncTime;
    } on PlatformException catch (e) {
      debugPrint('lastSync PlatformException: ${e.message}');
      return "false";
    } catch (e) {
      debugPrint('lastSync failed: $e');
      return "false";
    }
  }

  @override
  Future<String> getNextSyncTimeByJobId(String jobId) async{
    try {
      final nextSyncTime = await SyncApi().getNextSyncTimeByJobId(jobId);
      return nextSyncTime;
    } on PlatformException catch (e) {
      debugPrint('nextSync PlatformException: ${e.message}');
      return "false";
    } catch (e) {
      debugPrint('nextSync failed: $e');
      return "false";
    }
  }
}

SyncResponseService getSyncResponseServiceImpl() => SyncResponseServiceImpl();
