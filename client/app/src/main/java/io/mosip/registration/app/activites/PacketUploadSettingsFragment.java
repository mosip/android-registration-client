package io.mosip.registration.app.activites;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import io.mosip.registration.app.R;
import io.mosip.registration.app.util.ClientConstants;

public class PacketUploadSettingsFragment extends PreferenceFragmentCompat {

    ListPreference listPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        listPreference = findPreference(ClientConstants.MIN_UPSTREAM_BANDWIDTH_KBPS);
        assert listPreference != null;
        listPreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
    }
}