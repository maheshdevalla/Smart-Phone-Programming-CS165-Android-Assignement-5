package edu.dartmouth.cs.actiontabs;

import android.os.Bundle;
import android.preference.PreferenceFragment;


public class SettingTab extends PreferenceFragment {

    /**
     * called when the fragment is created
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }
}

