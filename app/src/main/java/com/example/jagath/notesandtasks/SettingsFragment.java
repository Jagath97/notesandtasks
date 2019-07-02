package com.example.jagath.notesandtasks;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preference);
    }
}
