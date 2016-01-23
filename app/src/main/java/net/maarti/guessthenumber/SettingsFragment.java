package net.maarti.guessthenumber;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends android.preference.PreferenceFragment {


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Chargement du "layout"
        addPreferencesFromResource(R.xml.activity_settings);

        // On récupère la valeur du username enregistrée dans les preferences
        Context hostActivity = getActivity();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(hostActivity);
        String username = preferences.getString(MainMenuActivity.USERNAME_LABEL, getString(R.string.defautUsername));

        // On récupère le widget préférence Username
        final Preference pref = getPreferenceManager().findPreference(MainMenuActivity.USERNAME_LABEL);

        // On initialise le summary du pseudo
        pref.setSummary(username);


        // On lui attache un listener pour mettre à jour le summary lorsqu'un pseudo est saisi
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                pref.setSummary(newValue.toString());
                return true;
            }
        });

    }




}
