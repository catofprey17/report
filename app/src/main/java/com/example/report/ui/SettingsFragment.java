package com.example.report.ui;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.report.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
    }
}
