package pl.gasior.analizasnu.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import pl.gasior.analizasnu.R;

/**
 * Created by Piotrek on 23.05.2016.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
