package com.techstreat.ssh_terminal;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;

import com.techstreat.ssh_terminal.preferences.SharedPreferencesManager;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(com.techstreat.ssh_terminal.R.xml.preferences);
        Preference p_delete = this.findPreference(SharedPreferencesManager.DELETETABLES);
        p_delete.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick (Preference preference)
    {
        String key = preference.getKey();
        if(key.equals(SharedPreferencesManager.DELETETABLES))
        {
            AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
            ab.setMessage("Are you sure you want to delete all saved data?");
            ab.setTitle("Delete");
            ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.clearConnectionsTable();
                    MainActivity.clearHostKeysTable();
                }
            });
            ab.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            ab.show();
        }
        return true;
    }
}