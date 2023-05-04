/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package io.mosip.registration_client;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import io.flutter.plugin.common.MethodChannel;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;

public class AboutActivityService {

    public void testMachine(ClientCryptoManagerService clientCryptoManagerService, MethodChannel.Result result) {
        String data = clientCryptoManagerService.testMachine();
        result.success("data: " + data);
    }

    public void getMachineDetails(ClientCryptoManagerService clientCryptoManagerService, MethodChannel.Result result) {
        Map<String, String> details =
                clientCryptoManagerService.getMachineDetails();

        if(details.get("name") == null) {
            result.error("ERR-INITIALIZATION-FAILED",
                    "Machine details could not be fetched!",
                    "Failed to initialize the machine. Try to clear the app data and cache before reinstalling the app.");
        }

        JSONObject jsonObject = new JSONObject(details);

        try {
            jsonObject.put("version", "Alpha");
        } catch (JSONException e) {
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
            result.error("JSON-ERROR",
                    "Machine details could not be fetched!",
                    "Failed to initialize the machine. Try to clear the app data and cache before reinstalling the app.");
        }
        result.success(jsonObject.toString());
    }

}
