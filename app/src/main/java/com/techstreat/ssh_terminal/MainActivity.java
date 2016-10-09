package com.techstreat.ssh_terminal;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.JSchException;
import com.techstreat.ssh_terminal.constants.Constants;
import com.techstreat.ssh_terminal.databaseutils.HostKeys;
import com.techstreat.ssh_terminal.databaseutils.Preference;
import com.techstreat.ssh_terminal.preferences.SharedPreferencesManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static String DATABASE_NAME = "rasp_ssh_db";
    static String tablepref="Preference";
    static String tablehost="HostKeys";
    static SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferencesManager.init(this);
        SharedPreferencesManager.getInstance();

        sqLiteDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+tablepref+"(connectionName VARCHAR,hostName VARCHAR,username VARCHAR,password VARCHAR,portNumber VARCHAR)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+tablehost+"(fingerprint VARCHAR,key VARCHAR,type VARCHAR,hostName VARCHAR)");
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        ListView listView = (ListView) findViewById(R.id.connections_list);
        setupConnectionsList(listView);
    }

    @Override
    public void onBackPressed()
    {
        ExitDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_saved_hosts)
        {
            Intent intent = new Intent(this, HostList.class);
            startActivity(intent);
        }
        else if(id == R.id.action_settings)
        {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.action_exit)
        {
            onBackPressed();
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void ExitDialog()
    {
        AlertDialog.Builder ab=new AlertDialog.Builder(MainActivity.this);
        ab.setMessage("Are you sure you want to exit?");
        ab.setTitle("Exit");
        ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
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
    private void setupConnectionsList(ListView lv)
    {
        final ArrayList<String> connname=new ArrayList();
        final Cursor cursor=sqLiteDatabase.rawQuery("select * from "+tablepref,null);
        while (cursor.moveToNext())
        {
            String cname=cursor.getString(0);
            connname.add(cname);
        }
        ArrayAdapter arrayAdapter=new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,connname);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                Preference preference=new Preference(cursor.getString(3),cursor.getString(0),cursor.getString(1),cursor.getString(2),Integer.parseInt(cursor.getString(4)));
                connectToPreference(preference);
            }
        });
    }

    private void connectToPreference(Preference p)
    {
        Intent intent = new Intent(this, TerminalActivity.class);
        intent.putExtra(Constants.PREFERENCE_PARCEABLE, (Parcelable) p);
        startActivity(intent);
    }





    /**
     * SQL Database Methods
     */
    public static void addHostkey(HostKeys h)
    {
        sqLiteDatabase.execSQL("INSERT INTO "+tablehost+"(fingerprint,key,type,hostName) VALUES ('"+h.getFingerprint()+"','"+h.getKey()+"','"+h.getType()+"','"+h.getHostName()+"')");
    }

    public static void addPreference(Preference p)
    {
        sqLiteDatabase.execSQL("INSERT INTO "+tablepref+"(connectionName,hostName,username,password,portNumber) VALUES ('"+p.getName()+"','"+p.getHostName()+"','"+p.getUsername()+"','"+p.getPassword()+"','"+p.getPort()+"')");
    }

    public static HostKeys getHostKey(String name)
    {
        HostKeys hostKeys=null;
        Cursor cursor=sqLiteDatabase.rawQuery("select * from "+tablehost+" where hostName = '"+name+"'",null);
        while (cursor.moveToNext())
        {
            hostKeys=new HostKeys(cursor.getString(3),cursor.getString(0),cursor.getString(1),cursor.getString(2));
        }
        return hostKeys;
    }

    public static int fingercheck(String name, byte[] key)
    {
        try {

            HostKey JcHost=new HostKey(name,key);
            HostKeys hostKeys;

            Cursor cursor=sqLiteDatabase.rawQuery("select * from "+tablehost+" where hostName = '"+name+"'",null);
            while (cursor.moveToNext())
            {
                hostKeys=new HostKeys(cursor.getString(3),cursor.getString(0),cursor.getString(1),cursor.getString(2));
                boolean res=JcHost.getKey().equals(hostKeys.getKey());
                if (res)
                    return 2;
                else return 0;
            }
        } catch (JSchException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static void deletePreference(Preference p)
    {
        String name=p.getName();
        String hname=p.getHostName();
        sqLiteDatabase.execSQL("DELETE FROM "+tablepref+" where connectionName = '"+name+"' AND hostName = '"+hname+"'");
    }

    public static void clearHostKeysTable()
    {
        sqLiteDatabase.execSQL("DELETE FROM"+tablehost);
    }

    public static void clearConnectionsTable()
    {
        sqLiteDatabase.execSQL("DELETE FROM"+tablepref);
    }
}
