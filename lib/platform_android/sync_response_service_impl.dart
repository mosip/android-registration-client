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
  Future<Sync> getPolicyKeySync(bool isManualSync) async {
    late Sync syncResponse;
    try {
      syncResponse = await SyncApi().getPolicyKeySync(isManualSync);
    } on PlatformException {
      debugPrint('PolicyKeySync Api call failed, PlatformException');
    } catch (e) {
      debugPrint('PolicyKeySync has failed! ${e.toString()}');
    }
    return syncResponse;
  }

  @override
  Future<Sync> getGlobalParamsSync(bool isManualSync) async {
    late Sync syncResponse;
    try {
      syncResponse = await SyncApi().getGlobalParamsSync(isManualSync);
    } on PlatformException {
      debugPrint('GlobalParamsSync Api call failed, PlatformException');
    } catch (e) {
      debugPrint('GlobalParamsSync has failed! ${e.toString()}');
    }
    return syncResponse;
  }

  @override
  Future<Sync> getUserDetailsSync(bool isManualSync) async {
    late Sync syncResponse;
    try {
      syncResponse = await SyncApi().getUserDetailsSync(isManualSync);
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
  Future<Sync> getMasterDataSync(bool isManualSync) async {
    late Sync syncResponse;
    try {
      syncResponse = await SyncApi().getMasterDataSync(isManualSync);
    } on PlatformException {
      debugPrint('MasterDataSync Api call failed, PlatformException');
    } catch (e) {
      debugPrint('MasterDataSync has failed! ${e.toString()}');
    }
    return syncResponse;
  }

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
  Future<Sync> getCaCertsSync(bool isManualSync) async {
    late Sync syncResponse;
    try {
      syncResponse = await SyncApi().getCaCertsSync(isManualSync);
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
  Future<String> getPreRegIds() async {
    String preRegIdResponse = "";
    try {
      preRegIdResponse = await SyncApi().getPreRegIds();
    } on PlatformException {
      debugPrint('Application Id Api call failed, PlatformException');
    } catch (e) {
      debugPrint('Application Id call has failed! ${e.toString()}');
    }
    return preRegIdResponse;
  }

  @override
  Future<Sync> getKernelCertsSync(bool isManualSync) async {
    late Sync syncResponse;
    try {
      syncResponse = await SyncApi().getKernelCertsSync(isManualSync);
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
  Future<bool> getSyncAndUploadInProgressStatus() async{
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

  static const MethodChannel _activeJobsChannel = MethodChannel('io.mosip.registration/activeSyncJobs');

  @override
  Future<List<String?>> getActiveSyncJobs() async {
    try {
      final result = await _activeJobsChannel.invokeMethod<List<dynamic>>('getActiveSyncJobs');
      final list = (result ?? const <dynamic>[]).cast<String?>();
      debugPrint('Active Sync Jobs from Android: $list');
      return list;
    } on PlatformException catch (e) {
      debugPrint('getActiveSyncJobs PlatformException: ${e.message}');
      return const [];
    } catch (e) {
      debugPrint('getActiveSyncJobs failed: $e');
      return const [];
    }
  }

  Future<bool> deleteAuditLogs() async {
    try {
      final ok = await SyncApi().deleteAuditLogsNative();
      return ok;
    } on PlatformException catch (e) {
      debugPrint('deleteAuditLogs PlatformException: ${e.message}');
      return false;
    } catch (e) {
      debugPrint('deleteAuditLogs failed: $e');
      return false;
    }
  }
}

SyncResponseService getSyncResponseServiceImpl() => SyncResponseServiceImpl();
