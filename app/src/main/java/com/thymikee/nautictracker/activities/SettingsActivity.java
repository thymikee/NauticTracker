package com.thymikee.nautictracker.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.thymikee.nautictracker.R;

import java.util.List;

/**
 * Created by thymikee on 30.11.14.
 */


public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        if (First.class.getName().equals(fragmentName)
                || Second.class.getName().equals(fragmentName)) {
            return(true);
        }

        return(false);
    }

    public static class First extends PreferenceFragment {
        private String TAG = "SettingsActivity$First";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }

    public static class Second extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}