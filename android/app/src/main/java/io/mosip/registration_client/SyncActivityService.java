/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
