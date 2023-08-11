import 'dart:async';
import 'dart:developer';
import 'dart:io';
import 'package:flutter/widgets.dart';
import 'package:registration_client/pigeon/master_data_sync_pigeon.dart';
import 'package:registration_client/platform_android/machine_key_impl.dart';
import 'package:registration_client/platform_android/sync_response_impl.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
class SyncProvider with ChangeNotifier {
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

  getLastSyncTime() async {
    SyncTime lastSyncTime = await SyncResponseImpl().getLastSyncTime();
    setLastSuccessfulSyncTime(lastSyncTime.syncTime!);
    // print("Last Sync Time from GlobalParamRepository:" + lastSyncTime.syncTime!);
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
    await SyncResponseImpl().getLastSyncTime();
    await SyncResponseImpl()
        .getGlobalParamsSync()
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
      await SyncResponseImpl()
          .getMasterDataSync()
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
        await SyncResponseImpl()
            .getUserDetailsSync()
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
          await SyncResponseImpl()
              .getIDSchemaSync()
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
            await SyncResponseImpl()
                .getPolicyKeySync()
                .then((Sync getAutoSync) {
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
          });
        });
      });
    });
  }

  bool isAllSyncSuccessful() {
    if (_policyKeySyncSuccess &&
        _globalParamsSyncSuccess &&
        _masterDataSyncSuccess &&
        _userDetailsSyncSuccess &&
        _idSchemaSyncSuccess) {
      return true;
    } else {
      return false;
    }
  }
}
