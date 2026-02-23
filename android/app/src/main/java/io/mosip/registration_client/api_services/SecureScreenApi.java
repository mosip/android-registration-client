/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

package io.mosip.registration_client.api_services;

import android.app.Activity;
import android.util.Log;
import java.lang.ref.WeakReference;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration_client.model.SecureScreenPigeon;

@Singleton
public class SecureScreenApi implements SecureScreenPigeon.SecureScreenApi {

    private WeakReference<Activity> activityRef = new WeakReference<>(null);

    @Inject
    public SecureScreenApi() {
    }

    public void setCallbackActivity(Activity activity) {
        this.activityRef = new WeakReference<>(activity);
    }

    @Override
    public void addFlagSecure(@NonNull SecureScreenPigeon.Result<Boolean> result) {
        Activity activity = activityRef.get();
        if (activity == null) {
            result.error(new IllegalStateException("Activity not set"));
            return;
        }
        final Activity localActivity = activity;
        localActivity.runOnUiThread(() -> {
            try {
                localActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
                result.success(true);
            } catch (Exception e) {
                result.error(e);
            }
        });
    }

    @Override
    public void clearFlagSecure(@NonNull SecureScreenPigeon.Result<Boolean> result) {
        Activity activity = activityRef.get();
        if (activity == null) {
            result.error(new IllegalStateException("Activity not set"));
            return;
        }
        final Activity localActivity = activity;
        localActivity.runOnUiThread(() -> {
            try {
                localActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
                result.success(true);
            } catch (Exception e) {
                result.error(e);
            }
        });
    }
}
