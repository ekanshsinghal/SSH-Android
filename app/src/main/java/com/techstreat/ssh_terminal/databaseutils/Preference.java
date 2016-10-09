package com.techstreat.ssh_terminal.databaseutils;

import android.os.Parcel;
import android.os.Parcelable;

public class Preference implements Parcelable
{
    private String connectionName;

    private String hostName;

    private Integer portNumber;

    private String username;

    private String password;

    public Preference()
    {
    }

    public Preference(String pass, String name, String host, String user, Integer port){
        connectionName=name;
        hostName=host;
        username = user;
        portNumber = port;
        password = pass;
    }

    //getters
    public String getName()
    {
        return connectionName;
    }

    public String getHostName()
    {
        return hostName;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public Integer getPort()
    {
        return portNumber;
    }

    //parceable methods
    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags)
    {
        out.writeString(connectionName);
        out.writeString(hostName);
        out.writeInt(portNumber);
        out.writeString(username);
        out.writeString(password);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Creator<Preference> CREATOR = new Creator<Preference>()
    {
        public Preference createFromParcel(Parcel in)
        {
            return new Preference(in);
        }

        public Preference[] newArray(int size)
        {
            return new Preference[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Preference(Parcel in)
    {
        connectionName = in.readString();
        hostName = in.readString();
        portNumber = in.readInt();
        username = in.readString();
        password = in.readString();
    }
}