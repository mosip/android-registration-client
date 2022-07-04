package io.mosip.registration.app.activites;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import io.mosip.registration.app.R;

public class PacketUploadSettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}