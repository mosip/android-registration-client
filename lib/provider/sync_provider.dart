/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'dart:async';
import 'dart:convert';
import 'dart:developer';

import 'package:flutter/widgets.dart';
import 'package:registration_client/pigeon/master_data_sync_pigeon.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/platform_spi/global_config_service.dart';
import 'package:registration_client/platform_spi/sync_response_service.dart';
import 'package:registration_client/utils/sync_job_def.dart';

enum RemapSyncStatus { idle, inProgress, success, failed }

class SyncProvider with ChangeNotifier {
  final SyncResponseService syncResponseService = SyncResponseService();
  final GlobalConfigService _globalConfigService = GlobalConfigService();

  String _lastSuccessfulSyncTime = "";
  int _currentSyncProgress = 0;
  String _currentProgressType = "";
  bool _isSyncing = false;
  bool _isGlobalSyncInProgress = false;

  String _lastMasterDataSyncTime = "";
  String _lastPreRegSyncTime = "";
  bool _isMasterDataSyncing = false;
  bool _isPreRegSyncing = false;
  int _masterDataSyncProgress = 0;
  int _preRegSyncProgress = 0;

  static const String _masterDataSyncTimeKey = 'last_synchronise_data_timestamp';
  static const String _preRegSyncTimeKey = 'last_download_pre_reg_data_timestamp';

  bool _policyKeySyncSuccess = false;
  bool _globalParamsSyncSuccess = false;
  bool _userDetailsSyncSuccess = false;
  bool _idSchemaSyncSuccess = false;
  bool _masterDataSyncSuccess = false;
  bool _cacertsSyncSuccess = false;
  bool _kernelCertsSyncSuccess = false;
  bool isSyncInProgress = false;
  bool _isSyncAndUploadInProgress = false;
  bool _isCenterRemapped = false;

  final List<RemapSyncStatus> _remapStepStatuses =
      List.filled(4, RemapSyncStatus.idle);
  DateTime? _remapCompletedAt;
  DateTime? _lastRemapSyncTime;

  static const String _remapSyncTimeKey = 'last_center_remap_sync_time';

  Timer? _jobStatusPollingTimer;
  final Map<String, JobStatus> _jobStatuses = {};

  String get lastSuccessfulSyncTime => _lastSuccessfulSyncTime;
  String get lastMasterDataSyncTime =>
      _lastMasterDataSyncTime.isNotEmpty ? _lastMasterDataSyncTime : _lastSuccessfulSyncTime;
  String get lastPreRegSyncTime => _lastPreRegSyncTime;
  bool get isMasterDataSyncing => _isMasterDataSyncing;
  bool get isPreRegSyncing => _isPreRegSyncing;
  int get masterDataSyncProgress => _masterDataSyncProgress;
  int get preRegSyncProgress => _preRegSyncProgress;

  int get currentSyncProgress => _currentSyncProgress;
  String get currentProgressType => _currentProgressType;
  bool get isSyncing => _isSyncing;
  bool get isGlobalSyncInProgress => _isGlobalSyncInProgress;
  bool get isSyncAndUploadInProgress => _isSyncAndUploadInProgress;
  bool get isCenterRemapped => _isCenterRemapped;

  /// Per-step status for the remap sync screen (index 0 = step 1 … index 3 = step 4).
  List<RemapSyncStatus> get remapStepStatuses => List.unmodifiable(_remapStepStatuses);

  /// Derived overall status for the remap sync screen.
  RemapSyncStatus get remapSyncStatus {
    if (_remapStepStatuses.every((s) => s == RemapSyncStatus.idle)) return RemapSyncStatus.idle;
    if (_remapStepStatuses.last == RemapSyncStatus.success) return RemapSyncStatus.success;
    if (_remapStepStatuses.any((s) => s == RemapSyncStatus.failed)) return RemapSyncStatus.failed;
    return RemapSyncStatus.inProgress;
  }

  /// Progress percentage (0–100) based on completed steps.
  int get remapSyncProgress =>
      _remapStepStatuses.where((s) => s == RemapSyncStatus.success).length * 25;

  /// Timestamp recorded when all four steps finish successfully.
  DateTime? get remapSyncCompletedAt => _remapCompletedAt;

  /// Timestamp of when the last center remap sync was initiated (persisted across restarts).
  DateTime? get lastRemapSyncTime => _lastRemapSyncTime;

