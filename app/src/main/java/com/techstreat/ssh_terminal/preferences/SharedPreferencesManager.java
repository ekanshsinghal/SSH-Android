package com.techstreat.ssh_terminal.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.techstreat.ssh_terminal.sshutils.SshConnection;
import com.techstreat.ssh_terminal.terminal.TerminalSession;
import com.techstreat.ssh_terminal.terminal.TerminalView;

public class SharedPreferencesManager
{
    private SharedPreferences sharedPref;
    static private SharedPreferencesManager instance;

    //Not sure if it is a good idea to cache preferences
    // private HashMap<String, Object> cachedPreferences;

    static private final String HOSTCHECKING = "pref_host_checking";
    static private final String FONTSIZE = "font_size";
    static private final String TERMINALEMULATION = "terminal_emulation";
    static public final String DELETETABLES = "delete_tables";
    static public final String ABOUT = "about";

    public static void init(Context context)
    {
        if(instance == null)
        {
            instance = new SharedPreferencesManager(context);
        }
    }

    public static SharedPreferencesManager getInstance()
    {
        return instance;
    }

    private SharedPreferencesManager(Context context)
    {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        //cachedPreferences = new HashMap<String, Object>();
    }

    //Preference getters
    public boolean hostChecking()
    {
        boolean enabled = sharedPref.getBoolean(HOSTCHECKING , false);
        return enabled;
    }

    public String fontSize()
    {
        String fontSize = sharedPref.getString(FONTSIZE , "10");
        return fontSize;
    }

    public String getTerminalEmulation()
    {
        String terminal = sharedPref.getString(TERMINALEMULATION, "xterm-256color");
        return terminal;
    }

    public void setPreferencesonShellConnection(SshConnection conn)
    {

        boolean hostChecking = hostChecking();
        if(hostChecking)
        {
            conn.disableHostChecking();
        }
    }

    public void setPreferencesTerminal(TerminalView terminal)
    {
        terminal.setTermType(getTerminalEmulation());
        int textSize = Integer.parseInt(fontSize());
        terminal.setTextSize(textSize);
        terminal.setAltSendsEsc(false);
        terminal.setMouseTracking(true);
       // terminal.setColorScheme(new ColorScheme(Constants.COLOR_SCHEMES[2]));
    }

    public void setPreferenceSession(TerminalSession terminalSession)
    {
        terminalSession.setDefaultUTF8Mode(true);
    }
}