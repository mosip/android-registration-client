/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'dart:developer';

import 'package:flutter/widgets.dart';
import 'package:registration_client/pigeon/master_data_sync_pigeon.dart';

import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/platform_spi/sync_response_service.dart';

class SyncProvider with ChangeNotifier {
  final SyncResponseService syncResponseService = SyncResponseService();

  String _lastSuccessfulSyncTime = "";
  int _currentSyncProgress = 0;
  String _currentProgressType = "";
  bool _isSyncing = false;
  bool _isGlobalSyncInProgress = false;

  bool _policyKeySyncSuccess = false;
  bool _globalParamsSyncSuccess = false;
  bool _userDetailsSyncSuccess = false;
  bool _idSchemaSyncSuccess = false;
  bool _masterDataSyncSuccess = false;
  bool _cacertsSyncSuccess = false;

  String get lastSuccessfulSyncTime => _lastSuccessfulSyncTime;
  int get currentSyncProgress => _currentSyncProgress;
  String get currentProgressType => _currentProgressType;
  bool get isSyncing => _isSyncing;
  bool get isGlobalSyncInProgress => _isGlobalSyncInProgress;
  bool get certificateSyncSuccess => _policyKeySyncSuccess;
  bool get globalParamsSyncSuccess => _globalParamsSyncSuccess;
  bool get userDetailsSyncSuccess => _userDetailsSyncSuccess;
  bool get idSchemaSyncSuccess => _idSchemaSyncSuccess;
  bool get masterDataSyncSuccess => _masterDataSyncSuccess;
  bool get cacertsSyncSuccess => _cacertsSyncSuccess;

  set isSyncing(bool value) {
    _isSyncing = value;
    notifyListeners();
  }

  getLastSyncTime() async {
    SyncTime lastSyncTime = await syncResponseService.getLastSyncTime();
    setLastSuccessfulSyncTime(lastSyncTime.syncTime!);
  }

  setLastSuccessfulSyncTime(String syncTime) {
    _lastSuccessfulSyncTime = syncTime;
    notifyListeners();
  }

  setCurrentSyncProgress(int progress) {
    _currentSyncProgress = progress;
    notifyListeners();
  }

  setCurrentProgressType(String progressType) {
    _currentProgressType = progressType;
    notifyListeners();
  }

  setIsGlobalSyncInProgress(bool isGlobalSyncInProgress) {
    _isGlobalSyncInProgress = isGlobalSyncInProgress;
  }

  autoSync(BuildContext context) async {
    await syncResponseService.getLastSyncTime();

    await syncResponseService
        .getGlobalParamsSync(false)
        .then((Sync getAutoSync) async {
      setCurrentProgressType(getAutoSync.syncType!);
      if (getAutoSync.errorCode == "") {
        _policyKeySyncSuccess = true;
        _currentSyncProgress = getAutoSync.syncProgress!;
        notifyListeners();
      } else {
        log(AppLocalizations.of(context)!.global_params_sync_failed);
      }
      notifyListeners();
    });

    await syncResponseService
        .getMasterDataSync(false)
        .then((Sync getAutoSync) async {
      setCurrentProgressType(getAutoSync.syncType!);
      if (getAutoSync.errorCode == "") {
        _globalParamsSyncSuccess = true;
        _currentSyncProgress = getAutoSync.syncProgress!;
        notifyListeners();
      } else {
        log(AppLocalizations.of(context)!.master_data_sync_failed);
      }
      notifyListeners();
    });

    await syncResponseService
        .getUserDetailsSync(false)
        .then((Sync getAutoSync) async {
      setCurrentProgressType(getAutoSync.syncType!);
      if (getAutoSync.errorCode == "") {
        _userDetailsSyncSuccess = true;
        _currentSyncProgress = getAutoSync.syncProgress!;
        notifyListeners();
      } else {
        log(AppLocalizations.of(context)!.user_details_sync_failed);
      }
      notifyListeners();
    });

    await syncResponseService
        .getIDSchemaSync(false)
        .then((Sync getAutoSync) async {
      setCurrentProgressType(getAutoSync.syncType!);
      if (getAutoSync.errorCode == "") {
        _idSchemaSyncSuccess = true;
        _currentSyncProgress = getAutoSync.syncProgress!;
        notifyListeners();
      } else {
        log(AppLocalizations.of(context)!.id_schema_sync_failed);
      }
      notifyListeners();
    });

    await syncResponseService
        .getPolicyKeySync(false)
        .then((Sync getAutoSync) async {
      setCurrentProgressType(getAutoSync.syncType!);
      if (getAutoSync.errorCode == "") {
        _masterDataSyncSuccess = true;
        _currentSyncProgress = getAutoSync.syncProgress!;
        notifyListeners();
      } else {
        log(AppLocalizations.of(context)!.policy_key_sync_failed);
      }
      notifyListeners();
    });

    await syncResponseService.getCaCertsSync(false).then((Sync getAutoSync) {
      setCurrentProgressType(getAutoSync.syncType!);
      if (getAutoSync.errorCode == "") {
        _cacertsSyncSuccess = true;
        _currentSyncProgress = getAutoSync.syncProgress!;
        notifyListeners();
      } else {
        log(AppLocalizations.of(context)!.ca_certs_sync_failed);
      }
      notifyListeners();
    });
  }

  bool isAllSyncSuccessful() {
    if (_policyKeySyncSuccess &&
        _globalParamsSyncSuccess &&
        _masterDataSyncSuccess &&
        _userDetailsSyncSuccess &&
        _idSchemaSyncSuccess &&
        _cacertsSyncSuccess) {
      return true;
    } else {
      return false;
    }
  }

  manualSync() async {
    Sync syncResult = await syncResponseService.getMasterDataSync(true);
    if (syncResult.errorCode != null && syncResult.errorCode!.isEmpty) {
      syncResult = await syncResponseService.getIDSchemaSync(true);
      if (syncResult.errorCode != null && syncResult.errorCode!.isEmpty) {
        syncResult = await syncResponseService.getUserDetailsSync(true);
        if (syncResult.errorCode != null && syncResult.errorCode!.isEmpty) {
          syncResult = await syncResponseService.getGlobalParamsSync(true);
          if (syncResult.errorCode != null && syncResult.errorCode!.isEmpty) {
            syncResult = await syncResponseService.getPolicyKeySync(true);
            if (syncResult.errorCode != null && syncResult.errorCode!.isEmpty) {
              syncResult = await syncResponseService.getCaCertsSync(true);
            }
          }
        }
      }
    }
  }

  batchJob() async {
    await syncResponseService.batchJob();
  }
}
