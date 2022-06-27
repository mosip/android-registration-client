package io.mosip.registration.app.util;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

import io.mosip.registration.app.R;

public class PacketListActivityHelper {

    public static void toggleStatusBarColor(Activity activity, int color) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(activity.getResources().getColor(R.color.primaryColor));
    }
}
