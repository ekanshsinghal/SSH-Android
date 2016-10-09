package com.techstreat.ssh_terminal.syncDialogs;

public interface BlockingOnUIRunnableListener
{
    /**
     * Code to execute on UI thread, runnable object must be synchronized on to notify waiting threads before this function returns
     * otherwise thread will wait indefinitely
     */
    public void onRunOnUIThread(Runnable runnable);
}