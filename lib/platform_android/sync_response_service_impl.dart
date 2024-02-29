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

class SyncResponseServiceImpl implements SyncResponseService {
  @override
  Future<Sync> getPolicyKeySync() async {
    late Sync syncResponse;
    try {
      syncResponse = await SyncApi().getPolicyKeySync();
    } on PlatformException {
      debugPrint('PolicyKeySync Api call failed, PlatformException');
    } catch (e) {
      debugPrint('PolicyKeySync has failed! ${e.toString()}');
    }
    return syncResponse;
  }

  @override
  Future<Sync> getGlobalParamsSync() async {
    late Sync syncResponse;
    try {
      syncResponse = await SyncApi().getGlobalParamsSync();
    } on PlatformException {
      debugPrint('GlobalParamsSync Api call failed, PlatformException');
    } catch (e) {
      debugPrint('GlobalParamsSync has failed! ${e.toString()}');
    }
    return syncResponse;
  }

  @override
  Future<Sync> getUserDetailsSync() async {
    late Sync syncResponse;
    try {
      syncResponse = await SyncApi().getUserDetailsSync();
    } on PlatformException {
      debugPrint('UserDetailsSync Api call failed, PlatformException');
    } catch (e) {
      debugPrint('UserDetailsSync has failed! ${e.toString()}');
    }
    return syncResponse;
  }

  @override
  Future<Sync> getIDSchemaSync() async {
    late Sync syncResponse;
    try {
      syncResponse = await SyncApi().getIDSchemaSync();
    } on PlatformException {
      debugPrint('IDSchemaSync Api call failed, PlatformException');
    } catch (e) {
      debugPrint('IDSchemaSync has failed! ${e.toString()}');
    }
    return syncResponse;
  }

  @override
  Future<Sync> getMasterDataSync() async {
    late Sync syncResponse;
    try {
      syncResponse = await SyncApi().getMasterDataSync();
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
  Future<Sync> getCaCertsSync() async {
    late Sync syncResponse;
    try {
      syncResponse = await SyncApi().getCaCertsSync();
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
  Future<String> manualSync() async {
    String manualSyncResponse = "";
    try {
      manualSyncResponse = await SyncApi().manualSync();
    } on PlatformException {
      debugPrint('Manual Sync Api call failed, PlatformException');
    } catch (e) {
      debugPrint('Manual Sync failed! Please try again. ${e.toString()}');
    }
    return manualSyncResponse;
  }
}

SyncResponseService getSyncResponseServiceImpl() => SyncResponseServiceImpl();
