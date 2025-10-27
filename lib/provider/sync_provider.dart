/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'dart:convert';
import 'dart:developer';

import 'package:flutter/widgets.dart';
import 'package:registration_client/pigeon/master_data_sync_pigeon.dart';

import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/platform_spi/sync_response_service.dart';
import 'package:registration_client/utils/sync_job_def.dart';

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
  bool _kernelCertsSyncSuccess = false;
  bool isSyncInProgress = false;
  bool _isSyncAndUploadInProgress = false;

  String get lastSuccessfulSyncTime => _lastSuccessfulSyncTime;
  int get currentSyncProgress => _currentSyncProgress;
  String get currentProgressType => _currentProgressType;
  bool get isSyncing => _isSyncing;
  bool get isGlobalSyncInProgress => _isGlobalSyncInProgress;
  bool get isSyncAndUploadInProgress => _isSyncAndUploadInProgress;
  bool get certificateSyncSuccess => _policyKeySyncSuccess;
  bool get globalParamsSyncSuccess => _globalParamsSyncSuccess;
  bool get userDetailsSyncSuccess => _userDetailsSyncSuccess;
  bool get idSchemaSyncSuccess => _idSchemaSyncSuccess;
  bool get masterDataSyncSuccess => _masterDataSyncSuccess;
  bool get cacertsSyncSuccess => _cacertsSyncSuccess;
  bool get kernelCertsSyncSuccess => _kernelCertsSyncSuccess;

  set isSyncing(bool value) {
    _isSyncing = value;
    notifyListeners();
  }

  set isSyncAndUploadInProgress(bool value) {
    _isSyncAndUploadInProgress = value;
    notifyListeners();
  }

  getLastSyncTime() async {
    SyncTime lastSyncTime = await syncResponseService.getLastSyncTime();
    print("Last Sync Time: ${lastSyncTime.syncTime}");
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
  
  Future<String Function(String)> _getJobIdFinder() async {
    List<SyncJobDef> activeJobs = [];
    print("Fetching active job IDs");
    try {
      List<String?> activeJobJsonList = await syncResponseService.getActiveSyncJobs();
      activeJobs = activeJobJsonList
          .whereType<String>()
          .map((jsonStr) {
            try {
              return SyncJobDef.fromJson(json.decode(jsonStr) as Map<String, dynamic>);
            } catch (e) {
              log("Failed to parse job JSON: $jsonStr, error: $e");
              return null;
            }
          })
          .whereType<SyncJobDef>()
          .toList();
    } catch (e) {
      log("Failed to fetch active job IDs: $e");
    }
    print("Finding job ID for API name: $activeJobs");
    return (String apiName) {

      var job = activeJobs.where((job) => job.apiName == apiName).firstOrNull;
      return job?.id ?? "";
    };
  }

  autoSync(BuildContext context) async {
    // Get the job ID finder function
    String Function(String) findJobIdByApiName = await _getJobIdFinder();
    print("Auto Sync: Finding job IDs for active jobs");
    await syncResponseService
        .getGlobalParamsSync(false, findJobIdByApiName("synchConfigDataJob"))
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
        .getMasterDataSync(false, findJobIdByApiName("masterSyncJob"))
        .then((Sync getAutoSync) async {
      setCurrentProgressType(getAutoSync.syncType!);
      if (getAutoSync.errorCode == "") {
        _globalParamsSyncSuccess = true;
        _currentSyncProgress = getAutoSync.syncProgress!;
        notifyListeners();
        findJobIdByApiName = await _getJobIdFinder();
      } else {
        log(AppLocalizations.of(context)!.master_data_sync_failed);
      }
      notifyListeners();
    });

    await syncResponseService
        .getUserDetailsSync(false, findJobIdByApiName("userDetailServiceJob"))
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
        .getPolicyKeySync(false, findJobIdByApiName("keyPolicySyncJob"))
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

    await syncResponseService.getCaCertsSync(false, findJobIdByApiName("syncCertificateJob")).then((Sync getAutoSync) {
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

    await syncResponseService.getKernelCertsSync(false, findJobIdByApiName("publicKeySyncJob")).then((Sync getAutoSync) {
      setCurrentProgressType(getAutoSync.syncType!);
      if (getAutoSync.errorCode == "") {
        _kernelCertsSyncSuccess = true;
        _currentSyncProgress = getAutoSync.syncProgress!;
        notifyListeners();
      } else {
        log(AppLocalizations.of(context)!.ca_certs_sync_failed);
      }
      notifyListeners();
    });
    await getLastSyncTime();
  }

  bool isAllSyncSuccessful() {
    if (_policyKeySyncSuccess &&
        _globalParamsSyncSuccess &&
        _masterDataSyncSuccess &&
        _userDetailsSyncSuccess &&
        _idSchemaSyncSuccess &&
        _cacertsSyncSuccess &&
        _kernelCertsSyncSuccess) {
      return true;
    } else {
      return false;
    }
  }

  manualSync() async {
    isSyncInProgress = true;
    // Get the job ID finder function
    String Function(String) findJobIdByApiName = await _getJobIdFinder();
    
    Sync syncResult = await syncResponseService.getMasterDataSync(true, findJobIdByApiName("masterSyncJob"));
    if (syncResult.errorCode != null && syncResult.errorCode!.isEmpty) {
      syncResult = await syncResponseService.getIDSchemaSync(true);
      if (syncResult.errorCode != null && syncResult.errorCode!.isEmpty) {
        syncResult = await syncResponseService.getUserDetailsSync(true, findJobIdByApiName("userDetailServiceJob"));
        if (syncResult.errorCode != null && syncResult.errorCode!.isEmpty) {
          syncResult = await syncResponseService.getGlobalParamsSync(true, findJobIdByApiName("synchConfigDataJob"));
          if (syncResult.errorCode != null && syncResult.errorCode!.isEmpty) {
            syncResult = await syncResponseService.getKernelCertsSync(true, findJobIdByApiName("publicKeySyncJob"));
            if (syncResult.errorCode != null && syncResult.errorCode!.isEmpty) {
              syncResult = await syncResponseService.getPolicyKeySync(true, findJobIdByApiName("keyPolicySyncJob"));
              if (syncResult.errorCode != null && syncResult.errorCode!.isEmpty) {
                syncResult = await syncResponseService.getCaCertsSync(true, findJobIdByApiName("syncCertificateJob"));
                await getLastSyncTime();
                isSyncInProgress= false;
              }
            }
          }
        }
      }
    }
  }

  batchJob() async {
    await syncResponseService.batchJob();
  }

  getPreRegistrationIds() async {
    await syncResponseService.getPreRegIds("");
  }
}
