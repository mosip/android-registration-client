/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

package io.mosip.registration_client;

import android.util.Log;

import io.flutter.plugin.common.MethodChannel;
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.MasterDataService;

public class SyncActivityService {

    public void clickSyncMasterData(MethodChannel.Result result,
                                      AuditManagerService auditManagerService,
                                      MasterDataService masterDataService) {
        auditManagerService.audit(AuditEvent.MASTER_DATA_SYNC, Components.HOME);
        try {
            masterDataService.manualSync();
            result.success("Master Data Sync Completed");
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Masterdata sync failed", e);
        }
    }
}
