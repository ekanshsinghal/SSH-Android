package com.techstreat.ssh_terminal.asyncNetworkTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.techstreat.ssh_terminal.sshutils.SshConnection;

public class SshConnectTask extends AsyncTask<SshConnection, Integer, Boolean>
{
    private IConnectionNotifier handler;
    private SshConnection conn;

    private final String log = "SshConnectTask";

    public SshConnectTask(IConnectionNotifier caller)
    {
        handler = caller;
        conn = null;
    }

    protected Boolean doInBackground(SshConnection... connection)
    {
        Boolean ret = false;
        try
        {
            conn = connection[0];
            if(!conn.isConnected())
            {
                ret = conn.connect();
            }
        }
        catch (Exception e)
        {
            Log.e(log, "doInBackground exception", e);
        }
        return ret;
    }

    protected void onPostExecute(Boolean result)
    {
        handler.connectionResult(result);
    }

}
