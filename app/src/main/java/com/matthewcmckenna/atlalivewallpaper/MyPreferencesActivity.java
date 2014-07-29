package com.matthewcmckenna.atlalivewallpaper;


import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;


public class MyPreferencesActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_LIST_PREFERENCE = "colorListPref";
    public static final String FADE_SPEED_PREFERENCE = "fadeSpeed";

    private ListPreference colorListPreference;
    private ListPreference fadeSpeedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

        // Get a reference to the preferences
        colorListPreference = (ListPreference) getPreferenceScreen().findPreference(KEY_LIST_PREFERENCE);
        fadeSpeedPreference = (ListPreference) getPreferenceScreen().findPreference(FADE_SPEED_PREFERENCE);

    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Set new summary, when a preference value changes
        if (key.equals(KEY_LIST_PREFERENCE)) {
            colorListPreference.setSummary("Current value is " + colorListPreference.getEntry().toString());
        }if (key.equals(FADE_SPEED_PREFERENCE)) {
            fadeSpeedPreference.setSummary("Current value is " + fadeSpeedPreference.getEntry().toString());
        }
    }

}