  Future<void> loadLastRemapSyncTime() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final stored = prefs.getString(_remapSyncTimeKey);
      if (stored != null) {
        _lastRemapSyncTime = DateTime.tryParse(stored);
        notifyListeners();
      }
    } catch (e) {
      log('REMAP: loadLastRemapSyncTime error: $e');
    }
  }

  Future<void> loadLastSyncTimes() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final masterTime = prefs.getString(_masterDataSyncTimeKey);
      final preRegTime = prefs.getString(_preRegSyncTimeKey);
      if (masterTime != null) _lastMasterDataSyncTime = masterTime;
      if (preRegTime != null) _lastPreRegSyncTime = preRegTime;
      notifyListeners();
    } catch (e) {
      log('SyncProvider: loadLastSyncTimes error: $e');
    }
  }

  Future<void> saveMasterDataSyncTime(String time) async {
    try {
      _lastMasterDataSyncTime = time;
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString(_masterDataSyncTimeKey, time);
      notifyListeners();
    } catch (e) {
      log('SyncProvider: saveMasterDataSyncTime error: $e');
    }
  }

  Future<void> savePreRegSyncTime(String time) async {
    try {
      _lastPreRegSyncTime = time;
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString(_preRegSyncTimeKey, time);
      notifyListeners();
    } catch (e) {
      log('SyncProvider: savePreRegSyncTime error: $e');
    }
  }

  Future<void> _saveRemapSyncTime(DateTime time) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString(_remapSyncTimeKey, time.toIso8601String());
    } catch (e) {
      log('REMAP: _saveRemapSyncTime error: $e');
    }
  }
  bool get certificateSyncSuccess => _policyKeySyncSuccess;
  bool get globalParamsSyncSuccess => _globalParamsSyncSuccess;
  bool get userDetailsSyncSuccess => _userDetailsSyncSuccess;
  bool get idSchemaSyncSuccess => _idSchemaSyncSuccess;
  bool get masterDataSyncSuccess => _masterDataSyncSuccess;
  bool get cacertsSyncSuccess => _cacertsSyncSuccess;
  bool get kernelCertsSyncSuccess => _kernelCertsSyncSuccess;

  Map<String, JobStatus> get jobStatuses => _jobStatuses;

  set isSyncing(bool value) {
    _isSyncing = value;
    notifyListeners();
  }

  set isSyncAndUploadInProgress(bool value) {
    _isSyncAndUploadInProgress = value;
    notifyListeners();
  }

  void _onRemapDetected() {
    _isCenterRemapped = true;
    stopJobPolling();
    notifyListeners();
  }

  Future<void> checkCenterRemapState() async {
    try {
      if (await _globalConfigService.getCenterRemapFlag()) _onRemapDetected();
    } catch (e) {
      log('REMAP: checkCenterRemapState error: $e');
    }
  }

  void resetRemapSyncState() {
    _remapStepStatuses.fillRange(0, _remapStepStatuses.length, RemapSyncStatus.idle);
    _remapCompletedAt = null;
    notifyListeners();
  }

  /// Drives the four-step center remap cleanup via the platform channel.
  /// Each step's status is updated individually so the UI can show per-step progress.
  /// Returns [true] when all four steps succeeded.
  Future<bool> performCenterRemapSync() async {
    resetRemapSyncState();
    for (int step = 1; step <= 4; step++) {
      _setStepStatus(step, RemapSyncStatus.inProgress);
      notifyListeners();

      final bool ok = await syncResponseService.executeRemapStep(step);

      _setStepStatus(step, ok ? RemapSyncStatus.success : RemapSyncStatus.failed);
      notifyListeners();

      if (!ok) return false;
    }
    _remapCompletedAt = DateTime.now();
    _lastRemapSyncTime = _remapCompletedAt;
    await _saveRemapSyncTime(_lastRemapSyncTime!);
    notifyListeners();
    return true;
  }

  void _setStepStatus(int step, RemapSyncStatus status) {
    _remapStepStatuses[step - 1] = status;
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
  
  Future<String Function(String)> _getJobIdFinder() async {
    List<SyncJobDef> activeJobs = [];
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
    return (String apiName) {
      final matches = activeJobs.where((job) => job.apiName == apiName);
      return matches.isNotEmpty ? matches.first.id ?? "" : "";
    };
  }

  autoSync(BuildContext context) async {
    // Get the job ID finder function
    String Function(String) findJobIdByApiName = await _getJobIdFinder();

    final Sync masterSync = await syncResponseService
        .getMasterDataSync(false, findJobIdByApiName("masterSyncJob"));
    if (!context.mounted) return;
    if (masterSync.syncType != null) setCurrentProgressType(masterSync.syncType!);
    if (masterSync.errorCode == 'KER-SNC-149') {
      _onRemapDetected();
      return;
    }
    if (masterSync.errorCode == "") {
      _globalParamsSyncSuccess = true;
      _currentSyncProgress = masterSync.syncProgress!;
      notifyListeners();
      findJobIdByApiName = await _getJobIdFinder();
    } else {
      log(AppLocalizations.of(context)!.master_data_sync_failed);
    }
    notifyListeners();

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
        .getIDSchemaSync(false, findJobIdByApiName("latestIdSchemaSyncJob"))
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

  Future<Sync> manualSync() async {
    isSyncInProgress = true;
    try {
      final findJobId = await _getJobIdFinder();

      final steps = [
        _SyncStep("masterSyncJob", 15, syncResponseService.getMasterDataSync),
        _SyncStep("latestIdSchemaSyncJob", 30, syncResponseService.getIDSchemaSync),
        _SyncStep("userDetailServiceJob", 45, syncResponseService.getUserDetailsSync),
        _SyncStep("synchConfigDataJob", 60, syncResponseService.getGlobalParamsSync),
        _SyncStep("publicKeySyncJob", 75, syncResponseService.getKernelCertsSync),
        _SyncStep("keyPolicySyncJob", 90, syncResponseService.getPolicyKeySync),
        _SyncStep("syncCertificateJob", 95, syncResponseService.getCaCertsSync),
      ];

      Sync syncResult = Sync();
      for (final step in steps) {
        _masterDataSyncProgress = step.progress;
        notifyListeners();

        syncResult = await step.syncFn(true, findJobId(step.jobName));
        if (syncResult.errorCode == 'KER-SNC-149') {
          _onRemapDetected();
          break;
        }
        if (syncResult.errorCode != null && syncResult.errorCode!.isNotEmpty) {
          break;
        }
      }

      if ((syncResult.errorCode ?? "").isEmpty) {
        await getLastSyncTime();
      }
      return syncResult;
    } finally {
      isSyncInProgress = false;
      _masterDataSyncProgress = 0;
    }
  }

  Future<bool> performMasterDataSync() async {
    _isMasterDataSyncing = true;
    _masterDataSyncProgress = 10;
    notifyListeners();

    try {
      final syncResult = await manualSync();
      if (_isCenterRemapped || (syncResult.errorCode ?? "").isNotEmpty) return false;

      _masterDataSyncProgress = 100;
      await saveMasterDataSyncTime(DateTime.now().toIso8601String());
      return true;
    } catch (e) {
      log('SyncProvider: performMasterDataSync error: $e');
      return false;
    } finally {
      _isMasterDataSyncing = false;
      notifyListeners();
    }
  }

  Future<bool> performPreRegDataSync() async {
    _isPreRegSyncing = true;
    _preRegSyncProgress = 20;
    notifyListeners();

    try {
      final findJobId = await _getJobIdFinder();
      _preRegSyncProgress = 50;
      notifyListeners();

      final response = await syncResponseService.getPreRegIds(findJobId("preRegistrationDataSyncJob"));
      if (response.isEmpty) return false;
      _preRegSyncProgress = 100;
      await savePreRegSyncTime(DateTime.now().toIso8601String());
      return true;
    } catch (e) {
      log('SyncProvider: performPreRegDataSync error: $e');
      return false;
    } finally {
      _isPreRegSyncing = false;
      notifyListeners();
    }
  }

  batchJob() async {
    await syncResponseService.batchJob();
  }

  Future<String?> getLastSyncTimeByJobId(String jobId) async {
    try {
      final value = await syncResponseService.getLastSyncTimeByJobId(jobId);
      return value;
    } catch (e) {
      log("Failed to get last sync time for job $jobId: $e");
      return null;
    }
  }

  Future<String?> getNextSyncTimeByJobId(String jobId) async {
    try {
      final value = await syncResponseService.getNextSyncTimeByJobId(jobId);
      return value;
    } catch (e) {
      log("Failed to get next sync time for job $jobId: $e");
      return null;
    }
  }

  void startJobPolling() {
    refreshJobStatuses(); // Initial fetch
    _jobStatusPollingTimer?.cancel();
    _jobStatusPollingTimer = Timer.periodic(const Duration(seconds: 30), (_) {
      refreshJobStatuses();
    });
  }

  void stopJobPolling() {
    _jobStatusPollingTimer?.cancel();
    _jobStatusPollingTimer = null;
  }

  Future<void> refreshJobStatuses() async {
    try {
      final activeJobs = await syncResponseService.getActiveSyncJobs();
      for (final jobJson in activeJobs) {
         if (jobJson == null) continue;
         try {
           final job = SyncJobDef.fromJson(json.decode(jobJson) as Map<String, dynamic>);
           if (job.id != null) {
              final lastSync = await getLastSyncTimeByJobId(job.id!);
              final nextSync = await getNextSyncTimeByJobId(job.id!);
              
              _jobStatuses[job.id!] = JobStatus(id: job.id!, lastSyncTime: lastSync, nextSyncTime: nextSync);
           }
         } catch (e) {
           log("Error parsing job during polling: $e");
         }
      }

      await getLastSyncTime(); // Update global last sync time (fallback for Master Sync)
      notifyListeners();
    } catch (e) {
      log("Error refreshing job statuses: $e");
    }
  }

  @override
  void dispose() {
    stopJobPolling();
    super.dispose();
  }
}

class JobStatus {
  final String id;
  final String? lastSyncTime;
  final String? nextSyncTime;

  JobStatus({required this.id, this.lastSyncTime, this.nextSyncTime});
}

class _SyncStep {
  final String jobName;
  final int progress;
  final Future<Sync> Function(bool, String) syncFn;

  _SyncStep(this.jobName, this.progress, this.syncFn);
}
