package com.techstreat.ssh_terminal.terminal;

import com.techstreat.ssh_terminal.sshutils.ShellConnection;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import jackpal.androidterm.emulatorview.TermSession;

public class TerminalSession extends TermSession
{
    private ShellConnection _conn;

    public TerminalSession(ShellConnection connection)
    {
        _conn = connection;
        PipedInputStream i = null;
        PipedOutputStream ou = null;
        try
        {
            i = new PipedInputStream(connection.getOutputStream());
            ou = new PipedOutputStream(connection.getInputStream());
            //setTerminalStuff Here
            setTermIn(i);
            setTermOut(ou);
        }
        catch(Exception e)
        {
           // Log.d(log, "Exception caught while creating jsch session" + e.getMessage());
        }
    }

    public ShellConnection getConnection()
    {
        return _conn;
    }

    @Override //called when data is processed from the input stream
    public void processInput(byte[] buffer, int offset, int count)
    {
        super.processInput(buffer, offset, count);
    }
}
