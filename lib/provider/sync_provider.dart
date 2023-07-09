import 'dart:async';
import 'dart:developer';
import 'dart:io';

import 'package:flutter/widgets.dart';
import 'package:registration_client/pigeon/sync_pigeon.dart';
import 'package:registration_client/platform_android/machine_key_impl.dart';
import 'package:registration_client/platform_android/sync_response_impl.dart';

class SyncProvider with ChangeNotifier {
  String _lastSuccessfulSyncTime = "";
  int _currentSyncProgress = 0;
  String _currentProgressType = "";
  bool _isSyncing = false;
  bool _isGlobalSyncInProgress = false;

  bool _certificateSyncSuccess = false;
  bool _globalParamsSyncSuccess = false;
  bool _userDetailsSyncSuccess = false;
  bool _idSchemaSyncSuccess = false;
  bool _masterDataSyncSuccess = false;

  String get lastSuccessfulSyncTime => _lastSuccessfulSyncTime;
  int get currentSyncProgress => _currentSyncProgress;
  String get currentProgressType => _currentProgressType;
  bool get isSyncing => _isSyncing;
  bool get isGlobalSyncInProgress => _isGlobalSyncInProgress;
  bool get certificateSyncSuccess => _certificateSyncSuccess;
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

  autoSync() async {
    await SyncResponseImpl()
        .getCertificateSync()
        .then((Sync getAutoSync) async {
      setCurrentProgressType(getAutoSync.syncType!);
      if (getAutoSync.errorCode == "") {
        _certificateSyncSuccess = true;
        _currentSyncProgress = getAutoSync.syncProgress!;
        notifyListeners();
      } else {
        _certificateSyncSuccess = false;
        log(getAutoSync.errorCode!);
      }
      notifyListeners();
      await SyncResponseImpl()
          .getGlobalParamsSync()
          .then((Sync getAutoSync) async {
        setCurrentProgressType(getAutoSync.syncType!);
        if (getAutoSync.errorCode == "") {
          _globalParamsSyncSuccess = true;
          _currentSyncProgress = getAutoSync.syncProgress!;
          notifyListeners();

          } else {
          log(getAutoSync.errorCode!);
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
            log(getAutoSync.errorCode!);
          }
          notifyListeners();
          await SyncResponseImpl()
              .getIDSchemaSync()
              .then((Sync getAutoSync) async {
            setCurrentProgressType(getAutoSync.syncType!);
            if (getAutoSync.errorCode == "kjh") {
              _idSchemaSyncSuccess = true;
              _currentSyncProgress = getAutoSync.syncProgress!;
              notifyListeners();
    
                  } else {
              log(getAutoSync.errorCode!);
            }
            notifyListeners();
            await SyncResponseImpl()
                .getMasterDataSync()
                .then((Sync getAutoSync) {
              setCurrentProgressType(getAutoSync.syncType!);
              if (getAutoSync.errorCode == "") {
                _masterDataSyncSuccess = true;
                _currentSyncProgress = getAutoSync.syncProgress!;
                notifyListeners();
      
                      } else {
                log(getAutoSync.errorCode!);
              }
              notifyListeners();
            });
          });
        });
      });
    });
  }

  bool isAllSyncSuccessful() {
    if (_certificateSyncSuccess &&
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
