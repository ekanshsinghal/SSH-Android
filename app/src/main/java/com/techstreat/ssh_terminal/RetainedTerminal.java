package com.techstreat.ssh_terminal;

import android.app.Fragment;
import android.os.Bundle;

import com.techstreat.ssh_terminal.terminal.TerminalSession;
import com.techstreat.ssh_terminal.terminal.TerminalView;

public class RetainedTerminal extends Fragment {

    // data object we want to retain
    private TerminalSession terminalSession;
    private TerminalView terminalview;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    //setters
    public void setTerminalSession(TerminalSession session)
    {
        terminalSession = session;
    }

    public void setTerminalView(TerminalView view)
    {
        terminalview = view;
    }

    //getters
    public TerminalSession getTerminalSession() {
        return terminalSession;
    }

    public TerminalView getTerminalView() {
        return terminalview;
    }
}