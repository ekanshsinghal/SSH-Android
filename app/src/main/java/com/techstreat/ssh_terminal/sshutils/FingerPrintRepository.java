package com.techstreat.ssh_terminal.sshutils;

import android.util.Log;

import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.UserInfo;
import com.techstreat.ssh_terminal.MainActivity;
import com.techstreat.ssh_terminal.databaseutils.HostKeys;

public class FingerPrintRepository implements HostKeyRepository
{
    private JSch parameter;
    static private final String log = "FingerPrintRepository";

    FingerPrintRepository(JSch jsch)
    {
        parameter = jsch;

    }

    //if the host port is 22, host: domain, otherwise host: [domain]:6021
    public int check(String host, byte[] key)
    {
        return MainActivity.fingercheck(host, key);
    }

    public void add(HostKey hostkey, UserInfo ui)
    {
        HostKeys host = new HostKeys(hostkey.getHost(), hostkey.getFingerPrint(parameter), hostkey.getKey(), hostkey.getType());
        MainActivity.addHostkey(host);
    }

    public void remove(String host, String type) {
    }

    public void remove(String host, String type, byte[] key) {
    }

    public String getKnownHostsRepositoryID()
    {
        Log.d(log, "getKnownHostsRepositoryID");
        return null;
    }

    public HostKey[] getHostKey()
    {
        Log.d(log, "getHostKey");
        return null;
    }

    public HostKey[] getHostKey(String host, String type)
    {
        HostKey[] arrayOfHostKey = new HostKey[0];
        HostKeys hostKeys=MainActivity.getHostKey(host);
        try {
            arrayOfHostKey[arrayOfHostKey.length] = new HostKey(hostKeys.getHostName(),hostKeys.getKey().getBytes());
        } catch (JSchException e) {
            e.printStackTrace();
        }
        return arrayOfHostKey;
    }
}