package io.mosip.registration.app.util;

import static android.content.Context.CONNECTIVITY_SERVICE;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;

public class NetworkUtil {

    ConnectivityManager connectivityManager;
    Context context;

    public NetworkUtil(Context context) {
        this.context = context;
        connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
    }

    public boolean isNetworkConnection() {
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public boolean isMobileNetworkConnect() {
        Network currentNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(currentNetwork);
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
    }

    public boolean isWifiConnect() {
        Network currentNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(currentNetwork);
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
    }

    public int getNetworkUpStreamBandwidthKbps() {
        NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        return nc.getLinkUpstreamBandwidthKbps();
    }
}
