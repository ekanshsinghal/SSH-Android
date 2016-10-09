package com.techstreat.ssh_terminal.sshutils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import com.jcraft.jsch.Session;

public class SshConnection
{
    // jsch objects
    private JSch _jsch;
    protected Channel _channel;
    protected Session _session;
    private SessionUserInfo     _userInfo;
    protected CONNECTION_STATE  _state;

    protected enum CONNECTION_STATE
    {
        CONNECTED,
        CONNECTING,
        DISCONNECTED;
    }

    // SessionUserInfo not connected to UI at this point
    public SshConnection(SessionUserInfo user)
    {
        _jsch = new JSch();
        // _channel created by child class, can be terminal or file transfer channel
        _channel = null;
        _userInfo = user;
        _state = CONNECTION_STATE.DISCONNECTED;

        try
        {
            _session = _jsch.getSession(_userInfo.getUser(), _userInfo.getHost(), _userInfo.getPort());
            _session.setHostKeyRepository(new FingerPrintRepository(_jsch));
            _session.setServerAliveInterval(10000);
        }
        catch(JSchException e)
        {
            _session = null;
        }
        catch(Exception e)
        {
            _session = null;
        }
    }

    private boolean setupSession()
    {
        if(_session == null) {
            return false;
        }

        boolean ret = true;
        if(_userInfo.usingRSA())
        {
            try
            {
                // load(alias, private, public, passphrase)
                KeyPair keyPair = KeyPair.load(_jsch, _userInfo.getRsa(), null);
                if (!keyPair.isEncrypted())
                {
                    _jsch.addIdentity(_userInfo.getHost(), keyPair.forSSHAgent(), null, null);
                }
                else
                {
                    String passphrase = _userInfo.promptInput("RSA Encrypted", "Please enter passphrase for key");
                    keyPair.decrypt(passphrase);
                }
            }
            catch(JSchException e)
            {
                _userInfo.handleException(e);
                ret = false;
            }
        }
        _session.setUserInfo(_userInfo);
        return ret;
    }

    public boolean connect()
    {
        return setupSession();
    }

    public void disconnect()
    {
        if(_state != CONNECTION_STATE.DISCONNECTED)
        {
            if(_channel != null)
            {
                _channel.disconnect();
            }
            if(_session != null)
            {
                _session.disconnect();
            }
            _state = CONNECTION_STATE.DISCONNECTED;
        }
    }

    //setter
    public void disableHostChecking()
    {
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        _session.setConfig(config);
    }

    //getters
    public boolean isConnected()
    {
        return _state != CONNECTION_STATE.DISCONNECTED;
    }

    public String getName()
    {
        return _userInfo.getHost();
    }

    public Channel getChannel()
    {
        return _channel;
    }

    //protected getters
    protected SessionUserInfo getUserInfo()
    {
        return _userInfo;
    }

    protected Session getSession()
    {
        return _session;
    }
}